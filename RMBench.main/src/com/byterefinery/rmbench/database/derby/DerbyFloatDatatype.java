/*
 * created 25.08.2006
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
package com.byterefinery.rmbench.database.derby;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.SizeDataType;

/**
 * Derby FLOAT datatype. Prevents output of default precision value, because for some 
 * reason Derby rejects FLOAT(53)
 * 
 * @author Hannes Niederhausen
 */
public class DerbyFloatDatatype extends SizeDataType {

    public DerbyFloatDatatype(String name, long maxSize, boolean sizeRequired, long initSize,
            long defaultSize) {
        this(new String[]{name}, maxSize, sizeRequired, initSize, defaultSize);
    }
    
    public DerbyFloatDatatype(String[] names, long maxSize, boolean sizeRequired, long initSize,
            long defaultSize) {
        super(names, maxSize, sizeRequired, initSize, defaultSize);
    }
    
    public String getDDLName() {
        if (size==maxSize)
            return getPrimaryName();
        
        StringBuffer buf = new StringBuffer(getPrimaryName());
        buf.append(" (");
        buf.append(size);
        buf.append(")");
        return buf.toString();
    }

    public IDataType concreteInstance() {
        return new DerbyFloatDatatype(names, maxSize, requiresSize, size, defaultSize);
    }
}
