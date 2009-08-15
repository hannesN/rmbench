/*
 * created 23.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DeleteTableConstraintOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.PrimaryKeyDependencyDialog;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.util.Utils;

/**
 * undoable operation for deleting a table constraint
 * @author cse
 */
public class DeleteTableConstraintOperation extends RMBenchOperation {

    private final Table table;
    private final Constraint constraint;
    private boolean deleteFk;
    private List<ForeignKey> foreignKeys;
    private List<Column> columns;
    
    public DeleteTableConstraintOperation(Table table, Constraint constraint) {
        super(Messages.Operation_DeleteConstraint);
        this.table = table;
        this.constraint = constraint;
        columns = Utils.emptyList();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(constraint instanceof PrimaryKey) {
            PrimaryKeyDependencyDialog dialog = new PrimaryKeyDependencyDialog((PrimaryKey) constraint);
            if (dialog.calculateDependencies()) {
                if (dialog.open()!=MessageDialog.OK) {
                    return Status.CANCEL_STATUS;
                }
            }
            foreignKeys = dialog.getForeignKeys();
            deleteFk = dialog.getDeleteFK();
            if (deleteFk) {
                columns = new ArrayList<Column>();
                for (ForeignKey foreignKey : foreignKeys) {
                    Column fkcolumns[] = foreignKey.getColumns();
                    for (int i = fkcolumns.length-1; i >= 0; i--) {
                        columns.add(fkcolumns[i]);
                        fkcolumns[i].saveIndex();
                    }
                }
            }
        }
        return redo (monitor, info);
    }
    
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (constraint instanceof PrimaryKey) {
            for (ForeignKey fk : foreignKeys) {
                if (deleteFk) {
                    for (Column column : columns) {
                        column.abandon();
                        RMBenchPlugin.getEventManager().fireColumnDeleted(
                                column.getTable(),
                                column,
                                true);
                    }
                }
                fk.abandon();
                RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this, fk);
            }

            table.setPrimaryKey(null);
            RMBenchPlugin.getEventManager().fireColumnsModified(table,
                    EventManager.Properties.COLUMN_PK, ((PrimaryKey) constraint).getColumns());
        }
        else if(constraint instanceof UniqueConstraint){
            table.removeUniqueConstraint((UniqueConstraint)constraint);
        }
        else if(constraint instanceof CheckConstraint){
            table.removeCheckConstraint((CheckConstraint)constraint);
        }
        else {
            throw new IllegalArgumentException();
        }
        RMBenchPlugin.getEventManager().fireConstraintDeleted(constraint);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(constraint instanceof PrimaryKey) {
            table.setPrimaryKey((PrimaryKey)constraint);
            RMBenchPlugin.getEventManager().fireColumnsModified(
                    table, EventManager.Properties.COLUMN_PK, ((PrimaryKey)constraint).getColumns());

            if (deleteFk) {
                for (Column column : columns) {
                    column.restore();
                    RMBenchPlugin.getEventManager().fireColumnAdded(column.getTable(), column, true);
                }
            }
            
            for (ForeignKey fk : foreignKeys) {
                fk.restore();
                RMBenchPlugin.getEventManager().fireForeignKeyAdded(this, fk);
            }
        }
        else if(constraint instanceof UniqueConstraint){
            table.addUniqueConstraint((UniqueConstraint)constraint);
        }
        else if(constraint instanceof CheckConstraint){
            table.addCheckConstraint((CheckConstraint)constraint);
        }
        else {
            throw new IllegalStateException();
        }
        RMBenchPlugin.getEventManager().fireConstraintAdded(constraint);
        return Status.OK_STATUS;
    }
}
