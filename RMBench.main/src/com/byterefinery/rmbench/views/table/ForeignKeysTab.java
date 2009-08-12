/*
 * created 06.04.2005
 * 
 * $Id:ForeignKeysTab.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.EventManager.Properties;
import com.byterefinery.rmbench.dialogs.ForeignKeyConfigurator;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddForeignKeyOperation;
import com.byterefinery.rmbench.operations.DeleteForeignKeyOperation;
import com.byterefinery.rmbench.operations.ModifyForeignKeyOperation;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;
import com.byterefinery.rmbench.views.TreeLayout;

/**
 * View tab for displaying foreign key information, and to delete foreign keys.
 * Foreign keys are created only visually
 *  
 * @author cse
 */
public class ForeignKeysTab extends DetailsTabGroup.DetailTab {

    //we are reusing the message constants as editor properties
    private static final String[] COLUMN_PROPERTIES = new String[] {
            RMBenchMessages.ForeignkeysTab_Column_Key,
            RMBenchMessages.ForeignkeysTab_Column_Column,
            RMBenchMessages.ForeignkeysTab_Column_Target,
            RMBenchMessages.ForeignkeysTab_Column_DeleteRule,
            RMBenchMessages.ForeignkeysTab_Column_UpdateRule
    };
    
    private EventManager.Listener foreignkeyListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            switch(eventType) {
                case FOREIGNKEY_ADDED:  {
                	if(event.owner == table) {
                        treeViewer.setInput(table);
                	}
                    break;
                }
                case FOREIGNKEY_DELETED:  {
                	if(event.owner == table) {
                		ForeignKey foreignKey = (ForeignKey)event.element;
                        treeViewer.remove(foreignKey);
                    }
                    break;
                }
                case FOREIGNKEY_SELECTED:  {
                    ForeignKey foreignKey = (ForeignKey)event.element;
                    activate();
                    if(foreignKey != selectedForeignKey) {
                    	if(table != event.owner)
                    		setTable((Table)event.owner);
                        treeViewer.setSelection(new StructuredSelection(foreignKey));
                    }
                    break;
                }
                case FOREIGNKEY_MODIFIED: {
                	if(event.owner == table) {
                		ForeignKey foreignKey = (ForeignKey)event.element;
	                    if(RMBenchMessages.ForeignkeysTab_Column_Column == event.info
	                    		|| Properties.FK_COLUMN_DELETED == event.info 
                                || EventManager.Properties.FK_COLUMN_REPLACED == event.info
                                || EventManager.Properties.COLUMN_NAME == event.info)
	                        treeViewer.refresh(foreignKey);
	                    else
	                        treeViewer.update(foreignKey, null);
                	}
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    FOREIGNKEY_ADDED | 
                    FOREIGNKEY_DELETED | 
                    FOREIGNKEY_SELECTED |
                    FOREIGNKEY_MODIFIED, this);
        }
    };
    
    private ForeignKey selectedForeignKey;
    private ColumnPair selectedColumns;
    
    private UpdateableAction[] actions = new UpdateableAction[2];
    private IActionBars actionBars;
    
    private TreeViewer treeViewer;

    private IForeignKey.Action[] deleteActions;
    private IForeignKey.Action[] updateActions;
    
    private ComboBoxCellEditor columnEditor;
    private ComboBoxCellEditor deleteActionEditor;
    private ComboBoxCellEditor updateActionEditor;
    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        this.actionBars = actionBars;
        
        final Tree tree = new Tree(parent,  SWT.SINGLE | SWT.FULL_SELECTION);
        final TreeLayout layout = new TreeLayout();
        
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        
        TreeColumn column;
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ForeignkeysTab_Column_Key);
        layout.addColumnData(new ColumnWeightData(20));

        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ForeignkeysTab_Column_Column);
        layout.addColumnData(new ColumnWeightData(20));

        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ForeignkeysTab_Column_Target);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ForeignkeysTab_Column_DeleteRule);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ForeignkeysTab_Column_UpdateRule);
        layout.addColumnData(new ColumnWeightData(20));
        
        tree.setLayout(layout);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(tree);
        treeViewer.setLabelProvider(new TreeViewerLabelProvider());
        treeViewer.setContentProvider(new TreeViewerContentProvider());
        treeViewer.setColumnProperties(COLUMN_PROPERTIES);
        treeViewer.setCellModifier(new CellModifier());

        CellEditor[] cellEditors = new CellEditor[5];
        cellEditors[0] = new TextCellEditor(treeViewer.getTree());
        cellEditors[0].setValidator(new NameValidator());
        
        columnEditor = new ComboBoxCellEditor(treeViewer.getTree(), new String[0], SWT.READ_ONLY);
        cellEditors[1] = columnEditor;
        deleteActionEditor = new ComboBoxCellEditor(treeViewer.getTree(), new String[0], SWT.READ_ONLY);
        cellEditors[3] = deleteActionEditor; 
        updateActionEditor = new ComboBoxCellEditor(treeViewer.getTree(), new String[0], SWT.READ_ONLY);
        cellEditors[4] = updateActionEditor;
        treeViewer.setCellEditors(cellEditors);
        
        treeViewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
            }
            public void treeExpanded(TreeExpansionEvent event) {
            	
            	if ((event.getElement()!=null)&&(event.getElement() instanceof ForeignKey))
            		selectedForeignKey=(ForeignKey) event.getElement();
            	
                tree.layout();
            }
        });
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Object element = selection.getFirstElement();
                if(element instanceof ForeignKey) {
                    selectedForeignKey = (ForeignKey)element;
                }
                else if(element instanceof ColumnPair) {
                    selectedColumns = (ColumnPair)element;
                    setupColumnCellEditor();
                }
                updateActions();
            }
        });
        
        actions[0] = new CreateAction();
        actions[1] = new DeleteAction();
        
        setTable(table);
        updateActions();
        
        foreignkeyListener.register();
    }

    public void setTable(Table table) {
        super.setTable(table);
        
        if(table != null) {
            deleteActions = table.getSchema().getDatabaseInfo().getDeleteActions();
            updateActions = table.getSchema().getDatabaseInfo().getUpdateActions();
        }
        else {
            deleteActions = new IForeignKey.Action[0];
            updateActions = new IForeignKey.Action[0];
        }
        selectedForeignKey = null;
        selectedColumns = null;
        
        if(this.treeViewer != null) {
            this.treeViewer.setInput(table);
            setupActionCellEditors();
        }
        
        //redraw actions
        for (int i=0; i<actions.length; i++) {
        	actions[i].setEnabled(actions[i].isEnabled());
        }
    }

    private void setupActionCellEditors() {
        String[] actions = new String[deleteActions.length];
        for (int i = 0; i < actions.length; i++)
            actions[i] = deleteActions[i].getName();
        deleteActionEditor.setItems(actions);
        
        actions = new String[updateActions.length];
        for (int i = 0; i < actions.length; i++)
            actions[i] = updateActions[i].getName();
        updateActionEditor.setItems(actions);
    }

    private void setupColumnCellEditor() {
        List<String> result = new ArrayList<String>(selectedForeignKey.getColumns().length);
        int valueIndex = -1;
        for (Column column : table.getColumns()) {
            if (column == selectedColumns.column) {
                result.add(column.getName());
                valueIndex = result.size() - 1;
            } else if ((column.getDataType().equals(selectedColumns.targetColumn.getDataType()))
                    && (!column.belongsToForeignKey())) {
                result.add(column.getName());
            }
                
            
        }
        String[] items = (String[])result.toArray(new String[result.size()]);
        columnEditor.setItems(items);
        columnEditor.setValue(new Integer(valueIndex));
    }
    
    public Action[] getActions() {
        return actions;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.FK_OUT);
    }

    public String getTitle() {
        return RMBenchMessages.ForeignkeysTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ForeignkeysTab_Description;
    }
    
    public void dispose() {
        foreignkeyListener.unregister();
    }

    private void updateActions() {
        for (int i = 0; i < actions.length; i++) {
            actions[i].update();
        }
    }

    private String setErrorMessage(String message) {
        actionBars.getStatusLineManager().setErrorMessage(message);
        return message;
    }
    
    private class TreeViewerLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0: {
                    if(element instanceof ForeignKey) {
                        return ((ForeignKey)element).getName();
                    }
                    break;
                }
                case 1: {
                    if(element instanceof ColumnPair)
                        return ((ColumnPair)element).column.getName();
                    break;
                }
                case 2: {
                    if(element instanceof ForeignKey)
                        return ((ForeignKey)element).getTargetTable().getName();
                    else {
                        return ((ColumnPair)element).targetColumn.getName();
                    }
                }
                case 3: {
                    if(element instanceof ForeignKey)
                        return ((ForeignKey)element).getDeleteAction().getName();
                    break;
                }
                case 4: {
                    if(element instanceof ForeignKey)
                        return ((ForeignKey)element).getUpdateAction().getName();
                    break;
                }
            }
            return null;
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
    
    private class TreeViewerContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            return ColumnPair.createFrom((ForeignKey)parentElement);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof ForeignKey;
        }

        public Object[] getElements(Object inputElement) {
            return ((Table)inputElement).getForeignKeys().toArray();
        }

        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class DeleteAction extends UpdateableAction {
        DeleteAction() {
            super();
            setText(RMBenchMessages.ForeignKeysTab_Delete_Text);
            setToolTipText(RMBenchMessages.ForeignKeysTab_Delete_Text);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setDisabledImageDescriptor(ImageConstants.DELETE_DISABLED_DESC);
        }
        
        public boolean isEnabled() {
            return selectedForeignKey != null;
        }

        public void run() {
            DeleteForeignKeyOperation operation = new DeleteForeignKeyOperation(selectedForeignKey);
            operation.execute(ForeignKeysTab.this);
        }
    }

    private class CreateAction extends UpdateableAction {
        CreateAction() {
            super();
            setText(RMBenchMessages.ForeignKeysTab_CreateAction_Text);
            setToolTipText(RMBenchMessages.ForeignKeysTab_CreateAction_Text);
            setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.ADD));
            setDisabledImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.ADD_disabled));
        }
        
        public boolean isEnabled() {
            return table != null && table.getColumns().size() > 0;
        }

        public void run() {
            ForeignKeyConfigurator dialog = new ForeignKeyConfigurator(
                    treeViewer.getTree().getShell(), table);
            if(dialog.open() == Window.OK) {
                AddForeignKeyOperation operation = new AddForeignKeyOperation(table);
                operation.setColumns(dialog.getKeyColumns());
                operation.setTargetTable(dialog.getTargetTable());
                operation.execute(ForeignKeysTab.this);
            }
        }
    }

    private class NameValidator implements ICellEditorValidator {

        public String isValid(Object value) {
            
            String sval = (String)value;
            if(sval == null || sval.length() == 0)
            	return setErrorMessage(RMBenchMessages.ForeignKeysTab_Err_InvalidName);
                        
            // we know the validator react before the selctionListener, 
            // so we have to retrieve the selection on our own
            Object obj=((StructuredSelection) treeViewer.getSelection()).getFirstElement();
            // this test should be obsolet, but for safety...
            if (obj==null || !(obj instanceof ForeignKey))
            	return "";		//we set no error message because that should never happen, we set it not null because that would mean the name is valid 
            
            selectedForeignKey = (ForeignKey)obj;
            
            if(!sval.equals(selectedForeignKey.getName()) && 
                    RMBenchPlugin.getModelManager().getModel().containsConstraint(sval))
                return setErrorMessage(RMBenchMessages.ForeignKeysTab_Err_DuplicateName);
            
            return setErrorMessage(null);
        }
    }

    private class CellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            if(RMBenchMessages.ForeignkeysTab_Column_Key == property)
                return element instanceof ForeignKey;
            if(RMBenchMessages.ForeignkeysTab_Column_Column == property)
                return (element==selectedColumns);
            if(RMBenchMessages.ForeignkeysTab_Column_DeleteRule == property)
                return element instanceof ForeignKey;
            if(RMBenchMessages.ForeignkeysTab_Column_UpdateRule == property)
                return element instanceof ForeignKey;
            return false;
        }

        public Object getValue(Object element, String property) {
            if(RMBenchMessages.ForeignkeysTab_Column_Key == property) {
                return ((ForeignKey)element).getName();
            }
            if(RMBenchMessages.ForeignkeysTab_Column_Column == property) {
                Column col = ((ColumnPair)element).column;
                String[] items = columnEditor.getItems();
                for (int i = 0; i < items.length; i++) {
                    if(items[i].equals(col.getName()))
                        return new Integer(i);
                }
            }
            if(RMBenchMessages.ForeignkeysTab_Column_DeleteRule == property) {
                IForeignKey.Action action = ((ForeignKey)element).getDeleteAction(); 
                for (int i = 0; i < deleteActions.length; i++) {
                    if(action == deleteActions[i])
                        return new Integer(i);
                }
            }
            if(RMBenchMessages.ForeignkeysTab_Column_UpdateRule == property) {
                IForeignKey.Action action = ((ForeignKey)element).getUpdateAction(); 
                for (int i = 0; i < updateActions.length; i++) {
                    if(action == updateActions[i])
                        return new Integer(i);
                }
            }
            return null;
        }

        public void modify(Object element, String property, Object value) {
        	ModifyForeignKeyOperation operation;
        	
            if(RMBenchMessages.ForeignkeysTab_Column_Key == property) {
            	//if theres an invalid name we don't create an operation
            	if ( (value==null) || ( ((String)value).length()==0) )
                	return;
            	operation = new ModifyForeignKeyOperation(
            			selectedForeignKey, 
            			property,
            			value, 
            			selectedForeignKey.getName());
            	operation.execute(ForeignKeysTab.this);
            }
            else if(RMBenchMessages.ForeignkeysTab_Column_Column == property) {
                Integer intVal = (Integer)value;
                String item = columnEditor.getItems()[intVal.intValue()];
                Column newColumn = table.getColumn(item);

                if (selectedColumns.column.equals(newColumn))
                    return;
                
            	operation = new ModifyForeignKeyOperation(
            			selectedForeignKey, 
            			property,
            			newColumn, 
            			selectedColumns.column);
            	operation.execute(ForeignKeysTab.this);
            }
            else if(RMBenchMessages.ForeignkeysTab_Column_DeleteRule == property) {
                Integer intVal = (Integer)value;
                
                if (selectedForeignKey.getDeleteAction().equals(deleteActions[intVal.intValue()]))
                        return;
                
            	operation = new ModifyForeignKeyOperation(
            			selectedForeignKey, 
            			property,
            			deleteActions[intVal.intValue()], 
            			selectedForeignKey.getDeleteAction());
            	operation.execute(ForeignKeysTab.this);
            }
            else if(RMBenchMessages.ForeignkeysTab_Column_UpdateRule == property) {
                Integer intVal = (Integer)value;
                
                if (selectedForeignKey.getUpdateAction().equals(updateActions[intVal.intValue()]))
                    return;
                
            	operation = new ModifyForeignKeyOperation(
            			selectedForeignKey, 
            			property,
            			updateActions[intVal.intValue()], 
            			selectedForeignKey.getUpdateAction());
            	operation.execute(ForeignKeysTab.this);
            }
        }
    }
}