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
 * $Id: AddSchemaOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * undoable operation for add schema
 * 
 * @author cse
 */
public class AddSchemaOperation extends RMBenchOperation {

    private final Model model;
    private final String schemaName;
    
    private Schema schema;
    
    public AddSchemaOperation(Model model, String schemaName) {
        super(Messages.Operation_NewSchema);
        this.model = model;
        this.schemaName = schemaName;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        schema =  new Schema(schemaName, model.getDatabaseInfo());
        model.addSchema(schema);
        RMBenchPlugin.getEventManager().fireSchemaAdded(eventSource, model, schema);
        return Status.OK_STATUS;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.addSchema(schema);
        RMBenchPlugin.getEventManager().fireSchemaAdded(this, model, schema);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        model.removeSchema(schema);
        RMBenchPlugin.getEventManager().fireSchemaDeleted(this, model, schema);
        return Status.OK_STATUS;
    }
}
