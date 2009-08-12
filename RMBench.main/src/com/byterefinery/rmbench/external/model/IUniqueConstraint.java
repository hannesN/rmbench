/*
 * created 28.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;

/**
 * representation of a unique constraint
 * 
 * @author cse
 */
public interface IUniqueConstraint {

    public String getName();

	public ITable getTable();

	public String[] getColumnNames();
}
