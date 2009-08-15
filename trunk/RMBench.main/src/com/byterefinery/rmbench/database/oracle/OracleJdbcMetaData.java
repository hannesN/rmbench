/*
 * created 31.08.2007
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
