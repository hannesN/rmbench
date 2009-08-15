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
 * $Id: TableCommentNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.operations.TableCommentOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * comparison node that represents a table comment
 * 
 * @author cse
 */
class TableCommentNode extends DBValueNode {

    private final DBTable table;
    
    protected TableCommentNode(DBTable table) {
        super(IModelComparisonNode.COMMENT, RMBenchPlugin.getImage(ImageConstants.FOREIGN_KEY));
        this.table = table;
    }

    public Object getElement() {
        return table.getComment();
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropTableComment(table.getITable(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return table.getComment();
	}

	public String getNodeType() {
		return IComparisonNode.TABLE_COMMENT;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table mtable = model.findTable(table.getSchemaName(), table.getName());
		TableCommentOperation operation = new TableCommentOperation(mtable);
		operation.setNewComment(table.getComment());
		return operation;
	}
}
