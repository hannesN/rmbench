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
 * $Id: AddForeignKeyOperation.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * operation that will create a foreign key relationship between two tables. The 
 * underlying columns can be set programatically or will be created implicitly
 * 
 * @author cse
 */
public class AddForeignKeyOperation extends RMBenchOperation {

    private final Table  sourceTable;
    private Table targetTable;

    private ForeignKey foreignKey;
    private boolean generateKeyColumns;
    private Column[] foreignKeyColumns;
    
    public AddForeignKeyOperation(Table sourceTable) {
        super(Messages.Operation_AddForeignKey);
        this.sourceTable = sourceTable;
        this.generateKeyColumns = true;
    }

    /**
     * @param targetTable the table this foreign key points to
     */
    public void setTargetTable(Table  targetTable) {
        this.targetTable = targetTable;
    }
    
    /**
     * @param columns the columns that are to form the foreign key. If not set, 
     * columns will be generated
     */
    public void setColumns(Column[] columns) {
        foreignKeyColumns = columns;
        generateKeyColumns = false;
    }
    
    public boolean canExecute() {
        return targetTable != null && targetTable.getPrimaryKey() != null;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        Model model = RMBenchPlugin.getActiveModel();
        if(generateKeyColumns)
            foreignKeyColumns = createForeignKeyColumns(model, targetTable.getPrimaryKey());
        
        INameGenerator nameGenerator = model.getNameGenerator();
        foreignKey = new ForeignKey(
                nameGenerator.generateForeignKeyName(model.getIModel(), sourceTable.getITable()), 
                foreignKeyColumns, 
                sourceTable, 
                targetTable);
        
        RMBenchPlugin.getEventManager().fireForeignKeyAdded(
        		this, foreignKey, generateKeyColumns ? foreignKeyColumns : null);
        return Status.OK_STATUS;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(generateKeyColumns) {
            for (int i = 0; i < foreignKeyColumns.length; i++) {
                foreignKeyColumns[i].restore();
            }
        }
        foreignKey.restore();
        RMBenchPlugin.getEventManager().fireForeignKeyAdded(
        		this, foreignKey, generateKeyColumns ? foreignKeyColumns : null);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(generateKeyColumns) {
            for (int i = 0; i < foreignKeyColumns.length; i++) {
                foreignKeyColumns[i].abandon();
            }
        }
        foreignKey.abandon();
        RMBenchPlugin.getEventManager().fireForeignKeyDeleted(
        		this, foreignKey, generateKeyColumns ? foreignKeyColumns : null);
        return Status.OK_STATUS;
    }

    /*
     * generate foreign key columns, using the appropriate name generator
     */
    private Column[] createForeignKeyColumns(Model model, PrimaryKey targetKey) {
        
        Column[] targetColumns = targetKey.getColumns();
        Column[] result = new Column[targetColumns.length];
        
        for (int i = 0; i < targetColumns.length; i++) {
            String columnName = 
                model.getNameGenerator().generateForeignKeyColumnName(
                        model.getIModel(), 
                        sourceTable.getITable(), 
                        targetKey.getIPrimaryKey(), 
                        targetColumns[i].getIColumn());
            result[i] = new Column(sourceTable, columnName, targetColumns[i].getDataType());
        }
        return result;
    }
}
