/*
 * created 07.11.2006
 * 
 * Copyright 2006, ByteRefinery
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
