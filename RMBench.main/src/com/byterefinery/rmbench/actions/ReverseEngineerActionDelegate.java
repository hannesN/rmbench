/*
 * created 11.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ReverseEngineerActionDelegate.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.views.db.ImportView;
import com.byterefinery.rmbench.views.dbtable.DBTableView;

/**
 * Menu action for the workbench menu which activates the database reverse engineering
 * (aka metadata import) tools
 * 
 * @author cse
 */
public class ReverseEngineerActionDelegate implements IWorkbenchWindowActionDelegate {

    IWorkbenchWindow workbenchWindow;
    
    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.workbenchWindow = window;
    }

    public void run(IAction action) {
        try {
            DBTableView tableView = 
                (DBTableView)workbenchWindow.getActivePage().showView(DBTableView.VIEW_ID);
            ImportView importView = 
                (ImportView)workbenchWindow.getActivePage().showView(ImportView.VIEW_ID);
            importView.setTableView(tableView);
        }
        catch (PartInitException e) {
            RMBenchPlugin.logError(e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
