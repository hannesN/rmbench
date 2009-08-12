/*
 * created 03.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ParserErrors.java 148 2006-01-30 20:40:59Z csell $
 */
package com.byterefinery.rmbench.util.xml;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author cse
 */
public class ParserErrors {

    private static final String BUNDLE_NAME = ParserErrors.class.getName();

    public static String messagePrefix;
    public static String startTagExpected;
    public static String endTagExpected;
    public static String tagExpected;
    public static String errorUndefinedDbInfo;
    public static String errorUndefinedType;
    public static String errorUndefinedSchema;
    public static String warnUndefinedSchemaInDiagram;
    public static String errorUndefinedTable;
    public static String errorUndefinedGenerator;

    public static String message(String fileName, int lineNumber, String message, Object[] args) {
        Object[] prefixArgs = new Object[]{fileName, new Integer(lineNumber)};
        String prefix = MessageFormat.format(messagePrefix, prefixArgs);
        
        return args != null ? 
                prefix + " "+MessageFormat.format(message, args) : 
                    prefix + " "+message; 
    }
    
    static {
           NLS.initializeMessages(BUNDLE_NAME, ParserErrors.class);
    }
}
