/*
 * created 11.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcResultSetAdapter;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IDataType;


/**
 * DatabaseMetaData adapter for the  atypical behaviour of the mysql jdbc driver.
 * Substitutes catalogs for schemas
 *  
 * @author Hannes Niederhausen
 *
 */
public class MySQLJdbcMetaData extends JdbcMetaDataAdapter {

	public static class Factory implements IMetaDataAccess.Factory {

		public IMetaDataAccess createMetaData(Connection connection, boolean option) throws SQLException {
			return new MySQLJdbcMetaData(connection.getMetaData());
		}
	}
	
	private MySQLJdbcMetaData(DatabaseMetaData metaData) {
		super(metaData);
	}

    public ResultSet getSchemas() throws SQLException {
        return new JdbcResultSetAdapter(metaData.getCatalogs()) {

            // we have to switch catalogue and schema values
            public String getString(int index) throws SQLException {
                if(index == 2)
                    return null; //getCatalogs() has only 1 column
                else
                    return super.getString(index);
            }
        };
    }
	public ResultSet getTables(String catalogName, String schemaName, String[] types) throws SQLException {
		return super.getTables(schemaName, null, types);
	}
	public ResultSet getColumns(String catalogName, String schemaName, String tableName) throws SQLException {
		return super.getColumns(schemaName, null, tableName);
	}
	public ResultSet getPrimaryKeys(String catalogName, String schemaName, String tableName) throws SQLException {
		return super.getPrimaryKeys(schemaName, null, tableName);
	}
	public ResultSet getIndexInfo(String catalogName, String schemaName, String tableName) throws SQLException {
		return super.getIndexInfo(schemaName, null, tableName);
	}
	public ResultSet getImportedKeys(String catalogName, String schemaName, String tableName) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getImportedKeys(schemaName, null, tableName)) {

			public String getString(int index) throws SQLException {
	            // we have to switch catalogue and schema values
				switch(index) {
				case 1: 
					return super.getString(2);
				case 2: 
					return super.getString(1);
				default: 
					return super.getString(index);
				}
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter#loadExtraData(com.byterefinery.rmbench.external.model.IDataType)
	 */
	public void loadExtraData(IColumn column, IDataType dataType) {
		if (dataType instanceof MySQLListDatatype){
			Statement stm;
			try {
				//retreaving the column definition, which second column contains the exact type definition
				stm = metaData.getConnection().createStatement();
				java.sql.ResultSet rs = stm.executeQuery("SHOW COLUMNS FROM "+column.getTable().getSchema().getName()+"."
							+column.getTable().getName()+" LIKE '"+column.getName()+"'");
				//parse the type definition for the types of an enum/set 
				if (rs.isBeforeFirst()) {
					rs.next();
					String typedeclaration = rs.getString(2);
					String types[] = typedeclaration.substring(
							typedeclaration.indexOf('(')+1, 
							typedeclaration.lastIndexOf(')')).split(",");
					for (int i=0; i<types.length; i++) {
						types[i]=types[i].substring(types[i].indexOf('\'')+1, types[i].lastIndexOf('\''));
					}
					((MySQLListDatatype)dataType).setElements(types);
				}
				// some cleaning code
				rs.close();
				stm.close();
			} catch (SQLException e) {
				RMBenchPlugin.logError(e);
			}
			
			
		}
		
	}
}
