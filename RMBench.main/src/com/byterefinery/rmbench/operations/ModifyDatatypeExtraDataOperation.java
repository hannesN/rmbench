/*
 * created 07.11.2006
 * 
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.model.schema.Column;

/**
 * The operation, which changes the extra data of an datatype
 * 
 * @author Hannes Niederhausen
 *
 */
public class ModifyDatatypeExtraDataOperation extends RMBenchOperation {

	final private int oldScale;
	final private long oldSize;
	final private String oldExtra;
	final private IDataType newDataType;
	final private Column column;
	
	
	/**
	 * @param dataType
	 * @param newData
	 */
	public ModifyDatatypeExtraDataOperation(Column column, IDataType newDataType) {
		super("Modify Datatype Extradata Operation");
		this.column = column;
		this.newDataType = newDataType;
		this.oldExtra = column.getDataType().getExtra();
		this.oldScale = column.getDataType().getScale();
		this.oldSize = column.getDataType().getSize();
	}

	/* (non-Javadoc)
	 * @see com.byterefinery.rmbench.operations.RMBenchOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IDataType type=column.getDataType();
		
		if (type.hasExtra())
			column.getDataType().setExtra(newDataType.getExtra());
		if (type.acceptsSize())
			column.getDataType().setSize(newDataType.getSize());
		if (type.acceptsScale())
			column.getDataType().setScale(newDataType.getScale());
		
		
		RMBenchPlugin.getEventManager().fireColumnModified(column.getTable(), EventManager.Properties.COLUMN_DATATYPE, column);
		
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see com.byterefinery.rmbench.operations.RMBenchOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IDataType type=column.getDataType();
		
		if (type.hasExtra())
			column.getDataType().setExtra(oldExtra);
		if (type.acceptsSize())
			column.getDataType().setSize(oldSize);
		if (type.acceptsScale())
			column.getDataType().setScale(oldScale);
		
		RMBenchPlugin.getEventManager().fireColumnModified(column.getTable(), EventManager.Properties.COLUMN_DATATYPE, column);

		return Status.OK_STATUS;
	}

}
