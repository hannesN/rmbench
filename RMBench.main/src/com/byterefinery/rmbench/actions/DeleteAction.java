/*
 * created 29.07.2005 by sell
 *
 * $Id: DeleteAction.java 655 2007-08-30 23:09:58Z cse $
 */
package com.byterefinery.rmbench.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editors.DiagramEditor;
import com.byterefinery.rmbench.operations.CompoundOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;
import com.byterefinery.rmbench.util.Deleteable;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * An action to delete selected objects from a diagram editor
 * 
 * @author sell
 */
public class DeleteAction extends SelectionAction {

	/**
	 * identifier for the REMOVE action subtype. This subtype performs a
	 * "shallow" delete (e.g., only from the diagram, not from the schema)
	 */
	public static final String REMOVE = "remove";
	
	/**
	 * identifier for the DELETE action subtype. This subtype performs a
	 * "hard" delete (e.g., both from the diagram and the schema)
	 */
	public static final String DELETE = "delete";
	
	/**
	 * create a new action instance
	 * @param part the workbench part 
	 * @param subType the subtype, one of {@link #REMOVE} or {@link #DELETE}
	 */
	public DeleteAction(IWorkbenchPart part, String subType) {
		super(part);
		if(subType != REMOVE && subType != DELETE)
			throw new IllegalArgumentException("invalid subType");
        
		setLazyEnablementCalculation(false);
        setId(subType);
        
        if(subType == DELETE) {
            setText(Messages.Delete_Label);
            setToolTipText(Messages.Delete_Tooltip);
	        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
	        setImageDescriptor(
	        		sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		    setDisabledImageDescriptor(
		    		sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
        }
        else {
            setText(Messages.Remove_Label);
            setToolTipText(Messages.Remove_Tooltip);
	        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.REMOVE));
        }
	}

    public DeleteAction(final DiagramEditor editor, String subType) {
        this((IWorkbenchPart)editor, subType);
        setSelectionProvider(new ISelectionProvider() {

            public void addSelectionChangedListener(ISelectionChangedListener listener) {
                throw new UnsupportedOperationException();
            }
            public ISelection getSelection() {
                return editor.getViewer().getSelection();
            }
            public void removeSelectionChangedListener(ISelectionChangedListener listener) {
                throw new UnsupportedOperationException();
            }
            public void setSelection(ISelection selection) {
                throw new UnsupportedOperationException();
            }
        });
    }
    
    /**
     * Initializes this action's text and images.
     */
    protected void init() {
        super.init();
        setEnabled(false);
    }

	public void update() {
        super.update();
    }

    /**
	 * Returns <code>true</code> if the selected objects can be deleted.
	 * Returns <code>false</code> if there are no objects selected or the
	 * selected objects do not implement not {@link DeleteableEditPart}.
	 * 
	 * @return <code>true</code> if the command should be enabled
	 */
	protected boolean calculateEnabled() {
        int count = 0;
		for(Iterator<?> it=getSelectedObjects().iterator(); it.hasNext(); ) {
            Object next = it.next();
            if(next instanceof Deleteable) {
                count++;
                IUndoableOperation operation = getOperation((Deleteable)next);
                if(operation == null || !operation.canExecute())
                    return false;
            }
        }
        return count > 0;
	}

    private IUndoableOperation getOperation(Deleteable deleteable) {
        return (getId() == DELETE) ?
                deleteable.getDeleteOperation() : deleteable.getRemoveOperation();
    }

    private String getCompoundName() {
        return getId() == DELETE ? 
                Messages.Delete_GroupLabel :
                    Messages.Remove_GroupLabel;
    }
    
    /**
	 * Performs the delete action on the selected objects.
	 */
	public void run() {
        List<?> selectedObjects = getSelectedObjects();
        
        if(selectedObjects.size() == 1) {
            IUndoableOperation operation = getOperation((Deleteable)selectedObjects.get(0));
            RMBenchOperation.executeOperation(operation, this);
        } else {
            CompoundOperation compoundOperation = new CompoundOperation(getCompoundName());
            
            for(Iterator<?> it=selectedObjects.iterator(); it.hasNext(); ) {
                Object next = it.next();
                if(next instanceof Deleteable)
                    compoundOperation.add(getOperation((Deleteable)next));
            }
            compoundOperation.execute(this);
        }
	}
}
