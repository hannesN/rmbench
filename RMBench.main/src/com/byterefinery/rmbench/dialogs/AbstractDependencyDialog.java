/*
 * created 20-Feb-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: AbstractDependencyDialog.java 231 2006-02-20 23:04:58Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract class for generating confirm dialogs which show indirect actions 
 * triggered by an action (e.g. deletion of modelelemnt)
 * @author Hannes Niederhausen
 *
 */
public abstract class AbstractDependencyDialog extends IconAndMessageDialog {

    private static final int MAX_LINES = 30;
    
    private Button  detailsButton;
    private Text    detailsText;
    private boolean detailsCreated;
        
    
    public AbstractDependencyDialog(Shell parentShell) {
        super(parentShell);
        detailsCreated=false;
    }
    
    /**
     * @return the detail message, which is shown in a collapseable, read-only text area 
     * below the message 
     */
    protected abstract String getDetails();
    
    /**
     * @return the message the message of the dialog, which is shown alongside the dialog icon
     */
    protected String getMessage() {
        return Messages.DependencyDialog_defaultMessage;
    }

    /**
     * @return the icon image for this dialog. By default, this is the warning icon 
     */
    protected Image getImage() {
        return getInfoImage();
    }
    
    /**
     * In this method, you can add some widgets to the dialog.
     * @param parent 
     * @return
     */
    protected Control getDialogAreaAdditions(Composite parent) {
        return null;
    }
    
    protected final Control createDialogArea(Composite parent) {
        createMessageArea(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        composite.setLayoutData(gd);
          
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setFont(parent.getFont());
        
        // allow subclasses to add custom controls
        createOptionsArea(composite);

        return composite;
    }
    
    /**
     * create the options area
     * 
     * @param parent the parent composite
     */
    protected void createOptionsArea(Composite parent) {
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.DependencyDialog_title);
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL,
                true);
        detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
                    IDialogConstants.SHOW_DETAILS_LABEL, false);
     
    }
    
    protected void buttonPressed(int id) {
        if (id == IDialogConstants.DETAILS_ID) {
            // was the details button pressed?
            toggleDetailsArea();
        } else {
            super.buttonPressed(id);
        }
    }
    
    private void toggleDetailsArea() {
        Point windowSize = getShell().getSize();
        Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (detailsCreated) {
            detailsText.dispose();
            detailsCreated = false;
            detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
        } else {
            detailsText = createDetailsText((Composite) getContents());
            detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
        }
        Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        getShell()
                .setSize(
                        new Point(windowSize.x, windowSize.y
                                + (newSize.y - oldSize.y)));
    }
    
    protected Text createDetailsText(Composite parent) {
        // create the list
        detailsText = new Text(
                parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI);
        // fill the list
        detailsText.setText(getDetails());
        
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
                | GridData.GRAB_VERTICAL);
        data.heightHint = Math.min(detailsText.getLineCount(), MAX_LINES) * detailsText.getLineHeight();
        data.horizontalSpan = 2;
        detailsText.setLayoutData(data);
        detailsText.setFont(parent.getFont());
        detailsCreated=true;
        
        return detailsText;
    }
    
    protected Control createMessageArea(Composite composite) {
        message = getMessage();
        return super.createMessageArea(composite);
    }
    
    protected void setDetailsText(String details) {
        if ( (detailsText==null) || (detailsText.isDisposed()))
            return;
        
        detailsText.setText(details);
    }
}
