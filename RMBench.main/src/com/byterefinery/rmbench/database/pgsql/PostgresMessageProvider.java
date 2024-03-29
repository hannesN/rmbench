/*
 * created 21.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: PostgresMessageProvider.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.database.pgsql;

import java.text.MessageFormat;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.external.database.DBMessageProvider;

/**
 * a provider for error messages that map to error keys produced by the 
 * {@link com.byterefinery.rmbench.database.pgsql.PostgreSQL} database class
 * 
 * @author cse
 */
public class PostgresMessageProvider extends DBMessageProvider {

    public String getMessage(String key, String argument) {
        if(PostgreSQL.RESERVED_SCHEMA_NAME.equals(key)) {
            return MessageFormat.format(
                    ExceptionMessages.PostgreSQL_reservedSchemaName, 
                    new Object[]{argument});
        }
            
        return super.getMessage(key, argument);
    }
}
