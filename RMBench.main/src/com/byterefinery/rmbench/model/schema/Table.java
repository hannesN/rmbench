/*
 * created 12.03.2005
 * 
 * $Id: Table.java 650 2007-08-30 16:31:11Z cse $
 */
package com.byterefinery.rmbench.model.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * @author cse
 */
public class Table {

    private final ITable itable = new ITable() {

        public String getName() {
            return Table.this.getName();
        }

        public ISchema getSchema() {
            return Table.this.getSchema().getISchema();
        }

        public IColumn[] getColumns() {
            IColumn[] cols = new IColumn[columns.size()];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = ((Column)columns.get(i)).getIColumn();
            }
            return cols;
        }
        
        public IPrimaryKey getPrimaryKey() {
            return primaryKey != null ? primaryKey.getIPrimaryKey() : null;
        }

        public IForeignKey[] getForeignKeys() {
            IForeignKey[] fks = new IForeignKey[foreignKeys.size()];
            for (int i = 0; i < fks.length; i++) {
                fks[i] = ((ForeignKey)foreignKeys.get(i)).getIForeignKey();
            }
            return fks;
        }

        public IColumn getColumn(String name) {
            Column column = Table.this.getColumn(name);
            return column != null ? column.getIColumn() : null;
        }

        public IUniqueConstraint[] getUniqueConstraints() {
            return (IUniqueConstraint[])Table.this.getUniqueConstraints()
                .toArray(new IUniqueConstraint[Table.this.getUniqueConstraints().size()]);
        }

        public ICheckConstraint[] getCheckConstraints() {
            return (ICheckConstraint[])Table.this.getCheckConstraints()
                .toArray(new ICheckConstraint[Table.this.getCheckConstraints().size()]);
        }
    };
    
    private Schema schema;
    private String name;
    private String comment;
    private String description;
    private String type;
    
    private List<Column> columns = new ArrayList<Column>();
    private PrimaryKey primaryKey;
    
    private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
    private List<ForeignKey> references = new ArrayList<ForeignKey>();
    private List<Index> indexes = new ArrayList<Index>(2);
    private List<UniqueConstraint> uniqueConstraints = new ArrayList<UniqueConstraint>(2);
    private List<CheckConstraint> checkConstraints = new ArrayList<CheckConstraint>(2);

    
    public Table(Schema schema, String name) {
        this.schema = schema;
        this.name = name;
        schema.addTable(this);
    }
    
    public ITable getITable() {
        return itable;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return schema.getName() + "." + getName();
    }
    
    public String getComment() {
        return comment;
    }

    public String getDescription() {
        return description;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String typeID) {
        this.type = typeID;
    }
    
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKey key) {
        this.primaryKey = key;
    }

    public Schema getSchema() {
        return schema;
    }
    
    public void setSchema(Schema schema) {
        if(this.schema != null) {
            this.schema.removeTable(this);
        }
        schema.addTable(this);
        this.schema = schema;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public Column getColumn(int index) {
        return (Column)columns.get(index);
    }

	/**
	 * @return the column with the given name, or <code>null</code> if such 
	 * a column does not exist in this table
	 */
	public Column getColumn(String name) {
		for (Iterator<Column> it = columns.iterator(); it.hasNext();) {
			Column column = it.next();
			if(column.getName().equals(name))
				return column;
		}
		return null;
	}
	
    public void addColumn(Column column) {
        columns.add(column);
    }

    public void addColumn(Column column, int index) {
        if (index > columns.size())
            columns.add(column);
        else
            columns.add(index, column);
    }
    
    public void moveColumn(Column column, int index) {
        columns.remove(column);
        columns.add(index, column);
    }
    
    public void removeColumn(Column column) {
        columns.remove(column);
    }

    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    void addForeignKey(ForeignKey key) {
        foreignKeys.add(key);
    }

    boolean removeForeignKey(ForeignKey key) {
        return foreignKeys.remove(key);
    }
    
    public List<ForeignKey> getReferences() {
        return references;
    }

    void addReference(ForeignKey key) {
        references.add(key);
    }

    boolean removeReference(ForeignKey key) {
        return references.remove(key);
    }

    public void addIndex(Index index) {
        indexes.add(index);
    }
    
    public List<Index> getIndexes() {
        return indexes;
    }

    public void removeIndex(Index index) {
        indexes.remove(index);
    }
    
    /**
     * @return the UNIQUE constraints on this table, excluding the primary key
     */
    public List<UniqueConstraint> getUniqueConstraints() {
        return uniqueConstraints;
    }
    
    /**
     * @param constraint a UNIQUE constraint
     */
    public void addUniqueConstraint(UniqueConstraint constraint) {
        uniqueConstraints.add(constraint);
    }

    /**
     * @param constraint the UNIQUE constraint to remove. Does nothing if the constraint is 
     * not owned by this table
     */
    public void removeUniqueConstraint(UniqueConstraint constraint) {
        uniqueConstraints.remove(constraint);
    }
    
    /**
     * @return the CHECK constraints on this table, excluding the primary key
     */
    public List<CheckConstraint> getCheckConstraints() {
        return checkConstraints;
    }
    
    /**
     * @param constraint a CHECK constraint
     */
    public void addCheckConstraint(CheckConstraint constraint) {
        checkConstraints.add(constraint);
    }

    /**
     * @param constraint the CHECK constraint to remove. Does nothing if the constraint is 
     * not owned by this table
     */
    public void removeCheckConstraint(CheckConstraint constraint) {
        checkConstraints.remove(constraint);
    }
    
    /**
     * remove this object from the owning schema
     */
    public void abandon() {
        schema.removeTable(this);
    }

    /**
     * add this object back to the formerly owning schema
     */
    public void restore() {
        schema.addTable(this);
    }

    /**
     * convenience method, see {@link Schema#getDatabaseInfo()}
     * 
     * @return the database info
     */
    public IDatabaseInfo getDatabaseInfo() {
        return getSchema().getDatabaseInfo();
    }

    /**
     * @param constraintName a constraint name
     * @return true if this object has any constraint (primary key, foreign key or other constraint)
     * with the given name
     */
    public boolean hasConstraintName(String constraintName) {
        
        if(getPrimaryKey() != null && constraintName.equals(getPrimaryKey().getName()))
            return true;
        for (Iterator<ForeignKey> it = getForeignKeys().iterator(); it.hasNext();) {
            ForeignKey fk = it.next();
            if(constraintName.equals(fk.getName()))
                return true;
        }
        for (Iterator<UniqueConstraint> it = getUniqueConstraints().iterator(); it.hasNext();) {
            Constraint constraint = it.next();
            if(constraintName.equals(constraint.getName()))
                return true;
        }
        for (Iterator<CheckConstraint> it = getCheckConstraints().iterator(); it.hasNext();) {
            Constraint constraint = it.next();
            if(constraintName.equals(constraint.getName()))
                return true;
        }
        return false;
    }

    /**
     * check if all columns named in the parameter are available in this table
     * @param columnNames
     * @return the name of the first missing column, or null if all available
     */
	public String checkColumns(String[] columnNames) {
		
		for (String colName : columnNames) {
			boolean found = false;
			for (Column column : columns) {
				if(column.getName().equalsIgnoreCase(colName)) {
					found = true;
					continue;
				}
			}
			if(!found) {
				return colName;
			}
		}
		return null;
	}
}
