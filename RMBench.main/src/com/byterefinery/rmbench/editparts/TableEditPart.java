/*
 * created 12.03.2005
 * 
 * $Id: TableEditPart.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.editparts;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.editpolicies.TableComponentEditPolicy;
import com.byterefinery.rmbench.editpolicies.TableContainerEditPolicy;
import com.byterefinery.rmbench.editpolicies.TableNameDirectEditPolicy;
import com.byterefinery.rmbench.editpolicies.TableNodeEditPolicy;
import com.byterefinery.rmbench.extension.TableTypeExtension;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.figures.Decorations;
import com.byterefinery.rmbench.figures.TableBorder;
import com.byterefinery.rmbench.figures.TableFigure;
import com.byterefinery.rmbench.figures.TableFonts;
import com.byterefinery.rmbench.figures.TableTheme;
import com.byterefinery.rmbench.model.diagram.AbstractDTable;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.DeleteTableOperation;
import com.byterefinery.rmbench.operations.RemoveTableOperation;
import com.byterefinery.rmbench.preferences.PreferenceHandler;
import com.byterefinery.rmbench.util.Deleteable;
import com.byterefinery.rmbench.util.RMBDragEditPartsTracker;



/**
 * part that represents a database table
 * 
 * @author cse
 */
public class TableEditPart extends AbstractTableEditPart implements Deleteable, IExportable {
    
    /** wrapper for table model with display data */
    private final DTable dtable;
    
    private DirectEditManager directEditManager;
    
    private TableTheme theme;
    private TableFonts fonts;
    private TableBorder border;
    private Decorations decorations;
    
    private DiagramExport diagramExport = new DiagramExport() {

		public IFigure getExportFigure() {
            return getFigure();
		}

		public IFigure getExportDiagramFigure() {
            DiagramEditPart diagramPart = (DiagramEditPart)getParent();
            return diagramPart.getExportFigure();
		}
    };
    
    private EventManager.Listener foreignkeyListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            
        	            
            switch(eventType) {
                case FOREIGNKEY_ADDED: {
                    dtable.getDiagram().updateTables();
                    ForeignKey foreignKey = (ForeignKey)event.element;
                    if(foreignKey.getTable() == getTable()) {
                        redrawColumns();
                        refreshSourceConnections();
                    	updateDecorations();
                        refreshSizeConstraint();
                    }
                    if(foreignKey.getTargetTable() == getTable()) {
                        refreshTargetConnections();
                    }
                    break;
                }
                case FOREIGNKEY_DELETED: {
                    dtable.getDiagram().updateTables();
                    ForeignKey foreignKey = (ForeignKey)event.element;
                    if(foreignKey.getTable() == getTable()) {
                        redrawColumns();
                        refreshSourceConnections();
                    	updateDecorations();
                        refreshSizeConstraint();
                    }
                    if(foreignKey.getTargetTable() == getTable()) {
                        refreshTargetConnections();
                    }
                    break;
                }
                case FOREIGNKEY_MODIFIED: {
                	ForeignKey foreignKey = (ForeignKey)event.element;
                    if(foreignKey.getTable() == getTable()) {
                        redrawColumns();
                    	updateDecorations();
                        refreshSizeConstraint();
                    }
                	break;
                }
                case TABLE_MODIFIED: {
                	if ( (event.info.equals(EventManager.Properties.COLUMN_ORDER))
                			&& (event.element==dtable.getTable()) ) {
                		redrawColumns();
                	}
                }
            }
        }
        
        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    FOREIGNKEY_ADDED | FOREIGNKEY_DELETED | FOREIGNKEY_MODIFIED | TABLE_MODIFIED, this);
        }
    };
    private EventManager.Listener columnsListener = new EventManager.Listener() {

        @SuppressWarnings("unchecked")
		public void eventOccurred(int eventType, Event event) {
            if(event.owner != getModel())
                return;
            switch(eventType) {
                case COLUMN_ADDED: {
                    addColumn((Column)event.element);
                    break;
                }
                case COLUMN_DELETED: {
                    Column column = (Column)event.element;
                    for (Iterator<ColumnEditPart> it = getChildren().iterator(); it.hasNext();) {
                        ColumnEditPart columnPart = it.next();
                        if(columnPart.getColumn() == column) {
                            removeChild(columnPart);
                            break;
                        }
                    }
                    break;
                }
                case COLUMNS_MODIFIED:
                case COLUMN_MODIFIED: {
                    if(event.info == COLUMN_PK || event.info == COLUMN_NAME || event.info == COLUMN_PRECISION || event.info == COLUMN_SCALE) {
                        if(eventType == COLUMN_MODIFIED)
                            refreshColumn((Column)event.element);
                        else {
                            Column[] columns = (Column[])event.element;
                            for (int i = 0; i < columns.length; i++) {
                                refreshColumn(columns[i]);
                            }
                        }
                    }
                    break;
                }
            }
            if(!event.moreComing)
                refreshSizeConstraint();
        }

        @SuppressWarnings("unchecked")
		private void refreshColumn(Column column) {
            for (Iterator<ColumnEditPart> it = getChildren().iterator(); it.hasNext();) {
                ColumnEditPart columnPart = it.next();
                if(columnPart.getColumn() == column) {
                    columnPart.refresh();
                    break;
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    COLUMN_ADDED | COLUMN_DELETED | COLUMN_MODIFIED | COLUMNS_MODIFIED, this);
        }
    };
    private EventManager.Listener tableListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.element != dtable.getTable())
                return;
            if(event.info == EventManager.Properties.NAME)
                refreshTableName();
            else if(event.info == EventManager.Properties.TYPE) {
                theme = PreferenceHandler.getTableTheme(getTable().getType());
                ((TableFigure)getFigure()).setTheme(theme);
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(TABLE_MODIFIED, this);
        }
    };
    
    private PropertyChangeListener tablePropertyListener = new PropertyChangeListener() {

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if(evt.getPropertyName() == DTable.PROPERTY_COLLAPSED) {
                Boolean collapsed = (Boolean)evt.getNewValue();
                TableFigure figure = (TableFigure)getFigure();
                figure.setCollapsed(collapsed.booleanValue());
                refreshChildren();
                refreshSizeConstraint();
            }
            else if(evt.getPropertyName() == DTable.PROPERTY_LOCATION) {
                Point location = (Point)evt.getNewValue();
                updateLocation(location);
            }
        }
    };
    
    private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
        
        @SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent event) {
            
            if(event.getProperty().equals(PreferenceHandler.PREF_TABLE_SHOWTYPES)){
                for (Iterator<ColumnEditPart> it = getChildren().iterator(); it.hasNext();) {
                    ColumnEditPart columnPart = it.next();
                    columnPart.refreshVisuals();
                }
                refreshSizeConstraint();
            }
            if(event.getProperty().equals(PreferenceHandler.PREF_TABLE_SHADOW)){
                initializeBorder();
                ((TableFigure)getFigure()).setBorder(border);
                refreshSizeConstraint();
            }
            else if(event.getProperty().equals(PreferenceHandler.PREF_DECORATION_STYLE)){
            	Decorations oldDecorations = decorations;
            	decorations = PreferenceHandler.getDecorations();
            	if(oldDecorations.affectsBorder() || decorations.affectsBorder()) {
	                initializeBorder();
	                ((TableFigure)getFigure()).setBorder(border);
	                refreshSizeConstraint();
            	}
            }
            else if(PreferenceHandler.applyFontPreference(event.getProperty(), fonts)) {
                ((TableFigure)getFigure()).setFonts(fonts);
                refreshSizeConstraint();
            }
            else if(getTable().getType() == null) {
                if(PreferenceHandler.applyDefaultThemePreference(event.getProperty(), theme))
                    ((TableFigure)getFigure()).setTheme(theme);
            }
            else {
                TableTypeExtension extension = RMBenchPlugin.getExtensionManager().getTableTypeExtension(getTable().getType());
                if(PreferenceHandler.applyTypeThemePreference(
                        event.getProperty(), extension.themeExtension, theme)) {
                    ((TableFigure)getFigure()).setTheme(theme);
                }
            }
        }
    };
    
    public TableEditPart(DTable dTable) {
        this.dtable = dTable;
        this.theme = PreferenceHandler.getTableTheme(dtable.getTable().getType());
        this.fonts = PreferenceHandler.getTableFonts();
        this.decorations = PreferenceHandler.getDecorations();
        
        initializeBorder();
        PreferenceHandler.addPreferenceChangeListener(preferenceListener);
        setModel(dTable.getTable());
    }

    private void updateDecorations() {
    	if(decorations.affectsBorder()) {
    		initializeBorder();
        	getFigure().setBorder(border);
    	}
    }
    
    private void initializeBorder() {
        this.border = new TableBorder();
        this.border.setShadow(PreferenceHandler.getBorderShadow());
        decorations.decorate(dtable.getTable(), this.border);
    }

    @SuppressWarnings("unchecked")
	protected void registerModel() {
        super.registerModel();
        getViewer().getEditPartRegistry().put(dtable, this);    
    }

    protected void unregisterModel() {
        super.unregisterModel();
        getViewer().getEditPartRegistry().remove(dtable);    
    }

    protected IFigure createFigure() {
        
        TableFigure fig = new TableFigure(dtable, theme, fonts, border);
        
        if(dtable.getLocation() != null) {
            fig.getBounds().setLocation(dtable.getLocation());
        }
        return fig;
    }

    public void activate() {
        super.activate();
        this.dtable.addPropertyListener(tablePropertyListener);
        columnsListener.register();
        foreignkeyListener.register();
        tableListener.register();
    }

    public void deactivate() {
        super.deactivate();
        this.dtable.removePropertyListener(tablePropertyListener);
        PreferenceHandler.removePreferenceChangeListener(preferenceListener);
        columnsListener.unregister();
        foreignkeyListener.unregister();
        tableListener.unregister();
    }

    public void performRequest(Request request){
        if (request.getType() == RequestConstants.REQ_OPEN) {
            RMBenchPlugin.showTableDetailsView();
        }
        else if(request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            TableFigure figure = (TableFigure)getFigure();
            if(figure.isNameLabelHit(((DirectEditRequest) request).getLocation()))
                performNameDirectEdit();
        }
    }
	
    public IUndoableOperation getDeleteOperation() {
        return new DeleteTableOperation(
                ((DiagramEditPart)getParent()).getDiagram(), 
                getDTable());
    }

    public IUndoableOperation getRemoveOperation() {
        return new RemoveTableOperation(
                ((DiagramEditPart)getParent()).getDiagram(), 
                getDTable());
    }
    
    public DragTracker getDragTracker(Request request) {
        return new RMBDragEditPartsTracker(this);
    }

    private void performNameDirectEdit() {
        if (directEditManager == null) {
            TableFigure figure = (TableFigure) getFigure();
            directEditManager = new TableNameDirectEditManager(this, figure);
        }
        directEditManager.show();
    }

    public DTable getDTable() {
        return dtable;
    }
    
    public Table getTable() {
        return dtable.getTable();
    }

    public IFigure getContentPane() {
        TableFigure figure = (TableFigure) getFigure();
        return figure.getColumnsFigure();
    }

    protected List<?> getModelChildren() {
        return dtable.getColumns();
    }

    protected List<?> getModelSourceConnections() {
        List<Object> modelSources = new ArrayList<Object>(dtable.getForeignKeys().size()+1);
        //foreignkeys to tables contained in diagram
        modelSources.addAll(dtable.getForeignKeys());
        //add foreignkey to stub if availeble
        if (dtable.getTableStub().isValid())
            modelSources.add(dtable.getTableStub().getStubConnection());
        
        return modelSources;
    }

    protected List<?> getModelTargetConnections() {
        return dtable.getReferences();
    }

    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TableNodeEditPolicy());
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableComponentEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new TableContainerEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TableNameDirectEditPolicy());
    }
  
    /**
     * refresh the table name from the model
     */
    public void refreshTableName() {
        
        TableFigure tableFigure = (TableFigure) getFigure();
        tableFigure.getNameLabel().setText(getTable().getName());
    }
    
    /**
     * add a new edit part for the given column
     * @param column a column assumed to be part of the underlying table
     */
    private void addColumn(Column column) {
        if(!dtable.isCollapsed()) {
            EditPart columnPart = createChild(column);
            int index = getTable().getColumns().indexOf(column);
            addChild(columnPart, index);
        }
    }
    
    /**
     * refresh the size constraint for this parts figure after a model change
     */
    private void refreshSizeConstraint() {
        Rectangle bounds = getFigure().getBounds().getCopy();
        if(dtable.isCollapsed()) {
            //only adapt height if collapsed
            bounds.height = getFigure().getPreferredSize().height;
        } else {
            //adapt height AND width
            bounds.setSize(getFigure().getPreferredSize());
        }
        DiagramEditPart parent = (DiagramEditPart) getParent();
        parent.setLayoutConstraint(TableEditPart.this, getFigure(), bounds);
    }

    /*
     * Refreshes the child columns from the model and also redraws the figures.
     */
    @SuppressWarnings("unchecked")
	private void redrawColumns() {
        refreshChildren();
        
        List<ColumnEditPart> children = getChildren();
        Iterator<ColumnEditPart> iter = children.iterator();                
        while(iter.hasNext()){
            ColumnEditPart column = (ColumnEditPart)iter.next();
            column.refreshVisuals();
        }                
    }

    protected AbstractDTable getModelDTable() {
        return dtable;
    }

    public ModelExport getModelExport() {
        return null;
    }

    public DiagramExport getDiagramExport() {
        return diagramExport;
    }
}
