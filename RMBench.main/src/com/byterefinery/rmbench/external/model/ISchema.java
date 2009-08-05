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
public interface ISchema {

    /**
     * @return the schema name
     */
    String getName();
    
    /**
     * @return the tables that belong to this schema
     */
    ITable[] getTables();
    
    /**
     * @param name a table name
     * @return the table of that name, or <code>null</code>
     */
    ITable getTable(String name);
    
    /**
     * @return the number of tables in this schema
     */
    int getTableCount();
}
