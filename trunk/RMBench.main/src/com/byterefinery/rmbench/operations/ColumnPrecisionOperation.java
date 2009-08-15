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
 * $Id: ColumnPrecisionOperation.java 665 2007-09-29 15:31:59Z cse $
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
 * Operation for changing the size/precision of a column
 * @author cse
 */
public class ColumnPrecisionOperation extends ModifyColumnOperation {

    private final long oldPrecision, newPrecision;
    
    public ColumnPrecisionOperation(Column column, long newPrecision) {
        super(Messages.Operation_ModifyColumn, column);
        this.oldPrecision = column.getSize();
        this.newPrecision = newPrecision;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setSize(newPrecision);
        updateForeignKeys(newPrecision);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_PRECISION, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_PRECISION);
        fireReferencesEvents(EventManager.Properties.COLUMN_PRECISION);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setSize(oldPrecision);
        updateForeignKeys(oldPrecision);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_PRECISION, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_PRECISION);
        fireReferencesEvents(EventManager.Properties.COLUMN_PRECISION);
        return Status.OK_STATUS;
    }
    
    private void updateForeignKeys(long precision) {
        // setting referring foreignkeys
        if (column.belongsToPrimaryKey()) {
            int keyIndex=column.getTable().getPrimaryKey().getIndex(column);
            for (ForeignKey foreignKey : column.getTable().getReferences()) {
                foreignKey.getColumn(keyIndex).setSize(precision);
            }
        }
    }
}
