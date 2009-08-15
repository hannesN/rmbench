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
 * representation of a unique constraint
 * 
 * @author cse
 */
public interface IUniqueConstraint {

    public String getName();

	public ITable getTable();

	public String[] getColumnNames();
}
