/*
 * created 10.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.mysql;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IDDLScript.Statement;
import com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * The MySQLDDLGenerator creates the DDL of a Databse model using MySQL Syntax
 * 
 * @author Hannes Niederhausen
 *
 */
public class MySQLDDLGenerator extends DefaultDDLGenerator {

    /**
     * factory class for this generator
     * 
     * @author cse
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new MySQLDDLGenerator(database);
        }
    }

    protected final Keywords drop_foreignkey = new Keywords("drop foreign key");
    protected final Keywords drop_primarykey = new Keywords("drop primary key");
    protected final Keywords _change_ = new Keywords(" change ");
    
    
    
    final static public String TABLETYPE_INNODB = "InnoDB";
    final static public String TABLETYPE_MYISAM = "MyISAM";
    
    private String tableType;
    

    protected MySQLDDLGenerator(IDatabaseInfo databaseInfo) {
    	super(databaseInfo);
        tableType = "InnoDB";
    }
    
    /**
     * @return Returns the tableType.
     */
    public String getTableType() {
        return tableType;
    }

    /**
     * Sets the database type, which is either InnoDB or MyISAM. Other values will be ignored.
     * 
     * @param tableType The tableType to set.
     */
    public void setTableType(String databaseType) {
        if ((databaseType.equals(TABLETYPE_INNODB)) || (databaseType.equals(TABLETYPE_MYISAM)))
            this.tableType = databaseType;
    }
    
    /* (non-Javadoc)
     * @see com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator#dropPrimaryKey(com.byterefinery.rmbench.external.model.IPrimaryKey, com.byterefinery.rmbench.external.IDDLScript)
     */
    public void dropPrimaryKey(IPrimaryKey primaryKey, IDDLScript script) {
    	IDDLScript.Statement statement = script.createStatement(MySQLDDLScript.ALTER_DROP_STATEMENT);
    	
    	alter_table_.append(statement);
    	printTable(primaryKey.getTable(), statement);
    	statement.append(" ");
    	drop_primarykey.append(statement);
    }

    public void dropForeignKey(IForeignKey foreignKey, IDDLScript script) {
        if (tableType.equals(TABLETYPE_MYISAM))
            return;
        IDDLScript.Statement statement = script.createStatement(MySQLDDLScript.ALTER_DROP_STATEMENT);
        
        alter_table_.append(statement);
        printTable(foreignKey.getTable(), statement);
        statement.append(" ");
        
        drop_foreignkey.append(statement);
        statement.append(" ");
        statement.append(foreignKey.getName());
    }

    public void dropTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_table_.append(statement);
        printTable(table, statement);
    }
    
    public void dropIndex(IIndex index, IDDLScript writer) {
        IDDLScript.Statement statement = writer.createStatement(MySQLDDLScript.ALTER_DROP_STATEMENT);
        
        alter_table_.append(statement);
        printTable(index.getTable(), statement);
        statement.append(" ");
        drop_index_.append(statement);
        statement.append(index.getName());
    }
    
    public void createForeignKey(IForeignKey foreignKey, IDDLScript script) {
        if (tableType.equals(TABLETYPE_MYISAM))
            return;
        
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
        statement.append("(");        
        printColumns(statement, foreignKey.getTargetTable().getPrimaryKey().getColumnNames());
        statement.append(")");
        _on_delete.append(statement);
        printFKAction(foreignKey.getDeleteAction(), statement);
        _on_update.append(statement);
        printFKAction(foreignKey.getUpdateAction(), statement);
    }

    public void createTable(ITable table, IDDLScript script) {
        super.createTable(table, script);
        IDDLScript.Statement statement = script.getLastStatement();
        statement.append(" ENGINE=");
        statement.append(tableType);
    }
    
    public void dropColumn(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(column.getTable(), statement);
        _drop_column_.append(statement);
        printColumnName(statement, column.getName());
    }
        
    protected void printPrimaryKey(IPrimaryKey primaryKey, Statement statement) {
        _primary_key_.append(statement);
        statement.append("(");
        printColumns(statement, primaryKey.getColumnNames());
        statement.append(")");
    }

	protected char getQuoteChar() {
		return '`';
	}
	
	public void alterColumnDefault(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
		alterColumn(oldColumn, newColumn, script);
	}
	
	public void alterColumnType(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
		alterColumn(oldColumn, newColumn, script);
	}
	
	public void alterColumnNullable(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
		alterColumn(oldColumn, newColumn, script);
	}
		
	protected void alterColumn(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        
        alter_table_.append(statement);
        printTable(newColumn.getTable(), statement);
        _change_.append(statement);
        printColumnName(statement, oldColumn.getName());
        statement.append(" ");
        printColumnName(statement, newColumn.getName());
        statement.append(" ");
        statement.append(newColumn.getDataType().getDDLName());
        if(newColumn.getDefault() != null) {
            _default_.append(statement);
        	statement.append(" ");
            statement.append(newColumn.getDefault());
        }
        if(newColumn.getNullable() == false) {
            _not_null.append(statement);
        }
	}
}
