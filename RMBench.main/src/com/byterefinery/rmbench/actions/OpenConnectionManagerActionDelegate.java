/*
 * created 16-Mar-2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: OpenConnectionManagerActionDelegate.java 591 2006-11-24 20:47:02Z hannesn $
 */
/**
 * 
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.byterefinery.rmbench.dialogs.ConnectionDialog;


/**
 * action delegate that opens the JDBC connection manager dialog
 * 
 * @author Hannes Niederhausen
 */
public class OpenConnectionManagerActionDelegate extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;
    
    public void run(IAction action) {
        ConnectionDialog dlg = new ConnectionDialog(window.getShell());
        dlg.open();
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;        
    }
}
