/*
 * created 11.11.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: NewDiagramWizardPage.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * wizard page for creating a new diagram
 * 
 * @author hannesn
 */
public class NewDiagramWizardPage extends WizardPage {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]\\S*");
    private static final int NAME_CHARS = 30;

    private Text diagramNameText;
    private Text defaultSchemaNameText;
    private Combo schemaCombo;

    private String diagramName;
    private String schemaName;
    private Model model;

    protected NewDiagramWizardPage(Model model) {
        super("NewDiagramWizardPage");
        diagramName = null;
        this.model = model;
        setTitle(Messages.NewDiagramWizard_Title);
        setDescription(Messages.NewDiagramWizard_Description);
    }

    public void createControl(Composite parent) {
        Label label;
        GridData gridData;
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);

        container.setLayout(layout);

        label = new Label(container, SWT.NONE);
        label.setText(Messages.NewDiagramWizard_Name);

        diagramNameText = new Text(container, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        diagramNameText.setLayoutData(gridData);
        diagramNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateDiagramName(diagramNameText.getText());
                if (isFinished()) {
                    setPageComplete(true);
                }
            }
        });
        diagramNameText.setText(createDiagramName());
        label = new Label(container, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);

        // Groub box for Schemaediting
        Group groupComposite = new Group(container, SWT.None);
        groupComposite.setLayout(new GridLayout(1, false));
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        gridData.horizontalSpan = 2;
        groupComposite.setLayoutData(gridData);
        groupComposite.setText(Messages.NewDiagramWizard_TargetSchema);

        label = new Label(groupComposite, SWT.WRAP);
        label.setText(Messages.NewDiagramWizard_TargetSchema_Desc);
        label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        
        Button existingSchemaButton = new Button(groupComposite, SWT.RADIO);
        existingSchemaButton.setText(Messages.NewDiagramWizard_UseAvailableSchema);
        existingSchemaButton.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                defaultSchemaNameText.setEnabled(false);
                schemaCombo.setEnabled(true);
                validateSchemaName(schemaCombo.getItem(schemaCombo.getSelectionIndex()));
            }
        });

        schemaCombo = new Combo(groupComposite, SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        gridData.horizontalIndent = 20;
        schemaCombo.setLayoutData(gridData);
        for (Schema schema : model.getSchemas()) {
            schemaCombo.add(schema.getName());
        }
        schemaCombo.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                validateSchemaName(schemaCombo.getItem(schemaCombo.getSelectionIndex()));
            }
        });

        Button newSchemaButton = new Button(groupComposite, SWT.RADIO);
        newSchemaButton.setText(Messages.NewDiagramWizard_CreateNewSchema);
        newSchemaButton.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                defaultSchemaNameText.setEnabled(true);
                schemaCombo.setEnabled(false);
                validateSchemaName(defaultSchemaNameText.getText());
            }
        });

        defaultSchemaNameText = new Text(groupComposite, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        gridData.horizontalIndent = 20;
        defaultSchemaNameText.setLayoutData(gridData);
        defaultSchemaNameText.addModifyListener(new ModifyListener() {
            
            public void modifyText(ModifyEvent e) {
                validateSchemaName(defaultSchemaNameText.getText());
            }
        });

        if (model.getSchemas().size() == 0) {
            existingSchemaButton.setEnabled(false);
            schemaCombo.setEnabled(false);
            newSchemaButton.setSelection(true);
            defaultSchemaNameText.setEnabled(true);
        }
        else {
            existingSchemaButton.setEnabled(true);
            schemaCombo.setEnabled(true);
            existingSchemaButton.setSelection(true);
            defaultSchemaNameText.setEnabled(false);
            schemaCombo.select(0);
            schemaName = schemaCombo.getItem(0);
        }

        setControl(container);
    }

    /*
     * @return a unique diagram name 
     */
    protected String createDiagramName() {
        int count = model.getDiagrams().size() + 1;
        String name = Messages.NewDiagramDialog_NewDiagramName + count;
        
        while(model.getDiagram(name) != null) {
            count++;
            name = Messages.NewDiagramDialog_NewDiagramName + count;
        }
        return name;
    }

    protected void validateDiagramName(String text) {
        diagramName = null;

        Matcher matcher = NAME_PATTERN.matcher(text);
        setPageComplete(false);
        if ((text.length() > NAME_CHARS) || (!matcher.matches())) {
            setErrorMessage(ExceptionMessages.invalidDiagramName);
        }
        else if (model.getDiagram(text) != null) {
            setErrorMessage(ExceptionMessages.duplicateName);
        }
        else {
            if (text.length() > 0)
                diagramName = text;
            // we check if we have a valid schema name to, else we would overwrite some errors
            if (isFinished()) {
                setErrorMessage(null);
                setPageComplete(true);
            }
        }
    }

    protected void validateSchemaName(String text) {
        schemaName = null;

        Matcher matcher = NAME_PATTERN.matcher(text);
        setPageComplete(false);
        if ((text.length() > NAME_CHARS) || (!matcher.matches())) {
            setErrorMessage(ExceptionMessages.invalidSchemaName);
        }
        else {
            if (text.length() > 0)
                schemaName = text;

            // we check if we have a valid diagram name to, else we would overwrite some errors
            if (isFinished()) {
                setErrorMessage(null);
                setPageComplete(true);
            }
        }

    }

    public String getDiagramName() {
        return diagramName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public boolean isFinished() {
        return ((diagramName != null) && (schemaName != null));
    }

}
