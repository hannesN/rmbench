/*
 * created 14.03.2005
 * 
 * $Id:TableNodeEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FeedbackHelper;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.operations.AddForeignKeyOperation;
import com.byterefinery.rmbench.operations.CommandAdapter;


/**
 * edit policy for creating foreign key connections
 * 
 * @author cse
 */
public class TableNodeEditPolicy extends GraphicalNodeEditPolicy {

    //@see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        TableEditPart part = (TableEditPart) request.getTargetEditPart();
        CommandAdapter cmd = (CommandAdapter)request.getStartCommand();
        ((AddForeignKeyOperation)cmd.getOperation()).setTargetTable(part.getTable());
        return cmd;
    }

    //@see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        TableEditPart part = (TableEditPart) getHost();
        Command cmd = new CommandAdapter(new AddForeignKeyOperation(part.getTable()));
        request.setStartCommand(cmd);
        return cmd;
    }

    //@see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
    protected Command getReconnectTargetCommand(ReconnectRequest request) {
        return null;
    }

    //@see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
    protected Command getReconnectSourceCommand(ReconnectRequest request) {
        return null;
    }

    /*
     * This method is overridden to set the connection router used during connection creation.
     * The implementation is copied from the superclass, except for the line marked as CHANGED.
     * Any suggestions to improve on this admittedly ugly procedure are very welcome
     */
    protected FeedbackHelper getFeedbackHelper(CreateConnectionRequest request) {
        if (feedbackHelper == null) {
            feedbackHelper = new FeedbackHelper();
            Point p = request.getLocation();
            connectionFeedback = createDummyConnection(request);
            connectionFeedback.setConnectionRouter(new ManhattanConnectionRouter()); //CHANGED
            connectionFeedback.setSourceAnchor(getSourceConnectionAnchor(request));
            feedbackHelper.setConnection(connectionFeedback);
            addFeedback(connectionFeedback);
            feedbackHelper.update(null, p);
        }
        return feedbackHelper;
    }
}
