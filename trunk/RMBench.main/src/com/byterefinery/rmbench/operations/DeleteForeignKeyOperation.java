/*
 * created 26.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DeleteForeignKeyOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * @author cse
 */
public class DeleteForeignKeyOperation extends RMBenchOperation {

    private final ForeignKey foreignKey;
    
    public DeleteForeignKeyOperation(ForeignKey foreignKey) {
        super(Messages.Operation_DeleteForeignKey);
        this.foreignKey = foreignKey;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        foreignKey.abandon();
        RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this, foreignKey, null);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        foreignKey.restore();
        RMBenchPlugin.getEventManager().fireForeignKeyAdded(this, foreignKey, null);
        return Status.OK_STATUS;
    }
}
