/*
 * created 08.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: CollapseTableOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.model.diagram.DTable;

/**
 * operation that collpases the table figure and/or sets the collapsed state
 * of the DTable model object 
 * 
 * @author cse
 */
public class CollapseTableOperation extends RMBenchOperation {

    private final DTable dtable;
    private final boolean collapsed;
    
    public CollapseTableOperation(DTable table) {
        super(Messages.Operation_CollapseTable);
        
        this.dtable = table;
        this.collapsed = dtable.isCollapsed();
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        setCollapsed(!collapsed);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        setCollapsed(collapsed);
        return Status.OK_STATUS;
    }

    private void setCollapsed(boolean newCollapsed) {
        dtable.setCollapsed(newCollapsed);
    }
}
