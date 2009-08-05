/*
 * created 15.03.2005
 * 
 * $Id:CommandAdapter.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.gef.commands.Command;


/**
 * adapter from a GEF command to a platform undo operation. Only execute is handled
 * by the command API, the rest (undo/redo) is performed by the platform undo machinery
 * 
 * @author cse
 */
public class CommandAdapter extends Command {
    private final RMBenchOperation operation;

    public CommandAdapter(RMBenchOperation operation) {
        super();
        this.operation = operation;
    }
    
    public void execute() {
        operation.execute(this);
    }

    public boolean canExecute() {
        return operation.canExecute();
    }

    public void redo() {
        throw new UnsupportedOperationException();
    }

    public void undo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the adapted operation
     */
    public RMBenchOperation getOperation() {
        return operation;
    }
}
