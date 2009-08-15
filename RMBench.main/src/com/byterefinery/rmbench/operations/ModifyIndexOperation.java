/*
 * created 07.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ModifyIndexOperation.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Index;

/**
 * undoable operation for editing an index
 * 
 * @author cse
 */
public class ModifyIndexOperation extends RMBenchOperation {

    private final Index index;
    private final String name, oldName;
    private final Column[] columns, oldColumns;
    private final boolean unique, oldUnique;
    private final boolean[] ascvalues, oldAscvalues;
    
    public ModifyIndexOperation(
            Index index, 
            String name, 
            Column[] columns, 
            boolean unique, 
            boolean[] ascvalues) {
        
        super(Messages.Operation_EditIndex);
        this.index = index;
        
        this.name = name;
        this.columns = columns;
        this.unique = unique;
        this.ascvalues = ascvalues;
        
        this.oldName = index.getName();
        this.oldColumns = index.getColumns();
        this.oldUnique = index.isUnique();
        this.oldAscvalues = index.getAscending();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        index.setValues(name, columns, unique, ascvalues);
        RMBenchPlugin.getEventManager().fireIndexModified(index.getTable(), null, index);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        index.setValues(oldName, oldColumns, oldUnique, oldAscvalues);
        RMBenchPlugin.getEventManager().fireIndexModified(index.getTable(), null, index);
        return Status.OK_STATUS;
    }
}
