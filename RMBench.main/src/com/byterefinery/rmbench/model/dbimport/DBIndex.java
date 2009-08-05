/*
 * created 08.08.2005 by sell
 *
 * $Id: DBIndex.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.model.dbimport;

import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * representation of index metadata imported from the database
 * @author sell
 */
public class DBIndex {

    public static class IndexColumn {
        public final String name;
        public final boolean ascending;
        
        private IndexColumn(String name, boolean ascending) {
            this.name = name;
            this.ascending = ascending;
        }
    }

    private final IIndex iindex = new IIndex() {

        public String getName() {
            return name;
        }

        public ITable getTable() {
            return table.getITable();
        }
        
        public IColumn[] getColumns() {
            throw new UnsupportedOperationException();
        }

        public boolean isUnique() {
            return unique;
        }

		public boolean isAscending(IColumn column) {
			return false;
		}
    };
    
    public final DBTable table;
    public final String name;
    public final boolean unique;
    
    private final List<IndexColumn> columns = new ArrayList<IndexColumn>();
    
    public DBIndex(DBTable table, String name, boolean unique) {
        this.table = table;
        this.name = name;
        this.unique = unique;
    }

    public IIndex getIIndex() {
        return iindex;
    }
    
    public IndexColumn[] getColumns() {
        return (IndexColumn[])columns.toArray(new IndexColumn[columns.size()]);
    }
    
    public String[] getColumnNames() {
        String[] names = new String[columns.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = ((IndexColumn)columns.get(i)).name;
        }
        return names;
    }
    
    /**
     * @return an array with boolean values designating the sort order for the column at
     * the given index
     */
    public boolean[] getAscending() {
        boolean[] ascending = new boolean[columns.size()];
        for (int i = 0; i < ascending.length; i++) {
            ascending[i] = ((IndexColumn)columns.get(i)).ascending;
        }
        return ascending;
    }
    
    public void addColumn(String colName, boolean ascDesc) {
        columns.add(new IndexColumn(colName, ascDesc));
    }
}
