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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.progress.IProgressService;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.ExceptionDialog;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDBAccess;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * an action that will execute the complete script
 * 
 * @author cse
 */
public class ExecuteScriptAction extends Action {
    
    public static final String ID = "ExecuteScriptAction";
    
    private final IDDLScriptContext ddlContext;
    
    public ExecuteScriptAction(IDDLScriptContext ddlContext) {
        super();
        
        this.ddlContext = ddlContext;
        setId(ID);
        setText(Messages.ExportEditor_executeScript);
        setToolTipText(Messages.ExportEditor_executeScript);
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.EXECUTE));
    }

    public void run() {
        final IDDLScriptContext.Statement[] statements = ddlContext.getAllStatements();
        if(statements.length == 0) {
            MessageDialog.openInformation(
                    ddlContext.getShell(),
                    Messages.ExportEditor_infoTitle, 
                    Messages.ExportEditor_noStmts);
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
            ExecRunnable execRunnable = new ExecRunnable(executor, statements);
            IProgressService progressService = 
                RMBenchPlugin.getDefault().getWorkbench().getProgressService();
            
            try {
                progressService.busyCursorWhile(execRunnable);
            }
            catch (Exception e) {
                RMBenchPlugin.logError(e);
            }
            if(execRunnable.errCount > 0) {
                MessageDialog.openError(
                        ddlContext.getShell(),
                        Messages.ExportEditor_infoTitle,
                        Messages.ExportEditor_executeError2);
            }
        }
    }

    /*
     * a runnable that does the statement execution proper
     */
    private class ExecRunnable implements IRunnableWithProgress {
        
        private final IDBAccess.Executor executor;
        private final IDDLScriptContext.Statement[] statements;
        
        public int errCount = 0;
        
        ExecRunnable(IDBAccess.Executor executor, IDDLScriptContext.Statement[] statements) {
            this.executor = executor;
            this.statements = statements;
        }
        
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            monitor.beginTask(Messages.ExecuteScript_Description, statements.length);
            for (int i = 0; i < statements.length && !monitor.isCanceled(); i++) {
                ddlContext.aboutToExecute(statements[i]);
                try {
                    executor.executeDDL(statements[i].text);
                    monitor.worked(1);
                } catch (SystemException e) {
                    errCount++;
                    RMBenchPlugin.logError(e.getCause());
                }
            }
            monitor.done();
        }
    }
}
