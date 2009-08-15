/*
 * created 31.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: RMBDragEditPartsTracker.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.DragEditPartsTracker;

import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.Messages;

/**
 * subclass that allows us to divert command execution to the global history
 * used by RMBenchPlugin
 * 
 * @author cse
 */
public class RMBDragEditPartsTracker extends DragEditPartsTracker {

    public RMBDragEditPartsTracker(EditPart sourceEditPart) {
        super(sourceEditPart);
    }

    protected void executeCommand(Command command) {
        CompoundCommand compound = (CompoundCommand)command;
        CompoundOperation operation = new CompoundOperation(Messages.Operation_Move, compound);
        operation.execute(null);
    }
}
