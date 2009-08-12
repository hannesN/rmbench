/*
 * created 1.12.2005
 * 
 * $Id: ForeignKeyEndpointEditPolicy.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.editpolicies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.ConnectionEndHandle;
import org.eclipse.gef.handles.ConnectionStartHandle;
import org.eclipse.gef.requests.ReconnectRequest;

import com.byterefinery.rmbench.editparts.ForeignKeyEditPart;
import com.byterefinery.rmbench.figures.MoveableAnchor;
import com.byterefinery.rmbench.util.RMBConnectionEndpointTracker;

/**
 * a connection endpoint edit policy that will modify move requests such that the
 * position complies with the available anchor positions.
 * 
 * @see com.byterefinery.rmbench.figures.MoveableAnchor
 * @author hannesn
 */
public class ForeignKeyEndpointEditPolicy extends ConnectionEndpointEditPolicy {

    protected void showConnectionMoveFeedback(ReconnectRequest request) {
        MoveableAnchor anchor;
        ForeignKeyEditPart editPart = (ForeignKeyEditPart) request.getConnectionEditPart();
        if (!request.isMovingStartAnchor()) {
            anchor = (MoveableAnchor) editPart.getTargetConnectionAnchor();
            anchor.computeSlot(request.getLocation());
        }
        else {
            anchor = (MoveableAnchor) editPart.getSourceConnectionAnchor();
            anchor.computeSlot(request.getLocation());
        }
        request.setLocation(anchor.getLocation(request.getLocation()));
        super.showConnectionMoveFeedback(request);
    }
    
    protected List<?> createSelectionHandles() {
        
        ForeignKeyEditPart host = (ForeignKeyEditPart)getHost();
        List<AbstractHandle> list = new ArrayList<AbstractHandle>(2);
        
        list.add(createEndHandle(host));
        list.add(createStartHandle(host));
        
        return list;
    }

    private AbstractHandle createStartHandle(ForeignKeyEditPart host) {
        AbstractHandle handle = new ConnectionStartHandle(host);
        handle.setCursor(SharedCursors.SIZEALL);
        handle.setDragTracker(new RMBConnectionEndpointTracker(host, REQ_RECONNECT_SOURCE));
        return handle;
    }

    private AbstractHandle createEndHandle(ForeignKeyEditPart host) {
        AbstractHandle handle = new ConnectionEndHandle(host);
        handle.setCursor(Cursors.SIZEALL);
        handle.setDragTracker(new RMBConnectionEndpointTracker(host, REQ_RECONNECT_TARGET));
        return handle;
    }
}
