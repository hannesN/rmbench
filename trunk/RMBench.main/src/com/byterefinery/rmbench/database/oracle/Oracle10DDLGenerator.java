/*
 * created 19.05.2007
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
package com.byterefinery.rmbench.database.oracle;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.ISchema;

/**
 * DDL generator for the Oracle database, following the SQL Reference for Oracle 10g
 * 
 * @author cse
 */
public class Oracle10DDLGenerator extends DefaultDDLGenerator {
    
    /**
     * factory class for this generator
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new Oracle10DDLGenerator(database);
        }
    }

    protected final Keywords create_user_ = new Keywords("create user ");
    protected final Keywords alter_user_ = new Keywords("alter user ");
    protected final Keywords quota_users_ = new Keywords(" quota 100M on users");
    protected final Keywords identified_by_ = new Keywords(" identified by ");
    
    protected Oracle10DDLGenerator(IDatabaseInfo database) {
        super(database);
    }

    public void createSchema(ISchema schema, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.CREATE_STATEMENT);
        
        create_user_.append(statement);
        printObjectName(statement, schema.getName());
        identified_by_.append(statement);
        statement.append(schema.getName());
        
        statement = script.createStatement(IDDLScript.CREATE_STATEMENT); //NOT alter!
        alter_user_.append(statement);
        statement.append(schema.getName());
        quota_users_.append(statement);
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
        
        if(foreignKey.getDeleteAction() == IForeignKey.CASCADE) {
            _on_delete.append(statement);
            _cascade.append(statement);
        }
        else if (foreignKey.getDeleteAction() == IForeignKey.SET_NULL) {
            _on_delete.append(statement);
            _set_null.append(statement);
        }
        //TODO: create trigger for on delete SET DEFAULT
        
        if(foreignKey.getUpdateAction() == IForeignKey.CASCADE) {
            //TODO: create trigger
        }
        else if(foreignKey.getUpdateAction() == IForeignKey.SET_NULL) {
            //TODO: create trigger
        }
        else if(foreignKey.getUpdateAction() == IForeignKey.SET_DEFAULT) {
            //TODO: create trigger
        }
    }
}
