/*
 * created 22.04.2005
 * 
 * $Id: RMBenchPerspective.java 633 2007-02-21 18:27:54Z hannesn $
 */
package com.byterefinery.rmbench;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import com.byterefinery.rmbench.dialogs.NewModelWizard;
import com.byterefinery.rmbench.views.db.ImportView;
import com.byterefinery.rmbench.views.dbtable.DBTableView;
import com.byterefinery.rmbench.views.model.ModelView;
import com.byterefinery.rmbench.views.table.TableDetailsView;

/**
 * the RMBench perspective
 * 
 * @author cse
 */
public class RMBenchPerspective implements IPerspectiveFactory {

    public static final String PERSPECTIVE_ID = "com.byterefinery.rmbench.perspective";
    
    //@see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
    public void createInitialLayout(IPageLayout layout) {
		setupVisual(layout);
		setupShortcuts(layout);
    }

	private void setupVisual(IPageLayout layout) {
		
        String editorArea = layout.getEditorArea();

        // Top left: Model View and Navigator, placeholder for Import
        IFolderLayout topLeft = layout.createFolder(
                "topLeft", IPageLayout.LEFT, 0.25f, editorArea);
        topLeft.addView(ModelView.VIEW_ID);
        topLeft.addView(IPageLayout.ID_RES_NAV);
        topLeft.addPlaceholder(ImportView.VIEW_ID);
        
        //Bottom left: Outline View
        IFolderLayout botLeft = layout.createFolder(
                "botLeft", IPageLayout.BOTTOM, 0.65f, "topLeft");
        botLeft.addView(IPageLayout.ID_OUTLINE);

        // Bottom right: Table Details and placeholders
        IFolderLayout bottomRight = layout.createFolder(
                "bottomRight", IPageLayout.BOTTOM, 0.70f, editorArea);
        bottomRight.addView(TableDetailsView.VIEW_ID);
        bottomRight.addPlaceholder(DBTableView.VIEW_ID);
        bottomRight.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
        bottomRight.addView(IPageLayout.ID_PROP_SHEET);
        bottomRight.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
        //error view of pde runtime
        bottomRight.addView("org.eclipse.pde.runtime.LogView");
	}
	
    private void setupShortcuts(IPageLayout layout) {

        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");	//$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");		//$NON-NLS-1$
        layout.addNewWizardShortcut(NewModelWizard.WIZARD_ID);

        layout.addShowViewShortcut(ModelView.VIEW_ID);
        layout.addShowViewShortcut(ImportView.VIEW_ID);
        layout.addShowViewShortcut(TableDetailsView.VIEW_ID);
        layout.addShowViewShortcut(DBTableView.VIEW_ID);
        
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        //error view of pde runtime
        layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
        layout.addActionSet("com.byterefinery.rmbench.actionSet");
    }
}
