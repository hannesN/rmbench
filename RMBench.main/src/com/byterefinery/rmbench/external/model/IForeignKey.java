/*
 * created 16.12.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;



/**
 * @author cse
 */
public interface IForeignKey {

    public static class Action {
        protected final String name;
        
        public Action(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    };

    
    public static final Action NO_ACTION = new Action("NO ACTION");
    public static final Action RESTRICT = new Action("RESTRICT");
    public static final Action CASCADE = new Action("CASCADE");
    public static final Action SET_NULL = new Action("SET NULL");
    public static final Action SET_DEFAULT = new Action("SET DEFAULT");
    
    public static final Action[] ALL_ACTIONS = new Action[] {
        NO_ACTION,
        RESTRICT,
        CASCADE,
        SET_DEFAULT,
        SET_NULL
    };

    /**
     * @return the owning table
     */
    ITable getTable();

    /**
     * @return the referenced table
     */
    ITable getTargetTable();
    
    /**
     * @return the constraint name
     */
    String getName();

    /**
     * @return the column names from the owning table
     */
    String[] getColumnNames();

    /**
     * @return the delete action, or <code>null</code>
     */
    Action getDeleteAction();

    /**
     * @return the update action, or <code>null</code>
     */
    Action getUpdateAction();
}
