/*
 * created 25.08.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  $Id$
 */
package com.byterefinery.rmbench.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressService;

import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * This abstract class wrapps the code needed for creating a job using the 
 * ProgressMonitorService of eclipse.
 * 
 * 
 * 
 * @author Hannes Niederhausen
 *
 */
public abstract class UIRunnableWithProgress implements IRunnableWithProgress {

    //not currently used
    protected boolean showDialog;
    
    public UIRunnableWithProgress() {
        this.showDialog = false;
    }
    
    public UIRunnableWithProgress(boolean showProgressDialog) {
        this.showDialog = showProgressDialog;
    }
    
    /**
     * The method which will be valled by the job.
     * 
     * Don't call this on your own. Use <code>start()</code> instead.
     * 
     * @param monitor the progress monitor
     */
    public abstract void run(IProgressMonitor monitor);
    
    public void start() {
        IProgressService progressService = RMBenchPlugin.getDefault().getWorkbench()
                .getProgressService();
        
        IRunnableContext context;
        if(showDialog)
            context = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        else
            context = RMBenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        
        try {
            progressService.runInUI(context, this, null);
        }
        catch (InvocationTargetException e) {
            RMBenchPlugin.logError(e);
        }
        catch (InterruptedException e) {
            RMBenchPlugin.logError(e);
        }
    }

}
