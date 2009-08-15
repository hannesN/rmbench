/*
 * created 22.09.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableTypesActionGroup.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.ActionGroup;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.extension.TableTypeExtension;
import com.byterefinery.rmbench.operations.SetTableTypeOperation;

/**
 * action group that creates actions for assigning table types to the selected
 * tables
 * @author cse
 */
public class TableTypesActionGroup extends ActionGroup {

    private abstract class TableTypeAction extends Action {
        
        public TableTypeAction(String label) {
            super(label);
        }

        public void run() {
            for (Iterator<TableEditPart> it = selectedTables.iterator(); it.hasNext();) {
                TableEditPart tablePart = it.next();
                SetTableTypeOperation operation = 
                    new SetTableTypeOperation(tablePart.getTable(), getType());
                operation.execute(this);
            }
        }

        protected abstract String getType();
        public abstract boolean hasType(String oneType);
    }
    
    private class ExtensionTypeAction extends TableTypeAction {

        private final TableTypeExtension extension;
        
        public ExtensionTypeAction(TableTypeExtension extension) {
            super(extension.getLabel());
            this.extension = extension;
        }

        protected String getType() {
            return extension.getId();
        }

        public boolean hasType(String type) {
            return extension.getId().equals(type);
        }
    }
    
    private class NoTypeAction extends TableTypeAction {

        public NoTypeAction() {
            super(Messages.TableType_None);
        }
        
        protected String getType() {
            return null;
        }

        public boolean hasType(String type) {
            return type == null;
        }
    }
    
    private TableTypeAction[] actions;
    private final EditPartViewer editPartViewer;
    private final List<TableEditPart> selectedTables = new ArrayList<TableEditPart>();
    
    public TableTypesActionGroup(EditPartViewer viewer) {
        this.editPartViewer = viewer;
        
        TableTypeExtension[] extensions = RMBenchPlugin.getExtensionManager().getTableTypeExtensions();
        if(extensions.length > 0) {
            actions = new TableTypeAction[extensions.length+1];
            for (int i = 0; i < extensions.length; i++) {
                actions[i] = new ExtensionTypeAction(extensions[i]);
            }
            actions[extensions.length] = new NoTypeAction();
        }
    }
    
    public void fillContextMenu(IMenuManager menu) {
        if(actions == null)
            return;
        
        List<?> selection = editPartViewer.getSelectedEditParts();
        selectedTables.clear();
        for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
            Object next = iter.next();
            if(next instanceof TableEditPart)
                selectedTables.add((TableEditPart)next);
            else {
                selectedTables.clear();
                break;
            }
        }
        String oneType = "";
        if(selectedTables.size() == 1) {
            TableEditPart tablePart = (TableEditPart)selectedTables.get(0);
            oneType = tablePart.getTable().getType();
        }
        if(!selectedTables.isEmpty()) {
            MenuManager submenu = new MenuManager(Messages.TableTypesSubmenu_ActionLabelText);
            for (int i = 0; i < actions.length; i++) {
                actions[i].setEnabled(!actions[i].hasType(oneType));
                submenu.add(actions[i]);
            }
            menu.appendToGroup(GEFActionConstants.GROUP_EDIT, submenu);
        }
    }
}
