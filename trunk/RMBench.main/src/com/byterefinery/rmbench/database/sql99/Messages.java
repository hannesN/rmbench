/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 90 2006-01-06 19:11:33Z csell $
 */
package com.byterefinery.rmbench.database.sql99;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.database.sql99.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

    public static String SQL99Wizard_quoteIdentifiers;
    public static String SQL99Wizard_quoteObjects;
    public static String SQL99Wizard_quoteColumns;
    public static String SQL99Wizard_uppercaseKeywords;
    public static String SQL99Wizard_exportPKs;
}
