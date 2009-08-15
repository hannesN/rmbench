/*
 * created 26.05.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: RMBenchPropertySource.java 117 2006-01-23 23:35:28Z csell $
 */
package com.byterefinery.rmbench.views.property;

import org.eclipse.ui.views.properties.IPropertySource;

/**
 * superclass for all property sources. 
 * 
 * @author Hannes Niederhausen
 *
 */
public abstract class RMBenchPropertySource implements IPropertySource {
    public final static String  P_NAME="name_property";
    
    RMBenchPropertySheetEntry propertySheetEntry;
    
    public Object getEditableValue() {
        return this;
    }
      
    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {
    }    
      
    protected void refresh() {
        propertySheetEntry.refreshFromRoot();
    }

    /**
     * @param entry the entry, which is called to refresh its contents
     */
    void setPropertySheetEntry(RMBenchPropertySheetEntry entry) {
        propertySheetEntry = entry;
    }
}
