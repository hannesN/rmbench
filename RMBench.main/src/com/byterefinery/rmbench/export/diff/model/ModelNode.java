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
 * $Id: ModelNode.java 667 2007-10-02 18:54:16Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.StructureNode;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * Diff node for the RMBench model
 * 
 * @author cse
 */
public class ModelNode extends StructureNode {

    /**
     * @param model the model represented by this node
     * @param publicSchemas the names of schemas that should always be available. If the
     * thus named schemas are not already defined by the model, dummy schema nodes will be created
     */
    public ModelNode(Model model, String[] publicSchemas) {
        
        super(model.getName(), RMBenchPlugin.getImage(ImageConstants.MODEL));
        List<SchemaNode> schemaNodes = new ArrayList<SchemaNode>(model.getSchemas().size());
        Set<String> schemaNames = new HashSet<String>(model.getSchemas().size());
        
        for (Iterator<Schema> it = model.getSchemas().iterator(); it.hasNext();) {
            Schema schema = it.next();
            schemaNodes.add(new SchemaNode(model, schema));
            schemaNames.add(schema.getName());
        }
        for (int i = 0; i < publicSchemas.length; i++) {
            if(!schemaNames.contains(publicSchemas[i])) {
                Schema dummySchema = new Schema(publicSchemas[i], model.getDatabaseInfo());
                schemaNodes.add(new SchemaNode(model, dummySchema));
            }
        }
        setChildNodes((SchemaNode[])schemaNodes.toArray(new SchemaNode[schemaNodes.size()]));
    }

	public String getNodeType() {
		return IComparisonNode.MODEL;
	}
}
