/*
 * created 06.11.2006
 *
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.external.IDataTypeEditorFactory;
import com.byterefinery.rmbench.external.model.IDataType;


/**
 * editor factory for the MySQL SET and ENUM data types
 * 
 * @author cse
 */
public class MySQLListTypeEditorFactory implements IDataTypeEditorFactory {

    public boolean openEditor(Shell shell, IDataType datatype) {
    	EnumAndSetDialog dialog;
    	if (datatype instanceof MySQLSetDataType) {
    		dialog = new EnumAndSetDialog(shell, Messages.MySQLSetFactory_DialogTitle, (MySQLListDatatype) datatype);
    	} else {
    		dialog = new EnumAndSetDialog(shell, Messages.MySQLEnumFactory_DialogTitle, (MySQLListDatatype) datatype);
    	}
        return (dialog.open()==Dialog.OK);
    }
}
