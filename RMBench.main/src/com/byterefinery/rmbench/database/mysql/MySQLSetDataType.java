/*
 * created 06.11.2006
 *
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * The MySQL SET data type
 * 
 * @author cse
 */
public class MySQLSetDataType extends MySQLListDatatype {

	public MySQLSetDataType() {
        super("SET");
    }

    public IDataType concreteInstance() {
        MySQLSetDataType instance = new MySQLSetDataType();
        
        instance.setElements(new String[]{"a"});
        return instance;
    }
}
