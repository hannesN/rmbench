/*
 * created 19.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: AddDiagramOperation.java 27 2005-11-11 22:10:42Z csell $
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
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * undoable operation for add diagram
 * 
 * @author cse
 */
public class AddDiagramOperation extends RMBenchOperation {

    private final Model model;
    private final String name;
    private final Schema targetSchema;
    
    private Diagram diagram;
    
    public AddDiagramOperation(Model model, String name, Schema targetSchema) {
        super(Messages.Operation_NewDiagram);
        this.model = model;
        this.name = name;
        this.targetSchema = targetSchema;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        diagram = new Diagram(model, name, targetSchema);
        RMBenchPlugin.getEventManager().fireDiagramAdded(eventSource, model, null, diagram);
        return Status.OK_STATUS;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.addDiagram(diagram);
        RMBenchPlugin.getEventManager().fireDiagramAdded(this, model, null, diagram);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.removeDiagram(diagram);
        RMBenchPlugin.getEventManager().fireDiagramDeleted(this, model, null, diagram);
        return Status.OK_STATUS;
    }
    
    /**
     * @return the diagram, which, depending on the current execution state, may be fully valid,
     * null or abandoned (not part of the model)
     */
    public Diagram getDiagram() {
        return diagram;
    }
}
