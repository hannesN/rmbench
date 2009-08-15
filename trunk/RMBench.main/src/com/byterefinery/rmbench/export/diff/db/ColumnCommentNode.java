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
 * $Id: ColumnCommentNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.ColumnCommentOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * @author cse
 */
class ColumnCommentNode extends DBValueNode {

    private final DBColumn column;
    
    protected ColumnCommentNode(DBColumn column) {
        super(IModelComparisonNode.COMMENT);
        this.column = column;
    }

    public Object getElement() {
        return column.comment;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropColumnComment(column.getIColumn(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return column.comment;
	}

	public String getNodeType() {
		return IComparisonNode.COLUMN_COMMENT;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table table = model.findTable(column.table.getSchemaName(), column.table.getName());
		Column modelColumn = table.getColumn(column.name);
		return new ColumnCommentOperation(modelColumn, column.comment);
	}
}
