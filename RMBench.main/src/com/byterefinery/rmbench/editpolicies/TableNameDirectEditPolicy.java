/*
 * created 14.03.2005
 * 
 * $Id: TableNameDirectEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;

import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.CommandAdapter;
import com.byterefinery.rmbench.operations.TableNameOperation;

/**
 * edit policy for direct editing of the table name
 * 
 * @author cse
 */
public class TableNameDirectEditPolicy extends DirectEditPolicy {

    protected Command getDirectEditCommand(DirectEditRequest request) {
        CellEditor cellEditor = request.getCellEditor();
        Table table = ((TableEditPart)getHost()).getTable();
        return new CommandAdapter(new TableNameOperation(table, (String)cellEditor.getValue()));
    }

    protected void showCurrentEditValue(DirectEditRequest request) {
    }
}
