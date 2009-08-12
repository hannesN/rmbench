/*
 * created 23.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DBColumn.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IColumn2;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * imported column metadata
 * 
 * @author cse
 */
public class DBColumn {

    private final IColumn2 icolumn = new IColumn2() {

        public String getName() {
            return name;
        }

        public ITable getTable() {
            return table.getITable();
        }

        public IDataType getDataType() {
            return dataType;
        }

        public String getDefault() {
            return defaultValue;
        }

        public boolean getNullable() {
            return nullable;
        }

        public String getComment() {
            return comment;
        }

        public void setDefault(String value) {
            defaultValue = value;
        }

        public void setComment(String value) {
            comment = value;
        }

		public long getSize() {
			return precision;
		}

		public int getScale() {
			return scale;
		}

		public boolean belongsToPrimaryKey() {
			DBPrimaryKey pk = table.getPrimaryKey();
			for (int i = 0; i < pk.getColumns().length; i++) {
				if(name.equals(pk.getColumns()[i]))
					return true;
			}
			return false;
		}
    };
    
    public final DBTable table;
    public final String name;
    public final IDataType dataType;
    public final String typeName;
    public final String typeID;
    public final long precision;
    public final int scale;
    public final boolean nullable;
    public String defaultValue;
    public String comment;

    public DBColumn(
            DBTable table, 
            String name, 
            IDataType dataType,
            String typeName,
            String typeID,
            long precision,
            int scale,
            boolean nullable, 
            String defaultValue, 
            String comment) {

        this.table = table;
        this.name = name;
        this.dataType = dataType;
        this.typeName = typeName;
        this.typeID = typeID;
        this.precision = precision;
        this.scale = scale;
        this.nullable = nullable;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    /**
     * convenience method
     * @return the database info associated with the owning schema
     */
    public IDatabaseInfo getDatabaseInfo() {
        return table.getSchema().getDatabase();
    }
    
    public IColumn getIColumn() {
        return icolumn;
    }

    public IColumn2 getIColumn2() {
        return icolumn;
    }
}
