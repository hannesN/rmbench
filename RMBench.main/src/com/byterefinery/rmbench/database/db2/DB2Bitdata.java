/*
 * created 24.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DB2Bitdata.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.database.db2;

import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.IntegralDataType;
import com.byterefinery.rmbench.external.model.type.SizeDataType;

/**
 * @author cse
 */
public class DB2Bitdata {

    private static final String FOR_BIT_DATA = " FOR BIT DATA";
    
    public static IDataType type(String[] names, long maxSize, boolean sizeRequired, long initSize) {
        return new Type1(names, maxSize, sizeRequired, initSize);
    }
    
    public static IDataType type(String name) {
        return new IntegralDataType(name + FOR_BIT_DATA);
    }
    
    private static class Type1 extends SizeDataType {
        private final String primaryName;
        
        public Type1(String[] names, long maxSize, boolean sizeRequired, long initSize) {
            super(convertNames(names), maxSize, sizeRequired, initSize);
            primaryName = names[0];
        }

        private Type1(
                String primaryName, String[] names, long maxSize, boolean sizeRequired, long initSize) {
            super(names, maxSize, sizeRequired, initSize);
            this.primaryName = primaryName;
        }

        private static String[] convertNames(String[] names) {
            String[] newNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                newNames[i] = names[i] + FOR_BIT_DATA;
            }
            return newNames;
        }

        public String getDDLName() {
            return primaryName + " (" + getSize() + ")" + FOR_BIT_DATA;
        }
        
        public IDataType concreteInstance() {
            return new Type1(primaryName, names, maxSize, requiresSize, size);
        }
    }
}
