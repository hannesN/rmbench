/*
 * created 04.08.2005 by sell
 *
 * $Id: TableContainerEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.wizard.WizardDialog;

import com.byterefinery.rmbench.dialogs.IndexEditorDialog;
import com.byterefinery.rmbench.dialogs.TableConstraintWizard;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a container policy for creating child parts within a table editpart. Currently,
 * only indexes can be created as children of a table part.
 * 
 * @author sell
 */
public class TableContainerEditPolicy extends ContainerEditPolicy {

    protected Command getCreateCommand(CreateRequest request) {
        if(ComponentFactory.INDEX == request.getNewObjectType()) {
            TableEditPart tablePart = (TableEditPart)getHost();
            if(tablePart.getTable().getColumns().size() > 0) {
                return new AddIndexCommand(tablePart.getTable());
            }
        }
        else if(ComponentFactory.CONSTRAINT == request.getNewObjectType()) {
            TableEditPart tablePart = (TableEditPart)getHost();
            if(tablePart.getTable().getColumns().size() > 0) {
                return new AddConstraintCommand(tablePart.getTable());
            }
        }
        return null;
    }

    public EditPart getTargetEditPart(Request request) {
        if (REQ_CREATE.equals(request.getType()))
            return getHost();
        return null;
    }
    
    private class AddIndexCommand extends Command {

        private final Table table;
        
        AddIndexCommand(Table table) {
            this.table = table;
        }
        
        public void execute() {
            IndexEditorDialog indexEditor = new IndexEditorDialog(
                    getHost().getViewer().getControl().getShell(), table);
            indexEditor.open();
        }

        public void redo() {
            throw new UnsupportedOperationException();
        }

        public void undo() {
            throw new UnsupportedOperationException();
        }
    }

    private class AddConstraintCommand extends Command {

        private final Table table;
        
        AddConstraintCommand(Table table) {
            this.table = table;
        }
        
        public void execute() {
            WizardDialog dialog = new WizardDialog(
                    getHost().getViewer().getControl().getShell(),
                    new TableConstraintWizard(table));
            dialog.open();
        }

        public void redo() {
            throw new UnsupportedOperationException();
        }

        public void undo() {
            throw new UnsupportedOperationException();
        }
    }
}
