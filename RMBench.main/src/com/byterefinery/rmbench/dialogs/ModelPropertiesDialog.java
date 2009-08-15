/*
 * created 10.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ModelPropertiesDialog.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.NameGeneratorExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.model.Model;

/**
 * dialog to edit model properties like name & database. Modifies the model if OK is pressed
 * 
 * @author cse
 */
public class ModelPropertiesDialog extends Dialog {

    //TODO V1: better get this from the database
    private static final int MODEL_NAME_CHARS = 30;

    private final Model model;
    private final String storageKey;
    
    private String modelName;
    
    private DatabaseExtension databaseExtension;
    private NameGeneratorExtension generatorExtension;
    
    public ModelPropertiesDialog(Shell parentShell, Model model, String storageKey) {
        super(parentShell);
        this.model = model;
        this.storageKey = storageKey;
        this.modelName = model.getName();
        this.databaseExtension = RMBenchPlugin.getExtensionManager().getDatabaseExtension(model.getDatabaseInfo());
        this.generatorExtension = RMBenchPlugin.getExtensionManager().getNameGeneratorExtension(model.getNameGenerator());
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ModelPropertiesDialog_Title);
    }
    
    protected Control createDialogArea(Composite parent) {
        
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        
        final Composite localComposite = new Composite(mainComposite, SWT.NONE);
        localComposite.setLayout(new GridLayout(2, false));
        
        Label label;
        GridData gd;
        
        if(storageKey != null) {
            Composite storageComposite = new Composite(localComposite, SWT.NONE);
            gd = new GridData(SWT.FILL, SWT.NONE, true, false);
            gd.horizontalSpan = 2;
            storageComposite.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            storageComposite.setLayout(layout);
            
            label = new Label(storageComposite, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BOTTOM, false, false));
            label.setText(Messages.ModelPropertiesDialog_Location+" "+storageKey);
            
            label = new Label(storageComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
            gd = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
        }
        
        label = new Label(localComposite, SWT.NONE);
        label.setText(Messages.ModelPropertiesDialog_Name);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        final Text nameText = new Text(localComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.LEFT, SWT.FILL, false, true);
        gd.widthHint = convertWidthInCharsToPixels(MODEL_NAME_CHARS);
        nameText.setLayoutData(gd);
        nameText.setTextLimit(MODEL_NAME_CHARS);
        nameText.setText(modelName);
        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateName(nameText.getText());
                updateOKButton();
            }
        });
        
        label = new Label(localComposite, SWT.NONE);
        label.setText(Messages.ModelPropertiesDialog_Database);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        final ComboViewer dbCombo = new ComboViewer(localComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        dbCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        dbCombo.setContentProvider(new ArrayContentProvider());
        dbCombo.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                DatabaseExtension dbExt = (DatabaseExtension)element;
                return dbExt.getName();
            }
        });
        dbCombo.setInput(RMBenchPlugin.getExtensionManager().getDatabaseExtensions());
        dbCombo.setSelection(new StructuredSelection(databaseExtension));
        dbCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                DatabaseExtension dbext = (DatabaseExtension)selection.getFirstElement();
                
                if(dbext != databaseExtension) {
                    boolean proceed = 
                        model.isEmpty() || MessageDialog.openQuestion(
                                getShell(),
                                Messages.ModelPropertiesDialog_ChangeModel_Title,
                                Messages.ModelPropertiesDialog_ChangeModel_Message);
                    
                    if(proceed) {
                        databaseExtension = dbext;
                        updateOKButton();
                    }
                    else {
                        dbCombo.setSelection(new StructuredSelection(databaseExtension), true);
                    }
                }
            }
        });
        
        label = new Label(localComposite, SWT.NONE);
        label.setText(Messages.ModelPropertiesDialog_NameGenerator);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        
        final ComboViewer generatorCombo = new ComboViewer(localComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        generatorCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        generatorCombo.setContentProvider(new ArrayContentProvider());
        generatorCombo.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                NameGeneratorExtension genext = (NameGeneratorExtension)element;
                return genext.getName();
            }
        });
        generatorCombo.setInput(RMBenchPlugin.getExtensionManager().getNameGeneratorExtensions());
        generatorCombo.setSelection(new StructuredSelection(generatorExtension));
        generatorCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                generatorExtension = (NameGeneratorExtension)selection.getFirstElement();
                updateOKButton();
            }
        });
        return mainComposite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    protected void validateName(String text) {
        modelName = text.length() >  0 ? text : null;
    }

    protected void updateOKButton() {
        boolean enabled = 
            modelName != null && (
            !modelName.equals(model.getName()) || 
            databaseExtension.getDatabaseInfo() != model.getDatabaseInfo() ||
            generatorExtension.getNameGenerator() != model.getNameGenerator());
        
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }

    protected void okPressed() {
        super.okPressed();
    }
    
    public String getModelName(){
        return modelName;
    }
    
    public IDatabaseInfo getDatabaseInfo(){
        return databaseExtension.getDatabaseInfo();
    }
    
    public INameGenerator getNameGenerator(){
        return generatorExtension.getNameGenerator();
    }
}
