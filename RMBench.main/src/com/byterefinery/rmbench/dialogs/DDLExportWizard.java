/*
 * created 29.11.2005
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
package com.byterefinery.rmbench.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DDLGeneratorExtension;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a wizard for configuring DDL export to a database or SQL script. The
 * wizard contains one standard page, which allows selection of the target database
 * and the DDL generator. A configuration page for the DLL generator is shown 
 * depending on the selected generator
 * 
 * @author cse
 */
public class DDLExportWizard implements IWizard {

    private IWizardContainer wizardContainer;
    private IDialogSettings settings;
    
    private final DDLExportWizardPage1 page1;
    private IWizardPage generatorPage;
    
    /**
     * @param dbInfo the model database info
     * @param dbmodels all available connections for that database type
     * @param generators all applicable generators
     */
    public DDLExportWizard(IDatabaseInfo dbInfo, DBModel[] dbmodels, DDLGeneratorExtension[] generators) {
        
        this.page1 = new DDLExportWizardPage1(dbInfo, dbmodels, generators);
        this.page1.setWizard(this);
        initializeGeneratorPage(page1.getGeneratorWizardPage());
    }

    private void initializeGeneratorPage(IWizardPage generatorWizardPage) {
        generatorPage = generatorWizardPage;
        if(generatorPage != null) {
            generatorPage.setWizard(this);
            if(generatorPage.getTitle() == null)
                generatorPage.setTitle(Messages.DDLExportWizard_Title);
            if(generatorPage.getDescription() == null) {
                String desc = MessageFormat.format(
                        Messages.DDLExportWizard2_Description, new Object[]{page1.getGeneratorName()});
                generatorPage.setDescription(desc);
            }
        }
    }
    
    public DBModel getDBModel() {
        return page1.dbmodel;
    }
    
    public IDDLGenerator getGenerator() {
        return page1.generator;
    }

    public IDDLFormatter getFormatter() {
        return page1.getFormatter();
    }

    public IDDLScript getScript() {
        return page1.getScript();
    }
    
    public boolean getGenerateDiff() {
        return page1.generateDiff;
    }
    
    public boolean getGenerateDrop() {
        return page1.generateDrop;
    }
    
    /**
     * @param generatorWizardPage the new wizard page 
     */
    public void setDDLGeneratorWizardPage(IWizardPage generatorWizardPage) {
        if(generatorPage != null) {
            generatorPage.dispose();
        }
        initializeGeneratorPage(generatorWizardPage);
        wizardContainer.updateButtons();
    }

    public void addPages() {
    }
    
    public boolean performFinish() {
        return true;
    }
    
    public boolean canFinish() {
        return page1.isPageComplete() && (generatorPage == null || generatorPage.isPageComplete());
    }

    public void createPageControls(Composite pageContainer) {
        page1.createControl(pageContainer);
    }

    public void dispose() {
        page1.dispose();
        if(generatorPage != null)
            generatorPage.dispose();
    }

    public IWizardContainer getContainer() {
        return wizardContainer;
    }

    public Image getDefaultPageImage() {
        return RMBenchPlugin.getImage(ImageConstants.DDLEXPORT_WIZARD);
    }

    public IDialogSettings getDialogSettings() {
        return settings;
    }

    public IWizardPage getNextPage(IWizardPage page) {
        return page == page1 ? generatorPage : null;
    }

    public IWizardPage getPage(String pageName) {
        if(pageName.equals(page1.getName()))
            return page1;
        if(generatorPage != null && pageName.equals(generatorPage.getName()))
            return generatorPage;
        return null;
    }

    public int getPageCount() {
        return generatorPage != null ? 2 : 1;
    }

    public IWizardPage[] getPages() {
        if(generatorPage != null)
            return new IWizardPage[] {page1, generatorPage};
        else 
            return new IWizardPage[] {page1};
    }

    public IWizardPage getPreviousPage(IWizardPage page) {
        return page != null && page == generatorPage ? page1 : null;
    }

    public IWizardPage getStartingPage() {
        return page1;
    }

    public RGB getTitleBarColor() {
        return null;
    }

    public String getWindowTitle() {
        return Messages.DDLExportWizard_Title;
    }

    public boolean isHelpAvailable() {
        return false;
    }

    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    public boolean needsProgressMonitor() {
        return false;
    }

    public boolean performCancel() {
        return true;
    }

    public void setContainer(IWizardContainer wizardContainer) {
        this.wizardContainer = wizardContainer;
    }
}
