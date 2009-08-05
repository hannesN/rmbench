/*
 * created 24.08.2006
 *
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.pgsql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcResultSetAdapter;

/**
 * JDBC MetaData adapter for PostgreSQL.
 * 
 * @author cse
 */
public class PgSQLJdbcMetaData extends JdbcMetaDataAdapter {

    public static class Factory implements IMetaDataAccess.Factory {

        public IMetaDataAccess createMetaData(Connection connection, boolean option) throws SQLException {
            return new PgSQLJdbcMetaData(connection.getMetaData());
        }
    }
    
    protected PgSQLJdbcMetaData(DatabaseMetaData metaData) {
        super(metaData);
    }

    public ResultSet getSchemas() throws SQLException {
        return new JdbcResultSetAdapter(metaData.getSchemas()) {

            public String getString(int index) throws SQLException {
                switch(index) {
                case 2: 
                    return null; //driver will throw exception, we return null instead
                default: 
                    return super.getString(index);
                }
            }
        };
    }
    
    public ResultSet getIndexInfo(String catalogName, String schemaName, String tableName) throws SQLException {
    	return new JdbcResultSetAdapter(metaData.getIndexInfo(catalogName, schemaName, tableName, false, false)) {
            public String getString(int index) throws SQLException {
                if (index==9) {
                	String colname = super.getString(index);
                	// if the column name is quoted we remove the quotes for columnidentification
                	if (colname.startsWith("\"")) {
                		return colname.substring(1, colname.length()-1);
                	}
                }
                return super.getString(index);
            }
        };
    }
}
