/*
 * created 15.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: AddColumnOperation.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * undoable operation for adding a column to a table
 * 
 * @author cse
 */
public class AddColumnOperation extends RMBenchOperation {

	private final Table table;
    private final String columnName;
    private final IDataType dataType;
    private final boolean nullable;
    private final String defaultValue;
    private final String comment;
    private final int position;
    	
    private Column column;
    
    public AddColumnOperation(Table table) {
        super(Messages.Operation_AddColumn);
        this.table = table;
        
        Model model = RMBenchPlugin.getActiveModel();
        this.columnName = model.getNameGenerator().generateColumnName(model.getIModel(), table.getITable());
        this.dataType = table.getSchema().getDatabaseInfo().getDefaultDataType();
        this.nullable = true;
        this.defaultValue = null;
        this.comment = null;
        this.position = RMBenchConstants.UNSPECIFIED_POSITION;
    }

    public AddColumnOperation(
    		Table table, 
    		String columnName, 
    		IDataType dataType, 
    		boolean nullable, 
    		String defaultValue, 
    		String comment, 
    		int position) {
    	
        super(Messages.Operation_AddColumn);
        this.table = table;
        
        this.columnName = columnName;
        this.dataType = dataType;
        this.nullable = nullable;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.position = position;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        column = new Column(table, columnName, dataType, nullable, defaultValue, comment, position);
        RMBenchPlugin.getEventManager().fireColumnAdded(table, column);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        table.removeColumn(column);
        RMBenchPlugin.getEventManager().fireColumnDeleted(table, column);
        return Status.OK_STATUS;
    }
}
