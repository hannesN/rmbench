/*
 * created 14.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TargetTableChooser.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a dialog for choosing the foreign key target table
 * @author cse
 */
public class TargetTableChooser extends TrayDialog {

    private final List<Table> tables;
    private final Diagram diagram;
    
    private Table resultTable;
    private boolean doImport;
    
    private TableViewer tableViewer;
    private Button importCheck;
    
    public TargetTableChooser(Shell parentShell, List<Table> tables, Diagram diagram) {
        super(parentShell);
        this.diagram = diagram;
        this.tables = tables;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.TargetTableChooser_title);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
        		shell, RMBenchConstants.HelpContexts.TargetTableChooser);
        setHelpAvailable(true);
    }
    
    protected Control createDialogArea(Composite container) {
        Composite parent = (Composite) super.createDialogArea(container);
        
        Label targetLabel = new Label(parent, SWT.NONE);
        targetLabel.setText(Messages.TargetTableChooser_message);
        
        org.eclipse.swt.widgets.Table viewerTable = new org.eclipse.swt.widgets.Table(
                parent, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        
        TableLayout tableLayout = new TableLayout();
        TableColumn column;
        
        column = new TableColumn(viewerTable, SWT.NONE);
        column.setText(Messages.ForeignKeyConfigurator_Table_Schema);
        tableLayout.addColumnData(new ColumnWeightData(10));
        column = new TableColumn(viewerTable, SWT.NONE);
        column.setText(Messages.ForeignKeyConfigurator_Table_Name);
        tableLayout.addColumnData(new ColumnWeightData(20));
        
        viewerTable.setLayout(tableLayout);
        
        tableViewer = new TableViewer(viewerTable);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setLabelProvider(new TablesViewerLabelProvider());
        tableViewer.setInput(tables);
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                Table selectedTable = 
                    (Table)((IStructuredSelection)event.getSelection()).getFirstElement();
                boolean needsImport = !diagram.containsTable(selectedTable);
                if(needsImport) {
                    importCheck.setEnabled(true);
                    importCheck.setSelection(true);
                    doImport = true;
                }
                else {
                    importCheck.setEnabled(false);
                    doImport = false;
                }
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            }
        });
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }
        });
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = convertHeightInCharsToPixels(15);
        gd.widthHint = convertWidthInCharsToPixels(55);
        tableViewer.getTable().setLayoutData(gd);
        tableViewer.getTable().setFont(container.getFont());
        
        importCheck = new Button(parent, SWT.CHECK);
        importCheck.setText(Messages.TargetTableChooser_importCheck);
        importCheck.setEnabled(false);
        importCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                doImport = importCheck.getSelection();
            }
        });
        
        return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    protected void okPressed() {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        resultTable = (Table)selection.getFirstElement();
        super.okPressed();
    }
    
    public Table getResultTable() {
        return resultTable;
    }
    
    public boolean getDoImport() {
        return doImport;
    }

    private class TablesViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            Table table = (Table)element;
            return columnIndex == 0 ? 
            		table.getSchema().getName() : table.getName();
        }
    }
}
