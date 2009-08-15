/*
 * created 09.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IndexesTab.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.views.dbtable;

import org.eclipse.jface.viewers.ColumnPixelData;
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
import com.byterefinery.rmbench.model.dbimport.DBIndex;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.TreeLayout;

/**
 * Import table view tab for indexes
 * 
 * @author cse
 */
public class IndexesTab implements DBTableView.DetailTab {

    private TreeViewer treeViewer;
    
    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.INDEX);
    }

    public String getTitle() {
        return RMBenchMessages.IndexesTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.IndexesTab_Description;
    }

    public void createControl(Composite parent) {
        
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
                    if(element instanceof DBIndex)
                        return ((DBIndex)element).name;
                    break;
                }
                case 1: {
                    if(element instanceof DBIndex)
                        return ((DBIndex)element).unique ? "Y" : "N";
                    break;
                }
                case 2: {
                    if(element instanceof DBIndex.IndexColumn)
                        return ((DBIndex.IndexColumn)element).name;
                    break;
                }
                case 3: {
                    if(element instanceof DBIndex.IndexColumn) {
                        DBIndex.IndexColumn col = (DBIndex.IndexColumn)element;
                        return col.ascending ? "ASC" : "DESC";
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
            return ((DBIndex)parentElement).getColumns();
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof DBIndex;
        }

        public Object[] getElements(Object inputElement) {
            return ((DBTable)inputElement).getIndexes().toArray();
        }

        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
}
