/*
 * created 26.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DeleteTableOperation.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.TableDependencyDialog;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * Operation that will delete a table from its schema, cutting off all  relationships.
 * If a diagram is passed to the constructor, table will be removed from the diagram 
 * as well.
 * 
 * @author cse
 */
public class DeleteTableOperation extends RMBenchOperation {

    private final ArrayList<DTable> dtables;
    private final Table   table;
    
    private final ForeignKey[] foreignKeys;
    
    public DeleteTableOperation(Table table, Model model) {
        super(Messages.Operation_DeleteTable);
        
        this.table=table;
        
        //find all diagrams which use the table:
        dtables = new ArrayList<DTable>();
               
        int sz = table.getForeignKeys().size() + table.getReferences().size();
        this.foreignKeys = new ForeignKey[sz];

        int index = 0;
        for (Iterator<ForeignKey> it = table.getForeignKeys().iterator(); it.hasNext();) {
            this.foreignKeys[index++] = (ForeignKey) it.next();
        }
        for (Iterator<ForeignKey> it = table.getReferences().iterator(); it.hasNext();) {
            this.foreignKeys[index++] = (ForeignKey) it.next();
        }
        
    }
    
    public DeleteTableOperation(DTable dtable) {
    	this(null, dtable);
    }
    
    public DeleteTableOperation(Diagram diagram, DTable dtable) {
        this (dtable.getTable(), dtable.getDiagram().getModel());
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        DTable tmpTable;
        
        TableDependencyDialog dialog = new TableDependencyDialog(table);
        if (dialog.calculateDependencies()) {
            if (dialog.open()==MessageDialog.OK) {
                dtables.addAll(dialog.getDTables());
            } else {
                return Status.CANCEL_STATUS;
            }
        }
        
        table.abandon();
        
        for(int i=0; i<foreignKeys.length; i++) {
            foreignKeys[i].abandon();
            RMBenchPlugin.getEventManager().fireForeignKeyDeleted(foreignKeys[i].getTable(), foreignKeys[i]);
        }
        for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
        	tmpTable=(DTable) it.next();
            tmpTable.getDiagram().removeTable(tmpTable);
        }
        
        RMBenchPlugin.getEventManager().fireTableDeleted(null, foreignKeys, table);
        return Status.OK_STATUS;
    }
    
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        DTable tmpTable;
        
        table.abandon();
        
        for(int i=0; i<foreignKeys.length; i++) {
            foreignKeys[i].abandon();
            RMBenchPlugin.getEventManager().fireForeignKeyDeleted(foreignKeys[i].getTable(), foreignKeys[i]);
        }
        for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
            tmpTable=(DTable) it.next();
            tmpTable.getDiagram().removeTable(tmpTable);
        }
        
        RMBenchPlugin.getEventManager().fireTableDeleted(null, foreignKeys, table);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        DTable tmpTable;
        
        table.restore();
        for(int i=0; i<foreignKeys.length; i++) {
            foreignKeys[i].restore();
            RMBenchPlugin.getEventManager().fireForeignKeyAdded(foreignKeys[i].getTable(), foreignKeys[i]);
        }
        
        for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
            tmpTable=(DTable) it.next();
            tmpTable.getDiagram().addTable(tmpTable);
        }
        
        RMBenchPlugin.getEventManager().fireTableAdded(null, foreignKeys, table);
        return Status.OK_STATUS;
    }
}
