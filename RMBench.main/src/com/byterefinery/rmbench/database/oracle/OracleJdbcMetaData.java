/*
 * created 31.08.2007
 *
 * Copyright 2007, ByteRefinery
 * 
 * $Id$
 */

package com.byterefinery.rmbench.database.oracle;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;

/**
 * an adapter for oracle jdbc metadata access, which sets the 'remarks reporting' option
 * to true such that comments get imported 
 * 
 * @author cse
 */
public class OracleJdbcMetaData extends JdbcMetaDataAdapter {

    public static class Factory implements IMetaDataAccess.Factory {

        public IMetaDataAccess createMetaData(Connection connection, boolean loadComments) throws SQLException {
        	
        	if(loadComments) {
	    		try {
	    			Method setRemarksReporting = connection.getClass().getMethod("setRemarksReporting", boolean.class);
	    			setRemarksReporting.setAccessible(true);
	    			setRemarksReporting.invoke(connection, Boolean.TRUE);
	    		} 
	    		catch (Exception e) {
	    		} 
        	}
            return new OracleJdbcMetaData(connection.getMetaData());
        }
    }
    
	public OracleJdbcMetaData(DatabaseMetaData metaData) {
		super(metaData);
	}
}
