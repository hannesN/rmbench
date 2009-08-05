/*
 * created 11.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DiagramActionBarContributor.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.EditorActionBarContributor;

import com.byterefinery.rmbench.actions.DiagramExportAction;
import com.byterefinery.rmbench.actions.LayoutFiguresAction;
import com.byterefinery.rmbench.actions.LayoutFiguresRetargetAction;
import com.byterefinery.rmbench.actions.Messages;
import com.byterefinery.rmbench.actions.PageOutlineAction;
import com.byterefinery.rmbench.actions.PrinterSetupAction;

/**
 * @author cse
 */
public class DiagramActionBarContributor extends EditorActionBarContributor {

    private ActionRegistry registry = new ActionRegistry();
    private List<RetargetAction> retargetActions = new ArrayList<RetargetAction>();
    private List<String> globalActionKeys = new ArrayList<String>();
    
    private IEditorPart activeEditorPart;
    
    public void init(IActionBars bars) {
        
        addRetargetAction(new DeleteRetargetAction());
        addRetargetAction(new LayoutFiguresRetargetAction());

        addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));
        
        addRetargetAction(new ZoomInRetargetAction());
        addRetargetAction(new ZoomOutRetargetAction());

        addRetargetAction(
                new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, 
                        Messages.ToggleGrid_Label, IAction.AS_CHECK_BOX));
        addRetargetAction(
                new RetargetAction(PageOutlineAction.ID, 
                        Messages.PageOutlineAction_Title, IAction.AS_CHECK_BOX));
        addRetargetAction(
                new RetargetAction(PrinterSetupAction.ID, 
                        Messages.PrinterSetupAction_Title, IAction.AS_PUSH_BUTTON));
        addRetargetAction(
                new RetargetAction(DiagramExportAction.ID, 
                        Messages.DiagramExportAction_Title, IAction.AS_PUSH_BUTTON));

        addGlobalActionKey(ActionFactory.PRINT.getId());
        addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
        addGlobalActionKey(ActionFactory.CUT.getId());
        addGlobalActionKey(ActionFactory.COPY.getId());
        addGlobalActionKey(ActionFactory.PASTE.getId());

        super.init(bars);
    }

    public void setActiveEditor(IEditorPart part) {
        if (activeEditorPart == part)
            return;

        activeEditorPart = part;
        if(activeEditorPart != null) {
            ActionRegistry registry = 
                (ActionRegistry)activeEditorPart.getAdapter(ActionRegistry.class);
            
            IActionBars bars = getActionBars();
            for (int i = 0; i < globalActionKeys.size(); i++) {
                String id = (String)globalActionKeys.get(i);
                bars.setGlobalActionHandler(id, registry.getAction(id));
            }
            getActionBars().updateActionBars();
        }
    }
    
    protected void addRetargetAction(RetargetAction action) {
        registry.registerAction(action);
        retargetActions.add(action);
        getPage().addPartListener(action);
        addGlobalActionKey(action.getId());
    }
    
    protected void addAction(IAction action) {
        registry.registerAction(action);
    }
    
    protected void addGlobalActionKey(String key) {
        globalActionKeys.add(key);
    }
    
    protected IAction getAction(String id) {
        return registry.getAction(id);
    }
    
    public void dispose() {
        for (RetargetAction action : retargetActions) {
            action.dispose();
        }
        registry.dispose();
        retargetActions = null;
        registry = null;
    }
    
    public void contributeToMenu(IMenuManager manager) {

        MenuManager viewMenu = new MenuManager(Messages.DiagramMenu_Label);
        viewMenu.add(getAction(DiagramExportAction.ID));
        viewMenu.add(getAction(PrinterSetupAction.ID));
        viewMenu.add(new Separator());
        viewMenu.add(getAction(LayoutFiguresAction.ACTION_ID));
        viewMenu.add(new Separator());
        viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
        viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
        viewMenu.add(new Separator());
        viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
        viewMenu.add(getAction(PageOutlineAction.ID));
        manager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
    }
    
    public void contributeToToolBar(IToolBarManager tbm) {
        
        tbm.add(new Separator());
        tbm.add(getAction(LayoutFiguresAction.ACTION_ID));
        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.ALIGN_LEFT));
        tbm.add(getAction(GEFActionConstants.ALIGN_CENTER));
        tbm.add(getAction(GEFActionConstants.ALIGN_RIGHT));
        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.ALIGN_TOP));
        tbm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
        tbm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));
        tbm.add(new Separator());  
        
        
        
        tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
        String[] zoomStrings = new String[] {
                ZoomManager.FIT_ALL, 
                ZoomManager.FIT_HEIGHT, 
                ZoomManager.FIT_WIDTH
                };
        tbm.add(new ZoomComboContributionItem(getPage(), zoomStrings));
        
        tbm.add(getAction(GEFActionConstants.ZOOM_IN));
    }
}
