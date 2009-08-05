/*
 * created 13.02.2008
 *
 * Copyright 2008, ByteRefinery
 * 
 * $Id$
 */

package com.byterefinery.rmbench.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.external.IDBAccess;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.dbimport.DBIndex;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.model.dbimport.DBPrimaryKey;
import com.byterefinery.rmbench.model.dbimport.DBSchema;
import com.byterefinery.rmbench.model.dbimport.DBTable;

/**
 * establishes a connection to a database via JDBC and imports schema information
 * 
 * @author cse
 */
public abstract class AbstractJdbcAccess implements IDBAccess {
    
    public interface TABLE_TYPES {
        String VIEW = "VIEW";
        String TABLE = "TABLE";
        String SEQUENCE = "SEQUENCE";
        String ALIAS = "ALIAS";
        String SYNONYM = "SYNONYM";
        String PACKAGE = "PACKAGE";
        String PROCEDURE = "PROCEDURE";
        
        String[] TABLE_ONLY = {TABLE};
        String[] TABLE_VIEW_SEQUENCE = {TABLE, VIEW, SEQUENCE};
        String[] ALL = {TABLE, VIEW, SEQUENCE, ALIAS, SYNONYM};
    }
    
    protected abstract static class AbstractExecutor implements Executor {
        
        private List<Listener> listeners = new ArrayList<Listener>(2);
        private Connection connection;
        
        protected abstract IJdbcConnectAdapter getConnectAdapter() throws SystemException;
        
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        public void close() throws SystemException {
            if(!isConnected())
                return;
            try {
                connection.close();
                connection = null;
                for (Listener listener : listeners) {
                    listener.connected(false);
                }
            }
            catch (SQLException e) {
                throw new SystemException(e);
            }
        }

        public void connect(String password) throws SystemException {
            if(isConnected())
                throw new IllegalStateException();
            try {
                connection = getConnectAdapter().getConnection();
                for (Listener listener : listeners) {
                    listener.connected(true);
                }
            }
            catch (SQLException e) {
                throw new SystemException(e);
            }
        }

        public void executeDDL(String ddl) throws SystemException {
            java.sql.Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate(ddl);
            }
            catch (SQLException e) {
                throw new SystemException(e);
            }
            finally {
                if(statement != null) {
                    try {
                        statement.close();
                    } catch(SQLException x) {}
                }
            }
        }

        public boolean isConnected() {
            return connection != null;
        }

        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }
    }
    
    protected static class DefaultExecutor extends AbstractExecutor {

        private IJdbcConnectAdapter connectAdapter;
        
        public DefaultExecutor(IJdbcConnectAdapter connectAdapter) {
            this.connectAdapter = connectAdapter;
        }
        
        protected IJdbcConnectAdapter getConnectAdapter() {
            return connectAdapter;
        }
    }
    
    protected final boolean loadIndexes;
    protected final boolean loadKeyIndexes;
    protected final boolean loadComments;
    
    protected AbstractJdbcAccess(boolean loadIndexes, boolean loadKeyIndexes, boolean loadComments) {
        this.loadIndexes = loadIndexes;
        this.loadKeyIndexes = loadKeyIndexes;
        this.loadComments = loadComments;
    }
    
    /**
     * @return a usable connect adapter
     * @throws SystemException 
     */
    protected abstract IJdbcConnectAdapter getConnectAdapter() throws SystemException;

    /**
     * @return the metadata factory
     */
    protected abstract IMetaDataAccess.Factory getMetaDataFactory();

    public boolean loadModel(DBModel dbmodel) throws SystemException {

        IJdbcConnectAdapter jdbc = getConnectAdapter();

        try {
            return loadMetaData(dbmodel, jdbc);
        }
        catch (SQLException e) {
            throw new SystemException(Messages.SQLException, e);
        }
        finally {
            try {
                jdbc.release();
            }
            catch (SQLException e) {
                RMBenchPlugin.logError(e);
            }
        }
    }


    /**
     * load the meta data from the given jdbcConnection into the connection object
     * @param dbModel the target connection object
     * @param jdbc the jdbc connection to read from
     * @loadIndexes whether indexes should be loaded also
     * @return true for success, false if a recoverable error occurred
     * @throws SQLException for unrecoverable errors
     */
    protected boolean loadMetaData(DBModel dbModel, IJdbcConnectAdapter jdbc) 
        throws SQLException {
        
        jdbc.getConnection().setAutoCommit(true);
        
        IMetaDataAccess.Factory factory = getMetaDataFactory();
        IMetaDataAccess metaData = factory != null ? 
                factory.createMetaData(jdbc.getConnection(), loadComments) : 
                    JdbcMetaDataAdapter.FACTORY.createMetaData(jdbc.getConnection(), true);
        
        loadSchemas(dbModel, metaData);

        boolean success = true;
        for (DBSchema schema : dbModel.getSchemaList()) {
            loadTables(schema, metaData);

            for (DBTable table : schema.getTables()) {
                try {
                    loadColumns(table, metaData);
                    loadPrimaryKey(table, metaData);
                    loadForeignKeys(table, metaData);
                    if(loadIndexes)
                        loadIndexes(table, metaData);
                }
                catch(SQLException sqlx) {
                    success = false;
                    String msg = MessageFormat.format(
                            Messages.errorLoadingTable,
                            new Object[]{table.getSchema().getName(), table.getName()});
                    RMBenchPlugin.logError(msg, sqlx);
                }
            }
        }
        return success;
    }

    protected void loadSchemas(DBModel dbModel, IMetaDataAccess metaData)
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getSchemas();
        while(resultSet.next()) {
            String schemaName = resultSet.getString(1);
            String catalogName = resultSet.getString(2);
            
            if(dbModel.getSchemaRule().accepts(catalogName, schemaName)) {
                DBSchema schema = new DBSchema(
                        catalogName, 
                        schemaName, 
                        getDatabaseInfo());
                dbModel.addSchema(schema);
            }
        }
        resultSet.close();
    }

    protected void loadTables(DBSchema schema, IMetaDataAccess metaData) 
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getTables(
                schema.getCatalogName(), 
                schema.getName(),
                TABLE_TYPES.TABLE_VIEW_SEQUENCE);
        while(resultSet.next()) {
            String name = resultSet.getString(3);
            String tableType = resultSet.getString(4);
            if(getDatabaseInfo().isLoadableObject(name, tableType)) {
                //some databases return empty comment strings
                String comment = resultSet.getString(5);
                if(comment != null && comment.length() == 0)
                    comment = null;
                DBTable table = new DBTable(schema, name, comment);
                
                if(TABLE_TYPES.TABLE.equals(tableType))
                    schema.addTable(table);
                else if(TABLE_TYPES.VIEW.equals(tableType))
                    schema.addView(table);
                else if(TABLE_TYPES.SEQUENCE.equals(tableType))
                    schema.addSeqence(table);
            }
        }
        resultSet.close();
    }
    
    /*
     * load table columns meta data 
     */
    protected void loadColumns(DBTable table, IMetaDataAccess metaData) 
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getColumns(
                table.getCatalogName(),
                table.getSchemaName(),
                table.getName());
        while(resultSet.next()) {
            String name = resultSet.getString(4);
            int jdbcType = resultSet.getInt(5);
            String typeName = resultSet.getString(6);
            long size = resultSet.getLong(7);
            int scale = resultSet.getInt(9);
            //int precisionRadix = resultSet.getInt(10);
            boolean nullable = (resultSet.getInt(11) != IMetaDataAccess.columnNoNulls);
            
            //some databases return empty comment and defaultvalue strings
            String comment = loadComments ? resultSet.getString(12) : null;
            if(comment != null && comment.length() == 0)
                comment = null;
            String defaultValue = resultSet.getString(13);
            if(defaultValue != null && defaultValue.length() == 0)
                defaultValue = null;

            if(size < 0)
                size = IDataType.UNSPECIFIED_SIZE;
            
            IDataType dataType = getDatabaseInfo().getDataType(jdbcType, typeName, size, scale);
            if(dataType == null) {
                DatabaseExtension dbext = RMBenchPlugin.getExtensionManager().getDatabaseExtension(getDatabaseInfo());
                String msg = MessageFormat.format(
                        Messages.unknownDatatype, 
                        new Object[]{
                                dbext.getName(), 
                                typeName, 
                                new Integer(jdbcType), 
                                new Long(size), 
                                new Integer(scale)});
                RMBenchPlugin.logError(msg);
                dataType = getDatabaseInfo().getDefaultDataType();
            }
                        
            DBColumn column = new DBColumn(
                    table,
                    name,
                    dataType,
                    typeName,
                    JdbcTypes.getName(jdbcType),
                    size,
                    scale,
                    nullable,
                    defaultValue,
                    comment);
            table.addColumn(column);
            // loading extra data if exists
            if (dataType.hasExtra()){
                metaData.loadExtraData(column.getIColumn(), dataType);
            }
        }
        resultSet.close();
    }

    /*
     * load table primary key meta data
     */
    protected void loadPrimaryKey(DBTable table, IMetaDataAccess metaData) 
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getPrimaryKeys(
                table.getCatalogName(), 
                table.getSchemaName(), 
                table.getName());
        DBPrimaryKey pk = new DBPrimaryKey(table);
        int count = 0;
        while(resultSet.next()) {
            int seqNo = resultSet.getInt(5);
            if(seqNo == 1) {
                pk.name = resultSet.getString(6);
            }
            String columnName = resultSet.getString(4);
            pk.addColumn(seqNo, columnName);
            count++;
        }
        if(count > 0)
            table.setPrimaryKey(pk);
        
        resultSet.close();
    }
    
    protected void loadForeignKeys(DBTable table, IMetaDataAccess metaData) 
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getImportedKeys(
                table.getCatalogName(),
                table.getSchemaName(),
                table.getName());
        
        DBForeignKey key = null;
        while(resultSet.next()) {
            int seqNo = resultSet.getInt(9);
            if(seqNo == 1)  {
                String targetCatalog = resultSet.getString(1);
                String targetSchema = resultSet.getString(2);
                String targetTable = resultSet.getString(3);
                String fkName = resultSet.getString(12);
                int updateRule = resultSet.getInt(10);
                int deleteRule = resultSet.getInt(11);
                int deferrable = resultSet.getInt(14);
                
                key = new DBForeignKey(
                        table,
                        fkName, 
                        updateRule,
                        deleteRule,
                        deferrable,
                        targetCatalog,
                        targetSchema,
                        targetTable);
                table.addForeignKey(key);
            }
            String column = resultSet.getString(8);
            //String targetColumn = resultSet.getString(4);
            key.addColumn(column);
        }
        resultSet.close();
    }

    protected void loadIndexes(DBTable table, IMetaDataAccess metaData) 
        throws SQLException {
        
        IMetaDataAccess.ResultSet resultSet = metaData.getIndexInfo(
                table.getCatalogName(), table.getSchemaName(), table.getName());
        
        Map<String, DBIndex> indexes = new HashMap<String, DBIndex>();
        while(resultSet.next()) {
            int type = resultSet.getInt(7);
            if(type != IMetaDataAccess.tableIndexStatistic) {
                String name = resultSet.getString(6);
                DBIndex dbindex = (DBIndex)indexes.get(name);
                if(dbindex == null) {
                    boolean nonUnique = resultSet.getBoolean(4);
                    
                    dbindex = new DBIndex(table, name, !nonUnique);
                    indexes.put(name, dbindex);
                }
                String colName = resultSet.getString(9);
                String ascDesc = resultSet.getString(10);
                
                dbindex.addColumn(colName, !("D".equals(ascDesc)));
            }
        }
        resultSet.close();
        
        //throw out unwanted imports
        for (DBIndex index : indexes.values()) {
            boolean doImport = loadKeyIndexes  || table.getPrimaryKey() == null || !table.getPrimaryKey().name.equals(index.name); 
            if(!loadKeyIndexes && doImport) {
                for (DBForeignKey foreignKey : table.getForeignKeys()) {
                    if(foreignKey.name.equals(index.name)) {
                        doImport = false;
                        break;
                    }
                }
            }
            if(doImport)
                table.addIndex(index);
        }
    }
}
