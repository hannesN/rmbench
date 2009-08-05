/*
 * created 16.03.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package org.byterefinery.proguard.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * dialog to choose the input arguments for a ReTrace operation
 * 
 * @author cse
 */
public class RetraceFilesDialog extends Dialog {

    private static String lastPath;
    
    private String mappingsFile, stacktraceFile, stackTraceString;
    boolean verbose;
    
    Text stacktraceText;
    
    public RetraceFilesDialog(Shell parentShell, String mappingsFile, boolean verbose) {
        super(parentShell);
        this.mappingsFile = mappingsFile;
        this.verbose = verbose;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ReTrace Properties");
    }
    
    protected Control createDialogArea(Composite parent) {
        
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        
        final Composite localComposite = new Composite(mainComposite, SWT.NONE);
        localComposite.setLayout(new GridLayout(3, false));
        
        Label label = new Label(localComposite, SWT.NONE);
        label.setText("mappings file:");
        
        final Text mappingsText = new Text(localComposite, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.minimumWidth = 300;
        mappingsText.setLayoutData(gd);
        mappingsText.setText(mappingsFile != null ? mappingsFile : "");
        
        Button button = new Button(localComposite, SWT.PUSH);
        button.setText("choose..");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                fileDialog.setFilterPath(lastPath);
                String fileName = fileDialog.open();
                if(fileName != null) {
                    lastPath = new File(fileName).getAbsolutePath();
                    mappingsFile = fileName;
                    mappingsText.setText(fileName);
                }
            }
        });

        label = new Label(localComposite, SWT.NONE);
        label.setText("stacktrace file:");
        
        final Text stackFileText = new Text(localComposite, SWT.BORDER | SWT.SINGLE);
        stackFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        button = new Button(localComposite, SWT.PUSH);
        button.setText("choose..");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                fileDialog.setFilterPath(lastPath);
                String fileName = fileDialog.open();
                if(fileName != null) {
                    lastPath = new File(fileName).getAbsolutePath();
                    stacktraceFile = fileName;
                    stackFileText.setText(fileName);
                }
            }
        });
        
        final Button verboseButton = new Button(localComposite, SWT.CHECK);
        gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        gd.horizontalSpan = 3;
        verboseButton.setLayoutData(gd);
        verboseButton.setText("verbose");
        verboseButton.setSelection(verbose);
        verboseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                verbose = verboseButton.getSelection();
            }
        });
        
        label = new Label(localComposite, SWT.NONE);
        label.setText("or paste stacktrace:");
        gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        gd.horizontalSpan = 3;
        
        stacktraceText = new Text(localComposite, SWT.BORDER | SWT.MULTI);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 3;
        gd.minimumHeight = 100;
        stacktraceText.setLayoutData(gd);
        
        return mainComposite;
    }

    protected void okPressed() {
        stackTraceString = stacktraceText.getText();
        if(stackTraceString.length() == 0)
            stackTraceString = null;
        super.okPressed();
    }

    public String getMappingsFile() {
        return mappingsFile;
    }

    public String getStacktraceFile() {
        return stacktraceFile;
    }

    public String getStacktraceString() {
        return stackTraceString;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
