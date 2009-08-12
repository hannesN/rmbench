package com.byterefinery.rmbench.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a wizard for creating a RMBench new model file
 */
public class NewModelWizard extends Wizard implements INewWizard {
    
    public static final String WIZARD_ID = "com.byterefinery.rmbench.wizards.NewModelWizard";
    
    private WizardNewFileCreationPage modelFilePage;
	private NewModelWizardPage1 modelPropertiesPage;
    
	private IStructuredSelection selection;

	public NewModelWizard() {
		super();
        setDefaultPageImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.MODEL_WIZARD));
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
        modelPropertiesPage = new NewModelWizardPage1();
        addPage(modelPropertiesPage);
        modelFilePage = new NewModelWizardPage2(selection);
        addPage(modelFilePage);
	}

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
	public boolean performFinish() {
		IFile result = modelFilePage.createNewFile();
        try {
            IDE.openEditor(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), result);
        }
        catch (PartInitException e) {
            RMBenchPlugin.logError(e);
        }
		return result != null;
	}
}