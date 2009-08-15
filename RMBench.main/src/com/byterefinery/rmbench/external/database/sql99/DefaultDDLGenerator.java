/*
 * created 28.11.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DefaultDDLGenerator.java 682 2008-03-03 22:39:50Z cse $
 */
package com.byterefinery.rmbench.external.database.sql99;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IDDLScript.Statement;
import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * Generator for generic SQL99 DDL. Supports upper-/lowercasing and quoting identifers
 * 
 * @author cse
 */
public class DefaultDDLGenerator implements IDDLGenerator {
    
    /**
     * factory class for this generator
     * 
     * @author cse
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new DefaultDDLGenerator(database);
        }
    }

    /**
     * a helper that wraps a group of keywords and prints them in upper- or
     * lowercase depending on configuration
     */
    protected class Keywords {
        private final String base;
        
        public Keywords(String base) {
            this.base = base;
        }
        
        public void append(Statement statement) {
            statement.append(uppercaseKeywords ? base.toUpperCase() : base.toLowerCase());
        }
    }
    
    protected final Keywords alter_table_ = new Keywords("alter table ");
    protected final Keywords _drop_column_ = new Keywords(" drop column ");
    protected final Keywords _cascade = new Keywords(" cascade");
    protected final Keywords _constraints = new Keywords(" constraints");
    protected final Keywords drop_schema_ = new Keywords("drop schema ");
    protected final Keywords drop_table_ = new Keywords("drop table ");
    protected final Keywords _drop_constraint_ = new Keywords(" drop constraint ");
    protected final Keywords _add_column_ = new Keywords(" add column ");
    protected final Keywords _not_null = new Keywords(" not null");
    protected final Keywords _alter_column_ = new Keywords(" alter column ");
    protected final Keywords drop_default = new Keywords("drop default");
    protected final Keywords set_default_ = new Keywords("set default ");
    protected final Keywords _constraint_ = new Keywords(" constraint ");
    protected final Keywords _foreign_key_ = new Keywords(" foreign key ");
    protected final Keywords _check_ = new Keywords(" check ");
    protected final Keywords _unique_ = new Keywords(" unique ");
    protected final Keywords _references_ = new Keywords(" references ");
    protected final Keywords _on_delete = new Keywords(" on delete");
    protected final Keywords _on_update = new Keywords(" on update");
    protected final Keywords _restrict = new Keywords(" restrict");
    protected final Keywords _no_action = new Keywords(" no action");
    protected final Keywords _set_null = new Keywords(" set null");
    protected final Keywords _set_default = new Keywords(" set default");
    protected final Keywords _add = new Keywords(" add");
    protected final Keywords create_table_ = new Keywords("create table ");
    protected final Keywords create_schema_ = new Keywords("create schema ");
    protected final Keywords _default_ = new Keywords(" default ");
    protected final Keywords _primary_key_ = new Keywords(" primary key ");
    protected final Keywords drop_index_ = new Keywords("drop index ");
    protected final Keywords create_index_ = new Keywords("create index ");
    protected final Keywords create_unique_index_ = new Keywords("create unique index ");
    protected final Keywords on_ = new Keywords("on ");
    protected final Keywords _desc_ = new Keywords(" desc");
    protected final Keywords _asc_ = new Keywords(" asc");
    
    protected DefaultDDLGenerator(IDatabaseInfo database) {
    }

    private boolean quoteObjectNames;
    private boolean quoteColumnNames;
    private boolean uppercaseKeywords;
    private boolean exportPKIndexes;
    
    /**
     * @return true if identifiers should be quoted
     */
    public boolean getQuoteObjectNames() {
        return quoteObjectNames;
    }

    /**
     * @return true if columns names should be quoted
     */
    public boolean getQuoteColumnNames() {
        return quoteColumnNames;
    }

    /**
     * @param quote true if identifiers should be quoted
     */
    public void setQuoteObjectNames(boolean quote) {
        this.quoteObjectNames = quote;
    }

    /**
     * @param quote true if column names should be quoted
     */
    public void setQuoteColumnNames(boolean quote) {
        this.quoteColumnNames = quote;
    }

    /**
     * @return true if SQL keywords should be uppercased
     */
    public boolean getUppercaseKeywords() {
        return uppercaseKeywords;
    }
    
    /**
     * @param uppercaseKeywords true if SQL keywords should be uppercased
     */
    public void setUppercaseKeywords(boolean uppercaseKeywords) {
        this.uppercaseKeywords = uppercaseKeywords;
    }

    /**
     * do nothing as comments are not part of standard SQL
     */
    public void dropColumnComment(IColumn column, IDDLScript script) {
    }

    public void dropColumn(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(column.getTable(), statement);
        _drop_column_.append(statement);
        printColumnName(statement, column.getName());
        _cascade.append(statement);
        _constraints.append(statement);
    }

    public void dropForeignKey(IForeignKey foreignKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        printDropConstraint(
                foreignKey.getName(), 
                foreignKey.getTable(), 
                statement);
    }

    public void dropPrimaryKey(IPrimaryKey primaryKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        printDropConstraint(
                primaryKey.getName(), 
                primaryKey.getTable(), 
                statement);
    }

    public void dropSchema(ISchema schema, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_schema_.append(statement);
        printObjectName(statement, schema.getName());
    }

    /**
     * do nothing as comments are not part of standard SQL
     */
    public void dropTableComment(ITable table, IDDLScript script) {
    }

    public void dropTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_table_.append(statement);
        printTable(table, statement);
        _cascade.append(statement);
        _constraints.append(statement);
    }

    /**
     * do nothing as comments are not part of standard SQL
     */
    public void createColumnComment(IColumn column, IDDLScript script) {
    }

    /**
     * do nothing as comments are not part of standard SQL
     */
    public void alterColumnComment(IColumn oldColumn, String newComment, IDDLScript script) {
    }

    public void addColumn(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(column.getTable(), statement);
        _add_column_.append(statement);
        printColumnName(statement, column.getName());
        statement.append(" ");
        statement.append(column.getDataType().getDDLName());
        if(column.getDefault() != null) {
            statement.append(" ");
            statement.append(column.getDefault());
        }
        if(column.getNullable() == false) {
            _not_null.append(statement);
        }
    }

    /**
     * standard SQL does not support altering the data type. A drop and subsequent create statement 
     * will be generated
     */
	public void alterColumnType(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        dropColumn(oldColumn, script);
        addColumn(newColumn, script);
	}

    /**
     * standard SQL does not support altering the NULL constraint. A drop and subsequent create statement 
     * will be generated
     */
	public void alterColumnNullable(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        dropColumn(oldColumn, script);
        addColumn(newColumn, script);
	}

	public void alterColumnDefault(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        alter_table_.append(statement);
        printTable(oldColumn.getTable(), statement);
        
        _alter_column_.append(statement);
        printColumnName(statement, newColumn.getName());
        statement.append(" ");
        
        if(newColumn.getDefault() != null)
            drop_default.append(statement);
        else {
            set_default_.append(statement);
            statement.append(newColumn.getDefault());
        }
	}
	
    public void createForeignKey(IForeignKey foreignKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(foreignKey.getTable(), statement);
        _add.append(statement);
        _constraint_.append(statement);
        printObjectName(statement, foreignKey.getName());
        _foreign_key_.append(statement);
        statement.append("(");
        printColumns(statement, foreignKey.getColumnNames());
        statement.append(")");
        _references_.append(statement);
        printTable(foreignKey.getTargetTable(), statement);
        
        _on_delete.append(statement);
        printFKAction(foreignKey.getDeleteAction(), statement);
        _on_update.append(statement);
        printFKAction(foreignKey.getUpdateAction(), statement);
    }

    /**
     * generate a drop and a subsequent create statement for the foreign key
     */
	public void alterForeignKey(IForeignKey oldKey, IForeignKey newKey, IDDLScript script) {
        
        dropForeignKey(oldKey, script);
        createForeignKey(newKey, script);
    }

	/**
	 * generates a create index statement
	 */
    public void createIndex(IIndex index, IDDLScript script) {
        if ( (!exportPKIndexes) && (isPrimaryKeyIndex(index)) )
        		return;
    	
    	IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        
        if (index.isUnique())
            create_unique_index_.append(statement);
        else
            create_index_.append(statement);
        
        statement.append(index.getName());
        statement.append(" ");
        on_.append(statement);
        printTable(index.getTable(), statement);
        statement.append("(");
        
        IColumn[] columns = index.getColumns();
        for (int i=0; i<columns.length; i++) {
            printColumnName(statement, columns[i].getName());
            if (index.isAscending(columns[i])) {
            	_asc_.append(statement);
            } else {
            	_desc_.append(statement);
            }
            if(i < columns.length - 1)
                statement.append(", ");
        }
        statement.append(")");
    }

    /**
     * creates a drop index statement
     */
    public void dropIndex(IIndex index, IDDLScript writer) {
        IDDLScript.Statement statement = writer.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_index_.append(statement);
        statement.append(index.getName());
    }
    
    /**
     * generate a drop and a subsequent create statement for the index
     * @see #createIndex(IIndex, IDDLScript)
     */
    public void alterIndex(IIndex dbIndex, IIndex modelIndex, IDDLScript script) {
    	dropIndex(dbIndex, script);
    	createIndex(modelIndex, script);
    }

    public void createPrimaryKey(IPrimaryKey primaryKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(primaryKey.getTable(), statement);
        _add.append(statement);
        printPrimaryKey(primaryKey, statement);
        
    }
    
    /**
     * generate a drop and a subsequent create statement for the primary key
     */
    public void alterPrimaryKey(IPrimaryKey dbKey, IPrimaryKey primaryKey, IDDLScript script) {
        dropPrimaryKey(dbKey, script);
        createPrimaryKey(primaryKey, script);
    }

    public void createSchema(ISchema schema, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        
        create_schema_.append(statement);
        printObjectName(statement, schema.getName());
    }

    /**
     * do nothing as object comments are not part of the SQL standard 
     */
    public void createTableComment(ITable table, String value, IDDLScript script) {
    }

    /**
     * do nothing as object comments are not part of the SQL standard 
     */
    public void alterTableComment(ITable table, String newComment, IDDLScript script) {
    }

    public void createTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        
        create_table_.append(statement);
        printTable(table, statement);
        statement.append("(");
        IColumn[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {
            printColumnName(statement, columns[i].getName());
            statement.append(" ");
            statement.append(columns[i].getDataType().getDDLName());
            if(columns[i].getDefault() != null) {
                _default_.append(statement);
                statement.append(columns[i].getDefault());
            }
            if(columns[i].getNullable() == false) {
                _not_null.append(statement);
            }
            if(i < columns.length - 1)
                statement.append(", ");
        }
        if(table.getPrimaryKey() != null) {
            statement.append(", ");
            printPrimaryKey(table.getPrimaryKey(), statement);
        }
        statement.append(")");
    }


	public void createCheckConstraint(ICheckConstraint constraint, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(constraint.getTable(), statement);
        _add.append(statement);
        _constraint_.append(statement);
        printObjectName(statement, constraint.getName());
        _check_.append(statement);
        statement.append("(");
        statement.append(constraint.getExpression());
        statement.append(")");
	}

	public void createUniqueConstraint(IUniqueConstraint constraint, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(constraint.getTable(), statement);
        _add.append(statement);
        _constraint_.append(statement);
        printObjectName(statement, constraint.getName());
        _unique_.append(statement);
        statement.append("(");
        printColumns(statement, constraint.getColumnNames());
        statement.append(")");
	}
	
	/**
	 * print a fully qualified table name 
	 */
    protected void printTable(ITable table, IDDLScript.Statement statement) {
        printObjectName(statement, table.getSchema().getName());
        statement.append(".");
        printObjectName(statement, table.getName());
    }

    /**
     * print a primary key definition, including the column list
     */
    protected void printPrimaryKey(IPrimaryKey primaryKey, IDDLScript.Statement statement) {
        _constraint_.append(statement);
        printObjectName(statement, primaryKey.getName());
        _primary_key_.append(statement);
        statement.append("(");
        printColumns(statement, primaryKey.getColumnNames());
        statement.append(")");
    }

    /**
     * print a foreign key action as part of a foreign key definition
     */
    protected void printFKAction(IForeignKey.Action fkAction, IDDLScript.Statement statement) {
        if(fkAction.equals(IForeignKey.CASCADE)) {
            _cascade.append(statement);
        }
        else if(fkAction.equals(IForeignKey.RESTRICT)) {
            _restrict.append(statement);
        }
        else if(fkAction.equals(IForeignKey.NO_ACTION)) {
            _no_action.append(statement);
        }
        else if(fkAction.equals(IForeignKey.SET_NULL)) {
            _set_null.append(statement);
        }
        else if(fkAction.equals(IForeignKey.SET_DEFAULT)) {
            _set_default.append(statement);
        }
	}

    /**
     * print a DROP CONSTRAINT statement
     */
    protected void printDropConstraint(String name, ITable table, IDDLScript.Statement statement) {
        alter_table_.append(statement);
        printTable(table, statement);
        _drop_constraint_.append(statement);
        printObjectName(statement, name);
    }

    /**
     * @return the character to be used for quoting
     */
    protected char getQuoteChar() {
    	return '"';
    }
    
    
    /**
     * print the column names, separated by commas
     * @param IDDLScript.Statement statement the statement to print to
     * @param columns the columns to print
     */
    protected void printColumns(IDDLScript.Statement statement, IColumn[] columns) {
        for (int i=0; i<columns.length; i++) {
            printColumnName(statement, columns[i].getName());
            if(i < columns.length - 1)
                statement.append(", ");
        }
    }
    
    /**
     * print the column names, separated by commas
     * @param IDDLScript.Statement statement the statement to print to
     * @param columnNames the column names to print
     */
    protected void printColumns(IDDLScript.Statement statement, String[] columnNames) {
        for (int i=0; i<columnNames.length; i++) {
            printColumnName(statement, columnNames[i]);
            if(i < columnNames.length - 1)
                statement.append(", ");
        }
    }
    
    /**
     * print a possibly quoted column name
     */
    protected void printColumnName(Statement statement, String name) {
        if(quoteColumnNames)
            statement.append(getQuoteChar());
        statement.append(name);
        if(quoteColumnNames)
            statement.append(getQuoteChar());
    }

    /**
     * print a possibly quoted object name
     */
    protected void printObjectName(Statement statement, String name) {
        if(quoteObjectNames)
            statement.append(getQuoteChar());
        statement.append(name);
        if(quoteObjectNames)
            statement.append(getQuoteChar());
    }

	public boolean getExportPKIndexes() {
		return exportPKIndexes;
	}

	public void setExportPKIndexes(boolean exportPKIndexes) {
		this.exportPKIndexes = exportPKIndexes;
	}
	
	/**
	 * 
	 * @param index
	 * @return <code>true</code> if index holds exactly the same columns as the primary key
	 * of the underlying table
	 */
	protected boolean isPrimaryKeyIndex(IIndex index) {
		
		ITable table = index.getTable();
		IPrimaryKey pk = table.getPrimaryKey();
		
		if(pk == null)
			return false;
		
		String colNames[] = pk.getColumnNames();
		IColumn indexColumns[] = index.getColumns();
		
		if (colNames.length != indexColumns.length)
			return false;
		
		for (int i=0; i<indexColumns.length; i++) {
			if (!(indexColumns[i].getName().equals(colNames[i])))
				return false;
		}
		
		return true;
	}
}
