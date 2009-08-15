/*
 * created 25.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: SizeScaleDataType.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external.model.type;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * a data type that accepts size and scale values
 * 
 * @author cse
 */
public class SizeScaleDataType extends SizeDataType {

    public static final String SCALE_TOO_BIG = "maxexceeded.scale";

    protected int scale;
    
    protected final int maxScale;
    protected final int defaultScale;
    protected final boolean requiresScale;
    
    public SizeScaleDataType(
            String[] names, 
            long maxSize, 
            boolean sizeRequired, 
            long initSize,
            int maxScale, 
            boolean scaleRequired,
            int initScale) {
        
        this(names, maxSize, sizeRequired, initSize, IDataType.UNSPECIFIED_SIZE, 
                maxScale, scaleRequired, initScale, IDataType.UNSPECIFIED_SCALE);
    }
    
    public SizeScaleDataType(
            String[] names, 
            long maxSize, 
            boolean sizeRequired,
            long initSize,
            long defaultSize,
            int maxScale, 
            boolean scaleRequired,
            int initScale,
            int defaultScale) {
        
        super(names, maxSize, sizeRequired, initSize, defaultSize);
        this.maxScale = maxScale;
        this.requiresScale = scaleRequired;
        this.scale = initScale;
        this.defaultScale = defaultScale;
    }

    public String getDDLName() {
        if(size != IDataType.UNSPECIFIED_SIZE) {
            StringBuffer buf = new StringBuffer(getPrimaryName());
            buf.append(" (");
            buf.append(size);
            
            if(scale != IDataType.UNSPECIFIED_SCALE) {
                buf.append(", ");
                buf.append(scale);
            }
            buf.append(")");
            return buf.toString();
        }
        return getPrimaryName();
    }

    public int getScale() {
        return scale;
    }

    public int getMaxScale() {
        return maxScale;
    }
    
    public boolean acceptsScale() {
        return true;
    }

    public boolean requiresScale() {
        return requiresScale;
    }

    public String validateScale(int scale) {
        return scale > maxScale ? SCALE_TOO_BIG : null;
    }

    public void setScale(int scale) {
        if(maxScale != UNLIMITED_SIZE && scale > maxScale)
            throw new TypeParameterException(this, SCALE_TOO_BIG, scale, maxScale);
        this.scale = scale;
    }

    public boolean isExplicitScale(int scale) {
        return scale != defaultScale && scale != IDataType.UNSPECIFIED_SCALE;
    }

    public IDataType concreteInstance() {
        return new SizeScaleDataType(
                names, maxSize, requiresSize, size, defaultSize, 
                maxScale, requiresScale, scale, defaultScale);
    }

    public boolean equals(Object other) {
        if(super.equals(other)) {
            return scale == ((SizeScaleDataType)other).scale;
        }
        return false;
    }
}
