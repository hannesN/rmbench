/*
 * created 21.05.2005
 * 
 * $Id:XMLWriter.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util.xml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * simple dedicated XML writer without namespace support
 * 
 * @author cse
 */
public class XMLWriter {

    private final Writer writer;
    private final PrintWriter pwriter;
    
    private int indentLevel = 0;
    private String indentString = "    ";

    public XMLWriter(Writer writer) {
        this.writer = writer;
        this.pwriter = new PrintWriter(writer);
        this.pwriter.println("<?xml version=\"1.0\"?>");
    }

    public void close() throws IOException {
        writer.close();
    }

    public void startElement(String element) throws IOException {
        startElement(element, null);
    }

    public void startElement(String element, XMLAttributes attributes) throws IOException {
        writeIndent();
        pwriter.print("<");
        pwriter.print(element);
        writeAttributes(attributes);
        pwriter.println(">");
        indentLevel++;
    }

    public void endElement(String element) {
        indentLevel--;
        writeIndent();
        pwriter.print("</");
        pwriter.print(element);
        pwriter.println(">");
    }

    public void emptyElement(String element, XMLAttributes attributes) {
        writeIndent();
        pwriter.print("<");
        pwriter.print(element);
        writeAttributes(attributes);
        pwriter.println("/>");
    }
    
    private void writeAttributes(XMLAttributes attributes) {
        if(attributes != null) {
            for (Iterator<?> it = attributes.iterator(); it.hasNext();) {
                Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
                pwriter.print(" ");
                pwriter.print(entry.getKey());
                pwriter.print("=\"");
                pwriter.print(entry.getValue());
                pwriter.print("\"");
            }
        }
    }
    
    private void writeIndent() {
        for(int i=0; i<indentLevel; i++) {
            pwriter.print(indentString);
        }
    }
}
