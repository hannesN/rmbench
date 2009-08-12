/*
 * created 23.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $$Id: DTableStub.java 664 2007-09-28 17:28:39Z cse $$
 */
package com.byterefinery.rmbench.model.diagram;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;


/**
 * this class is a placeholder for tables that are connected to tables in the current diagram (i.e., 
 * they are referenced by or they reference such tables) but are not themselves part of the diagram. <BR/>
 * Each table in the diagram will have one associated stub, which may be empty.<br/>
 * The stub maintains 2 collections:
 * <ul>
 * <li>references contains external tables that have foreign keys referencing the associated table</li>
 * <li>foreignKeys contains tables that are referenced by a foreign key belonging to the associated table</li>
 * </ul>
 * 
 * @author Hannes Niederhausen
 *
 */
public class DTableStub extends AbstractDTable {

    public static final String PROPERTY_TABLESLIST = "tablelist";
    
    /** the associated dtable */
    private final DTable dTable;
     
    /** the DForeignKey connecting the stub with the dtable*/
    private final StubConnection stubConnection;

    /** the list of foreignKeyTables referenced by dTable */
    private final ArrayList<Table> foreignKeyTables = new ArrayList<Table>();
    
    /** the list of referenceTables referenced by dTable */
    private final ArrayList<Table> referenceTables = new ArrayList<Table>();
    
    /** the list of referenceTables referenced by dTable */
    private final ArrayList<Table> referenceAndForeignkeyTables = new ArrayList<Table>();
    
    public DTableStub(DTable dTable, Point location) {
        super(location);
        this.dTable = dTable;
        stubConnection = new StubConnection();
    }

    public DTable getDTable() {
        return dTable;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DTableStub)
            return dTable.equals(((DTableStub)obj).dTable);
        return false;
    }

    public int hashCode() {
        return dTable.hashCode();
    }

    public StubConnection getStubConnection() {
        return stubConnection;
    }

    
    public void fireTableListChanged() {
        propertySupport.firePropertyChange(PROPERTY_TABLESLIST, null, null);
    }

    /**
     * @return true if one of the table lists has at least one element<br/>
     *         false otherwise
     */
    public boolean isValid() {
        return referenceAndForeignkeyTables.size() > 0 || foreignKeyTables.size() > 0 || referenceTables.size() > 0;
    }
    
    
    /**
     * @return true if the given table is referenced by a foreign key
     */
    public boolean hasForeignKeyTable(Table table) {
        return (foreignKeyTables.indexOf(table) >= 0 || referenceAndForeignkeyTables.indexOf(table) >= 0);
    }
    
    /**
     * @return true if the given table is referenced and foreignkey
     */
    public boolean hasReferenceAndForeignKeyTable(Table table) {
        return referenceAndForeignkeyTables.indexOf(table) >= 0;
    }
    
    /**
     * @return true if the given table has a reference to the underlying table
     */
    public boolean hasReferenceTable(Table table) {
        return (referenceTables.indexOf(table) >= 0 || referenceAndForeignkeyTables.indexOf(table) >= 0);
    }
    
    /**
     * @param targetTable a table that is no longer referenced by a foreign key
     */
    public void removeForeignKeyTable(Table targetTable) {
        if(foreignKeyTables.remove(targetTable) == false)
            referenceAndForeignkeyTables.remove(targetTable);
    }

    /**
     * @param targetTable a table that is referenced by a foreign key
     */
    public void addForeignKeyTable(Table targetTable) {
        if (referenceTables.remove(targetTable))
            referenceAndForeignkeyTables.add(targetTable);
        else
            foreignKeyTables.add(targetTable);
    }

    /**
     * @return true if there are tables referenced by foreignKeys
     */
    public boolean hasForeignKeyTables() {
        return (foreignKeyTables.size() > 0 || referenceAndForeignkeyTables.size() > 0);
    }

    /**
     * @return an iterator over the tables referenced by foreignKeys
     */
    public Iterator<Table> foreignKeyTables() {
        return foreignKeyTables.iterator();
    }

    /**
     * @return an iterator over the referencing tables
     */
    public Iterator<Table> referenceTables() {
        return referenceTables.iterator();
    }

    /**
     * @return true if there are referencing tables
     */
    public boolean hasReferenceTables() {
        return (referenceTables.size() > 0 || referenceAndForeignkeyTables.size()>0);
    }

    /**
     * @param table a table that references the underlying table
     */
    public void addReferenceTables(Table table) {
        if(foreignKeyTables.remove(table))
            referenceAndForeignkeyTables.add(table);
        else
            referenceTables.add(table);
    }

    /**
     * @param table a table that no longer references the underlying table
     */
    public void removeReferenceTable(Table table) {
        if(referenceTables.remove(table) == false)
            referenceAndForeignkeyTables.remove(table);
    }
        
    public Iterator<Table> referenceAndForeignKeyTables() {
        return referenceAndForeignkeyTables.iterator();
    }
    
    /**
     * 
     * @return the number of stubbed tables, which means foreignkeys+references
     */
    public int size() {
        return foreignKeyTables.size()+referenceTables.size()+referenceAndForeignkeyTables.size();
    }
    
    /**
     * This class is the model of the stubConnection.
     * Actually only it's existence is needed 
     * @author Hannes Niederhausen
     *
     */
    public class StubConnection {
        public StubConnection() {
        }
    }

    /**
     * @param foreignKey
     * @return true if this stub represents a table that either owns or is referenced by 
     * the given foreign key
     */
	public boolean represents(ForeignKey foreignKey) {
		return 
			foreignKeyTables.contains(foreignKey.getTargetTable()) || 
			referenceTables.contains(foreignKey.getTable()) || 
			referenceAndForeignkeyTables.contains(foreignKey.getTable());
	}

	public void invalidate() {
		foreignKeyTables.clear();
		referenceTables.clear();
		referenceAndForeignkeyTables.clear();
	}
}
