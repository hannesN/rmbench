/*
 * created 26.09.2005 by sell
 *
 * $Id: CopyDiagramTablesAction.java 655 2007-08-30 23:09:58Z cse $
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

/**
 * an action for copying the selected tables from the current diagram
 * 
 * @author sell
 */
public class CopyDiagramTablesAction extends SelectionAction {

    public CopyDiagramTablesAction(IWorkbenchPart part) {
        super(part);
        setId(ActionFactory.COPY.getId());
        setText(Messages.Copy_Label);
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(
                sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
    }

    public void run() {
        List<?> selected = getSelectedObjects();
        TableEditPart[] editParts = (TableEditPart[])selected.toArray(new TableEditPart[selected.size()]);
        Clipboard.getDefault().setContents(editParts);
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
