/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DBValueNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.export.diff.BytesNode;
import com.byterefinery.rmbench.export.diff.IDBComparisonNode;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * a node that represents a leaf value on the database side of the comparison
 * 
 * @author cse
 */
abstract class DBValueNode extends BytesNode implements IDBComparisonNode {

    protected DBValueNode(String name) {
        super(name);
    }

    protected DBValueNode(String name, Image image) {
        super(name, image);
    }

    public String getValue() {
        return toString();
    }

	public RMBenchOperation getAddToModelOperation(Model model) {
		return newAddToModelOperation(model);
	}
}
