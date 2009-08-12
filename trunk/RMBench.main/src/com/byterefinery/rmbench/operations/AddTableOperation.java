/*
 * created 26.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: AddTableOperation.java 274 2006-03-01 13:36:12Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * a command that adds a new table to a schema <em>and</em> a diagram. Note that
 * there is a separate opertaion that only adds to the model
 * 
 * @author cse
 * @see com.byterefinery.rmbench.operations.NewTableOperation
 */
public class AddTableOperation extends RMBenchOperation {

    private final Diagram diagram;
    private final Point location;
    
    private transient DTable dtable;
    
    public AddTableOperation(Diagram diagram, Point location) {
        super(Messages.Operation_AddTable);
        this.diagram = diagram;
        this.location = location;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	
        String name = diagram.getModel().getNameGenerator().generateTableName(
                diagram.getModel().getIModel(), 
                diagram.getDefaultSchema().getISchema());
        dtable = new DTable(new Table(diagram.getDefaultSchema(), name), location);
        
		diagram.addTable(dtable);
		
        RMBenchPlugin.getEventManager().fireTableAdded(eventSource, null, dtable.getTable());
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        diagram.removeTable(dtable);
        
        dtable.getTable().abandon();
        
        RMBenchPlugin.getEventManager().fireTableDeleted(this, null, dtable.getTable());
        dtable = null;

        return Status.OK_STATUS;
    }
}
