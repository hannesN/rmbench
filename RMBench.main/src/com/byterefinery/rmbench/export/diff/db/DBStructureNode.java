/*
 * created 28.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DBStructureNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.export.diff.BaseNode;
import com.byterefinery.rmbench.export.diff.IDBComparisonNode;
import com.byterefinery.rmbench.export.diff.StructureNode;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * abstract superclass for DB structureNodes that only enforces the corresponding interface
 * 
 * @author cse
 */
abstract class DBStructureNode extends StructureNode implements IDBComparisonNode {
    
    DBStructureNode(String name, Image image) {
        super(name, image);
    }

	public RMBenchOperation getAddToModelOperation(Model model) {
		RMBenchOperation baseOperation = newAddToModelOperation(model);
		
		CompoundOperation cop = new CompoundOperation(baseOperation.getLabel());
		cop.add(baseOperation);
		for (BaseNode node : getChildNodes()) {
			cop.add(((IDBComparisonNode)node).newAddToModelOperation(model));
		}
		return cop;
	}
}
