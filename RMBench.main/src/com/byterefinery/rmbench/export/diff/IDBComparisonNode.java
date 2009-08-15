/*
 * created 19.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IDBComparisonNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.RMBenchOperation;


/**
 * a comparison node which represents the live database side during model comparison 
 * 
 * @author cse
 */
public interface IDBComparisonNode extends IComparisonNode {

    /**
     * generate a DDL statement to drop the element represented by this node
     * 
     * @param generator the DDL generator
     * @param writer the writer to which the statement is to be written
     */
    void generateDropDDL(IDDLGenerator generator, IDDLScript script);

    /**
     * @return the underlying database model element
     */
    Object getElement();

	/**
	 * @param model the model to be modified
	 * @return an operation for performing the modification w/o considering child elements
	 */
	RMBenchOperation newAddToModelOperation(Model model);

    /**
     * @param model
     * @return an operation that adds the element represented by this node and all subnodes to the given model
     */
	RMBenchOperation getAddToModelOperation(Model model);
}
