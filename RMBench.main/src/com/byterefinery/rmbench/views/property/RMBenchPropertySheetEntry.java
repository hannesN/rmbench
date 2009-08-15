/*
 * created 19.11.2005
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
package com.byterefinery.rmbench.views.property;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetEntry;

/**
 * subclassed to allow RMBenchPropertySource objects to refresh their representation
 * when changes occur "offline" (e.g., through undo).
 * <p>
 * <em>This is achieved through a slight hack, wich is necessitated by the fact that 
 * PropertySources dont seem to have a standard way to propagate offline changes</em>
 */
public final class RMBenchPropertySheetEntry extends PropertySheetEntry {

    protected PropertySheetEntry createChildEntry() {
        return new RMBenchPropertySheetEntry();
    }

    protected IPropertySource getPropertySource(Object object) {
        IPropertySource propertySource = super.getPropertySource(object);
        if(propertySource instanceof RMBenchPropertySource) {
            ((RMBenchPropertySource)propertySource).setPropertySheetEntry(this);
        }
        return propertySource;
    }

    protected void refreshFromRoot() {
        super.refreshFromRoot();
    }
}
