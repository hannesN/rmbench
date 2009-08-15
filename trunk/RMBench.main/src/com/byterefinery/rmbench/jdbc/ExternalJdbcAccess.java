/*
 * created 13.02.2008
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

package com.byterefinery.rmbench.jdbc;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.JdbcDriverExtension;
import com.byterefinery.rmbench.external.ExtensionId;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IExternalJdbcProvider;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;

/**
 * establishes a connection to a database via an external JDBC connection configuration
 * and imports schema information
 * 
 * @author cse
 */
public class ExternalJdbcAccess extends AbstractJdbcAccess {

    private final IExternalJdbcProvider.Connector connector;
    private final IDatabaseInfo databaseInfo;
    private final IMetaDataAccess.Factory metaData;
    
    /**
     * @param connector
     */
    public ExternalJdbcAccess(IExternalJdbcProvider.Connector connector) {
        super(true, false, true);
        this.connector = connector;
        
        ExtensionId dbId = connector.getDatabaseId();
        if(dbId != null) {
            DatabaseExtension dbExt = RMBenchPlugin.getExtensionManager()
                .getDatabaseExtension(dbId.namespace, dbId.id);
            this.databaseInfo = dbExt.getDatabaseInfo();
        }
        else
            this.databaseInfo = RMBenchPlugin.getStandardDatabaseExtension().getDatabaseInfo();
            

        ExtensionId driverId = connector.getDriverId();
        if(driverId != null) {
            JdbcDriverExtension driverExt = RMBenchPlugin.getExtensionManager()
                .getJdbcDriverExtension(driverId.namespace, driverId.id);
            this.metaData = driverExt.getMetaDataFactory();
        }
        else
            this.metaData = JdbcMetaDataAdapter.FACTORY;
    }

    protected IJdbcConnectAdapter getConnectAdapter() throws SystemException {
        return connector.getConnectAdapter();
    }

    protected IMetaDataAccess.Factory getMetaDataFactory() {
        return metaData;
    }

    public IDatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public Executor getExecutor() {
        return new DefaultExecutor(connector.getConnectAdapter());
    }

    /**
     * ignored
     */
    public void setPassword(String password) {
    }

    public boolean isConfigurable() {
        return false;
    }
}
