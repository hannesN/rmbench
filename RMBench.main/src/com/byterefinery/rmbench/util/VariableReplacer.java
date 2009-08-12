/*
 * created 13.05.2005
 * 
 * $Id:VariableReplacer.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * utility class that will replace variable references in arbitrary strings.
 * Variable references are of the form ${[var]}, where [var] can be either a
 * name or a number. 
 * 
 * @author cse
 */
public class VariableReplacer {

    private static Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]*)\\}");
    
    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    private final Map<String, Object> varMap = new HashMap<String, Object>(5);
    private final List<String> variables = new ArrayList<String>(5);
    private final List<String> parts = new ArrayList<String>(5);
    private final String template;

    public VariableReplacer(String template) {
        
        this.template = template;
        Matcher matcher = PATTERN.matcher(template);
        int start = 0;
        while(matcher.find()) {
            String part = template.substring(start, matcher.start());
            parts.add(part);
            String var = matcher.group(1);
            variables.add(var);
            varMap.put(var, null);
            
            start = matcher.end();
        }
        if(start < template.length()) {
            parts.add(template.substring(start));
        }
    }
    
    public void addPropertyListener(String property, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(property, listener);
    }
    
    public void addPropertyListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
        
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * @return the number of variable references in the string
     */
    public int getVariableCount() {
        return variables.size();
    }
    
    /**
     * @return the variable names (which might be numbers)
     */
    public String[] getVariableNames() {
        return (String[])variables.toArray(new String[variables.size()]);
    }
    
    /**
     * set a variable which has a numeric reference
     * @param number the value of the numeric reference
     * @param value the value to substitute
     */
    public void setVariable(int number, String value) {
        setVariable(String.valueOf(number), value);
    }
    
    /**
     * set a variable which has a named (or numeric) reference
     * @param name the reference name or number
     * @param value the value to substitute
     * @throws IllegalArgumentException if the variable is not declared
     */
    public void setVariable(String name, String value) {
        if(!varMap.containsKey(name)) {
            throw new IllegalArgumentException("undefined variable: "+name);
        }
        Object oldValue = varMap.get(name);
        varMap.put(name, value);
        propertySupport.firePropertyChange(name, oldValue, value);
    }
    
    /**
     * @return the string with all variables replaced whose values are set
     */
    public String getReplaced() {
        
        StringBuffer replaced = new StringBuffer();
        for (int i=0; i<parts.size(); i++) {
            replaced.append((String) parts.get(i));
            if(i < variables.size()) {
                String val = (String)varMap.get((String)variables.get(i));
                if(val != null) {
                    replaced.append(val);
                } else {
                    replaced.append("${");
                    replaced.append((String)variables.get(i));
                    replaced.append("}");
                }
            }
        }
        return replaced.toString();
    }

    /**
     * @return the original template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @param name a variable name
     * @return the value currently used for the given variable
     */
    public String getValue(String name) {
        return (String)varMap.get(name);
    }

    /**
     * @param number a numeric variable reference
     * @return the value currently used for the given variable
     */
    public String getValue(int number) {
        return (String)varMap.get(String.valueOf(number));
    }

    /**
     * Initialize the values from an url that was previously created by this template.<br> 
     * Note that in general it is not recommened to pass an url that does not conform as 
     * the results are somewhat unpredictable.
     * 
     * @param url an url that conforms to the template underlying this replacer, or <code>null</code>
     */
    public void initializeValues(String url) {
        
        if(url ==  null) {
            // clear the variables 
            for (String var : varMap.keySet()) {
                varMap.put(var, null);
            }
            
            return;
        }
            
        int pos = 0, lastPos = 0, index = 0;
        
        for (String part : parts) {
            if(part.length() > 0)  {
                lastPos = pos;
                pos = url.indexOf(part, lastPos);
                if(pos < 0) {
                    throw new IllegalArgumentException(url);
                }
                if(index > 0) {
                    String name = (String)variables.get(index-1);
                    String value = url.substring(lastPos, pos);
                    setVariable(name, value);
                }
                pos += part.length();
            }
            index++;
        }
        if(pos < url.length() - 1) {
            String name = (String)variables.get(index-1);
            String value = url.substring(pos, url.length());
            setVariable(name, value);
        }
    }
}
