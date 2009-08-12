/*
 * created 08.04.2005
 * 
 * $Id:EventManager.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a manager for events that are communicated within the plugin context
 * 
 * @author cse
 */
public class EventManager {

    public interface Types {
        int COLUMN_ADDED = 1;
        int COLUMN_MODIFIED = 1<<1;
        int COLUMNS_MODIFIED = 1<<2;
        int COLUMN_DELETED = 1<<3;
        int TABLE_SELECTED = 1<<4;
        int COLUMN_SELECTED = 1<<5;
        int COLUMN_OPENED = 1<<6;
        int FOREIGNKEY_ADDED = 1<<7;
        int FOREIGNKEY_DELETED = 1<<8;
        int FOREIGNKEY_MODIFIED = 1<<9;
        int INDEX_ADDED = 1<<10;
        int INDEX_DELETED = 1<<11;
        int INDEX_MODIFIED = 1<<12;
        int TABLE_ADDED = 1<<13;
        int TABLE_MODIFIED = 1<<14;
        int TABLE_DELETED  = 1<<15;
        int TABLE_REMOVED = 1<<16;
        int SCHEMA_ADDED = 1<<17;
        int SCHEMA_MODIFIED = 1<<18;
        int SCHEMA_DELETED = 1<<19;
        int DIAGRAM_DELETED = 1<<20;
        int DIAGRAM_MODIFIED = 1<<21;
        int DIAGRAM_ADDED = 1<<22;
        int FOREIGNKEY_SELECTED = 1<<23;
        int CONSTRAINT_ADDED = 1<<24;
        int CONSTRAINT_DELETED = 1<<25;
        int CONSTRAINT_MODIFIED = 1<<26;
        int DBMODELS_CHANGED = 1<<27;
        int DBMODELS_ADDED = 1<<28;
        int DBMODELS_REMOVED = 1<<29;
        int MODEL_PROPERTIES_CHANGED = 1<<30;
    }
    
    public interface Properties {
        String NAME = "name";
        String CATALOG = "catalog";
        String COMMENT = "comment";
        String DESCRITPION = "description";
        String SCHEMA = "schema";
        String TYPE = "type";
        String COLUMN_ORDER = "column_order";
        String COLUMN_PK = "column_pk";
        String COLUMN_NAME = "column_name";
        String COLUMN_DATATYPE = "column_name";
        String COLUMN_PRECISION = "precision";
        String COLUMN_SCALE = "scale";
        String COLUMN_NULLABLE = "nullable";
        String COLUMN_DEFAULT = "default";
        String COLUMN_COMMENT = "comment";
		String FK_COLUMN_DELETED = "fk.column_deleted";
		String FK_COLUMN_ADDED = "fk.column_added";
		String FK_COLUMN_REPLACED = "fk.column_replaced";
		String FK_DELETE_RULE = "fk.delete_rule";
		String FK_UPDATE_RULE = "fk.update_rule";
    }
    
    public static class Event {
        /** (optional) the GUI element from which the event originates */
        public final Object origin;
        /** (recommended) the model element that 'owns' the event target element */
        public final Object owner;
        /** (optional) informational data that further characterizes the event */
        public final Object info;
        /** (required) the model element that is the event target (e.g., that was modified) */
        public final Object element;
        /** (optional) whether more events of the same type are immediately following */
        public final boolean moreComing;
        
        private Event(Object origin, Object owner, Object info, Object element, boolean moreComing) {
            this.origin = origin;
            this.owner = owner;
            this.info = info;
            this.element = element;
            this.moreComing = moreComing;
        }
        
        private Event(Object origin, Object owner, Object info, Object element) {
            this(origin, owner, info, element, false);
        }
        
        private Event(Object owner, Object info, Object element) {
            this(null, owner, info, element, false);
        }
        
        private Event(Object owner, Object info, Object element, boolean moreComing) {
            this(null, owner, info, element, moreComing);
        }
    }
    
    public abstract static class Listener implements Types, Properties {

        /**
         * the event types this listener wants to be notified for. Bitwise or'd
         * from the {@link Types} constants 
         */
        private int eventTypes;
        
        /**
         * notification about the occurrence of an event 
         * @param eventType the event type for which the listener was registered
         * @param event the event proper
         */
        public abstract void eventOccurred(int eventType, Event event);

        /** 
         * subclasses may override to register with the event manager
         * @throws UnsupportedOperationException 
         */
        public void register() {
            throw new UnsupportedOperationException();
        }
        
        /**
         * unregister from the event manager
         */
        public void unregister() {
            RMBenchPlugin.getEventManager().removeListener(this);
        }
    }
    
    private Set<Listener> listeners = new HashSet<Listener>();
    
    private int deferActions = 0;
    private List<DeferredAction> deferredActions = new ArrayList<DeferredAction>();
    
    private abstract class DeferredAction {
        protected Listener listener;
        abstract void execute();
    }
    private class DeferredRemove extends DeferredAction {
        DeferredRemove(Listener listener) {
            this.listener = listener;
        }
        void execute() {
            listeners.remove(listener);
        }
    }
    private class DeferredAdd extends DeferredAction {
        DeferredAdd(Listener listener) {
            this.listener = listener;
        }
        void execute() {
            listeners.add(listener);
        }
    }
    
    private Table selectedTable;
    private Column selectedColumn;
    

    /**
     * add a listener for the given event type(s). A listener will only be 
     * registered once
     * 
     * @param eventTypes predefined event types, bitwise or'd together
     * @param listener the listener to be registered
     */
    public void addListener(int eventTypes, Listener listener) {
        listener.eventTypes |= eventTypes;
        if(deferActions > 0)
            deferredActions.add(new DeferredAdd(listener));
        else
            listeners.add(listener);
    }

    /**
     * completely remove a listener
     * @param listener
     */
    public void removeListener(Listener listener) {
        listener.eventTypes = 0;
        if(deferActions > 0)
            deferredActions.add(new DeferredRemove(listener));
        else
            listeners.remove(listener);
    }
    
    /**
     * remove a listener from a given event type
     * @param listener
     */
    public void removeListener(int eventType, Listener listener) {
        listener.eventTypes ^= eventType;
        if(listener.eventTypes == 0) {
            removeListener(listener);
        }
    }

    /**
     * fire an event notification about an addition to the connection
     * 
     * @param plugin the originating plugin
     * @param dbmodel an added DB model, or null
     */
    public void fireDBModelAdded(RMBenchPlugin plugin, DBModel dbmodel) {
        fireEvent(Types.DBMODELS_ADDED, new Event(plugin, null, dbmodel));
    }
    /**
     * fire an event notification about an addition to the connection
     * 
     * @param plugin the originating plugin
     * @param dbmodel an added DB model, or null
     */
    public void fireDBModelRemoved(RMBenchPlugin plugin, DBModel dbmodel) {
        fireEvent(Types.DBMODELS_REMOVED, new Event(plugin, null, dbmodel));
    }
    
    
    /**
     * fire an event notification about a change to the connections
     * 
     * @param plugin the originating plugin
     * @param dbmodel an added DB model, or null
     */
    public void fireDBModelsChanged(RMBenchPlugin plugin, DBModel dbmodel) {
        fireEvent(Types.DBMODELS_CHANGED, new Event(plugin, null, dbmodel));
    }
    
    
    
    /**
     * fire an event notification about a new column that was added
     * 
     * @param table the table the columns was added to
     * @param column the column proper
     */
    public void fireColumnAdded(Table table, Column column) {
        fireEvent(Types.COLUMN_ADDED, new Event(table, null, column));
    }
    
    /**
     * fire an event notification about a new column that was added
     * 
     * @param table the table the columns was added to
     * @param column the column proper
     * @param moreComing true if are more of the same kind of event immediately following
     */
    public void fireColumnAdded(Table table, Column column, boolean moreComing) {
        fireEvent(Types.COLUMN_ADDED, new Event(table, null, column, moreComing));
    }
    
    /**
     * fire an event notification about a new column that was removed
     * 
     * @param the table the column was removed from
     * @param column the column proper
     */
    public void fireColumnDeleted(Table table, Column column) {
        fireEvent(Types.COLUMN_DELETED, new Event(table, null, column));
    }

    /**
     * fire an event notification about a new column that was removed
     * 
     * @param the table the column was removed from
     * @param column the column proper
     * @param moreComing true if are more of the same kind of event immediately following
     */
    public void fireColumnDeleted(Table table, Column column, boolean moreComing) {
        fireEvent(Types.COLUMN_DELETED, new Event(table, null, column, moreComing));
    }

	/**
     * fire a modification event for a column
     * 
     * @param table the owning table
     * @param property the modified property
     * @param column the column that was modified
     */
    public void fireColumnModified(Table table, String property, Column column) {
        fireEvent(Types.COLUMN_MODIFIED, new Event(table, property, column));
    }
    
    /**
     * fire a modification event for a group of columns. Modifications will normally only 
     * affect secondary properties (e.g., being part of the Primary Key)
     * 
     * @param table the owning table
     * @param property the modified property
     * @param columns the columns that were modified
     */
    public void fireColumnsModified(Table table, String property, Column[] columns) {
        fireEvent(Types.COLUMNS_MODIFIED, new Event(table, property, columns));
    }
    
    /**
     * fire an event that a column has been selected. If a table selection does 
     * not exist already, a table selection event will also be fired 
     * 
     * @param source the source of the event
     * @param data additional data describing the event
     * @param column the column model object
     */
    public void fireColumnSelected(Object source, Column column) {
        setSelectedColumn(column);
        fireEvent(Types.COLUMN_SELECTED, new Event(source, null, column));
    }

    /**
     * fire an event that a column has been double-clicked, which should 
     * immediately put the column in edit mode
     *  
     * @param source the source of the event
     * @param data additional data describing the event
     * @param column the column model object
     * @see #fireColumnSelected(Object, Object, Column)
     */
    public void fireColumnOpened(Object origin, Column column) {
        setSelectedColumn(column);
        fireEvent(Types.COLUMN_OPENED, new Event(origin, column.getTable(), null, column));
    }
    
    /**
     * @param source the source of the event
     * @param data additional data describing the event
     * @param table the new table
     */
    public void fireTableAdded(Object source, Object data, Table table) {
        fireEvent(Types.TABLE_ADDED, new Event(source, data, table));
    }
    
    /**
     * fire an event that a table was deleted from the model
     * 
     * @param source the source of the event
     * @param data additional data describing the event
     * @param table the table
     */
    public void fireTableDeleted(Object source, Object data, Table table) {
        fireEvent(Types.TABLE_DELETED, new Event(source, data, table));
    }
    
    /**
     * fire an event that a table was removed from a diagram
     * 
     * @param diagram the diagram
     * @param data additional data describing the event
     * @param table the table
     */
    public void fireTableRemoved(Diagram diagram, Object data, Table table) {
        fireEvent(Types.TABLE_REMOVED, new Event(diagram, data, table));
    }
    
    /**
     * fire an event about a table selection
     * 
     * @param origin the GUI origin of the event
     * @param table the table model object
     */
    public void fireTableSelected(Object origin, Table table) {
        if(selectedTable != table) {
            selectedTable = table;
            if(selectedColumn != null && selectedTable != selectedColumn.getTable()) {
                selectedColumn = null;
            }
            Schema schema = selectedTable != null ? selectedTable.getSchema() : null;
            fireEvent(Types.TABLE_SELECTED, new Event(origin, schema, null, selectedTable));
        }
    }
    
    /**
     * fire an event notification that a foreign key was selected. This will first
     * fire a table selection event.
     * 
     * @param source the source of the event
     * @param data additional data describing the event
     * @param key the foreign key model object
     */
    public void fireForeignKeySelected(Object source, ForeignKey key) {
        fireTableSelected(source, key.getTable());
        fireEvent(Types.FOREIGNKEY_SELECTED, new Event(source, key.getTable(), null, key));
    }
    
    /**
     * fire an event about a table modification
     * 
     * @param origin the GUI origin of the event
     * @param property the property that was modified
     * @param table the table model object
     */
    public void fireTableModified(Object origin, String property, Table table) {
        fireEvent(Types.TABLE_MODIFIED, new Event(origin, property, table));
    }
    
    /**
     * fire an event that a table was moved to a different schema
     * 
     * @param origin the GUI origin of the event
     * @param from Schema the schema from which the table was moved
     * @param table the table model object
     */
    public void fireTableMoved(Object origin, Schema fromSchema, Table table) {
        fireEvent(Types.TABLE_MODIFIED, new Event(origin, fromSchema, Properties.SCHEMA, table));
    }
    
    /**
     * fire an event notification that a foreign key was created
     * 
     * @param origin the GUI origin of the event
     * @param foreignKey the foreign key
     * @param generatedColumns the columns that were generated, or <code>null</code>
     */
    public void fireForeignKeyAdded(Object origin, ForeignKey foreignKey, Column[] generatedColumns) {
        fireEvent(Types.FOREIGNKEY_ADDED, 
        		new Event(origin, foreignKey.getTable(), generatedColumns, foreignKey));
    }
    
    /**
     * fire an event notification that a foreign key was created
     * 
     * @param origin the GUI origin of the event
     * @param foreignKey the foreign key
     */
    public void fireForeignKeyAdded(Object origin, ForeignKey foreignKey) {
        fireEvent(Types.FOREIGNKEY_ADDED, new Event(origin, foreignKey.getTable(), null, foreignKey));
    }
    
    /**
     * fire an event notification that a foreign key was deleted
     * 
     * @param origin the GUI origin of the event
     * @param foreignKey the foreign key
     * @param generatedColumns the columns that were generated, or <code>null</code>
     */
    public void fireForeignKeyDeleted(Object origin, ForeignKey foreignKey, Column[] generatedColumns) {
        fireEvent(Types.FOREIGNKEY_DELETED, 
        		new Event(origin, foreignKey.getTable(), generatedColumns, foreignKey));
    }
    
    /**
     * fire an event notification that a foreign key was deleted
     * 
     * @param origin the GUI origin of the event
     * @param foreignKey the foreign key
     */
    public void fireForeignKeyDeleted(Object origin, ForeignKey foreignKey) {
        fireEvent(Types.FOREIGNKEY_DELETED,new Event(origin, foreignKey.getTable(), null, foreignKey));
    }
    
    /**
     * fire an event notification that a foreign key was modified
     * 
     * @param property the property that was modified
     * @param foreignKey the foreign key that was modified
     */
    public void fireForeignKeyModified(String property, ForeignKey foreignKey) {
        fireEvent(Types.FOREIGNKEY_MODIFIED, new Event(foreignKey.getTable(), property, foreignKey));
    }
    
    /**
     * fire an event notification that an index was added
     * 
     * @param table the owning table
     * @param index the index that was added
     */
    public void fireIndexAdded(Table table, Index index) {
        fireEvent(Types.INDEX_ADDED, new Event(table, null, index));
    }
    
    /**
     * fire an event notification that an index was removed
     * 
     * @param table the owning table
     * @param index the index that was removed
     */
    public void fireIndexDeleted(Table table, Index index) {
        fireEvent(Types.INDEX_DELETED, new Event(table, null, index));
    }

    /**
     * fire an event notification that an index was modified
     * 
     * @param table the owning table
     * @param property the property that was modified
     * @param index the index that was modified
     */
    public void fireIndexModified(Table table, String property, Index index) {
        fireEvent(Types.INDEX_MODIFIED, new Event(table, property, index));
    }
    
    /**
     * fire an event notification that a constraint was added
     * 
     * @param constraint the constraint that was added
     */
    public void fireConstraintAdded(Constraint constraint) {
        fireEvent(Types.CONSTRAINT_ADDED, new Event(constraint.getTable(), null, constraint));
    }
    
    /**
     * fire an event notification that a constraint was removed
     * 
     * @param table the owning table
     * @param constraint the constraint that was removed
     */
    public void fireConstraintDeleted(Constraint constraint) {
        fireEvent(Types.CONSTRAINT_DELETED, new Event(constraint.getTable(), null, constraint));
    }

    /**
     * fire an event notification that a constraint was modified
     * 
     * @param table the owning table
     * @param index the constraint that was modified
     */
    public void fireConstraintModified(Constraint constraint) {
        fireEvent(Types.CONSTRAINT_MODIFIED, new Event(constraint.getTable(), null, constraint));
    }
    
    /**
     * fire an event notification that a schema was added
     * 
     * @param origin the GUI origin of the event
     * @param model the model the schema is being added to
     * @param schema the schema
     */
    public void fireSchemaAdded(Object origin, Model model, Schema schema) {
        fireEvent(Types.SCHEMA_ADDED, new Event(origin, model, null, schema));
    }
    
    /**
     * fire an event notification that a schema was removed
     * 
     * @param origin the GUI origin of the event
     * @param model the Model from which the schema is being removed
     * @param schema the schema
     */
    public void fireSchemaDeleted(Object origin, Model model, Schema schema) {
        fireEvent(Types.SCHEMA_DELETED, new Event(origin, model, null, schema));
    }
    
    /**
     * fire an event notification that a schema was renamed
     * @param origin the GUI origin of the event
     * @param prop the property that was modified
     * @param schema Schema the schema from which the table was moved
     */
    public void fireSchemaModified(Object origin, String prop, Schema schema) {
        fireEvent(Types.SCHEMA_MODIFIED, new Event(origin, prop, schema));
    }
    
    
    
    /**
     * fire an event notification that a diargam was added
     * 
     * @param origin the GUI origin of the event
     * @param model the owning model
     * @param data additional data describing the event
     * @param diagram the diagram
     */
    public void fireDiagramAdded(Object origin, Model model, Object data, Diagram diagram) {
        fireEvent(Types.DIAGRAM_ADDED, new Event(origin, model, data, diagram));
    }

    /**
     * fire an event notification that a diagram was removed
     * 
     * @param origin the GUI origin of the event
     * @param model the owning model
     * @param data additional data describing the event
     * @param diagram the diagram
     */
    public void fireDiagramDeleted(Object origin, Model model, Object data, Diagram diagram) {
        fireEvent(Types.DIAGRAM_DELETED, new Event(origin, model, data, diagram));
    }
    
    /**
     * fire an event notification that a diagram was modfied
     * @param origin the object that caused the event
     * @param property the property that was modified
     * @param diagram the diagram that was modified
     */
    public void fireDiagramModified(Object origin, String property, Diagram diagram) {
        fireEvent(Types.DIAGRAM_MODIFIED, new Event(origin, property, diagram));
    }
    
    /**
     * fire an event notification that the given models properties were changed
     * @param model the model where  properies were changed
     */
    public void fireModelPropertiesChanged(Model model){
        fireEvent(Types.MODEL_PROPERTIES_CHANGED, new Event(null, null, model));
    }
    
    /**
     * @return the table that was last selected globally
     */
    public Table getSelectedTable() {
        return selectedTable;
    }
    
    private void setSelectedColumn(Column column) {
        selectedColumn = column;
        fireTableSelected(null, column.getTable());
    }

    private void fireEvent(int eventType, Event event) {
        deferActions++;
        for (Iterator<Listener> it = listeners.iterator(); it.hasNext();) {
            Listener listener = it.next();
            if((eventType & listener.eventTypes) != 0) {
                listener.eventOccurred(eventType, event);
            }
        }
        deferActions--;
        if(deferActions == 0) {
            for (Iterator<DeferredAction> it = deferredActions.iterator(); it.hasNext();) {
                DeferredAction action = it.next();
                action.execute();
            }
            deferredActions.clear();
        }
    }
}
