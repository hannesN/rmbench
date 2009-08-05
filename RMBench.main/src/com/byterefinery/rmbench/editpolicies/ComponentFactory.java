/*
 * created 16.03.2005
 * 
 * $Id: ComponentFactory.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.gef.requests.CreationFactory;


/**
 * we are not really using the CreationFactory concept (whatever it may be).
 * This class only serves as a constant namespace
 *  
 * @author sell
 */
public class ComponentFactory implements CreationFactory {

    public static final ComponentFactory TABLE = new ComponentFactory();
    public static final ComponentFactory INDEX = new ComponentFactory();
    public static final ComponentFactory CONSTRAINT = new ComponentFactory();
    
    private ComponentFactory() {
    }
    
    //@see org.eclipse.gef.requests.CreationFactory#getNewObject()
    public Object getNewObject() {
        return this;
    }

    //@see org.eclipse.gef.requests.CreationFactory#getObjectType()
    public Object getObjectType() {
        return this;
    }
}
