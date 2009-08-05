/*
 * created 18.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableSchemaOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * undoable operation that moves a table to another schema
 * 
 * @author cse
 */
public class TableSchemaOperation extends RMBenchOperation {

    private final Table table;
    private final Schema oldSchema; 
    private Schema newSchema;
    
    public TableSchemaOperation(Table table) {
        super(Messages.Operation_ChangeTableSchema);
        this.table = table;
        this.oldSchema = table.getSchema();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setSchema(eventSource, oldSchema, newSchema);
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setSchema(this, oldSchema, newSchema);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setSchema(this, newSchema, oldSchema);
    }

    private IStatus setSchema(Object eventSource, Schema fromSchema, Schema toSchema) {
        table.setSchema(toSchema);
        RMBenchPlugin.getEventManager().fireTableMoved(eventSource, fromSchema, table);
        return Status.OK_STATUS;
    }

    public void execute(Object eventSource, Schema schema) {
        newSchema = schema;
        super.execute(eventSource);
    }
}
