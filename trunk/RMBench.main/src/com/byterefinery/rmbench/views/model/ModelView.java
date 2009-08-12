/*
 * created 26.05.2005
 * 
 * &copy; 2005, DynaBEAN Consulting
 * 
 * $Id: ModelView.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.views.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.dialogs.DDLExportWizard;
import com.byterefinery.rmbench.dialogs.FileResourceSelectionDialog;
import com.byterefinery.rmbench.dialogs.ModelPropertiesDialog;
import com.byterefinery.rmbench.dialogs.NewDiagramWizard;
import com.byterefinery.rmbench.dialogs.NewModelWizard;
import com.byterefinery.rmbench.dnd.ModelTransfer;
import com.byterefinery.rmbench.editors.DiagramEditor;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.export.DDLEditor;
import com.byterefinery.rmbench.export.DDLEditorInput;
import com.byterefinery.rmbench.export.ModelCompareEditor;
import com.byterefinery.rmbench.export.ModelCompareEditorInput;
import com.byterefinery.rmbench.extension.DDLGeneratorExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddDiagramOperation;
import com.byterefinery.rmbench.operations.AddSchemaOperation;
import com.byterefinery.rmbench.operations.DeleteDiagramOperation;
import com.byterefinery.rmbench.operations.DeleteSchemaOperation;
import com.byterefinery.rmbench.operations.DeleteTableOperation;
import com.byterefinery.rmbench.operations.DiagramDefaultSchemaOperation;
import com.byterefinery.rmbench.operations.DiagramNameOperation;
import com.byterefinery.rmbench.operations.ModifyModelPropertiesOperation;
import com.byterefinery.rmbench.operations.NewTableOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.operations.SchemaNameOperation;
import com.byterefinery.rmbench.operations.TableNameOperation;
import com.byterefinery.rmbench.operations.TableSchemaOperation;
import com.byterefinery.rmbench.operations.TablesNewDiagramOperation;
import com.byterefinery.rmbench.util.FileResourceModelStorage;
import com.byterefinery.rmbench.util.IModelStorage;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.ModelManager;
import com.byterefinery.rmbench.util.UpdateableAction;
import com.byterefinery.rmbench.views.property.RMBenchPropertySheetPage;
import com.byterefinery.rmbench.views.table.TableDetailsView;


/**
 * a view that shows the contents of the current model, i.e. schemas with nested 
 * schema elements, and diagrams
 * 
 * @author cse
 */
public class ModelView extends ViewPart implements ISaveablePart {

    public static final String VIEW_ID = "com.byterefinery.rmbench.views.ModelView"; //$NON-NLS-1$
    
    private static final String MODEL_MEMENTO_KEY = "rmbench.openmodel"; //$NON-NLS-1$
    
    /* listener for EventManager events */
    private EventManager.Listener modelListener = new EventManager.Listener()  {

        public void eventOccurred(int eventType, Event event) {
            switch(eventType) {
                case TABLE_ADDED: {
                    Table table = (Table)event.element;
                    TreeNode node = getTablesNode(table.getSchema());
                    node.addChild(table);
                    treeViewer.refresh(node);
                    break;
                }
                case TABLE_DELETED: {
                    Table table = (Table)event.element;
                    TreeNode node = getTablesNode(table.getSchema());
                    node.removeChild(table);
                    treeViewer.refresh(node);
                    break;
                }
                case TABLE_MODIFIED: {
                    if(event.info == NAME) {
                        Table table = (Table)event.element;
                        TreeNode node = getTableNode(table);
                        node.name = table.getName();
                        treeViewer.update(node, null);
                        // update table nodes in diagrams if exists
                        Iterator<TreeNode> diagramsIter = getDiagramsNode().children();
                        while(diagramsIter.hasNext()){
                            Iterator<TreeNode> dtablesIter = diagramsIter.next().children();
                            while(dtablesIter.hasNext()){
                                TreeNode tableNode = (TreeNode) dtablesIter.next();
                                if(((DTable) tableNode.element).getTable() == table){
                                    tableNode.name = table.getName();
                                    treeViewer.update(tableNode, null);
                                    break; // leave the table iteration, there is only one instance of table per diagram
                                }
                            }
                        }
 
                    }
                    else if(event.info == SCHEMA) {
                        Schema oldSchema = (Schema)event.owner;
                        Table table = (Table)event.element;
                        
                        TreeNode tablesNode = getTablesNode(oldSchema);
                        tablesNode.removeChild(table);
                        treeViewer.refresh(tablesNode);
                        
                        tablesNode = getTablesNode(table.getSchema());
                        tablesNode.addChild(table);
                        treeViewer.refresh(tablesNode);
                    }
                    break;
                }
                case SCHEMA_ADDED: {
                    Schema schema = (Schema)event.element;
                    TreeNode node = getSchemasNode();
                    TreeNode childNode = node.addChild(schema);
                    treeViewer.refresh(node);
                    if(event.origin == ModelView.this)
                        treeViewer.setSelection(new StructuredSelection(childNode), true);
                    break;
                }
                case SCHEMA_MODIFIED: {
                    TreeNode children[]=(TreeNode[]) getSchemasNode().getChildren();
                    
                    for (int i=0; i<children.length; i++) {
                        children[i].updateNodeName();
                    }
                    if(treeViewer !=null) {
                        treeViewer.refresh(input[0]);
                    }
                    break;
                }
                
                case SCHEMA_DELETED: {
                    Schema schema = (Schema)event.element;
                    TreeNode node = getSchemasNode();
                    node.removeChild(schema);
                    treeViewer.refresh(node);
                    break;
                }
                case DIAGRAM_ADDED: {
                    Diagram diagram = (Diagram)event.element;
                    diagram.addPropertyListener(diagramListener);
                    TreeNode node = getDiagramsNode();
                    TreeNode newNode = node.addChild(diagram);
                    //expanding only the level of diagram names
                    treeViewer.expandToLevel(node, 1);
                    treeViewer.refresh(node);
                    if(event.origin == ModelView.this)
                        treeViewer.setSelection(new StructuredSelection(newNode), true);
                    break;
                }
                
                case DIAGRAM_MODIFIED: {
                    if (event.info==EventManager.Properties.SCHEMA)
                        return;
                    TreeNode diagrams[]=(TreeNode[]) getDiagramsNode().getChildren();                    
                    
                    if(event.info.equals(EventManager.Properties.NAME)){
                        for (int i=0; i<diagrams.length; i++) {
                            diagrams[i].updateNodeName();
                        }
                        if(treeViewer !=null) {
                            treeViewer.refresh(getDiagramsNode());
                        }
                        break;
                    }
                    
                    break;
                }
                
                case DIAGRAM_DELETED: {
                    Diagram diagram = (Diagram)event.element;
                    diagram.removePropertyListener(diagramListener);
                    TreeNode node = getDiagramsNode();
                    node.removeChild(diagram);
                    treeViewer.refresh(node);
                    break;
                }
                case MODEL_PROPERTIES_CHANGED: {
                    String modelName = ((Model)event.element).getName();
                    if(RMBenchPlugin.isEclipse31())
                        setPartName(( getPartName().charAt(0) == '*' ) ? "*" + modelName : modelName); //$NON-NLS-1$
                    break;
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    TABLE_ADDED | TABLE_DELETED | TABLE_MODIFIED | SCHEMA_ADDED | SCHEMA_MODIFIED | SCHEMA_DELETED |
                    DIAGRAM_ADDED | DIAGRAM_MODIFIED | DIAGRAM_DELETED | MODEL_PROPERTIES_CHANGED,  this);
        }
    };
    
    /* listener for diagram property changes */
    private PropertyChangeListener diagramListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if(Diagram.PROPERTY_TABLE.equals(evt.getPropertyName())) {
                Diagram diagram = (Diagram)evt.getSource();
                TreeNode node = getDiagramsNode().getChild(diagram);
                node.setChildren(diagram.getDTables());
                treeViewer.refresh(node);
            }
        }
    };
    
    /* listener for model storage events */
    private ModelManager.Listener modelStorageListener = new ModelManager.Listener() {
        
        public void modelAboutToBeReplaced(Model model) {
            for (Iterator<Diagram> it = model.getDiagrams().iterator(); it.hasNext();) {
                Diagram diagram = (Diagram) it.next();
                diagram.removePropertyListener(diagramListener);
            }
        }

        public void modelReplaced(Model model) {
        	if(model != null) {
	            for (Iterator<Diagram> it = model.getDiagrams().iterator(); it.hasNext();) {
	                Diagram diagram = (Diagram) it.next();
	                diagram.addPropertyListener(diagramListener);
	            }
	            setPartName(model.getName());
        	}
        	else {
        		setPartName(getSite().getRegisteredName());
        	}
            updateActions();
            updateInput(true);
        }

        public void dirtyStateChanged(Model model, boolean isDirty) {
            if(RMBenchPlugin.isEclipse31())
                setPartName(isDirty ? "*"+model.getName() : model.getName()); //$NON-NLS-1$
            firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
        }
    };
    
    private Comparator<Schema> schemaComparator = new Comparator<Schema>() {
        public int compare(Schema o1, Schema o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    
    private Comparator<Diagram> diagramComparator = new Comparator<Diagram>() {
        public int compare(Diagram o1, Diagram o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    
    TreeNode[] input = new TreeNode[] {
            new TreeNode(RMBenchMessages.ModelView_SCHEMA_GROUP),
            new TreeNode(RMBenchMessages.ModelView_DIAGRAM_GROUP)
    };
    TreeNode[] emptyModelNode = new TreeNode[] {
            new TreeNode(RMBenchMessages.ModelView_EMPTY_MODEL_INFO)
    };
    
    private TreeViewer treeViewer;
    private DragSource dragSource;
    private DropTarget dropTarget;
    private transient Object[] localDNDData;
    
    private TreeNode selectedNode;
   
    
    private CopyAction copyAction;
    private DeleteAction deleteAction;
    private ExportAction exportAction;
    private RenameAction renameAction;
    
    private UndoRedoActionGroup undoRedoGroup;
    private OpenDiagramAction openDiagramAction;
    private ModelPropertiesAction propertiesAction;
    private CloseModelAction closeModelAction;
    
    /** falg if the selected treenode is editable or not*/
    private boolean nodeEditable;
    
    public void createPartControl(Composite parent) {
        
        treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.setContentProvider(new CustomContentProvider());
        treeViewer.setLabelProvider(new CustomLabelProvider());
        treeViewer.setCellEditors(new CellEditor[]{new TextCellEditor(treeViewer.getTree())});
        treeViewer.setColumnProperties(new String[]{"Name"}); //$NON-NLS-1$
        treeViewer.getTree().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                if (e.keyCode==SWT.F2) {
                    TreeNode selection = (TreeNode) ((IStructuredSelection) treeViewer
                            .getSelection()).getFirstElement();
                    if ((selection.element instanceof Diagram)||(selection.element instanceof Schema)
                            || (selection.element instanceof Table))
                        nodeEditable=true;
                        treeViewer.editElement(selection, 0);
                        nodeEditable=false;
                }
            }
            public void keyReleased(KeyEvent e) {               
            }
            
        });

        treeViewer.setCellModifier(new ICellModifier() {
            public boolean canModify(Object element, String property) {
                return nodeEditable;
            }

            public Object getValue(Object element, String property) {
                if (((TreeNode) element).element instanceof Diagram) {
                    Diagram diagram = (Diagram) ((TreeNode) element).element;
                    return diagram.getName();
                }
                if (((TreeNode) element).element instanceof Schema) {
                    Schema schema =  (Schema) ((TreeNode) element).element;
                    return schema.getName();
                }
                if (((TreeNode) element).element instanceof Table) {
                    Table table =  (Table) ((TreeNode) element).element;
                    return table.getName();
                }
                
                return null;
            }

            public void modify(Object element, String property, Object value) {
                Object modelElement = ((TreeNode) ((TreeItem)element).getData()).element;
                if (modelElement instanceof Diagram) {
                    Diagram diagram = (Diagram) modelElement;
                    if (diagram.getName().equals(value))
                        return;
                    DiagramNameOperation op = new DiagramNameOperation(diagram, (String) value);
                    op.execute(this);
                    return;
                }
                if (modelElement instanceof Schema) {
                    Schema schema =  (Schema) modelElement;
                    if (value.equals(schema.getName()))
                        return;
                    
                    SchemaNameValidator validator = new SchemaNameValidator();
                    if (validator.isValid((String) value)==null) {
                        SchemaNameOperation op = new SchemaNameOperation(schema, (String) value);
                        op.execute(this);
                    }
                    return;
                }
                if (modelElement instanceof Table) {
                    Table table =  (Table) modelElement;
                    if (value.equals(table.getName()))
                        return;
                    
                    
                    TableNameOperation op = new TableNameOperation(table, (String) value);
                    op.execute(this);
                    
                    
                }
            }
            
        });
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection(); 
                TreeNode node = (TreeNode)selection.getFirstElement();
                
                if(node != null && node.element instanceof Table) {
                    RMBenchPlugin.getEventManager().fireTableSelected(ModelView.this, (Table)node.element);
                } else if(node != null && node.element instanceof DTable) {
                    RMBenchPlugin.getEventManager().fireTableSelected(ModelView.this, ((DTable) node.element).getTable());
                } 
                selectedNode = node;
                updateSelectionActions();
            }
        });
        treeViewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
                TreeNode node = (TreeNode) event.getElement();
                node.setExpanded(false);
            }

            public void treeExpanded(TreeExpansionEvent event) {
                TreeNode node = (TreeNode) event.getElement();
                node.setExpanded(true);
            }
        });
        
        
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                
                if ((selectedNode.element == RMBenchMessages.ModelView_DIAGRAM_GROUP)
                        || (selectedNode.element == RMBenchMessages.ModelView_SCHEMA_GROUP)
                        || (selectedNode.element instanceof Schema)
                        || (selectedNode.element == RMBenchMessages.ModelView_TABLES_GROUP)
                        || (selectedNode.element == RMBenchMessages.ModelView_VIEWS_GROUP)
                        || (selectedNode.element == RMBenchMessages.ModelView_SEQUENCES_GROUP)) {
                    if (selectedNode.isExpanded()) {
                        treeViewer.collapseToLevel(selectedNode, 1);
                        //manual collapse doesn't fire event for TreeViewerListener
                        selectedNode.setExpanded(false);
                    }
                    else {
                        treeViewer.expandToLevel(selectedNode, 1);
                        // manual expand doesn't fire event for TreeViewerListener
                        selectedNode.setExpanded(true);
                    }
                }
                else if(selectedNode.element instanceof Diagram) {
                    openDiagramAction.run();
                }
                else if ((selectedNode.element instanceof Table)
                        || (selectedNode.element instanceof DTable) ){
                    RMBenchPlugin.showTableDetailsView();
                }
            }
        });
        dragSource = new DragSource(
                treeViewer.getControl(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        dragSource.setTransfer(new Transfer[] {ModelTransfer.getInstance()});

        dragSource.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                event.doit = selectedNode.isSchemaElement();
                localDNDData = getSelectedSchemaElements();
            }
            public void dragSetData(DragSourceEvent event) {
                if (ModelTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = localDNDData;
                }
            }
            public void dragFinished(DragSourceEvent event) {
            }
        });
        
        dropTarget = new DropTarget(treeViewer.getControl(), DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] {ModelTransfer.getInstance()});
        dropTarget.addDropListener(new ViewerDropAdapter(treeViewer) {

            public boolean performDrop(Object data) {
                for (int i = 0; i < localDNDData.length; i++) {
                    TableSchemaOperation operation = new TableSchemaOperation((Table)localDNDData[i]);
                    operation.execute(this, ((TreeNode)getCurrentTarget()).getSchema());
                }
                return true;
            }

            public boolean validateDrop(Object target, int operation, TransferData transferType) {
                if(target instanceof TreeNode) {
                    if(((TreeNode)target).isDropTablesNode()) {
                        for (int i = 0; i < localDNDData.length; i++) {
                            if(!(localDNDData[i] instanceof Table))
                                return false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        
        createActions();
        createContextMenu();
        getSite().setSelectionProvider(treeViewer);
        
        updateInput(false);
        
        if(RMBenchPlugin.getModelManager().isDirty()) {
            modelStorageListener.dirtyStateChanged(getModel(), true);
        }
        
        modelListener.register();
    }

    /**
     * @return the currently (directly or implicitly) selected schema elements
     */
    protected Object[] getSelectedSchemaElements() {
        IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        List<?> result = new ArrayList<Object>();
        for (Iterator<?> it = selection.iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            node.addSchemaElements(result);
        }
        return result.toArray();
    }

    private void createContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                
                if (getModel() != null) {
                    manager.add(new NewSchemaAction());
                    manager.add(new NewDiagramAction());
                    manager.add(new NewTableAction());
                    
                    manager.add(new Separator());
                    manager.add(new FindTableDiagramAction());
                    manager.add(new TableNewDiagramAction());
                    if(openDiagramAction.isEnabled()) {
                        manager.add(openDiagramAction);
                        MenuManager subMenu = new MenuManager(RMBenchMessages.ModelView_SetTargetSchema);
                        manager.add(subMenu);
                        
                        Diagram diagram = (Diagram)selectedNode.element;
                        for(Iterator<Schema> it=getModel().getSchemas().iterator(); it.hasNext(); ) {
                            
                            Schema schema = it.next();
                            IAction action = new SetTargetSchemaAction(schema);
                            action.setChecked(diagram.getDefaultSchema() == schema);
                            subMenu.add(action);
                        }
                    }
                    manager.add(new Separator());
                    manager.add(copyAction);
                    manager.add(deleteAction);
                    manager.add(renameAction);
                    manager.add(new Separator());
                    manager.add(propertiesAction);
                    manager.add(new Separator());
                    manager.add(new CloseModelAction());
                } else {
                    manager.add(new NewModelAction());
                    manager.add(new OpenModelAction());
                }
                
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
        
        getSite().registerContextMenu(menuMgr, treeViewer);
    }

    public void dispose() {
        undoRedoGroup.dispose();
        dragSource.dispose();
        
        super.dispose();
        
        RMBenchPlugin.getModelManager().removeListener(modelStorageListener);
        modelListener.unregister();
    }

    private Model getModel() {
        return RMBenchPlugin.getModelManager().getModel();
    }
    
    private void updateSelectionActions() {
        openDiagramAction.update();
        deleteAction.update();
        copyAction.update();
        renameAction.update();
    }
    
    private void createActions() {
        
        copyAction = new CopyAction();
        deleteAction = new DeleteAction();
        openDiagramAction = new OpenDiagramAction();
        propertiesAction = new ModelPropertiesAction();
        exportAction = new ExportAction();
        renameAction = new RenameAction();
        closeModelAction = new CloseModelAction();
        
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager manager = actionBars.getToolBarManager();
    
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
        actionBars.setGlobalActionHandler(deleteAction.getId(), deleteAction);
        
        manager.add(propertiesAction);
        manager.add(new Separator());
        manager.add(new NewModelAction());
        manager.add(new OpenModelAction());
        manager.add(closeModelAction);
        manager.add(new Separator());
        manager.add(exportAction);

        undoRedoGroup = new UndoRedoActionGroup(this.getSite(), RMBenchOperation.CONTEXT, false);
        undoRedoGroup.fillActionBars(getViewSite().getActionBars());
        
        propertiesAction.setEnabled(RMBenchPlugin.getModelManager().getModel()!=null);
        exportAction.setEnabled(RMBenchPlugin.getModelManager().getModel()!=null);
        closeModelAction.setEnabled(RMBenchPlugin.getModelManager().getModel()!=null);
        
        actionBars.updateActionBars();
    }
    
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        
        super.init(site, memento);
        if(memento != null) {
            String modelKey = memento.getString(MODEL_MEMENTO_KEY);
            if(modelKey != null) {
                RMBenchPlugin.getModelManager().activateModelStorage(modelKey, null);
            }
        }
        if(getModel() != null)
            setPartName(getModel().getName());

        RMBenchPlugin.getModelManager().addListener(modelStorageListener);
    }

    public void saveState(IMemento memento) {
        if(!RMBenchPlugin.getModelManager().isNewStorage())
            memento.putString(MODEL_MEMENTO_KEY, RMBenchPlugin.getModelManager().getModelStorageKey());
    }

    public void setFocus() {
    }

    private void updateInput(boolean updateTreeViewer) {
        
        if (getModel()!=null) {

            Schema[] schemas = getModel().getSchemas().toArray(new Schema[getModel().getSchemas().size()]);
            Diagram[] diagrams = getModel().getDiagrams().toArray(new Diagram[getModel().getDiagrams().size()]);
            
            Arrays.sort(schemas, schemaComparator);
            Arrays.sort(diagrams, diagramComparator);
            
            input[0].setChildren(schemas);
            input[1].setChildren(diagrams);
            
            if (treeViewer!=null)
                treeViewer.setInput(input);
            
            if(updateTreeViewer && treeViewer != null) {
                treeViewer.refresh(input[0]);
                treeViewer.refresh(input[1]);
            }
        } else {
            treeViewer.setInput(emptyModelNode);
            treeViewer.refresh();
        }

    }

    private void updateActions() {
        boolean enabled = RMBenchPlugin.getModelManager().getModel() != null;
        
        propertiesAction.setEnabled(enabled);
        exportAction.setEnabled(enabled);
        closeModelAction.setEnabled(enabled);
        
        getViewSite().getActionBars().updateActionBars();
    }
    
    private TreeNode getSchemasNode() {
        return input[0];
    }
    
    private TreeNode getDiagramsNode() {
        return input[1];
    }
    
    private TreeNode getTablesNode(Schema schema) {
        return getSchemasNode().getChild(schema).getChild(RMBenchMessages.ModelView_TABLES_GROUP);
    }
    
    private TreeNode getTableNode(Table table) {
        return getTablesNode(table.getSchema()).getChild(table);
    }
    
    public void doSave(IProgressMonitor progressMonitor) {
        RMBenchPlugin.getModelManager().doSave(getSite().getShell(), progressMonitor);
    }

    public void doSaveAs() {
        IProgressMonitor progressMonitor =
            getViewSite().getActionBars().getStatusLineManager().getProgressMonitor();
        
        RMBenchPlugin.getModelManager().doSaveAs(getSite().getShell(), progressMonitor);
    }
    
    public boolean isDirty() {
        return RMBenchPlugin.getModelManager().isDirty();
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    public boolean isSaveOnCloseNeeded() {
        return isDirty();
    }
    
    @SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
        if (adapter == org.eclipse.ui.views.properties.IPropertySheetPage.class) {
            return RMBenchPropertySheetPage.getInstance(); 
        }
        return super.getAdapter(adapter);
    }

    /**
     * open an already stored diagram in an editor
     * 
     * @param diagram the diagram to open
     * @return the associated edit part
     */
    public DiagramEditPart openDiagram(Diagram diagram) throws PartInitException {
        IEditorInput editorInput = new DiagramEditor.Input(diagram);
        DiagramEditor editor = 
            (DiagramEditor)getSite().getPage().openEditor(editorInput, DiagramEditor.ID);
        return editor.getDiagramPart();
    }
    
    /**
     * open a new diagram in an editor
     * 
     * @param diagram the diagram to open
     * @return the associated edit part
     */
    public DiagramEditPart openNewDiagram(Diagram diagram) throws PartInitException {
        IEditorInput editorInput = new DiagramEditor.Input(diagram, true);
        DiagramEditor editor = 
            (DiagramEditor)getSite().getPage().openEditor(editorInput, DiagramEditor.ID);
        return editor.getDiagramPart();
    }
    
    private class CustomLabelProvider implements ILabelProvider {

        public Image getImage(Object element) {
            return ((TreeNode)element).image;
        }
        public String getText(Object element) {
            return ((TreeNode)element).name;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }
    
    private class CustomContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            return ((TreeNode)parentElement).getChildren();
        }
        public Object getParent(Object element) {
            return ((TreeNode)element).parent;
        }
        public boolean hasChildren(Object element) {
            return ((TreeNode)element).hasChildren();
        }
        public Object[] getElements(Object inputElement) {
            return (TreeNode[])inputElement;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private class NewModelAction extends Action {
        
        NewModelAction() {
            super();
            setText(RMBenchMessages.ModelView_New_Text);
            setToolTipText(RMBenchMessages.ModelView_New_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.NEWMODEL));
        }
        
        public void run() {
            if(!RMBenchPlugin.getModelManager().canSwitchModel(getSite().getShell()))
                return;
            //creating wizard
            NewModelWizard wizard = new NewModelWizard();
            wizard.init(RMBenchPlugin.getDefault().getWorkbench(), new StructuredSelection());
            
            //creating dialog, which shows the wizard
            WizardDialog dialog = new WizardDialog(getViewSite().getShell(), wizard);
            dialog.open();
        }
    }
    
    private class CloseModelAction extends Action {
        
        CloseModelAction() {
            super();
            setText("Close");
            setToolTipText("Close current model");
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.CLOSE_FOLDER));
        }
        
        public void run() {
            if(!RMBenchPlugin.getModelManager().canSwitchModel(getSite().getShell()))
                return;
            RMBenchPlugin.getModelManager().closeModel();
        }
    }
    
    private class OpenModelAction extends Action {
        
        OpenModelAction() {
            super();
            setText(RMBenchMessages.ModelView_Open_Text);
            setToolTipText(RMBenchMessages.ModelView_Open_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.OPEN));
        }
        
        public void run() {
            if(!RMBenchPlugin.getModelManager().canSwitchModel(getSite().getShell()))
                return;

            FileResourceSelectionDialog resourceDialog = 
                new FileResourceSelectionDialog(
                        getSite().getShell(), 
                        RMBenchMessages.OpenModelDialog_title, 
                        RMBenchMessages.OpenModelDialog_message);
            resourceDialog.open();
            
            Object[] result = resourceDialog.getResult();
            if(result != null) {
                
                IFile file = (IFile)result[0];
                FileResourceModelStorage storage = new FileResourceModelStorage(file);
                try {
                    IModelStorage.DefaultLoadListener loadListener = new IModelStorage.DefaultLoadListener();
                    storage.load(loadListener);
                    RMBenchPlugin.getModelManager().setModelStorage(storage, loadListener.dirty);
                }
                catch (SystemException e) {
                    RMBenchPlugin.logError(e);
                    MessageDialog.openError(getSite().getShell(), null, e.getMessage());
                }
            }
            updateActions();
        }
    }
    
    private class NewDiagramAction extends UpdateableAction {
        
        NewDiagramAction() {
            super();
            setText(RMBenchMessages.ModelView_NewDiagram_Text);
            setToolTipText(RMBenchMessages.ModelView_NewDiagram_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DIAGRAM));
        }
        
        public void run() {
            if(RMBenchPlugin.getLicenseManager().checkMaxDiagrams(getModel().getDiagrams().size()))
                return;
            
            //create the wizard
            NewDiagramWizard wizard = new NewDiagramWizard(getModel());
            IWorkbench workbench = RMBenchPlugin.getDefault().getWorkbench();
                        
            //now create a wizard dialog which contains our wizard
            WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
            dialog.setPageSize(400, 300);
            dialog.setBlockOnOpen(true);
            if (dialog.open() == Window.OK) {
                String diagramName = wizard.getDiagramName();
                Schema tmpSchema = getModel().getSchema(wizard.getSchemaName());
                AddDiagramOperation operation = new AddDiagramOperation(
                        getModel(), diagramName, tmpSchema);
                operation.execute(ModelView.this);
                
                try {
                    openNewDiagram((Diagram)selectedNode.element);
                }
                catch (PartInitException e) {
                    RMBenchPlugin.logError(e);
                    ErrorDialog.openError(
                            getSite().getShell(),
                            null, null,
                            SystemException.getStatus(e, ExceptionMessages.errorOpeningEditor));
                }
            }
            updateActions();
        }
    }

    private class FindTableDiagramAction extends UpdateableAction {
    	
		FindTableDiagramAction() {
            super();
            setText(RMBenchMessages.ModelView_FindTableDiagram_Text);
            setToolTipText(RMBenchMessages.ModelView_FindTableDiagram_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.TABLE_DETAILS));
    	}
    	
        public boolean isEnabled() {
            return selectedNode != null && selectedNode.element instanceof Table;
        }
        
    	public void run() {
    		
    		Table table = (Table)selectedNode.element;
    		
    		List<Diagram> diagrams = new ArrayList<Diagram>(getModel().getDiagrams().size());
    		for (Diagram diagram : getModel().getDiagrams()) {
				if(diagram.containsTable(table)) {
					diagrams.add(diagram);
				}
			}
    		if(diagrams.isEmpty()) {
    			MessageDialog.openInformation(
    					getSite().getShell(), null, RMBenchMessages.ModelView_FindTableDiagram_none);
    		}
    		else {
    			ListDialog listDialog = new ListDialog(getSite().getShell());
    			listDialog.setTitle(RMBenchMessages.ModelView_FindTableDiagram_diagrams);
    			listDialog.setMessage(RMBenchMessages.ModelView_FindTableDiagram_chooseDiagram);
    			listDialog.setContentProvider(new ArrayContentProvider());
    			listDialog.setInput(diagrams);
    			listDialog.setBlockOnOpen(true);
    			listDialog.setLabelProvider(new LabelProvider() {

					public String getText(Object element) {
						return ((Diagram)element).getName();
					}
    			});
    			if(listDialog.open() == Window.OK) {
    				Diagram diagram = (Diagram)listDialog.getResult()[0];
    	            try {
    	                DiagramEditPart diagramPart = openDiagram(diagram);
    	                diagramPart.getViewer().setSelection(
    	                		new StructuredSelection(diagramPart.getViewer().getEditPartRegistry().get(table)));
    	            }
    	            catch (Exception e) {
    	                RMBenchPlugin.logError(e);
    	            }
    			}
    		}
    	}
    }
    
    private class TableNewDiagramAction extends UpdateableAction {
    	
		TableNewDiagramAction() {
            super();
            setText(RMBenchMessages.ModelView_TableNewDiagram_Text);
            setToolTipText(RMBenchMessages.ModelView_TableNewDiagram_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DIAGRAM));
    	}
    	
        public boolean isEnabled() {
            return selectedNode != null && selectedNode.element instanceof Table;
        }
        
        public void run() {
        	TablesNewDiagramOperation operation = 
        		new TablesNewDiagramOperation(getModel(), (Table)selectedNode.element);
        	operation.execute(this);
        }
    }
    
    private class OpenDiagramAction extends UpdateableAction {
        
        OpenDiagramAction() {
            super();
            setText(RMBenchMessages.ModelView_OpenDiagram_Text);
            setToolTipText(RMBenchMessages.ModelView_OpenDiagram_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.OPEN));
        }
        
        public boolean isEnabled() {
            return selectedNode != null && selectedNode.element instanceof Diagram;
        }

        public void run() {
            try {
                openDiagram((Diagram)selectedNode.element);
            }
            catch (Exception e) {
                RMBenchPlugin.logError(e);
                ErrorDialog.openError(
                        getSite().getShell(),
                        null, null,
                        SystemException.getStatus(e, ExceptionMessages.errorOpeningEditor));
            }
        }
    }

    private class NewTableAction extends UpdateableAction {
        
        NewTableAction() {
            super();
            setText(RMBenchMessages.ModelView_NewTable_Text);
            setToolTipText(RMBenchMessages.ModelView_NewTable_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DIAGRAM));
        }
        
        public boolean isEnabled() {
            return 
                selectedNode != null && 
                (selectedNode.element instanceof Schema || 
                        selectedNode.element == RMBenchMessages.ModelView_TABLES_GROUP);
        }
        
        public void run() {
            if(RMBenchPlugin.getLicenseManager().checkMaxTables(getModel().getTableCount()))
                return;
            
            Schema schema = 
                selectedNode.element instanceof Schema ? 
                        (Schema)selectedNode.element : (Schema)selectedNode.parent.element;
            NewTableOperation operation = new NewTableOperation(getModel(), schema);
            operation.execute(this);
            
            TableDetailsView detailsView = RMBenchPlugin.showTableDetailsView();
            detailsView.showFirstPage();
            RMBenchPlugin.getEventManager().fireTableSelected(this, operation.getTable());
        }
    }
    
    private class ModelPropertiesAction extends Action {
        
        ModelPropertiesAction() {
            super();
            setText(RMBenchMessages.ModelView_Properties_Text);
            setToolTipText(RMBenchMessages.ModelView_Properties_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.PROPERTIES));
            setEnabled(true);
        }
        
        public void run() {
            ModelPropertiesDialog dialog = new ModelPropertiesDialog(
                    getSite().getShell(), 
                    getModel(),
                    RMBenchPlugin.getModelManager().getModelStorageKey());
            
            if(dialog.open() == Window.OK) {
                RMBenchOperation operation = new ModifyModelPropertiesOperation(
                        getModel(), 
                        dialog.getModelName(),
                        dialog.getNameGenerator(),
                        dialog.getDatabaseInfo()
                        );
                operation.execute(this);
                RMBenchPlugin.getModelManager().setDirty(true);
            }
        }
    }
    
    private class DeleteAction extends UpdateableAction {
        
        DeleteAction() {
            super();
            setId(ActionFactory.DELETE.getId());
            setText(RMBenchMessages.ModelView_Delete_Text);
            setToolTipText(RMBenchMessages.ModelView_Delete_Description);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setEnabled(false);
        }
        
        public boolean isEnabled() {
            return selectedNode != null && 
                (selectedNode.element instanceof Schema || selectedNode.element instanceof Diagram ||
                 selectedNode.element instanceof Table);
        }

        public void run() {
            if(selectedNode.element instanceof Diagram) {
                Diagram diagram = (Diagram)selectedNode.element;
                DeleteDiagramOperation operation = new DeleteDiagramOperation(getModel(), diagram);
                operation.execute(ModelView.this);
                treeViewer.refresh(getDiagramsNode());
            }
            else if (selectedNode.element instanceof Schema) {
                Schema schema = (Schema)selectedNode.element;
                
                DeleteSchemaOperation operation = new DeleteSchemaOperation(getModel(), schema);
                operation.execute(ModelView.this);
                treeViewer.refresh(getSchemasNode());
                
            } else {
                Table table = (Table) selectedNode.element;
                DeleteTableOperation operation = new DeleteTableOperation (table, getModel());
                operation.execute(ModelView.this);
                treeViewer.refresh();
            }
        }
    }

    public class CopyAction extends UpdateableAction {

        public CopyAction() {
            super();
            setId(ActionFactory.COPY.getId());
            setText(RMBenchMessages.ModelView_Copy_Text);
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            setDisabledImageDescriptor(
                    sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
            setEnabled(false);
        }

        public boolean isEnabled() {
            return selectedNode != null && (
                    selectedNode.element instanceof Schema || 
                    selectedNode.element instanceof Table ||
                    selectedNode.element == RMBenchMessages.ModelView_TABLES_GROUP);
        }
        
        public void run() {
            Table[] tables;
            if(selectedNode.element instanceof Schema) {
                List<Table> tableList = ((Schema)selectedNode.element).getTables();
                tables = tableList.toArray(new Table[tableList.size()]);
            }
            else if (selectedNode.element instanceof Table) {
                tables = new Table[]{(Table)selectedNode.element};
            }
            else {
                tables = selectedNode.asTables();
            }
            Clipboard.getDefault().setContents(tables);
        }
    }
    
    private class NewSchemaAction extends UpdateableAction {
        
        NewSchemaAction() {
            super();
            setText(RMBenchMessages.ModelView_NewSchema_Text);
            setToolTipText(RMBenchMessages.ModelView_NewSchema_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.SCHEMA));
        }

        public void run() {
            InputDialog inputDialog = new InputDialog(
                    getViewSite().getShell(),
                    RMBenchMessages.ModelView_NewSchemaDlg_Title,
                    RMBenchMessages.ModelView_NewSchemaDlg_Msg,
                    "", //$NON-NLS-1$
                    new SchemaNameValidator());
            if(inputDialog.open() == Window.OK) {
                String schemaName = inputDialog.getValue();
                AddSchemaOperation operation = new AddSchemaOperation(getModel(), schemaName);
                operation.execute(ModelView.this);
            }
        }
    }
    
    private class ExportAction extends UpdateableAction {
        
        ExportAction() {
            super();
            setText(RMBenchMessages.ModelView_DBExportModel_Text);
            setToolTipText(RMBenchMessages.ModelView_DBExportModel_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DBEXPORT));
        }
       
        public void run() {
            IDatabaseInfo dbInfo = getModel().getDatabaseInfo();
            DBModel[] dbmodels = RMBenchPlugin.getDefault().getDBModels(dbInfo);
            
            DDLGeneratorExtension[] generators = RMBenchPlugin.getDDLGeneratorExtensions(dbInfo);
            if(generators.length == 0) {
                MessageDialog.openInformation(
                        getSite().getShell(), 
                        RMBenchMessages.ModelView_ExportDialog_Title,
                        RMBenchMessages.ModelView_ExportDialog_NoGenerator);
                return;
            }
            DDLExportWizard wizard = new DDLExportWizard(dbInfo, dbmodels, generators);
            IWorkbench workbench = RMBenchPlugin.getDefault().getWorkbench();
            WizardDialog dialog = 
                new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
            dialog.setBlockOnOpen(true);

            if(dialog.open() == Window.OK) {
                if(wizard.getGenerateDiff()) {
                    ModelCompareEditorInput editorInput = new ModelCompareEditorInput(
                            getModel(), 
                            wizard.getDBModel(),
                            wizard.getGenerator(),
                            wizard.getFormatter(),
                            wizard.getScript(),
                            wizard.getGenerateDrop(),
                            true,
                            getSite().getShell());
                    
                    if(editorInput.compareResultOK()) {
                        try {
                            getSite().getPage().openEditor(editorInput, ModelCompareEditor.ID);
                        }
                        catch (PartInitException e) {
                            RMBenchPlugin.logError(e);
                        }
                    }
                }
                else {
                    DDLEditorInput editorInput = new DDLEditorInput(
                            getModel(),
                            wizard.getGenerator(),
                            wizard.getFormatter(),
                            wizard.getScript(),
                            wizard.getGenerateDrop()); 
                    try {
                        getSite().getPage().openEditor(editorInput, DDLEditor.ID);
                    }
                    catch (PartInitException e) {
                        RMBenchPlugin.logError(e);
                    }
                }
            }
        }
    }
    
    private class SetTargetSchemaAction extends UpdateableAction {
        
        private final Schema schema;
        
        SetTargetSchemaAction(Schema schema) {
            super(schema.getName(), IAction.AS_RADIO_BUTTON);
            this.schema = schema;
        }

        public void run() {
            RMBenchOperation operation = 
                new DiagramDefaultSchemaOperation((Diagram)selectedNode.element, schema);
            operation.execute(this);
        }
    }
    
    private class RenameAction extends UpdateableAction {
        public RenameAction() {
            super();
            setId(ActionFactory.RENAME.getId());
            setText(RMBenchMessages.ModelView_Rename_Text);                    
            setEnabled(false);
        }
        
        public void run() {
            nodeEditable=true;
            treeViewer.editElement(selectedNode, 0);
            nodeEditable=false;
        }
        
        public boolean isEnabled() {
            return selectedNode != null && (
                    selectedNode.element instanceof Schema || 
                    selectedNode.element instanceof Table ||
                    selectedNode.element instanceof Diagram);
        }

    }
    
}
