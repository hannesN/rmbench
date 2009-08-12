/*
 * created 17.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnNullableOperation.java 3 2005-11-02 03:04:20Z csell $
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
 * operation for setting the nullable flag on a column
 *  
 * @author cse
 */
public class ColumnNullableOperation extends ModifyColumnOperation {

    private final boolean isNullable;
    
    public ColumnNullableOperation(Column column) {
        super(Messages.Operation_ModifyColumn, column);
        this.isNullable = column.getNullable();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setNullable(!isNullable);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_NULLABLE, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_NULLABLE);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setNullable(isNullable);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_NULLABLE, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_NULLABLE);
        return Status.OK_STATUS;
    }
}
