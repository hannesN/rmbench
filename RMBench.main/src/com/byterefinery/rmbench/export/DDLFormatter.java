/*
 * created 09.01.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDDLScript.Statement;

/**
 * this is a fairly simple formatter that only handles create statements, by scanning for the 
 * first occurrence of the keyword <em>CREATE</em>, and the first following opening bracket.
 * After that it increases the indent by 1 level. It then scans for the last closing bracket, 
 * and decreases the indent before it. In between these, a new line is written after every 
 * occurrence of a comma character. Constraint statements are also taken into consideration<p>
 * 
 * Keyword scans are case-insensitive
 * 
 * @author cse
 */
public class DDLFormatter implements IDDLFormatter {

    public static class Factory implements IDDLFormatter.Factory {
        public IDDLFormatter createFormatter(IDDLGenerator generator) {
            return new DDLFormatter();
        }
    }
    
    /*
     * internal representation of a case-insensitive keyword 
     */
    private static class Keyword {
        final String upper;
        final String lower;
        
        Keyword(String word) {
            upper = word.toUpperCase();
            lower = word.toLowerCase();
        }

        /**
         * @param string the string to search
         * @return the index of the keyword, or -1
         */
        public int getIndex(String string, int startIndex) {
            int pos = string.indexOf(upper, startIndex);
            return pos >= 0 ? pos : string.indexOf(lower, startIndex);
        }
    }
	private static final Keyword CREATE = new Keyword("create");
    private static final Keyword CONSTRAINT = new Keyword("constraint");
    
    public void format(Statement statement, String terminator, IDDLScript.Writer writer) {
        if(IDDLScript.CREATE_STATEMENT == statement.getKind()) {
        	String string = statement.getString();
        	int endBracket = -1;
            int startBracket = CREATE.getIndex(string, 0);
        	if(startBracket >= 0) {
        		startBracket = string.indexOf('(', startBracket);
        		if(startBracket >= 0)
        			endBracket = string.lastIndexOf(')');
        	}
        	if(endBracket > 0) {
                int constraintPos = CONSTRAINT.getIndex(string, startBracket);
                
        		writer.print(string, 0, startBracket+1);
        		writer.println();
        		writer.indent();
        		int comma = startBracket + 1;
        		int nextComma = findColumnEnd(string, comma);
                
        		while(nextComma > 0 && (constraintPos < 0 || nextComma < constraintPos)) {
                    comma = skipWhitespace(string, comma);
        			writer.print(string, comma, nextComma+1);
        			writer.println();
            		comma = nextComma + 1;
            		nextComma = findColumnEnd(string, comma);
        		}
                if(constraintPos > 0) {
                    int nextConstraint = CONSTRAINT.getIndex(string, constraintPos+1);
                    while(nextConstraint > 0) {
                        int constraintEnd = rskipWhitespace(string, nextConstraint);
                        writer.print(string, constraintPos, constraintEnd);
                        constraintPos = nextConstraint;
                        nextConstraint = CONSTRAINT.getIndex(string, constraintPos+1);
                    }
                    writer.print(string, constraintPos, endBracket);
                } else {
                    comma = skipWhitespace(string, comma);
        			writer.print(string, comma, endBracket);
                }
    			writer.println();
        		writer.dedent();
        		writer.print(string, endBracket, string.length());
        		writer.print(terminator);
        	}
        	else {
                writer.print(statement.getString());
        		writer.print(terminator);
        	}
            writer.println();
        }
        else if(IDDLScript.DROP_STATEMENT == statement.getKind()) {
            writer.print(statement.getString());
    		writer.print(terminator);
            writer.println();
        }
        else if(IDDLScript.ALTER_STATEMENT == statement.getKind()) {
            writer.print(statement.getString());
    		writer.print(terminator);
            writer.println();
        }
    }

    /*
     * find the ending comma of a column definition, skipping possible size/scale specifications
     */
    private int findColumnEnd(String string, int start) {
        int nextComma = string.indexOf(',', start);
        if(nextComma >= 0) {
            int nextBracket = string.indexOf('(', start);
            if(nextBracket >= 0 && nextBracket < nextComma) {
                nextBracket = string.indexOf(')', nextBracket);
                nextComma = string.indexOf(',', nextBracket);
            }
        }
        return nextComma;
    }

    private int skipWhitespace(String string, int pos) {
        for(; Character.isWhitespace(string.charAt(pos)); pos++) ;
        return pos;
    }

    private int rskipWhitespace(String string, int pos) {
        for(; Character.isWhitespace(string.charAt(pos)); pos--) ;
        return pos;
    }
}
