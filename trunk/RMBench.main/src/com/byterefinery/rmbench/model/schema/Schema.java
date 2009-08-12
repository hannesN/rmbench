/*
 * created 13.03.2005
 * 
 * $Id:Schema.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.model.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * model class that represents the database schema
 *  
 * @author cse
 */
public class Schema {
    
    private final ISchema ischema = new ISchema() {

        public String getName() {
            return name;
        }

        public ITable[] getTables() {
            ITable[] itables = new ITable[tables.size()];
            for (int i = 0; i < itables.length; i++) {
                itables[i] = ((Table)tables.get(i)).getITable();
            }
            return itables;
        }

        public ITable getTable(String name) {
            Table table = Schema.this.getTable(name);
            return table != null ? table.getITable() : null;
        }

        public int getTableCount() {
            return tables.size();
        }
    };
    
	private String catalogName;
    private String name;
    private final List<Table> tables = new ArrayList<Table>();
    private IDatabaseInfo database;

    public Schema(String catalogName, String name, IDatabaseInfo database) {
		this.catalogName = catalogName;
        this.name = name;
        this.database = database;
    }
    
    public Schema(String name, IDatabaseInfo database) {
		this.catalogName = null;
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

    public void setName(String name) {
        this.name=name;
    }
    
    public boolean isEmpty() {
        return tables.isEmpty();
    }
    
    public List<Table> getTables() {
        return tables;
    }

    public List<View> getViews() {
        return Collections.emptyList(); //TODO V2: views
    }
    
    public List<Sequence> getSequences() {
        return Collections.emptyList();  //TODO V2: sequences
    }
    
    void addTable(Table table) {
        tables.add(table);
    }

    void removeTable(Table table) {
        if(!tables.remove(table))
			throw new IllegalArgumentException();
    }

    /**
     * @return the table with the given name, or <code>null</code>
     */
    public Table getTable(String tableName) {
        for (Iterator<Table> it = tables.iterator(); it.hasNext();) {
            Table table = it.next();
            if(table.getName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }
    
    public IDatabaseInfo getDatabaseInfo() {
        return database;
    }

    public void setDatabaseInfo(IDatabaseInfo databaseInfo) {
        this.database = databaseInfo;
    }
    
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }
}
