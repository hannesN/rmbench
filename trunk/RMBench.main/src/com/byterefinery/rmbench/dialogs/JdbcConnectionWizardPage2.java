/*
 * created 08.05.2005
 * 
 * $Id:JdbcConnectionWizardPage2.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dialogs;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.external.IURLSetupGroup;

/**
 * wizard page that allows configuration of the connection URL components
 * @author cse
 */
public class JdbcConnectionWizardPage2 extends WizardPage {

    private final JdbcConnectionWizard wizard;

    private Composite container;

    private Composite loginContainer;

    private IURLSetupGroup urlSetupGroup;
    
    private String lastUrlSetupGroupErrorMessage = null;
    
    private Text passwordText;
    private Text passwordCheckText;

    public JdbcConnectionWizardPage2(JdbcConnectionWizard wizard) {
        super(JdbcConnectionWizardPage2.class.getName());

        this.wizard = wizard;
        setTitle(wizard.getPageTitle());
        setDescription(Messages.JdbcConnectionWizardPage2_description);
    }

    public void createControl(Composite parent) {

        container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 9;
        container.setLayout(layout);

        setControl(container);
        setPageComplete(wizard.getConnectionUrl() != null);
    }

    private void createLogintexts() {
        if (loginContainer != null) {
            loginContainer.dispose();
        }

        loginContainer = new Composite(container, SWT.NONE);
        loginContainer.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 9;
        loginContainer.setLayout(layout);

        if (wizard.getDriverInfo().isUserIdNeeded()) {
            Label label = new Label(loginContainer, SWT.NULL);
            label.setText(Messages.JdbcConnectionWizardPage2_userid);

            final Text userText = new Text(loginContainer, SWT.BORDER | SWT.SINGLE);
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            userText.setLayoutData(gridData);
            if (wizard.getUserid() != null)
                userText.setText(wizard.getUserid());
            userText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    wizard.setUserId(userText.getText());
                    updatePageComplete();
                }
            });
        }

        if (wizard.getDriverInfo().isPasswordNeeded()) {
            Label label = new Label(loginContainer, SWT.NULL);
            label.setText(Messages.JdbcConnectionWizardPage2_password);
            GridData gridData = new GridData(GridData.FILL_VERTICAL);
            gridData.verticalAlignment = SWT.CENTER;
            label.setLayoutData(gridData);
            
            passwordText= new Text(loginContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            passwordText.setLayoutData(gridData);
            if (wizard.getPassword() != null)
                passwordText.setText(wizard.getPassword());
            
            label = new Label(loginContainer, SWT.NULL);
            label.setText(Messages.JdbcConnectionWizardPage2_repeat);
            gridData = new GridData(GridData.FILL_VERTICAL);
            gridData.verticalAlignment = SWT.CENTER;
            label.setLayoutData(gridData);
            
            passwordCheckText = new Text(loginContainer, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            passwordCheckText.setLayoutData(gridData);
            if (wizard.getPassword() != null)
            	passwordCheckText.setText(wizard.getPassword());
            
            ModifyListener listener = new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
                	String passwd = passwordText.getText(); 
                	
                	if ( (passwd.length() > 0) &&
                		(passwd.equals(passwordCheckText.getText())) )
                			wizard.setPassword(passwd);
                		
                    updatePageComplete();            		
            	}
            };
            passwordText.addModifyListener(listener);
            passwordCheckText.addModifyListener(listener);
            

            Button promptCheck = new Button(loginContainer, SWT.CHECK);
            promptCheck.setText(Messages.JdbcConnectionWizardPage2_prompt);
            gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
            gridData.horizontalSpan = 2;
            promptCheck.setLayoutData(gridData);
            promptCheck.setSelection(wizard.getPrompt());
            promptCheck.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    Button promptCheck = ((Button) event.getSource());
                    boolean prompt = promptCheck.getSelection();
                    wizard.setPrompt(prompt);
                    passwordText.setEnabled(!prompt);
                    passwordCheckText.setEnabled(!prompt);
                    updatePageComplete();
                    if (prompt) {
                        passwordText.setText(""); //$NON-NLS-1$
                        passwordCheckText.setText(""); //$NON-NLS-1$
                    }
                }
            });
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            createLogintexts();
            showDriverSetupGroup();
        }
        super.setVisible(visible);
    }

    private void showDriverSetupGroup() {
        Point windowSize = getShell().getSize();
        Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        if (urlSetupGroup != null) {
            urlSetupGroup.disposeWidgets();
        }

        urlSetupGroup = wizard.createSetupGroup();
        urlSetupGroup.addListener(new IURLSetupGroup.Listener() {
            public void inputCompleted(boolean completed) {
                wizard.setConnectionUrl(urlSetupGroup.getConnectionURL());
                updatePageComplete();
            }

            public void errorOccured(String errorMessage) {
            	lastUrlSetupGroupErrorMessage = errorMessage;
            	updatePageComplete();
            }
        });

        // need to be after the listener because of the firing of the initial errormessage
        urlSetupGroup.createWidgets(container);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        urlSetupGroup.getControl().setLayoutData(gridData);
        urlSetupGroup.getControl().pack();

        updatePageComplete();

        resizeWindow(windowSize, oldSize);
        container.layout();
        container.redraw();
    }

    private void resizeWindow(Point windowSize, Point oldSize) {
        Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (newSize.y > windowSize.y) {
            getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
        }
    }

    private void updatePageComplete() {
    	String error = null;
    	
    	if (!urlSetupGroup.isComplete()) {
    		error = lastUrlSetupGroupErrorMessage;
    	}
    	
    	if ((wizard.getDriverInfo().isPasswordNeeded()) && (!wizard.getPrompt())) { 
    			if (wizard.getPassword() == null)
    				error = Messages.JdbcConnectionWizardPage2_password_required;
    			if (!(passwordText.getText().equals(passwordCheckText.getText()))) {
    				error = Messages.JdbcConnectionWizardPage2_passwords_not_equal;
    			}
        }
    	if ((wizard.getDriverInfo().isUserIdNeeded()) && (wizard.getUserid() == null)) {
    		error = Messages.JdbcConnectionWizardPage2_userid_required;
        }

        
        setPageComplete((!wizard.getDriverInfo().isUserIdNeeded())
                || (wizard.getUserid() != null)
                && (wizard.getPassword() != null || wizard.getPrompt() || !wizard.getDriverInfo()
                        .isPasswordNeeded()) && urlSetupGroup.isComplete());
        
       	setErrorMessage(error);
    }}
