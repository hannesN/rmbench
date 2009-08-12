/*
 * created 7.11.2006
 * 
 * Copyright 2006, ByteRefinery
 * 
 * $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.model.type.IntegralDataType;

/**
 * This is the superclass for datatypes whose definition consists of a list of values,
 * in particular ENUM and SET
 * 
 * @author Hannes Niederhausen
 * 
 */
public class MySQLListDatatype extends IntegralDataType {

    private static final String SEPERATOR = ",";

    protected String[] elements;

    public MySQLListDatatype(String name) {
        super(name);
    }

    public String getExtra() {
        return serialize(getElements());
    }

    public boolean hasExtra() {
        return true;
    }

    public void setExtra(String extra) {
        setElements(deserialize(extra));
    }

    public String getDDLName() {
        StringBuffer buffer = new StringBuffer(10);
        buffer.append(getPrimaryName());
        buffer.append("(");
        for (int i = 0; i < elements.length; i++) {
            buffer.append("'");
            buffer.append(elements[i]);
            buffer.append("'");
            if (i < elements.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * @return the elements held by the datatype
     */
    String[] getElements() {
        return elements;
    }

    /**
     * @param elements the elements
     */
    void setElements(String[] elements) {
        this.elements = elements;
    }

    static String serialize(String[] types) {
        StringBuffer buffer = new StringBuffer(10);
        for (int i = 0; i < types.length; i++) {
            buffer.append(types[i]);
            if (i < types.length - 1) {
                buffer.append(SEPERATOR);
            }
        }
        return buffer.toString();
    }

    static String[] deserialize(String typesString) {
        return typesString.split(SEPERATOR);
    }

    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass())
            return (((IntegralDataType) obj).getPrimaryName().equals(getPrimaryName()) && 
                    ((IntegralDataType) obj).getExtra().equals(getExtra()));
        return false;
    }
}