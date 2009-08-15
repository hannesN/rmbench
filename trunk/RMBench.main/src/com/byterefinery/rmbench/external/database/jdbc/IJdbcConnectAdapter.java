/*
 * created 28.08.2006
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
package com.byterefinery.rmbench.external.database.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * adapter interface for JDBC connection creation and release
 *  
 * @author cse
 */
public interface IJdbcConnectAdapter {

    public interface Factory {
        /**
         * @param driver the JDBC driver
         * @param url the connection URL
         * @param properties the already configured connect properties, which may be amended 
         * by the implementing class
         * @return a connect adapter that wraps a valid JDBC connection
         */
        IJdbcConnectAdapter create(Driver driver, String url, Properties properties);
    }
    /**
     * @return the JDBC connection wrapped by this object. Note that the results of closing that
     * connection are undefined, but most likely undesirable
     * @see #release()
     */
    Connection getConnection() throws SQLException;
    
    /**
     * release this object, by closing the underlying connection and performing any additional
     * cleanup
     * 
     * @throws SQLException
     */
    void release() throws SQLException;
}
