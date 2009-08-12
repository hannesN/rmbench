/*
 * created 21.05.2005
 * 
 * $Id:XMLAttributes.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * a simplified variant of org.xml.sax.Attributes
 * 
 * @author cse
 */
public class XMLAttributes {

    private Map<String, String> attMap = new HashMap<String, String>();
    
    public void addAttribute(String name, String value) {
        attMap.put(name, value);
    }

    public void addAttribute(String name, boolean value) {
        attMap.put(name, String.valueOf(value));
    }

    public void reset() {
        attMap.clear();
    }
    
    public String getString(String name) {
        return (String)attMap.get(name);
    }
    
    public boolean getBoolean(String name) {
        String value = getString(name);
        return Boolean.valueOf(value).booleanValue();
    }
    
    public Iterator<Map.Entry<String, String>> iterator() {
        return attMap.entrySet().iterator();
    }
}

