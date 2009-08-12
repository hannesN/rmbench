/*
 * created 14.04.2005
 * 
 * $Id: PrimaryKey.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.model.schema;

import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ITable;


/**
 * a primary key that identifies rows of a table
 * 
 * @author cse
 */
public class PrimaryKey extends Key implements Constraint {

    public static final String CONSTRAINT_TYPE = "PRIMARY KEY";
    
    private final IPrimaryKey iprimaryKey = new IPrimaryKey() {

        public String getName() {
            return PrimaryKey.this.getName();
        }

        public ITable getTable() {
            return PrimaryKey.this.getTable().getITable();
        }

        public String[] getColumnNames() {
            return PrimaryKey.this.getColumnNames();
        }
    };
    
    /**
     * create a new primary key and associate it with the given table
     * @param name the key/constraint name
     * @param columns the columns, assumed to belong to <code>table</code>
     * @param table the table with which the key is associated
     */
    public PrimaryKey(String name, Column[] columns, Table table) {
        super(name, columns, table);
        table.setPrimaryKey(this);
    }

    public PrimaryKey(String name, String[] columnNames, Table table) {
        super(name, columnNames, table);
        table.setPrimaryKey(this);
	}

    public IPrimaryKey getIPrimaryKey() {
        return iprimaryKey;
    }
    
    /**
     * @param columnGroup a group of columns from another table
     * @return <code>true</code> if the given group of columns is foreign-key compatible with 
     * this key, in that the number of columns is equal to the number of columns in this key, 
     * and the columns are sequentially type-compatible
     */
    public boolean matches(Column[] columnGroup) {
        if(columnGroup.length != columns.length)
            return false;
        for (int i = 0; i < columns.length; i++) {
            if(!columns[i].getDataType().equals(columnGroup[i].getDataType()))
                return false;
        }
        return true;
    }

    public String getConstraintType() {
        return CONSTRAINT_TYPE;
    }

    public String getConstraintBody() {
        return getColumnsList();
    }
    
    /**
     * remove this key from the owning table
     * @return this object, for convenience
     * @see #restore()
     */
    public PrimaryKey abandon() {
        table.setPrimaryKey(null);
        return this;
    }
    
    /**
     * restore this primary key into the formerly owning table
     * @see #abandon()
     */
    public void restore() {
        table.setPrimaryKey(this);
    }
}
