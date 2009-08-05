/*
 * created 21.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DB2MessageProvider.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.database.db2;

import java.text.MessageFormat;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.external.database.DBMessageProvider;

/**
 * a provider for error messages that map to error keys produced by the 
 * {@link com.byterefinery.rmbench.database.db2.DB2} database class
 * 
 * @author cse
 */
public class DB2MessageProvider extends DBMessageProvider {

    public String getMessage(String key, String argument) {
        if(DB2.RESERVED_SCHEMA_NAME.equals(key)) {
            return MessageFormat.format(
                    ExceptionMessages.DB2_reservedSchemaName, 
                    new Object[]{argument});
        }
            
        return super.getMessage(key, argument);
    }
}
