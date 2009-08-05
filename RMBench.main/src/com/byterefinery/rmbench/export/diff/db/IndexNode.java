/*
 * created 28.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: IndexNode.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.export.diff.db;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.diff.DiffUtils;
import com.byterefinery.rmbench.export.diff.IComparisonNode;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBIndex;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddIndexOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * @author cse
 */
class IndexNode extends DBValueNode {

    private final DBIndex index;
    
    protected IndexNode(DBIndex index) {
        super(index.name, RMBenchPlugin.getImage(ImageConstants.INDEX));
        this.index = index;
    }

    public Object getElement() {
        return index;
    }

    public void generateDropDDL(IDDLGenerator generator, IDDLScript script) {
        script.beginStatementContext();
        generator.dropIndex(index.getIIndex(), script);
        setStatementContext(script.endStatementContext());
    }

	protected String generateValue(boolean ignoreCase) {
		return DiffUtils.generateIndexValue(
                index.name,
                index.unique,
                index.getColumnNames(), 
                index.getAscending(),
                ignoreCase);
	}

	public String getNodeType() {
		return IComparisonNode.INDEX;
	}

	public RMBenchOperation newAddToModelOperation(Model model) {
		Table table = model.findTable(index.table.getSchemaName(), index.table.getName());
		String[] colNames = index.getColumnNames();
		Column[] columns = new Column[colNames.length];
		for (int i=0; i<columns.length; i++) {
			columns[i] = table.getColumn(colNames[i]);
		}
		return new AddIndexOperation(index.name, columns, table, index.unique, index.getAscending());
	}
}
