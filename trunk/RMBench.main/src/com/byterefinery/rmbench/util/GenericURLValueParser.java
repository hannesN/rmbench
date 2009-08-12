/*
 * created 06.09.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
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

import com.byterefinery.rmbench.extension.VariableDescriptor;

/**
 * A class which gets a url template (from the extension point) and creates a variable parser
 * out of it.
 * 
 * @author Hannes Niederhausen
 *
 */
public class GenericURLValueParser implements IURLValueParser {

    private static Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{([^\\}]*)\\}");
    
    
    private final Pattern urlPattern;
    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    private final VariableDescriptor varDesciptors[];
    private final List<String> variables = new ArrayList<String>(5);
    private final List<String> parts = new ArrayList<String>(5);
    private final Map<String, String>  varMap = new HashMap<String, String>();
   
    private String options = "";
    
    public GenericURLValueParser(String template, VariableDescriptor vars[]) {
        varDesciptors = vars;
        urlPattern = Pattern.compile(getPatterString(template));
    }

    private String getPatterString(String template) {
        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
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
        StringBuffer pattern = new StringBuffer();
        int i;
        for (i=0; i<parts.size(); i++) {
            pattern.append(parts.get(i));
            if (i < varDesciptors.length) {
                if (varDesciptors[i].getType()==VariableDescriptor.TYPE_INT) {
                    pattern.append("(\\p{Digit}*)");
                } else {
                    pattern.append("([\\p{Alnum}|\\p{Punct}]*)");
                }
            }
        }
        if (i<varDesciptors.length) {
            if (varDesciptors[i].getType()==VariableDescriptor.TYPE_INT) {
                pattern.append("(\\p{Digit}*)");
            } else {
                pattern.append("([\\p{Alnum}|\\p{Punct}]*)");
            }
        }
        return pattern.toString();
    }

    public String getValidURL() {
        StringBuffer buffer = new StringBuffer();
        int i;
        for (i=0; i<parts.size(); i++) {
            buffer.append(parts.get(i));
            if (i < varDesciptors.length) {
                String val = varMap.get(varDesciptors[i].getName());
                if (val==null) 
                    if (varDesciptors[i].getDefaultValue()!=null) {
                        val = varDesciptors[i].getDefaultValue();
                    } else {
                        val="???";
                    }
                
                buffer.append(val);
            }
        }
        while (i<varDesciptors.length) {
            buffer.append(varMap.get(varDesciptors[i].getName()));
        }

        return buffer.toString();
    }

    public String getValue(String varName) {
        return varMap.get(varName);
    }

    public void setValue(String name, String value) {
        if(!varMap.containsKey(name)) {
            throw new IllegalArgumentException("undefined variable: "+name);
        }
        Object oldValue = varMap.get(name);
        //we need no changes if there eare no one
        if ((oldValue!=null) && (oldValue.equals(value)))
            return;
        
        varMap.put(name, value);
        propertySupport.firePropertyChange(name, oldValue, value);
    }

    public boolean isValidUrl(String url) {
        Matcher matcher = urlPattern.matcher(url);
        if (matcher.find())
            return matcher.end()==url.length();
        return false;
    }

    public boolean parseUrl(String url) {
        Matcher matcher = urlPattern.matcher(url);
        if (matcher.find()) {
            for (int i=0; i<varDesciptors.length; i++) {
                setValue(varDesciptors[i].getName(), matcher.group(i+1));
                //System.out.println(varDesciptors[i].getName() +":" + matcher.group(i+1));
            }
            
            return true;
        }
        
        return false;
    }

    public void reset() {
        options="";
        for (int i=0; i< varDesciptors.length; i++) {
            varMap.put(varDesciptors[i].getName(), varDesciptors[i].getDefaultValue());
        }
    }

    public String getURLOptions() {
        return options;
    }

    public void setUrlOptions(String options) {
        this.options = options;
    }

    /**
     * @return the number of variable references in the string
     */
    public int getVariableCount() {
        return variables.size();
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

}
