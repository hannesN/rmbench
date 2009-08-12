/*
 * created 24-Feb-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id$
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.CheckConstraint;

public class ModifyCheckConstraintOperation extends RMBenchOperation {

    private CheckConstraint constraint;
    private String newName;
    private String newExpression;
    
    private String oldName;
    private String oldExpression;
    
    
    public ModifyCheckConstraintOperation(CheckConstraint constraint, String name, String expression) {
        super(Messages.Operation_ModifyCheckConstraint);
        this.constraint = constraint;
        this.newName = name;
        this.newExpression = expression;
        this.oldName = constraint.getName();
        this.oldExpression = constraint.getExpression();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        constraint.setName(newName);
        constraint.setExpression(newExpression);
        
        RMBenchPlugin.getEventManager().fireConstraintModified(constraint);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        constraint.setName(oldName);
        constraint.setExpression(oldExpression);
        
        RMBenchPlugin.getEventManager().fireConstraintModified(constraint);
        return Status.OK_STATUS;
    }

}
