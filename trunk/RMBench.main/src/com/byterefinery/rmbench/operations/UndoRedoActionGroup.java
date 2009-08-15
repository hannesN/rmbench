/*
 * created 29.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: UndoRedoActionGroup.java 140 2006-01-30 11:15:37Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

/**
 * copied from  {@link org.eclipse.ui.operations.UndoRedoActionGroup}, and added 
 * fillContextMenu method. Had to do this because UndoRedoActionGroup is final,
 * and we need access to instance variables
 *  
 * @author cse
 * @see org.eclipse.ui.operations.UndoRedoActionGroup
 */
public final class UndoRedoActionGroup extends ActionGroup {

    private UndoActionHandler undoActionHandler;

    private RedoActionHandler redoActionHandler;

    /**
     * Construct an undo redo action group for the specified workbench part
     * site, using the specified undo context.
     * 
     * @param site
     *            the workbench part site that is creating the action group
     * @param undoContext
     *            the undo context to be used for filtering the operation
     *            history
     * @param pruneHistory
     *            a boolean that indicates whether the history for the specified
     *            context should be pruned whenever an invalid operation is
     *            encountered.
     */
    public UndoRedoActionGroup(IWorkbenchPartSite site,
            IUndoContext undoContext, boolean pruneHistory) {

        // create the undo action handler
        undoActionHandler = new UndoActionHandler(site, undoContext);
        undoActionHandler.setPruneHistory(pruneHistory);

        // create the redo action handler
        redoActionHandler = new RedoActionHandler(site, undoContext);
        redoActionHandler.setPruneHistory(pruneHistory);
    }

    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        if (undoActionHandler != null) {
            actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
                    undoActionHandler);
            actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
                    redoActionHandler);
        }
    }

    public void fillContextMenu(IMenuManager menu) {
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, undoActionHandler);
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, redoActionHandler);
    }
}
