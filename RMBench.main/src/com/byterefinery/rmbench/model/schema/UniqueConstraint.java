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
 * $Id: UniqueConstraint.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.model.schema;

import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * a unique constraint, aka candidate key
 * 
 * @author cse
 */
public class UniqueConstraint extends Key implements Constraint {

    public static final String CONSTRAINT_TYPE = "UNIQUE";
    
    private final IUniqueConstraint iconstraint = new IUniqueConstraint() {

		public String getName() {
			return UniqueConstraint.this.getName();
		}

		public ITable getTable() {
			return UniqueConstraint.this.getTable().getITable();
		}

		public String[] getColumnNames() {
			return UniqueConstraint.this.getColumnNames();
		}
    };
    
    public UniqueConstraint(String name, Column[] columns, Table table) {
        super(name, columns, table);
    }

    public IUniqueConstraint getIConstraint() {
		return iconstraint;
	}

	public String getConstraintType() {
        return CONSTRAINT_TYPE;
    }

    public String getConstraintBody() {
        return getColumnsList();
    }
    
	public void abandon() {
		table.removeUniqueConstraint(this);
	}
	
	public void restore() {
		table.addUniqueConstraint(this);
	}
}
