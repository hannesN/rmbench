/*
 * created 12.06.2011 
 *
 * Copyright 2011, ByteRefinery
 */
package com.byterefinery.rmbench.database.mssql;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.SizeDataType;

/**
 * this datatype implements the SQLServer-specific feature of "max" precision which
 * may be specified for certain variable size data types. For these types, the size
 * (aka precision) may be specified as a numeric value, which is typically fairly
 * small, or as the string "max", which raises the allowable size to 2^31-1 (2,147,483,647) bytes
 * 
 * @author cse
 */
public class SizeMaxDataType extends SizeDataType 
{
	public static final long MAX_VARSIZE = 2147483647;

	private static final String MAX_POSTFIX = "(max)";
	
	private boolean isMax;
	
	public SizeMaxDataType(String[] names, long maxSize) {
		super(names, maxSize, false, IDataType.UNSPECIFIED_SIZE);
	}

	@Override
	public IDataType concreteInstance() {
        return new SizeMaxDataType(names, maxSize);
	}

	public boolean isMax() {
		return isMax;
	}

	public void setMax(boolean isMax) {
		this.isMax = isMax;
		this.size = isMax ? IDataType.UNSPECIFIED_SIZE : 1;
	}

	@Override
	public String getDDLName() {
		if(isMax) {
            StringBuilder sb = new StringBuilder(getPrimaryName());
            sb.append(MAX_POSTFIX);
            return sb.toString();
		}
		else {
			return super.getDDLName();
		}
	}

	@Override
	public void setSize(long size) {
		if(size == MAX_VARSIZE)
			setMax(true);
		else
			super.setSize(size);
	}

	@Override
	public boolean acceptsSize() {
		return !isMax;
	}

	@Override
	public boolean requiresSize() {
		return !isMax;
	}

	@Override
	public boolean hasExtra() {
		return true;
	}

	@Override
	public String getExtra() {
		return isMax ? MAX_POSTFIX : null;
	}

	@Override
	public void setExtra(String extra) {
		setMax(MAX_POSTFIX.equals(extra));
	}
}
