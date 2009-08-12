/*
 * created 18.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *  $Id$
 */
package com.byterefinery.rmbench.database.derby;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.external.model.ISchema;
import com.byterefinery.rmbench.external.model.ITable;


/**
 * Class which generates the DDL Code for a Derby Database.
 * 
 * @author Hannes Niederhausen
 *
 */
public class DerbyDDLGenerator extends DefaultDDLGenerator {

    /**
     * factory class for this generator
     * 
     * @author cse
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new DerbyDDLGenerator(database);
        }
    }
    

    protected final Keywords _restrict = new Keywords(" restrict");
    protected final Keywords drop_foreign_key = new Keywords("drop foreign key");
    
    protected DerbyDDLGenerator(IDatabaseInfo database) {
        super(database);
    }


    
    public void dropColumn(IColumn column, IDDLScript script) {
        //drop column is not supported by derby
    }
    
    public void dropTable(ITable table, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_table_.append(statement);
        printTable(table, statement);
    }
    
    public void dropSchema(ISchema schema, IDDLScript script) {
        super.dropSchema(schema, script);
        IDDLScript.Statement statement = script.getLastStatement();
        _restrict.append(statement);
    }
    
    public void dropForeignKey(IForeignKey foreignKey, IDDLScript script) {
        IDDLScript.Statement statement = script.createStatement(DerbyDDLScript.ALTER_DROP_STATEMENT);
        
        alter_table_.append(statement);
        printTable(foreignKey.getTable(), statement);
        statement.append(" ");
        
        drop_foreign_key.append(statement);
        statement.append(" ");
        statement.append(foreignKey.getName());
    }
}
