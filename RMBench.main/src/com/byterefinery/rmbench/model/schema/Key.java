/*
 * created 13.03.2005
 * 
 * $Id: Key.java 254 2006-02-24 19:17:27Z hannesn $
 */
package com.byterefinery.rmbench.model.schema;


/**
 * superclass for different types of keys
 * 
 * @author cse
 */
public abstract class Key {

    protected final Table table;
    protected Column[] columns;
    protected String name;

    /**
     * create a new key. Note that the key must additionally be added/set 
     * to the table by the caller of this method.
     * 
     * @param name the constraint name
     * @param columns the columns, assumed to be part of the owning table
     * @param table the owning table
     */
    public Key(String name, Column[] columns, Table table) {
        this.name = name;
        this.columns = columns;
        this.table = table;
    }

	/**
	 * create a new key
	 * @param name the constraint name
	 * @param columnNames column names
	 * @param table the owning table
	 * @throws IllegalArgumentException if a column name does not belong to a 
	 * column of the given table
	 */
    public Key(String name, String[] columnNames, Table table) {
        this.columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			Column col =  table.getColumn(columnNames[i]);
			if(col == null)
				throw new IllegalArgumentException("invalid column name: "+columnNames[i]);
			this.columns[i] = col;
		}
        this.name = name;
        this.table = table;
	}

	public String getName() {
        return name;
    }
    
    /**
     * @return a string, enclosed by parentheses, listing the columns separated by commas (with a following space)
     */
    protected String getColumnsList() {
        StringBuffer definition = new StringBuffer();
        definition.append("(");
        for (int i = 0; i < columns.length; i++) {
            definition.append(columns[i].getName());
            if(i < columns.length - 1)
                definition.append(", ");
        }
        definition.append(")");
        return definition.toString();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Table getTable() {
        return table;
    }

    public Column[] getColumns() {
        return columns;
    }
    
    public String[] getColumnNames() {
        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            names[i] = columns[i].getName();
        }
        return names;
    }
    
    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
    
    /**
     * @param column a new column to add to this key
     */
    public void addColumn(Column column) {
    	addColumn(column, -1);
    }
    
    /**
     * @param column a new column to add to this key
     * @param index the index at which the column is to be inserted 
     */
    public void addColumn(Column column, int index) {
        Column[] newColumns = new Column[columns.length + 1];
        if(index < 0) {
	        System.arraycopy(columns, 0, newColumns, 0, columns.length);
	        newColumns[columns.length] = column;
        } else if(index == 0) {
	        System.arraycopy(columns, 0, newColumns, 1, columns.length);
	        newColumns[0] = column;
        }
        else {
	        System.arraycopy(columns, 0, newColumns, 0, index);
	        newColumns[index] = column;
	        System.arraycopy(columns, index, newColumns, index+1, columns.length-index);
        }
        setColumns(newColumns);
    }
    
    /**
     * @param column a column to remove from this key
     * @return the index at which the column was removed, -1 if not removed 
     */
    public int removeColumn(Column column) {
        Column[] newColumns = new Column[columns.length - 1];
        int index = -1;
        for (int i = 0, i2 = 0; i < columns.length; i++) {
            if(columns[i] == column)
            	index = i;
            else {
                newColumns[i2] = columns[i];
                i2++;
            }
        }
        setColumns(newColumns);
        return index;
    }
    
    /**
     * @return the number of columns in this key
     */
    public int size() {
        return columns.length;
    }

    /**
     * @param index an index into the columns that make up this key
     * @return the column at the given index
     */
    public Column getColumn(int index) {
        return columns[index];
    }

    /**
     * @return true if the given column is part of this foreign key. 
     * <em>Comparison is made by identity</em>
     */
    public boolean contains(Column column) {
        for (int i = 0; i < columns.length; i++) {
            if(columns[i] == column)
                return true;
        }
        return false;
    }
    
    /**
     * Move a column upwards (towards higher index) or downwards (towards lower index)
     * within the key
     * 
     * @param column the column to move
     * @param upDown <code>true</code> if up, <code>false</code> if down
     * @return the column that was moved away, or <code>null</code> if the column
     * could not be moved
     * @throws IllegalArgumentException if the given column is not part of this key
     */
    public Column moveColumn(Column column, boolean upDown) {
        int index = getIndex(column);
        if(index > 0 && upDown == false) { //down
            Column movedCol = columns[index - 1];
            columns[index] = movedCol;
            columns[index - 1] = column;
            return movedCol;
        }
        else if(index < (columns.length - 1) && upDown == true) { //up
            Column movedCol = columns[index + 1];
            columns[index] = movedCol;
            columns[index + 1] = column;
            return movedCol;
        }
        return null;
    }

    /**
     * compute the index of a column in this key
     * @param column a column from this key
     * @return the index (0-based)
     * @throws IllegalArgumentException if the given column is not part of this key
     */
    public int getIndex(Column column) {
        for (int i = 0; i < columns.length; i++) {
            if(columns[i] == column)
                return i;
        }
        throw new IllegalArgumentException("column not in key");
    }
    
    /**
     * @param column a column from this key
     * @return true if <code>column</code> is the first column
     * @throws IllegalArgumentException if the given column is not part of this key
     */
    public boolean isFirst(Column column) {
        return getIndex(column) == 0;
    }
    
    /**
     * @param column a column from this key
     * @return true if <code>column</code> is the last column
     * @throws IllegalArgumentException if the given column is not part of this key
     */
    public boolean isLast(Column column) {
        return getIndex(column) == (columns.length - 1);
    }
}
