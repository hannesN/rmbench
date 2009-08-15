/*
 * created 23.08.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: CheckboxTableHandler.java 656 2007-08-31 00:11:31Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a helper class that will create a button group consisting of 2 buttons arranged 
 * vertically in a composite, and which operate on a CheckboxTableViewer to rearrange 
 * the checked lines. The class also provides the ContentProvider and CheckedStateListener
 * for the viewer that maintain the checked items.<p/>
 * 
 * <em class="note">This class is the result of factoring out commonalities from 2 dialogs. 
 * It was not designed by itself from the ground up</em> 
 * 
 * @author cse
 */
public class CheckboxTableHandler {

    public interface Listener {
        void checkMoved(Column column, int oldIndex, int newIndex);
        void checkCountChanged();
    }
    
    private final List<Listener> listeners = new ArrayList<Listener>(2);
    
    private CheckboxTableViewer columnsViewer;
    private List<Column> tableColumns;
    private List<Column> keyColumns;
    
    private Button upButton, downButton;

    /**
     * create the visual representation. Note that this must be followed by a call to
     * {@link #setViewer(CheckboxTableViewer, List, Column[])}
     * 
     * @param parent
     */
    public CheckboxTableHandler(Composite parent) {
        
        Composite buttonGroup = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, true);
        gd.verticalSpan = 2;
        buttonGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonGroup.setLayout(layout);
        
        upButton = new Button(buttonGroup, SWT.PUSH);
        upButton.setImage(RMBenchPlugin.getImage(ImageConstants.UP));
        upButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
        upButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = columnsViewer.getTable().getSelectionIndex();
                moveKeyColumn(index, -1);
            }
        });
        upButton.setEnabled(false);
        
        downButton = new Button(buttonGroup, SWT.PUSH);
        downButton.setImage(RMBenchPlugin.getImage(ImageConstants.DOWN));
        downButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        downButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = columnsViewer.getTable().getSelectionIndex();
                moveKeyColumn(index, 1);
            }
        });
        downButton.setEnabled(false);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    protected void moveKeyColumn(int index, int offset) {
        
        Column column = (Column)keyColumns.remove(index);
        keyColumns.add(index+offset, column);
        columnsViewer.refresh();
        for (Iterator<Listener> it = listeners.iterator(); it.hasNext();) {
            Listener listener = it.next();
            listener.checkMoved(column, index, index+offset);
        }
        updateButtonState();
    }

    protected void updateButtonState() {
        IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
        Column column = (Column)selection.getFirstElement();
        if(columnsViewer.getChecked(column)) {
            int index = keyColumns.indexOf(column);
            upButton.setEnabled(index > 0);
            downButton.setEnabled(index < keyColumns.size()-1);
        }
        else {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }

    /**
     * configure the viewer this object operates upon.
     * 
     * @param columnsViewer the viewer
     * @param tableColumns the table columns
     * @param checkedColumns pre-checked columns
     */
    public void setViewer(CheckboxTableViewer columnsViewer, List<Column> tableColumns) {
        setViewer(columnsViewer, tableColumns, null);
    }
    
    /**
     * configure the viewer this object operates upon.
     * 
     * @param columnsViewer the viewer
     * @param tableColumns the table columns
     */
    public void setViewer(CheckboxTableViewer columnsViewer, List<Column> tableColumns, Column[] checkedColumns) {
        this.columnsViewer = columnsViewer;
        this.tableColumns = new ArrayList<Column>(tableColumns);
        this.keyColumns = new ArrayList<Column>();
        if(checkedColumns != null) {
            for (int i = 0; i < checkedColumns.length; i++) {
                this.keyColumns.add(checkedColumns[i]);
                this.tableColumns.remove(checkedColumns[i]);
            }
        }
        
        columnsViewer.addCheckStateListener(new LocalCheckStateListener());
        columnsViewer.setContentProvider(new LocalContentProvider());
        columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                updateButtonState();
            }
        });
        columnsViewer.setInput(tableColumns);
        if(checkedColumns != null)
            columnsViewer.setCheckedElements(checkedColumns);
    }
    
    /**
     * @return whether any columns have been checked
     */
    public boolean hasChecked() {
        return keyColumns.size() > 0;
    }
    
    /**
     * @return the currently checked columns
     */
    public Column[] getCheckedColumns() {
        return (Column[])keyColumns.toArray(new Column[keyColumns.size()]);
    }
    
    /**
     * @return an Iterator over the checked columns
     */
    public Iterator<Column> checkedColumns() {
        return keyColumns.iterator();
    }
    
    private class LocalCheckStateListener implements ICheckStateListener {
        
        public void checkStateChanged(CheckStateChangedEvent event) {
            if(event.getChecked()) {
                keyColumns.add((Column)event.getElement());
                tableColumns.remove(event.getElement());
            }
            else {
                keyColumns.remove(event.getElement());
                tableColumns.add(0, (Column)event.getElement());
            }
            columnsViewer.refresh();
            
            for (Iterator<Listener> it = listeners.iterator(); it.hasNext();) {
                Listener listener = it.next();
                listener.checkCountChanged();
            }
        }
    }
        
    private class LocalContentProvider implements IStructuredContentProvider {
        
        public Object[] getElements(Object inputElement) {
            
            if(keyColumns.isEmpty())
                return tableColumns.toArray();
            
            List<Column> elements = new ArrayList<Column>(keyColumns);
            elements.addAll(tableColumns);
            return elements.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
}
