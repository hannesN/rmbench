/*
 * created 13.11.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * undoable operation for setting the diagram target schema
 * 
 * @author cse
 */
public class DiagramDefaultSchemaOperation extends RMBenchOperation {

    private final Diagram diagram;
    private final Schema targetSchema, oldTargetSchema;
    
    /**
     * @param diagram the diagram whose target schema is to be set
     * @param schema the target schema
     */
    public DiagramDefaultSchemaOperation(Diagram diagram, Schema schema) {
        super(Messages.Operation_DiagramSetDefaultSchema);
        this.diagram = diagram;
        this.targetSchema = schema;
        this.oldTargetSchema = diagram.getDefaultSchema();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        diagram.setDefaultSchema(targetSchema);
        RMBenchPlugin.getEventManager().fireDiagramModified(eventSource, EventManager.Properties.SCHEMA,
                            diagram);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        diagram.setDefaultSchema(oldTargetSchema);
        RMBenchPlugin.getEventManager().fireDiagramModified(eventSource, EventManager.Properties.SCHEMA,
                diagram);
        return Status.OK_STATUS;
    }
}
