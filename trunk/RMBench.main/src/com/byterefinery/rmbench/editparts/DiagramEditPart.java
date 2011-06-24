/*
 * created 13.03.2005
 * 
 * $Id:DiagramEditPart.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.ui.views.properties.IPropertySource;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.editpolicies.DiagramContainerEditPolicy;
import com.byterefinery.rmbench.editpolicies.DiagramXYLayoutEditPolicy;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.model.diagram.AbstractDTable;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.DTableStub;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.views.property.DiagramPropertySource;


/**
 * Edit part representing a schema diagram. A schema diagram can display any number 
 * of tables from a schema. Note, however, that a schema can be spread across multiple 
 * diagrams, and tables from one schema can appear in multiple diagrams.
 * 
 * @author cse
 */
public class DiagramEditPart extends AbstractGraphicalEditPart 
    implements IExportable, IExportable.DiagramExport {

    private class Listener extends EventManager.Listener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName() == Diagram.PROPERTY_TABLE) {
                if(evt.getOldValue() == null) {
                    DTable dtable = (DTable)evt.getNewValue();
                    if (dtable!=null) {
                        addChild(dtable);
                        getDiagram().updateTables();
                        refreshTablesStubs();
                        refreshReferences(dtable);
                        refreshForeignKeys(dtable);
                    }
                }
                else {
                    DTable dtable = (DTable)evt.getOldValue();
                    removeChild(dtable);
                    refreshReferences(dtable);
                    refreshForeignKeys(dtable);
                    refreshTablesStubs();
                }
            }
        }

        public void eventOccurred(int eventType, Event event) {
        	if(eventType == FOREIGNKEY_ADDED) {
        		//pick up stubs for foreign keys w/o table import
                ForeignKey foreignKey = (ForeignKey)event.element;
                if(getDiagram().getDTable(foreignKey.getTargetTable()) == null ||
                	getDiagram().getDTable(foreignKey.getTable()) == null) {
                	refreshTablesStubs();
                }
        	}
        	else if(eventType == FOREIGNKEY_DELETED) {
        		//undo op for former
                ForeignKey foreignKey = (ForeignKey)event.element;
                DTableStub stub = getDiagram().getTableStub(foreignKey);
                if(stub != null) {
                	stub.invalidate();
                	refreshTablesStubs();
                    getTableEditPart(stub.getDTable().getTable()).refreshSourceConnections();
                }
        	}
        	else if(event.owner == getDiagram())  {
        		//table added/deleted
                ForeignKey[] foreignKeys = (ForeignKey[])event.info;
                if(foreignKeys != null) {
                    Table table = (Table)event.element;
                    refreshConnectedParts(table, foreignKeys);
					refreshTablesStubs();                 
                }
            }
        }

        public void register() {
            getDiagram().addPropertyListener(this);
            RMBenchPlugin.getEventManager().addListener(
            		TABLE_ADDED | TABLE_DELETED | FOREIGNKEY_ADDED | FOREIGNKEY_DELETED, this);
        }

        public void unregister() {
            getDiagram().removePropertyListener(this);
            super.unregister();
        }
    }
    
    private LayoutManager layoutManager = new FreeformLayout();
    private Listener listener = new Listener();

    
    public void activate() {
        getDiagram().updateTables();
        super.activate();
        listener.register();
    }

	public void deactivate() {
        listener.unregister();
    }

    /**
     * @return the schema model object
     */
    public Diagram getDiagram() {
        return (Diagram)getModel();
    }
    
    protected List<?> getModelChildren() {
        getDiagram().updateTables();
        List<AbstractDTable> list = new ArrayList<AbstractDTable>();
        list.addAll(getDiagram().getDTables());
        list.addAll(getDiagram().getTableStubs());
        return list;
    }

    @SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
        if (adapter == SnapToHelper.class) {
            Boolean val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
            if (val != null && val.booleanValue())
                return new SnapToGrid(this);
        }
        else if (adapter == IPropertySource.class)
            return new DiagramPropertySource(getDiagram());
        
        return super.getAdapter(adapter);
    }

    //@see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    protected IFigure createFigure() {
        Figure figure = new FreeformLayer();
        figure.setLayoutManager(layoutManager );
        figure.setOpaque(false);
        return figure;
    }

    //@see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.NODE_ROLE, null);
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new DiagramContainerEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
        installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
    }

    /**
     * add a child edit part to represent the given table object
     * 
     * @param dtable the table model object
     * @return the edit part representing the child
     */
    public AbstractTableEditPart addChild(AbstractDTable dtable) {
        AbstractTableEditPart tablePart = (AbstractTableEditPart)createChild(dtable);
        
        int modelIndex = getModelChildren().indexOf(dtable);
        addChild(tablePart, modelIndex);
        
        return tablePart;
    }

    protected void addChild(EditPart childEditPart, int index) {
        super.addChild(childEditPart, index);
        if(childEditPart instanceof TableEditPart) {
            AbstractTableEditPart tablePart = (AbstractTableEditPart)childEditPart;
            tablePart.updateLocation();
        }
    }

    /**
     * remove the child edit part representing the given table object
     * 
     * @param dtable the table model object whose edit part is to be removed
     */
    public void removeChild(DTable dtable) {

        EditPart part = (EditPart)getViewer().getEditPartRegistry().get(dtable.getTable());
        if (part == null)
            throw new IllegalArgumentException("unknown table");
        removeChild(part);
    }

    protected TableEditPart getTableEditPart(Table table) {
		return (TableEditPart) getViewer().getEditPartRegistry().get(table);
	}

    private void refreshReferences(DTable dtable) {
        //refeshing table stubs
        EditPart otherpart = (EditPart) getViewer().getEditPartRegistry().get(
                getDiagram().getTableStub(dtable));
        if (otherpart != null)
            otherpart.refresh();

        for (Iterator<DForeignKey> it = dtable.getReferences().iterator(); it.hasNext();) {
            ForeignKey foreignKey = it.next().getForeignKey();
            otherpart = (EditPart) getViewer().getEditPartRegistry().get(foreignKey.getTable());
            if (otherpart!=null)
                otherpart.refresh();
        }
    }
    
    private void refreshForeignKeys(DTable dtable) {
        //refeshing table stubs
        EditPart otherpart = (EditPart) getViewer().getEditPartRegistry().get(
                getDiagram().getTableStub(dtable));
        if (otherpart!=null)
            otherpart.refresh();
        
        for (Iterator<DForeignKey> it = dtable.getForeignKeys().iterator(); it.hasNext();) {
            ForeignKey foreignKey = it.next().getForeignKey();
            otherpart = (EditPart) getViewer().getEditPartRegistry().get(
                    foreignKey.getTargetTable());
            if (otherpart!=null)
                otherpart.refresh();
        }
        
    }
    
    /**
     * refresh all parts representing tables that are connected with a given table through 
     * given foreign keys
     */
    private void refreshConnectedParts(Table table, ForeignKey[] foreignKeys) {
        for (int i = 0; i < foreignKeys.length; i++) {
            if(foreignKeys[i].getTable() == table)  {
                TableEditPart connectedPart = (TableEditPart) 
                    getViewer().getEditPartRegistry().get(foreignKeys[i].getTargetTable());
                if(connectedPart != null)
                    connectedPart.refresh();
            }
            else {
                AbstractTableEditPart connectedPart = (AbstractTableEditPart)   
                    getViewer().getEditPartRegistry().get(foreignKeys[i].getTable());
                if(connectedPart != null)
                    connectedPart.refresh();
            }
        }
    }

    public void ignoreDiagramEvents() {
        getDiagram().removePropertyListener(listener);
    }

    public void watchDiagramEvents() {
        getDiagram().addPropertyListener(listener);
    }

    public void refreshTablesStubs() {
        ArrayList<EditPart> trash = new ArrayList<EditPart>();
        Map<Object, EditPart> modelToEditPart = new HashMap<Object, EditPart>();
        
        //fill HashMap with EditParts for faster checks
        for (Iterator<?> it=getChildren().iterator(); it.hasNext();) {
        	EditPart tmpPart = (EditPart)it.next();
            if (tmpPart instanceof TableStubEditPart)
                modelToEditPart.put(tmpPart.getModel(), tmpPart);
        }
        
        //adding new edit part if needed
        for (Iterator<?> it=getModelChildren().iterator(); it.hasNext();) {
            AbstractDTable dTable = (AbstractDTable) it.next();
            if (dTable instanceof DTableStub) {
            	EditPart tmpPart = (EditPart) modelToEditPart.get(dTable);
                if (tmpPart == null)
                    addChild(dTable);
            }
        }
        //collecting trash StubEditparts
        for (Iterator<?> it=getChildren().iterator(); it.hasNext();) {
        	EditPart part = (EditPart) it.next();
            if (part instanceof TableStubEditPart) {
            	DTableStub tableStub = (DTableStub) part.getModel();
                if ((!tableStub.isValid())
                        || (getDiagram().getDTable(tableStub.getDTable().getTable()) == null)) {
                    trash.add(part);
                } else {
                    part.refresh();
                }
                
            }
        }
        for(EditPart part : trash) {
            removeChild(part);
        }
    }

    public ModelExport getModelExport() {
        return null;
    }

    public DiagramExport getDiagramExport() {
        return this;
    }

    /**
     * @return the combined printale layers for this diagram
     */
    public IFigure getExportFigure() {
        LayerManager layers = (LayerManager) getViewer().getEditPartRegistry().get(LayerManager.ID);
        return layers.getLayer(LayerConstants.PRINTABLE_LAYERS);
    }

    public IFigure getExportDiagramFigure() {
        return null;
    }
}
