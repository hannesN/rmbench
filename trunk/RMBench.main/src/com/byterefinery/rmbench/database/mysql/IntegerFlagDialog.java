/*
 * created 22.02.2007
 * 
 * $Id$
 */ 
package com.byterefinery.rmbench.database.mysql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * This dialog is used to set the optional flags for
 * the integer dataatypes.
 * 
 * @author Hannes Niederhausen
 *
 */
public class IntegerFlagDialog extends Dialog {

	final private MySQLIntegerDataType datatype;
	private Button zeroFillButton;
	private Button autoIncrementButton;
	private Button unsignedButton;
	
	private boolean autoIncrement;
	private boolean zerofill;
	private boolean unsigned;
	
	protected IntegerFlagDialog(Shell parentShell, MySQLIntegerDataType datatype) {
		super(parentShell);
		this.datatype = datatype;
		setShellStyle(SWT.TITLE|SWT.RESIZE);
		if ( (datatype.getFlags()&MySQLIntegerDataType.AUTO_INCREMENT) != 0)
				autoIncrement=true;
		if ( (datatype.getFlags()&MySQLIntegerDataType.UNSIGNED) != 0)
			unsigned=true;
		if ( (datatype.getFlags()&MySQLIntegerDataType.ZEROFILL) != 0)
			zerofill=true;
		
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.IntegerFlagDialog_Title);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		
		autoIncrementButton = new Button(comp, SWT.CHECK);
		autoIncrementButton.setText(Messages.IntegerFlagDialog_AUTO_INCREMENT);
		
		autoIncrementButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				if (b.getSelection()) {
					autoIncrement=true;
				} else {
					autoIncrement=false;
				}
				updateButtons();
			}
		});

		zeroFillButton = new Button(comp, SWT.CHECK);
		zeroFillButton.setText(Messages.IntegerFlagDialog_ZEROFILL);
		zeroFillButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				if (b.getSelection()) {
					zerofill = true;
				} else {
					zerofill = false;
				}
				updateButtons();
			}
		});
		
		unsignedButton = new Button(comp, SWT.CHECK);
		unsignedButton.setText(Messages.IntegerFlagDialog_UNSIGNED);
		unsignedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				if (b.getSelection()) {
					unsigned = true;
				} else {
					unsigned = false;
				}
				updateButtons();
			}
		});
		
		if (autoIncrement)
			autoIncrementButton.setSelection(true);
		else {
			zeroFillButton.setSelection(zerofill);
			unsignedButton.setSelection(unsigned);
		}
		
		updateButtons();
		
		return comp;
	}
	
	protected void updateButtons() {
		if (autoIncrement) {
			zerofill=false;
			zeroFillButton.setSelection(false);
			zeroFillButton.setEnabled(false);
			unsigned=false;
			unsignedButton.setSelection(false);
			unsignedButton.setEnabled(false);
		} else {
			if ( (zerofill) || (unsigned) )
				autoIncrementButton.setEnabled(false);
			else
				autoIncrementButton.setEnabled(true);
			
			zeroFillButton.setEnabled(true);
			unsignedButton.setEnabled(true);			
		}
		
	}
	
	protected void okPressed() {
		int currentFlags = 0;
		if (autoIncrement)
			currentFlags+=MySQLIntegerDataType.AUTO_INCREMENT;
		else {
			if (unsigned)
				currentFlags+=MySQLIntegerDataType.UNSIGNED;
			if (zerofill)
				currentFlags+=MySQLIntegerDataType.ZEROFILL;
		}
		datatype.setFlags(currentFlags);
		super.okPressed();
	}
	
	
}
