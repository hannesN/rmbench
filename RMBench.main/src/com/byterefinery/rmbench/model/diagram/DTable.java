/*
 * created 04.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DTable.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.model.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a wrapper of a schema table in the context of a diagram, that maintains
 * display relevant data
 * 
 * @author cse
 */
public class DTable extends AbstractDTable {

    public static final String PROPERTY_COLLAPSED = "collapsed";
    
    private static final List<Column> NO_COLUMNS = Collections.emptyList();
    
    /** whether table columns are shown */
    private boolean collapsed;
    
    /** the table represented by this object */
    private final Table table;
    
    /** the owning diagram **/
    private Diagram diagram;
    
    /** the list of DForeignKeys, which have as source this table */
    private ArrayList<DForeignKey> dForeignKeys;
    
    /** the list of DForeignKeys, which have as target this table */
    private ArrayList<DForeignKey> dReferences;
    
    /** the table stub of referring tables missing in the diagram*/
    private final DTableStub tableStub;
    
    public DTable(Table table, Point location) {
        super(location);
        this.table = table;
        this.dForeignKeys = new ArrayList<DForeignKey>();
        this.dReferences = new ArrayList<DForeignKey>();
        this.tableStub = new DTableStub(this, new Point(location.x-50, location.y));
    }
    
    public Table getTable() {
        return table;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        boolean oldCollapsed = this.collapsed;
        this.collapsed = collapsed;
        propertySupport.firePropertyChange(PROPERTY_COLLAPSED, oldCollapsed, collapsed);
    }
    
	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}
    
    /**
     * updates the renderable foreignkeys depending on the tables in the diagram.<br />
     * This should be called, when a table is added to or removed from the diagram
     */
    public void updateForeignKeys() {
        List<ForeignKey> foreignKeys = table.getForeignKeys();
        if(foreignKeys.size() == 0) {
            dForeignKeys.clear();
            return;
        } 
        
        DForeignKey dForeignKey;
        int index;
        ArrayList<DForeignKey> result;
            
        result = new ArrayList<DForeignKey>(foreignKeys.size());
        
        for (Iterator<ForeignKey> it = foreignKeys.iterator(); it.hasNext();) {
            ForeignKey foreignKey = it.next();
            if(diagram.getDTable(foreignKey.getTargetTable()) != null) {
                dForeignKey = new DForeignKey(foreignKey);
                if ((index=dForeignKeys.indexOf(dForeignKey))>-1) {
                    dForeignKey = dForeignKeys.get(index);
                }
                result.add(dForeignKey);
                // check if table is in the stub, if yes remove it
                if (tableStub.hasForeignKeyTable(foreignKey.getTargetTable())) {
                    tableStub.removeForeignKeyTable(foreignKey.getTargetTable());
                }
            } else {
                if (!tableStub.hasForeignKeyTable(foreignKey.getTargetTable())) {
                    tableStub.addForeignKeyTable(foreignKey.getTargetTable());
                }

            }
        }         
        dForeignKeys = result;
    }
    
    public void updateReferences() {
        List<ForeignKey> references = table.getReferences();
        if(references.size() == 0) {
            dReferences.clear();
            return;
        }
        DForeignKey dForeignKey;
        int index;
        ArrayList<DForeignKey> result = new ArrayList<DForeignKey>(references.size());
        for (Iterator<ForeignKey> it = references.iterator(); it.hasNext();) {
            ForeignKey foreignKey = it.next();
            DTable targetTable = diagram.getDTable(foreignKey.getTable());
            if(targetTable != null) {
                dForeignKey = targetTable.getDForeignKey(foreignKey);
                if ((index=dForeignKeys.indexOf(dForeignKey))>-1) {
                    dForeignKey = dForeignKeys.get(index);
                }
                result.add(dForeignKey);
                // check if table is in the stub, if yes remove it
                if (tableStub.hasReferenceTable(foreignKey.getTable())) {
                    tableStub.removeReferenceTable(foreignKey.getTable());
                }
                
            } else {
                if (!tableStub.hasReferenceTable(foreignKey.getTable())) {
                    tableStub.addReferenceTables(foreignKey.getTable());
                }
            }
        }
        dReferences = result;
    }
    

	/**
	 * @return only those foreign keys that reference tables that are also contained in the
	 * owning diagram 
	 */
	public List<DForeignKey> getForeignKeys() {
		return Collections.unmodifiableList(dForeignKeys);
	}

	/**
	 * @return only those references that originate from tables that are also contained in the
	 * owning diagram 
	 */
	public List<DForeignKey> getReferences() {
		return Collections.unmodifiableList(dReferences);
	}
    
    public DForeignKey getDForeignKey(ForeignKey key) {
        for (Iterator<DForeignKey> it=dForeignKeys.iterator(); it.hasNext();) {
            DForeignKey dForeignKey = it.next();
            if (dForeignKey.getForeignKey().equals(key))
                return dForeignKey;
        }
        return null;
    }

	/**
	 * @return the currently visible columns, i.e. none if this object is in the collapsed
	 * state, all table columns otherwise
	 */
	public List<Column> getColumns() {
		return collapsed ? NO_COLUMNS : getTable().getColumns();
	}
    
    
    public Diagram getDiagram() {
        return diagram;
    }
    
    public void addDForeignKey(DForeignKey dForeignKey) {
        if(dForeignKey.getForeignKey().getTable() != getTable())
            throw new IllegalArgumentException();
        dForeignKeys.add(dForeignKey);
    }
    
    /**
     * A DTable equals another it the two wrapped tables are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof DTable))
              return false;
        return table.equals(((DTable)obj).getTable());
    }
    
    public int hashCode() {
        return table.hashCode();
    }
    
    public DTableStub getTableStub() {
        return tableStub;
    }
}
