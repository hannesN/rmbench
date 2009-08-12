/*
 * created 03.04.2005
 * 
 * $Id:DiagramTreeEditPart.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

import com.byterefinery.rmbench.model.diagram.DTable;

/**
 * edit part to represent the schema object in the tree outline view
 * 
 * @author cse
 */
public class DiagramTreeEditPart extends AbstractTreeEditPart {

    private final EditPartListener editPartListener = new EditPartListener.Stub() {

        public void childAdded(EditPart child, int index) {
            EditPart newChild = createChild(child.getModel());
            if (newChild!=null)
                addChild(newChild, index);
        }

        public void removingChild(EditPart child, int index) {
            for (Iterator<?> it = getChildren().iterator(); it.hasNext();) {
                EditPart table = (EditPart) it.next();
                if(table.getModel() == child.getModel()) {
                    removeChild(table);
					break;
                }
            }
        }
    };

    private final DiagramEditPart diagramPart;
    
    private final static Comparator<DTable> dTableComparator = new Comparator<DTable>() {
		public int compare(DTable firstTable, DTable secTable) {
			return (firstTable.getTable().getName().compareTo(secTable.getTable().getName()));
		}
	};
    
    public DiagramTreeEditPart(DiagramEditPart diagramPart) {
        this.diagramPart = diagramPart;
        this.diagramPart.addEditPartListener(editPartListener);
    }
	
    public void deactivate() {
		diagramPart.removeEditPartListener(editPartListener);
    }

    protected List<?> getModelChildren() {
        List<DTable> dTables = new ArrayList<DTable>(diagramPart.getDiagram().getDTables()); 
    	Collections.sort(dTables, dTableComparator); 
    	return dTables;
    }

    protected String getText() {
        return diagramPart.getDiagram().getName();
    }
}
