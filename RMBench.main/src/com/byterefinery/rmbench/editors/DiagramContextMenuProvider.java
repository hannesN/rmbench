/*
 * created 05.04.2005
 * 
 * $Id:DiagramContextMenuProvider.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.actions.AddStubbedTablesAction;
import com.byterefinery.rmbench.actions.DeleteAction;
import com.byterefinery.rmbench.actions.DetailsViewAction;
import com.byterefinery.rmbench.actions.ForeignKeyAction;
import com.byterefinery.rmbench.actions.TableTypesActionGroup;
import com.byterefinery.rmbench.actions.TablesDiagramAction;
import com.byterefinery.rmbench.operations.UndoRedoActionGroup;

/**
 * context menu provider for graphical editors
 * 
 * @author cse
 */
public class DiagramContextMenuProvider extends ContextMenuProvider {

    private final ActionRegistry registry;
    private final UndoRedoActionGroup undoRedoGroup;
    private final TableTypesActionGroup typesActionGroup;
    
    public DiagramContextMenuProvider(
            EditPartViewer viewer, 
            ActionRegistry registry, 
            UndoRedoActionGroup undoRedoGroup) {
        
        super(viewer);
        this.registry = registry;
        this.undoRedoGroup = undoRedoGroup;
        this.typesActionGroup = new TableTypesActionGroup(viewer);
    }

    public void buildContextMenu(IMenuManager manager) {
        GEFActionConstants.addStandardActionGroups(manager);

        IAction action;

        undoRedoGroup.fillContextMenu(manager);
        typesActionGroup.fillContextMenu(manager);
        
        action = registry.getAction(ActionFactory.CUT.getId());
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_COPY, action);

        action = registry.getAction(ActionFactory.COPY.getId());
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_COPY, action);

        action = registry.getAction(ActionFactory.PASTE.getId());
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_COPY, action);

        action = registry.getAction(TablesDiagramAction.ACTION_ID);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_COPY, action);
        
        action = registry.getAction(ForeignKeyAction.ACTION_ID);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        action = registry.getAction(DeleteAction.DELETE);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        action = registry.getAction(AddStubbedTablesAction.ACTION_ID);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

        action = registry.getAction(DeleteAction.REMOVE);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        
        // Alignment Actions
        MenuManager submenu = new MenuManager(RMBenchMessages.AlignSubmenu_ActionLabelText);

        action = registry.getAction(GEFActionConstants.ALIGN_LEFT);
        if (action.isEnabled())
            submenu.add(action);

        action = registry.getAction(GEFActionConstants.ALIGN_CENTER);
        if (action.isEnabled())
            submenu.add(action);

        action = registry.getAction(GEFActionConstants.ALIGN_RIGHT);
        if (action.isEnabled())
            submenu.add(action);
            
        submenu.add(new Separator());
        
        action = registry.getAction(GEFActionConstants.ALIGN_TOP);
        if (action.isEnabled())
            submenu.add(action);

        action = registry.getAction(GEFActionConstants.ALIGN_MIDDLE);
        if (action.isEnabled())
            submenu.add(action);

        action = registry.getAction(GEFActionConstants.ALIGN_BOTTOM);
        if (action.isEnabled())
            submenu.add(action);

        if (!submenu.isEmpty())
            manager.appendToGroup(GEFActionConstants.GROUP_REST, submenu);

        action = registry.getAction(DetailsViewAction.ACTION_ID);
        if (action.isEnabled())
            manager.appendToGroup(GEFActionConstants.GROUP_REST, action);

        action = registry.getAction(ActionFactory.SAVE.getId());
        manager.appendToGroup(GEFActionConstants.GROUP_SAVE, action);
    }
}
