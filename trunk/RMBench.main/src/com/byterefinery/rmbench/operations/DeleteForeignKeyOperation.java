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
 * $Id: DeleteForeignKeyOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * @author cse
 */
public class DeleteForeignKeyOperation extends RMBenchOperation {

    private final ForeignKey foreignKey;
    
    public DeleteForeignKeyOperation(ForeignKey foreignKey) {
        super(Messages.Operation_DeleteForeignKey);
        this.foreignKey = foreignKey;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        foreignKey.abandon();
        RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this, foreignKey, null);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        foreignKey.restore();
        RMBenchPlugin.getEventManager().fireForeignKeyAdded(this, foreignKey, null);
        return Status.OK_STATUS;
    }
}
