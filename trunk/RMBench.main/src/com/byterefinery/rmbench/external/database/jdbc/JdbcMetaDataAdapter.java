/*
 * created 11.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.database.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IDataType;


/**
 * this IMetaDataAccess implementation simply wraps the standard JDBC MetaData object. It can be 
 * used as a superclass by implementors who only wish to modify specific aspects of JDBC driver behavior 
 * 
 * @author cse
 */
public class JdbcMetaDataAdapter implements IMetaDataAccess {

	public static final IMetaDataAccess.Factory FACTORY = new IMetaDataAccess.Factory() {
		public IMetaDataAccess createMetaData(Connection connection, boolean option) throws SQLException {
			return new JdbcMetaDataAdapter(connection.getMetaData());
		}
	};
	
	protected final DatabaseMetaData metaData;
	
	protected JdbcMetaDataAdapter(DatabaseMetaData metaData) {
		this.metaData = metaData;
	}
	public ResultSet getSchemas() throws SQLException {
        return new JdbcResultSetAdapter(metaData.getSchemas()) {
            public String getString(int index) throws SQLException {
                if(index == 2) {
                	try {
                		return super.getString(2);
                	}
                	catch(Exception x) {
                        return null; //non-3.0 driver will throw exception, we return null instead
                	}
                }
                else 
                    return super.getString(index);
            }
        };
	}
	public ResultSet getTables(String catalogName, String schemaName, String[] types) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getTables(catalogName, schemaName, null, types));
	}
	public ResultSet getColumns(String catalogName, String schemaName, String tableName) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getColumns(catalogName, schemaName, tableName, null));
	}
	public ResultSet getPrimaryKeys(String catalogName, String schemaName, String tableName) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getPrimaryKeys(catalogName, schemaName, tableName));
	}
	public ResultSet getImportedKeys(String catalogName, String schemaName, String tableName) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getImportedKeys(catalogName, schemaName, tableName));
	}
	public ResultSet getIndexInfo(String catalogName, String schemaName, String tableName) throws SQLException {
		return new JdbcResultSetAdapter(metaData.getIndexInfo(catalogName, schemaName, tableName, false, false));
	}
	
	/**
	 * The default implementation of the method does nothing
	 * @see {@link IMetaDataAccess}
	 *
	 */
	public void loadExtraData(IColumn column, IDataType dataType) {
	}
	
}
