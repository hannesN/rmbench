/*
 * created 08.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnsTab.java 512 2006-08-26 16:52:02Z cse $
 */
package com.byterefinery.rmbench.views.dbtable;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.dbimport.DBColumn;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.TableSorter;

/**
 * Import table view tab for columns
 * 
 * @author cse
 */
class ColumnsTab implements DBTableView.DetailTab {

    private DBTable dbtable;
    private TableViewer tableViewer;
    
    private TableSorter entrySorter;
    
    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.COL_VIEW);
    }

    public String getTitle() {
        return RMBenchMessages.ColumnsTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ColumnsTab_Description;
    }

    public void createControl(Composite parent) {
        final org.eclipse.swt.widgets.Table table = 
            new org.eclipse.swt.widgets.Table(
                    parent,  SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        
        TableLayout layout = new TableLayout();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Name);
        layout.addColumnData(new ColumnWeightData(30));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_DataType);
        layout.addColumnData(new ColumnWeightData(20));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_TypeID);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Precision);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Scale);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_NotNull);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_PKIndex);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Default);
        layout.addColumnData(new ColumnWeightData(15));
        column.addSelectionListener(headerListener);
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ColumnsTab_Column_Comment);
        layout.addColumnData(new ColumnWeightData(25));
        column.addSelectionListener(headerListener);
        
        table.setLayout(layout);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(new ArrayContentProvider());

        entrySorter = new TableSorter(labelProvider);
    }

    public void setTable(DBTable table) {
        dbtable = table;
        if(table != null) {
            entrySorter.setEntries(table.getColumns());
            tableViewer.setInput(entrySorter.getEntries());
        }
        else {
            entrySorter.setEntries(null);
            tableViewer.setInput(null);
        }
    }
    
    private SelectionListener headerListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) {
            
            int column = tableViewer.getTable().indexOf((TableColumn) e.widget);
            
            if (column == entrySorter.getSortColumn())
                entrySorter.reverseOrder();
            else {
                entrySorter.setSortColumn(column);
            }
            tableViewer.refresh();
        }
    };
    
    private ITableLabelProvider labelProvider = new ITableLabelProvider() {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            DBColumn column = (DBColumn)element;
            switch (columnIndex) {
            case 0:
                return column.name;
            case 1:
                return column.typeName;
            case 2:
                return column.typeID;
            case 3:
                return String.valueOf(column.precision);
            case 4:
                return String.valueOf(column.scale);
            case 5:
                return column.nullable ? "" : "Y";
            case 6: {
                int index = dbtable.getPrimaryKey() != null ? 
                        dbtable.getPrimaryKey().getIndex(column.name) : -1;
                return index > 0 ? String.valueOf(index) : "";
            }
            case 7:
                return column.defaultValue;
            case 8:
                return column.comment;
            default:
                return null;
            }
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
    };
}
