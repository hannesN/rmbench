/*
 * created 13.03.2005
 * 
 * $Id:ColumnEditPart.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editpolicies.ColumnSelectionEditPolicy;
import com.byterefinery.rmbench.figures.ColumnFigure;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.preferences.PreferenceHandler;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.MarqueeSelectionTool2;

/**
 * edit part that represents a database table column
 * 
 * @author cse
 */
public class ColumnEditPart extends AbstractGraphicalEditPart {

    protected IFigure createFigure() {
        return new ColumnFigure();
    }

    public void refreshVisuals() {
        ColumnFigure figure = (ColumnFigure)getFigure();
        
        figure.setIcon(computeImage());
        figure.setColumnName(getColumn().getName());
        
        if(PreferenceHandler.getShowTypes()) {
	        figure.setColumnType(getColumn().getDataType().getDDLName());
            figure.setToolTip(null);
        }
        else {
	        figure.setColumnType("");
	        
            Label tooltip = new Label();
            tooltip.setText(getColumn().getName()+": "+getColumn().getDataType().getDDLName());
            figure.setToolTip(tooltip);
        }
    }

    public void performRequest(Request request){
        if (request.getType() == RequestConstants.REQ_OPEN) {
            RMBenchPlugin.getEventManager().fireColumnOpened(this, getColumn());
        }
    }
    
    public DragTracker getDragTracker(Request request) {
        return new ColumnPartDragTracker(this);
    }
    
    //@see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ColumnSelectionEditPolicy());
    }
    
    public boolean isSelectable() {
        return !MarqueeSelectionTool2.inProgress;
    }
    
    private Image computeImage() {
        Column column = (Column) getModel();
        
        ImageDescriptor descriptor = null;
        if(column.belongsToPrimaryKey()) {
            if(column.belongsToForeignKey()) {
                descriptor = RMBenchPlugin.getImageDescriptor(ImageConstants.COL_PKFK);
            }
            else {
                descriptor = RMBenchPlugin.getImageDescriptor(ImageConstants.KEY);
            }
        }
        else if(column.belongsToForeignKey()) {
            descriptor = RMBenchPlugin.getImageDescriptor(ImageConstants.COL_FK);
        }
        return descriptor != null ? descriptor.createImage() : null;
    }

    /**
     * @return the column model object
     */
    public Column getColumn() {
        return (Column)getModel();
    }

    /*
     * a drag tracker that delegeates the drag operation back to the enclosing table edit part
     */
    private static class ColumnPartDragTracker extends DragEditPartsTracker {

        public ColumnPartDragTracker(EditPart sourceEditPart) {
            super(sourceEditPart);
        }

        protected List<?> createOperationSet() {
            List<EditPart> list = new ArrayList<EditPart>(1);
            list.add(getSourceEditPart().getParent());
            return list;
        }

        protected boolean handleDragStarted() {
            getCurrentViewer().select(getSourceEditPart().getParent());
            setSourceEditPart(getSourceEditPart().getParent());
            return super.handleDragStarted();
        }
    }
}
