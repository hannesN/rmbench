/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: SchemaNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.Iterator;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.DeleteSchemaOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class SchemaNode extends ModelStructureNode {

	private final Model model;
    private final Schema schema;
    
    SchemaNode(Model model, Schema schema) {
        super(schema.getName(), RMBenchPlugin.getImage(ImageConstants.SCHEMA2));
        
        this.model = model;
        this.schema = schema;
        TableNode[] tableNodes = new TableNode[schema.getTables().size()];
        int count = 0;
        for (Iterator<Table> it = schema.getTables().iterator(); it.hasNext();) {
            tableNodes[count++] = new TableNode(it.next(), model);
        }
        setChildNodes(tableNodes);
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createSchema(schema.getISchema(), script);
        TableNode[] nodes = (TableNode[])getChildNodes();
        for (int i = 0; i < nodes.length; i++) {
			generator.createTable(nodes[i].getTable().getITable(), script);
		}
        setStatementContext(script.endStatementContext());
    }

	public String getNodeType() {
		return IComparisonNode.SCHEMA;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		if(element == null) {
			return new DeleteSchemaOperation(model, schema);
		}
		else {
			//schema down not understand attribute modification
			return null;
		}
	}
}
