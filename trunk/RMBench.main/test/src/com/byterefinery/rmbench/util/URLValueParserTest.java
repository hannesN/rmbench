package com.byterefinery.rmbench.util;

import com.byterefinery.rmbench.extension.VariableDescriptor;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class URLValueParserTest extends TestCase {
    final VariableDescriptor descriptors[] = { new VariableDescriptor("host", "string", "optional", "Host:", "localhost"),
            new VariableDescriptor("port", "int", "optional", "Port:", "12345"),
            new VariableDescriptor("database", "string", "optional", "Database:", null),
            new VariableDescriptor("autocommit", "boolean", "optional", "Autocommit:", null),
            new VariableDescriptor("loglevel", "int", "optional", "Loglevel:", null)
            };

    final String template = "jdbc:postgresql://${host}:${port}/${database}";
    
    final String template2 = "jdbc:postgresql://${host}:${port}/${database}?autocommit=${autocommit}&loglevel=${loglevel}";
    
    final String correctUrls[] = {
            "jdbc:postgresql://localhost:12345/testdb",
            "jdbc:postgresql://localhost/testdb",
            "jdbc:postgresql:testdb",
            "jdbc:postgresql://localhost:12345/test?autocommit=true&loglevel=2",
    };
    final String falseUrls[] = {
            "jdbc:postgresql::12345/testdb",
            "jdbc:postgresql:/testdb",
    };
    
    public void testURLParser() {
        URLValueParser parser = new URLValueParser(template2, descriptors);
        
        parser.parseUrl(correctUrls[0]);
        assertEquals("jdbc:postgresql://localhost:12345/testdb", parser.getValidURL());
        
    }
    
    public void testURLAppendices() {
        URLValueParser parser = new URLValueParser(template2, descriptors);
        
        parser.parseUrl(correctUrls[0]);
        assertEquals("jdbc:postgresql://localhost:12345/testdb", parser.getValidURL());
        
        
        parser.setValue("autocommit", "false");
        assertEquals("jdbc:postgresql://localhost:12345/testdb?autocommit=false", parser.getValidURL());
        
        parser.setValue("loglevel", "2");
        assertEquals("jdbc:postgresql://localhost:12345/testdb?autocommit=false&loglevel=2", parser.getValidURL());
        
        parser.setValue("autocommit", null);
        assertEquals("jdbc:postgresql://localhost:12345/testdb?loglevel=2", parser.getValidURL());
    }
    
    public void testURLValidator() {
        URLValueParser parser = new URLValueParser(template, descriptors);
        
        for (int i=0; i<correctUrls.length; i++) {
            assertEquals(true, parser.isValidUrl(correctUrls[i]));
        }
        
        for (int i=0; i<falseUrls.length; i++) {
            assertEquals(false, parser.isValidUrl(falseUrls[i]));
        }
    }
    
    public static void main(String[] args) {
        TestSuite suite = new TestSuite(URLValueParserTest.class);
        TestRunner.run(suite);
    }
}
