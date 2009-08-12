/*
 * created 09.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: JdbcTypes.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.jdbc;

import java.util.HashMap;
import java.util.Map;

/**
 * mapping of strings to types from {@link java.sql.Types}
 * 
 * @author cse
 */
public class JdbcTypes {

    private static final Map<String, Integer> NameToTypeID = new HashMap<String, Integer>(30);
    private static final Map<Integer, String> IDToTypeName = new HashMap<Integer, String>(30);
    static {
        NameToTypeID.put("BIT", new Integer(java.sql.Types.BIT));
        NameToTypeID.put("TINYINT", new Integer(java.sql.Types.TINYINT));
        NameToTypeID.put("SMALLINT", new Integer(java.sql.Types.SMALLINT));
        NameToTypeID.put("INTEGER", new Integer(java.sql.Types.INTEGER));
        NameToTypeID.put("BIGINT", new Integer(java.sql.Types.BIGINT));
        NameToTypeID.put("FLOAT", new Integer(java.sql.Types.FLOAT));
        NameToTypeID.put("REAL", new Integer(java.sql.Types.REAL));
        NameToTypeID.put("DOUBLE", new Integer(java.sql.Types.DOUBLE));
        NameToTypeID.put("NUMERIC", new Integer(java.sql.Types.NUMERIC));
        NameToTypeID.put("DECIMAL", new Integer(java.sql.Types.DECIMAL));
        NameToTypeID.put("CHAR", new Integer(java.sql.Types.CHAR));
        NameToTypeID.put("VARCHAR", new Integer(java.sql.Types.VARCHAR));
        NameToTypeID.put("LONGVARCHAR", new Integer(java.sql.Types.LONGVARCHAR));
        NameToTypeID.put("DATE", new Integer(java.sql.Types.DATE));
        NameToTypeID.put("TIME", new Integer(java.sql.Types.TIME));
        NameToTypeID.put("TIMESTAMP", new Integer(java.sql.Types.TIMESTAMP));
        NameToTypeID.put("BINARY", new Integer(java.sql.Types.BINARY));
        NameToTypeID.put("VARBINARY", new Integer(java.sql.Types.VARBINARY));
        NameToTypeID.put("LONGVARBINARY", new Integer(java.sql.Types.LONGVARBINARY));
        NameToTypeID.put("NULL", new Integer(java.sql.Types.NULL));
        NameToTypeID.put("OTHER", new Integer(java.sql.Types.OTHER));
        NameToTypeID.put("JAVA_OBJECT", new Integer(java.sql.Types.JAVA_OBJECT));
        NameToTypeID.put("DISTINCT", new Integer(java.sql.Types.DISTINCT));
        NameToTypeID.put("STRUCT", new Integer(java.sql.Types.STRUCT));
        NameToTypeID.put("ARRAY", new Integer(java.sql.Types.ARRAY));
        NameToTypeID.put("BLOB", new Integer(java.sql.Types.BLOB));
        NameToTypeID.put("CLOB", new Integer(java.sql.Types.CLOB));
        NameToTypeID.put("REF", new Integer(java.sql.Types.REF));
        NameToTypeID.put("DATALINK", new Integer(java.sql.Types.DATALINK));
        NameToTypeID.put("BOOLEAN", new Integer(java.sql.Types.BOOLEAN));
        
        for (Map.Entry<String, Integer> entry : NameToTypeID.entrySet()) {
            IDToTypeName.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * @param name a name from {@link java.sql.Types}
     * @return the corresponding value, or {@link java.sql.Types#OTHER}
     */
    public static int getID(String name) {
        Integer integer = (Integer)NameToTypeID.get(name);
        return integer != null ? integer.intValue() : java.sql.Types.OTHER;
    }
    
    /**
     * @param id an ID from {@link java.sql.Types}
     * @return the correspoding name, or the id as String
     */
    public static String getName(int id) {
        String result = (String)IDToTypeName.get(new Integer(id));
        return result != null ? result : String.valueOf(id);
    }
}
