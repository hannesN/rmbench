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
public interface IPrimaryKey {

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    ITable getTable();

    /**
     * @return the names of the columns that make up this key
     */
    public String[] getColumnNames();
}
