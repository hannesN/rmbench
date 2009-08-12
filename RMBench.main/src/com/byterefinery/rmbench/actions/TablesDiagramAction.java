/*
 * created 24.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TablesDiagramAction.java 673 2007-11-13 19:49:41Z cse $
 */
package com.byterefinery.rmbench.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.TablesNewDiagramOperation;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * an action that creates a new diagram containing only the currently selected tables
 * 
 * @author cse
 */
public class TablesDiagramAction extends SelectionAction {

    public static final String ACTION_ID = "com.byterefinery.rmbench.TablesDiagramAction";
    
    private final Model model;
    
    public TablesDiagramAction(IWorkbenchPart part, Model model) {
        super(part, AS_PUSH_BUTTON);
        setId(ACTION_ID);
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DIAGRAM));
        setText(Messages.TablesDiagram_Label);
        setToolTipText(Messages.TablesDiagram_Description);
        
        this.model = model;
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

    public void run() {
    	
        List<?> selected = getSelectedObjects();
        Table[] tables = new Table[selected.size()];
        Point[] locations = new Point[selected.size()];
        
        for (int i=0; i<selected.size(); i++) {
            TableEditPart part = (TableEditPart) selected.get(i);
            tables[i] = part.getTable();
            locations[i] = part.getLocation();
        }
        
        TablesNewDiagramOperation operation = new TablesNewDiagramOperation(model, tables, locations);
        operation.execute(this);
    }
}
