/*
 * created 30.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: CompoundOperation.java 670 2007-10-30 05:24:49Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * an operation that groups other operations
 * 
 * @author cse
 */
public class CompoundOperation extends RMBenchOperation {

    private final List<IUndoableOperation> operations = new ArrayList<IUndoableOperation>();
    
    public CompoundOperation(String label) {
        super(label);
    }

    /**
     * this constructor assumes that the CompoundCommand contains only
     * {@link CommandAdapter}s
     */
    @SuppressWarnings("unchecked")
	public CompoundOperation(String label, CompoundCommand command) {
        super(label);
        for (Iterator<CommandAdapter> it = command.getCommands().iterator(); it.hasNext();) {
            operations.add(it.next().getOperation());
        }
    }

    /**
     * @param masterOperation the master operation, which provides the label
     */
    public CompoundOperation(IUndoableOperation masterOperation) {
        super(masterOperation.getLabel());
        add(masterOperation);
    }

    /**
     * @param label
     * @param operations
     */
    public CompoundOperation(String label, List<? extends IUndoableOperation> operations) {
        super(label);
        for (IUndoableOperation undoableOperation : operations) {
            add(undoableOperation);
		}
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        for (Iterator<IUndoableOperation> it = operations.iterator(); it.hasNext();) {
            it.next().execute(monitor, info);
        }
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        for(int i=operations.size()-1; i >= 0; i--) {
            IUndoableOperation operation = (IUndoableOperation)operations.get(i);
            operation.undo(monitor, info);
        }
        return Status.OK_STATUS;
    }

    /**
     * @param operation the operation to be appended to this group
     */
    public void add(IUndoableOperation operation) {
        operations.add(operation);
    }

    /**
     * @param operation the operation to be prepended to this group
     */
    public void addFirst(IUndoableOperation operation) {
        operations.add(0, operation);
    }
    
    /**
     * @return the numer of sub-operations directly contained in this operation
     */
    public int size() {
    	return operations.size();
    }
}
