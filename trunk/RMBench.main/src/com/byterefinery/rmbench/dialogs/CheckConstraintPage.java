/*
 * created 22.08.2005 by sell
 *
 * $Id: CheckConstraintPage.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.model.schema.CheckConstraint;

/**
 * wizard page for entering a CHECK expression
 * 
 * @author sell
 */
public class CheckConstraintPage extends WizardPage implements TableConstraintWizard.ConstraintPage{

    public static final String PAGE_NAME = CheckConstraint.CONSTRAINT_TYPE;
    
    private Text constraintText;
    
    protected CheckConstraintPage() {
        super(PAGE_NAME);
        setTitle(Messages.ConstraintWizard_CheckTitle);
        setDescription(Messages.ConstraintWizard_CheckDescription);
    }

    //@see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout();
        layout.marginWidth = 30;
        layout.marginHeight = 30;
        composite.setLayout(layout);

        constraintText = new Text(
                composite, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumWidth = convertWidthInCharsToPixels(30);
        gd.minimumHeight = convertHeightInCharsToPixels(5);
        constraintText.setLayoutData(gd);
        constraintText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setPageComplete(constraintText.getCharCount() > 0);
            }
        });
        
        setControl(composite);
        setPageComplete(false);
    }
    
    public IWizardPage getNextPage() {
        return null;
    }

    public String getExpression() {
        return constraintText.getText();
    }

    public Object getConstraintArg() {
        return getExpression();
    }
}
