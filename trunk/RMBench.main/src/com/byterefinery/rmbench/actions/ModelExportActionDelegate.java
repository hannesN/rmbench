/*
 * created 08.04.2006, cse
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
package com.byterefinery.rmbench.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.export.ExportWizard;
import com.byterefinery.rmbench.external.IExportable;

/**
 * action delegate that opens the model export wizard
 * 
 * @author cse
 */
public class ModelExportActionDelegate implements
		IWorkbenchWindowActionDelegate {

    IWorkbenchWindow workbenchWindow;
    IExportable.ModelExport modelExport;
    
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.workbenchWindow = window;
		modelExport = RMBenchPlugin.getDefault().getModelExport();
	}

	public void run(IAction action) {
		ExportWizard wizard = new ExportWizard();
		wizard.initModelExport(modelExport);
		WizardDialog dialog = new WizardDialog(workbenchWindow.getShell(), wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		modelExport = RMBenchPlugin.getDefault().getModelExport();
		action.setEnabled(modelExport != null);
	}
}
