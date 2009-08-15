/*
 * created 12.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ModifyColumnOperation.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.operations;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * optional superclass for column modifications that also affect keys
 * 
 * @author cse
 */
public abstract class ModifyColumnOperation extends RMBenchOperation {

    protected final Column column;
    
	public ModifyColumnOperation(String label, Column column) {
		super(label);
		this.column = column;
	}

	/**
	 * fire modification events for all foreign keys this column belongs to
	 * @param property
	 */
	protected void fireForeignKeyEvents(String property) {
		for (ForeignKey foreignKey : column.getForeignKeys()) {
			RMBenchPlugin.getEventManager().fireForeignKeyModified(property, foreignKey);
		}
	}
    
    /**
     * If the column is part of a primary key, the changes made has to be done 
     * to the corresponding foreignkeys. After that this method fires a ForeignKeyModified
     * modified event for all references.
     * @param property 
     */
    protected void fireReferencesEvents(String property) {
        if (column.belongsToPrimaryKey()) {
            for (ForeignKey foreignKey : column.getTable().getReferences()) {
                RMBenchPlugin.getEventManager().fireForeignKeyModified(property, foreignKey);
            }
        }
    }
}
