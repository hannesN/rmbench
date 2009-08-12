/*
 * created 21.02.2007
 * 
 * $Id$
 */ 
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.SizeDataType;

/**
 * The Integerdatatypes of MySQL supportes a size, which is only 
 * for display options and several flags, e.g. ZEROFILL or 
 * AUTO_INCREMENT.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLIntegerDataType extends SizeDataType {

	public static final int AUTO_INCREMENT = 1;
	public static final int ZEROFILL = 2;
	public static final int UNSIGNED = 4;
	
	private int flags = 0;

	
	public MySQLIntegerDataType(String name, long maxSize) {
		super(name, maxSize, false, IDataType.UNSPECIFIED_SIZE);
	}
	
	public MySQLIntegerDataType(String[] names, long maxSize) {
		super(names, maxSize, false, IDataType.UNSPECIFIED_SIZE);
	}

	
	public IDataType concreteInstance() {
		MySQLIntegerDataType datatype = new MySQLIntegerDataType(names, maxSize);
		datatype.setFlags(flags);
		return datatype;
	}
	
	/**
	 * Sets an OR-ed flag value, e.g. AUTO_INCRMENT|ZEROFILL .
	 * @param flags
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	/**
	 * 
	 * @return the flag value
	 */
	public int getFlags() {
		return flags;
	}
	
	/**
	 * Convenient method for ((getFlags()&AUTO_INCREMENT)!=0)
	 * @return
	 */
	public boolean isAutoIncrement() {
		return ((flags&AUTO_INCREMENT)!=0);
	}
	
	/**
	 * Convenient method for ((getFlags()&UNSIGNED)!=0)
	 * @return
	 */
	public boolean isUnsigned() {
		return ((flags&UNSIGNED)!=0);
	}
	
	/**
	 * Convenient method for ((getFlags()&ZEROFILL)!=0)
	 * @return
	 */
	public boolean isZeroFill() {
		return ((flags&ZEROFILL)!=0);
	}
	
	
	public String getExtra() {
		return Integer.toString(flags);
	}
	
	public void setExtra(String extra) {
		try {
			flags = Integer.parseInt(extra);
		} catch (NumberFormatException e) {
			RMBenchPlugin.logError(Messages.MySQLIntegerDataType_NumberFormatExceptionText, e); //$NON-NLS-1$
		}
	}
	
	public boolean hasExtra() {
		return true;
	}
	
	public String getDDLName() {
		StringBuffer buffer = new StringBuffer(10);
        buffer.append(getPrimaryName());
        
        if (isAutoIncrement())
        	buffer.append(" AUTO_INCREMENT");
        
        if (isUnsigned())
        	buffer.append(" UNSIGNED");
        
        if (isZeroFill())
        	buffer.append(" ZEROFILL");
                
        return buffer.toString();
	}
}
