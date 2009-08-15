/*
 * created 23.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DBPrimaryKey.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * representation of primary key metadata during database import
 * 
 * @author cse
 */
public class DBPrimaryKey {

    public final DBTable table;
    public String name;
    
    private final IPrimaryKey iprimaryKey = new IPrimaryKey() {

        public String getName() {
            return name;
        }

        public ITable getTable() {
            return table.getITable();
        }

        public String[] getColumnNames() {
            return DBPrimaryKey.this.getColumns();
        }
    };
    
    private final List<ColSequence> columns = new ArrayList<ColSequence>();
    
    private static class ColSequence implements Comparable<ColSequence> {
    	final int no;
    	final String name;
    	
    	ColSequence(int no, String name) {
    		this.no = no;
    		this.name = name;
    	}

		public int compareTo(ColSequence other) {
			return no - other.no;
		}
    }
    
    public DBPrimaryKey(DBTable table) {
        this.table = table;
    }

    public IPrimaryKey getIPrimaryKey() {
        return iprimaryKey;
    }
    
    public void addColumn(int sequenceNo, String columnName) {
        columns.add(new ColSequence(sequenceNo, columnName));
    }

    public String[] getColumns() {
    	Collections.sort(columns);
    	String[] result = new String[columns.size()];
    	for (int i=0; i<result.length; i++) {
			ColSequence element = (ColSequence) columns.get(i);
			result[i] = element.name;
		}
        return result;
    }

    public int getIndex(String name) {
        for (int i = 0; i < columns.size(); i++) {
            ColSequence seq = (ColSequence)columns.get(i);
            if(name.equals(seq.name))
                return seq.no;
        }
        return -1;
    }
}
