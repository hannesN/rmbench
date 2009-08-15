/*
 * created 17.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableNameOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * historized operation for changing the table name
 * 
 * @author cse
 */
public class TableNameOperation extends RMBenchOperation {

    private Table table;
    private String oldName;
    private String newName;
    
    public TableNameOperation(Table table) {
        super(Messages.Operation_ChangeTableName);
        setTable(table);
    }
    
    public TableNameOperation(Table table, String newName) {
        super(Messages.Operation_ChangeTableName);
        setTable(table);
        setNewName(newName);
    }
    
    public void setTable(Table table) {
        this.table = table;
        this.oldName = table != null ? table.getName() : null;
    }
    
    public void setNewName(String newName){
        this.newName = newName;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setName(eventSource, newName);
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setName(this, newName);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setName(this, oldName);
    }

    private IStatus setName(Object eventSource, String name) {
        table.setName(name);
        RMBenchPlugin.getEventManager().fireTableModified(
                eventSource, EventManager.Properties.NAME, table);
        return Status.OK_STATUS;
    }
}
