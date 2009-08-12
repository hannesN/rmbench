/*
 * created 03.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.test;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.model.ICheckConstraint;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.IPrimaryKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;
import com.byterefinery.rmbench.external.model.IUniqueConstraint;

/**
 * test DDL generator
 * 
 * @author cse
 */
public class DDLGenerator implements IDDLGenerator {

    /**
     * factory class for this generator
     * @author cse
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new DDLGenerator(database);
        }
    }

    private DDLGenerator(IDatabaseInfo database) {
    }

    public void dropColumnComment(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropColumnComment");
    }


    public void dropColumn(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropColumn");
    }


    public void dropForeignKey(IForeignKey foreignKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropForeignKey");
    }


    public void dropIndex(IIndex index, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropIndex");
    }


    public void dropPrimaryKey(IPrimaryKey primaryKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropPrimaryKey");
    }


    public void dropSchema(ISchema schema, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropSchema");
    }


    public void dropTableComment(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropTableComment");
    }


    public void dropTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        statement.append("dropTable");
    }


    public void createColumnComment(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createColumnComment");
    }


    public void alterColumnComment(IColumn modelColumn, String newComment, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterColumnComment");
    }


    public void addColumn(IColumn column, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createColumn");
    }


	public void alterColumnType(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterColumnType");
	}

	public void alterColumnNullable(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterColumnNullable");
	}

	public void alterColumnDefault(IColumn oldColumn, IColumn newColumn, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterColumnDefault");
	}

    public void createForeignKey(IForeignKey foreignKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createForeignKey");
    }


    public void alterForeignKey(IForeignKey foreignKey, IForeignKey dbKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterForeignKey");
    }


    public void createIndex(IIndex index, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createIndex");
    }


    public void alterIndex(IIndex index, IIndex dbIndex, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterIndex");
    }


    public void createPrimaryKey(IPrimaryKey primaryKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createPrimaryKey");
    }


    public void alterPrimaryKey(IPrimaryKey primaryKey, IPrimaryKey dbKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterPrimaryKey");
    }


    public void createSchema(ISchema schema, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createSchema");
    }


    public void createTableComment(ITable table, String value, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createTableComment");
    }


    public void alterTableComment(ITable table, String newComment, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.ALTER_STATEMENT);
        statement.append("alterTableComment");
    }


    public void createTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createTable");
    }


	public void createCheckConstraint(ICheckConstraint constraint, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createCheckConstraint");
	}

	public void createUniqueConstraint(IUniqueConstraint constraint, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        statement.append("createUniqueConstraint");
	}

    public boolean getOptionValue() {
        return false;
    }
}
