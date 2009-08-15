/*
 * created 24.08.2006
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcResultSetAdapter;

/**
 * JDBC MetaData adapter for Derby.
 * 
 * @author cse
 */
public class DerbyJdbcMetaData extends JdbcMetaDataAdapter {

    public static class Factory implements IMetaDataAccess.Factory {

        public IMetaDataAccess createMetaData(Connection connection, boolean option) throws SQLException {
            return new DerbyJdbcMetaData(connection.getMetaData());
        }
    }
    
    protected DerbyJdbcMetaData(DatabaseMetaData metaData) {
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
}
