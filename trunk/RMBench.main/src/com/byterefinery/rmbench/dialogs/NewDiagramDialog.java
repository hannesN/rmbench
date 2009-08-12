/*
 * created 11.11.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.dialogs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * dialog for querying diagram creation properties from the user
 * 
 * @author cse
 */
public class NewDiagramDialog extends Dialog {

    private static final Pattern DIAGRAM_NAME_PATTERN = Pattern.compile("[a-zA-Z]\\S*");
    private static final int DIAGRAM_NAME_CHARS = 30;
    
    private String diagramName;
    private final Model model;
    
    private Schema selectedSchema;
    
    private Label errorMessageLabel;
    
    public NewDiagramDialog(Shell parentShell, Model model) {
        super(parentShell);
        this.model = model;
        this.diagramName = createDiagramName();
    }

    /*
     * @return a unique diagram name 
     */
    private String createDiagramName() {
        int count = model.getDiagrams().size() + 1;
        String name = Messages.NewDiagramDialog_NewDiagramName + count;
        
        while(model.getDiagram(name) != null) {
            count++;
            name = Messages.NewDiagramDialog_NewDiagramName + count;
        }
        return name;
    }

    public Schema getSelectedSchema() {
        return selectedSchema;
    }

    public String getDiagramName() {
        return diagramName;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.NewDiagramDialog_Title);
    }
    
    protected Control createDialogArea(Composite parent) {
        
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        
        final Composite localComposite = new Composite(mainComposite, SWT.NONE);
        localComposite.setLayout(new GridLayout(2, false));
        
        final Label nameLabel = new Label(localComposite, SWT.NONE);
        nameLabel.setText(Messages.NewDiagramDialog_Name);
        nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        final Text nameText = new Text(localComposite, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(SWT.LEFT, SWT.FILL, false, true);
        gd.widthHint = convertWidthInCharsToPixels(DIAGRAM_NAME_CHARS);
        nameText.setLayoutData(gd);
        nameText.setTextLimit(DIAGRAM_NAME_CHARS);
        nameText.setText(diagramName);
        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateName(nameText.getText());
                updateOKButton();
            }
        });
        
        final Label schemaLabel = new Label(localComposite, SWT.NONE);
        schemaLabel.setText(Messages.NewDiagramDialog_TargetSchema);
        schemaLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        final ComboViewer schemaCombo = new ComboViewer(localComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        schemaCombo.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        schemaCombo.setContentProvider(new ArrayContentProvider());
        schemaCombo.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                Schema schema = (Schema)element;
                return schema.getName();
            }
        });
        Schema[] schemas = model.getSchemasArray();
        schemaCombo.setInput(schemas);
        selectedSchema = schemas[0];
        schemaCombo.setSelection(new StructuredSelection(selectedSchema), true);
        schemaCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                selectedSchema = (Schema)selection.getFirstElement();
            }
        });
        
        errorMessageLabel = new Label(localComposite, SWT.NONE);
        errorMessageLabel.setForeground(ColorConstants.red);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        errorMessageLabel.setLayoutData(gd);
        
        return mainComposite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    protected void validateName(String text) {
        diagramName = null;
        
        Matcher matcher = DIAGRAM_NAME_PATTERN.matcher(text);
        if(!matcher.matches()) {
            setErrorMessage(ExceptionMessages.invalidDiagramName);
        }
        else if(model.getDiagram(text) != null) {
            setErrorMessage(ExceptionMessages.duplicateName);
        }
        else {
            setErrorMessage(null);
            if(text.length() >  0)
                diagramName = text;
        }
    }

    private void setErrorMessage(String message) {
        errorMessageLabel.setText(message != null ? message : "");
    }

    protected void updateOKButton() {
        getButton(IDialogConstants.OK_ID).setEnabled(diagramName != null);
    }
}
