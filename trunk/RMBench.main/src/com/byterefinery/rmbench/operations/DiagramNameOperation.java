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
 * $Id: DiagramNameOperation.java 117 2006-01-23 23:35:28Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.diagram.Diagram;

/**
 * undoable operation for changing the diagram name
 * 
 * @author cse
 */
public class DiagramNameOperation extends RMBenchOperation {

    private Diagram diagram;
    private String oldName;
    private String newName;
    
    public DiagramNameOperation(Diagram diagram) {
        super(Messages.Operation_ChangeDiagramName);
        setSchema(diagram);
    }
    
    public DiagramNameOperation(Diagram diagram, String newName) {
        super(Messages.Operation_ChangeDiagramName);
        setSchema(diagram);
        setNewName(newName);
    }

    public void setSchema(Diagram diagram) {
        this.diagram=diagram;
        oldName = (diagram!=null) ? diagram.getName() : null;
    }
    
    public void setNewName(String newName) {
        this.newName=newName;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return setName(this, newName);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return setName(this, oldName);
    }

    private IStatus setName(Object eventSource, String name) {
        diagram.setName(name);
        RMBenchPlugin.getEventManager().fireDiagramModified(
                eventSource, EventManager.Properties.NAME, diagram);
        return Status.OK_STATUS;
    }
}
