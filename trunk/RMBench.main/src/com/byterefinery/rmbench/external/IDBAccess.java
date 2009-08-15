/*
 * created 21.01.2006
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

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.model.dbimport.DBModel;

/**
 * interface to be used for accessing a physical database during model import and export
 * 
 * @author cse
 */
public interface IDBAccess {

    /**
     * a listener for connection state changes
     */
    public interface Listener {
        /**
         * @param state <code>true</code> if connection was established, <code>false</code> if it was 
         * released
         */
        void connected(boolean state);
    }
    
    /**
     * an executor is responsible for executing DDL statements during schema export
     */
    public interface Executor {
        boolean isConnected();
        void connect(String password) throws SystemException;
        void executeDDL(String ddl) throws SystemException;
        void close() throws SystemException;
        void addListener(Listener listener);
        void removeListener(Listener listener);
    }
    
    /**
     * @return the database info
     */
    IDatabaseInfo getDatabaseInfo();
    
    /**
     * load model data from the pyhical database
     * @param model the model object to be loaded
     * @return <code>true</code> if the loading went through without errors, <code>false</code>
     * if recoverable errors were logged
     * @throws SystemException if an unrecoverable error occurred
     */
    boolean loadModel(DBModel model) throws SystemException;
    
    /**
     * @return the executor for issuing SQL statements
     */
    Executor getExecutor();

    /**
     * @param password the password to be used during connecting. Implementors may ignore
     */
    void setPassword(String password);

    /**
     * @return true if this object is based on RMBench configuration data
     */
    boolean isConfigurable();
}
