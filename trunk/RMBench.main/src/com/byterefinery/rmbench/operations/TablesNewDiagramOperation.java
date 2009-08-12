/*
 * created 13.11.2007
 *
 * Copyright 2007, ByteRefinery
 * 
 * $Id$
 */

package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.views.model.ModelView;


/**
 * an operation that will create a new diagram from existing tables
 * 
 * @author cse
 */
public class TablesNewDiagramOperation extends RMBenchOperation {

	private final Table[] tables;
    private final Point[] locations;
	
    private final AddDiagramOperation addDiagramOperation;
    private AddToDiagramOperation addToDiagramOperation;
	
	/**
	 * @param model the model
	 * @param table the table for which a new diagram is to be created
	 */
	public TablesNewDiagramOperation(Model model, Table table) {
		super(Messages.Operation_TablesNewDiagram_Label);
		this.tables = new Table[]{table};
		this.locations = new Point[]{new Point(100, 100)};
        this.addDiagramOperation = 
        	new AddDiagramOperation(model, computeName(model, table), table.getSchema());
	}

    /**
	 * @param model the model
	 * @param tables the tables for which a new diagram is to be created
	 * @param locations the locations at which the tables should be inserted
	 */
	public TablesNewDiagramOperation(Model model, Table[] tables, Point[] locations) {
		super(Messages.Operation_TablesNewDiagram_Label);
		this.tables = tables;
		this.locations = locations;
        this.addDiagramOperation = 
        	new AddDiagramOperation(model, computeName(model), tables[0].getSchema());
	}

	private String computeName(Model model) {
        String computedName = RMBenchMessages.NewDiagram_Default;
        for(int i=1; model.getDiagram(computedName) != null; i++) {
            computedName = computedName + " ["+i+']';
        }
        return computedName;
	}

	private String computeName(Model model, Table table) {
        String computedName = table.getName();
        for(int i=1; model.getDiagram(computedName) != null; i++) {
            computedName = computedName + " ["+i+']';
        }
        return computedName;
    }
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
        ModelView modelView = 
            (ModelView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
            getActivePage().findView(ModelView.VIEW_ID);
        try {
            addDiagramOperation.execute(monitor, info);
            DiagramEditPart diagramPart = modelView.openNewDiagram(addDiagramOperation.getDiagram());
            addToDiagramOperation = new AddToDiagramOperation(diagramPart, tables, locations);
            addToDiagramOperation.execute(monitor, info);
        }
        catch (Exception e) {
            RMBenchPlugin.logError(e);
        }
        return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
        addToDiagramOperation.undo(monitor, info);
        addDiagramOperation.undo(monitor, info);
        return Status.OK_STATUS;
	}
}
