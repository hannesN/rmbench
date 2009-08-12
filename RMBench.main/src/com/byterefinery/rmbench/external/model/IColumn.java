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
public interface IColumn {

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the owning table
     */
    ITable getTable();

    /**
     * @return the data type
     */
    IDataType getDataType();

    /**
     * @return whether a NOT NULL constraint is defined on this column
     */
    boolean getNullable();

    /**
     * @return the default clause, or <code>null</code> if none defined
     */
    String getDefault();

    /**
     * @return the column comment, or <code>null</code> if none defined
     */
    String getComment();

    /**
     * @return the precision/size if available, or {@link IDataType#UNSPECIFIED_SIZE}
     */
	long getSize();

    /**
     * @return the scale if available, or {@link IDataType#UNSPECIFIED_SCALE}
     */
	int getScale();

	/**
	 * @return true if this column is part of the primary key
	 */
	boolean belongsToPrimaryKey();
}
