/*
 * created 26.06.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.derby;

import com.byterefinery.rmbench.database.db2.DB2Bitdata;
import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * @author Hannes Niederhausen
 */
public class Derby extends DatabaseInfo {

    private final IDataType char_for_bitdata;

    private final IDataType varchar_for_bitdata;

    private final IDataType longvarchar_for_bitdata;

    public Derby() {

        // registering datatypes
        registerDataType("SMALLINT", SQL99.SMALLINT.getPrimaryName(), 1);
        registerDataType(new String[] { "INT", "INTEGER" }, SQL99.INTEGER.getPrimaryName(), 1);
        registerDataType("BIGINT", SQL99.INTEGER.getPrimaryName(), 2);

        registerDataType("REAL", SQL99.REAL.getPrimaryName(), 1);
        registerDataType(
                new DerbyFloatDatatype("FLOAT", 53, false, 53, IDataType.UNSPECIFIED_SIZE),
                SQL99.FLOAT.getPrimaryName(), 1);
        registerDataType(
                new String[] { "DOUBLE PRECISION", "DOUBLE" }, 
                SQL99.DOUBLE_PRECISION.getPrimaryName(), 1);
        registerDataType(
                new String[] { "DECIMAL", "NUMERIC" },
                31, false, IDataType.UNSPECIFIED_SIZE, 31, false, IDataType.UNSPECIFIED_SCALE,
                SQL99.DECIMAL.getPrimaryName(), 1);

        registerDataType(
                new String[] { "CHAR", "CHARACTER" }, 
                Integer.MAX_VALUE, false, 1, SQL99.CHAR.getPrimaryName(), 1);

        registerDataType("BLOB", 2147483647, true, 1024 * 1024, SQL99.BLOB.getPrimaryName(), 1);
        registerDataType("CLOB", 2147483647, true, 1024 * 1024, SQL99.CLOB.getPrimaryName(), 1);

        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
        registerDataType("TIME", SQL99.TIME.getPrimaryName(), 1);
        registerDataType("TIMESTAMP", SQL99.TIMESTAMP.getPrimaryName(), 1);

        registerDataType(new String[] { "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" }, 32672,
                true, 100, SQL99.VARCHAR.getPrimaryName(), 1);

        registerDataType("LONG VARCHAR", SQL99.VARCHAR.getPrimaryName(), 2);

        char_for_bitdata = DB2Bitdata.type(new String[] { "CHAR", "CHARACTER" }, 254, false, 1);
        registerDataType(char_for_bitdata, SQL99.BIT.getPrimaryName(), 1);

        varchar_for_bitdata = DB2Bitdata.type(new String[] { "VARCHAR", "CHARACTER VARYING",
                "CHAR VARYING" }, 32672, true, 100);
        registerDataType(varchar_for_bitdata, SQL99.BIT_VARYING.getPrimaryName(), 1);

        longvarchar_for_bitdata = DB2Bitdata.type("LONG VARCHAR");
        registerDataType(longvarchar_for_bitdata, SQL99.BIT_VARYING.getPrimaryName(), 2);
    }

    public IDataType getDataType(int typeID, String typeName, long size, int scale) {

        IDataType dataType;
        if (typeID == java.sql.Types.BINARY)
            dataType = char_for_bitdata.concreteInstance();
        else if (typeID == java.sql.Types.VARBINARY)
            dataType = varchar_for_bitdata.concreteInstance();
        else if (typeID == java.sql.Types.LONGVARBINARY)
            dataType = longvarchar_for_bitdata.concreteInstance();
        else
            dataType = getDataType(typeName);

        if (dataType.acceptsSize())
            dataType.setSize(size);
        if (dataType.acceptsScale())
            dataType.setScale(scale);

        return dataType;
    }

    public String validateSchemaName(String name) {
        if (name.startsWith("SYS") || name.startsWith("sys"))
            return MessageKeys.RESERVED_NAME;
        return super.validateSchemaName(name);
    }
}
