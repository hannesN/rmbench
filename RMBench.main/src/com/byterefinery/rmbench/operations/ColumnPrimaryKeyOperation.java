/*
 * created 16.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnPrimaryKeyOperation.java 280 2006-03-03 14:04:43Z hannesn $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * An operation for switching the primary key property of a column. If the column is added to the
 * primary key, the nullable property will also be set to <code>false</code>
 * @author cse
 */
public class ColumnPrimaryKeyOperation extends ModifyColumnOperation {

    private boolean isPrimaryKey;

    private PrimaryKey savedPrimaryKey;

    private ForeignKey savedReferences[];

    private Column referenceColumns[];

    private int index; // index of column in primary key

    private final boolean isNullable;

    public ColumnPrimaryKeyOperation(Column column) {
        super(Messages.Operation_ModifyColumn, column);
        this.isPrimaryKey = column.belongsToPrimaryKey();
        this.isNullable = column.getNullable();
        if (column.getTable().getReferences().size() > 0)
            savedReferences = (ForeignKey[]) column.getTable().getReferences().toArray(
                    new ForeignKey[column.getTable().getReferences().size()]);
        if (isPrimaryKey)
            index = column.getTable().getPrimaryKey().getIndex(column);
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (isPrimaryKey) {
            if (column.getTable().getPrimaryKey().size() == 1) {

                if (savedReferences != null) {
                    for (int i = 0; i < savedReferences.length; i++) {
                        savedReferences[i].abandon();
                        RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this,
                                savedReferences[i], null);
                    }
                }

                savedPrimaryKey = column.getTable().getPrimaryKey().abandon();
                RMBenchPlugin.getEventManager().fireConstraintDeleted(savedPrimaryKey);
            }
            else {
                if (savedReferences != null) {
                    referenceColumns = new Column[savedReferences.length];
                    // removing column from foreignkeys
                    for (int i = 0; i < savedReferences.length; i++) {
                        referenceColumns[i] = savedReferences[i].getColumn(index);
                        savedReferences[i].removeColumn(referenceColumns[i]);
                        referenceColumns[i].abandon();
                        RMBenchPlugin.getEventManager().fireForeignKeyModified(null,
                                savedReferences[i]);
                    }
                }

                column.removeFromPrimaryKey();
                RMBenchPlugin.getEventManager().fireConstraintModified(
                        column.getTable().getPrimaryKey());
            }
        }
        else {
            column.setNullable(false);
            Table table = column.getTable();
            if (table.getPrimaryKey() == null) {
                Model model = RMBenchPlugin.getActiveModel();
                String name = model.getNameGenerator().generatePrimaryKeyName(model.getIModel(),
                        table.getITable());
                savedPrimaryKey = new PrimaryKey(name, new Column[] { column }, column.getTable());

                RMBenchPlugin.getEventManager().fireConstraintAdded(savedPrimaryKey);
            }
            else {
                Model model = RMBenchPlugin.getActiveModel();
                column.addToPrimaryKey();
                RMBenchPlugin.getEventManager().fireConstraintModified(table.getPrimaryKey());

                if (savedReferences != null) {
                    referenceColumns = new Column[savedReferences.length];
                    for (int i = 0; i < savedReferences.length; i++) {
                        String columnName = model.getNameGenerator().generateForeignKeyColumnName(
                                model.getIModel(), column.getTable().getITable(),
                                column.getTable().getPrimaryKey().getIPrimaryKey(),
                                column.getIColumn());
                        referenceColumns[i] = new Column(savedReferences[i].getTable(), columnName,
                                column.getDataType());
                        savedReferences[i].addColumn(referenceColumns[i]);
                        RMBenchPlugin.getEventManager().fireForeignKeyModified(null,
                                savedReferences[i]);
                    }
                }

            }
        }
        RMBenchPlugin.getEventManager().fireColumnModified(column.getTable(),
                EventManager.Properties.COLUMN_PK, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_PK);

        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (isPrimaryKey) {
            if (savedPrimaryKey != null) {

                savedPrimaryKey.restore();
                RMBenchPlugin.getEventManager().fireConstraintAdded(savedPrimaryKey);

                if (savedReferences != null) {
                    for (int i = 0; i < savedReferences.length; i++) {
                        savedReferences[i].restore();
                        RMBenchPlugin.getEventManager().fireForeignKeyAdded(this,
                                savedReferences[i], null);
                    }
                }

            }
            else {
                column.addToPrimaryKey();
                RMBenchPlugin.getEventManager().fireConstraintModified(
                        column.getTable().getPrimaryKey());

                if (savedReferences != null) {
                    for (int i = 0; i < savedReferences.length; i++) {
                        referenceColumns[i].restore();
                        savedReferences[i].addColumn(referenceColumns[i], index);
                        RMBenchPlugin.getEventManager().fireForeignKeyModified(null,
                                savedReferences[i]);
                    }
                }

            }
        }
        else {
            column.setNullable(isNullable);
            if (savedPrimaryKey != null) {
                savedPrimaryKey.abandon();
                RMBenchPlugin.getEventManager().fireConstraintDeleted(savedPrimaryKey);
            }
            else {
                column.removeFromPrimaryKey();
                RMBenchPlugin.getEventManager().fireConstraintModified(
                        column.getTable().getPrimaryKey());
                if (savedReferences != null) {
                    for (int i = 0; i < savedReferences.length; i++) {
                        savedReferences[i].removeColumn(referenceColumns[i]);
                        referenceColumns[i].abandon();
                        RMBenchPlugin.getEventManager().fireForeignKeyModified(null,
                                savedReferences[i]);
                    }
                }
            }
        }
        RMBenchPlugin.getEventManager().fireColumnModified(column.getTable(),
                EventManager.Properties.COLUMN_PK, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_PK);

        return Status.OK_STATUS;
    }
}
