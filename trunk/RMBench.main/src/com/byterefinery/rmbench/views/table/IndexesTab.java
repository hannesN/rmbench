/*
 * created 06.04.2005
 * 
 * $Id: IndexesTab.java 466 2006-08-19 11:53:02Z hannesn $
 */
package com.byterefinery.rmbench.views.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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
import com.byterefinery.rmbench.dialogs.IndexEditorDialog;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.DeleteIndexOperation;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;
import com.byterefinery.rmbench.views.TreeLayout;

/**
 * viewer tab that shows the indexes defined on the selected table
 * 
 * @author cse
 */
public class IndexesTab extends DetailsTabGroup.DetailTab {
    
    private TreeViewer treeViewer;
    
    private Index selectedIndex;
    
    private UpdateableAction[] actions = new UpdateableAction[3];

    private EventManager.Listener eventListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.owner != table)
                return;
            Index index = (Index)event.element;
            switch(eventType) {
                case INDEX_ADDED: {
                    treeViewer.add(table, index);
                    break;
                }
                case INDEX_DELETED: {
                    treeViewer.remove(index);
                    break;
                }
                case INDEX_MODIFIED: {
                    treeViewer.refresh(index);
                    break;
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    INDEX_ADDED | INDEX_DELETED | INDEX_MODIFIED, this);
        }
    };
    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        final Tree tree = new Tree(parent,  SWT.FULL_SELECTION);
        final TreeLayout layout = new TreeLayout();
        
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        
        TreeColumn column;
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.IndexesTab_Column_Key);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.IndexesTab_Column_Unique);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.IndexesTab_Column_Column);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.IndexesTab_Column_Order);
        layout.addColumnData(new ColumnWeightData(30));
        
        tree.setLayout(layout);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(tree);
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Object element = selection.getFirstElement();
                if(element instanceof Index) {
                    selectedIndex = (Index)element;
                }
                else {
                    selectedIndex = null;
                }
                updateActions();
            }
        });
                
        actions[0] = new EditAction();
        actions[1] = new AddAction();
        actions[2] = new DeleteAction();
        
        eventListener.register();
        setTable(table);
        updateActions();
    }

    public void setTable(Table table) {
        super.setTable(table);
        if(this.treeViewer != null) {
            this.treeViewer.setInput(table);
            updateActions();
        }
    }

    public Action[] getActions() {
        return actions;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.INDEX);
    }

    public String getTitle() {
        return RMBenchMessages.IndexesTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.IndexesTab_Description;
    }
    
    private void updateActions() {
        for (int i = 0; i < actions.length; i++) {
            actions[i].update();
        }
    }

    private class LabelProvider implements ITableLabelProvider {

        private transient Index lastIndex;
        
        private static final String ASC = "ASC";
        private static final String DESC = "DESC";
        
        public Image getColumnImage(Object element, int columnIndex) {
            switch(columnIndex) {
                case 1: {
                    if(element instanceof Index && lastIndex.isUnique()) {
                        return RMBenchPlugin.getImage(ImageConstants.CHECKED);
                    }
                    break;
                }
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0: {
                    if(element instanceof Index) {
                        lastIndex = (Index)element;
                        return lastIndex.getName();
                    }
                    break;
                }
                case 2: {
                    if(element instanceof IndexColumn)
                        return ((IndexColumn)element).getColumn().getName();
                    break;
                }
                case 3: {
                    if(element instanceof IndexColumn) {
                        return ((IndexColumn)element).isAscending() ? ASC : DESC;
                    }
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
    
    private class ContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Index) {
                Index index = (Index) parentElement;
                Column[] columns = index.getColumns();
                IndexColumn indexColumns[] = new IndexColumn[columns.length];
                for (int i=0; i< indexColumns.length; i++) {
                    indexColumns[i] = new IndexColumn(columns[i], index.isAscending(columns[i]));
                }
                
                return indexColumns;
            }
            return null;
            
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof Index;
        }

        public Object[] getElements(Object inputElement) {
            return ((Table)inputElement).getIndexes().toArray();
        }

        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class EditAction extends UpdateableAction {
        EditAction() {
            super();
            setText(RMBenchMessages.IndexesTab_EditIndex_Text);
            setToolTipText(RMBenchMessages.IndexesTab_EditIndex_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT_disabled));
        }

        public boolean isEnabled() {
            return selectedIndex != null;
        }

        public void run() {
            IndexEditorDialog indexEditor = new IndexEditorDialog(
                    treeViewer.getTree().getShell(), table);
            indexEditor.open(selectedIndex);
        }
    }
    
    private class AddAction extends UpdateableAction {
        AddAction() {
            super();
            setText(RMBenchMessages.IndexesTab_AddIndex_Text);
            setToolTipText(RMBenchMessages.IndexesTab_AddIndex_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD_disabled));
        }

        public boolean isEnabled() {
            return table != null && !table.getColumns().isEmpty();
        }

        public void run() {
            assert(table != null);
            IndexEditorDialog indexEditor = 
                new IndexEditorDialog(treeViewer.getTree().getShell(), table);
            indexEditor.open();
        }
    }
    
    private class DeleteAction extends UpdateableAction {
        DeleteAction() {
            super();
            setText(RMBenchMessages.IndexesTab_DeleteIndex_Text);
            setToolTipText(RMBenchMessages.IndexesTab_DeleteIndex_Description);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setDisabledImageDescriptor(ImageConstants.DELETE_DISABLED_DESC);
        }
        
        public boolean isEnabled() {
            return selectedIndex != null;
        }

        public void run() {
            DeleteIndexOperation operation = new DeleteIndexOperation(selectedIndex);
            operation.execute(IndexesTab.this);
        }
    }
    
    private class IndexColumn {
        final private Column column;
        final private boolean ascending;
        
        public IndexColumn(Column column, boolean ascending) {
            super();
            this.column = column;
            this.ascending = ascending;
        }

        /**
         * @return Returns the ascending.
         */
        public boolean isAscending() {
            return ascending;
        }

        /**
         * @return Returns the column.
         */
        public Column getColumn() {
            return column;
        }
        
        
    }
}
