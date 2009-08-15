/*
 * created 23.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: CheckConstraint.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.model.schema;

import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * model representation of a check constraint, which contains an arbitrary expression
 * 
 * @author cse
 */
public class CheckConstraint implements Constraint {

    public static final String CONSTRAINT_TYPE = "CHECK";
    
    private final ICheckConstraint iconstraint = new ICheckConstraint() {

		public String getName() {
			return CheckConstraint.this.getName();
		}

		public ITable getTable() {
			return CheckConstraint.this.getTable().getITable();
		}

		public String getExpression() {
			return CheckConstraint.this.getExpression();
		}
    };
    
    private final Table table;
    private String name;
    private String expression;
    
    public CheckConstraint(String name, String expression, Table table) {
        this.name = name;
        this.table = table;
        this.expression = expression;
    }
    
    
    public ICheckConstraint getIConstraint() {
		return iconstraint;
	}

	public String getName() {
        return name;
    }
    
    public String getExpression() {
        return expression;
    }

    public String getConstraintBody() {
        return getExpression();
    }

    public String getConstraintType() {
        return CONSTRAINT_TYPE;
    }

    public Table getTable() {
        return table;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setName(String name) {
        this.name = name;
    }
  
}
