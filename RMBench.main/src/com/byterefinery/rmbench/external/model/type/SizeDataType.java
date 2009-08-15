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
 * $Id: SizeDataType.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external.model.type;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * a data type that accepts a size value
 * 
 * @author cse
 */
public class SizeDataType extends IntegralDataType {

    public static final String SIZE_TOO_BIG = "maxexceeded.size";

    protected long size;
    
    protected final long maxSize;
    protected final long defaultSize;
    protected final boolean requiresSize;
    
    /**
     * parse the given string for a size specification, which consists of a number between, 
     * opening and a closing brace, with optional whitespace
     * 
     * @param string the input string
     * @param startPos the starting position in the string
     * @return the numeric value found, or {@link IDataType#UNSPECIFIED_SIZE} if no match was found
     * @throws IllegalArgumentException if the closing brace is missing or the value between the 
     * braces is not an integral numer
     */
    protected static long parseNumber(String string, int startPos) {
        long result = IDataType.UNSPECIFIED_SIZE;
        int pos = string.indexOf('(', startPos);
        if(pos > 0) {
            int pos2 = string.indexOf(')', pos);
            if(pos2 <= 0)
                throw new IllegalArgumentException("invalid syntax: "+string);
            
            String prec = string.substring(pos+1, pos2);
            try {
            result = Long.parseLong(prec.trim());
            }
            catch(NumberFormatException x) {
                throw new IllegalArgumentException("invalid syntax: "+string);
            }
        }
        return result;
    }

    /**
     * @param name the type name
     * @param maxSize the maximum size
     * @param sizeRequired whether a size value is required
     * @param initSize the initial size to be set
     */
    public SizeDataType(String name, long maxSize, boolean sizeRequired, long initSize) {
        this(new String[]{name}, maxSize, sizeRequired, initSize);
    }

    /**
     * @param names tha alternative names, the first being the primary name
     * @param maxSize the maximum size
     * @param sizeRequired whether a size value must be given
     * @param initSize the initial size to be set
     */
    public SizeDataType(String[] names, long maxSize, boolean sizeRequired, long initSize) {
        this(names, maxSize, sizeRequired, initSize, IDataType.UNSPECIFIED_SIZE);
    }

    /**
     * @param names tha alternative names, the first being the primary name
     * @param maxSize the maximum size
     * @param sizeRequired whether a size value must be given
     * @param initSize the initial size to be set
     * @param defaultSize the default size, if a size parameter is neither required nor given
     */
    public SizeDataType(String[] names, long maxSize, boolean sizeRequired, long initSize, long defaultSize) {
        super(names);
        this.maxSize = maxSize;
        this.requiresSize = sizeRequired;
        this.size = initSize;
        this.defaultSize = defaultSize;
    }
    
    public String getDDLName() {
        if(size != IDataType.UNSPECIFIED_SIZE) {
            StringBuffer buf = new StringBuffer(getPrimaryName());
            buf.append(" (");
            buf.append(getSize());
            buf.append(")");
            return buf.toString();
        }
        return getPrimaryName();
    }

    public long getSize() {
        return size;
    }

    public long getMaxSize() {
        return maxSize;
    }
    
    public boolean acceptsSize() {
        return true;
    }

    public boolean isExplicitSize(long size) {
        return size != defaultSize && size != IDataType.UNSPECIFIED_SIZE;
    }

    public String validateSize(long size) {
        return size > maxSize ? SIZE_TOO_BIG : null;
    }

    public boolean requiresSize() {
        return requiresSize;
    }

    public void setSize(long size) {
        if(maxSize != UNLIMITED_SIZE && size > maxSize)
            throw new TypeParameterException(this, SIZE_TOO_BIG, size, maxSize);
        this.size = size;
    }

    public IDataType concreteInstance() {
        return new SizeDataType(names, maxSize, requiresSize, size);
    }
    
    public boolean equals(Object other) {
        if(super.equals(other)) {
            return ((SizeDataType)other).size == size;
        }
        return false;
    }
}
