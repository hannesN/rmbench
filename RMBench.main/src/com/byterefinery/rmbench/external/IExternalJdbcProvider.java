/*
 * created 12.02.2008
 *
 * Copyright 2008, ByteRefinery
 * 
 * $Id$
 */

package com.byterefinery.rmbench.external;

import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;

/**
 * describes the interface to another plugins connection management facilities
 * 
 * @author cse
 */
public interface IExternalJdbcProvider {

    interface Connector {
        String getName();
        ExtensionId getDatabaseId();
        ExtensionId getDriverId();
        IJdbcConnectAdapter getConnectAdapter();
    }
    
    interface Listener {
        void added(Connector connector);
        void removed(String name);
    }
    
    /**
     * @param listener a listener for connector events
     */
    void activate(Listener listener);
    
    /**
     * deactivate this object, deregistering the listener listeners
     * @param listener 
     */
    void deactivate(Listener listener);
    
    /**
     * @return an array holding all currently registered connectors
     */
    Connector[] getConnectors();
}
