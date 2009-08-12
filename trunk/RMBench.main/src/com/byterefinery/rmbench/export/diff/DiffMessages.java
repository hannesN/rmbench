/*
 * created 02.08.2005 by sell
 *
 * $Id$
 */
package com.byterefinery.rmbench.export.diff;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class DiffMessages extends NLS {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.export.diff.DiffMessages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, DiffMessages.class);
	}

	public static String Add_primary_key;
	public static String Modify_column;
	public static String Modify_ForeignKey;
	public static String Delete_primary_key;
	public static String Modify_primary_key;
}
