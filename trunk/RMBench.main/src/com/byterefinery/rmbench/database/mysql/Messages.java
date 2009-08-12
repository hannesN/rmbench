/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 90 2006-01-06 19:11:33Z csell $
 */
package com.byterefinery.rmbench.database.mysql;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends  com.byterefinery.rmbench.database.sql99.Messages {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.database.mysql.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String MySQLWizard_tableType;
	public static String MySQLEnumFactory_DialogTitle;
	public static String MySQLSetFactory_DialogTitle;
	public static String MySQLIntegerDataType_NumberFormatExceptionText;
	public static String IntegerFlagDialog_AUTO_INCREMENT;
	public static String IntegerFlagDialog_ZEROFILL;
	public static String IntegerFlagDialog_UNSIGNED;
	public static String IntegerFlagDialog_Title;
}
