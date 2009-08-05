/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelStructureNode.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.export.diff.StructureNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;

/**
 * @author cse
 */
abstract class ModelStructureNode extends StructureNode implements IModelComparisonNode {

    ModelStructureNode(String name, Image image) {
        super(name, image);
    }

    /**
     * This default implementation does nothing because structure nodes dont get altered in themselves.
     * The only comparable attribute for structure nodes is the name, which is used as identifier. 
     * Thus, a change to the name will be regarded as a delete/create. Changes proper only occur on the 
     * level of child nodes and are implemented there accordingly
     * <p/>
     * Subclasses will not normally need to reimplement this method  
     */
    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
    }
}
