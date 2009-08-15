/*
 * created 26.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: NewTableOperation.java 664 2007-09-28 17:28:39Z cse $
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
import com.byterefinery.rmbench.model.schema.Table;

/**
 * undoable operation that adds a new table to a schema. Note that there is 
 * a separate operation that adds to the model <em>and</em> to a diagram
 * 
 * @author cse
 * @see com.byterefinery.rmbench.operations.AddTableOperation
 */
public class NewTableOperation extends RMBenchOperation {

    private final Schema schema;
    private final String tableName;
    private transient Table table;
    
    public NewTableOperation(Model model, Schema schema, String name) {
        super(Messages.Operation_AddTable);
        this.schema = schema;
        this.tableName = name;
    }

    public NewTableOperation(Model model, Schema schema) {
        this(model, schema, model.getNameGenerator().generateTableName(model.getIModel(), schema.getISchema()));
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	
        table = new Table(schema, tableName);
        
        RMBenchPlugin.getEventManager().fireTableAdded(eventSource, null, table);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        table.abandon();
        RMBenchPlugin.getEventManager().fireTableDeleted(this, null, table);
        
        table = null;
        return Status.OK_STATUS;
    }

    public Table getTable() {
        return table;
    }
}
