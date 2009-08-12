/*
 * created 21.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelTransfer.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dnd;


/**
 * a DND transfer that represents a schema object from the model view as it
 * is tranferred over to a diagram.
 * 
 * @author cse
 */
public class ModelTransfer extends RMBenchTransfer {

    private static final ModelTransfer instance = new ModelTransfer();
    
    public static final String TYPE_NAME = 
    	"model-transfer-format" + System.currentTimeMillis() + ":" + //$NON-NLS-2$//$NON-NLS-1$ 
    	instance.hashCode();
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static ModelTransfer getInstance() {
        return instance;
    }
    
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

	public Object getObjectType() {
		return TYPE_NAME;
	}
}
