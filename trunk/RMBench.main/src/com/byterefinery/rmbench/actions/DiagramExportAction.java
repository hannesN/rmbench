/*
 * created 08.04.2006, cse
 *
 * Copyright 2005, 2006 DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.dialogs.export.ExportWizard;
import com.byterefinery.rmbench.editors.DiagramEditor;

/**
 * action that will open the diagram export wizard
 * 
 * @author cse
 */
public class DiagramExportAction extends Action {

	public static final String ID = "com.byterefinery.rmbench.DiagramExportAction";

	private final Shell shell;
	private final DiagramEditor diagramEditor;
	
	public DiagramExportAction(Shell shell, DiagramEditor diagramEditor) {
		super(Messages.DiagramExportAction_Title, AS_PUSH_BUTTON);
		
		this.shell = shell;
		this.diagramEditor = diagramEditor;
        setId(ID);
	}

	public void run() {
		ExportWizard wizard = new ExportWizard();
		wizard.initDiagramExport(diagramEditor.getDiagramPart());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();
	}
}
