/*
 * created 14.04.2005
 * 
 * $Id: Index.java 627 2007-02-15 20:52:08Z hannesn $
 */
package com.byterefinery.rmbench.model.schema;

import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * model representation of a table index
 * 
 * @author cse
 */
public class Index extends Key  {

    private final IIndex iindex = new IIndex() {

        public String getName() {
            return name;
        }
        
        public ITable getTable() {
            return table.getITable();
        }
        
        public IColumn[] getColumns() {
            Column[] columns = Index.this.getColumns();
            IColumn[] iColumns = new IColumn[columns.length];
            
            for (int i=0; i<columns.length; i++) {
                iColumns[i] = columns[i].getIColumn();
            }
            
            return iColumns;
        }
        
        public boolean isUnique() {
            return unique;
        }
        
        public boolean isAscending(IColumn column) {
        	Column cols[] = Index.this.getColumns();
        	for (int i=0; i<cols.length; i++) {
        		if (cols[i].getIColumn()==column) {
        			return Index.this.isAscending(cols[i]);
        		}
        	}
        	return false;
        }
    };
    
    private boolean unique;
    private boolean[] ascending;
    
    /**
     * create a new index, all columns ascending order
     * 
     * @param name index name
     * @param columns participating columns
     * @param table target table
     * @param unique unique property
     */
    public Index(
            String name, 
            Column[] columns, 
            Table table, 
            boolean unique) {
        this(name, columns, table, unique, null);
    }

    /**
     * @param ascvalues order per column (true = ascending, false = descending) 
     * @see #Index(String, Column[], Table, boolean) 
     */
    public Index(
            String name, 
            Column[] columns, 
            Table table, 
            boolean unique, 
            boolean[] ascvalues) {
        
        super(name, columns, table);
        this.unique = unique;
        table.addIndex(this);
        initAscending(ascvalues);
    }

    /**
     * @param name index name
     * @param columns column names
     * @param table owning table
     * @param unique unique flag
     * @see #Index(String, Column[], Table, boolean, boolean[]) 
     */
    public Index(
            String name, 
            String[] columns, 
            Table table, 
            boolean unique,
            boolean[] ascvalues) {
        
        super(name, columns, table);
        this.unique = unique;
        table.addIndex(this);
        initAscending(ascvalues);
    }

    public IIndex getIIndex() {
        return iindex;
    }
    
    /**
     * initialize the asc/desc designator array from the given array. If
     * <code>value</code> is <code>null</code> or its length is less than 
     * columns.length, the remaining designators will be set to <code>true</code> 
     *  
     * @param values
     */
    private void initAscending(boolean[] values) {
        
        if(values != null && values.length == columns.length) {
            ascending = values;
        }
        else {
            ascending = new boolean[columns.length];
            for (int i = 0; i < ascending.length; i++) {
                ascending[i] = true;
            }
            if(values != null) {
                for (int i = 0; i < values.length; i++) {
                    ascending[i] = values[i];
                }
            }
        }
    }

    /**
     * bulk change the given properties
     * 
     * @param name new name
     * @param columns the columns this index is based upon
     * @param unique whether this is a unique index
     * @param ascending ascending(=true)/descending(=false) designators
     */
    public void setValues(String name, Column[] columns, boolean unique, boolean[] ascending) {
        this.name = name;
        this.columns = columns;
        this.unique = unique;
        initAscending(ascending);
    }
    
    public boolean isAscending(Column column) {
        int index = getIndex(column);
        return ascending[index];
    }

    public void setAscending(Column column, boolean ascending) {
        int index = getIndex(column);
        this.ascending[index] = ascending;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * @return the array of per-column ascending/descending values
     */
    public boolean[] getAscending() {
        return ascending;
    }
    
    /**
     * abandon this index by removing it from the owning table. 
     * @see #restore()
     */
    public void abandon() {
        getTable().removeIndex(this);
    }

    /**
     * restore this index by adding it to the previously abandonged table
     * @see #abandon()
     */
    public void restore() {
        getTable().addIndex(this);
    }
}
