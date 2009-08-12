/*
 * created 06.11.2006
 *
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external;

import org.eclipse.swt.widgets.Shell;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * Factory interface for creating editors on custom data types that require additional
 * specific configuration
 * 
 * @author cse
 */
public interface IDataTypeEditorFactory {

    /**
     * create and open a custom type editor dialog 
     * @param datatype the data type object
     * @return <code>true</code> if the datatype was changed, else <code>false</code>  
     */
    boolean openEditor(Shell shell, IDataType datatype);
}
