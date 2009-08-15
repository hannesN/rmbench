/*
 * created 07.11.2006
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
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * The MySQL ENUM Datatype
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLEnumDataType extends MySQLListDatatype {

	public MySQLEnumDataType() {
		super("ENUM");
	}

    public IDataType concreteInstance() {
        MySQLEnumDataType instance = new MySQLEnumDataType();
        
        instance.setElements(new String[]{"a"});
        return instance;
    }
}
