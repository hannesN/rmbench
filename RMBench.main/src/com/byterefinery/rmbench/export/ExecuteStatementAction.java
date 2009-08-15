/*
 * created 14.01.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.ExceptionDialog;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDBAccess;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * an action that executes the currently selected statement, i.e. the statement under 
 * the cursor
 * 
 * @author cse
 */
public class ExecuteStatementAction extends Action {
    
    public static final String ID = "ExecuteStatementAction";
    
    private final IDDLScriptContext ddlContext;
    
    public ExecuteStatementAction(IDDLScriptContext ddlContext) {
        super();
        
        this.ddlContext = ddlContext;
        setId(ID);
        setText(Messages.ExportEditor_executeStmt);
        setToolTipText(Messages.ExportEditor_executeStmt);
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.EXECUTE_CARET));
    }

    public void run() {
        IDDLScriptContext.Statement statement = ddlContext.getSelectedStatement();
        if(statement == null) {
            MessageDialog.openInformation(
                    ddlContext.getShell(),
                    Messages.ExportEditor_infoTitle, 
                    Messages.ExportEditor_noStmt);
            return;
        }
        IDBAccess.Executor executor = null;
        try {
            executor = ddlContext.getSelectedDBModel().getExecutor(ddlContext.getShell());
        }
        catch (SystemException e) {
            ExceptionDialog.openError(
                    ddlContext.getShell(), 
                    Messages.ExportEditor_connectError, 
                    e.getStatus(Messages.ExportEditor_connectError));
            RMBenchPlugin.logError(e.getCause());
        }
        if(executor != null) {
            ExecRunnable execRunnable = new ExecRunnable(executor, statement);
            BusyIndicator.showWhile(Display.getCurrent(), execRunnable);
            if(execRunnable.error != null) {
                ExceptionDialog.openError(
                        ddlContext.getShell(), 
                        Messages.ExportEditor_executeError, 
                        execRunnable.error.getStatus(Messages.ExportEditor_executeError));
            }
        }
    }
    
    /*
     * a runnable that does the statement execution proper
     */
    private class ExecRunnable implements Runnable {
        
        private final IDBAccess.Executor executor;
        private final IDDLScriptContext.Statement statement;
        
        public SystemException error;
        
        ExecRunnable(IDBAccess.Executor executor, IDDLScriptContext.Statement statement) {
            this.executor = executor;
            this.statement = statement;
        }
        
        public void run() {
            IProgressMonitor monitor = ddlContext.getProgressMonitor();
            monitor.beginTask(Messages.ExecuteScript_Description, 1);
            ddlContext.aboutToExecute(statement);
            try {
                executor.executeDDL(statement.text);
            } catch (SystemException e) {
                error = e;
                RMBenchPlugin.logError(error.getCause());
            }
            ddlContext.executed(statement, error);
            monitor.done();
        }
    }
}
