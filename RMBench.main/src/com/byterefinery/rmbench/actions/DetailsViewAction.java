/*
 * created 05.04.2005
 * 
 * $Id: DetailsViewAction.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.views.table.TableDetailsView;

/**
 * an action that will open the details view for the selected table
 * 
 * @author cse
 */
public class DetailsViewAction extends SelectionAction {

    public static final String ACTION_ID = "com.byterefinery.rmbench.edit_properties";
    
    public DetailsViewAction(IWorkbenchPart part) {
        super(part, AS_PUSH_BUTTON);
        setId(ACTION_ID);
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.TABLE_DETAILS));
        setText(Messages.TableDetails_Label);
        setToolTipText(Messages.TableDetails_Description);
    }

    //@see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
    protected boolean calculateEnabled() {
        if(getSelectedObjects().size() == 1) {
            return getSelectedObjects().get(0) instanceof TableEditPart;
        }
        return false;
    }

    public void run() {
        
        try {
            getWorkbenchPart().getSite().getPage().showView(TableDetailsView.VIEW_ID);
        }
        catch (PartInitException e) {
            RMBenchPlugin.logError(e);
        }
    }
}
