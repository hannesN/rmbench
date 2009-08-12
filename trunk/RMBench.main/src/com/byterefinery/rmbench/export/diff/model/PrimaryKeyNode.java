/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: PrimaryKeyNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.HashSet;
import java.util.Set;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffMessages;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBPrimaryKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.operations.ColumnPrimaryKeyOperation;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class PrimaryKeyNode extends ModelValueNode {

    private final PrimaryKey primaryKey;
    
    protected PrimaryKeyNode(PrimaryKey primaryKey) {
        super(primaryKey.getName(), RMBenchPlugin.getImage(ImageConstants.KEY));
        this.primaryKey = primaryKey;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.createPrimaryKey(primaryKey.getIPrimaryKey(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        DBPrimaryKey otherKey = (DBPrimaryKey)otherElement;
        script.beginStatementContext();
        generator.alterPrimaryKey(otherKey.getIPrimaryKey(), primaryKey.getIPrimaryKey(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generatePrimaryKeyValue(primaryKey.getColumnNames(), ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.PRIMARY_KEY;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		CompoundOperation operation;
		if(element == null) {
			operation = new CompoundOperation(DiffMessages.Delete_primary_key);
			for (Column column : primaryKey.getColumns()) {
				operation.add(new ColumnPrimaryKeyOperation(column));
			}
		}
		else {
			operation = new CompoundOperation(DiffMessages.Modify_primary_key);
			DBPrimaryKey dbKey = (DBPrimaryKey)element;
			Set<String> names = new HashSet<String>(dbKey.getColumns().length);
			
			//add new columns
			for (String colName: dbKey.getColumns()) {
				names.add(colName);
				Column column = primaryKey.getTable().getColumn(colName);
				if(!primaryKey.contains(column))
					operation.add(new ColumnPrimaryKeyOperation(column));
			}
			//remove old ones
			for (Column column : primaryKey.getColumns()) {
				if(!names.contains(column.getName()))
					operation.add(new ColumnPrimaryKeyOperation(column));
			}
		}
		return operation;
	}
}
