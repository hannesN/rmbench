/*
 * created 29.03.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

public class MySQL extends DatabaseInfo {

    private final int MAX_CHAR_LENGTH = 255;
    private final int MAX_VARCHAR_LENGTH = 65535;

    private final MySQLListDatatype enumDataType = new MySQLEnumDataType();
    private final MySQLListDatatype setDataType = new MySQLSetDataType();
    
    public MySQL() {

        // string types
        registerDataType("CHAR", MAX_CHAR_LENGTH, true, IDataType.UNSPECIFIED_SIZE, SQL99.CHAR
                .getPrimaryName(), 1);
        registerDataType("NATIONAL CHAR", MAX_CHAR_LENGTH, true, IDataType.UNSPECIFIED_SIZE,
                SQL99.NCHAR.getPrimaryName(), 1);
        registerDataType(new String[] { "VARCHAR", "CHARACTER VARYING" }, MAX_VARCHAR_LENGTH, true,
                1, SQL99.VARCHAR.getPrimaryName(), 1);

        registerDataType("BIT", 64, false, 1, SQL99.BIT.getPrimaryName(), 1);
        registerDataType("BINARY", MAX_CHAR_LENGTH, true, IDataType.UNSPECIFIED_SIZE, SQL99.BIT
                .getPrimaryName(), 2);
        registerDataType("VARBINARY", MAX_VARCHAR_LENGTH, true, 1, SQL99.BIT_VARYING
                .getPrimaryName(), 1);

        registerDataType("TINYBLOB", SQL99.BLOB.getPrimaryName(), 2);
        registerDataType("TINYTEXT", SQL99.CLOB.getPrimaryName(), 2);

        registerDataType("BLOB", SQL99.BLOB.getPrimaryName(), 1);
        registerDataType("TEXT", SQL99.CLOB.getPrimaryName(), 1);
        registerDataType("MEDIUMBLOB", SQL99.BLOB.getPrimaryName(), 1);
        registerDataType("MEDIUMTEXT", SQL99.CLOB.getPrimaryName(), 2);
        registerDataType("LONGBLOB", SQL99.BLOB.getPrimaryName(), 2);
        registerDataType("LONGTEXT", SQL99.BLOB.getPrimaryName(), 2);

        // numeric types
        registerDataType(new String[] { "BOOLEAN", "BOOL" }, IDataType.UNLIMITED_SIZE, false,
                IDataType.UNSPECIFIED_SIZE, SQL99.SMALLINT.getPrimaryName(), 2);

        registerDataType(new MySQLIntegerDataType("TINYINT", 4), SQL99.SMALLINT
                .getPrimaryName(), 2);
        registerDataType(new MySQLIntegerDataType("SMALLINT", 6), SQL99.SMALLINT
                .getPrimaryName(), 1);
        registerDataType(new MySQLIntegerDataType("MEDIUMINT", 9), SQL99.INTEGER
                .getPrimaryName(), 2);
        registerDataType(new MySQLIntegerDataType(new String[] { "INT",
				"INTEGER" }, (long) 11), SQL99.INTEGER.getPrimaryName(), 1);
        // TODO MySQL.INTEGER --> SQL99.BIGINT ... no good idea :(
        registerDataType(new MySQLIntegerDataType("BIGINT", 20), SQL99.INTEGER
                .getPrimaryName(), 2);
        
        registerDataType("SERIAL", SQL99.INTEGER.getPrimaryName(), 3);

        registerDataType(new MySQLDoubleDataType(
                new String[] { "FLOAT" }, 
                IDataType.UNLIMITED_SIZE, 
                false,
                IDataType.UNSPECIFIED_SIZE, 
                IDataType.UNLIMITED_SCALE, 
                false,
                IDataType.UNSPECIFIED_SCALE)
                , SQL99.FLOAT.getPrimaryName(), 1);

        registerDataType(new MySQLDoubleDataType(
                new String[] { "DOUBLE", "DOUBLE PRECISION" }, 
                IDataType.UNLIMITED_SIZE, 
                false,
                IDataType.UNSPECIFIED_SIZE, 
                IDataType.UNLIMITED_SCALE, 
                false,
                IDataType.UNSPECIFIED_SCALE)
                , SQL99.DOUBLE_PRECISION.getPrimaryName(), 1);
        
        registerDataType(new String[] { "DECIMAL", "DEC" }, 65, false,
                10, 30, false, 0, SQL99.DECIMAL.getPrimaryName(), 1);
        
        // date & time
        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
        registerDataType(new String[]{"TIMESTAMP", "DATETIME"}, SQL99.TIMESTAMP.getPrimaryName(), 1);
        registerDataType("TIME", SQL99.TIME.getPrimaryName(), 1);
        
        registerDataType(enumDataType, null, 1);
        registerDataType(setDataType, null, 1);
    }

    public IDataType getDataType(int typeID, String typeName, long size, int scale) {

        IDataType dataType;

        // handle the unsigned flag
        String[] fragments = typeName.split(" ", 2);
        if ((fragments.length >= 2) && fragments[1].equalsIgnoreCase("unsigned"))
            dataType = getDataType(fragments[0]);
        else
            dataType = getDataType(typeName);

        if (dataType != null) {
            if (dataType.acceptsScale())
                dataType.setScale(scale);
            if (dataType.acceptsSize())
                dataType.setSize(size);
        }

        return dataType;
    }

}
