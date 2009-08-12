/*
 * created 23.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: AddTableConstraintOperation.java 274 2006-03-01 13:36:12Z cse $
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
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;

/**
 * undoable operation for addding a table constraint
 * @author cse
 */
public class AddTableConstraintOperation extends RMBenchOperation {

    private final Table table;
    private final String constraintType;
    private final String constraintName;
    private final Object constraintArg;
    
    private Constraint constraint;
    
    public AddTableConstraintOperation(
            Table table, String constraintType, String constraintName, Object constraintArg) {
        super(Messages.Operation_AddConstraint);
        this.table = table;
        this.constraintType = constraintType;
        this.constraintName = constraintName;
        this.constraintArg = constraintArg;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(constraint != null)
            restoreConstraint();
        else
            createConstraint();
        
        RMBenchPlugin.getEventManager().fireConstraintAdded(constraint);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if(CheckConstraint.CONSTRAINT_TYPE == constraintType) {
            table.removeCheckConstraint((CheckConstraint)constraint);
        }
        else if(UniqueConstraint.CONSTRAINT_TYPE == constraintType) {
            table.removeUniqueConstraint((UniqueConstraint)constraint);
        }
        else {
            table.setPrimaryKey(null);
            fireColumnEvents();
        }
        RMBenchPlugin.getEventManager().fireConstraintDeleted(constraint);
        return Status.OK_STATUS;
    }
    
    private void createConstraint() {
        if(CheckConstraint.CONSTRAINT_TYPE == constraintType) {
            String name = constraintName != null ? constraintName : generateCheckConstraintName();
            constraint = new CheckConstraint(name, (String)constraintArg, table);
            table.addCheckConstraint((CheckConstraint)constraint);
        }
        else if(UniqueConstraint.CONSTRAINT_TYPE == constraintType) {
            String name = constraintName != null ? constraintName : generateUniqueConstraintName();
            constraint = new UniqueConstraint(name, (Column[])constraintArg, table);
            table.addUniqueConstraint((UniqueConstraint)constraint);
        }
        else if(PrimaryKey.CONSTRAINT_TYPE == constraintType) {
            String name = constraintName != null ? constraintName : generatePKName();
            constraint = new PrimaryKey(name, (Column[])constraintArg, table);
            fireColumnEvents();
        }
        else 
            throw new IllegalArgumentException();
    }

    private void restoreConstraint() {
        if(CheckConstraint.CONSTRAINT_TYPE == constraintType) {
            table.addCheckConstraint((CheckConstraint)constraint);
        }
        else if(UniqueConstraint.CONSTRAINT_TYPE == constraintType) {
            table.addUniqueConstraint((UniqueConstraint)constraint);
        }
        else {
            table.setPrimaryKey((PrimaryKey)constraint);
            fireColumnEvents();
        }
    }

    private String generateCheckConstraintName() {
        Model model = RMBenchPlugin.getActiveModel();
        return model.getNameGenerator().generateCheckConstraintName(
                model.getIModel(), table.getITable());
    }

    private String generateUniqueConstraintName() {
        Model model = RMBenchPlugin.getActiveModel();
        return model.getNameGenerator().generateUniqueConstraintName(
                model.getIModel(), table.getITable());
    }

    private String generatePKName() {
        Model model = RMBenchPlugin.getActiveModel();
        return model.getNameGenerator().generatePrimaryKeyName(
                model.getIModel(), table.getITable());
    }

    private void fireColumnEvents() {
        Column[] columns = ((PrimaryKey)constraint).getColumns();
        
        RMBenchPlugin.getEventManager().fireColumnsModified(
                table, EventManager.Properties.COLUMN_PK, columns);
    }
}
