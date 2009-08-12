/*
 * created 16.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;

/**
 * @author cse
 */
public interface IIndex {

    /**
     * @return the name of the index
     */
    String getName();
    
    /**
     * 
     * @return the table, to which the index belongs
     */
    ITable getTable();

    
    /**
     * 
     * @return the columns of the table building this index
     */
    IColumn[] getColumns();
    
    /**
     * @return 
     */
    public boolean isUnique();
    
    /**
     * 
     * @param column the column to check
     * @return <code>true</code> if the given column is sorted ascending, <code>false</code> else
     */
    public boolean isAscending(IColumn column);
}
