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
 * $Id: DBForeignKey.java 667 2007-10-02 18:54:16Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * represents an imported foreign key
 * 
 * @author cse
 */
public class DBForeignKey {

    private final IForeignKey iforeignKey = new IForeignKey() {

        private DBTable targetTable;
        
        public ITable getTable() {
            return table.getITable();
        }

        public String getName() {
            return name;
        }

        public String[] getColumnNames() {
            return DBForeignKey.this.getColumns();
        }

        public Action getDeleteAction() {
            return deleteRule;
        }

        public Action getUpdateAction() {
            return updateRule;
        }

        public ITable getTargetTable() {
            if(targetTable == null) {
                DBSchema schema = new DBSchema(targetCatalog, targetSchema, table.getSchema().getDatabase());
                targetTable = new DBTable(schema, name, null);
            }
            return targetTable.getITable();
        }
    };
    
    public final DBTable table;
    public final String name;
    public final IForeignKey.Action updateRule;
    public final IForeignKey.Action deleteRule;
    public final int deferrable;
    public final String targetCatalog; 
    public final String targetSchema;
    public final String targetTable;
    
    private final List<String> columns = new ArrayList<String>();
    
    public DBForeignKey(
            DBTable table,
            String fkName, 
            int updateRule, 
            int deleteRule, 
            int deferrable, 
            String targetCatalog,
            String targetSchema, 
            String targetTable) {

        this.table = table;
        this.name = fkName; 
        this.updateRule = translateAction(updateRule);
        this.deleteRule = translateAction(deleteRule);
        this.deferrable = deferrable;
        this.targetCatalog = targetCatalog;
        this.targetSchema = targetSchema;
        this.targetTable = targetTable;
    }

    public IForeignKey getIForeignKey() {
        return iforeignKey;
    }
    
    public void addColumn(String column) {
        columns.add(column);
    }
    
    public String[] getColumns() {
        return (String[])columns.toArray(new String[columns.size()]);
    }

	public String  getColumn(int index) {
		return columns.get(index);
	}
    
	public int size() {
		return columns.size();
	}
	
    private IForeignKey.Action translateAction(int rule) {
        IForeignKey.Action  action = null;
        switch(rule) {
        case DatabaseMetaData.importedKeyNoAction:
            action = IForeignKey.NO_ACTION;
            break;
        case DatabaseMetaData.importedKeyRestrict:
            action = IForeignKey.RESTRICT;
            break;
        case DatabaseMetaData.importedKeyCascade:
            action = IForeignKey.CASCADE;
            break;
        case DatabaseMetaData.importedKeySetDefault:
            action = IForeignKey.SET_DEFAULT;
            break;
        case DatabaseMetaData.importedKeySetNull:
            action = IForeignKey.SET_NULL;
            break;
        }
        return action;
    }
}
