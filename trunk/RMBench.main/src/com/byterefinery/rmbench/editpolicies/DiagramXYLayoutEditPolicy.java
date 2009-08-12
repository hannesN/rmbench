/*
 * created 15.03.2005
 * 
 * $Id:DiagramXYLayoutEditPolicy.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.NonResizableHandleKit;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.editparts.TableStubEditPart;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.operations.CommandAdapter;
import com.byterefinery.rmbench.operations.MoveTableOperation;
import com.byterefinery.rmbench.util.RMBDragEditPartsTracker;


/**
 * layout policy that prevents selection of stub editparts, and installs a custom drag tracker
 * for handling move operations
 * 
 * @author cse
 */
public class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {

    protected Command createChangeConstraintCommand(
            ChangeBoundsRequest request, EditPart child, Object constraint) {
        
		Rectangle rect = (Rectangle)constraint;
        DTable dtable;
        if (child instanceof TableEditPart) {
            dtable = ((TableEditPart)child).getDTable();
        
            return new CommandAdapter(new MoveTableOperation(dtable, rect.getLocation()));
        }
        return null;
    }

    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        return null;
    }

    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }

    protected Command getCreateCommand(CreateRequest request) {
        return null;
    }

    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }

    protected EditPolicy createChildEditPolicy(EditPart child) {
        if(child instanceof TableStubEditPart)
            return null;
        
        return new NonResizableEditPolicy() {

            /*
             * unfortunately, we have to resort to this rather ugly means in order to divert compound
             * move commands to the new histoy implementation
             */
            protected List<?> createSelectionHandles() {
                List<AbstractHandle> list = new ArrayList<AbstractHandle>();
                GraphicalEditPart hostPart = (GraphicalEditPart)getHost();
                
                NonResizableHandleKit.addMoveHandle(hostPart, list);
                NonResizableHandleKit.addCornerHandles(hostPart, list);
                
                for (AbstractHandle handle : list) {
                    handle.setDragTracker(new RMBDragEditPartsTracker(hostPart));
                }
                return list;
            }
        };
    }
}
