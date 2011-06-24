/*
 * created 05.04.2005
 * 
 * $Id: TableDetailsView.java 456 2006-08-17 15:19:56Z cse $
 */
package com.byterefinery.rmbench.views.table;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.ViewPart;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.ModelManager;

/**
 * a view that shows the details of a table in several tabs within a tab folder.
 * Each of the subordinate tabs defines its own set of actions, which are 
 * displayed upon activation
 *  
 * @author cse
 */
public class TableDetailsView extends ViewPart implements ISaveablePart {

    public static final String VIEW_ID = "com.byterefinery.rmbench.tabledetailsview";
    
    private EventManager.Listener globalEventListener = new EventManager.Listener() {
        public void eventOccurred(int eventType, Event event) {
            switch(eventType) {
                case TABLE_SELECTED: {
                    Table table = (Table)event.element;
                    tabs.setTable(table);
                    setPartNameFromTable(table);
                    break;
                }
                case TABLE_MODIFIED: {
                    Table table = (Table)event.element;
                    if(table == tabs.getTable()) {
                        setPartNameFromTable(table);
                    }
                    break;
                }
                case TABLE_DELETED: {
                    Table table = (Table)event.element;
                    if(table == tabs.getTable()) {
                        tabs.setTable(null);
                        setPartNameFromTable(null);
                    }
                    break;
                }
                case SCHEMA_DELETED: {
                    Schema schema = (Schema)event.element;
                    if(tabs.getTable() != null && schema == tabs.getTable().getSchema()) {
                        tabs.setTable(null);
                        setPartNameFromTable(null);
                    }
                    break;
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    TABLE_DELETED | TABLE_SELECTED | TABLE_MODIFIED | SCHEMA_DELETED, this);
        }
    };
    
    private ModelManager.Listener modelStorageListener = new ModelManager.Listener() {

		public void modelAboutToBeReplaced(Model model) {
		}

		public void modelReplaced(Model model) {
            tabs.setTable(null);
            setPartNameFromTable(null);
		}

		public void dirtyStateChanged(Model model, boolean isDirty) {
            firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
		}
    };
    
    protected DetailsTabGroup tabs;
    protected UndoRedoActionGroup undoRedoGroup;
    
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        tabs = new DetailsTabGroup(site.getActionBars());
    }

    //@see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    public void createPartControl(Composite parent) {

		Table table = RMBenchPlugin.getEventManager().getSelectedTable();
		setPartNameFromTable(table);
		
        final IActionBars actionBars = getViewSite().getActionBars();
        
		tabs.createControl(parent, table);
        tabs.addTabListener(new DetailsTabGroup.TabListener() {

            public void tabActivated(DetailsTabGroup.DetailTab tab) {
                IToolBarManager manager = actionBars.getToolBarManager();
                manager.removeAll();
                
                Action[] actions = tab.getActions();
                if(actions != null) {
                    for (int i = 0; i < actions.length; i++) {
                        manager.add(actions[i]);
                    }
                }
                actionBars.updateActionBars();
            }
        });
        tabs.showFirstPage();
        
        undoRedoGroup = new UndoRedoActionGroup(this.getSite(), RMBenchOperation.CONTEXT, false);
        undoRedoGroup.fillActionBars(actionBars);
        
        RMBenchPlugin.getModelManager().addListener(modelStorageListener);
        globalEventListener.register();
    }

    public void dispose() {
        RMBenchPlugin.getModelManager().removeListener(modelStorageListener);
        globalEventListener.unregister();
        tabs.dispose();
        undoRedoGroup.dispose();
        super.dispose();
    }

    //@see org.eclipse.ui.part.WorkbenchPart#setFocus()
    public void setFocus() {
    }
	
    public void showFirstPage() {
        tabs.showFirstPage();
    }
    
	private void setPartNameFromTable(Table table) {
		if(table != null) {
	        setPartName(
                    MessageFormat.format(
                            RMBenchMessages.TableDetailsView_title, 
                            new Object[]{table.getName()}));
		}
        else {
            setPartName(getSite().getRegisteredName());
        }
	}

    public void doSave(IProgressMonitor progressMonitor) {
        RMBenchPlugin.getModelManager().doSave(getSite().getShell(), progressMonitor);
    }

    public void doSaveAs() {
        RMBenchPlugin.getModelManager().doSaveAs(getViewSite());
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
}
