/*
 * created 24.05.2005
 * 
 * $Id: ExceptionDialog.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;

/**
 * a dialog that shows an error message and allows inspecting the stack trace
 * in a collapseable text field
 * 
 * @author cse
 */
public class ExceptionDialog extends IconAndMessageDialog {

    private static final int MAX_LINES = 30;
    
    private Clipboard clipboard;
    private IStatus status;
    private Button detailsButton;
    private Text detailsText;
    boolean detailsCreated;
    
    private final String title;
    
    public static int openError(Shell parentShell, String message, IStatus status) {
        
        ExceptionDialog dialog = new ExceptionDialog(parentShell, message, status);
        return dialog.open();
    }
    
    public ExceptionDialog(Shell parentShell, String message, IStatus status) {
        this(parentShell, ExceptionMessages.errorTitle, message, status);
    }
    
    public ExceptionDialog(Shell parentShell, String title, String message, IStatus status) {
        
        super(parentShell);
        this.message = message == null ? status.getMessage(): message;
        this.status = status;
        this.title = title;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    protected Image getImage() {
        return getErrorImage();
    }

    protected Control createDialogArea(Composite parent) {
        createMessageArea(parent);
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        composite.setLayout(layout);
        GridData childData = new GridData(GridData.FILL_BOTH);
        childData.horizontalSpan = 2;
        composite.setLayoutData(childData);
        composite.setFont(parent.getFont());
        return composite;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }
    
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        if(status.getException() != null) {
            detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
                    IDialogConstants.SHOW_DETAILS_LABEL, false);
        }
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
        populateDetails();
        
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
                | GridData.GRAB_VERTICAL);
        data.heightHint = Math.min(detailsText.getLineCount(), MAX_LINES) * detailsText.getLineHeight();
        data.horizontalSpan = 2;
        detailsText.setLayoutData(data);
        detailsText.setFont(parent.getFont());
        Menu copyMenu = new Menu(detailsText);
        MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
        copyItem.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                copyToClipboard();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                copyToClipboard();
            }
        });
        copyItem.setText(JFaceResources.getString("copy")); //$NON-NLS-1$
        detailsText.setMenu(copyMenu);
        detailsCreated = true;
        return detailsText;
    }

    private void populateDetails() {
        
        Throwable exception = status.getException();
        if(exception != null) {
            StringWriter stackTrace = new StringWriter();
            PrintWriter writer = new PrintWriter(stackTrace);
            exception.printStackTrace(writer);
            detailsText.setText(stackTrace.toString());
            
        }
    }

    private void copyToClipboard() {
        if (clipboard != null)
            clipboard.dispose();
        clipboard = new Clipboard(detailsText.getDisplay());
        clipboard.setContents(new Object[] { detailsText.getText() },
                new Transfer[] { TextTransfer.getInstance() });
    }
}