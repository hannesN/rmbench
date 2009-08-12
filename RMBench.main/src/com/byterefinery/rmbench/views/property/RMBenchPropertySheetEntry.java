/*
 * created 19.11.2005
 *
 * Copyright 2005, DynaBEAN Consulting
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
