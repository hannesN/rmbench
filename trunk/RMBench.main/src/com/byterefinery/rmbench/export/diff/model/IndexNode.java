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
 * $Id: IndexNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBIndex;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.operations.DeleteIndexOperation;
import com.byterefinery.rmbench.operations.ModifyIndexOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class IndexNode extends ModelValueNode {

    private final Index index;
    
    protected IndexNode(Index index) {
        super(index.getName(), RMBenchPlugin.getImage(ImageConstants.INDEX));
        this.index = index;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createIndex(index.getIIndex(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        DBIndex otherIndex = (DBIndex)otherElement;
        script.beginStatementContext();
        generator.alterIndex(otherIndex.getIIndex(), index.getIIndex(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateIndexValue(
                index.getName(),
                index.isUnique(),
                index.getColumnNames(), 
                index.getAscending(),
                ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.INDEX;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		if(element == null) {
			return new DeleteIndexOperation(index);
		}
		else {
			DBIndex dbIndex = (DBIndex)element;
			List<Column> columns = new ArrayList<Column>();
			for (String columnName : dbIndex.getColumnNames()) {
				columns.add(index.getTable().getColumn(columnName));
			}
			return new ModifyIndexOperation(
					index, 
					index.getName(), 
					columns.toArray(new Column[columns.size()]), 
					dbIndex.unique, 
					dbIndex.getAscending());
		}
	}
}
