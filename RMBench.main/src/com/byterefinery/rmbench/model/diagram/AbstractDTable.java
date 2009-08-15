/*
 * created 23.01.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $$Id: AbstractDTable.java 114 2006-01-23 21:03:26Z hannesn $$
 */
package com.byterefinery.rmbench.model.diagram;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.draw2d.geometry.Point;

/**
 * This is the super class of the DTable and DTableStub, containing the diagram and position 
 * in the diagram.
 * 
 * @author Hannes Niederhausen
 *
 */
public abstract class AbstractDTable {
    
    public static final String PROPERTY_LOCATION = "location";
    
    /** used with manual layout only */
    protected Point location;
    
    protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    public AbstractDTable(Point location) {
        super();
        this.location = location;
    }

    public void setLocation(Point location) {
        Point oldLocation = this.location;
        this.location = location;
        propertySupport.firePropertyChange(PROPERTY_LOCATION, oldLocation, location);
    }
    
    public Point getLocation() {
        return location;
    }
    
    public void addPropertyListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
}
