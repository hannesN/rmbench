/*
 * created 16.03.2005
 * 
 * $Id:DiagramContainerEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.dnd.ImportTransfer;
import com.byterefinery.rmbench.dnd.ModelTransfer;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.operations.AddTableOperation;
import com.byterefinery.rmbench.operations.AddToDiagramOperation;
import com.byterefinery.rmbench.operations.CommandAdapter;
import com.byterefinery.rmbench.operations.ImportSchemaOperation;


/**
 * edit policy for creating new components within the schema diagram
 * 
 * @author cse
 */
public class DiagramContainerEditPolicy extends ContainerEditPolicy {

    //@see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
    protected Command getCreateCommand(CreateRequest request) {
        
        Point location = request.getLocation();
        final DiagramEditPart diagramPart = (DiagramEditPart)getHost();
        diagramPart.getFigure().translateToRelative(location);
        
        if(ComponentFactory.TABLE == request.getNewObjectType()) {
	        return new CommandAdapter(new AddTableOperation(diagramPart.getDiagram(), location)) {
                public void execute() {
                    int numTables = diagramPart.getDiagram().getModel().getTableCount();
                    if(!RMBenchPlugin.getLicenseManager().checkMaxTables(numTables))
                        super.execute();
                }
            };
        }
        else if(ImportTransfer.TYPE_NAME == request.getNewObjectType()) {
	        return new CommandAdapter(
	        		new ImportSchemaOperation(
	        				diagramPart,
	        				(Object[])request.getNewObject(),
	        				location));
        }
        else if(ModelTransfer.TYPE_NAME == request.getNewObjectType()) {
            return new CommandAdapter(
                    new AddToDiagramOperation(
                            diagramPart,
                            (Object[])request.getNewObject(),
                            location));
        }
        return null;
    }

    public EditPart getTargetEditPart(Request request) {
        
        if (REQ_CREATE.equals(request.getType()))
            return getHost();
        if (REQ_ADD.equals(request.getType()))
            return getHost();
        if (REQ_MOVE.equals(request.getType()))
            return getHost();
        return super.getTargetEditPart(request);
    }
}
