/*
 * created 26.09.2005 by sell
 *
 * $Id: CutDiagramTablesAction.java 655 2007-08-30 23:09:58Z cse $
 */
package com.byterefinery.rmbench.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.operations.CompoundOperation;

/**
 * an action for deleting the selected tables from the current diagram and copying them 
 * to the clipboard
 * 
 * @author sell
 */
public class CutDiagramTablesAction extends SelectionAction {

    public CutDiagramTablesAction(IWorkbenchPart part) {
        super(part);
        setId(ActionFactory.CUT.getId());
        setText(Messages.Cut_Label);
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
        setDisabledImageDescriptor(
                sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
    }

    public void run() {
        List<?> selected = getSelectedObjects();
        TableEditPart[] editParts = (TableEditPart[])selected.toArray(new TableEditPart[selected.size()]);
        runDeleteOperation(editParts);
        Clipboard.getDefault().setContents(editParts);
    }

    private void runDeleteOperation(TableEditPart[] editParts) {
        CompoundOperation compoundOperation = new CompoundOperation(Messages.Cut_Label);
        
        for (int i = 0; i < editParts.length; i++)
            compoundOperation.add(editParts[i].getRemoveOperation());
        
        compoundOperation.execute(this);
    }

    protected boolean calculateEnabled() {
        int count = 0;
        for (Iterator<?> it = getSelectedObjects().iterator(); it.hasNext();) {
            if(!(it.next() instanceof TableEditPart))
                return false;
            count++;
        }
        return count > 0;
    }
}
