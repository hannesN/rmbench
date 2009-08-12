/*
 * created 06.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
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
