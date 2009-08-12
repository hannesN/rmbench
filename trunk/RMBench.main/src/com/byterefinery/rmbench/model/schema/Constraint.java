/*
 * created 24.08.2005 by sell
 *
 * $Id: Constraint.java 273 2006-02-28 15:41:35Z cse $
 */
package com.byterefinery.rmbench.model.schema;

/**
 * @author sell
 */
public interface Constraint {

    /**
     * @return the constraint type name, as defined by the SQL standard
     */
    String getConstraintType();
    
    /**
     * @return the remaining part of the constraint definition that comes after 
     * the constraint type
     */
    String getConstraintBody();

    /**
     * @return the table for which this constraint is defined
     */
    Table getTable();

    /**
     * @return the constraint name
     */
    String getName();
}
