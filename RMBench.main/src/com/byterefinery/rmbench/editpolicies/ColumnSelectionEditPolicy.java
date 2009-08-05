/*
 * created 14.03.2005
 * 
 * $Id: ColumnSelectionEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;

/**
 * Handles column selection by changing the color
 * 
 * @author cse
 */
public class ColumnSelectionEditPolicy extends SelectionEditPolicy {

    //@see org.eclipse.gef.editpolicies.SelectionEditPolicy#showSelection()
    protected void showSelection() {
        getHostFigure().setBackgroundColor(ColorConstants.buttonDarker);
        getHostFigure().setForegroundColor(ColorConstants.white);
        getHostFigure().setOpaque(true);
        getHostFigure().repaint();
    }

    //@see org.eclipse.gef.editpolicies.SelectionEditPolicy#hideSelection()
    protected void hideSelection() {
        getHostFigure().setForegroundColor(ColorConstants.black);
        getHostFigure().setOpaque(false);
        getHostFigure().repaint();
    }

    public EditPart getTargetEditPart(Request request) {
        if (RequestConstants.REQ_SELECTION.equals(request.getType()))
            return getHost();
        return null;
    }
}
