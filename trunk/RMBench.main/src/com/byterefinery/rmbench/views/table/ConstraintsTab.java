/*
 * created 06.04.2005
 * 
 * $Id: ConstraintsTab.java 656 2007-08-31 00:11:31Z cse $
 */
package com.byterefinery.rmbench.views.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
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
import org.eclipse.jface.wizard.WizardDialog;
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
import com.byterefinery.rmbench.dialogs.CheckConstraintEditorDialog;
import com.byterefinery.rmbench.dialogs.KeyEditorDialog;
import com.byterefinery.rmbench.dialogs.TableConstraintWizard;
import com.byterefinery.rmbench.model.schema.CheckConstraint;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.Key;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.DeleteTableConstraintOperation;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.UpdateableAction;


/**
 * a tab that displays the table constraints (except foreign keys and primary key)
 * 
 * @author cse
 */
public class ConstraintsTab extends DetailsTabGroup.DetailTab {
    
    private EventManager.Listener constraintsListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.owner != table)
                return;
            
            Constraint constraint = (Constraint)event.element;
            switch(eventType) {
                case CONSTRAINT_ADDED: {
                    tableViewer.add(constraint);
                    break;
                }
                case CONSTRAINT_DELETED: {
                    tableViewer.remove(constraint);
                    if(constraint == selectedConstraint)
                        setSelectedConstraint(null);
                    break;
                }
                case CONSTRAINT_MODIFIED: {
                    tableViewer.update(constraint, null);
                    break;
                }
            }
        }
        public void register() {
            RMBenchPlugin.getEventManager().addListener(
                    CONSTRAINT_DELETED | CONSTRAINT_ADDED | CONSTRAINT_MODIFIED, this);
        }
    };
    
    private Constraint selectedConstraint;
    
    private TableViewer tableViewer;
    private final UpdateableAction[] actions = new UpdateableAction[3];

    
    public void createControl(Composite parent, IActionBars actionBars) {
        
        org.eclipse.swt.widgets.Table table = 
            new org.eclipse.swt.widgets.Table(
                    parent,  SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        
        TableLayout layout = new TableLayout();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableColumn column;
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ConstraintsTab_Column_Name);
        layout.addColumnData(new ColumnWeightData(20));
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ConstraintsTab_Column_Type);
        layout.addColumnData(new ColumnWeightData(10));
        
        column = new TableColumn(table, SWT.NONE);
        column.setText(RMBenchMessages.ConstraintsTab_Column_Definition);
        layout.addColumnData(new ColumnWeightData(40));
        
        table.setLayout(layout);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new LocalLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Constraint constraint = (Constraint)selection.getFirstElement();
                setSelectedConstraint(constraint);
            }
        });
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                actions[0].run();
            }
        });
        
        actions[0] = new EditAction();
        actions[1] = new AddAction();
        actions[2] = new DeleteAction();
        
        setTable(this.table);
        constraintsListener.register();
    }

    public Action[] getActions() {
        return actions;
    }

    public Image getImage() {
        return RMBenchPlugin.getImage(ImageConstants.CONSTRAINT);
    }

    public String getTitle() {
        return RMBenchMessages.ConstraintsTab_Title;
    }

    public String getDescription() {
        return RMBenchMessages.ConstraintsTab_Description;
    }
    
    public void dispose() {
        constraintsListener.unregister();
    }

    public void setTable(Table table) {
        super.setTable(table);
        if(this.tableViewer != null) {
            if(table != null) {
                List<Constraint> constraints = new ArrayList<Constraint>(table.getUniqueConstraints());
                constraints.addAll(table.getCheckConstraints());
                if(table.getPrimaryKey() != null)
                    constraints.add(table.getPrimaryKey());
                this.tableViewer.setInput(constraints);
            }
            else
                this.tableViewer.setInput(null);
            updateActions();
        }
    }
    
    private void setSelectedConstraint(Constraint constraint) {
        if(constraint != selectedConstraint) {
            selectedConstraint = constraint;
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
                    return ((Constraint)element).getName();
                }
                case 1: {
                    return ((Constraint)element).getConstraintType();
                }
                case 2: {
                    return ((Constraint)element).getConstraintBody();
                }
            }
            return null;
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class EditAction extends UpdateableAction {

        EditAction() {
            super();
            setText(RMBenchMessages.ConstraintsTab_Edit_Text);
            setToolTipText(RMBenchMessages.ConstraintsTab_Edit_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.EDIT_disabled));
        }
        
        public boolean isEnabled() {
            return selectedConstraint != null;
        }

        public void run() {
            if(selectedConstraint instanceof Key) {
                KeyEditorDialog dialog = 
                    new KeyEditorDialog(getShell(), (Key)selectedConstraint);
                dialog.open();
            } else if (selectedConstraint instanceof CheckConstraint) {
                CheckConstraintEditorDialog dialog = new CheckConstraintEditorDialog(getShell(), (CheckConstraint) selectedConstraint);
                dialog.open();
            }
        }
    }
    
    private class AddAction extends UpdateableAction {

        AddAction() {
            super();
            setText(RMBenchMessages.ConstraintsTab_Add_Text);
            setToolTipText(RMBenchMessages.ConstraintsTab_Add_Description);
            setImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD));
            setDisabledImageDescriptor(
                    RMBenchPlugin.getImageDescriptor(ImageConstants.ADD_disabled));
        }
        
        public boolean isEnabled() {
            return table != null && !table.getColumns().isEmpty();
        }

        public void run() {
            WizardDialog dialog = new WizardDialog(getShell(), new TableConstraintWizard(table));
            dialog.open();
        }
    }
    
    private class DeleteAction extends UpdateableAction {
        
        DeleteAction() {
            super();
            setText(RMBenchMessages.ConstraintsTab_Delete_Text);
            setToolTipText(RMBenchMessages.ConstraintsTab_Delete_Description);
            setImageDescriptor(ImageConstants.DELETE_DESC);
            setDisabledImageDescriptor(ImageConstants.DELETE_DISABLED_DESC);
        }
        
        public boolean isEnabled() {
            return selectedConstraint != null;
        }

        public void run() {
            DeleteTableConstraintOperation operation = 
                new DeleteTableConstraintOperation(table, selectedConstraint);
            operation.execute(this);
        }
    }
}
