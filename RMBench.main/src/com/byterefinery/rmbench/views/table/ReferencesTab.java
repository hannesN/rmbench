/*
 * created 21.04.2005
 * 
 * $Id: ReferencesTab.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.views.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
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
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.TreeLayout;
import com.byterefinery.rmbench.views.table.DetailsTabGroup.DetailTab;

/**
 * a table details tab that shows the incoming foreign key references
 * 
 * @author cse
 */
public class ReferencesTab extends DetailTab {

    private EventManager.Listener foreignkeyListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.origin != ReferencesTab.this) {
                ForeignKey foreignKey = (ForeignKey)event.element;
                if(foreignKey.getTargetTable() == table) {
                    treeViewer.setInput(table);
                }
            }
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(FOREIGNKEY_ADDED | FOREIGNKEY_DELETED, this);
        }
    };
    
    private TreeViewer treeViewer;
    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        final Tree tree = new Tree(parent,  SWT.FULL_SELECTION);
        final TreeLayout layout = new TreeLayout();
        
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        
        TreeColumn column;
        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ReferencesTab_Column_Table);
        layout.addColumnData(new ColumnWeightData(20));

        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ReferencesTab_Column_Column);
        layout.addColumnData(new ColumnWeightData(20));

        column = new TreeColumn(tree, SWT.NONE);
        column.setText(RMBenchMessages.ReferencesTab_Column_Target);
        layout.addColumnData(new ColumnWeightData(40));
        
        tree.setLayout(layout);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(tree);
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
            }
            public void treeExpanded(TreeExpansionEvent event) {
                tree.layout();
            }
        });
        setTable(table);
        foreignkeyListener.register();
    }

    public void setTable(Table table) {
        super.setTable(table);
        if(this.treeViewer != null) {
            this.treeViewer.setInput(table);
        }
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.FK_IN);
    }

    public String getTitle() {
        return RMBenchMessages.ReferencesTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ReferencesTab_Description;
    }

    void dispose() {
        foreignkeyListener.unregister();
    }
    
    private class LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0: {
                    if(element instanceof String)
                        return (String)element;
                    else if(element instanceof ForeignKey) {
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
                    if(element instanceof ColumnPair) {
                        return ((ColumnPair)element).targetColumn.getName();
                    }
                    break;
                }
            }
            return null;
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void removeListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
    }

    private class ContentProvider implements ITreeContentProvider {

        private Map<String, List<ForeignKey>> tableToReferences = new HashMap<String, List<ForeignKey>>(2);
        
        public Object[] getChildren(Object parentElement) {
            
            if(parentElement instanceof String) {
                return tableToReferences.get(parentElement).toArray();
            }
            else {
                return ColumnPair.createFrom((ForeignKey)parentElement);
            }
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof String || element instanceof ForeignKey;
        }

        public Object[] getElements(Object inputElement) {
            return tableToReferences.keySet().toArray();
        }

        public void dispose() {
        }
        
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            tableToReferences.clear();
            if(newInput != null) {
                Table table = (Table)newInput;
                for (ForeignKey reference : table.getReferences()) {
                    String name = reference.getTable().getName();
                    List<ForeignKey> refs = tableToReferences.get(name);
                    if(refs == null) {
                        refs = new ArrayList<ForeignKey>(2);
                        tableToReferences.put(name, refs);
                    }
                    refs.add(reference);
                }
            }
        }
    }
}
