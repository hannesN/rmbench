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
 * $Id: PrimaryKeyNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffMessages;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBPrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.ColumnPrimaryKeyOperation;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class PrimaryKeyNode extends DBValueNode {

    private final DBPrimaryKey primaryKey;
    
    protected PrimaryKeyNode(DBPrimaryKey primaryKey) {
        super(primaryKey.name, RMBenchPlugin.getImage(ImageConstants.KEY));
        this.primaryKey = primaryKey;
    }

    public Object getElement() {
        return primaryKey;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropPrimaryKey(primaryKey.getIPrimaryKey(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generatePrimaryKeyValue(primaryKey.getColumns(), ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.PRIMARY_KEY;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table table = model.findTable(primaryKey.table.getSchemaName(), primaryKey.table.getName());
		String[] colNames = primaryKey.getColumns();

		CompoundOperation operation = new CompoundOperation(DiffMessages.Add_primary_key);
		for (String colName : colNames) {
			ColumnPrimaryKeyOperation op = new ColumnPrimaryKeyOperation(table.getColumn(colName));
			operation.add(op);
		}
		return operation;
	}
}
