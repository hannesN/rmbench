/*
 * created 01.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IModelStorage.java 163 2006-02-09 14:38:48Z cse $
 */
package com.byterefinery.rmbench.util;

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.model.Model;

/**
 * storage API for {@link com.byterefinery.rmbench.model.Model} instances
 * @author cse
 */
public interface IModelStorage {

    /**
     * interface for parties interested in model changes that occur during model loading. 
     * Model elements may be created or dropped to make the loaded model consistent.
     */
    public interface LoadListener {
        /**
         * an element was added
         * @param element any schema element
         */
        void elementAdded(Object element);
        /**
         * an element was removed
         * @param element any schema element
         */
        void elementDropped(Object element);
    }

    /**
     * default load listener that sets its <code>dirty</code> flag if a model change occurs during 
     * loading 
     */
    public class DefaultLoadListener implements LoadListener {

        public boolean dirty;
        
        public void elementAdded(Object element) {
            dirty = true;
        }

        public void elementDropped(Object element) {
            dirty = true;
        }
    }
    
    /**
     * @param progressMonitor
     */
    void store() throws SystemException;

    /**
     * @return
     */
    Model getModel();

    /**
     * 
     */
    void load(LoadListener listener) throws SystemException;

    /**
     * @return
     */
    boolean isNew();
}
