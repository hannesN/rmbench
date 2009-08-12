/*
 * created 14.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.IShellProvider;

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.model.dbimport.DBModel;

/**
 * provides the context for the execution of a DDL script
 * 
 * @author cse
 */
public interface IDDLScriptContext extends IShellProvider {

    public class Statement {
        /** the normalized statement text */
        public final String text;
        /** the document offset of the first character of the statement */
        public final int offset;
        /** the length of the document range from which the statement was parsed */
        public final int length;
        /** the line number of the starting character */
        public final int lineNumber;
        
        /**
         * @param text the normalized statement text
         * @param offset the offset of the first character of the statement
         * @param lineNumber the line number of the starting character
         * @param length the length of the document range from which the statement was parsed
         */
        public Statement(String text, int offset, int lineNumber, int length) {
            this.text = text;
            this.offset = offset;
            this.lineNumber = lineNumber;
            this.length = length;
        }
    }

    /**
     * a statement is about to be executed. Update the display accordingly 
     */
    void aboutToExecute(Statement statement);
    
    /**
     * @param statement the statement that was executed
     * @param error any exception that occurred, or <code>null</code>. Note that the actual 
     * error might be available through <code>error.getCause()</code> 
     */
    void executed(Statement statement, SystemException error);
    
    /**
     * @return the currently selected statement, e.g. the statement under the cursor
     */
    Statement getSelectedStatement();
    
    /**
     * @return the statements from the underlying script, either as generated or 
     * as edited by the user 
     */
    Statement[] getAllStatements();
    
    /**
     * @return the connection to use for statement execution
     */
    DBModel getSelectedDBModel();

    /**
     * @return the progress monitor
     */
    IProgressMonitor getProgressMonitor();
}
