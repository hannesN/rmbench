/*
 * created 06.04.2005
 * 
 * $Id: PrimaryKeyTab.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.views.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Key;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;


/**
 * a tab that displays table primary key definition and allows in-place editing
 * 
 * @author cse
 */
public class PrimaryKeyTab extends DetailsTabGroup.DetailTab {
    
    private EventManager.Listener columnListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.owner != table)
                return;
            tableViewer.setInput(table);
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    CONSTRAINT_ADDED | CONSTRAINT_MODIFIED | CONSTRAINT_DELETED, this);
        }
    };
    
    private Column selectedColumn;
    
    private TableViewer tableViewer;

    private UpdateableAction[] actions = new UpdateableAction[2];

    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        org.eclipse.swt.widgets.Table table = 
            new org.eclipse.swt.widgets.Table(
                    parent,  SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        
        TableLayout layout = new TableLayout();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableColumn column;
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.PrimaryKeyTab_Column_Name);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth() * 4));
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.PrimaryKeyTab_Column_Index);
        column.pack();
        layout.addColumnData(new ColumnPixelData(column.getWidth()));
        
        table.setLayout(layout);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new LocalLabelProvider());
        tableViewer.setContentProvider(new ContentProvider());

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Column column = (Column)selection.getFirstElement();
                setSelectedColumn(column);
            }
        });

        actions[0] = new UpAction();
        actions[1] = new DownAction();
        
        setTable(this.table);
        columnListener.register();
    }

    public Action[] getActions() {
        return actions;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.PK_VIEW);
    }

    public String getTitle() {
        return RMBenchMessages.PrimaryKeyTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.PrimaryKeyTab_Description;
    }
    
    public void dispose() {
        RMBenchPlugin.getEventManager().removeListener(columnListener);
    }

    public void setTable(Table table) {
        super.setTable(table);
        if(this.tableViewer != null) {
            this.tableViewer.setInput(table);
            updateActions();
        }
    }
    
    private void setSelectedColumn(Column column) {
        if(column != selectedColumn) {
            selectedColumn = column;
            updateActions();
        }
    }
    
    private void updateActions() {
        for (int i = 0; i < actions.length; i++) {
            actions[i].update();
        }
    }
    
    private class LocalLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
                case 0: {
                    return ((Column)element).getName();
                }
                case 1: {
                    return String.valueOf(table.getPrimaryKey().getIndex((Column)element));
                }
            }
            return null;
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    private class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Key key = table.getPrimaryKey();
            return key != null ? key.getColumns() : new Object[0]; 
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class UpAction extends UpdateableAction {

        UpAction() {
            super();
            setText(RMBenchMessages.PrimaryKeyTab_Up_Text);
            setToolTipText(RMBenchMessages.PrimaryKeyTab_Up_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.UP));
        }
        
        public boolean isEnabled() {
            return selectedColumn != null && !table.getPrimaryKey().isFirst(selectedColumn);
        }

        public void run() {
            table.getPrimaryKey().moveColumn(selectedColumn, false);
            tableViewer.refresh();
            updateActions();
        }
    }
    
    private class DownAction extends UpdateableAction {
        
        DownAction() {
            super();
            setText(RMBenchMessages.PrimaryKeyTab_Down_Text);
            setToolTipText(RMBenchMessages.PrimaryKeyTab_Down_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.DOWN));
        }
        
        public boolean isEnabled() {
            return selectedColumn != null && !table.getPrimaryKey().isLast(selectedColumn);
        }

        public void run() {
            table.getPrimaryKey().moveColumn(selectedColumn, true);
            tableViewer.refresh();
            updateActions();
        }
    }
}
