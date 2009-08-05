/*
 * created 13.03.2005
 * 
 * $Id:ForeignKeyEditPart.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PartInitException;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.editpolicies.ForeignKeyEditPolicy;
import com.byterefinery.rmbench.editpolicies.ForeignKeyEndpointEditPolicy;
import com.byterefinery.rmbench.figures.ConnectionLabelLocator;
import com.byterefinery.rmbench.figures.Decorations;
import com.byterefinery.rmbench.figures.MoveableAnchor;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.operations.DeleteForeignKeyOperation;
import com.byterefinery.rmbench.preferences.PreferenceHandler;
import com.byterefinery.rmbench.util.Deleteable;
import com.byterefinery.rmbench.views.table.TableDetailsView;


/**
 * part that represents a foreign key relationship
 * 
 * @author cse
 */
public class ForeignKeyEditPart extends AbstractConnectionEditPart implements Deleteable {

    //used for layout computation
    private Edge edge;
    private Label connectionLabel;
    private Decorations decorations;
    private ConnectionLabelLocator labelLocator;
    
    private ConnectionAnchor sourceAnchor;
    private ConnectionAnchor targetAnchor;
        
    private DForeignKey dForeignKey;
    
    private EventManager.Listener eventListener = new EventManager.Listener() {
		public void eventOccurred(int eventType, Event event) {
			if(event.element == getForeignKey()) {
				if(event.info == COLUMN_NULLABLE || event.info == COLUMN_PK)
					recreateFigure();
			}
		}

		public void register() {
	        RMBenchPlugin.getEventManager().addListener(FOREIGNKEY_MODIFIED, this);
		}
    };
    
    private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if(event.getProperty().equals(PreferenceHandler.PREF_CONNECTION_LABELS)) {
                if(PreferenceHandler.getShowLabels()) {
                    PolylineConnection conn = (PolylineConnection)getFigure();
                    if(labelLocator == null)
                        labelLocator = new ConnectionLabelLocator(conn, connectionLabel);
                    conn.add(connectionLabel, labelLocator);
                }
                else {
                    getFigure().remove(connectionLabel);
                }
            }
            else if(event.getProperty().equals(PreferenceHandler.PREF_DECORATION_STYLE)) {
                decorations = PreferenceHandler.getDecorations();
				recreateFigure();
            }
        }
    };
    
    public ForeignKeyEditPart(DForeignKey foreignKey) {
        setModel(foreignKey);
        decorations = PreferenceHandler.getDecorations();
        this.dForeignKey = foreignKey;
    }
    
    public void activate() {
        super.activate();
        RMBenchPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
        eventListener.register();
    }

    public void deactivate() {
        RMBenchPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListener);
        eventListener.unregister();
        super.deactivate();
    }

    protected IFigure createFigure() {
		PolylineConnection conn = (PolylineConnection)super.createFigure();
        
        
        decorations.decorate(getForeignKey(), conn);
        conn.setConnectionRouter(new ManhattanConnectionRouter());
        connectionLabel = new Label();
        initializeLabel();
        
        
        if(PreferenceHandler.getShowLabels()) {
            labelLocator = new ConnectionLabelLocator(conn, connectionLabel);
            conn.add(connectionLabel, labelLocator);
        }
        else {
            conn.setToolTip(connectionLabel);
        }
        
		return conn;
    }

    /*
     * re-create the figure to apply a decoration change
     */
    private void recreateFigure() {
        deactivateFigure();
        unregisterVisuals();
        setFigure(createFigure());
        activateFigure();
        registerVisuals();
        
        refreshSourceAnchor();
        refreshTargetAnchor();
    }
    
    private void initializeLabel() {
        ForeignKey foreignKey = ((DForeignKey)getModel()).getForeignKey();
        StringBuffer buf = new StringBuffer();
        
        Column[] columns = foreignKey.getColumns();
        for (int i=0; true; i++) {
            buf.append(columns[i].getName());
            buf.append("=");
            buf.append(foreignKey.getTargetColumn(columns[i]).getName());
            if(i < columns.length-1)
                buf.append("\n");
            else
                break;
        }
        connectionLabel.setText(buf.toString());
    }

    //@see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ForeignKeyEndpointEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ForeignKeyEditPolicy());
    }

    /**
     * @return the foreign key that serves as the model for this part
     */
    public ForeignKey getForeignKey() {
        return ((DForeignKey)getModel()).getForeignKey();
    }
    
    /**
     * @return the DForeignKey that serves as the model for this part
     */
    public DForeignKey getDForeignKey() {
        return (DForeignKey)getModel();
    }
    
    public void performRequest(Request request){
        if (request.getType() == RequestConstants.REQ_OPEN) {
            try {
                RMBenchPlugin.getDefault().getWorkbench().
                    getActiveWorkbenchWindow().getActivePage().showView(TableDetailsView.VIEW_ID);
                //in case the view has just been opened - repeat selection event
                RMBenchPlugin.getEventManager().fireForeignKeySelected(this, getForeignKey());
            } catch (PartInitException e) {
                RMBenchPlugin.logError(e);
            }
        }
    }
    
    /**
     * @return a new edge for computing the layout for this object. The edge is stored 
     * internally and returned by reference, so that later {@link #applyLayoutEdge()} can
     * be called on this object to apply the computed layout 
     * @see #applyLayoutEdge()
     */
    public Edge createLayoutEdge() {
        AbstractTableEditPart source = (AbstractTableEditPart)getSource();
        AbstractTableEditPart target = (AbstractTableEditPart)getTarget();
        edge = new Edge(this, source.getLayoutNode(), target.getLayoutNode());
        return edge;
    }

    /**
     * apply the computed layout. CURRENTLY NOT USED
     * @see #createLayoutEdge()
     */
    public void applyLayoutEdge() {
        NodeList nodes = edge.vNodes;
        PolylineConnection conn = (PolylineConnection)getConnectionFigure();
        conn.setTargetDecoration(new PolygonDecoration());
        if (nodes != null) {
            List<AbsoluteBendpoint> bends = new ArrayList<AbsoluteBendpoint>();
            for (int i = 0; i < nodes.size(); i++) {
                Node vn = nodes.getNode(i);
                int x = vn.x;
                int y = vn.y;
                if (edge.isFeedback()) {
                    bends.add(new AbsoluteBendpoint(x, y + vn.height));
                    bends.add(new AbsoluteBendpoint(x, y));

                } else {
                    bends.add(new AbsoluteBendpoint(x, y));
                    bends.add(new AbsoluteBendpoint(x, y + vn.height));
                }
            }
            conn.setRoutingConstraint(bends);
        } else {
            conn.setRoutingConstraint(Collections.EMPTY_LIST);
        }
    }

    public IUndoableOperation getDeleteOperation() {
        return new DeleteForeignKeyOperation(((DForeignKey)getModel()).getForeignKey());
    }

    public IUndoableOperation getRemoveOperation() {
        return null;//new DeleteForeignKeyOperation((ForeignKey)getModel());
    }
    
    public ConnectionAnchor getSourceConnectionAnchor() {
        if (getSource() != null) {
            if (sourceAnchor == null) {
                AbstractTableEditPart source = (AbstractTableEditPart) getSource();
                    sourceAnchor = new MoveableAnchor(source.getFigure(), dForeignKey, true);
                recreateFigure();
            }
            return sourceAnchor;
        }
        return new XYAnchor(new Point(10, 10));
    }

    public ConnectionAnchor getTargetConnectionAnchor() {
        if (getTarget() != null) {
            if (targetAnchor == null) {
                AbstractTableEditPart target = (AbstractTableEditPart) getTarget();
                    targetAnchor = new MoveableAnchor(target.getFigure(), dForeignKey, false);
                recreateFigure();
            }
            return targetAnchor;
        }
        //this is an error, really! --> what meaning has this comment?
        return new XYAnchor(new Point(100, 100));
    }
    
    public void setTarget(EditPart editPart) {
        super.setTarget(editPart);
        if (editPart instanceof AbstractTableEditPart) {
            MoveableAnchor anchor = ((AbstractTableEditPart)editPart).getTmpTargetAnchor();
            if ( !dForeignKey.isTargetValid() && (anchor!=null)) {
                dForeignKey.setTargetEdge(anchor.getDForeignKeyEdge());
                dForeignKey.setTargetSlot(anchor.getDForeignKeySlot());
                dForeignKey.setTargetValid(true);
            } else {
                targetAnchor=null;
                getTargetConnectionAnchor();
            }
        }
    }
    
    public void setSource(EditPart editPart) {
        super.setSource(editPart);
        if (editPart instanceof AbstractTableEditPart) {
            MoveableAnchor anchor = ((AbstractTableEditPart)editPart).getTmpSourceAnchor();
            if ( !dForeignKey.isSourceValid() && (anchor!=null)) {
                dForeignKey.setSourceEdge(anchor.getDForeignKeyEdge());
                dForeignKey.setSourceSlot(anchor.getDForeignKeySlot());
                dForeignKey.setSourceValid(true);
            } else {
                sourceAnchor=null;
                getSourceConnectionAnchor();
            }
        }
    }     
}
