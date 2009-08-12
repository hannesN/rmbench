/*
 * created 30.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModifyKeyConstraintOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.PrimaryKeyDependencyDialog;
import com.byterefinery.rmbench.dialogs.PrimaryKeyDependencyDialog.ColumnInfo;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Key;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.util.Utils;

/**
 * undoable operation for modifying primary and unique keys
 * @author cse
 */
public class ModifyKeyConstraintOperation extends RMBenchOperation {

    // thats all the stuff for a key
    private final Key key;
    private final String newName;
    private final String oldName;
    private final Column[] newColumns;
    private final Column[] oldColumns;
    private final Column[] deltaColumns;
    
    // that's all the stuff for dependencies of a Primary Key
    private List<Column> deletedColumns;
    private List<Column> addedColumns;
    private List<ForeignKey> foreignKeys;
    private Map<ForeignKey, Column[]> newForeignKeyColumns;
    private Map<ForeignKey, Column[]> oldForeignKeyColumns;
    
    /**
     * @param key the key to modify. Note that this key must also implement the {@link Constraint} 
     * interface
     * @param newName the modified name, or <code>null</code>
     * @param newColumns the modified columns, or <code>null</code>
     */
    public ModifyKeyConstraintOperation(Key key, String newName, Column[] newColumns) {
        super(Messages.Operation_ModifyKeyConstraint);
        this.key = key;
        this.newName = newName;
        this.newColumns = newColumns;
        this.oldName = key.getName();
        this.oldColumns = key.getColumns();
        
        if(newColumns != null) {
            List<Column> delta = new ArrayList<Column>();
            addMissing(delta, newColumns, oldColumns);
            addMissing(delta, oldColumns, newColumns);
            this.deltaColumns = (Column[])delta.toArray(new Column[delta.size()]);
        }
        else {
            this.deltaColumns = null;
        }
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (key instanceof PrimaryKey) {
            PrimaryKeyDependencyDialog dialog = new PrimaryKeyDependencyDialog((PrimaryKey) key, newColumns, false);
            if (dialog.calculateDependencies()) {
                if (dialog.open() != MessageDialog.OK)
                    return Status.CANCEL_STATUS;
            }
            
            Map<ForeignKey, ColumnInfo[]> fkColumnInfos = dialog.getForeignKeyColumns();
            
            foreignKeys = dialog.getForeignKeys();
            if(dialog.getDeleteFK())
            	deletedColumns = dialog.getRemoveableFKColumns();
            else 
            	deletedColumns = Utils.emptyList();
            
            
            newForeignKeyColumns = new HashMap<ForeignKey, Column[]>();
            oldForeignKeyColumns = new HashMap<ForeignKey, Column[]>(foreignKeys.size());
            addedColumns = new ArrayList<Column>();
            
            for (ForeignKey fk : foreignKeys) {
                oldForeignKeyColumns.put(fk, fk.getColumns());
            }
            

            for (Column col : deletedColumns) {
                col.abandon();
                RMBenchPlugin.getEventManager().fireColumnDeleted(col.getTable(), col);
            }

            for (ForeignKey fk : foreignKeys) {
            	
                ColumnInfo infos[] = (ColumnInfo[]) fkColumnInfos.get(fk);
                Column columns[] = new Column[infos.length];
                for (int i = 0; i < infos.length; i++) {
                    if (!infos[i].isExist()) {
                        //creates a new column and adds it to the table
                        columns[i] = infos[i].getColumn();
                        addedColumns.add(columns[i]);
                        RMBenchPlugin.getEventManager().fireColumnAdded(columns[i].getTable(), columns[i]);
                        
                    } else {
                        columns[i] = infos[i].getColumn();
                    }
                }
                fk.setColumns(columns);
                newForeignKeyColumns.put(fk, columns);
                RMBenchPlugin.getEventManager().fireForeignKeyModified(
                        EventManager.Properties.FK_COLUMN_REPLACED, fk);
            }
            
        }
        
        if(newName != null)
            key.setName(newName);
        if(newColumns != null)
            key.setColumns(newColumns);
        RMBenchPlugin.getEventManager().fireConstraintModified((Constraint)key);
        fireColumnEvents();
        
        return Status.OK_STATUS;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        if (key instanceof PrimaryKey) {           
            for (Iterator<Column> it = deletedColumns.iterator(); it.hasNext();) {
                Column col = it.next();
                col.abandon();
                if (it.hasNext())
                    RMBenchPlugin.getEventManager().fireColumnDeleted(col.getTable(), col, true);
                else
                    RMBenchPlugin.getEventManager().fireColumnDeleted(col.getTable(), col);
            }

            for (Iterator<Column> it = addedColumns.iterator(); it.hasNext();) {
                Column col = it.next();
                col.restore();
                if (it.hasNext())
                    RMBenchPlugin.getEventManager().fireColumnAdded(col.getTable(), col, true);
                else
                    RMBenchPlugin.getEventManager().fireColumnAdded(col.getTable(), col);
            }
            
            for (ForeignKey fk : foreignKeys) {
                fk.setColumns((Column[]) newForeignKeyColumns.get(fk));
                RMBenchPlugin.getEventManager().fireForeignKeyModified(
                        EventManager.Properties.FK_COLUMN_REPLACED, fk);
            }
        }
        
        
        
        if(newName != null)
            key.setName(newName);
        if(newColumns != null)
            key.setColumns(newColumns);
        RMBenchPlugin.getEventManager().fireConstraintModified((Constraint)key);
        fireColumnEvents();
        
        return Status.OK_STATUS;
    }
    
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        
        if (key instanceof PrimaryKey) {
            
            for (Column col : deletedColumns) {
                col.restore();
                RMBenchPlugin.getEventManager().fireColumnAdded(col.getTable(), col, true);
            }
            
            for (ForeignKey fk : foreignKeys) {
                
                for (Column col : addedColumns) {
                    col.abandon();
                    RMBenchPlugin.getEventManager().fireColumnDeleted(col.getTable(), col, true);
                }
                
                Column columns[] = (Column[]) oldForeignKeyColumns.get(fk);
                fk.setColumns(columns);
                RMBenchPlugin.getEventManager().fireForeignKeyModified(
                        EventManager.Properties.FK_COLUMN_REPLACED, fk);
            }
            
            
        }
        
        if(newName != null)
            key.setName(oldName);
        if(newColumns != null)
            key.setColumns(oldColumns);
        RMBenchPlugin.getEventManager().fireConstraintModified((Constraint)key);
        fireColumnEvents();
        
        return Status.OK_STATUS;
    }

    private void fireColumnEvents() {
        if(deltaColumns != null && key instanceof PrimaryKey) {
            RMBenchPlugin.getEventManager().fireColumnsModified(
                    key.getTable(), EventManager.Properties.COLUMN_PK, deltaColumns);
        }
    }

    private static void addMissing(List<Column> delta, Column[] columns1, Column[] columns2) {
        for (int i = 0; i < columns1.length; i++) {
            int j = 0;
            for (; j < columns2.length; j++) {
                if(columns1[i] == columns2[j])
                    break;
            }
            if(j == columns2.length) 
                delta.add(columns1[i]);
        }
    }
}
