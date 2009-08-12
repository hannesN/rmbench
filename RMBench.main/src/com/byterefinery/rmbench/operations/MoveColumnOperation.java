/*
 * created 24.11.2006
 * 
 * $Id$
 */ 
package com.byterefinery.rmbench.operations;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Table;


/**
 * operation to move a column to another position in a table
 * 
 * @author Hannes Niederhausen
 *
 */
public class MoveColumnOperation extends RMBenchOperation {

	private final Column column;
	private final int moveDelta;
	
	private final int oldIndex;
	
	public MoveColumnOperation(Column column, int moveDelta) {
		super(Messages.Operation_MoveColumn);
		this.column = column;
		this.moveDelta = moveDelta;
		oldIndex = findIndex();
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		Table table = column.getTable();
		int index = oldIndex + moveDelta;
		if (index<0) {
			index = 0;
		} else if (index>=column.getTable().getColumns().size()) {
			index = column.getTable().getColumns().size()-1;
		}
		table.moveColumn(column, index);
		
		RMBenchPlugin.getEventManager().fireTableModified(this,
				EventManager.Properties.COLUMN_ORDER, table);
		
		return Status.OK_STATUS;
	}
	
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		column.getTable().moveColumn(column, oldIndex);
		
		RMBenchPlugin.getEventManager().fireTableModified(this,
				EventManager.Properties.COLUMN_ORDER, column.getTable());
		
		return Status.OK_STATUS;
	}
	
	private int findIndex() {
		List<Column> columns = column.getTable().getColumns();
		int i=0;
		for (Iterator<Column> it=columns.iterator(); it.hasNext(); i++) {
			if(it.next() == column)
				return i;
		}
		
		return -1;
	}

}
