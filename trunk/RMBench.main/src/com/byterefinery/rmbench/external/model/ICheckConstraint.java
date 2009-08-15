/*
 * created 28.02.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
