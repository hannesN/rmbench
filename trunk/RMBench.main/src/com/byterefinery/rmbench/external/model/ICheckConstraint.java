/*
 * created 28.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;

/**
 * representation of a CHECK constraint
 * 
 * @author cse
 */
public interface ICheckConstraint {

    public String getName();
    
    ITable getTable();

	public String getExpression();
}
