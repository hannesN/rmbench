/*
 * created 24.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: SetTableTypeOperation.java 3 2005-11-02 03:04:20Z csell $
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
 * undoable operation for setting the table type
 * @author cse
 */
public class SetTableTypeOperation extends RMBenchOperation {

    private final Table table;
    private final String oldType, newType;
    
    public SetTableTypeOperation(Table table, String type) {
        super(Messages.Operation_SetTableType);
        this.table = table;
        this.oldType = table.getType();
        this.newType = type;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        table.setType(newType);
        RMBenchPlugin.getEventManager().fireTableModified(
                eventSource, EventManager.Properties.TYPE, table);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        table.setType(oldType);
        RMBenchPlugin.getEventManager().fireTableModified(
                eventSource, EventManager.Properties.TYPE, table);
        return Status.OK_STATUS;
    }
}
