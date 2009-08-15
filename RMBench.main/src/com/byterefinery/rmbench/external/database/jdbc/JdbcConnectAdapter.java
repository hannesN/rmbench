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
 * an implementation of IJdbcConnectAdapter that simply wraps the JDBC connection without
 * additional handling
 * 
 * @author cse
 */
public class JdbcConnectAdapter implements IJdbcConnectAdapter {

    public static final Factory FACTORY = new Factory() {

        public IJdbcConnectAdapter create(Driver driver, String url, Properties properties) {
            return new JdbcConnectAdapter(driver, url, properties);
        }
    };

    protected final Driver driver;
    protected final String url; 
    protected final Properties properties;
    protected Connection connection;
    
    protected JdbcConnectAdapter(Driver driver, String url, Properties properties) {
        this.driver = driver;
        this.url = url;
        this.properties = properties;
    }
    
    public Connection getConnection() throws SQLException {
        if(connection == null)
            connection = driver.connect(url, properties);
        return connection;
    }

    public void release() throws SQLException {
        connection.close();
        connection = null;
    }
}
