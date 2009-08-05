/*
 * created 26.09.2005 by sell
 *
 * $Id: PasteTablesAction.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.actions.ActionFactory;

import com.byterefinery.rmbench.editors.DiagramEditor;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.operations.AddToDiagramOperation;
import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * action for pasting the tables currently found in the clipboard
 * 
 * @author sell
 */
public class PasteTablesAction extends WorkbenchPartAction {

    private final DiagramEditor diagramEditor;
    
    private transient RMBenchOperation operation;
    
    private final Point location = new Point(-1, -1);
    
    private MouseListener mouseListener = new MouseListener() {
        public void mouseDoubleClick(MouseEvent e) {
        }
        public void mouseDown(MouseEvent e) {
            location.x = e.x;
            location.y = e.y;
        }
        public void mouseUp(MouseEvent e) {
            location.x = e.x;
            location.y = e.y;
        }
    };
    
    public PasteTablesAction(DiagramEditor diagramEditor) {
        super(diagramEditor);
        setId(ActionFactory.PASTE.getId());
        setText(Messages.Paste_Label);
        
        this.diagramEditor = diagramEditor;
    }

    public void dispose() {
        diagramEditor.getViewer().getControl().removeMouseListener(mouseListener);
    }

    public void hookEvents() {
        diagramEditor.getViewer().getControl().addMouseListener(mouseListener);
    }

    public void run() {
        operation.execute(this);
    }

    protected boolean calculateEnabled() {
        operation = null;
        if(Clipboard.getDefault().getContents() instanceof TableEditPart[]) {
            operation = partsOperation((TableEditPart[])Clipboard.getDefault().getContents());
        }
        else if(Clipboard.getDefault().getContents() instanceof Table[]) {
            operation = tablesOperation((Table[])Clipboard.getDefault().getContents());
        }
        return operation != null;
    }

    private RMBenchOperation tablesOperation(Table[] tables) {
        Point loc = location.x >= 0 ? location : 
                diagramEditor.getDiagramPart().getContentPane().getBounds().getCenter();
        return new AddToDiagramOperation(diagramEditor.getDiagramPart(), tables, loc);
    }

    private RMBenchOperation partsOperation(TableEditPart[] tableParts) {
        Table[] tables = new Table[tableParts.length];
        Point[] locations = new Point[tableParts.length];
        
        for (int i = 0; i < tableParts.length; i++) {
            locations[i] = tableParts[i].getLocation();
            tables[i] = tableParts[i].getTable();
        }
        return new AddToDiagramOperation(diagramEditor.getDiagramPart(), tables, locations);
    }
}
