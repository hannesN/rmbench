/*
 * created 12.01.2006, cse
 * 
 * Copyright 2005, 2006 DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.text.DDLPartitionScanner;

/**
 * Utility for parsing the DDL statements from an underlying document. Since the document
 * may have been edited by the user, the executable statements have to be re-constructed 
 * from the document contents. This is conceptionally done by concatenating the non-comment 
 * regions, cutting them apart at each occurrence of the statement terminator, and replacing 
 * all groups of consecutive whitespace by one space character
 * 
 * @author cse
 */
public class ScriptUtil {

    private final String terminator;
    
	public ScriptUtil(String terminator) {
		this.terminator = terminator;
	}

    /**
     * parse the complete document and extract all statement strings by inspecting the non-comment
     * regions and finding all strings ending with the statement terminator 
     * 
     * @param document
     * @return the parsed statements
     */
	public IDDLScriptContext.Statement[] parseStatements(IDocument document) {
        
        List<IDDLScriptContext.Statement> statements = new ArrayList<IDDLScriptContext.Statement>();
        try {
            parseStatements(document, statements);
        } catch(BadLocationException x) {
            RMBenchPlugin.logError(x); //internal error
        }
		return (IDDLScriptContext.Statement[])statements.toArray(
                new IDDLScriptContext.Statement[statements.size()]);
	}

    /**
     * parse the statement at a given caret location 
     * @param document the document
     * @param caret the caret location
     * @return the statement at the location, or <code>null</code>
     */
    public IDDLScriptContext.Statement parseStatement(IDocument document, int caret) {
        try {
            return primParseStatement(document, caret);
        }
        catch (BadLocationException e) {
            RMBenchPlugin.logError(e); //internal error
            return null;
        }
    }
    
    /*
     * parse the statement at the given location
     */
    private IDDLScriptContext.Statement primParseStatement(IDocument document, int caret) 
    throws BadLocationException {
        
        int beginOffset = findStatementStart(document, caret);
        if(beginOffset < 0)
            return null;

        int startOffset = -1;
        StringBuffer statement = new StringBuffer();
        int docLength = document.getLength();
        boolean wasWhitespace = false;
        for(int currentOffset = beginOffset; currentOffset < docLength; currentOffset++) {
            
            if(document.getContentType(currentOffset) != DDLPartitionScanner.DDL_CODE)
                continue;
            
            if(isTerminator(document, currentOffset)) {
                return new IDDLScriptContext.Statement(
                        statement.toString(), 
                        startOffset, 
                        document.getLineOfOffset(startOffset),
                        currentOffset - startOffset);
            }

            char nextChar = document.getChar(currentOffset);
            if(Character.isWhitespace(nextChar)) {
                if(!wasWhitespace) {
                    wasWhitespace = true;
                    if(statement.length() > 0) //no WS at start of statement
                        statement.append(' '); //only one space
                }
            }
            else {
                wasWhitespace = false;
                if(startOffset < 0) startOffset = currentOffset;
                statement.append(nextChar);
            }
        }
        return statement.length() > 0 ?
                new IDDLScriptContext.Statement(
                        statement.toString(), 
                        startOffset, 
                        document.getLineOfOffset(startOffset),
                        docLength - startOffset) : null; 
    }
    
    /*
     * find the start of the statement at the caret location. Note that this may include leading
     * whitespace.
     * @return the offset of the statement start, or -1 if the caret is not inside a code region
     */
    private int findStatementStart(IDocument document, int caret) throws BadLocationException {
        
        if(document.getContentType(caret) != DDLPartitionScanner.DDL_CODE)
            return -1;
        
        int minCodeOffset = -1;
        if(isTerminator(document, caret))
            //if the caret is in front of the terminator, we go for the previous statement
            caret--;
        for(int currentOffset = caret; currentOffset >= 0; currentOffset--) {
            if(document.getContentType(currentOffset) != DDLPartitionScanner.DDL_CODE)
                continue;
            if(isTerminator(document, currentOffset))
                return currentOffset + terminator.length();
            minCodeOffset = currentOffset;
        }
        return minCodeOffset;
    }

    /*
     * parse statements and add them to the list
     */
    private void parseStatements(
    		IDocument document, List<IDDLScriptContext.Statement> statements) throws BadLocationException {
        StringBuffer stmtBuffer = new StringBuffer();
        ITypedRegion[] regions = TextUtilities.computePartitioning(
                document, IDocumentExtension3.DEFAULT_PARTITIONING, 0, document.getLength(), false);
        
        boolean wasWhitespace = false;
        int startOffset = -1;
        
        for(int i=0; i<regions.length; i++) {
            if(regions[i].getType() == DDLPartitionScanner.DDL_CODE) {
                for(int j=0; j<regions[i].getLength(); j++) {
                    
                    int currentOffset = regions[i].getOffset()+j;
                    if(isTerminator(document, currentOffset)) {
                        IDDLScriptContext.Statement statement = new IDDLScriptContext.Statement(
                                stmtBuffer.toString(), 
                                startOffset, 
                                document.getLineOfOffset(startOffset),
                                currentOffset - startOffset);
                        
                        statements.add(statement);
                        stmtBuffer.setLength(0);
                        startOffset = -1;
                        j += terminator.length() - 1;
                    }
                    else {
                        char nextChar = document.getChar(currentOffset);
                        if(Character.isWhitespace(nextChar)) {
                            if(!wasWhitespace) {
                                wasWhitespace = true;
                                if(stmtBuffer.length() > 0) //no WS at start of statement
                                    stmtBuffer.append(' '); //only one space
                            }
                        }
                        else {
                            wasWhitespace = false;
                            if(startOffset < 0) startOffset = currentOffset;
                            stmtBuffer.append(nextChar);
                        }
                    }
                }
            }
        }
    }

    /*
     * determine if the current offset is the start of a terminator
     */
    private boolean isTerminator(IDocument document, int offset) throws BadLocationException {
        for(int i=0; i<terminator.length(); i++) {
            if(document.getChar(offset+i) != terminator.charAt(i))
                return false;
        }
        return true;
    }
}
