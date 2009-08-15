/*
 * created 11.08.2006
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
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.SizeScaleDataType;

/**
 * This is the datatype for double and float of the mysql database,
 * which get a scale and size, but if they are used, both must be used.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLDoubleDataType extends SizeScaleDataType {

    
    
    public MySQLDoubleDataType(String[] names, long maxSize, boolean sizeRequired, long initSize, long defaultSize, int maxScale, boolean scaleRequired, int initScale, int defaultScale) {
        super(names, maxSize, sizeRequired, initSize, defaultSize, maxScale, scaleRequired, initScale,
                defaultScale);
    }

    public MySQLDoubleDataType(String[] names, long maxSize, boolean sizeRequired, long initSize, int maxScale, boolean scaleRequired, int initScale) {
        super(names, maxSize, sizeRequired, initSize, maxScale, scaleRequired, initScale);
    }
    
    public String getDDLName() {
        if ( (size != IDataType.UNSPECIFIED_SIZE) && (scale != IDataType.UNSPECIFIED_SCALE)) {
            StringBuffer buf = new StringBuffer(getPrimaryName());
            buf.append(" (");
            buf.append(size);
            
            buf.append(", ");
            buf.append(scale);
            
            buf.append(")");
            return buf.toString();
        }
        return getPrimaryName();
    }
    
    public IDataType concreteInstance() {
        return new MySQLDoubleDataType(
                names, maxSize, requiresSize, size, defaultSize, 
                maxScale, requiresScale, scale, defaultScale);
    }
}
