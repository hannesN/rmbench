/*
 * created 01.05.2006, cse
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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * import configuration page
 * 
 * @author cse
 */
public class JdbcConnectionWizardPage4 extends WizardPage {

    private final JdbcConnectionWizard wizard;
    
    private Button indexesCheck, keyCheck, commentsCheck;
    
    protected JdbcConnectionWizardPage4(JdbcConnectionWizard wizard) {
        super("JdbcConnectionWizardPage4");
        this.wizard = wizard;
        setTitle(wizard.getPageTitle());
        setDescription(Messages.JdbcConnectionWizardPage4_description);
    }

	public void createControl(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        indexesCheck = new Button(mainComposite, SWT.CHECK);
        indexesCheck.setSelection(wizard.getDoIndexes());
        indexesCheck.setText(Messages.JdbcConnectionWizardPage4_indexes);
        indexesCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
	        	wizard.setDoIndexes(indexesCheck.getSelection());
	            keyCheck.setEnabled(wizard.getDoIndexes());
			}
        });
        
        keyCheck = new Button(mainComposite, SWT.CHECK);
        keyCheck.setSelection(wizard.getDoKeyIndexes());
        keyCheck.setText(Messages.JdbcConnectionWizardPage4_keyindexes);
        keyCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				wizard.setDoKeyIndexes(keyCheck.getSelection());
			}
        });
        keyCheck.setEnabled(wizard.getDoIndexes());

        commentsCheck = new Button(mainComposite, SWT.CHECK);
        commentsCheck.setSelection(wizard.getDoComments());
        commentsCheck.setText(Messages.JdbcConnectionWizardPage4_comments);
        commentsCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				wizard.setDoComments(commentsCheck.getSelection());
			}
        });
        
        setControl(mainComposite);
	}
}
