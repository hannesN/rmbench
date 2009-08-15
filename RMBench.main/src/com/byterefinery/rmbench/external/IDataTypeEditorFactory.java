/*
 * created 06.11.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
