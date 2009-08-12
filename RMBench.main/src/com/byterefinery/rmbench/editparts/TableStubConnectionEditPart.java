/*
 * created 29-Jan-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id$
 */
/**
 * 
 */
package com.byterefinery.rmbench.editparts;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import com.byterefinery.rmbench.figures.CornerAnchor;

/**
 * Connection between a TableEditPart and a TableStubEditPart
 * 
 * @author Hannes Niederhausen
 *
 */
public class TableStubConnectionEditPart extends AbstractConnectionEditPart {

   
    public TableStubConnectionEditPart() {
        super();
    }

    protected ConnectionAnchor getSourceConnectionAnchor() {
        if (getSource() != null) {
            return new CornerAnchor(((AbstractTableEditPart) getSource()).getFigure(), CornerAnchor.TOP_LEFT);
        } else {
            return new XYAnchor(new Point(100, 50));
        }
    }
    
    protected ConnectionAnchor getTargetConnectionAnchor() {
        if (getTarget() != null) {
            return new CornerAnchor(((AbstractTableEditPart) getTarget()).getFigure(), CornerAnchor.BOTTOM_RIGHT);
        } else {
            return new XYAnchor(new Point(10, 50));
        }
    }
        
    protected IFigure createFigure() {
        PolylineConnection conn=new PolylineConnection();
        conn.setConnectionRouter(ConnectionRouter.NULL);
        return conn;
     
    }    
    
    protected void createEditPolicies() {
    }
}
