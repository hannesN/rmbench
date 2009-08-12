/*
 * created 22.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * utility class for writing formatted output. If indent level is > 0, avery line start will 
 * be prefixed with one instance of the indent string per level
 * 
 * @author cse
 */
public class ScriptWriter {

    private static final int SEP_LENGTH = System.getProperty("line.separator").length(); 
    
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    
    private int printPosition;
    private int indentLevel;
    private boolean lineStart = true;
    
    private boolean useTabs = false;
    private String indentString = "    ";

    
    public ScriptWriter() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        printPosition = 0;
    }
    
    public void setIndent(String indent) {
        this.indentString = indent;
    }
    
    public void reset() {
        if(printWriter != null) {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            printPosition = 0;
        }
    }
    
    public void println(String line) {
        if(lineStart && line.length() > 0) writeIndent();
        printPosition += line.length() + SEP_LENGTH;
        printWriter.println(line);
        lineStart = true;
    }
    
    public void println() {
        printPosition += SEP_LENGTH;
        printWriter.println();
        lineStart = true;
    }
    
    public void print(String text, int startIndex, int endIndex) {
        if(lineStart && text.length() > 0) writeIndent();
        int length = endIndex - startIndex;
        printPosition += length;
        printWriter.print(text.substring(startIndex, endIndex));
        lineStart = lineStart && length == 0;
    }

    public void print(String text) {
        if(lineStart && text.length() > 0) writeIndent();
        printPosition += text.length();
        printWriter.print(text);
        lineStart = lineStart && text.length() == 0;
    }
    
    /**
     * increase the indent by one level
     */
    public void indent() {
        indent(1);
    }

    /**
     * decrease the indent by one level
     */
    public void dedent() {
        dedent(1);
    }

    /**
     * @param level number of levels by which to increase the indent
     */
    public void indent(int level) {
        indentLevel += level;
    }

    /**
     * @param level number of levels by which to decrease the indent
     */
    public void dedent(int level) {
        indentLevel -= level;
    }

    /**
     * @return the result string
     */
    public String getString() {
        return stringWriter.toString();
    }

    /**
     * @return the current position
     */
    public int getPosition() {
        return printPosition;
    }
    
    private void writeIndent() {
        if(useTabs) {
            for(int i=0; i<indentLevel; i++) {
                printWriter.print('\t');
                printPosition++;
            }
        }
        else {
            for(int i=0; i<indentLevel; i++) {
                printWriter.print(indentString);
                printPosition += indentString.length();
            }
        }
    }
}
