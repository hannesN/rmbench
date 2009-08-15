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
public interface ITable extends IModelElement {

    /**
     * @return the unqualified table name
     */
    String getName();

    /**
     * @return the schema this table belongs to
     */
    ISchema getSchema();
    
    /**
     * @return the columns owned by this table
     */
    IColumn[] getColumns();
    
    /**
     * @param name a column name
     * @return the column of that name, or <code>null</code>
     */
    IColumn getColumn(String name);
    
    /**
     * @return the primary key
     */
    IPrimaryKey getPrimaryKey();
    
    /**
     * @return the foreign keys owned by this table
     */
    IForeignKey[] getForeignKeys();
    
    /**
     * @return the unique constraints defined on this table
     */
    IUniqueConstraint[] getUniqueConstraints();
    
    /**
     * @return the check constraints defined on this table
     */
    ICheckConstraint[] getCheckConstraints();
}
