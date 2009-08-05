/*
 * created 29.12.2005
 * 
 * $Id: MoveAnchorOperation.java 130 2006-01-26 19:23:28Z hannesn $
 */ 
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.figures.MoveableAnchor;

public class MoveAnchorOperation extends RMBenchOperation {

    private MoveableAnchor anchor;
    private int newEdge;
    private int newSlot;
    private int oldEdge;
    private int oldSlot;
    
    
    public MoveAnchorOperation(MoveableAnchor anchor, int edge, int slot) {
        super(Messages.Operation_MoveAnchor);
        this.anchor = anchor;
        newEdge = edge;
        newSlot = slot;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        oldSlot = anchor.getDForeignKeySlot();
        oldEdge = anchor.getDForeignKeyEdge();
        anchor.assignSlot(newSlot, newEdge);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        anchor.assignSlot(oldSlot, oldEdge);
        return Status.OK_STATUS;
    }

}
