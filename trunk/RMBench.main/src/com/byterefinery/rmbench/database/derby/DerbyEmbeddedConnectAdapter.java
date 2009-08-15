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
package com.byterefinery.rmbench.database.derby;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcConnectAdapter;

/**
 * a connect adapter for derby, which will shutdown the database upon release
 * 
 * @author cse
 */
public class DerbyEmbeddedConnectAdapter extends JdbcConnectAdapter {

    public static class Factory implements IJdbcConnectAdapter.Factory {
        public IJdbcConnectAdapter create(Driver driver, String url, Properties properties) {
            return new DerbyEmbeddedConnectAdapter(driver, url, properties);
        }
    }
    
    protected DerbyEmbeddedConnectAdapter(Driver driver, String url, Properties properties) {
        super(driver, url, properties);
    }

    public void release() throws SQLException {
        super.release();
        try {
            Properties properties = new Properties();
            properties.put("shutdown", "true");
            connection = driver.connect(url, properties);
        } catch (SQLException e) {
            //no one will be interested..
        }
    }
}
