/*
 * created 21.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ImportTransfer.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dnd;


/**
 * a DND transfer that represents a schema object from the import view as it
 * is tranferred over to a model view (e.g., a diagram).<p>
 * 
 * Also serves as a creation factory in the drop target context
 * 
 * @author cse
 */
public class ImportTransfer extends RMBenchTransfer {

    private static final ImportTransfer instance = new ImportTransfer();
    
    public static final String TYPE_NAME = 
    	"schema-transfer-format" + System.currentTimeMillis() + ":" + //$NON-NLS-2$//$NON-NLS-1$ 
    	instance.hashCode();
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static ImportTransfer getInstance() {
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
