/*
 * created 16.06.2011 
 *
 * Copyright 2011, ByteRefinery
 */
package com.byterefinery.rmbench.database.mssql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.external.IDataTypeEditorFactory;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * @author cse
 */
public class XMLTypeEditorFactory implements IDataTypeEditorFactory 
{
	@Override
	public boolean openEditor(Shell shell, IDataType datatype) 
	{
		if (datatype instanceof XMLDataType) {
			XMLDataTypeDialog dialog = new XMLDataTypeDialog(shell, (XMLDataType)datatype);
			if (dialog.open() == Dialog.OK)
				return true;
		}
		return false;
	}
}
