/*
 * created 10.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLGeneratorWizardFactory;

/**
 * This Factory creates a widget for the DDLGeneratorWizard. In fact it's an extended version of
 * the com.byterefinery.rmbench.database.SQL99DDLGeneratorWizardFactory.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLDDLGeneratorWizardFactory implements IDDLGeneratorWizardFactory {

    public IWizardPage getWizardPage(IDDLGenerator generator) {
        return new MySQLDDLGeneratorWizardPage((MySQLDDLGenerator) generator);
    }
    
    private static final class MySQLDDLGeneratorWizardPage extends WizardPage {

        private final MySQLDDLGenerator generator;
        
        private Button quoteIdentifiers;
        private Button quoteColumns;
        
        protected MySQLDDLGeneratorWizardPage(MySQLDDLGenerator generator) {
            super(MySQLDDLGeneratorWizardPage.class.getName());
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
           
            Composite dbTypeCOmposite = new Composite(composite, SWT.NONE);
            dbTypeCOmposite.setLayout(new GridLayout(2, false));
            Label label = new Label(dbTypeCOmposite, SWT.NONE);
            label.setText(Messages.MySQLWizard_tableType);
            final Combo databaseTypeCombo = new Combo(dbTypeCOmposite, SWT.NONE);
            databaseTypeCombo.add(MySQLDDLGenerator.TABLETYPE_INNODB);
            databaseTypeCombo.add(MySQLDDLGenerator.TABLETYPE_MYISAM);
            if (generator.getTableType().equals(MySQLDDLGenerator.TABLETYPE_INNODB))
                databaseTypeCombo.select(0);
            else
                databaseTypeCombo.select(1);
            databaseTypeCombo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    generator.setTableType(databaseTypeCombo.getItem(databaseTypeCombo
                            .getSelectionIndex()));
                }
            });
            
            
            setControl(composite);
            setPageComplete(true);
        }
    }
}
