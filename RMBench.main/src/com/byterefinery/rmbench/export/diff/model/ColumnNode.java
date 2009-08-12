/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnNode.java 682 2008-03-03 22:39:50Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.structuremergeviewer.IStructureComparator;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffMessages;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.operations.ColumnDataTypeOperation;
import com.byterefinery.rmbench.operations.ColumnNullableOperation;
import com.byterefinery.rmbench.operations.ColumnPrecisionOperation;
import com.byterefinery.rmbench.operations.ColumnScaleOperation;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.DeleteColumnOperation;
import com.byterefinery.rmbench.operations.ModifyColumnOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * node that represents a column, with one optional comment child
 * 
 * @author cse
 */
class ColumnNode extends ModelValueNode implements IStructureComparator {

    private final Column column;
    private final Object[] children;
    
    ColumnNode(Column column) {
        super(column.getName(), RMBenchPlugin.getImage(ImageConstants.COLUMN));
        
        this.column = column;
        this.children = 
            column.getComment() != null ? 
                new Object[]{new ColumnCommentNode(column)} : new Object[0];
    }

    public Object[] getChildren() {
        return children;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.addColumn(column.getIColumn(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
        DBColumn otherColumn = (DBColumn)otherElement;
        script.beginStatementContext();
        
        if(!otherColumn.dataType.equals(column.getDataType()))
        	generator.alterColumnType(otherColumn.getIColumn(), column.getIColumn(), script);
        else if(otherColumn.nullable != column.getNullable())
        	generator.alterColumnNullable(otherColumn.getIColumn(), column.getIColumn(), script);
        else if(!otherColumn.getDatabaseInfo().impliesDefault(column.getDataType())) {
            String oldDefault = otherColumn.defaultValue != null ? otherColumn.defaultValue : "";
            String newDefault = column.getDefault() != null ? column.getDefault() : "";
            if(!oldDefault.equals(newDefault))
                generator.alterColumnDefault(otherColumn.getIColumn(), column.getIColumn(), script);
        }
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateColumnValue(
                column.getName(), 
                column.getDataType(), 
                column.getNullable(), 
                column.getDefault(),
                ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.COLUMN;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		RMBenchOperation operation;
		if(element == null) {
			operation = new DeleteColumnOperation(column.getTable(), column);
		}
		else {
			DBColumn dbColumn = (DBColumn)element;
			List<ModifyColumnOperation> mops = new ArrayList<ModifyColumnOperation>();
			
			if(!dbColumn.dataType.equals(column.getDataType())) {
				mops.add(new ColumnDataTypeOperation(column, dbColumn.dataType));
			}
			if(dbColumn.nullable != column.getNullable()) {
				mops.add(new ColumnNullableOperation(column));
			}
			if(dbColumn.dataType.acceptsSize() && dbColumn.precision != column.getSize()) {
				mops.add(new ColumnPrecisionOperation(column, dbColumn.precision));
			}
			if(dbColumn.dataType.acceptsScale() && dbColumn.scale != column.getScale()) {
				mops.add(new ColumnScaleOperation(column, dbColumn.scale));
			}
			
			if(mops.size() > 1) {
				CompoundOperation cop = new CompoundOperation(DiffMessages.Modify_column);
				operation = cop;
				
				for (ModifyColumnOperation mop : mops) {
					cop.add(mop);
				}
			}
			else {
				operation = mops.get(0);
			}
		}
		return operation;
	}
}
