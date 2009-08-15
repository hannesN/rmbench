/*
 * created 16.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ColumnDataTypeOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * @author cse
 */
public class ColumnDataTypeOperation extends ModifyColumnOperation {

    private final IDataType newDataType, oldDataType;
    
    public ColumnDataTypeOperation(Column column, IDataType dataType) {
        super(Messages.Operation_ModifyColumn, column);
        this.oldDataType = column.getDataType();
        this.newDataType = dataType;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setDataType(newDataType);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_DATATYPE, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_DATATYPE);
        setReferences(newDataType);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setDataType(oldDataType);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_DATATYPE, column);
        fireForeignKeyEvents(EventManager.Properties.COLUMN_DATATYPE);
        setReferences(oldDataType);
        return Status.OK_STATUS;
    }
    
    private void setReferences(IDataType dataType) {
        if (!column.belongsToPrimaryKey())
            return;
        int index = column.getTable().getPrimaryKey().getIndex(column);
        
        for (ForeignKey foreignKey : column.getTable().getReferences()) {
        	Column fkColumn = foreignKey.getColumn(index);
            fkColumn.setDataType(dataType);
            RMBenchPlugin.getEventManager().fireColumnModified(
                    fkColumn.getTable(), EventManager.Properties.COLUMN_DATATYPE, fkColumn);
        }
    }
}
