/*
 * created 12.02.2008
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
package com.byterefinery.rmbench.dtp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;

import com.byterefinery.rmbench.external.ExtensionId;
import com.byterefinery.rmbench.external.IExternalJdbcProvider;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;

/**
 * a provider that will adapt DTP datasources for RMBench
 * 
 * @author cse
 */
public class ExternalJdbcProvider implements IExternalJdbcProvider {

    private static final String POSTGRESQL_CONNECTION_PROFILE = 
        "org.eclipse.datatools.enablement.postgresql.connectionProfile";
    private static final String ORACLE_CONNECTION_PROFILE = 
        "oracle.dbtools.dtp.connectivity.db.connectionProfile";

    private static final Map<String, ExtensionId> registeredDriverIds = 
        new HashMap<String, ExtensionId>();
    private static final Map<String, ExtensionId> registeredDBIds = 
        new HashMap<String, ExtensionId>();
    
    static {
        registeredDriverIds.put(
                ORACLE_CONNECTION_PROFILE, 
                new ExtensionId("oracle"));
        registeredDBIds.put(
                ORACLE_CONNECTION_PROFILE, 
                new ExtensionId("oracle"));
        registeredDriverIds.put(
                POSTGRESQL_CONNECTION_PROFILE, 
                new ExtensionId("postgresql"));
        registeredDBIds.put(
                POSTGRESQL_CONNECTION_PROFILE, 
                new ExtensionId("postgresql"));
    }
    
    private static final class DTPConnector implements IExternalJdbcProvider.Connector {
        
        private final IJdbcConnectAdapter connectAdapter;
        private final ExtensionId databaseID;
        private final ExtensionId driverID;
        private final IConnectionProfile profile;
        
        DTPConnector(
                IConnectionProfile profile, 
                ExtensionId databaseId,
                ExtensionId driverId) {
            this.connectAdapter = new DTPConnectAdapter(profile);
            this.databaseID = databaseId;
            this.driverID = driverId;
            this.profile = profile;
        }
        
        public IJdbcConnectAdapter getConnectAdapter() {
            return connectAdapter;
        }
    
        public ExtensionId getDatabaseId() {
            return databaseID;
        }
    
        public ExtensionId getDriverId() {
            return driverID;
        }
    
        public String getName() {
            return profile.getName();
        }
    }

    private static final class DTPConnectAdapter implements IJdbcConnectAdapter {

        private final IConnectionProfile profile;
        private IConnection iconn;
        
        DTPConnectAdapter(IConnectionProfile profile) {
            this.profile = profile;
        }
        
        public Connection getConnection() throws SQLException {
            if(iconn == null) {
                iconn = profile.createConnection("java.sql.Connection");
                if(iconn.getConnectException() != null) {
                    String msg = iconn.getConnectException().getMessage();
                    iconn = null;
                    throw new SQLException(msg);
                }
            }
            return (Connection)iconn.getRawConnection();
        }

        public void release() throws SQLException {
            if(iconn != null) {
                iconn.close();
                iconn = null;
            }
        }
    }
    
    private static final class ListenerAdapter implements IProfileListener {

        private final Listener rmBenchListener;
        
        ListenerAdapter(Listener rmBenchListener) {
            this.rmBenchListener = rmBenchListener;
        }
        
        public void profileAdded(IConnectionProfile profile) {
            rmBenchListener.added(createConnector(profile));
        }

        public void profileChanged(IConnectionProfile profile) {
        }

        public void profileDeleted(IConnectionProfile profile) {
            rmBenchListener.removed(profile.getName());
        }
    }
    
    private ListenerAdapter listenerAdapter;
    
    public Connector[] getConnectors() {
        
        IConnectionProfile[] profiles = ProfileManager.getInstance()
            .getProfilesByCategory("org.eclipse.datatools.connectivity.db.category");
        
        DTPConnector[] connectors = new DTPConnector[profiles.length];
        for (int i = 0; i < connectors.length; i++) {
            connectors[i] = createConnector(profiles[i]);
        }
        return connectors;
    }

    private static DTPConnector createConnector(IConnectionProfile profile) {
        ExtensionId dbID = findDatabaseId(profile.getProviderId());
        ExtensionId driverID = findDriverId(profile.getInstanceID());
        
        return new DTPConnector(profile, dbID, driverID);
    }

    private static ExtensionId findDriverId(String profileID) {
        return registeredDriverIds.get(profileID);
    }

    private static ExtensionId findDatabaseId(String profileID) {
        return registeredDBIds.get(profileID);
    }

    public void activate(Listener listener) {
        listenerAdapter = new ListenerAdapter(listener);
        ProfileManager.getInstance().addProfileListener(listenerAdapter);
    }

    public void deactivate(Listener listener) {
        if(listenerAdapter != null) {
            ProfileManager.getInstance().removeProfileListener(listenerAdapter);
            listenerAdapter = null;
        }
    }
}
