/*
 * created 13.05.2005
 * 
 * $Id$
 */
package com.byterefinery.rmbench.util;

import com.byterefinery.rmbench.util.VariableReplacer;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author cse
 */
public class VariableReplacerTest extends TestCase {

    final VariableReplacer replacer1 = 
        new VariableReplacer("jdbc:postgresql://${3}:${2}/${1}");
    
    final VariableReplacer replacer2 = 
        new VariableReplacer("jdbc:postgresql://${hostname}/test");
    
    final VariableReplacer replacer3 = 
        new VariableReplacer("jdbc:postgresql://hostname/test");
    
    final VariableReplacer replacer4 =
        new VariableReplacer("jdbc:oracle:thin:@${hostname}:${port}:${dbname}");
    
    
    public void testCount() {
        
        assertEquals("replacer1.getVariableCount()==3", 3, replacer1.getVariableCount());
        assertEquals("replacer2.getVariableCount()==1", 1, replacer2.getVariableCount());
        assertEquals("replacer3.getVariableCount()==0", 0, replacer3.getVariableCount());
        assertEquals("replacer4.getVariableCount()==3", 3, replacer4.getVariableCount());
    }
    
    public void testNumericIndex() {
        replacer1.setVariable(3, "hostname");
        replacer1.setVariable(2, "port");
        replacer1.setVariable(1, "dbname");
        
        IllegalArgumentException ix = null;
        try {
            replacer1.setVariable(4, "nix");
        } catch(IllegalArgumentException x) {
            ix = x;
        }
        assertNotNull("IllegalArgumentException != null", ix);
        assertEquals("jdbc:postgresql://hostname:port/dbname", replacer1.getReplaced());
    }
    
    public void testNames() {
        replacer4.setVariable("hostname", "hostname");
        replacer4.setVariable("port", "port");
        replacer4.setVariable("dbname", "dbname");
        
        IllegalArgumentException ix = null;
        try {
            replacer4.setVariable("nixda", "val3");
        } catch(IllegalArgumentException x) {
            ix = x;
        }
        assertNotNull("IllegalArgumentException != null", ix);
        assertEquals("jdbc:oracle:thin:@hostname:port:dbname", replacer4.getReplaced());
    }
    
    public void testInitialize() {
        
        VariableReplacer replacer1 = new VariableReplacer("jdbc:postgresql://${3}:${2}/${1}");
        VariableReplacer replacer2 = new VariableReplacer("${eins}${zwei}${drei}");
        VariableReplacer replacer3 = new VariableReplacer("${eins}${zwei}xx${drei}");
        
        replacer1.initializeValues("jdbc:postgresql://drei:zwei/eins");
        assertEquals("drei", replacer1.getValue(3));
        assertEquals("zwei", replacer1.getValue(2));
        assertEquals("eins", replacer1.getValue(1));

        replacer2.initializeValues("dreizweieins");
        assertEquals("dreizweieins", replacer2.getValue("drei"));
        assertEquals(null, replacer2.getValue("zwei"));
        assertEquals(null, replacer2.getValue("eins"));

        replacer3.initializeValues("dreixxzweieins");
        assertEquals("zweieins", replacer3.getValue("drei"));
        assertEquals("drei", replacer3.getValue("zwei"));
        assertEquals(null, replacer3.getValue("eins"));
    }
    
    public static void main(String[] args) {

        TestSuite suite = new TestSuite(VariableReplacerTest.class);
        TestRunner.run(suite);
    }
}
