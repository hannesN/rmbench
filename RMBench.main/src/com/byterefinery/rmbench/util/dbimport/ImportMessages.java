/*
 * created 25.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ImportMessages.java 127 2006-01-26 18:26:00Z thomasp $
 */
package com.byterefinery.rmbench.util.dbimport;

import org.eclipse.osgi.util.NLS;

/**
 * @author cse
 */
public class ImportMessages {

    public static String importForeignKey_errorTargetTable;
    public static String importForeignKey_errorTargetSchema;
    public static String importColumn_errorConvertType;
    public static String importTable_errorParentSchema;
    
    private static final String BUNDLE_NAME = 
        "com.byterefinery.rmbench.util.dbimport.ImportMessages"; //$NON-NLS-1$
    static {
           NLS.initializeMessages(BUNDLE_NAME, ImportMessages.class);
    }
}
