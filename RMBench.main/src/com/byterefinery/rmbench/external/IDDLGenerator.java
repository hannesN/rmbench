/*
 * created 26.11.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IDDLGenerator.java 682 2008-03-03 22:39:50Z cse $
 */
package com.byterefinery.rmbench.external;

import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;


/**
 * specification of a DDL generator
 * 
 * @author cse
 */
public interface IDDLGenerator {

    /**
     * a factory for creating IDDLGenerator instances
     */
    interface Factory {

        /**
         * @param database a database info whose ID corresponds to the value of the <em>database</em>
         * attribute of the DDL generator extension point 
         * @return a new generator instance for the given database
         */
        IDDLGenerator getGenerator(IDatabaseInfo database);
    }
    
    /**
     * @param column
     * @param script the script to generate statements into
     */
    void dropColumnComment(IColumn column, IDDLScript script);

    /**
     * @param column
     * @param script the script to generate statements into
     */
    void dropColumn(IColumn column, IDDLScript script);

    /**
     * @param foreignKey
     * @param script the script to generate statements into
     */
    void dropForeignKey(IForeignKey foreignKey, IDDLScript script);

    /**
     * @param index
     * @param script the script to generate statements into
     */
    void dropIndex(IIndex index, IDDLScript script);

    /**
     * @param primaryKey
     * @param script the script to generate statements into
     */
    void dropPrimaryKey(IPrimaryKey primaryKey, IDDLScript script);

    /**
     * @param schema
     * @param script the script to generate statements into
     */
    void dropSchema(ISchema schema, IDDLScript script);

    /**
     * @param table
     * @param script the script to generate statements into
     */
    void dropTableComment(ITable table, IDDLScript script);

    /**
     * @param table
     * @param script the script to generate statements into
     */
    void dropTable(ITable table, IDDLScript script);

    /**
     * @param column
     * @param script the script to generate statements into
     */
    void createColumnComment(IColumn column, IDDLScript script);

    /**
     * @param column
     * @param newComment
     * @param script the script to generate statements into
     */
    void alterColumnComment(IColumn column, String newComment, IDDLScript script);

    /**
     * @param column
     * @param script the script to generate statements into
     */
    void addColumn(IColumn column, IDDLScript script);

	/**
	 * @param oldColumn
	 * @param newColumn
     * @param script the script to generate statements into
	 */
	void alterColumnType(IColumn oldColumn, IColumn newColumn, IDDLScript script);

	/**
	 * @param oldColumn
	 * @param newColumn
     * @param script the script to generate statements into
	 */
	void alterColumnNullable(IColumn oldColumn, IColumn newColumn, IDDLScript script);

	/**
	 * @param oldColumn
	 * @param newColumn
     * @param script the script to generate statements into
	 */
	void alterColumnDefault(IColumn oldColumn, IColumn newColumn, IDDLScript script);
	
    /**
     * @param foreignKey
     * @param script the script to generate statements into
     */
    void createForeignKey(IForeignKey foreignKey, IDDLScript script);

    /**
     * @param oldKey
     * @param newKey
     * @param script the script to generate statements into
     */
    void alterForeignKey(IForeignKey oldKey, IForeignKey newKey, IDDLScript script);

    /**
     * @param index
     * @param script the script to generate statements into
     */
    void createIndex(IIndex index, IDDLScript script);

    /**
     * @param oldIndex
     * @param newIndex
     * @param script the script to generate statements into
     */
    void alterIndex(IIndex oldIndex, IIndex newIndex, IDDLScript script);

    /**
     * @param primaryKey
     * @param script the script to generate statements into
     */
    void createPrimaryKey(IPrimaryKey primaryKey, IDDLScript script);

    /**
     * @param newKey
     * @param oldKey
     * @param script the script to generate statements into
     */
    void alterPrimaryKey(IPrimaryKey oldKey, IPrimaryKey newKey, IDDLScript script);

    /**
     * @param schema
     * @param script the script to generate statements into
     */
    void createSchema(ISchema schema, IDDLScript script);

    /**
     * @param table the owning table
     * @param value
     * @param script the script to generate statements into
     */
    void createTableComment(ITable table, String value, IDDLScript script);

    /**
     * @param table the owning table
     * @param newComment
     * @param dbComment
     * @param script the script to generate statements into
     */
    void alterTableComment(ITable table, String newComment, IDDLScript script);

    /**
     * @param table the new table
     * @param script the script to generate statements into
     */
    void createTable(ITable table, IDDLScript script);

    /**
     * generate DDL to create a new check constraint 
     * @param constraint the new constraint
     * @param script the target script
     */
	void createCheckConstraint(ICheckConstraint constraint, IDDLScript script);

    /**
     * generate DDL to create a new unique constraint 
     * @param constraint the new constraint
     * @param script the target script
     */
	void createUniqueConstraint(IUniqueConstraint constraint, IDDLScript script);
}
