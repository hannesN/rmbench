/*
 * created 23.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DBTable.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * imported table metadata
 * 
 * @author cse
 */
public class DBTable {

    private final ITable itable = new ITable() {

        public String getName() {
            return name;
        }

        public ISchema getSchema() {
            return schema.getISchema();
        }

        public IColumn[] getColumns() {
            IColumn[] cols = new IColumn[columns.size()];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = ((DBColumn)columns.get(i)).getIColumn();
            }
            return cols;
        }

        public IPrimaryKey getPrimaryKey() {
            return primaryKey != null ? primaryKey.getIPrimaryKey() : null;
        }

        public IForeignKey[] getForeignKeys() {
            IForeignKey[] fks = new IForeignKey[foreignKeys.size()];
            for (int i = 0; i < fks.length; i++) {
                fks[i] = ((DBForeignKey)foreignKeys.get(i)).getIForeignKey();
            }
            return fks;
        }

        public IColumn getColumn(String name) {
        	for (DBColumn column : columns) {
                if(column.name.equals(name))
                    return column.getIColumn();
			}
            return null;
        }

        public IUniqueConstraint[] getUniqueConstraints() {
            return new IUniqueConstraint[0];
        }

        public ICheckConstraint[] getCheckConstraints() {
            return new ICheckConstraint[0];
        }
    };
    
    private final DBSchema schema;
    private final String name;
    private final String comment;
    
    private DBPrimaryKey primaryKey;
    private final List<DBColumn> columns = new ArrayList<DBColumn>();
    private final List<DBForeignKey> foreignKeys = new ArrayList<DBForeignKey>();
    private final List<DBIndex> indexes = new ArrayList<DBIndex>();
    
    public DBTable(DBSchema schema, String name, String comment) {
        this.schema = schema;
        this.name = name;
        this.comment = comment;
    }

    public ITable getITable() {
        return itable;
    }
    
    public String getCatalogName() {
        return schema != null ? schema.getCatalogName() : null;
    }

    public String getSchemaName() {
        return schema != null ? schema.getName() : null;
    }

    public DBSchema getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void addColumn(DBColumn column) {
        columns.add(column);
    }

    public void setPrimaryKey(DBPrimaryKey primaryKey) {
        this.primaryKey = primaryKey; 
    }

    public void addForeignKey(DBForeignKey key) {
        foreignKeys.add(key);
    }

    public void addIndex(DBIndex dbindex) {
        indexes.add(dbindex);
    }
    
    public List<DBColumn> getColumns() {
        return columns;
    }

    public List<DBForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public DBPrimaryKey getPrimaryKey() {
        return primaryKey;
    }
    
    public List<DBIndex> getIndexes() {
        return indexes;
    }
}
