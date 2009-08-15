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
