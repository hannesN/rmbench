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
