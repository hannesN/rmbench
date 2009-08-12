/*
 * created 23.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $$Id: AbstractTableEditPart.java 389 2006-06-29 19:31:09Z hannesn $$
 */

package com.byterefinery.rmbench.editparts;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.byterefinery.rmbench.figures.MoveableAnchor;
import com.byterefinery.rmbench.model.diagram.AbstractDTable;

/**
 * superclass for table edit parts. [Christian] There is really no reason why it should be there, nost of what 
 * happens here is specific to TableEditPart
 * 
 * @author Hannes Niederhausen
 *
 */
public abstract class AbstractTableEditPart extends  AbstractGraphicalEditPart implements NodeEditPart {
    /** used for automatic layout computation */
    protected Node node;
    
    protected MoveableAnchor tmpSourceAnchor;
    protected MoveableAnchor tmpTargetAnchor;
    
    public AbstractTableEditPart() {
       
    }
    
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return ((ForeignKeyEditPart)connection).getSourceConnectionAnchor();
    }

    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return ((ForeignKeyEditPart)connection).getTargetConnectionAnchor();
    }

    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        if(request instanceof ReconnectRequest) {
            //this method is called also if this tablepart is really the target. We therefore
            //need to get the source part from the connection and use its figure
            ReconnectRequest rr = (ReconnectRequest)request;
            GraphicalEditPart sourcePart = (GraphicalEditPart)rr.getConnectionEditPart().getSource();
            return new XYAnchor2(((ReconnectRequest)request).getLocation(), sourcePart.getFigure());
        }
        else if(request instanceof CreateConnectionRequest) {
            tmpSourceAnchor = new MoveableAnchor(getFigure(), ((CreateConnectionRequest)request).getLocation()); 

            return tmpSourceAnchor;
        }
        return new MoveableAnchor(getFigure());
    }

    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        if(request instanceof ReconnectRequest) {
            //here we are lucky, we can use this parts figure as owner
            return new XYAnchor2(((ReconnectRequest)request).getLocation(), getFigure());
        }
        else if(request instanceof CreateConnectionRequest) {
            tmpTargetAnchor = new MoveableAnchor(getFigure(), ((CreateConnectionRequest)request).getLocation());

            return tmpTargetAnchor;
        }
        return new MoveableAnchor(getFigure());
    }

    /** 
     * 
     * @return the temporary Anchor created by the getSourceConnectionAnchor
     */
    public MoveableAnchor getTmpSourceAnchor() {
        return tmpSourceAnchor;
    }
    
    /** 
     * 
     * @return the temporary Anchor created by the getTargetConnectionAnchor
     */
    public MoveableAnchor getTmpTargetAnchor() {
        return tmpTargetAnchor;
    }

    /**
     * set the display location for an already displayed figure
     * 
     * @param location the new location
     */
    protected void updateLocation(Point location) {
        
        Rectangle bounds = getFigure().getBounds().getCopy();
        bounds.setLocation(location);
        
        DiagramEditPart parent = (DiagramEditPart) getParent();
        parent.setLayoutConstraint(this, figure, bounds);
    }

    // made public for optimization purposes
    public void refreshSourceConnections() {
        super.refreshSourceConnections();
    }
    
    // made public for optimization purposes
    public void refreshTargetConnections() {
        super.refreshTargetConnections();
    }
    
    public Point getLocation() {
        return getModelDTable().getLocation();
    }
    
    /** returns the dtable */
    protected abstract AbstractDTable getModelDTable();

    /**
     * update the display location from the stored state 
     * <em>This method must only be called before the figure is created, but after the
     * part has been added to its parent</em>
     */
    public void updateLocation() {
        AbstractDTable dTable = getModelDTable();
        if(dTable.getLocation() != null) {
            DiagramEditPart parent = (DiagramEditPart) getParent();
            Rectangle constraint = new Rectangle(dTable.getLocation().x, dTable.getLocation().y, -1, -1);
            parent.setLayoutConstraint(this, getFigure(), constraint);
        }
    }
    

    /**
     * create and initialize a new layout node. This method should be called at the start
     * of a layout process for each involved part. The edge is stored internally and 
     * returned by reference, so that later {@link #applyLayoutNode()} can be called on 
     * this object to apply the computed layout
     * 
     * @return the newly created node
     * @see #applyLayoutNode()
     */
    public Node createLayoutNode() {
        Dimension size = getFigure().getPreferredSize();
        node = new Node(this);
        node.width = size.width;
        node.height = size.height;
        return node;
    }

    /**
     * @return the previously created node
     * @see #createLayoutNode()
     * @see #applyLayoutNode()
     */
    public Node getLayoutNode() {
        if (node==null)
            createLayoutNode();
        return node;
    }

    /**
     * apply the computed layout by setting the location to the coordinates
     * stored in the layout node
     * 
     * @param size whether size should be applied, too
     * @see #createLayoutNode()
     * @see #getLayoutNode()
     */
    public void applyLayoutNode(boolean size) {
        if(size) {
            //dont use the node, because heights would be equalled
            IFigure figure = getFigure();
            figure.setSize(figure.getPreferredSize());
        }
        //DiagramPart will be notified through property event
        getModelDTable().setLocation(new Point(node.x, node.y));
    }
    
    /*
     * an XY anchor thath maintains the owner reference. Only used during reconnect 
     * operations
     */
    private static class XYAnchor2 extends XYAnchor {

        final IFigure owner;
        
        public XYAnchor2(Point p, IFigure owner) {
            super(p);
            this.owner = owner;
        }

        public IFigure getOwner() {
            return owner;
        }
    }
}
