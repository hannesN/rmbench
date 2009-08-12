package com.byterefinery.rmbench.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.DTableStub;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.diagram.DTableStub.StubConnection;
import com.byterefinery.rmbench.model.schema.Column;

/**
 * a factory that creates the edit parts and associates them with a model object
 */
public class CustomEditPartFactory implements EditPartFactory {
    
    public EditPart createEditPart(EditPart context, Object model) {
        
        EditPart part = null;
        if (model instanceof Diagram) {
            part = new DiagramEditPart();
            part.setModel(model);
        }
        else if (model instanceof DTable) {
            part = new TableEditPart((DTable)model);
        }
        else if (model instanceof DForeignKey) {
            part = new ForeignKeyEditPart((DForeignKey)model);
        }
        else if (model instanceof Column) {
            part = new ColumnEditPart();
            part.setModel(model);
        }
        else if (model instanceof DTableStub) {
            part = new TableStubEditPart((DTableStub) model);
            part.setModel(model);
        }
        else if (model instanceof StubConnection) {
            part = new TableStubConnectionEditPart();
            part.setModel(model);
        }
        return part;
    }
}