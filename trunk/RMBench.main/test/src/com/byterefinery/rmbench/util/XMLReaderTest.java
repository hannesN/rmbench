/*
 * created 21.05.2005
 * 
 * $Id$
 */
package com.byterefinery.rmbench.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import com.byterefinery.rmbench.util.xml.XMLAttributes;
import com.byterefinery.rmbench.util.xml.XMLReader;

/**
 * @author cse
 */
public class XMLReaderTest extends TestCase {

    private static final String TESTXML1 = 
        "<list>"+
         "<element1 att1=\"test\">"+
          "<element2 att2=\"test\"/>"+
          "<element3 att3=\"test\">"+
           "<element4 att4=\"test\"/>"+
          "</element3>"+
         "</element1>"+
         "<element1 att1=\"test\">"+
          "<element2 att2=\"test\"/>"+
          "<element3 att3=\"test\">"+
           "<element4 att4=\"test\"/>"+
          "</element3>"+
         "</element1>"+
        "</list>";

    private static final String TESTXML2 = 
        "<list>"+
         "<element1 att1=\"test\">"+
          "<element2 att2=\"test\">"+
              "<element3 att3=\"test3\"/>"+
              "<element3 att3=\"test3\"/>"+
              "<element3 att3=\"test3\"/>"+
          "</element2>"+
          "<element4 att4=\"test4\"/>"+
         "</element1>"+
        "</list>";
    
    public void testParser1() throws Exception {
        XMLReader reader = new XMLReader(new StringReader(TESTXML1));
        
        reader.nextElement("list");
        XMLAttributes atts = reader.nextElement("element1");
        int count= 0;
        while(atts != null) {
            count++;
            assertEquals("test", atts.getString("att1"));
            
            atts = reader.nextElement("element2");
            assertEquals("test", atts.getString("att2"));
            
            atts = reader.nextElement("element3");
            assertEquals("test", atts.getString("att3"));
            
            atts = reader.nextElement("element4");
            assertEquals("test", atts.getString("att4"));
            
            atts = reader.nextElement("element1");
        }
        assertEquals(2, count);
    }
    
    public void testNestedList() throws Exception {
        XMLReader reader = new XMLReader(new StringReader(TESTXML2));
        verifyNestedList(reader);
    }

    public void testNestedListFile() throws Exception {
        String file = System.getProperty("xmlreadertest.file");
        assertNotNull("system property 'xmlreadertest.file' not set", file);
        XMLReader reader = new XMLReader(new FileReader(file));
        verifyNestedList(reader);
    }

    private void verifyNestedList(XMLReader reader) throws IOException {
        reader.nextElement("list");
        XMLAttributes atts = reader.nextElement("element1");
        int count1=0, count3=0;
        while(atts != null) {
            count1++;
            assertEquals("test", atts.getString("att1"));
            
            atts = reader.nextElement("element2");
            atts = reader.nextElement("element3");
            while(atts != null) {
                count3++;
                assertEquals("test3", atts.getString("att3"));
                atts = reader.nextElement("element3");
            }
            atts = reader.nextElement("element4");
            assertEquals("test4", atts.getString("att4"));

            atts = reader.nextElement("element1");
        }
        assertEquals(1, count1);
        assertEquals(3, count3);
    }
    
    public static void main(String[] args) {
        TestRunner.run(XMLReaderTest.class);
    }
}
