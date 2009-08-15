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
 * $Id: TableCommentNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.operations.TableCommentOperation;

/**
 * @author cse
 */
class TableCommentNode extends ModelValueNode {

    private final Table table;
    
    protected TableCommentNode(Table table) {
        super(IModelComparisonNode.COMMENT);
        this.table = table;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createTableComment(table.getITable(), getValue(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        String otherComment = (String)otherElement;
        script.beginStatementContext();
        generator.alterTableComment(table.getITable(), otherComment, script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return table.getComment();
	}

	public String getNodeType() {
		return IComparisonNode.TABLE_COMMENT;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		String comment =  element != null ? ((DBTable)element).getComment() : null;
		return new TableCommentOperation(table, comment);
	}
}
