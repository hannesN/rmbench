/*
 * created 20.02.2006
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
package com.byterefinery.rmbench.dialogs.export;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.Messages;
import com.byterefinery.rmbench.editors.DiagramEditor;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * wizard that implements the export functionality for RMBench. The general export types
 * are
 * <ul>
 * <li>image export for diagrams</li>
 * <li>model export</li>
 * </ul>
 * Model exports can be added through extension points. The wizard dynamically displays the
 * configuration pages for the currently selected model export
 * 
 * @author cse
 */
public class ExportWizard implements IExportWizard {

    private final String windowTitle = Messages.ExportWizard_mainTitle;
    private final ImageDescriptor defaultImageDescriptor = RMBenchPlugin.getImageDescriptor(ImageConstants.MODEL_WIZARD);
    
    private IWizardContainer container;
    
    private Image defaultImage;
    
    private IWizardPage[] pages;
    private IWizardPage[] modelPages;
    
    private Set<IWizardPage> pageDump = new HashSet<IWizardPage>();
    
    private NoExportPage noExportPage;
    private ExportSelectionPage exportSelectionPage;
    private ExportFilePage imageExportPage;
    private ExportDirectoryPage modelExportPage;
    
    private IExportable.DiagramExport diagramExport;
    private IExportable.ModelExport modelExport;
    
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        if(!selection.isEmpty()) {
            IExportable exportable = (IExportable)selection.getFirstElement();
            diagramExport = exportable.getDiagramExport();
            modelExport = exportable.getModelExport();
        }
        if(diagramExport == null) {
            IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
            if(ww != null && ww.getActivePage() != null) {
                IEditorPart ep = ww.getActivePage().getActiveEditor();
                if(ep instanceof DiagramEditor) {
                    DiagramEditor de = (DiagramEditor)ep;
                    diagramExport = de.getDiagramPart();
                }
            }
        }
        if(modelExport == null)
        	modelExport = RMBenchPlugin.getDefault().getModelExport();
    }
    
	public void initDiagramExport(DiagramEditPart diagramPart) {
        diagramExport = diagramPart.getDiagramExport();
	}
	
	public void initModelExport(IExportable.ModelExport modelExport) {
		this.modelExport = modelExport;
	}
	
    public void addPages() {
    	if(diagramExport == null && modelExport == null) {
            noExportPage = NoExportPage.noExport();
            noExportPage.setWizard(this);
    	}
    	else {
            exportSelectionPage = new ExportSelectionPage(diagramExport, modelExport);
            exportSelectionPage.setWizard(this);
            if(diagramExport != null) {
                imageExportPage = new ExportFilePage();
                imageExportPage.setWizard(this);
            }
            if(modelExport != null) {
                modelExportPage = new ExportDirectoryPage();
                modelExportPage.setWizard(this);
            }
    	}
    }

    public boolean performFinish() {
        if(exportSelectionPage.isImageExport()) {
            IFile imageFile = imageExportPage.createImageFile(
                    exportSelectionPage.getImageExporter(), 
                    exportSelectionPage.getExportFigure());
            return imageFile != null;
        }
        else {
            try {
                exportSelectionPage.getModelExporter().export(modelExportPage.getDirectory());
            }
            catch (IOException e) {
                RMBenchPlugin.logError(e);
                return false;
            }
            return true;
        }
    }
    
    public boolean canFinish() {
        computePagesIfNeeded();
        for (int i = 0; i < pages.length; i++) {
            if(!pages[i].isPageComplete())
                return false;
        }
        return true;
    }

    public void createPageControls(Composite pageContainer) {
        
        if(noExportPage != null)
            noExportPage.createControl(pageContainer);
        else {
            if(exportSelectionPage != null)
                exportSelectionPage.createControl(pageContainer);
            if(imageExportPage != null) {
                imageExportPage.createControl(pageContainer);
                imageExportPage.getControl().setVisible(false);
            }
            if(modelExportPage != null) {
                modelExportPage.createControl(pageContainer);
                modelExportPage.getControl().setVisible(false);
            }
        }
    }

    public void dispose() {
        if(noExportPage != null)
            noExportPage.dispose();
        else {
            if(exportSelectionPage != null)
                exportSelectionPage.dispose();
            if(imageExportPage != null)
                imageExportPage.dispose();
            if(modelExportPage != null)
                modelExportPage.dispose();
        }
        if(modelPages != null) {
            for (int i = 0; i < modelPages.length; i++) {
                pageDump.add(modelPages[i]);
            }
        }
        for (IWizardPage page : pageDump) {
            page.dispose();
        }
        if(defaultImage != null)
            defaultImage.dispose();
    }

    public IWizardContainer getContainer() {
        return container;
    }

    public Image getDefaultPageImage() {
        if(defaultImage == null)
            defaultImage = defaultImageDescriptor.createImage(true);
        return defaultImage;
    }

    public IDialogSettings getDialogSettings() {
        return null;
    }

    public IWizardPage getNextPage(IWizardPage page) {
        if(page == noExportPage)
            return null;
        else if(page == exportSelectionPage) {
            if(modelPages != exportSelectionPage.getModelPages()) {
                replaceModelPages(exportSelectionPage.getModelPages());
            }
            if(modelPages != null)
                return modelPages[0];
            else {
                if(exportSelectionPage.isImageExport())
                    return imageExportPage;
                else
                    return modelExportPage;
            }
        }
        else if(modelPages != null) {
            for (int i = 0; i < modelPages.length; i++) {
                if(page == modelPages[i])
                    return i < modelPages.length - 1 ? modelPages[i+1] : modelExportPage;
            }
        }
        return null;
    }

    public IWizardPage getPreviousPage(IWizardPage page) {
        if(page == modelExportPage) {
            if(modelPages != null)
                return modelPages[modelPages.length - 1];
            else 
                return exportSelectionPage;
        }
        else if(page == imageExportPage)
            return exportSelectionPage;
        else if(modelPages != null) {
            for (int i = 0; i < modelPages.length; i++) {
                if(page == modelPages[i])
                    return i > 0 ? modelPages[i-1] : exportSelectionPage;
            }
        }
        return null;
    }

    private void replaceModelPages(IWizardPage[] newPages) {
        pages = null;
        if(modelPages != null) {
            for (int i = 0; i < modelPages.length; i++) {
                pageDump.add(modelPages[i]);
                if(modelPages[i].getControl() != null)
                    modelPages[i].getControl().setVisible(false);
            }
        }
        modelPages = newPages;
        if(modelPages != null) {
            for (int i = 0; i < newPages.length; i++) {
                modelPages[i].setWizard(this);
            }
        }
    }

    public IWizardPage getPage(String pageName) {
        computePagesIfNeeded();
        for (int i = 0; i < pages.length; i++) {
            if(pages[i].getName().equals(pageName))
                return pages[i];
        }
        return null;
    }

    public int getPageCount() {
        return getPages().length;
    }

    public IWizardPage[] getPages() {
        computePagesIfNeeded();
        return pages;
    }

    private void computePagesIfNeeded() {
        if(pages != null)
            return;
        
        if(noExportPage != null) {
            pages = new IWizardPage[]{noExportPage};
            return;
        }
        
        if(modelPages != null) {
            pages = new IWizardPage[modelPages.length + 2];
            pages[0] = exportSelectionPage;
            System.arraycopy(modelPages, 0, pages, 1, modelPages.length);
        }
        else {
            pages = new IWizardPage[2];
            pages[0] = exportSelectionPage;
        }
        if(exportSelectionPage.isImageExport())
            pages[pages.length - 1] = imageExportPage; 
        else 
            pages[pages.length - 1] = modelExportPage;
    }

    public IWizardPage getStartingPage() {
        return getPages()[0];
    }

    public RGB getTitleBarColor() {
        return null;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public boolean isHelpAvailable() {
        return false;
    }

    public boolean needsPreviousAndNextButtons() {
        return getPageCount() > 1;
    }

    public boolean needsProgressMonitor() {
        return false;
    }

    public boolean performCancel() {
        return true;
    }

    public void setContainer(IWizardContainer wizardContainer) {
        this.container = wizardContainer;
    }
}
