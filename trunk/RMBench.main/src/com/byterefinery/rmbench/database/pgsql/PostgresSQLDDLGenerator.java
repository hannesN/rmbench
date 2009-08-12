/*
 * created 19.02.2007
 * 
 * $Id$
 */ 
package com.byterefinery.rmbench.database.pgsql;

import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.database.sql99.DefaultDDLGenerator;
import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IIndex;
import com.byterefinery.rmbench.external.model.ITable;

/**
 * The DDL Generator for the postgres database. 
 * 
 * @author Hannes Niederhausen
 *
 */
public class PostgresSQLDDLGenerator extends DefaultDDLGenerator {

    /**
     * factory class for this generator
     * 
     * @author cse
     */
    public static class Factory implements IDDLGenerator.Factory {
        public IDDLGenerator getGenerator(IDatabaseInfo database) {
            return new PostgresSQLDDLGenerator(database);
        }
    }
	
	protected PostgresSQLDDLGenerator(IDatabaseInfo database) {
		super(database);
	}

	
	public void dropTable(ITable table, IDDLScript script) {
		IDDLScript.Statement statement = script.createStatement(IDDLScript.DROP_STATEMENT);
        
        drop_table_.append(statement);
        printTable(table, statement);
        _cascade.append(statement);
	}
	
	public void createIndex(IIndex index, IDDLScript script) {
        if ((!getExportPKIndexes()) && (isPrimaryKeyIndex(index)))
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

        printColumns(statement, columns);

        statement.append(")");
    }
}
