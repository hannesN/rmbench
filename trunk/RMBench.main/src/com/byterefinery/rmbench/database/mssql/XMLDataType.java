/*
 * created 17.06.2011 
 *
 * Copyright 2011, ByteRefinery
 */
package com.byterefinery.rmbench.database.mssql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * representation of the XML datatatype implemented by SQL Server
 * 
 * @author cse
 */
public class XMLDataType implements IDataType 
{
	public static final String NAME = "XML";
	
	private static final Pattern EXTRA_PATTERN = Pattern.compile("\\(((CONTENT|DOCUMENT)\\s)?(\\S+)\\)");
	
	private String restriction, schemaName;
	
	public XMLDataType() {
	}
	
	public XMLDataType(String restriction, String schemaName) 
	{
		this.restriction = restriction;
		this.schemaName = schemaName;
	}

	@Override
	public String getPrimaryName() {
		return NAME;
	}

	@Override
	public boolean hasName(String name) {
		return NAME.equals(name);
	}

	@Override
	public String getDDLName() 
	{
		if(schemaName != null) {
			StringBuilder sb = new StringBuilder(NAME);
			appendExtra(sb);
			return sb.toString();
		}
		return NAME;
	}

	private void appendExtra(StringBuilder sb) {
		sb.append("(");
		if(restriction != null) {
			sb.append(restriction);
			sb.append(" ");
		}
		sb.append(schemaName);
		sb.append(")");
	}

	@Override
	public long getSize() {
		return IDataType.UNSPECIFIED_SIZE;
	}

	@Override
	public long getMaxSize() {
		return IDataType.UNSPECIFIED_SIZE;
	}

	@Override
	public void setSize(long size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getScale() {
		return IDataType.UNSPECIFIED_SCALE;
	}

	@Override
	public int getMaxScale() {
		return IDataType.UNSPECIFIED_SCALE;
	}

	@Override
	public void setScale(int scale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean acceptsSize() {
		return false;
	}

	@Override
	public boolean acceptsScale() {
		return false;
	}

	@Override
	public boolean isExplicitSize(long size) {
		return false;
	}

	@Override
	public boolean isExplicitScale(int scale) {
		return false;
	}

	@Override
	public boolean requiresSize() {
		return false;
	}

	@Override
	public boolean requiresScale() {
		return false;
	}

	@Override
	public String validateSize(long size) {
		return null;
	}

	@Override
	public String validateScale(int scale) {
		return null;
	}

	@Override
	public boolean hasExtra() {
		return true;
	}

	@Override
	public String getExtra() {
		if(schemaName != null && schemaName.length() > 0) {
			StringBuilder sb = new StringBuilder();
			appendExtra(sb);
			return sb.toString();
		}
		else {
			return null;
		}
	}

	@Override
	public void setExtra(String extra) {
		if(extra != null && extra.length() > 0) {
			Matcher matcher = EXTRA_PATTERN.matcher(extra);
			if(matcher.matches()) {
				restriction = matcher.group(2);
				schemaName = matcher.group(3);
			}
			else {
				RMBenchPlugin.logError("could not restore extra data {0} for XML datatype", extra);
			}
		}
	}

	@Override
	public IDataType concreteInstance() {
		return new XMLDataType(restriction, schemaName);
	}

	public String getRestriction() {
		return restriction;
	}

	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
}
