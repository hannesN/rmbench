/*
 * created 19.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DeleteDiagramOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.diagram.Diagram;

/**
 * undoable operation for delete diagram
 * 
 * @author cse
 */
public class DeleteDiagramOperation extends RMBenchOperation {

    private final Model model;
    private final Diagram diagram;
    
    public DeleteDiagramOperation(Model model, Diagram diagram) {
        super(Messages.Operation_DeleteDiagram);
        this.model = model;
        this.diagram = diagram;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.removeDiagram(diagram);
        RMBenchPlugin.getEventManager().fireDiagramDeleted(eventSource, model, null, diagram);
        return Status.OK_STATUS;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.removeDiagram(diagram);
        RMBenchPlugin.getEventManager().fireDiagramDeleted(this, model, null, diagram);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.addDiagram(diagram);
        RMBenchPlugin.getEventManager().fireDiagramAdded(this, model, null, diagram);
        return Status.OK_STATUS;
    }
}
