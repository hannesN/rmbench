/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ConstraintNode.java 669 2007-10-27 08:23:31Z cse $
 */
package com.byterefinery.rmbench.export.diff.model;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.operations.DeleteTableConstraintOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class ConstraintNode extends ModelValueNode {

	private final Constraint constraint;
	
    protected ConstraintNode(Constraint constraint) {
        super(constraint.getName(), RMBenchPlugin.getImage(ImageConstants.CONSTRAINT));
        this.constraint = constraint;
    }

    public void generateCreateDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
    	if(constraint.getConstraintType() == CheckConstraint.CONSTRAINT_TYPE)
    		generator.createCheckConstraint(((CheckConstraint)constraint).getIConstraint(), script);
    	else if(constraint.getConstraintType() == UniqueConstraint.CONSTRAINT_TYPE)
    		generator.createUniqueConstraint(((UniqueConstraint)constraint).getIConstraint(), script);
        setStatementContext(script.endStatementContext());
    }

    public void generateAlterDDL(IDDLGenerator generator, Object otherElement, IDDLScript script) {
    	//as JDBC import doesnt deliver constraints, there is nothing we can do for now
        //DBConstraint otherConstraint = (DBConstraint)otherElement;
        //generator.alterConstraint(column, otherConstraint, writer);
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateConstraintValue(constraint);
	}

	public String getNodeType() {
		return IComparisonNode.CONSTRAINT;
	}

	public RMBenchOperation getModifyOperation(Object element) {
		if(element == null) {
			return new DeleteTableConstraintOperation(constraint.getTable(), constraint);
		}
		else {
			//no attribute modifications as import does not deliver constraints
			return null;
		}
	}
}
