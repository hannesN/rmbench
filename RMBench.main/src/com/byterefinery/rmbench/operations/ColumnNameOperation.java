/*
 * created 17.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ColumnNameOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * Operation for changing the name of a column
 * @author cse
 */
public class ColumnNameOperation extends ModifyColumnOperation {

    private final String oldName, newName;
    
    public ColumnNameOperation(Column column, String newName) {
        super(Messages.Operation_ModifyColumn, column);
        this.oldName = column.getName();
        this.newName = newName;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setName(newName);
        renameForeignKeyColumn(newName, oldName);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_NAME, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_NAME);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setName(oldName);
        renameForeignKeyColumn(oldName, newName);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_NAME, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_NAME);
        return Status.OK_STATUS;
    }
    
    public void renameForeignKeyColumn(String newName, String oldName) {
        for (ForeignKey key : column.getForeignKeys()) {
            Column[] columns=key.getColumns();
            for (int i=0; i<columns.length; i++) {
                if (columns[i].getName().equals(oldName)) {
                    columns[i].setName(newName);
                }
            }
        }
    }
}
