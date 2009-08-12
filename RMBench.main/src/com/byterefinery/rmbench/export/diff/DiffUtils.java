/*
 * created 29.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DiffUtils.java 496 2006-08-25 12:26:10Z cse $
 */
package com.byterefinery.rmbench.export.diff;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.model.schema.Constraint;

/**
 * helper methods to unify generation of comparison byte sequences for DB and model nodes.
 * Note that the strings generated here are only used for the purpose of comparison
 * 
 * @author cse
 */
public class DiffUtils {

    //hide the constructor
    private DiffUtils() {}

    /**
     * @return a byte comparison string value for a column
     */
    public static String generateColumnValue(
            String name, 
            IDataType dataType, 
            boolean nullable, 
            String defaultValue,
            boolean ignoreCase) {
        
        StringBuffer buf = new StringBuffer();
        buf.append(ignoreCase ? name.toUpperCase() : name);
        buf.append(" ");
        buf.append(dataType.getDDLName());
        if(!nullable) {
            buf.append(" NOT NULL");
        }
        if(defaultValue != null) {
            buf.append(" DEFAULT ");
            buf.append(defaultValue);
        }
        return buf.toString();
    }

    /**
     * @return a byte comparison string value for a foreign key
     */
    public static String generateForeignKeyValue(
            String name, 
            String[] columnNames, 
            String targetSchemaName,
            String targetTableName,
            IForeignKey.Action deleteAction,
            IForeignKey.Action updateAction,
            boolean ignoreCase) {
        
        StringBuffer buf = new StringBuffer();
        buf.append("FOREIGN KEY ");
        buf.append(ignoreCase ? name.toUpperCase() : name);
        buf.append(" (");
        for (int i = 0; i < columnNames.length; i++) {
            buf.append(ignoreCase ? columnNames[i].toUpperCase() : columnNames[i]);
            if(i < columnNames.length -1)
                buf.append(",");
        }
        buf.append(") REFERENCES ");
        buf.append(ignoreCase ? targetSchemaName.toUpperCase(): targetSchemaName);
        buf.append(".");
        buf.append(ignoreCase ? targetTableName.toUpperCase() : targetTableName);
        if(deleteAction != null) {
            buf.append(" ON DELETE ");
            buf.append(deleteAction.getName());
        }
        if(updateAction != null) {
            buf.append(" ON UPDATE ");
            buf.append(updateAction.getName());
        }
        return buf.toString();
    }

    /**
     * @return a byte comparison string value for an index
     */
    public static String generateIndexValue(
            String name, 
            boolean unique, 
            String[] columnNames, 
            boolean[] ascending,
            boolean ignoreCase) {
        
        StringBuffer buf = new StringBuffer();
        if(unique)
            buf.append("UNIQUE ");
        buf.append("INDEX (");
        for (int i = 0; i < columnNames.length; i++) {
            buf.append(ignoreCase ? columnNames[i].toUpperCase() : columnNames[i]);
            buf.append(ascending[i] ? "ASC" : "DESC");
            if(i < columnNames.length - 1)
                buf.append(", ");
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * @return a byte comparison string value for a (unique or check) constraint
     */
    public static String generateConstraintValue(Constraint constraint) {
        return 
            "CONSTRAINT "+
            constraint.getName()+" "+
            constraint.getConstraintType()+" "+
            constraint.getConstraintBody();
    }

    /**
     * @return a byte comparison string value for a primary key
     */
    public static String generatePrimaryKeyValue(String[] columns, boolean ignoreCase) {
        StringBuffer buf = new StringBuffer();
        buf.append("PRIMARY KEY (");
        for (int i = 0; i < columns.length; i++) {
        	if(ignoreCase)
        		buf.append(columns[i].toUpperCase());
        	else
        		buf.append(columns[i]);
            if(i < columns.length - 1)
                buf.append(", ");
        }
        buf.append(")");
        return buf.toString();
    }
}
