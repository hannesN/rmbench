/*
 * created 14.03.2005
 * 
 * $Id: TableComponentEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.operations.CommandAdapter;
import com.byterefinery.rmbench.operations.DeleteTableOperation;

/**
 * table component policy. Provides a delete command only. Not used because 
 * {@link com.byterefinery.rmbench.actions.DeleteAction} does not use policies
 * 
 * @author cse
 */
public class TableComponentEditPolicy extends ComponentEditPolicy {

    protected Command createDeleteCommand(GroupRequest request) {
        TableEditPart tablePart = (TableEditPart) getHost();
        DiagramEditPart diagramPart = (DiagramEditPart)tablePart.getParent();
        
        return new CommandAdapter(
                new DeleteTableOperation(diagramPart.getDiagram(), tablePart.getDTable()));
    }
}
