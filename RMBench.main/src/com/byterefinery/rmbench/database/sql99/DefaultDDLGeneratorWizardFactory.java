/*
 * created 02.12.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DefaultDDLGeneratorWizardFactory.java 632 2007-02-19 16:42:40Z hannesn $
 */
package com.byterefinery.rmbench.database.sql99;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLGeneratorWizardFactory;
import com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator;

/**
 * creates the wizard page that configures the SQL99 DDL generator
 *  
 * @author cse
 */
public class DefaultDDLGeneratorWizardFactory implements IDDLGeneratorWizardFactory {

    public IWizardPage getWizardPage(IDDLGenerator generator) {
        return new DefaultDDLGeneratorWizardPage((DefaultDDLGenerator)generator);
    }
    
    private static final class DefaultDDLGeneratorWizardPage extends WizardPage {

        private final DefaultDDLGenerator generator;
        
        private Button quoteIdentifiers;
        private Button quoteColumns;
        
        protected DefaultDDLGeneratorWizardPage(DefaultDDLGenerator generator) {
            super(DefaultDDLGeneratorWizardPage.class.getName());
            this.generator = generator;
        }

        public void createControl(Composite parent) {
            final Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            composite.setLayout(new GridLayout());

            Composite quoteGroup = new Composite(composite, SWT.NONE);
            quoteGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.verticalSpacing = 3;
            quoteGroup.setLayout(layout);
            
            final Button quoteNames = new Button(quoteGroup, SWT.CHECK);
            GridData gd = new GridData();
            gd.horizontalSpan = 2;
            quoteNames.setLayoutData(gd);
            
            boolean quoteNamesSelection = generator.getQuoteObjectNames() || generator.getQuoteColumnNames();
            quoteNames.setText(Messages.SQL99Wizard_quoteIdentifiers);
            quoteNames.setSelection(quoteNamesSelection);
            quoteNames.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    boolean selection = quoteNames.getSelection();
                    generator.setQuoteObjectNames(selection);
                    generator.setQuoteColumnNames(selection);
                    
                    quoteIdentifiers.setSelection(selection);
                    quoteColumns.setSelection(selection);
                    quoteIdentifiers.setEnabled(selection);
                    quoteColumns.setEnabled(selection);
                }
            });
            quoteIdentifiers = new Button(quoteGroup, SWT.CHECK);
            gd = new GridData();
            gd.horizontalIndent = 25;
            quoteIdentifiers.setLayoutData(gd);
            quoteIdentifiers.setText(Messages.SQL99Wizard_quoteObjects);
            quoteIdentifiers.setSelection(generator.getQuoteObjectNames());
            quoteIdentifiers.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    generator.setQuoteObjectNames(quoteIdentifiers.getSelection());
                }
            });
            quoteIdentifiers.setEnabled(quoteNamesSelection);
            
            quoteColumns = new Button(quoteGroup, SWT.CHECK);
            quoteColumns.setText(Messages.SQL99Wizard_quoteColumns);
            quoteColumns.setSelection(generator.getQuoteColumnNames());
            quoteColumns.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    generator.setQuoteColumnNames(quoteColumns.getSelection());
                }
            });
            quoteColumns.setEnabled(quoteNamesSelection);
            
            final Button upperCaseKeywords = new Button(composite, SWT.CHECK);
            upperCaseKeywords.setText(Messages.SQL99Wizard_uppercaseKeywords);
            upperCaseKeywords.setSelection(generator.getUppercaseKeywords());
            upperCaseKeywords.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    generator.setUppercaseKeywords(upperCaseKeywords.getSelection());
                }
            });
            
            final Button exportPKs = new Button(composite, SWT.CHECK);
            exportPKs.setText(Messages.SQL99Wizard_exportPKs);
            exportPKs.setSelection(generator.getExportPKIndexes());
            exportPKs.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    generator.setExportPKIndexes(exportPKs.getSelection());
                }
            });
            
            setControl(composite);
            setPageComplete(true);
        }
    }
}
