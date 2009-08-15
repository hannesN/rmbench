/*
 * created 16.12.2005
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
