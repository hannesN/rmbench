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
 * $Id: ColumnCommentNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.export.diff.IModelComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.operations.ColumnCommentOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * @author cse
 */
class ColumnCommentNode extends ModelValueNode {

    private final Column column;
    
    protected ColumnCommentNode(Column column) {
        super(IModelComparisonNode.COMMENT);
        this.column = column;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createColumnComment(column.getIColumn(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        DBColumn otherColumn = (DBColumn)otherElement;
        script.beginStatementContext();
        generator.alterColumnComment(column.getIColumn(), otherColumn.comment, script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return column.getComment();
	}

	public String getNodeType() {
		return IComparisonNode.COLUMN_COMMENT;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		String comment =  element != null ? ((DBColumn)element).comment : null;
		return new ColumnCommentOperation(column, comment);
	}
}
