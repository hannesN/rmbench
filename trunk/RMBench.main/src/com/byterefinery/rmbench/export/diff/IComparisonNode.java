/*
 * created 25.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.export.diff;

import org.eclipse.compare.ITypedElement;

import com.byterefinery.rmbench.external.IDDLScript;

/**
 * a node used during model comparison
 * 
 * @author cse
 */
public interface IComparisonNode extends ITypedElement {

    String COLUMN_COMMENT = "column_comment";
	String COLUMN = "column";
	String MODEL = "model";
	String FOREIGN_KEY = "foreign_key";
	String INDEX = "index";
	String PRIMARY_KEY = "primary_key";
	String SCHEMA = "schema";
	String TABLE_COMMENT = "table_comment";
	String TABLE = "table";
	String CONSTRAINT = "constraint";

    /**
     * @return the type identifier for the schema element represented by this node 
     */
    String getNodeType();
    
	/**
     * @return the value for byte-wise comparison , or null if this node does not hold a 
     * byte comparison value (i.e., it is a structure node)
     */
    String getValue();
    
    /**
     * @return the ranges in the generated script that correspond to the statements generated
     * for this node, or <code>null</code> if that data is not available
     */
    IDDLScript.Range[] getStatementRanges();
}
