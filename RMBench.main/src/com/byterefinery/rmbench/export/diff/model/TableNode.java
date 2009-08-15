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
 * $Id: TableNode.java 670 2007-10-30 05:24:49Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.BaseNode;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.operations.DeleteTableOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.operations.TableCommentOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a node that represents a table during model comparison
 * 
 * @author cse
 */
public class TableNode extends ModelStructureNode {

    private final Table table;
    private final Model model;
    
    TableNode(Table table, Model model) {
        super(table.getName(), RMBenchPlugin.getImage(ImageConstants.TABLE2));
        this.table = table;
        this.model = model;
        
        List<BaseNode> nodes = new ArrayList<BaseNode>();
        for (Iterator<Column> it=table.getColumns().iterator(); it.hasNext(); ) {
            nodes.add(new ColumnNode(it.next()));
        }
        for (Iterator<ForeignKey> it=table.getForeignKeys().iterator(); it.hasNext(); ) {
            nodes.add(new ForeignKeyNode(it.next()));
        }
        for (Iterator<Index> it=table.getIndexes().iterator(); it.hasNext(); ) {
            nodes.add(new IndexNode(it.next()));
        }
        for (Iterator<UniqueConstraint> it=table.getUniqueConstraints().iterator(); it.hasNext(); ) {
            nodes.add(new ConstraintNode(it.next()));
        }
        for (Iterator<CheckConstraint> it=table.getCheckConstraints().iterator(); it.hasNext(); ) {
            nodes.add(new ConstraintNode(it.next()));
        }
        if(table.getPrimaryKey() != null)
            nodes.add(new PrimaryKeyNode(table.getPrimaryKey()));
        
        if(table.getComment() != null)
            nodes.add(new TableCommentNode(table));
        
        setChildNodes((BaseNode[])nodes.toArray(new BaseNode[nodes.size()]));
    }

    protected Table getTable() {
    	return table;
    }
    
    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createTable(table.getITable(), script);
        
        for (Iterator<ForeignKey> it=table.getForeignKeys().iterator(); it.hasNext();) {
        	ForeignKey foreignKey = it.next();
            generator.createForeignKey(foreignKey.getIForeignKey(), script);
        }
        for (Iterator<Index> indexIter = table.getIndexes().iterator(); indexIter.hasNext();) {
            Index index = (Index) indexIter.next();
            generator.createIndex(index.getIIndex(), script);
        }
        for (Iterator<UniqueConstraint> it=table.getUniqueConstraints().iterator(); it.hasNext();) {
        	UniqueConstraint constraint = (UniqueConstraint)it.next();
            generator.createUniqueConstraint(constraint.getIConstraint(), script);
        }
        for (Iterator<CheckConstraint> it=table.getCheckConstraints().iterator(); it.hasNext();) {
        	CheckConstraint constraint = (CheckConstraint)it.next();
            generator.createCheckConstraint(constraint.getIConstraint(), script);
        }
        setStatementContext(script.endStatementContext());
    }

	public String getNodeType() {
		return IComparisonNode.TABLE;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		if(element == null) {
			return new DeleteTableOperation(table, model);
		}
		else {
			String tc = table.getComment();
			String dc = ((DBTable)element).getComment();
			if(tc != dc && !tc.equals(dc)) {
				return new TableCommentOperation(table, dc);
			}
		}
		return null;
	}
}
