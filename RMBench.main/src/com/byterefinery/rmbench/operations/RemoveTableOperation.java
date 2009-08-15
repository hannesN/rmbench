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
 * $Id: RemoveTableOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;

/**
 * Operation that will remove a table from a diagram.
 * 
 * @author cse
 */
public class RemoveTableOperation extends RMBenchOperation {

    private final DTable dtable;
    private final Diagram diagram;
    
    public RemoveTableOperation(Diagram diagram, DTable dtable) {
        super(Messages.Operation_RemoveTable);
        
        this.dtable = dtable;
        this.diagram = diagram;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	diagram.removeTable(dtable);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	diagram.addTable(dtable);
        return Status.OK_STATUS;
    }
}
