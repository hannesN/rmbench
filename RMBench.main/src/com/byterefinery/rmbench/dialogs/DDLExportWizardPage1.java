/*
 * created 29.11.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DDLGeneratorExtension;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;

/**
 * @author cse
 */
public class DDLExportWizardPage1 extends WizardPage {

    private final DBModel[] dbmodels;
    private final DDLGeneratorExtension[] generators;
    private final IDatabaseInfo dbInfo;
    
    DBModel dbmodel;
    IDDLGenerator generator;
    DDLGeneratorExtension generatorExtension;
    boolean generateDrop = true, generateDiff;
    
    ListViewer connectionsViewer;
    
    protected DDLExportWizardPage1(
            IDatabaseInfo dbInfo, DBModel[] dbmodels, DDLGeneratorExtension[] generators) {
        
        super("DDLExportWizardPage1");
        
        this.dbmodels = dbmodels;
        this.dbInfo = dbInfo;
        this.dbmodel = dbmodels.length > 0 ? dbmodels[0] : null;
        this.generators = generators;
        setGenerator(generators[0]);
        
        setTitle(Messages.DDLExportWizard_Title);
        setDescription(Messages.DDLExportWizard1_Description);
    }

    public void createControl(Composite parent) {
        
        final DDLExportWizard ddlWizard = (DDLExportWizard)getWizard();
        
        GridLayout layout;
        GridData gd;
        
        final Composite composite = new Composite(parent, SWT.NONE);
        layout = new GridLayout();
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Composite connectionGroup = new Composite(composite, SWT.NONE);
        connectionGroup.setLayout(new GridLayout());
        connectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Button diffCheck = new Button(connectionGroup, SWT.CHECK);
        diffCheck.setText(Messages.DDLExportWizard1_generateDiff_label);
        diffCheck.setSelection(generateDiff);
        diffCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(!generateDiff) {
                    if(dbmodels.length == 0) {
                        MessageDialog.openInformation(
                                getShell(), 
                                RMBenchMessages.ModelView_ExportDialog_Title,
                                Messages.DDLExportWizard1_NoConnection);
                        diffCheck.setSelection(false);
                        return;
                    }
                    else if(RMBenchPlugin.getLicenseManager().isUnlicensed()) {
                        MessageDialog.openInformation(
                                getShell(), 
                                RMBenchMessages.License_Title,
                                RMBenchMessages.License_FeatureNotAvailable);
                        diffCheck.setSelection(false);
                        return;
                    }
                }
                generateDiff = diffCheck.getSelection();
                connectionsViewer.getControl().setEnabled(generateDiff);
                checkCompleteState();
            }
        });
        
        connectionsViewer = new ListViewer(connectionGroup, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumHeight = convertHeightInCharsToPixels(4);
        connectionsViewer.getList().setLayoutData(gd);
        connectionsViewer.setContentProvider(new ArrayContentProvider());
        connectionsViewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                return ((DBModel)element).getName();
            }
        });
        connectionsViewer.setInput(dbmodels);
        if(dbmodel != null)
            connectionsViewer.setSelection(new StructuredSelection(dbmodel));
        connectionsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection)event.getSelection();
                dbmodel = (DBModel)selection.getFirstElement();
                checkCompleteState();
            }
        });
        connectionsViewer.getControl().setEnabled(generateDiff);
        
        final Button generateDropCheck = new Button(composite, SWT.CHECK);
        generateDropCheck.setText(Messages.DDLExportWizard1_generateDrop_text);
        generateDropCheck.setSelection(generateDrop);
        generateDropCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                generateDrop = generateDropCheck.getSelection();
            }
        });
        
        final Composite generatorGroup = new Composite(composite, SWT.NONE);
        generatorGroup.setLayout(new GridLayout());
        generatorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        final Label generatorLabel = new Label(generatorGroup, SWT.NONE);
        generatorLabel.setText(Messages.DDLExportWizard1_generator_label);
        generatorLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        
        final ComboViewer generatorsViewer = 
            new ComboViewer(generatorGroup, SWT.DROP_DOWN | SWT.READ_ONLY); 
        generatorsViewer.getCombo().setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));
        generatorsViewer.setContentProvider(new ArrayContentProvider());
        generatorsViewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                return ((DDLGeneratorExtension)element).getName();
            }
        });
        generatorsViewer.setInput(generators);
        generatorsViewer.setSelection(new StructuredSelection(generatorExtension));
        generatorsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection)event.getSelection();
                
                DDLGeneratorExtension genExt = (DDLGeneratorExtension)selection.getFirstElement();
                setGenerator(genExt);
                
                ddlWizard.setDDLGeneratorWizardPage(genExt.createGeneratorWizardPage(generator));
                checkCompleteState();
            }
        });
        
        setControl(composite);
        checkCompleteState();
    }
    
    private void setGenerator(DDLGeneratorExtension extension) {
        this.generatorExtension = extension;
        this.generator = extension.getDDLGenerator(dbInfo);
    }

    protected void checkCompleteState() {
        setPageComplete(
                ((generateDiff && dbmodel != null) || !generateDiff) && generatorExtension != null);
    }

    /**
     * @return the page for the currently selected generator 
     */
    public IWizardPage getGeneratorWizardPage() {
        return generatorExtension.createGeneratorWizardPage(generator);
    }

    public String getGeneratorName() {
        return generatorExtension.getName();
    }
    
    public IDDLFormatter getFormatter() {
        return generatorExtension.createFormatter(generator);
    }
    
    public IDDLScript getScript() {
        return generatorExtension.createScript(generator);
    }
}
