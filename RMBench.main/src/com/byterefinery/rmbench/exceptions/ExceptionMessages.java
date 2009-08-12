/**
 * created 30.04.2005, cse
 * 
 * $Id:ExceptionMessages.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.exceptions;

import org.eclipse.osgi.util.NLS;

/**
 * @author cse
 */
public class ExceptionMessages extends NLS {

    private static final String BUNDLE_NAME = ExceptionMessages.class.getName();

    public static String errorOccurred;
    public static String errorWritingStateFile;
    public static String errorReadingStateFile;
    public static String cantEstablishConnection;
    public static String errorDatabaseImport;
    public static String errorOpeningModelFile;
    public static String errorOpeningEditor;
    public static String errorTitle;
    public static String errorFileSave_message;
    public static String errorActivateModel;
    
    public static String invalidDiagramName;
    public static String invalidSchemaName;
    public static String invalidName;
    public static String reservedName;
    public static String duplicateName;

    public static String Message_SizeTooBig;
    public static String Message_ScaleTooBig;

    public static String PostgreSQL_reservedSchemaName;
    public static String DB2_reservedSchemaName;

    static {
	       NLS.initializeMessages(BUNDLE_NAME, ExceptionMessages.class);
	}
}
