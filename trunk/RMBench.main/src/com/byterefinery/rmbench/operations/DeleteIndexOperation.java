/*
 * created 07.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DeleteIndexOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Index;

/**
 * undoable operation for deleting an index
 * @author cse
 */
public class DeleteIndexOperation extends RMBenchOperation {

    private final Index index;
    
    public DeleteIndexOperation(Index index) {
        super(Messages.Operation_AddIndex);
        this.index = index;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        index.abandon();
        RMBenchPlugin.getEventManager().fireIndexDeleted(index.getTable(), index);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        index.restore();
        RMBenchPlugin.getEventManager().fireIndexAdded(index.getTable(), index);
        return Status.OK_STATUS;
    }
}
