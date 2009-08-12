/*
 * created 07.05.2005
 * 
 * $Id: PostgreSQL.java 501 2006-08-25 19:33:49Z hannesn $
 */
package com.byterefinery.rmbench.database.pgsql;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * @author cse
 */
public class PostgreSQL extends DatabaseInfo {
    
    public static final String RESERVED_SCHEMA_NAME = "reserved_schema_name";
    public static final Object PUBLIC_SCHEMA_NAME = "public";
    
    public final IDataType time;
    public final IDataType timetz;
    public final IDataType timestamp;
    public final IDataType timestamptz;
    public final IDataType interval;
    public final IDataType serial;
    public final IDataType bigserial;
    
    public PostgreSQL() {
        registerDataType(new String[]{"SMALLINT", "INT2"}, SQL99.SMALLINT.getPrimaryName(), 1);
        registerDataType(new String[]{"INTEGER", "INT", "INT4"}, SQL99.INTEGER.getPrimaryName(), 1);
        registerDataType(new String[]{"BIGINT", "INT8"}, SQL99.INTEGER.getPrimaryName(), 2);
        
        registerDataType(new String[]{"DOUBLE PRECISION", "FLOAT8"}, SQL99.DOUBLE_PRECISION.getPrimaryName(), 1);
        registerDataType(new String[]{"REAL", "FLOAT4"}, SQL99.FLOAT.getPrimaryName(), 1);
        
        registerDataType("MONEY", SQL99.DECIMAL.getPrimaryName(), 2);
        registerDataType(
                new String[] {"NUMERIC", "DECIMAL"}, 
                IDataType.UNLIMITED_SIZE, false, 10, IDataType.UNLIMITED_SCALE, false, 2,
                SQL99.DECIMAL.getPrimaryName(), 1);
        
        registerDataType(
                new String[] {"VARCHAR", "CHARACTER VARYING"}, 
                IDataType.UNLIMITED_SIZE, false, 100, SQL99.VARCHAR.getPrimaryName(), 1);
        registerDataType(
                new String[] {"CHAR", "CHARACTER", "BPCHAR"}, 
                IDataType.UNLIMITED_SIZE, false, 10, SQL99.CHAR.getPrimaryName(), 1);
        registerDataType("TEXT", SQL99.CLOB.getPrimaryName(), 2);
        
        serial = registerDataType(new String[]{"SERIAL", "SERIAL4"}, SQL99.INTEGER.getPrimaryName(), 2);
        bigserial = registerDataType(new String[]{"BIGSERIAL", "SERIAL8"}, SQL99.INTEGER.getPrimaryName(), 2);
        
        registerDataType(
                new String[]{"BIT"}, 
                IDataType.UNLIMITED_SIZE, false, 10, SQL99.BIT.getPrimaryName(), 1);
        registerDataType(
                new String[]{"BIT VARYING", "VARBIT"}, 
                IDataType.UNLIMITED_SIZE, false, 100, SQL99.BIT_VARYING.getPrimaryName(), 1);
        
        registerDataType(new String[]{"BOOL", "BOOLEAN"}, SQL99.BOOLEAN.getPrimaryName(), 1);
        registerDataType("BYTEA", SQL99.BLOB.getPrimaryName(), 2);
        
        registerDataType("DATE", SQL99.DATE.getPrimaryName(), 1);
        time = registerDataType(
                "TIME", 6, false, IDataType.UNSPECIFIED_SIZE, SQL99.TIME.getPrimaryName(), 1);
        timetz = registerDataType(
                new String[]{"TIMETZ", "TIME WITH TIME ZONE"}, 
                6, false, IDataType.UNSPECIFIED_SIZE,
                SQL99.TIME.getPrimaryName(), 1);
        timestamp = registerDataType(
                "TIMESTAMP", 6, false, IDataType.UNSPECIFIED_SIZE, SQL99.TIMESTAMP.getPrimaryName(), 1);
        timestamptz = registerDataType(
                new String[]{"TIMESTAMPTZ", "TIMESTAMP WITH TIME ZONE"}, 
                6, false, IDataType.UNSPECIFIED_SIZE, SQL99.TIMESTAMP.getPrimaryName(), 2);
        
        interval = registerDataType(
                "INTERVAL", 6, false, IDataType.UNSPECIFIED_SIZE, null, 1);
        
        registerDataType("BOX", null, 1);
        registerDataType("CIRCLE", null, 1);
        registerDataType("LINE", null, 1);
        registerDataType("LSEG", null, 1);
        registerDataType("PATH", null, 1);
        registerDataType("POINT", null, 1);
        registerDataType("POLYGON", null, 1);
        registerDataType("INET", null, 1);
        registerDataType("CIDR", null, 1);
        registerDataType("MACADDR", null, 1);
    }

    public IDataType getDataType(int jdbcType, String typeName, long size, int scale) {
        IDataType dataType = getDataType(typeName);
        if(dataType == null)
            return null;
        
        if(!dataType.equals(time) && 
                !dataType.equals(timetz) && 
                !dataType.equals(timestamp) && 
                !dataType.equals(timestamptz) &&
                !dataType.equals(interval)) {
            
            if(dataType.acceptsSize())
                dataType.setSize(size);
            if(dataType.acceptsScale())
                dataType.setScale(scale);
        }
        return dataType;
    }

    public String validateSchemaName(String name) {
        if(PUBLIC_SCHEMA_NAME.equals(name))
            return RESERVED_SCHEMA_NAME;
        return validateIdentifier(name);
    }

    public boolean isPublicSchema(String catalogname, String name) {
        return PUBLIC_SCHEMA_NAME.equals(name);
    }

    
    public boolean impliesDefault(IDataType dataType) {
		return dataType == serial || dataType == bigserial;
	}
}
