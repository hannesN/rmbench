/*
 * created 20.08.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  $Id$
 */
package com.byterefinery.rmbench.external;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;

/**
 * A URLSetupGroup provides a widget implementation used by the wizard for creating a database
 * connection. The purpose of this widget ist to provide a user interface for editing the needed data
 * to create a url for a jdbc connection.
 * @author Hannes Niederhausen
 */
public interface IURLSetupGroup {

    /**
     * an interface that allows access to the context in which the connection URL us being configured
     */
    public interface Context {
        
        /**
         * @return the name of the currently configured driver
         */
        public String getDriverName();
        
        /**
         * atempts to establish JDBC connection using the already configured driver
         * 
         * @param properties the connection properties
         * @param url the ConnectionAdapter implementation for connection url
         * @throws SystemException if the dirver could not be loaded, an error occurred during connect. 
         * In the latter case, the SQLException will be wrapped
         */
        public IJdbcConnectAdapter getConnectionAdapter(Properties properties, String url) throws SystemException;
    }
    
    /**
     * a factory for creating IURLSetupGroup instances
     */
    public interface Factory {

        /**
         * @param connectionUrl the current value of the connection URL to be configured
         * @param context the configuration context
         * @return a new setup widget group
         */
        public IURLSetupGroup createSetupGroup(String connectionUrl, Context context);
    }

    /**
     * A listener of events on this object
     */
    public interface Listener {
        /**
         * the completed status has changed
         * @param completed
         *            the new status
         */
        void inputCompleted(boolean completed);

        /**
         * an input error has occurred
         * @param the
         *            error message
         */
        void errorOccured(String errorMessage);
    }

    /**
     * Dispose all widgets created by the setup group
     */
    public void disposeWidgets();

    /**
     * creates the widgets of the setup group.
     * @param parent
     *            the parent composite
     */
    public void createWidgets(Composite parent);

    /**
     * @param listener
     *            a listener to be notified about object events
     */
    public void addListener(Listener listener);

    /**
     * @param listener
     *            the listener to remove
     */
    public void removeListener(Listener listener);

    /**
     * return whether the input is is complete and therefore the URL returned by
     * {@link #getConnectionURL()} can be safely used.
     * @return the complete status
     */
    public boolean isComplete();

    /**
     * @return the completed connection url
     */
    public abstract String getConnectionURL();

    /**
     * @return the outermost control maintained by this object
     */
    public Control getControl();
    
}
