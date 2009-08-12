/*
 * created 12.12.2005
 * 
 * $Id:ForeignKeyEditPart.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gef.tools.ConnectionEndpointTracker;
import org.eclipse.swt.graphics.Cursor;

import com.byterefinery.rmbench.editparts.ForeignKeyEditPart;
import com.byterefinery.rmbench.figures.MoveableAnchor;
import com.byterefinery.rmbench.operations.MoveAnchorOperation;

/**
 * Tracker for anchor movement.
 * 
 * @author Hannes Niederhausen
 *
 */
public class RMBConnectionEndpointTracker extends ConnectionEndpointTracker {
    
    private String commandName;

    /*
     * a reconnect request that maintains the target reference. We dont want reconnect
     * operations, therefore this avoids routing anomalies during dragging of connection 
     * endpoints
     */
    private static class ReconnectRequest2 extends ReconnectRequest {
        public void setTargetEditPart(EditPart part) {
            //part might be null => ignore
        }
        public EditPart getTarget() {
            return getConnectionEditPart().getTarget();
        }
    }
    
    public RMBConnectionEndpointTracker(ConnectionEditPart cep, String commandName) {
       super(cep);
       setCommandName(commandName);
    }

    protected Cursor getDefaultCursor() {
        return Cursors.SIZEALL;
    }
    
    protected Request createTargetRequest() {
        ReconnectRequest request = new ReconnectRequest2();
        
        Point location = new Point();
        location.x = getStartLocation().x + getDragMoveDelta().width;
        location.y = getStartLocation().y + getDragMoveDelta().height;
        
        request.setLocation(location);
        request.setConnectionEditPart(getConnectionEditPart());
        request.setType(getCommandName());
        
        return request;
    }
    
    public void setCommandName(String newCommandName) {
        commandName = newCommandName;
    }
    
    protected String getCommandName() {
        return commandName;
    }
    
    protected boolean handleMove() {
        return true;
    }
    
    protected boolean handleButtonDown(int button) {
        MoveableAnchor anchor = null;
        if (commandName.equals(REQ_RECONNECT_SOURCE)) {
            anchor = (MoveableAnchor) ((ForeignKeyEditPart) getConnectionEditPart()).getSourceConnectionAnchor();
        } else {
            anchor = (MoveableAnchor) ((ForeignKeyEditPart) getConnectionEditPart()).getTargetConnectionAnchor();
        }
        anchor.setPreview(true);
        return super.handleButtonDown(button);
    }
    
    protected boolean handleButtonUp(int button) {
        MoveableAnchor anchor = null;
        if (commandName.equals(REQ_RECONNECT_SOURCE)) {
            anchor = (MoveableAnchor) ((ForeignKeyEditPart) getConnectionEditPart())
                    .getSourceConnectionAnchor();
        }
        else {
            anchor = (MoveableAnchor) ((ForeignKeyEditPart) getConnectionEditPart())
                    .getTargetConnectionAnchor();
        }
        anchor.setPreview(false);

        MoveAnchorOperation op = new MoveAnchorOperation(anchor, anchor.getEdge(), anchor
                .getSlotNumber());
        op.execute(this);

        return super.handleButtonUp(button);
    }
    
    protected Cursor calculateCursor() {
        return Cursors.SIZEALL;
    }
}
