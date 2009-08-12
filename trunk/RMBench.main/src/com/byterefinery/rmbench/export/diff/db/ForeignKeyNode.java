/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ForeignKeyNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddForeignKeyOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class ForeignKeyNode extends DBValueNode {

    private final DBForeignKey foreignKey;
    
    protected ForeignKeyNode(DBForeignKey foreignKey) {
        super(foreignKey.name, RMBenchPlugin.getImage(ImageConstants.FOREIGN_KEY));
        this.foreignKey = foreignKey;
    }

    public Object getElement() {
        return foreignKey;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropForeignKey(foreignKey.getIForeignKey(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateForeignKeyValue(
                foreignKey.name,
                foreignKey.getColumns(),
                foreignKey.targetSchema,
                foreignKey.targetTable,
                foreignKey.deleteRule,
                foreignKey.updateRule,
                ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.FOREIGN_KEY;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table table = model.findTable(foreignKey.table.getSchemaName(), foreignKey.table.getName());
		Table targetTable = model.findTable(foreignKey.targetSchema, foreignKey.targetTable);
		
		AddForeignKeyOperation operation = new AddForeignKeyOperation(table);
		operation.setTargetTable(targetTable);
		String[] colNames = foreignKey.getColumns();
		Column[] columns = new Column[colNames.length];
		for (int i=0; i<columns.length; i++) {
			columns[i] = table.getColumn(colNames[i]);
		}
		operation.setColumns(columns);
		return operation;
	}
}
