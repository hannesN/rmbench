/*
 * created 23.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.derby;

import com.byterefinery.rmbench.database.mysql.MySQLDDLScript;
import com.byterefinery.rmbench.export.DDLFormatter;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript.Statement;
import com.byterefinery.rmbench.external.IDDLScript.Writer;

/**
 * @author Hannes Niederhausen
 *
 */
public class DerbyDDLFormatter extends DDLFormatter {
    public static class Factory implements IDDLFormatter.Factory {
        public IDDLFormatter createFormatter(IDDLGenerator generator) {
            return new DerbyDDLFormatter();
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
