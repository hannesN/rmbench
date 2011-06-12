/*
 * created 07.06.2011 
 *
 * Copyright 2011, ByteRefinery
 */
package com.byterefinery.rmbench.database.mssql;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * this class implements support for SQL Server 2008 (and most likely earlier versions of SQL Server)
 * 
 * @author cse
 */
public class SQLServer2008 extends DatabaseInfo {

	private final IDataType VARCHAR = new SizeMaxDataType(new String[] {"VARCHAR", "CHARACTER VARYING"}, 8000);
	private final IDataType NVARCHAR = new SizeMaxDataType(new String[] {"NVARCHAR", "NATIONAL CHARACTER.VARYING"}, 4000);
	private final IDataType VARBINARY = new SizeMaxDataType(new String[] {"VARBINARY"}, 8000);
	
	public SQLServer2008() 
	{
		registerDataType("BIGINT", SQL99.INTEGER.getPrimaryName(), 1); 
		registerDataType(new String[] {"INT", "INTEGER"}, SQL99.INTEGER.getPrimaryName(), 1); 
		registerDataType("SMALLINT", SQL99.SMALLINT.getPrimaryName(), 1);
		registerDataType("TINYINT", SQL99.SMALLINT.getPrimaryName(), 1);
		registerDataType("BIT", 1, false, IDataType.UNSPECIFIED_SIZE, SQL99.CHAR.getPrimaryName(), 1); 
		registerDataType(new String[] {"NUMERIC"}, 38, false, IDataType.UNSPECIFIED_SIZE, 38, false, IDataType.UNSPECIFIED_SCALE, SQL99.DECIMAL.getPrimaryName(), 1); 
		registerDataType(new String[] {"DECIMAL", "DEC"}, 38, false, IDataType.UNSPECIFIED_SIZE, 38, false, IDataType.UNSPECIFIED_SCALE, SQL99.DECIMAL.getPrimaryName(), 1);
		registerDataType("MONEY", SQL99.DECIMAL.getPrimaryName(), 1); 
		registerDataType("SMALLMONEY", SQL99.DECIMAL.getPrimaryName(), 1); 
		
		registerDataType(new String[] {"FLOAT"}, 53, false, IDataType.UNSPECIFIED_SIZE, SQL99.FLOAT.getPrimaryName(), 1); 
		registerDataType("REAL", SQL99.FLOAT.getPrimaryName(), 1); 

        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
		registerDataType("DATETIME", SQL99.TIMESTAMP.getPrimaryName(), 1);
		registerDataType(new String[] {"DATETIME2"}, IDataType.UNSPECIFIED_SIZE, false, IDataType.UNSPECIFIED_SIZE, 7, false, 0, SQL99.TIMESTAMP.getPrimaryName(), 1);
		registerDataType(new String[] {"DATETIMEOFFSET"}, 7, false, IDataType.UNSPECIFIED_SIZE, SQL99.UNKNOWN.getPrimaryName(), 1); 
		registerDataType("SMALLDATETIME", SQL99.TIMESTAMP.getPrimaryName(), 1);
		registerDataType(new String[] {"TIME"}, 7, false, IDataType.UNSPECIFIED_SIZE, SQL99.TIME.getPrimaryName(), 1); 
		
        registerDataType(new String[] {"CHAR", "CHARACTER"}, 8000, false, IDataType.UNSPECIFIED_SIZE, SQL99.CHAR.getPrimaryName(), 1);
        registerDataType(VARCHAR, SQL99.VARCHAR.getPrimaryName(), 1);
		registerDataType("TEXT", SQL99.CLOB.getPrimaryName(), 1);
		
		registerDataType(new String[] {"NCHAR", "NATIONAL CHARACTER"}, 4000, false, IDataType.UNSPECIFIED_SIZE, SQL99.NCHAR.getPrimaryName(), 1);
		registerDataType("NTEXT", SQL99.NCLOB.getPrimaryName(), 1); 
		registerDataType(NVARCHAR, SQL99.NCHAR.getPrimaryName(), 1); 
        
		registerDataType("BINARY", 8000, false, IDataType.UNSPECIFIED_SIZE, SQL99.BIT.getPrimaryName(), 1); 
		registerDataType(VARBINARY, SQL99.BIT_VARYING.getPrimaryName(), 1); 
		registerDataType("IMAGE", SQL99.BLOB.getPrimaryName(), 1); 
		
		registerDataType("HIERARCHYID", SQL99.INTEGER.getPrimaryName(), 1); 
		registerDataType(new String[] {"TIMESTAMP", "ROWVERSION"}, SQL99.TIMESTAMP.getPrimaryName(), 1); 
		registerDataType("UNIQUEIDENTIFIER", SQL99.CHAR.getPrimaryName(), 1); 
		registerDataType("SQL_VARIANT", SQL99.UNKNOWN.getPrimaryName(), 1);
		
		registerDataType("XML", SQL99.VARCHAR.getPrimaryName(), 1);
	}

	@Override
	public IDataType getDataType(int typeID, String typeName, long size, int scale) 
	{
		int pos = typeName.indexOf(" identity");
		if(pos > 0) {
			typeName = typeName.substring(0, pos);
		}
		IDataType dataType = getDataType(typeName);
		if(dataType == null) {
			IDataType sql99Type = SQL99.instance.jdbcToType(typeID);
			if(sql99Type != SQL99.UNKNOWN) {
				dataType = getDataType(sql99Type.getPrimaryName());
			}
		}
		if(dataType != null) {
			if (dataType.acceptsSize())
				dataType.setSize(size);
			if (dataType.acceptsScale())
				dataType.setScale(scale);
		}
		return dataType;
	}
}
