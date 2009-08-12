/*
 * created 18.08.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ForeignKeyConfigurator.java 667 2007-10-02 18:54:16Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a dialog thhat allows to configure a foreign key, by specifying the key columns 
 * and the target table
 * 
 * @author cse
 */
public class ForeignKeyConfigurator extends Dialog {

    private final Table sourceTable;
    private Table targetTable;
    
    
    private CheckboxTableHandler viewerHandler;
    private CheckboxTableViewer columnsViewer;
    private TableViewer tablesViewer;
    
    public ForeignKeyConfigurator(Shell parentShell, Table sourceTable) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
                | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
        
        this.sourceTable = sourceTable;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.ForeignKeyConfigurator_title);
    }
    
    protected Control createDialogArea(Composite container) {
        
        Composite parent = (Composite) super.createDialogArea(container);
        
        Composite mainGroup = new Composite(parent, SWT.NONE);
        mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainGroup.setLayout(new GridLayout(3, false));
        
        viewerHandler = new CheckboxTableHandler(mainGroup);
        
        Label columnsLabel = new Label(mainGroup, SWT.NONE);
        columnsLabel.setText(Messages.ForeignKeyConfigurator_Columns);
        Label tablesLabel = new Label(mainGroup, SWT.NONE);
        tablesLabel.setText(Messages.ForeignKeyConfigurator_TargetTable);
        
        columnsViewer = CheckboxTableViewer.newCheckList(mainGroup, SWT.SINGLE | SWT.BORDER);
        columnsViewer.getTable().setLayoutData(createTableGridData());
        columnsViewer.setLabelProvider(new LabelProvider() {
            public Image getImage(Object element) {
                if(((Column)element).belongsToPrimaryKey())
                    return RMBenchPlugin.getImage(ImageConstants.KEY);
                return null;
            }
            public String getText(Object element) {
                return ((Column)element).getName();
            }
        });
        viewerHandler.setViewer(columnsViewer, sourceTable.getColumns());
        viewerHandler.addListener(new CheckboxTableHandler.Listener() {
            public void checkMoved(Column column, int oldIndex, int newIndex) {
                computeTargetTables();
            }
            public void checkCountChanged() {
                computeTargetTables();
            }
        });
        
        org.eclipse.swt.widgets.Table viewerTable = 
            new org.eclipse.swt.widgets.Table(mainGroup,  SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        
        TableLayout tableLayout = new TableLayout();
        TableColumn column;
        
        column = new TableColumn(viewerTable, SWT.NONE);
        column.setText(Messages.ForeignKeyConfigurator_Table_Schema);
        tableLayout.addColumnData(new ColumnWeightData(10));
        column = new TableColumn(viewerTable, SWT.NONE);
        column.setText(Messages.ForeignKeyConfigurator_Table_Name);
        tableLayout.addColumnData(new ColumnWeightData(20));
        
        viewerTable.setLayout(tableLayout);
        viewerTable.setLayoutData(createTableGridData());
        
        tablesViewer = new TableViewer(viewerTable);
        tablesViewer.setContentProvider(new ArrayContentProvider());
        tablesViewer.setLabelProvider(new TablesViewerLabelProvider());
        tablesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                if(event.getSelection().isEmpty()) {
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                }
                else {
                    IStructuredSelection selection = (IStructuredSelection)tablesViewer.getSelection();
                    targetTable = (Table)selection.getFirstElement();
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                }
            }
        });
        
        return parent;
    }
    
    private void computeTargetTables() {
        if(viewerHandler.hasChecked()) {
            Model model = RMBenchPlugin.getActiveModel();
            List<Table> tables = model.findMatchingTables(viewerHandler.getCheckedColumns());
            tablesViewer.setInput(tables);
        }
        else {
            tablesViewer.setInput(null);
        }
    }

    private GridData createTableGridData() {
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = convertHeightInCharsToPixels(15);
        gd.widthHint = convertWidthInCharsToPixels(28);
        return gd;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    public Table getTargetTable() {
        return targetTable;
    }
    
    public Column[] getKeyColumns() {
        return viewerHandler.getCheckedColumns();
    }
    
    private class TablesViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            Table table = (Table)element;
            return columnIndex == 0 ? table.getSchema().getName() : table.getName();
        }
    }
}
