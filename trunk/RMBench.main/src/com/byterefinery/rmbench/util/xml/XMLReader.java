/*
 * created 21.05.2005
 * 
 * $Id: XMLReader.java 110 2006-01-22 01:32:19Z csell $
 */
package com.byterefinery.rmbench.util.xml;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.byterefinery.rmbench.exceptions.SystemException;

/**
 * a reader for simple XML files that offers a pull interface on top of a SAX
 * parser
 * 
 * @author cse
 */
public class XMLReader {

    public static final long TIMEOUT = 1000;
    
    private final String elementMonitor = "";
    
    private String currentElement;
    private Attributes currentAttributes;
    private boolean finished = false;
    private XMLAttributes attributes = new XMLAttributes();
    private Exception exception;
    
    /**
     * run a parser on a reader
     * @param reader the parseable input
     * @throws SystemException 
     */
    public XMLReader(Reader reader) throws SystemException {
        
        parse(reader);
    }

    public Exception getException() {
        return exception;
    }

    /**
     * read the next element from the input
     * 
     * @param element the element name
     * @return the attributes associated with the element, or <code>null</code> if
     * the element was not available
     * 
     * @throws SystemException
     */
    public XMLAttributes nextElement(String element) throws IOException {
        synchronized(elementMonitor) {
            if(!finished && currentElement == null) {
                try {
                    elementMonitor.wait(TIMEOUT);
                }
                catch (InterruptedException e) {
                    throw new IOException(e.getMessage());
                }
            }
            if(finished)
                return null;
            
            if(!currentElement.equals(element)) {
                //elementMonitor.notify();
                return null;
            }
            attributes.reset();
            for(int i=0; i<currentAttributes.getLength(); i++) {
                attributes.addAttribute(
                        currentAttributes.getQName(i), 
                        currentAttributes.getValue(i));
            }
            currentElement = null;
            elementMonitor.notify();
            return attributes;
        }
    }

    /*
     * start parsing in a separate thread
     * @param reader the parseable input
     * @throws SystemException
     */
    private void parse(Reader reader) throws SystemException {
        try  {
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            final InputSource in = new InputSource(reader);
            
            Runnable runnable = new Runnable() {

                public void run() {
                    try {
                        parser.parse(in, new XMLHandler());
                    }
                    catch (Exception e) {
                        exception = e;
                    }
                    finally {
                        synchronized(elementMonitor) {
                            finished = true;
                            elementMonitor.notify();
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }
    
    private class XMLHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qName, Attributes attributes) 
            throws SAXException {
            
            synchronized(elementMonitor) {
                currentElement = qName;
                currentAttributes = attributes;
                
                elementMonitor.notify();
                try {
                    elementMonitor.wait(TIMEOUT);
                }
                catch (InterruptedException e) {
                    exception = e;
                }
            }
        }
    }
}
