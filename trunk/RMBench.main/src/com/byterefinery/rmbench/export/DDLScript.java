/*
 * created 29.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.util.ScriptWriter;

/**
 * default implementation of IDDLScript, which will generate statement in this order:
 * <ol>
 * <li>drop statements, if requested</li>
 * <li>create statements</li>
 * <li>alter statements</li>
 * </ol>
 * Statements will be terminated by a ';'<p>
 * Between each category, there will be a comment line that introduces the statement type
 * 
 * @author cse
 */
public class DDLScript implements IDDLScript, IDDLScript.Writer {

    public static final class Factory implements IDDLScript.Factory {
        public IDDLScript createScript(IDDLGenerator generator) {
            return new DDLScript();
        }
    }
    
    protected class StatementIterator implements Iterator<DDLStatement> {

        private final Iterator<DDLStatement> baseIterator;
        private final String kind;
        private DDLStatement next;
        
        public StatementIterator(String kind) {
            this.kind = kind;
            this.baseIterator = statements.iterator();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            if(next == null) {
                findNext();
                return next != null;
            }
            return true;
        }

        public DDLStatement next() {
            if(!hasNext())
                throw new NoSuchElementException();
            DDLStatement tmp = next;
            next = null;
            return tmp;
        }
        
        private void findNext() {
            while(baseIterator.hasNext()) {
                DDLStatement stmt = baseIterator.next();
                if(stmt.getKind() == kind) {
                    next = stmt;
                    return;
                }
            }
        }
    }
    
    protected class ContextImpl implements IDDLScript.Context {

        private final List<DDLStatement> statements = new ArrayList<DDLStatement>(2);
        private Range[] ranges;
        
        public Range[] getRanges() {
            if(ranges == null) {
                ranges = new Range[statements.size()];
                for (int i = 0; i < ranges.length; i++) {
                    ranges[i] = ((DDLStatement)statements.get(i)).getRange();
                }
            }
            return ranges;
        }

        void addStatement(DDLStatement statement) {
            statements.add(statement);
        }

		public void reset() {
			this.statements.clear();
			this.ranges = null;
		}
    }
    
    private List<DDLStatement> statements = new ArrayList<DDLStatement>();
    private ContextImpl context;
    private String terminator = IDDLScript.DEFAULT_TERMINATOR;
    
    protected ScriptWriter scriptWriter = new ScriptWriter();
    
    /**
     * constructor is only accessible through {@link Factory#createScript(IDDLGenerator)}
     */
    protected DDLScript() {
    }
    
    public Statement createStatement(String type) {
        DDLStatement statement = new DDLStatement(type);
        statements.add(statement);
        if(context != null) {
            context.addStatement(statement);
        }
        return statement;
    }

    /**
     * setup a statement generation context
     */
    public void beginStatementContext() {
        context = new ContextImpl();
    }

    /**
     * @return the statement generated in the current context
     */
    public Context endStatementContext() {
        ContextImpl result = context;
        context = null;
        return result;
    }

    public String generate(IDDLFormatter formatter) {
        scriptWriter.reset();
        
        Iterator<DDLStatement> iter = new StatementIterator(IDDLScript.DROP_STATEMENT);
        if(iter.hasNext()) {
            println("-- === drop statements ===");
            do {
                DDLStatement stmt = (DDLStatement) iter.next();
                printStatement(formatter, stmt);
            } while(iter.hasNext());
        }
        iter = new StatementIterator(IDDLScript.CREATE_STATEMENT);
        if(iter.hasNext()) {
            println("-- === create statements ===");
            do {
                DDLStatement stmt = (DDLStatement) iter.next();
                printStatement(formatter, stmt);
            } while(iter.hasNext());
        }
        iter = new StatementIterator(IDDLScript.ALTER_STATEMENT);
        if(iter.hasNext()) {
            println("-- === alter statements ===");
            do {
                DDLStatement stmt = (DDLStatement) iter.next();
                printStatement(formatter, stmt);
            } while(iter.hasNext());
        }
        return scriptWriter.getString();
    }

    public Statement getLastStatement() {
		return statements.isEmpty() ? null : (Statement)statements.get(statements.size() - 1);
	}
    
	/*
     * format and print the statement, and set the range information
     */
    protected void printStatement(IDDLFormatter formatter, DDLStatement stmt) {
        Range range = new Range();
        range.position = scriptWriter.getPosition();
        formatter.format(stmt, terminator, this);
        range.length = scriptWriter.getPosition() - range.position;
        
        stmt.setRange(range);
    }

    public void println(String line) {
        scriptWriter.println(line);
    }
    
    public void println() {
        scriptWriter.println();
    }
    
    public void print(String text, int startIndex, int endIndex) {
        scriptWriter.print(text, startIndex, endIndex);
    }

    public void print(String text) {
        scriptWriter.print(text);
    }
    
	public void indent() {
        scriptWriter.indent();
    }

    public void dedent() {
        scriptWriter.dedent();
    }

    public void indent(int level) {
        scriptWriter.indent(level);
    }

    public void dedent(int level) {
        scriptWriter.dedent(level);
    }

    public String getStatementTerminator() {
        return terminator;
    }

	public void reset() {
		statements.clear();
		if(context != null)
			context.reset();
		scriptWriter.reset();
	}
}
