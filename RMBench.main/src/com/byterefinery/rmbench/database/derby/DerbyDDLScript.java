/*
 * created 23.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.derby;

import java.util.Iterator;

import com.byterefinery.rmbench.export.DDLScript;
import com.byterefinery.rmbench.export.DDLStatement;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;

/**
 * @author Hannes Niederhausen
 *
 */
public class DerbyDDLScript extends DDLScript {
    public static final class Factory implements IDDLScript.Factory {
        public IDDLScript createScript(IDDLGenerator generator) {
            return new DerbyDDLScript();
        }
    }
    
    public static String ALTER_DROP_STATEMENT = "alter drop";
    
    protected DerbyDDLScript() {
    }
    
    public String generate(IDDLFormatter formatter) {
        scriptWriter.reset();
        
        Iterator<DDLStatement> iter = new StatementIterator(ALTER_DROP_STATEMENT);
        if(iter.hasNext()) {
            println("-- === dropping alter statements ===");
            do {
                printStatement(formatter, iter.next());
            } while(iter.hasNext());
        }
        
        iter = new StatementIterator(IDDLScript.DROP_STATEMENT);
        if(iter.hasNext()) {
            println("-- === drop statements ===");
            do {
                printStatement(formatter, iter.next());
            } while(iter.hasNext());
        }
        iter = new StatementIterator(IDDLScript.CREATE_STATEMENT);
        if(iter.hasNext()) {
            println("-- === create statements ===");
            do {
                printStatement(formatter, iter.next());
            } while(iter.hasNext());
        }
        iter = new StatementIterator(IDDLScript.ALTER_STATEMENT);
        if(iter.hasNext()) {
            println("-- === alter statements ===");
            do {
                printStatement(formatter, iter.next());
            } while(iter.hasNext());
        }
        return scriptWriter.getString();
    }
}
