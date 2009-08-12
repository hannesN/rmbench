/*
 * created 22.08.2005 by sell
 *
 * $Id: ConstraintTypePage.java 235 2006-02-21 16:37:26Z thomasp $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;

/**
 * wiard page for entering the constraint name and establishing the choice 
 * between a CHECK constraint and a UNIQUE constraint
 * 
 * @author sell
 */
public class ConstraintTypePage extends WizardPage {

    private static final String PAGE_NAME = "ConstraintTypePage";
    
    private Button checkRadio;
    private Button uniqueRadio;
    private Button primaryRadio;
    
    private Button generateCheck;
    private Text nameText;
    private Model model;    
    
    protected ConstraintTypePage() {
        super(PAGE_NAME);
        setTitle(Messages.ConstraintWizard_TypeTitle);
        setDescription(Messages.ConstraintWizard_TypeDescription);
        this.model = RMBenchPlugin.getModelManager().getModel();
    }

    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout());
        
        Group nameGroup = new Group(composite, SWT.NONE);
        nameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        nameGroup.setLayout(new GridLayout());
        nameGroup.setText(Messages.ConstraintWizard_Name_Group);
        generateCheck = new Button (nameGroup, SWT.CHECK);
        generateCheck.setLayoutData(new GridData(SWT.BEGINNING, SWT.BOTTOM, false, false));
        generateCheck.setText(Messages.ConstraintWizard_CheckGenerate);
        generateCheck.setSelection(true);
        
        nameText = new Text(nameGroup, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        nameText.setEnabled(false);
        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updatePageComplete();                
            }
        });
        nameText.addVerifyListener(new VerifyListener(){

            public void verifyText(VerifyEvent e) {
                e.doit = !Character.isWhitespace(e.character);
            }
            
        });
        generateCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                nameText.setEnabled(!generateCheck.getSelection());
                updatePageComplete();
            }
        });
        
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.ConstraintWizard_Type_Group);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true);
        group.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 30;
        layout.marginHeight = 30;
        group.setLayout(layout);
        
        primaryRadio = new Button(group, SWT.RADIO);
        primaryRadio.setText(Messages.ConstraintWizard_Type_PRIMARY);
         if(((TableConstraintWizard)getWizard()).getTable().getPrimaryKey() == null) {
             primaryRadio.setEnabled(true);
             primaryRadio.setSelection(true);
        }
         else
             primaryRadio.setEnabled(false);
        
        uniqueRadio = new Button(group, SWT.RADIO);
        uniqueRadio.setText(Messages.ConstraintWizard_Type_UNIQUE);
        if(!primaryRadio.isEnabled())
            uniqueRadio.setSelection(true);
        
        checkRadio = new Button(group, SWT.RADIO);
        checkRadio.setText(Messages.ConstraintWizard_Type_CHECK);
        
        setControl(composite);
        updatePageComplete();
    }
    
    private boolean validName(){
        if(generateCheck.getSelection()){
            setErrorMessage(null);
            return true;
        }
        if(nameText.getText().length() < 1){
            setErrorMessage(Messages.ConstraintWizard_emptyNameError);
            return false;           
        }
        if(model.containsConstraint(nameText.getText())){
            setErrorMessage(Messages.ConstraintWizard_nameError);
            return false;
        }
        
        setErrorMessage(null);
        return true;
    }
    
    private void updatePageComplete() {
        setPageComplete(validName());
    }

    public IWizardPage getNextPage() {
        if(checkRadio.getSelection()) 
            return getWizard().getPage(CheckConstraintPage.PAGE_NAME);
        else if(uniqueRadio.getSelection())
            return getWizard().getPage(UniqueConstraintPage.PAGE_NAME);
        else if(primaryRadio.getSelection())
            return getWizard().getPage(PrimaryConstraintPage.PAGE_NAME);
        
        throw new IllegalStateException();
    }

    public String getConstraintName() {
        return generateCheck.getSelection() ? null : nameText.getText();
    }

    /**
     * @return the constraint type name
     */
    public String getConstraintType() {
        if(checkRadio.getSelection())
            return CheckConstraint.CONSTRAINT_TYPE;
        else if(uniqueRadio.getSelection())
            return UniqueConstraint.CONSTRAINT_TYPE;
        else
            return PrimaryKey.CONSTRAINT_TYPE;
    }
}
