/*
 * created 11.11.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: NewDiagramWizard.java 54 2005-11-26 12:31:49Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.operations.AddSchemaOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * Wizard for creating new diagrams
 * 
 * @author hannesn
 */
public class NewDiagramWizard extends Wizard {

    private NewDiagramWizardPage diagramPage;
    
    private Model model;
    
    public NewDiagramWizard(Model model) {
        super();
        this.model=model;
        setNeedsProgressMonitor(false);
        setWindowTitle(Messages.NewDiagramWizard_Title);
        setDefaultPageImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.MODEL_WIZARD));
    }
    
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        
    }

    public void addPages() {
        diagramPage = new NewDiagramWizardPage(model);
        addPage(diagramPage);

    }

    public boolean canFinish() {
        return diagramPage.isFinished();
    }

    public boolean performFinish() {
        Schema tmpSchema=model.getSchema(diagramPage.getSchemaName());
        
        if (tmpSchema==null) {
            AddSchemaOperation addSchemaoperation = new AddSchemaOperation(model, diagramPage.getSchemaName());
            addSchemaoperation.execute(this);
        }
        
        return true;
    }

    public String getDiagramName() {
        return diagramPage.getDiagramName();
    }
    
    public String getSchemaName() {
        return diagramPage.getSchemaName();
    }
}
