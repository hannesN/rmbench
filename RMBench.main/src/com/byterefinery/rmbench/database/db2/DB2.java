/*
 * created 23.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DB2.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.database.db2;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * @author cse
 */
public class DB2 extends DatabaseInfo {

    public static final String RESERVED_SCHEMA_NAME = "reserved_schema_name";

    private final IDataType char_for_bitdata;
    private final IDataType varchar_for_bitdata;
    private final IDataType longvarchar_for_bitdata;
    
    public DB2() {
        registerDataType("SMALLINT", SQL99.SMALLINT.getPrimaryName(), 1);
        registerDataType(new String[]{"INTEGER", "INT"}, SQL99.INTEGER.getPrimaryName(), 1);
        registerDataType("BIGINT", SQL99.INTEGER.getPrimaryName(), 2);
        registerDataType("REAL", SQL99.REAL.getPrimaryName(), 1);
        registerDataType("FLOAT", 53, false, IDataType.UNSPECIFIED_SIZE, SQL99.FLOAT.getPrimaryName(), 1);
        registerDataType(
                new String[]{"DOUBLE", "DOUBLE PRECISION"}, 
                SQL99.DOUBLE_PRECISION.getPrimaryName(), 1);
        registerDataType(
                new String[]{"DECIMAL", "DEC", "NUMERIC", "NUM"},
                31, false, IDataType.UNSPECIFIED_SIZE, 31, false, IDataType.UNSPECIFIED_SCALE,
                SQL99.DECIMAL.getPrimaryName(), 1);
        
        registerDataType(
                new String[]{"CHAR", "CHARACTER"},
                254, false, IDataType.UNSPECIFIED_SIZE,
                SQL99.CHAR.getPrimaryName(), 1);
        registerDataType(
                new String[]{"VARCHAR", "CHARACTER VARYING", "CHAR VARYING"},
                32672, true, 100,
                SQL99.VARCHAR.getPrimaryName(), 1);
        registerDataType("LONG VARCHAR", SQL99.VARCHAR.getPrimaryName(), 2);

        char_for_bitdata = DB2Bitdata.type(
                new String[]{"CHAR", "CHARACTER"}, 254, false, IDataType.UNSPECIFIED_SIZE); 
        registerDataType(char_for_bitdata, SQL99.BIT.getPrimaryName(), 1);
        varchar_for_bitdata = DB2Bitdata.type(
                new String[]{"VARCHAR", "CHARACTER VARYING", "CHAR VARYING"},
                32672, true, 100); 
        registerDataType(varchar_for_bitdata, SQL99.BIT_VARYING.getPrimaryName(), 1);
        longvarchar_for_bitdata = DB2Bitdata.type("LONG VARCHAR"); 
        registerDataType(longvarchar_for_bitdata, SQL99.BIT_VARYING.getPrimaryName(), 2);
        
        registerDataType(
                new String[]{"BLOB", "BINARY LARGE OBJECT"}, 
                IDataType.GIGABYTE * 2, false, IDataType.UNSPECIFIED_SIZE,
                SQL99.BLOB.getPrimaryName(), 1);
        registerDataType(
                new String[]{"CLOB", "CHAR LARGE OBJECT", "CHARACTER LARGE OBJECT"}, 
                IDataType.GIGABYTE * 2, false, IDataType.UNSPECIFIED_SIZE,
                SQL99.CLOB.getPrimaryName(), 1);
        registerDataType(
                new String[]{"DBCLOB"}, 
                IDataType.GIGABYTE * 2, false, IDataType.UNSPECIFIED_SIZE,
                SQL99.CLOB.getPrimaryName(), 2);
        registerDataType(new String[]{"GRAPHIC",}, 127, false, 100, SQL99.CHAR.getPrimaryName(), 2);
        registerDataType(new String[]{"VARGRAPHIC",}, 16336, true, 100, SQL99.VARCHAR.getPrimaryName(), 2);
        
        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
        registerDataType("TIME", SQL99.TIME.getPrimaryName(), 1);
        registerDataType("TIMESTAMP", SQL99.TIMESTAMP.getPrimaryName(), 1);
        
        //TODO V2: datalink and ref datatypes
    }
    
    public IDataType getDataType(int typeID, String typeName, long size, int scale) {
        
        IDataType dataType;
        if(typeID == java.sql.Types.BINARY)
            dataType = char_for_bitdata.concreteInstance();
        else if(typeID == java.sql.Types.VARBINARY)
            dataType = varchar_for_bitdata.concreteInstance();
        else if(typeID == java.sql.Types.LONGVARBINARY)
            dataType = longvarchar_for_bitdata.concreteInstance();
        else
            dataType = getDataType(typeName);
        
        if(dataType.acceptsSize())
            dataType.setSize(size);
        if(dataType.acceptsScale())
            dataType.setScale(scale);
        
        return dataType;
    }

    public String validateSchemaName(String name) {
        if(name.startsWith("SYS") || name.startsWith("sys"))
            return MessageKeys.RESERVED_NAME;
        return super.validateSchemaName(name);
    }

    public int getMaxConstraintNameLength() {
        return 18;
    }
}
