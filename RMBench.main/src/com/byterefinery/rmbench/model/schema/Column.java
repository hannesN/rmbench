/*
 * created 13.03.2005
 * 
 * $Id: Column.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.model.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * @author cse
 */
public class Column {
    
    private final IColumn icolumn = new IColumn() {

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

		public long getSize() {
			return dataType.getSize();
		}

		public int getScale() {
			return dataType.getScale();
		}

		public boolean belongsToPrimaryKey() {
			return Column.this.belongsToPrimaryKey();
		}
    };
    
    private String name;
    private String defaultValue;
    private String comment;
    private boolean nullable;
    
    private IDataType dataType;
    private final Table table;
    
    /**tableIndex of column in the table*/
    private int tableIndex;

    public Column(Table table, String name, IDataType dataType) {
		this(table, name, dataType, true, null, null);
    }

    public Column(
			Table table, 
			String name, 
			IDataType dataType, 
			boolean nullable, 
			String defaultValue, 
			String comment) {
    	this(table, name, dataType, nullable, defaultValue, comment, RMBenchConstants.UNSPECIFIED_POSITION);
    	
    }
    public Column(
			Table table, 
			String name, 
			IDataType dataType, 
			boolean nullable, 
			String defaultValue, 
			String comment,
			int position) {
		
        assert(name != null);
        this.table = table;
        this.name = name;
        this.dataType = dataType;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
		this.comment = comment;
		this.tableIndex = position;
		if(position == RMBenchConstants.UNSPECIFIED_POSITION)
			table.addColumn(this);
		else
			table.addColumn(this, position);
	}

    public IColumn getIColumn() {
        return icolumn;
    }
    
	public String getName() {
        return name;
    }
    
    public void setName(String name) {
        assert(name != null);
        this.name = name;
    }

    public IDataType getDataType() {
        return dataType;
    }
    
    public void setDataType(IDataType type) {
        this.dataType = type;
    }

    /**
     * @return the size/precision of this column, if applicable
     */
    public long getSize() {
        return dataType.getSize();
    }

    /**
     * @return the scale of this column, if applicable
     */
    public int getScale() {
        return dataType.getScale();
    }

    /**
     * @return the default value, as a SQL expression
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * @return <code>true</code> if the column accepts null values 
     */
    public boolean getNullable() {
        return nullable;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    

    public void setDefault(String defaultVal) {
        this.defaultValue = defaultVal;
    }
    

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    

    public void setScale(int scale) {
        this.dataType.setScale(scale);
    }
    

    public void setSize(long size) {
        this.dataType.setSize(size);
    }

    public void saveIndex() {
        tableIndex = table.getColumns().indexOf(this); 
    }
    
    /**
     * @return the comment defined on this column, if any
     */
    public String getComment() {
        return comment;
    }
    
    public Table getTable() {
        return table;
    }

    /**
     * @return whether this column is part of the primary key
     */
    public boolean belongsToPrimaryKey() {
        Key pk = getTable().getPrimaryKey();
        return pk != null && pk.contains(this);
    }

    /**
     * @return whether this column is part of a foreign key
     */
    public boolean belongsToForeignKey() {
        for(Iterator<ForeignKey> it=getTable().getForeignKeys().iterator(); it.hasNext();) {
            if(it.next().contains(this))
                return true;
        }
        return false;
    }
    
    /**
     * @return the foreign keys this column participates in
     */
    public List<ForeignKey> getForeignKeys() {
    	List<ForeignKey> result = new ArrayList<ForeignKey>(getTable().getForeignKeys().size());
        for(Iterator<ForeignKey> it=getTable().getForeignKeys().iterator(); it.hasNext();) {
            ForeignKey fk = it.next();
            if(fk.contains(this))
                result.add(fk);
        }
        return result;
    }
    
    /**
     * @return the unique constraints this column participates in
     */
    public List<UniqueConstraint> getUniqueConstraints() {
    	List<UniqueConstraint> result = new ArrayList<UniqueConstraint>(getTable().getForeignKeys().size());
        for(Iterator<UniqueConstraint> it=getTable().getUniqueConstraints().iterator(); it.hasNext();) {
            UniqueConstraint next = it.next();
            if(next.contains(this))
                result.add(next);
        }
        return result;
    }
    
    /**
     * add this column to the primary key of the owning table, assuming it
     * is not already part of the former
     */
    public void addToPrimaryKey() {
        table.getPrimaryKey().addColumn(this);
    }

    /**
     * remove this column from the primary key of the owning table, assuming it
     * is already part of the former
     */
    public void removeFromPrimaryKey() {
        table.getPrimaryKey().removeColumn(this);
    }

    /**
     * remove the column from the owning table
     */
    public void abandon() {
        if (tableIndex == RMBenchConstants.UNSPECIFIED_POSITION)
            saveIndex();
        table.removeColumn(this);
    }

    /**
     * re-add the column to the owning table at the position it 
     * had when it was abandoned
     */
    public void restore() {
        table.addColumn(this, tableIndex);
    }
}
