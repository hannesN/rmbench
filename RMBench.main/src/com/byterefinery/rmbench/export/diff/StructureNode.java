/*
 * created 19.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: StructureNode.java 451 2006-08-15 11:13:06Z cse $
 */
package com.byterefinery.rmbench.export.diff;

import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.swt.graphics.Image;

/**
 * @author cse
 */
public abstract class StructureNode extends BaseNode implements IStructureComparator {

    private BaseNode[] children;
    
    protected StructureNode(String name, Image image) {
        super(name, image);
    }

    public void setIgnoreCase(boolean ignoreCase) {
		super.setIgnoreCase(ignoreCase);
		if(children != null) {
			for (int i = 0; i < children.length; i++) {
				children[i].setIgnoreCase(ignoreCase);
			}
		}
	}

	protected void setChildNodes(BaseNode[] children) {
        this.children = children;
    }
    
	protected BaseNode[] getChildNodes() {
		return children;
	}
	
    public Object[] getChildren() {
        return children;
    }

	public String getValue() {
		return null;
	}
}
