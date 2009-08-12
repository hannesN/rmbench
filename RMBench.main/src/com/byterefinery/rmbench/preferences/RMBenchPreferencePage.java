/*
 * created 10.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id: RMBenchPreferencePage.java 530 2006-08-30 15:11:04Z cse $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * root preference page for RMBench. It shows the licensing status and allows 
 * installation of a license
 *  
 * @author cse
 */
public class RMBenchPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

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
        
        Label statusLabel = new Label(licenseGroup, SWT.LEFT | SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint = 300; // Windows: force initial width
        statusLabel.setLayoutData(gd);
        statusLabel.setText("RMBench Open Source Edition");
        
        return composite;
    }

}
