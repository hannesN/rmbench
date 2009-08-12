/*
 * created 30.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;

/**
 * extension of the {@link com.byterefinery.rmbench.external.model.IColumn} interface that 
 * adds mutator methods
 * 
 * @author cse
 */
public interface IColumn2 extends IColumn {
    /**
     * @param value the new default value
     */
    void setDefault(String value);

    /**
     * @param comment the new default comment
     */
    void setComment(String value);
}
