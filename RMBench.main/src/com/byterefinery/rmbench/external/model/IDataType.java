/*
 * created 14.03.2005
 * 
 * $Id: IDataType.java 567 2006-11-06 23:24:39Z cse $
 */
package com.byterefinery.rmbench.external.model;

/**
 * specification of a datatype that is associated with a database column 
 * 
 * @author cse
 */
public interface IDataType {

    public static final String SIZE_NOT_SUPPORTED = "notsupported.size";
    public static final String SCALE_NOT_SUPPORTED = "notsupported.scale";
    
    public static final long UNSPECIFIED_SIZE = 0L;
    public static final int UNSPECIFIED_SCALE = 0;
    
    public static final long INVALID_SIZE = Long.MIN_VALUE;
    public static final int INVALID_SCALE = Integer.MIN_VALUE;
    
    public static final long GIGABYTE = 1024*1024*1024;
    public static final long UNLIMITED_SIZE = Long.MAX_VALUE;
    public static final int UNLIMITED_SCALE = Integer.MAX_VALUE;

    /**
     * @return the primary name of this type
     */
    public String getPrimaryName();
    

    /**
     * @return <code>true</code> if the given name is equal to any of the possibly 
     * alternative names of this type
     */
    public boolean hasName(String name);
    
    /**
     * @return the type name as appropriate in a DDL statement, i.e. including size/scale
     */
    public String getDDLName();
    
    /**
     * @return the size value, or {@link INVALID_VALUE} if the type does not 
     * accept a size value 
     */
    public long getSize();
    
    /**
     * @return the maximum size value, or {@link INVALID_VALUE} if the type does not 
     * accept a size value 
     */
    public long getMaxSize();
    
    /**
     * @param size the new size value
     * @throws UnsupportedOperationException if this type does not accept size values
     * @throws IllegalArgumentException if size exceeds the maximum size of this type,
     */
    public void setSize(long size);
    
    /**
     * @return the scale value, or {@link INVALID_VALUE} if the type does not 
     * accept a scale value 
     */
    public int getScale();
    
    /**
     * @return the maximum scale value, or {@link INVALID_VALUE} if the type does not 
     * accept a scale value 
     */
    public int getMaxScale();
    
    /**
     * @param scale the new scale value
     * @throws UnsupportedOperationException if this type does not accept scale values
     * @throws IllegalArgumentException if scale exceeds the maximum scale of this type,
     */
    public void setScale(int scale);
    
    /**
     * @return true if this type carries a size property 
     */
    boolean acceptsSize();
    
    /**
     * @return true if this type carries a scale property 
     */
    boolean acceptsScale();
    
    /**
     * @param size an imported size value
     * @return true if this type accepts size values and the given value is not equal to the 
     * implicit default value (i.e. the one implied when the type is used without a size parameter)
     */
    public boolean isExplicitSize(long size);

    /**
     * @param scale an imported scale value
     * @return true if this type accepts scale values and the given value is not equal to the 
     * implicit default value (i.e. the one implied when the type is used without a scale parameter)
     */
    public boolean isExplicitScale(int scale);
    
    /**
     * @return true if a size value must be specified (there is no implicit default) 
     */
    boolean requiresSize();
    
    /**
     * @return true if a scale value must be specified (there is no implicit default) 
     */
    boolean requiresScale();
    
    /**
     * @param size a size value to be assigned
     * @return null if the given size is acceptable, an error token otherwise. If the type 
     * does not support the size property, the value will be {@link #SIZE_NOT_SUPPORTED} for
     * any size value
     */
    String validateSize(long size);
    
    /**
     * @param scale a scale value to be assigned
     * @return null if the given scale is acceptable, an error token otherwise. If the type 
     * does not support the scale property, the value will be {@link #SCALE_NOT_SUPPORTED} for
     * any scale value
     */
    String validateScale(int scale);
    
    /**
     * @return true if this type carries extra information, which is typically maintained through a type 
     * extension editor and whose internal structure is unknown to RMBench core. 
     */
    public boolean hasExtra();
    
    /**
     * @return the extra information for this type, which is typically maintained through a type 
     * extension editor and whose internal structure is unknown to RMBench core. 
     * <code>null</code> if not applicable
     */
    public String getExtra();

    /**
     * @param extra the extra information for this type, as returned by a previous call to {@link #getExtra()}
     * @throws IllegalArgumentException if not applicable to this type
     */
    public void setExtra(String extra);
    
    /**
     * @return an instance that is equivalent to this object, but may be specialized
     * with size and/or scale values if applicable. The returned object will be initialized
     * with default values if applicable
     */
    public IDataType concreteInstance();
}
