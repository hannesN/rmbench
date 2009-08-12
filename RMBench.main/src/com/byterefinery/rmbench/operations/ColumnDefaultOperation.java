/*
 * created 17.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnDefaultOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;

/**
 * Operation for changing the name of a column
 * @author cse
 */
public class ColumnDefaultOperation extends RMBenchOperation {

    private final Column column;
    private final String oldDefault, newDefault;
    
    public ColumnDefaultOperation(Column column, String newDefault) {
        super(Messages.Operation_ModifyColumn);
        this.column = column;
        this.oldDefault = column.getDefault();
        this.newDefault = 
            newDefault == null || newDefault.length() == 0 ? null : newDefault;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setDefault(newDefault);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_DEFAULT, column);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setDefault(oldDefault);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_DEFAULT, column);
        return Status.OK_STATUS;
    }
}
