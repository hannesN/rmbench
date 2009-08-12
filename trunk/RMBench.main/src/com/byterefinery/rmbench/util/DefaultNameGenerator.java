/*
 * created 28.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DefaultNameGenerator.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.util;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IModel;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * default implementation of a name generator for new database objects
 * 
 * @author cse
 */
public class DefaultNameGenerator implements INameGenerator {

    private static final String RENAME_PREFIX = "X";
    private static final String TABLE_NAME_PREFIX = "TABLE_";
    private static final String CONSTRAINT_NAME_SEP = "_";
    private static final String FK_NAME_PREFIX = "FK_";
    private static final String PK_NAME_PREFIX = "PK_";
    private static final String CONSTRAINT_NAME_PREFIX = "CT_";
    private static final String COLUMN_NAME_PREFIX = "COLUMN_";

    public String generateForeignKeyName(IModel model, ITable table) {
    	IForeignKey fks[] = table.getForeignKeys();
    	int counter = fks.length+1;
    	String template = computeConstraintName(
                FK_NAME_PREFIX, model.getDatabaseInfo(), table, CONSTRAINT_NAME_SEP);
    	String newName = template+counter;
    	for (int i=0; i<fks.length; i++) {
    		if (fks[i].getName().equals(newName)) {
    			counter++;
    			newName = template+counter;
    			i=-1;
    		}
    	}
    	return newName;
    }

    public String generatePrimaryKeyName(IModel model, ITable table) {
    	return PK_NAME_PREFIX+table.getName();
    }

    public String generateCheckConstraintName(IModel model, ITable table) {
        ICheckConstraint constraints[] = table.getCheckConstraints();
        int counter = constraints.length+1;
        String template = computeConstraintName(
                CONSTRAINT_NAME_PREFIX, model.getDatabaseInfo(), table, CONSTRAINT_NAME_SEP);
    	String newName = template+counter;
        
        for (int i=0; i<constraints.length; i++) {
        	if (constraints[i].getName().equals(newName)) {
        		counter++;
        		newName = template+counter;
        		i=-1;
        	}
        }
        return newName;
    }

    public String generateUniqueConstraintName(IModel model, ITable table) {
    	IUniqueConstraint constraints[] = table.getUniqueConstraints();
        int counter = constraints.length+1;
        String template = computeConstraintName(
                CONSTRAINT_NAME_PREFIX, model.getDatabaseInfo(), table, CONSTRAINT_NAME_SEP);
    	String newName = template+counter;
        
        for (int i=0; i<constraints.length; i++) {
        	if (constraints[i].getName().equals(newName)) {
        		counter++;
        		newName = template+counter;
        		i=-1;
        	}
        }
        return newName;
    }

    public String generateColumnName(IModel model, ITable table) {
        
        String newName = shorten(
                COLUMN_NAME_PREFIX+table.getColumns().length, 
                model.getDatabaseInfo().getMaxColumnNameLength());
        
        IColumn columns[] = table.getColumns();
        int counter = columns.length;
        
        for (int i=0; i<columns.length; i++) {
        	if (columns[i].getName().equals(newName)) {
        		counter++;
        		newName = newName+counter;
        		i=-1;
        	}
        }
        return newName;
    }

    public String generateForeignKeyColumnName(
            IModel model, ITable sourceTable, IPrimaryKey targetKey, IColumn targetColumn) {
        
        String newName = shorten(
                targetColumn.getTable().getName()+"_"+targetColumn.getName(), 
                model.getDatabaseInfo().getMaxColumnNameLength());
        
        String columnName = newName;
        for(int count=1; sourceTable.getColumn(columnName) != null; count++)
            columnName = newName + count;
        
        return columnName;
    }

    public String generateTableName(IModel model, ISchema schema) {
        int tableNumber = schema.getTableCount() + 1;

        String newName = shorten(
                TABLE_NAME_PREFIX+tableNumber,
                model.getDatabaseInfo().getMaxTableNameLength());
        
        //check if theres already a table with the wanted name
        while(schema.getTable(newName) != null) {
            tableNumber++;
            newName = newName+tableNumber;
        } 
        return newName;
    }

    /**
     * this default implementation just adds a prefix, so the user can later revisit the
     * name
     */
    public String generateNewSchemaName(IModel model, String previousName) {
        return RENAME_PREFIX+previousName;
    }

    /**
     * this default implementation just adds a prefix, so the user can later revisit the
     * name
     */
    public String generateNewTableName(IModel model, ISchema schema, String previousName) {
        return RENAME_PREFIX+previousName;
    }

    /**
     * this default implementation just adds a prefix, so the user can later revisit the
     * name
     */
    public String generateNewColumnName(IModel model, ITable table, String previousName) {
        return RENAME_PREFIX+previousName;
    }

    private String shorten(String name, int maxlength) {
        StringBuffer buffer = new StringBuffer(name);

        if(maxlength < buffer.length())
            buffer.setLength(maxlength);
        
        return buffer.toString();
    }

    private String computeConstraintName(String prefix, IDatabaseInfo database, ITable table, String postfix) {
        
        StringBuffer buffer = new StringBuffer();
        if(prefix != null)
            buffer.append(prefix);
        buffer.append(table.getName());
        if(postfix != null)
            buffer.append(postfix);
        
        int max = database.getMaxConstraintNameLength();
        if(max < buffer.length())
            buffer.setLength(max);
        
        return buffer.toString();
    }
}
