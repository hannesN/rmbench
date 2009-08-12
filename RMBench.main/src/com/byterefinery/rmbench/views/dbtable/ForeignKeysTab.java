/*
 * created 09.08.2005 by sell
 *
 * $Id: ForeignKeysTab.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.dbtable;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.TreeLayout;

/**
 * Import table view tab for foreign keys
 * 
 * @author sell
 */
public class ForeignKeysTab implements DBTableView.DetailTab {

    private TreeViewer treeViewer;

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.FK_OUT);
    }

    public String getTitle() {
        return RMBenchMessages.ForeignkeysTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ForeignkeysTab_Description;
    }

    public void createControl(Composite parent) {
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
        layout.addColumnData(new ColumnWeightData(40));
        
        tree.setLayout(layout);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(tree);
        treeViewer.setLabelProvider(new LabelProvider(){
            
        });
        treeViewer.setContentProvider(new ContentProvider());
    }

    public void setTable(DBTable table) {
        treeViewer.setInput(table);
    }

    private class LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0: {
                    if(element instanceof DBForeignKey)
                        return ((DBForeignKey)element).name;
                    break;
                }
                case 1: {
                    if(element instanceof String)
                        return (String)element;
                    break;
                }
                case 2: {
                    if(element instanceof DBForeignKey)
                        return ((DBForeignKey)element).targetTable;
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
            return ((DBForeignKey)parentElement).getColumns();
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof DBForeignKey;
        }

        public Object[] getElements(Object inputElement) {
            DBTable table = (DBTable)inputElement;
            return table != null ? table.getForeignKeys().toArray() : new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
}
