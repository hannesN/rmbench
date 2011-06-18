/*
 * created 17.06.2011 
 *
 * Copyright 2011, ByteRefinery
 */
package com.byterefinery.rmbench.database.mssql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * a dialog for configuring properties of a XML datatype column
 * 
 * @author cse
 */
public class XMLDataTypeDialog extends Dialog 
{
	private final XMLDataType dataType;
	
	private ComboViewer wRestriction;
	private Text wSchemaCollection;
	
	public XMLDataTypeDialog(Shell parentShell, XMLDataType dataType)
	{
		super(parentShell);
		this.dataType = dataType;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("XML Column Properties");
		newShell.setMinimumSize(400, 150);
	}
	
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite comp = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
		
		Label lRestriction = new Label(comp, SWT.NONE);
		lRestriction.setText("Restriction:");
		wRestriction = new ComboViewer(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		wRestriction.setContentProvider(new ArrayContentProvider());
		wRestriction.setLabelProvider(new LabelProvider());
		wRestriction.setInput(new String[]{"", "CONTENT", "DOCUMENT"});
		wRestriction.setSelection(new StructuredSelection(dataType.getRestriction() != null ? dataType.getRestriction() : ""));
		wRestriction.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(validateInput());
			}
		});
		
		Label lSchema = new Label(comp, SWT.NONE);
		lSchema.setText("Schema Collection:");
		wSchemaCollection = new Text(comp, SWT.BORDER);
		wSchemaCollection.setText(dataType.getSchemaName() != null ? dataType.getSchemaName() : "");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(wSchemaCollection);
		
		wSchemaCollection.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(validateInput());
			}
		});
		
		return comp;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	private boolean validateInput() 
	{
		StructuredSelection selection = (StructuredSelection)wRestriction.getSelection();
		String newRestriction = selection.isEmpty() ? "" : (String)selection.getFirstElement();
		String oldRestriction = dataType.getRestriction() != null ? dataType.getRestriction() : "";
		boolean restrictionChanged = !oldRestriction.equals(newRestriction);
		
		String newSchema = wSchemaCollection.getText();
		String oldSchema = dataType.getSchemaName() != null ? dataType.getSchemaName() : "";
		boolean schemaChanged = !oldSchema.equals(newSchema);
		
		return (restrictionChanged || schemaChanged) && ((newRestriction.length() > 0 && newSchema.length() > 0) || (newRestriction.length() == 0 && newSchema.length() == 0));
	}
	
	@Override
	protected void okPressed() 
	{
		StructuredSelection selection = (StructuredSelection)wRestriction.getSelection();
		String restriction = (String)selection.getFirstElement();
		dataType.setRestriction(restriction.length() > 0 ? restriction : null);
		
		String schemaCollection = wSchemaCollection.getText();
		dataType.setSchemaName(schemaCollection.length() > 0 ? schemaCollection : null);
		
		super.okPressed();
	}
}
