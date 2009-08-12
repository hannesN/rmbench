/*
 * created 19.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: IModelComparisonNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * a comparison node which represents the RMBench mode side during model comparison 
 * 
 * @author cse
 */
public interface IModelComparisonNode extends IComparisonNode {

    /**
     * the name of the comment element, as used e.g. in table and column comments
     */
    String COMMENT = "comment";
    
    /**
     * generate DDL to create the element represented by this node, and all nested elements
     * 
     * @param generator the DDL generator
     * @param script the script to write to
     */
    void generateCreateDDL(IDDLGenerator generator, IDDLScript script);

    /**
     * generate DDL to alter the element represented by this node. This method is only called 
     * for nodes that return non-null from {@link IComparisonNode#getValue()}
     * 
     * @param generator the DDL generator
     * @param otherElement the other (database) side of the comaprison
     * @param script the script to write to
     */
    void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script);

    /**
     * @param element an element from the imported database model, or <code>null</code> if
     * the model element does not have a counterpart in the database
     * @return an operation which applies the change represented by the given DB model 
     * element to the design model
     */
	RMBenchOperation getModifyOperation(Object element);
}
