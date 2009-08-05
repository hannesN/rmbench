/*
 * created 10.05.2005
 * 
 * $Id: Oracle.java 683 2008-03-06 22:38:03Z cse $
 */
package com.byterefinery.rmbench.database.oracle;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.Interval;
import com.byterefinery.rmbench.external.model.type.SizeDataType;
import com.byterefinery.rmbench.external.model.type.Timestamp;

/**
 * Oracle 9i database
 * 
 * @author cse
 */
public class Oracle extends DatabaseInfo {

    private static class ByteCharType extends SizeDataType {

        private final String baseName;
        
        public ByteCharType(String name, long maxSize, long initSize) {
            super(name+" BYTE", maxSize, true, initSize);
            this.baseName = name;
        }

        public String getDDLName() {
            StringBuffer buf = new StringBuffer(baseName);
            buf.append("(");
            buf.append(getSize());
            buf.append(" BYTE)");
            return buf.toString();
        }
    }
    
    private static final Timestamp TIMESTAMP = new Timestamp(9);
    private static final Interval INTERVAL_YTOM = Interval.yearToMonth();
    
    public Oracle() {
        registerDataType("VARCHAR2", 4000, true, 100, SQL99.VARCHAR.getPrimaryName(), 1);
        registerDataType(
                new ByteCharType("VARCHAR2", 4000, 100), SQL99.BIT_VARYING.getPrimaryName(), 1);
        registerDataType(
                new String[]{"VARCHAR", "CHARACTER VARYING", "CHAR VARYING"}, 
                4000, true, 100, SQL99.VARCHAR.getPrimaryName(), 2);
        registerDataType(
                new String[]{
                        "NVARCHAR2", 
                        "NATIONAL CHARACTER VARYING", 
                        "NATIONAL CHAR VARYING", 
                        "NCHAR VARYING"}, 
                4000, true, 100, SQL99.NVARCHAR.getPrimaryName(), 1);
        registerDataType(
                new String[]{"CHAR", "CHARACTER"}, 
                2000, false, 10, SQL99.CHAR.getPrimaryName(), 1);
        registerDataType(
                new ByteCharType("CHAR", 2000, 10), SQL99.BIT.getPrimaryName(), 1);
        registerDataType(
                new String[]{"NCHAR", "NATIONAL CHARACTER", "NATIONAL CHAR"}, 
                4000, false, 10, SQL99.NCHAR.getPrimaryName(), 2);
        registerDataType(new String[] {"NUMBER"}, 
                38, false, 
                IDataType.UNSPECIFIED_SIZE, 
                127, false,
                IDataType.UNSPECIFIED_SCALE,
                SQL99.NUMERIC.getPrimaryName(), 1);
        registerDataType("LONG", SQL99.CLOB.getPrimaryName(), 1);
        registerDataType("RAW", 2000, true, 2000, SQL99.BIT_VARYING.getPrimaryName(), 1);
        registerDataType("LONG RAW", SQL99.BLOB.getPrimaryName(), 1);
        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
        registerDataType(
                new String[]{"DECIMAL", "DEC"}, 
                39, false, 39, 
                127, false, 2,
                SQL99.DECIMAL.getPrimaryName(), 1);
        registerDataType(
                new String[]{"NUMERIC", "NUM"}, 
                39, false, 39, 
                127, false, 0, 
                SQL99.NUMERIC.getPrimaryName(), 1);
        registerDataType(
                new String[]{"BINARY_FLOAT", "FLOAT"}, SQL99.FLOAT.getPrimaryName(), 1);
        registerDataType(
                new String[]{"BINARY_DOUBLE", "DOUBLE"}, SQL99.DOUBLE_PRECISION.getPrimaryName(), 1);
        registerDataType("ROWID", SQL99.CHAR.getPrimaryName(), 2);
        registerDataType("UROWID", 4000, false, IDataType.UNSPECIFIED_SIZE, SQL99.VARCHAR.getPrimaryName(), 2);
        
        registerDataType(TIMESTAMP, SQL99.TIMESTAMP.getPrimaryName(), 1);
        registerDataType(Timestamp.withTimeZone(9), SQL99.TIMESTAMP.getPrimaryName(), 2);
        registerDataType(Timestamp.withLocalTimeZone(9), SQL99.TIMESTAMP.getPrimaryName(), 2);
        registerDataType(INTERVAL_YTOM, SQL99.INTERVAL_YTOM.getPrimaryName(), 1);
        registerDataType(Interval.dayToSecond(), SQL99.INTERVAL_DTOS.getPrimaryName(), 1);
        registerDataType("CLOB", SQL99.CLOB.getPrimaryName(), 1);
        registerDataType("NCLOB", SQL99.NCLOB.getPrimaryName(), 1);
        registerDataType("BLOB", SQL99.BLOB.getPrimaryName(), 1);
        
        registerDataType("BFILE", SQL99.CHAR.getPrimaryName(), 2);
    }
    
    public IDataType getDataType(int jdbcType, String typeName, long size, int scale) {
        IDataType dataType = getDataType(typeName);
        if(dataType != null) {
            if(dataType.isExplicitSize(size))
                dataType.setSize(size);
            if(dataType.isExplicitScale(scale))
                dataType.setScale(scale);
        }
        else {
            dataType = TIMESTAMP.parse(typeName);
            if(dataType == null)
                dataType = INTERVAL_YTOM.parse(typeName);
        }
        return dataType;
    }

    public boolean isLoadableObject(String name, String tableType) {
        //is this really a safe rule?
        return !name.startsWith("BIN$");
    }
}
