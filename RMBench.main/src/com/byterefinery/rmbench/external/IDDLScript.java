/*
 * created 19.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IExportWriter.java 59 2005-12-03 20:25:23Z csell $
 */
package com.byterefinery.rmbench.external;





/**
 * abstraction for a DDL script generated by a DDL generator. It has the following responsibilities:
 * <ul>
 * <li>serve as a factory for statements , which in turn are used to store the generated DDL code</li>
 * <li>serve as a container for the generated DDL statements</li>
 * <li>determine general properties of the script, like the order of statements, comment lines etc.</li>
 * </ul> 
 * The statements returned by the factory method {@link #createStatement(String)} carry a type 
 * identifier, which is a hint to the code generator backend.<p/>
 * 
 * @author cse
 */
public interface IDDLScript {

    /**
     * the character sequence used as a statement terminator by default
     */
    String DEFAULT_TERMINATOR = ";";
    
    /**
     * type constants for create statements
     */
    String CREATE_STATEMENT = "create";

    /**
     * type constants for alter statements
     */
    String ALTER_STATEMENT = "alter";

    /**
     * type constants for drop statements
     */
    String DROP_STATEMENT = "drop";
    
    /**
     * the factory used to create new scripts
     */
    public interface Factory {
        IDDLScript createScript(IDDLGenerator generator);
    }
    
    /**
     * helper class that represents a text range in the generated DDL script
     */
    public class Range {
        public int position;
        public int length;
    }
    
    /**
     * a context groups together statements generated for a given model change, and provides 
     * access to the text ranges for these statements  
     */
    public interface Context {
        /**
         * @return the ranges in the generated script that correspond to this context, or 
         * <code>null</code> if that information is not available
         */
        Range[] getRanges();
    }
    
    /**
     * a writer for appending to the text buffer underlying a script 
     */
    public interface Writer {
        void print(String string, int startIndex, int endIndex);
        void print(String string);
        void println();
        void indent();
        void dedent();
        void indent(int level);
        void dedent(int level);
    }
    
    /**
     * a statement in the context of a DDL script
     */
    public interface Statement {
        /**
         * @return the statement kind, usually one of 
         * <ul>
         * <li>{@link IDDLScript#CREATE_STATEMENT}</li>
         * <li>{@link IDDLScript#ALTER_STATEMENT}</li>
         * <li>{@link IDDLScript#DROP_STATEMENT}</li>
         * </ul> 
         */
        String getKind();
        
        /**
         * append the given text to the internal buffer of this statement
         * @param text the statement text
         */
        void append(String text);
        
		/**
         * append the given character to the internal buffer of this statement
		 * @param character
		 */
		void append(char character);
		
        /**
         * @return the DDL statement string
         */
        String getString();
    }
    
    /**
     * create a new statement and register it with this object
     * 
     * @param type the statement type, one of
     * <ul>
     * <li>{@link IDDLScript#CREATE_STATEMENT}</li>
     * <li>{@link IDDLScript#ALTER_STATEMENT}</li>
     * <li>{@link IDDLScript#DROP_STATEMENT}</li>
     * </ul> 
     * @return a new empty statement
     * @see #generate(IDDLFormatter)
     */
    Statement createStatement(String type);

    /**
     * start a statement generation context. Clients must call this method to indicate their 
     * intention to later retreive the context via {@link #endStatementContext()}   
     */
    void beginStatementContext();

    /**
     * @return the current statement generation context
     */
    Context endStatementContext();

	/**
	 * @return the last generated statement, or null if no statemenets were generated (yet)
	 */
	Statement getLastStatement();
	
    /**
     * generate the DDL script from the currently registered statements
     * 
     * @param formatter the formatter to use
     * @return the readily formatted script
     */
    String generate(IDDLFormatter formatter);

    /**
     * @return the statement terminator used by this script
     */
    String getStatementTerminator();

    /**
     * reset the contents
     */
	void reset();
}
