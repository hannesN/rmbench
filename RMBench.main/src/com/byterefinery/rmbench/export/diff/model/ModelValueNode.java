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
 * $Id: ModelValueNode.java 154 2006-02-01 19:45:24Z csell $
 */
package com.byterefinery.rmbench.export.diff.model;

import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.export.diff.BytesNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;

/**
 * a node that represents a leaf value on the model side of the comparison
 * 
 * @author cse
 */
abstract class ModelValueNode extends BytesNode implements IModelComparisonNode {

    protected ModelValueNode(String name) {
        super(name);
    }

    protected ModelValueNode(String name, Image image) {
        super(name, image);
    }

    public String getValue() {
        return toString();
    }
}
