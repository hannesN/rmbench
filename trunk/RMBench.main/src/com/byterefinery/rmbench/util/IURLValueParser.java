/*
 * created 08.08.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  $Id$
 */
package com.byterefinery.rmbench.util;

import java.beans.PropertyChangeListener;

/**
 * This interface defines the methods of a parser used to validate and configurate
 * a database connection. 
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IURLValueParser {

    /**
     * 
     * @return the url with replaced variables (values instead of var names), if they exist.
     */
    public String getValidURL();
    
    /**
     * 
     * @param varName the name of the variable, which value is needed
     * @return the value of the given var, or null if the variable is not set
     */
    public String getValue(String varName);
    
    /**
     * Sets the value for the variable with the name <i>varName</i>.
     * <code>null</code> will invalidate the variable.
     * @param varName the name of the variable
     * @param value the value of the variable or <i>null</i>
     */
    public void setValue(String varName, String value);
    
    /**
     * Checks if the given url is a valid url.
     * @param url the url to check 
     * @return <code>true</code> if the parser can parse the url, <code>false</code> else
     */
    public boolean isValidUrl(String url);
    
    /**
     * Parses the url and sets internally all variables. 
     * @param url the url to parse
     * @return <code>true</code> if parsing suceed, <code>false</code> esle
     */
    public boolean parseUrl(String url);
    
    /**
     * Resets the values to the default value, if it exist else sets them to <code>null</code>.
     *
     */
    public void reset();
    
    /**
     * 
     * @return the string which contains all additional jdbc options
     */
    public String getURLOptions();
    
    
    /**
     * UrlOptions are addional parameters concatenated with the jdbc url.
     * @param options the additional options in form of the string added to the jdbc url 
     */
    public void setUrlOptions(String options);
    
    public void addPropertyListener(String property, PropertyChangeListener listener);
    
    public void addPropertyListener(PropertyChangeListener listener);
        
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);    

    public void removePropertyChangeListener(PropertyChangeListener listener);
}
