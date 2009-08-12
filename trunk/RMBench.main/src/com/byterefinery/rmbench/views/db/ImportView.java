/*
 * created 12.03.2005
 * 
 * $Id: ImportView.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.views.db;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.dialogs.ExceptionDialog;
import com.byterefinery.rmbench.dialogs.JdbcConnectionWizard;
import com.byterefinery.rmbench.dnd.ImportTransfer;
import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDBAccess.Executor;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.operations.ImportSchemaOperation;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;
import com.byterefinery.rmbench.views.dbtable.DBTableView;

/**
 * a view that allows browsing database metadata through one or more JDBC connections,
 * for the purpose of importing schema elements into a design model
 *  
 * @author cse
 */
public class ImportView extends ViewPart {

    public static final String VIEW_ID = "com.byterefinery.rmbench.views.ImportView"; //$NON-NLS-1$
    
    private IPartListener tableViewListener = new IPartListener() {
        public void partOpened(IWorkbenchPart part) {
            if(part instanceof DBTableView)
                tableView = (DBTableView)part;
        }
        public void partClosed(IWorkbenchPart part) {
            if(part == tableView) tableView = null;
        }
        public void partDeactivated(IWorkbenchPart part) {
        }
        public void partActivated(IWorkbenchPart part) {
        }
        public void partBroughtToTop(IWorkbenchPart part) {
        }
    };
    
    private DBModel.Listener dbModelListener = new DBModel.Listener() {
        public void loadedChanged(DBModel dbmodel) {
            TreeNode[] treeNodes = (TreeNode[])treeViewer.getInput();
            if(treeNodes == null)
                return;
            for (int i = 0; i < treeNodes.length; i++) {
                if(treeNodes[i].element == dbmodel) {
                    final TreeNode treeNode = treeNodes[i];
                    
                    treeNode.rebuildChildren(dbmodel.getSchemaList());
                    treeNode.image = RMBenchPlugin.getImage(ImageConstants.CONNECTED);
                    
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            treeViewer.refresh(treeNode);
                        }
                    });
                    return;
                }
            }
        }

        public void connectChanged(Executor executor) {
        }
    };
    
    private EventManager.Listener modelListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if (eventType==DBMODELS_ADDED) {
                //is remove event
                if (event.element==null)
                    return;
                
                ((DBModel)event.element).addListener(dbModelListener);
            }
            treeViewer.setInput(buildInput());
        }
        
        public void register() {
            RMBenchPlugin.getEventManager().addListener(DBMODELS_ADDED|DBMODELS_CHANGED|DBMODELS_REMOVED,  this);
        }
        
    };
    
	private TreeViewer treeViewer;
    private DragSource dragSource;
    
    private TreeNode selectedDBModelNode;
    private boolean importableNodesSelected;

    private LoadAction loadAction;
    private DeleteAction deleteAction;
    private EditAction editAction;
    private CopyAction copyAction;
    
    private DBTableView tableView;
    
	public void createPartControl(Composite parent) {
		
		treeViewer = new TreeViewer(
	            parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new CustomContentProvider());
		treeViewer.setLabelProvider(new CustomLabelProvider());
        
        getSite().getPage().addPartListener(tableViewListener);
        
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            TreeNode selectedNode;
            
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection(); 

                if(selection.size() == 1) {
                	TreeNode node = (TreeNode)selection.getFirstElement();
                    if(node == selectedNode)
                        return;
                    else
                        selectedNode = node;
                    
                	if(node.element instanceof DBModel) {
                        selectedDBModelNode = node;
                    }
                    else {
                        selectedDBModelNode = null;
                        
                        if(node.element instanceof DBTable && getTableView() != null)
                            tableView.setDBTable((DBTable)node.element);
                    }
                    importableNodesSelected = canImportSelectedNodes();
                    updateActions();
                }
                else {
                    selectedDBModelNode = null;
                    importableNodesSelected = canImportSelectedNodes();
                    updateActions();
                }
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                TreeNode node = (TreeNode)selection.getFirstElement();
                if(node.element instanceof DBTable) {
                    try {
                        tableView = (DBTableView)RMBenchPlugin.getDefault().getWorkbench().
                            getActiveWorkbenchWindow().getActivePage().showView(DBTableView.VIEW_ID);
                        tableView.setDBTable((DBTable)node.element);
                    } catch (PartInitException e) {
                        RMBenchPlugin.logError(e);
                    }
                }
            }
        });
        
        dragSource = new DragSource(
        		treeViewer.getControl(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        dragSource.setTransfer(new Transfer[] {ImportTransfer.getInstance()});

        dragSource.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
            	event.doit = importableNodesSelected;
            }
            public void dragSetData(DragSourceEvent event) {
                if (ImportTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = getSelectedImportElements();
                }
            }
			public void dragFinished(DragSourceEvent event) {
            }
        });
        
        TreeNode[] input = buildInput();
		treeViewer.setInput(input);
        
        createActions();
        createContextMenu();
        updateActions();
        modelListener.register();
	}

    protected DBTableView getTableView() {
        if(tableView == null) {
            tableView = (DBTableView)getSite().getPage().findView(DBTableView.VIEW_ID);
        }
        return tableView;
    }

    public void setTableView(DBTableView tableView) {
        this.tableView = tableView;
    }
    
    private boolean canImportSelectedNodes() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        
        //multiple selection is only supported for objects on the same level
        int level = -1;
        int count = 0;
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			TreeNode node = (TreeNode) it.next();
			if((level >= 0 && node.level != level) || !node.canImport())
				return false;
			level = node.level;
			count += node.countImportElements();
		}
		return count > 0;
	}
	
    private Object[] getSelectedImportElements() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		List<TreeNode> result = new ArrayList<TreeNode>();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			TreeNode node = (TreeNode) it.next();
			node.addImportElements(result);
		}
		return result.toArray();
	}
    
    private void createActions() {
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager manager = actionBars.getToolBarManager();
    
        loadAction = new LoadAction(); 
        manager.add(loadAction);
        manager.add(new AddAction());
        copyAction = new CopyAction();
        manager.add(copyAction);
        editAction = new EditAction();
        manager.add(editAction);
        deleteAction = new DeleteAction();
        manager.add(deleteAction);
    }

    private void createContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new AddAction());
                manager.add(new Separator());
                
                Model model = RMBenchPlugin.getModelManager().getModel();
                manager.add(new ImportAction(model));
                
                if(selectedDBModelNode != null) {
                    manager.add(loadAction);
                    manager.add(new Separator());
                    manager.add(copyAction);
                    manager.add(editAction);
                    manager.add(deleteAction);
                }
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
        
        getSite().registerContextMenu(menuMgr, treeViewer);
    }
    
    private void updateActions() {
        loadAction.update();
        deleteAction.update();
        editAction.update();
        copyAction.update();
    }
    
    public void setFocus() {
	}
	
    public void dispose() {
        if(getTableView() != null) {
            if(!getSite().getWorkbenchWindow().getWorkbench().isClosing())
                getSite().getPage().hideView(tableView);
        }
        dragSource.dispose();
        getSite().getPage().removePartListener(tableViewListener);

        DBModel[] dbmodels = RMBenchPlugin.getDefault().getDBModels();
        for (int i = 0; i < dbmodels.length; i++)
            dbmodels[i].removeListener(dbModelListener);

        super.dispose();
	}

	private TreeNode[] buildInput() {
        DBModel[] dbmodels = RMBenchPlugin.getDefault().getDBModels();
        TreeNode[] forest = new TreeNode[dbmodels.length];
        
        for (int i = 0; i < dbmodels.length; i++) {
            dbmodels[i].addListener(dbModelListener);
            forest[i] = new TreeNode(null, dbmodels[i]);
        }
        Arrays.sort(forest);
        return forest;
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
            return ((TreeNode)parentElement).children;
		}
		public Object getParent(Object element) {
            return ((TreeNode)element).parent;
		}
		public boolean hasChildren(Object element) {
			return ((TreeNode)element).children != null;
		}
		public Object[] getElements(Object inputElement) {
			return (TreeNode[])inputElement;
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
    
    private class AddAction extends Action {
        
        AddAction() {
            super();
            setText(RMBenchMessages.ImportView_Add_Text);
            setToolTipText(RMBenchMessages.ImportView_Add_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD_disabled));
        }
        
        public void run() {
            JdbcConnectionWizard wizard = new JdbcConnectionWizard();
            WizardDialog dialog = new WizardDialog(getViewSite().getShell(), wizard);
            if(dialog.open() == Window.OK) {
                DBModel dbmodel = wizard.getDBModel();
                RMBenchPlugin.addDBModel(dbmodel);
                dbmodel.addListener(dbModelListener);
            }
        }
    }

    private class EditAction extends UpdateableAction {
        
        EditAction() {
            super();
            setText(RMBenchMessages.ImportView_Edit_Text);
            setToolTipText(RMBenchMessages.ImportView_Edit_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT_disabled));
        }
        
        public boolean isEnabled() {
            return selectedDBModelNode != null &&
                ((DBModel)selectedDBModelNode.element).getDBAccess().isConfigurable();
        }
        
        public void run() {
            DBModel dbmodel = (DBModel)selectedDBModelNode.element;
            JdbcConnectionWizard wizard = new JdbcConnectionWizard(dbmodel);
            WizardDialog dialog = new WizardDialog(getViewSite().getShell(), wizard);
            if(dialog.open() == Window.OK) {
                DBModel newModel = wizard.getDBModel();
                selectedDBModelNode.updateDBModel(newModel, false);
                RMBenchPlugin.dbModelChanged(dbmodel, newModel);
                treeViewer.refresh(selectedDBModelNode);
            }
        }
    }

    private class CopyAction extends UpdateableAction {
        
        CopyAction() {
            super();
            setText(RMBenchMessages.ImportView_Copy_Text);
            setToolTipText(RMBenchMessages.ImportView_Copy_Description);
            setImageDescriptor(ImageConstants.COPY_DESC);
            setDisabledImageDescriptor(ImageConstants.COPY_DISABLED_DESC);
        }
        
        public boolean isEnabled() {
            return selectedDBModelNode != null &&
                ((DBModel)selectedDBModelNode.element).getDBAccess().isConfigurable();
        }
        
        public void run() {
            DBModel dbmodel = (DBModel)selectedDBModelNode.element;
            String newName = dbmodel.getName()+RMBenchMessages.ImportView_Copy_nameExt;
            
            DBModel[] models = RMBenchPlugin.getDefault().getDBModels();
            boolean duplicate;
            String testName = newName;
            int count = 0;
            do {
                duplicate = false;
            	for (int i = 0; i < models.length; i++) {
					if(models[i].getName().equals(testName)) {
						count++;
						testName = newName + count;
						duplicate = true;
						break;
					}
				}
            } while(duplicate);
            
            RMBenchPlugin.addDBModel(dbmodel.copy(newName));
            dbmodel.addListener(dbModelListener);
            treeViewer.setInput(buildInput()); //how else update the root??
        }
    }

    private class DeleteAction extends UpdateableAction {
        
        DeleteAction() {
            super();
            setText(RMBenchMessages.ImportView_Delete_Text);
            setToolTipText(RMBenchMessages.ImportView_Delete_Description);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setDisabledImageDescriptor(ImageConstants.DELETE_DISABLED_DESC);
        }
        
        public boolean isEnabled() {
            return selectedDBModelNode != null &&
                ((DBModel)selectedDBModelNode.element).getDBAccess().isConfigurable();
        }
        
        public void run() {
            DBModel dbmodel = (DBModel)selectedDBModelNode.element;
            RMBenchPlugin.removeDBModel(dbmodel);
            dbmodel.removeListener(dbModelListener);
            treeViewer.setInput(buildInput()); //how else update the root??
        }
    }

    private class LoadAction extends UpdateableAction implements IRunnableWithProgress {
        
        private DBModel dbmodel;
        private SystemException error;
        private Shell shell;
        
        LoadAction() {
            super();
            setText(RMBenchMessages.ImportView_Load_Text);
            setToolTipText(RMBenchMessages.ImportView_Load_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.LOADMETA));
        }
        
        public boolean isEnabled() {
            return selectedDBModelNode != null;
        }
        
        public void run() {
            
            dbmodel = (DBModel)selectedDBModelNode.element;
            shell = treeViewer.getControl().getShell();
            error = null;
            
            IProgressService progressService = 
                RMBenchPlugin.getDefault().getWorkbench().getProgressService();
            try {
                progressService.busyCursorWhile(this);
            }
            catch (Exception e) {
                RMBenchPlugin.logError(e);
            }
            importableNodesSelected = canImportSelectedNodes();
            
            if(error != null) {
                RMBenchPlugin.logError(error);
                ExceptionDialog.openError(
                        treeViewer.getControl().getShell(), 
                        ExceptionMessages.errorDatabaseImport,
                        error.getStatus(error.getMessage()));
            }
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            monitor.beginTask(RMBenchMessages.ImportView_Loading_Message, 1);
            try {
                dbmodel.load(shell);
            } 
            catch (SystemException e) {
                error = e;
            }
            finally {
                monitor.done();
            }
        }
    }

    private class ImportAction extends UpdateableAction {
        
        private final Model targetModel;
        
        ImportAction(Model targetModel) {
            super();
            setText(RMBenchMessages.ImportView_Import_Text);
            setToolTipText(RMBenchMessages.ImportView_Import_Description);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.LOADMETA));
            this.targetModel = targetModel;
        }
        
        public boolean isEnabled() {
            return importableNodesSelected;
        }
        
        public void run() {
            if (targetModel==null) {
                MessageDialog.openError(getViewSite().getShell(),
                        RMBenchMessages.ImportView_NO_MODEL_TITLE,
                        RMBenchMessages.ImportView_NO_MODEL_MESSAGE);
                return;
            }
            ImportSchemaOperation operation = 
                new ImportSchemaOperation(targetModel, getSelectedImportElements());
            operation.execute(ImportView.this);
        }
    }
}
