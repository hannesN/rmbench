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
 * $Id: ForeignKeyNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.ArrayList;
import java.util.List;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffMessages;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.DeleteForeignKeyOperation;
import com.byterefinery.rmbench.operations.ModifyForeignKeyOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
public class ForeignKeyNode extends ModelValueNode {

    private final ForeignKey foreignKey;
    
    protected ForeignKeyNode(ForeignKey foreignKey) {
        super(foreignKey.getName(), RMBenchPlugin.getImage(ImageConstants.FOREIGN_KEY));
        this.foreignKey = foreignKey;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createForeignKey(foreignKey.getIForeignKey(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        DBForeignKey otherKey = (DBForeignKey)otherElement;
        script.beginStatementContext();
        generator.alterForeignKey(otherKey.getIForeignKey(), foreignKey.getIForeignKey(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateForeignKeyValue(
                foreignKey.getName(),
                foreignKey.getColumnNames(),
                foreignKey.getTargetTable().getSchema().getName(),
                foreignKey.getTargetTable().getName(),
                foreignKey.getDeleteAction(),
                foreignKey.getUpdateAction(),
                ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.FOREIGN_KEY;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		if(element == null) {
			return new DeleteForeignKeyOperation(foreignKey);
		}
		else {
			DBForeignKey dbKey = (DBForeignKey)element;
			List<ModifyForeignKeyOperation> ops = new ArrayList<ModifyForeignKeyOperation>(2);
			if(dbKey.deleteRule != foreignKey.getDeleteAction()) {
				ops.add(
						new ModifyForeignKeyOperation(
								foreignKey, 
								ModifyForeignKeyOperation.Modification.DELETE_RULE, 
								dbKey.deleteRule, 
								foreignKey.getDeleteAction()));
			}
			if(dbKey.updateRule != foreignKey.getUpdateAction()) {
				ops.add(
						new ModifyForeignKeyOperation(
								foreignKey, 
								ModifyForeignKeyOperation.Modification.UPDATE_RULE, 
								dbKey.updateRule, 
								foreignKey.getUpdateAction()));
			}
			String[] columns = foreignKey.getColumnNames();
			for(int i=0; i<columns.length && i<dbKey.size(); i++) {
				if(!columns[i].equals(dbKey.getColumn(i))) {
					ops.add(
							new ModifyForeignKeyOperation(
									foreignKey, 
									ModifyForeignKeyOperation.Modification.REPLACE_COLUMN, 
									dbKey.getColumn(i),
									columns[i]));
				}
			}
			for(int i=columns.length; i<dbKey.size(); i++) {
				Column col = foreignKey.getTable().getColumn(dbKey.getColumn(i));
				if(col != null) {
					ops.add(
							new ModifyForeignKeyOperation(
									foreignKey, 
									ModifyForeignKeyOperation.Modification.ADD_COLUMN, 
									col,
									null));
				}
			}
			for(int i=dbKey.size(); i<columns.length; i++) {
				Column col = foreignKey.getTable().getColumn(columns[i]);
				ops.add(
						new ModifyForeignKeyOperation(
								foreignKey, 
								ModifyForeignKeyOperation.Modification.DELETE_COLUMN, 
								col, 
								null));
			}
			return ops.size() == 1 ? 
					ops.get(0) : new CompoundOperation(DiffMessages.Modify_ForeignKey, ops);
		}
	}
}
