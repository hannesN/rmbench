/*
 * created 17.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableDescriptionOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * historized operation for changing the table description
 * 
 * @author cse
 */
public class TableDescriptionOperation extends RMBenchOperation {

    private Table table;
    private String oldDescription;
    private String newDescription;
    
    public TableDescriptionOperation(Table table) {
        super(Messages.Operation_ChangeTableDescription);
        setTable(table);
    }
    
    public void setTable(Table table) {
        this.table = table;
        this.oldDescription = table != null ? table.getDescription() : null;
    }
    
    public void setNewDescription(String newDescription){
        this.newDescription = newDescription;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setDescription(eventSource, newDescription);
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setDescription(this, newDescription);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setDescription(this, oldDescription);
    }

    private IStatus setDescription(Object eventSource, String description) {
        table.setDescription(description);
        RMBenchPlugin.getEventManager().fireTableModified(
                eventSource, EventManager.Properties.DESCRITPION, table);
        return Status.OK_STATUS;
    }
}
