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
 * $Id: DBSchema.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * @author cse
 */
public class DBSchema {

    private final ISchema ischema = new ISchema() {

        public String getName() {
            return name;
        }

        public ITable[] getTables() {
            ITable[] itables = new ITable[tables.size()];
            for (int i = 0; i < itables.length; i++) {
                itables[i] = ((DBTable)tables.get(i)).getITable();
            }
            return itables;
        }

        public ITable getTable(String name) {
        	for (DBTable table : tables) {
                if(table.getName().equals(name))
                    return table.getITable();
			}
            return null;
        }

        public int getTableCount() {
            return tables.size();
        }
    };
    
    private final String catalogName;
    private final String name;
    private final IDatabaseInfo database;
    
    private final List<DBTable> tables = new ArrayList<DBTable>();
    private final List<DBTable> views = new ArrayList<DBTable>();
    private final List<DBTable> sequences = new ArrayList<DBTable>();

    public DBSchema(String catalogName, String name, IDatabaseInfo database) {
        this.catalogName = catalogName;
        this.name = name;
        this.database = database;
    }
    
    public ISchema getISchema() {
        return ischema;
    }
    
    public String getCatalogName() {
        return catalogName;
    }

    public String getName() {
        return name;
    }
    public IDatabaseInfo getDatabase() {
        return database;
    }
    
    public void addTable(DBTable dbtable) {
        tables.add(dbtable);
    }

    public void addView(DBTable dbtable) {
        views.add(dbtable);
    }
    
    public void addSeqence(DBTable dbtable) {
        sequences.add(dbtable);
    }
    
    public List<DBTable> getTables() {
        return tables;
    }

    public List<DBTable> getViews() {
        return views;
    }

    public List<DBTable> getSequences() {
        return sequences;
    }
}
