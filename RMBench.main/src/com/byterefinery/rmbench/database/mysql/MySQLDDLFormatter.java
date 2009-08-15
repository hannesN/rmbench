/*
 * created 14.08.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.export.DDLFormatter;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript.Statement;
import com.byterefinery.rmbench.external.IDDLScript.Writer;

/**
 * Formatter to handle writing the new ALTER_DROP_STATEMENT 
 * type.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLDDLFormatter extends DDLFormatter {
    public static class Factory implements IDDLFormatter.Factory {
        public IDDLFormatter createFormatter(IDDLGenerator generator) {
            return new MySQLDDLFormatter();
        }
    }
    
    /* (non-Javadoc)
     * @see com.byterefinery.rmbench.extension.IDDLFormatter#format(com.byterefinery.rmbench.extension.IDDLScript.Statement, java.lang.String, com.byterefinery.rmbench.extension.IDDLScript.Writer)
     */
    public void format(Statement statement, String terminator, Writer writer) {
        if (statement.getKind()==MySQLDDLScript.ALTER_DROP_STATEMENT) {
            writer.print(statement.getString());
            writer.print(terminator);
            writer.println();
        } else
            super.format(statement, terminator, writer);
    }
    
}
