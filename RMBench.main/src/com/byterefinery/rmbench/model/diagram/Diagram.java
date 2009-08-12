/*
 * created 29.04.2005
 * 
 * $Id:Diagram.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.model.diagram;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.byterefinery.rmbench.external.model.IDiagram;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a model representation of a diagram
 * 
 * @author cse
 */
public class Diagram {

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_TABLE = "table";
    public static final String PROPERTY_SCHEMA = "schema";
    
    public final class IDiagramImpl implements IDiagram {
    	
		public ITable[] getTables() {
			ITable[] itables = new ITable[dtables.size()];
			Iterator<Table> tablesIt = dtables.keySet().iterator();
			for (int i = 0; i < itables.length; i++) {
				itables[i] = tablesIt.next().getITable();
			}
			return itables;
		}
		public String getName() {
			return name;
		}
		public Diagram getDiagram() {
			return Diagram.this;
		}
    }
    private final IDiagramImpl idiagram = new IDiagramImpl();

    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    private final Model model;
    private final Map<Table, DTable> dtables = new LinkedHashMap<Table, DTable>();
    private List<DTableStub> tableStubs; 
    private String name;
    private Schema defaultSchema;
    
    public Diagram(Model model, String name, Schema defaultSchema) {
        this.name = name;
        this.tableStubs = new ArrayList<DTableStub>();
        this.model = model;
        this.model.addDiagram(this);
        setDefaultSchema(defaultSchema);
    }
    
    public String getName() {
        return name;
    }
    
    public Model getModel() {
        return model;
    }
    
    public Collection<DTable> getDTables() {
        return dtables.values();
    }
    
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        updateTables();
        propertySupport.firePropertyChange(PROPERTY_NAME, oldName, name);
    }

    public void addTable(DTable dtable) {
    	dtable.setDiagram(this);
        dtables.put(dtable.getTable(), dtable);
        updateTables();
        propertySupport.firePropertyChange(PROPERTY_TABLE, null, dtable);
    }

    public void removeTable(DTable dtable) {
        dtables.remove(dtable.getTable());
        updateTables();
        propertySupport.firePropertyChange(PROPERTY_TABLE, dtable, null);
        
    }

    public void removeTable(Table table) {
        DTable dtable = getDTable(table);
        removeTable(dtable);
    }
    
    /**
     * @param table a model table
     * @return the diagram table, or <code>null</code> if the table is not displayed in this diagram
     */
    public DTable getDTable(Table table) {
        return dtables.get(table);
    }
    
    /**
     * Add the new DTables from the the given collection to this diagram. If a table is already 
     * found in this diagram, it will be removed from the collection<p>
     * <em>Note: no events are triggered</em>
     * @param dtables
     */
	public void addNewTables(Collection<DTable> dtables) {
		for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
			DTable dtable = it.next();
			if (!this.dtables.containsKey(dtable.getTable())) {
                dtable.setDiagram(this);
    			this.dtables.put(dtable.getTable(), dtable);
            } else //(old comment) is this really good?
                it.remove();
		}
        updateTables();
	}
	
    /**
     * @param schema the schema in which new tables will be initially created
     */
    public void setDefaultSchema(Schema schema) {
        Schema oldDefault = defaultSchema;
        defaultSchema = schema;
        propertySupport.firePropertyChange(PROPERTY_SCHEMA, oldDefault, defaultSchema);
    }

    /**
     * @return the schema in which new tables will be initially created
     */
    public Schema getDefaultSchema() {
        return defaultSchema;
    }

    public void addPropertyListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * @return <code>true</code> if the given table is contained in this diagram
     */
    public boolean containsTable(Table selectedTable) {
        return dtables.containsKey(selectedTable);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Diagram) {
            Diagram d2 = (Diagram)obj;
            return model.equals(d2.model) && name.equals(d2.name);
        }
        return false;
    }

    public int hashCode() {
        return model.getName().hashCode() ^ name.hashCode();
    }
    
    public void updateTables() {
        
        for (Iterator<DTable> it=dtables.values().iterator(); it.hasNext();) {
        	DTable table = it.next();
            table.updateForeignKeys();
        }
        
        //has to be after the foreignkey update, cause references depend on them
        for (Iterator<DTable> it=dtables.values().iterator(); it.hasNext();) {
        	DTable table = it.next();
            table.updateReferences();
            
            
            DTableStub tableStub = table.getTableStub();
            tableStub.fireTableListChanged();

            if (tableStubs.indexOf(tableStub)>-1) {
                if (!tableStub.isValid())
                    tableStubs.remove(tableStub);
            } else {
                if (tableStub.isValid())
                    tableStubs.add(tableStub);
            }
        }
        
        // now we clean all tableStubs from diagram, owned by a dtable removed from the
        // diagram
        for (Iterator<DTableStub> it=tableStubs.iterator(); it.hasNext();) {
        	DTableStub tableStub = it.next();
            if (getDTable(tableStub.getDTable().getTable())==null)
                it.remove();
        }
    }
    
    public DTableStub getTableStub(DTable dTable) {
        for (Iterator<DTableStub> it=tableStubs.iterator(); it.hasNext();) {
        	DTableStub tableStub = it.next();
            if (tableStub.getDTable().equals(dTable))
                return tableStub;
        }
        return null;
    }

    /**
     * @param foreignKey
     * @return a table stub that represents a table that owns or is referenced by the given foreign key
     */
	public DTableStub getTableStub(ForeignKey foreignKey) {
        for (Iterator<DTableStub> it=tableStubs.iterator(); it.hasNext();) {
        	DTableStub tableStub = it.next();
            if (tableStub.represents(foreignKey))
                return tableStub;
        }
        return null;
	}
	
	public IDiagram getIDiagram() {
		return idiagram;
	}

    public List<DTableStub> getTableStubs() {
        return tableStubs;
    }
    
    /**
     * Reports property changes to the registered PropertyChangeListener. It is asumed that only changes of this diagrams 
     * properties are reported in this way. <br>
     * The following property types are supported:
     * <ul>
     *      <li>Diagram.PROPERTY_NAME</li>
     *      <li>Diagram.PROPERTY_SCHEMA</li>
     *      <li>Diagram.PROPERTY_TABLE</li>      
     * </ul>
     * The registered listener will receive a java.beans.PropertyChangeEvent created with this constructor call: <br><br>
     * <code>new PropertyChangeEvent(this, property, null, null).</code><br><br>
     * @param property The property which will be reported as changed.
     */
    public void fireDiagrammPropertyChange(String property){
        propertySupport.firePropertyChange(new PropertyChangeEvent(this, property, null, null));
    }
}
