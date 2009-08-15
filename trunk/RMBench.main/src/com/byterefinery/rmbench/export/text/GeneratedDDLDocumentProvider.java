/*
 * created 01.01.2006
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
package com.byterefinery.rmbench.export.text;


/**
 * a document provider that is asscoiated with {@link com.byterefinery.rmbench.export.DDLEditorInput} 
 * input types. All it does is force a saveAs operation by overriding the {@link #isDeleted(Object)} 
 * method to always return <code>true</code>.
 * 
 * @author cse
 */
public class GeneratedDDLDocumentProvider extends DDLDocumentProvider {

    /**
     * @return <code>true</code> to force a saveAs operation
     */
    public boolean isDeleted(Object element) {
        //force saveAs for generated ExportEditorInput
        return true;
    }
}
