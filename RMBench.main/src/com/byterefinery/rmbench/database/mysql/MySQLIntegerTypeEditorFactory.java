/*
 * created 21.02.2007
 * 
 * $Id$
 */ 
package com.byterefinery.rmbench.database.mysql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.external.IDataTypeEditorFactory;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * The factory thich generates a dialog to edit possible flags for 
 * the integer datatypes.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLIntegerTypeEditorFactory implements IDataTypeEditorFactory {

	/* (non-Javadoc)
	 * @see com.byterefinery.rmbench.external.IDataTypeEditorFactory#openEditor(org.eclipse.swt.widgets.Shell, com.byterefinery.rmbench.external.model.IDataType)
	 */
	public boolean openEditor(Shell shell, IDataType datatype) {
		if (datatype instanceof MySQLIntegerDataType) {
			IntegerFlagDialog dlg = new IntegerFlagDialog(shell, (MySQLIntegerDataType) datatype);
			if (dlg.open()==Dialog.OK)
				return true;
		}
		return false;
	}

}
