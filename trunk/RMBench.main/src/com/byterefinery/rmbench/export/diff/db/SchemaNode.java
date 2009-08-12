/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: SchemaNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import java.util.List;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBSchema;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.operations.AddSchemaOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class SchemaNode extends DBStructureNode {

    private final DBSchema schema;
    
    SchemaNode(DBSchema schema) {
        super(schema.getName(), RMBenchPlugin.getImage(ImageConstants.SCHEMA2));
    
        this.schema = schema;
        List<DBTable> schemaTables = schema.getTables();
        TableNode[] tableNodes = new TableNode[schemaTables.size()];
        for (int i = 0; i < tableNodes.length; i++) {
            tableNodes[i] = new TableNode(schemaTables.get(i));
        }
        setChildNodes(tableNodes);
    }

    public Object getElement() {
        return schema;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropSchema(schema.getISchema(), script);
        setStatementContext(script.endStatementContext());
    }

	public String getNodeType() {
		return IComparisonNode.SCHEMA;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		return new AddSchemaOperation(model, schema.getName());
	}
}
