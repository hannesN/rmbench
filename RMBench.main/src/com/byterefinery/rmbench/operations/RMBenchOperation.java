/*
 * created 18.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: RMBenchOperation.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * utility base class for operations which maintains an event source during history 
 * execution. Operations are executed against the operation history administered by
 * the plugin class. The context is defined statically by this class 
 * 
 * @author cse
 */
public abstract class RMBenchOperation extends AbstractOperation {

    public static final IUndoContext CONTEXT = new IUndoContext() {

        public String getLabel() {
            return "RMBenchDefaultUndoContext";
        }

        public boolean matches(IUndoContext context) {
            return context == this;
        }
    };
    
    private static class Wrapper extends RMBenchOperation {

        private final IUndoableOperation wrapped;
        
        Wrapper(IUndoableOperation wrapped) {
            super(wrapped.getLabel());
            this.wrapped = wrapped;
        }
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return wrapped.execute(monitor, info);
        }

        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return wrapped.undo(monitor, info);
        }
    }
    
    protected transient Object eventSource = this;
    private boolean wasExecuted;
    
    
    public RMBenchOperation(String label) {
        super(label);
        addContext(CONTEXT);
    }

    /**
     * @param eventSource the event source to be used by sublcasses for event notifications 
     * (as appropriate)
     */
    public void setEventSource(Object eventSource) {
        this.eventSource = eventSource;
    }
    
    public abstract IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;
    public abstract IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

    /**
     * default implementation of redo, delegates to the 
     * {@link AbstractOperation.execute(IProgressMonitor, IAdaptable)} method
     */
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return execute(monitor, info);
    }

    /**
     * execute this operation in the RMBenchPlugin history
     *  
     * @param eventSource
     * @return true if the operation was executed successfully (status OK)
     */
    public boolean execute(Object eventSource) {
        this.eventSource = eventSource;
        try {
        	IStatus status;
            if(wasExecuted) {
                status = execute(null, null);
            }
            else {
                wasExecuted = true;
                status = RMBenchPlugin.getOperationHistory().execute(this, null, null);
            }
            return status.isOK();
        }
        catch (ExecutionException e) {
            RMBenchPlugin.logError(e);
            return false;
        }
    }

    /**
     * execute this operation outside of the undo history. The operation cannot be undone
     * through the history mechanism
     */
    public void nonUndoableExecute() {
        try {
            execute(null, null);
        } catch (ExecutionException e) {
            RMBenchPlugin.logError(e);
        }
    }
    
    /**
     * execute the given operation as an RMBenchOperation, either as is or appropriately wrapped
     */
    public static void executeOperation(IUndoableOperation operation, Object eventSource) {
        if(operation instanceof RMBenchOperation) {
            ((RMBenchOperation)operation).execute(eventSource);
        }
        else {
            new Wrapper(operation).execute(eventSource);
        }
    }
}
