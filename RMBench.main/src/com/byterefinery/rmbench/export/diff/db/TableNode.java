/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.BaseNode;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.dbimport.DBIndex;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.operations.NewTableOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class TableNode extends DBStructureNode {

    private final DBTable table;
    
    TableNode(DBTable table) {
        super(table.getName(), RMBenchPlugin.getImage(ImageConstants.TABLE2));
    
        this.table = table;
        List<BaseNode> nodes = new ArrayList<BaseNode>();
        for (Iterator<DBColumn> it=table.getColumns().iterator(); it.hasNext(); ) {
            nodes.add(new ColumnNode(it.next()));
        }
        for (Iterator<DBForeignKey> it=table.getForeignKeys().iterator(); it.hasNext(); ) {
            nodes.add(new ForeignKeyNode(it.next()));
        }
        for (Iterator<DBIndex> it=table.getIndexes().iterator(); it.hasNext(); ) {
            nodes.add(new IndexNode(it.next()));
        }
        if(table.getPrimaryKey() != null)
            nodes.add(new PrimaryKeyNode(table.getPrimaryKey()));
        if(table.getComment() != null)
            nodes.add(new TableCommentNode(table));
        
        setChildNodes((BaseNode[])nodes.toArray(new BaseNode[nodes.size()]));
    }
    
    public Object getElement() {
        return table;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropTable(table.getITable(), script);
        setStatementContext(script.endStatementContext());
    }

	public String getNodeType() {
		return IComparisonNode.TABLE;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		return new NewTableOperation(model, model.getSchema(table.getSchemaName()), table.getName());
	}
}
