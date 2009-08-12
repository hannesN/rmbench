/*
 * created 10.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id: RMBenchPreferencePage.java 530 2006-08-30 15:11:04Z cse $
 */
package com.byterefinery.rmbench.preferences;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dialogs.LicenseDetailsDialog;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.LicenseManager;

/**
 * root preference page for RMBench. It shows the licensing status and allows 
 * installation of a license
 *  
 * @author cse
 */
public class RMBenchPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    protected static final String[] FILTER_EXTS = {"*.key", "*.*"};
    
    //these constants must be the same as generated into the license (see ILicenseConstants)
    private static final String INFO_USERNAME = "USER";
    private static final String INFO_EMAIL = "EMAIL";
    
    private Label statusLabel;
    
    public void init(IWorkbench workbench) {
    }

    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        
        final Label imageLabel = new Label(composite, SWT.CENTER);
        imageLabel.setImage(RMBenchPlugin.getImage(ImageConstants.RMBENCH_INFO));
        imageLabel.addMouseListener(new MouseListener() {
            public void mouseDoubleClick(MouseEvent e) {
            }
            public void mouseDown(MouseEvent e) {
            }
            public void mouseUp(MouseEvent e) {
                RMBenchPlugin.externalBrowseHomepage(getShell());
            }
        });
        imageLabel.setCursor (Display.getCurrent().getSystemCursor (SWT.CURSOR_HAND));
        imageLabel.setToolTipText(RMBenchPlugin.RMBENCH_HOME);
        
        new Label(composite, SWT.NONE); //distance only
        
        Group licenseGroup = new Group(composite, SWT.SHADOW_NONE);
        licenseGroup.setText(Messages.RMBenchPreferencePage_licenseGroup);
        licenseGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        licenseGroup.setLayout(new GridLayout());
        
        statusLabel = new Label(licenseGroup, SWT.LEFT | SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint = 300; // Windows: force initial width
        statusLabel.setLayoutData(gd);
        
        final LicenseManager licenseManager = RMBenchPlugin.getLicenseManager();
        computeStatusLabel(licenseManager);
        
        if(licenseManager.isUserLicense()) {
            
            final Button detailsButton = new Button(licenseGroup, SWT.PUSH);
            detailsButton.setText(Messages.RMBenchPreferencePage_licenseDetails);
            detailsButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
            
            detailsButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    new LicenseDetailsDialog(getShell(), licenseManager.getInfo()).open();
                }
            });
            
        } else {
            
            final Button licenseButton = new Button(licenseGroup, SWT.PUSH);
            licenseButton.setText(Messages.RMBenchPreferencePage_installLicense);
            licenseButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
            
            licenseButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                    fileDialog.setFilterExtensions(FILTER_EXTS);
                    
                    String path = fileDialog.open();
                    if(path != null) {
                    	try {
                    		licenseManager.installLicenseFile(path);
                            if(licenseManager.isUserLicense()) {
                                licenseButton.setEnabled(false);
                            }
                            computeStatusLabel(licenseManager);
                    	} 
                    	catch(LicenseManager.LicenseException x) {
                    		MessageDialog.openError(getShell(), Messages.RMBenchPreferencePage_LicenseError, x.getMessage());
                    	}
                    }
                }
            });
        }
        return composite;
    }

    /**
     * compute and set the status label depending on the license status
     */
    protected void computeStatusLabel(LicenseManager licenseManager) {
        if(licenseManager.isUserLicense()) {
            Properties info = licenseManager.getInfo();
            String message = MessageFormat.format(
                    Messages.RMBenchPreferencePage_userLicenseInfo, 
                    new Object[]{info.getProperty(INFO_USERNAME), info.getProperty(INFO_EMAIL)});
            statusLabel.setText(message);
        }
        else {
            String message;
            if(licenseManager.isTrialLicense()) {
                message = MessageFormat.format(
                        Messages.RMBenchPreferencePage_trialLicense, 
                        new Object[]{new Integer(licenseManager.getTrialDays())});
            }
            else
                message = Messages.RMBenchPreferencePage_communityLicense;
            
            statusLabel.setText(message+"\n\n"+Messages.RMBenchPreferencePage_licenseAddendum);
        }
    }
}
