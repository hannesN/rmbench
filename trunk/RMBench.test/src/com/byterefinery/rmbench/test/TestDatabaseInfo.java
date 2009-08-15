/*
 * created 04.12.2005
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
package com.byterefinery.rmbench.test;

import com.byterefinery.rmbench.external.database.sql99.SQL99;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * a database info that only supports a few data types
 * @author cse
 */
public class TestDatabaseInfo extends com.byterefinery.rmbench.external.database.DatabaseInfo {

    public TestDatabaseInfo() {
        registerDataType("SMALLINT", SQL99.SMALLINT.getPrimaryName(), 1);
        registerDataType(new String[]{"INTEGER", "INT"}, SQL99.INTEGER.getPrimaryName(), 1);
        registerDataType("BIGINT", SQL99.INTEGER.getPrimaryName(), 1);
        registerDataType("REAL", SQL99.REAL.getPrimaryName(), 1);
    }
    
    public IDataType getDataType(int typeID, String typeName, long size, int scale) {
        return null;
    }
}
