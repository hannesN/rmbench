/*
 * created 14.03.2005
 * 
 * $Id: IntegralDataType.java 567 2006-11-06 23:24:39Z cse $
 */
package com.byterefinery.rmbench.external.model.type;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * generic datatype class
 * 
 * @author cse
 */
public class IntegralDataType implements IDataType {

    protected final String[] names;

    public IntegralDataType(String name) {
        this(new String[]{name});
    }
    
    public IntegralDataType(String[] names) {
        this.names = names;
    }
    
    public String getPrimaryName() {
        return names[0];
    }

    public String getDDLName() {
        return getPrimaryName();
    }

    public long getSize() {
        return UNSPECIFIED_SIZE;
    }

    public long getMaxSize() {
        return UNSPECIFIED_SIZE;
    }

    public void setSize(long size) {
        throw new UnsupportedOperationException();
    }

    public int getScale() {
        return UNSPECIFIED_SCALE;
    }
    
    public int getMaxScale() {
        return UNSPECIFIED_SCALE;
    }
    
    public void setScale(int scale) {
        throw new UnsupportedOperationException();
    }

    public boolean acceptsSize() {
        return false;
    }

    public boolean acceptsScale() {
        return false;
    }
    
    public boolean isExplicitSize(long size) {
        return false;
    }

    public boolean isExplicitScale(int scale) {
        return false;
    }
    
    public boolean requiresSize() {
        return false;
    }

    public boolean requiresScale() {
        return false;
    }

    public String validateSize(long size) {
        return IDataType.SIZE_NOT_SUPPORTED;
    }

    public String validateScale(int scale) {
        return IDataType.SCALE_NOT_SUPPORTED;
    }
    
    public boolean hasName(String name) {
        for (int i = 0; i < names.length; i++) {
            if(names[i].equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public IDataType concreteInstance() {
        return this;
    }

    public boolean equals(Object obj) {
        if(obj != null && obj.getClass() == getClass())
            return ((IntegralDataType)obj).getPrimaryName().equals(getPrimaryName());
        return false;
    }

    public int hashCode() {
        return getPrimaryName().hashCode();
    }
    
    public String toString() {
        return getDDLName();
    }

    public boolean hasExtra() {
        return false;
    }

    public String getExtra() {
        return null;
    }

    public void setExtra(String extra) {
        throw new IllegalArgumentException();
    }
}
