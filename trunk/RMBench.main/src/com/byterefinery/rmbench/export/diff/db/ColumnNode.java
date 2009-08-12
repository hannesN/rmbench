/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import org.eclipse.compare.structuremergeviewer.IStructureComparator;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddColumnOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * column node is both a structured node with one optional child, the comment, and a bytes node
 * 
 * @author cse
 */
class ColumnNode extends DBValueNode implements IStructureComparator {

    private final DBColumn column;
    private final Object[] children;
    
    ColumnNode(DBColumn column) {
        super(column.name, RMBenchPlugin.getImage(ImageConstants.COLUMN));
        this.column = column;
        this.children = 
            column.comment != null ? 
                new Object[]{new ColumnCommentNode(column)} : new Object[0];
    }
    
    public Object[] getChildren() {
        return children;
    }

    public Object getElement() {
        return column;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropColumn(column.getIColumn(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		
		String defaultValue = column.getDatabaseInfo().impliesDefault(column.dataType) ? null : column.defaultValue;
		return DiffUtils.generateColumnValue(
                column.name, 
                column.dataType, 
                column.nullable,
                defaultValue,
                ignoreCase);
	}
	
	public String getNodeType() {
		return IComparisonNode.COLUMN;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table table = model.findTable(column.table.getSchemaName(), column.table.getName());
		int index = column.table.getColumns().indexOf(column);
		return new AddColumnOperation(
				table, column.name, column.dataType, column.nullable, column.defaultValue, column.comment, index);
	}
}
