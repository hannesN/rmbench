/*
 * created 31.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
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
