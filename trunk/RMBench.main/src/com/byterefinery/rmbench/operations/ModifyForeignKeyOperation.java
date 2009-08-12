/*
 * created 11.10.2005
 * 
 * $Id: ModifyForeignKeyOperation.java 668 2007-10-04 18:48:16Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.model.IForeignKey;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.ForeignKey;

/**
 * undoable operation for modifying foreign key properties
 * @author cse
 */
public class ModifyForeignKeyOperation extends RMBenchOperation {

	/**
	 * an enum describing the available modifications, with the ability to map to a
	 * GUI resource string
	 */
	public enum Modification {
		NAME(RMBenchMessages.ForeignkeysTab_Column_Key) {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.setName((String)newVal);
	            return EventManager.Properties.NAME;
			}
		},
		DELETE_RULE(RMBenchMessages.ForeignkeysTab_Column_DeleteRule) {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.setDeleteAction((IForeignKey.Action)newVal);
	            return EventManager.Properties.FK_DELETE_RULE;
			}
		},
		UPDATE_RULE(RMBenchMessages.ForeignkeysTab_Column_UpdateRule) {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.setUpdateAction((IForeignKey.Action)newVal);
	            return EventManager.Properties.FK_UPDATE_RULE;
			}
		},
		REPLACE_COLUMN(RMBenchMessages.ForeignkeysTab_Column_Column) {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.replaceColumn((Column)oldVal, (Column)newVal);
	            return EventManager.Properties.FK_COLUMN_REPLACED;
			}
		},
		DELETE_COLUMN() {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.removeColumn((Column)oldVal);
	            return EventManager.Properties.FK_COLUMN_DELETED;
			}
		},
		ADD_COLUMN() {
			public String execute(ForeignKey foreignKey, Object oldVal, Object newVal) {
				
	            foreignKey.addColumn((Column)newVal);
	            return EventManager.Properties.FK_COLUMN_ADDED;
			}
		};
		
		private final String property;
		
		private Modification(String property) {
			this.property = property;
		}
		
		private Modification() {
			this.property = null;
		}
		
		public abstract String execute(ForeignKey foreignKey, Object oldVal, Object newVal);

		/**
		 * map a GUI resource to an enum value
		 */
		public static Modification forProperty(String property) {
			for (Modification modification : values()) {
				if(modification.property == property)
					return modification;
			}
			throw new IllegalArgumentException(); //hopefully dont happen
		}
	}
	
	private final ForeignKey foreignKey;
	private final Modification modification;
	private final Object newValue, oldValue;
	
	/**
	 * @param foreignKey the foreign key to modify
	 * @param resource the resource string
	 * @param newValue the new value
	 * @param oldValue the old value
	 */
	public ModifyForeignKeyOperation(
			ForeignKey foreignKey, 
			String resource, 
			Object newValue, 
			Object oldValue) {
		
		super(Messages.Operation_ModifyForeignKey);
		this.foreignKey = foreignKey;
		this.modification = Modification.forProperty(resource);
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * @param foreignKey the foreign key to modify
	 * @param modification the modification to perform
	 * @param newValue the new value
	 * @param oldValue the old value
	 */
	public ModifyForeignKeyOperation(
			ForeignKey foreignKey, 
			Modification modification, 
			Object newValue, 
			Object oldValue) {
		
		super(Messages.Operation_ModifyForeignKey);
		this.foreignKey = foreignKey;
		this.modification = modification;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        String eventProperty = modification.execute(foreignKey, oldValue, newValue);
        RMBenchPlugin.getEventManager().fireForeignKeyModified(eventProperty, foreignKey);
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String eventProperty = modification.execute(foreignKey, newValue, oldValue);
        RMBenchPlugin.getEventManager().fireForeignKeyModified(eventProperty, foreignKey);
		return Status.OK_STATUS;
	}
}
