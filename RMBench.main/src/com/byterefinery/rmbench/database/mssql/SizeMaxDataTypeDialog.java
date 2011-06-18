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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * a dialog for configuring properties of a XML datatype column
 * 
 * @author cse
 */
public class SizeMaxDataTypeDialog extends Dialog 
{
	private final SizeMaxDataType dataType;
	
	private Button bSizeInt, bSizeMax;
	
	public SizeMaxDataTypeDialog(Shell parentShell, SizeMaxDataType dataType)
	{
		super(parentShell);
		this.dataType = dataType;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(String.format("%s Column Properties", dataType.getPrimaryName()));
		newShell.setMinimumSize(400, 150);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		GridLayoutFactory.swtDefaults().applyTo(composite);
		
		Group comp = new Group(composite, SWT.NONE);
		comp.setText("Specify Size As");
		GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);
		GridLayoutFactory.swtDefaults().applyTo(comp);
		
		bSizeInt = new Button(comp, SWT.RADIO);
		bSizeInt.setText(String.format("Integer value (max. %d)", dataType.getMaxSize()));
		bSizeInt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(bSizeMax.getSelection() != dataType.isMax());
			}
		});
		
		bSizeMax = new Button(comp, SWT.RADIO);
		bSizeMax.setText("\"max\" (2^31-1 bytes)");
		bSizeMax.setSelection(dataType.isMax());
		bSizeMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(bSizeMax.getSelection() != dataType.isMax());
			}
		});
		
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void okPressed() 
	{
		dataType.setMax(bSizeMax.getSelection());
		
		super.okPressed();
	}
}
