/*
 * created 16-Mar-2006
 *
 * Copyright 2006, DynaBEAN Consulting
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
