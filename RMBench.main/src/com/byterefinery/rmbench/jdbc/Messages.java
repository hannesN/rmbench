/*
 * created 06.02.2006
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
package com.byterefinery.rmbench.jdbc;

import org.eclipse.osgi.util.NLS;

/**
 * @author cse
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
 }
    
    public static String SQLException;
    public static String JDBCInvalidDriver;
    public static String unknownDatatype;
    public static String errorLoadingTable;
}
