/*
 * created 03.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.test;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLGeneratorWizardFactory;

/**
 * @author cse
 */
public class DDLGeneratorWizardFactory implements IDDLGeneratorWizardFactory {

    public IWizardPage getWizardPage(IDDLGenerator generator) {
        return new DDLGeneratorWizardPage((DDLGenerator)generator);
    }
    
    private static final class DDLGeneratorWizardPage extends WizardPage {

        private final DDLGenerator generator;
        
        protected DDLGeneratorWizardPage(DDLGenerator generator) {
            super("DDLGeneratorWizardPage");
            this.generator = generator;
        }

        public void createControl(Composite parent) {
            final Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            Button optionButton = new Button(composite, SWT.CHECK);
            optionButton.setText("Some option");
            optionButton.setSelection(generator.getOptionValue());
            
            setControl(composite);
            setPageComplete(true);
        }
    }
}
