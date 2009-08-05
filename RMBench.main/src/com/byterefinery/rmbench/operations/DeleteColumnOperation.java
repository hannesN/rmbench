/*
 * created 15.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: DeleteColumnOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Properties;
import com.byterefinery.rmbench.dialogs.ColumnDependencyDialog;
import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.Constraint;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Index;
import com.byterefinery.rmbench.model.schema.Key;
import com.byterefinery.rmbench.model.schema.PrimaryKey;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.model.schema.UniqueConstraint;
import com.byterefinery.rmbench.util.Utils;

/**
 * undoable operation for deleting a column from a table. The operation cascades to 
 * affected primary keys, foreign keys and unique constraints
 * 
 * @author cse
 */
public class DeleteColumnOperation extends RMBenchOperation {

	private static final class KeyModification {
		final Key key;
		final int index;
		final Column column;
		
		KeyModification(Key key, int index, Column column) {
			this.key = key;
			this.index = index;
			this.column = column;
		}
        
		public Column getColumn() {
            return column;
        }

        public void revert() {
			key.addColumn(column, index);
		}
	}
	
    private final Table table;
    private final Column column;
    private List<ForeignKey> deletableForeignKeys;
    private List<ForeignKey> undeletableForeignKeys;
    private List<Index> indexList;
    private final List<UniqueConstraint> uniqueConstraints;
    private boolean deleteConstraints;
    private boolean deleteIndices;
    private int index; //index of column in primary key
    
    //private List deletedFKColumns = Collections.EMPTY_LIST;
    private List<ForeignKey> abandonedFKs = Utils.emptyList();
    private List<KeyModification> modifiedFKs = Utils.emptyList();
    private List<UniqueConstraint> abandonedConstraints = Utils.emptyList();
    private List<KeyModification> modifiedConstraints = Utils.emptyList();
    private List<Column> abandonedColumns = Utils.emptyList();
    private List<IndexModification> modifiedIndexes = Utils.emptyList();
    private PrimaryKey abandonedPK;
    private KeyModification modifiedPK;
    
    public DeleteColumnOperation(Table table, Column column) {
        super(Messages.Operation_DeleteColumn);
        this.table = table;
        this.column = column;
        this.uniqueConstraints = column.getUniqueConstraints();
        
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        if (column.belongsToForeignKey()) {
            MessageDialog.openInformation(
                    RMBenchPlugin.getModelView().getViewSite().getShell(),
                    Messages.Operation_DeleteColumn_messageTitle,
                    Messages.Operation_DeleteColumn_foreignKey_Message);
            return Status.CANCEL_STATUS;
        }
        
        ColumnDependencyDialog dialog = new ColumnDependencyDialog(column);
        
        if (dialog.calculateDependencies()) {
            if (dialog.open()!=MessageDialog.OK) {
                return Status.CANCEL_STATUS;
            }
        }
        deletableForeignKeys = dialog.getDeletableForeignKeys();
        undeletableForeignKeys = dialog.getUnDeletableForeignKeys();
        deleteConstraints = dialog.getDeleteConstraints();
        
        if (column.belongsToPrimaryKey()) {
            index = column.getTable().getPrimaryKey().getIndex(column);
        }
        
        indexList = dialog.getIndexList();
        deleteIndices = dialog.getDeleteIndices();
        
        return redo(monitor, info);
    }
    
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
    	//clear liusts so we do not fill them more than once if we we redo mor than one time
        abandonedFKs.clear();
        abandonedColumns.clear();
        modifiedFKs.clear();
        modifiedConstraints.clear();
        modifiedIndexes.clear();
        
        for (ForeignKey foreignKey : undeletableForeignKeys) {
           
            Column fkColumn = foreignKey.getColumn(index);

            if (foreignKey.size() == 1) {
                foreignKey.abandon();
                addAbandonedFK(foreignKey);
                RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this, foreignKey);
            }
            else {
                foreignKey.removeColumn(fkColumn);
                addModifiedFK(new KeyModification(foreignKey, index, fkColumn));
                RMBenchPlugin.getEventManager().fireForeignKeyModified(
                        EventManager.Properties.FK_COLUMN_DELETED, foreignKey);
            }            
        }
        
        for (ForeignKey foreignKey : deletableForeignKeys) {

            if (abandonedColumns==Utils.EMPTY_LIST)
                abandonedColumns = new ArrayList<Column>(undeletableForeignKeys.size());
            
            Column fkColumn = foreignKey.getColumn(index);
            fkColumn.abandon();
            abandonedColumns.add(fkColumn);
            RMBenchPlugin.getEventManager().fireColumnDeleted(fkColumn.getTable(), fkColumn);
            
            if (foreignKey.size() == 1) {
                foreignKey.abandon();
                addAbandonedFK(foreignKey);
                RMBenchPlugin.getEventManager().fireForeignKeyDeleted(this, foreignKey);
            }
            else {
                foreignKey.removeColumn(fkColumn);
                addModifiedFK(new KeyModification(foreignKey, index, fkColumn));
                RMBenchPlugin.getEventManager().fireForeignKeyModified(
                        EventManager.Properties.FK_COLUMN_DELETED, foreignKey);
            }            
        }
        
        
        for (UniqueConstraint constraint : uniqueConstraints) {
            if ( (constraint.size() == 1) || (deleteConstraints) ) {
                constraint.abandon();
                addAbandonedConstraint(constraint);
                RMBenchPlugin.getEventManager().fireConstraintDeleted(constraint);
            }
            else {
                int index = constraint.removeColumn(column);
                addModifiedConstraint(new KeyModification(constraint, index, column));
                RMBenchPlugin.getEventManager().fireConstraintModified(constraint);
            }
        }
        if(column.belongsToPrimaryKey()) {
            PrimaryKey pk = table.getPrimaryKey();
            if(pk.size() == 1) {
                pk.abandon();
                abandonedPK = pk;
                RMBenchPlugin.getEventManager().fireConstraintDeleted(pk);
            }
            else {
                int index = table.getPrimaryKey().removeColumn(column);
                modifiedPK = new KeyModification(table.getPrimaryKey(), index, column);
                RMBenchPlugin.getEventManager().fireConstraintModified(pk);
            }
        }
        
        for (Index index : indexList) {
        	if ((deleteIndices) || (index.getColumns().length==1)) {
        		index.abandon();
        		addModifiedIndex(new IndexModification(-1, index));
        		RMBenchPlugin.getEventManager().fireIndexDeleted(column.getTable(), index);
        	} else {
        		int colIndex = index.removeColumn(column);
        		addModifiedIndex(new IndexModification(colIndex, index));
        		RMBenchPlugin.getEventManager().fireIndexModified(column.getTable(), 
        				Properties.COLUMN_ORDER, index);
        	}
        }
        
        column.abandon();
        RMBenchPlugin.getEventManager().fireColumnDeleted(table, column);
        return Status.OK_STATUS;
    }

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.restore();

        
        
        if(abandonedPK != null) {
            abandonedPK.restore();
            RMBenchPlugin.getEventManager().fireConstraintAdded(abandonedPK);
        }
        else if(modifiedPK != null) {
            modifiedPK.revert();
            RMBenchPlugin.getEventManager().fireConstraintModified((Constraint)modifiedPK.key);
        }
        
        for (IndexModification mod : modifiedIndexes) {
        	if (mod.colIndex==-1) {
        		mod.index.restore();
        		RMBenchPlugin.getEventManager().fireIndexAdded(column.getTable(), mod.index);
        	} else {
        		mod.index.addColumn(column, mod.colIndex);
        		RMBenchPlugin.getEventManager().fireIndexModified(column.getTable(), 
        				Properties.COLUMN_ORDER, mod.index);
        	}
        }
        
        for (Column fkColumn : abandonedColumns) {
            fkColumn.restore();
            RMBenchPlugin.getEventManager().fireColumnAdded(fkColumn.getTable(), fkColumn);
        }
        
        for (ForeignKey foreignKey : abandonedFKs) {
			foreignKey.restore();
	        RMBenchPlugin.getEventManager().fireForeignKeyAdded(this, foreignKey);
		}
        for (KeyModification modification : modifiedFKs) {
            modification.revert();
	        RMBenchPlugin.getEventManager().fireForeignKeyModified(
	        		EventManager.Properties.FK_COLUMN_ADDED, (ForeignKey)modification.key);
		}
        
        for (UniqueConstraint constraint : abandonedConstraints) {
			constraint.restore();
	        RMBenchPlugin.getEventManager().fireConstraintAdded(constraint);
		}
        for (KeyModification modification : modifiedConstraints) {
			modification.revert();
	        RMBenchPlugin.getEventManager().fireConstraintModified((Constraint)modification.key);
		}
        
        
        RMBenchPlugin.getEventManager().fireColumnAdded(table, column);
        return Status.OK_STATUS;
    }

    private void addAbandonedFK(ForeignKey foreignKey) {
    	if(abandonedFKs == Utils.EMPTY_LIST)
    		abandonedFKs = new ArrayList<ForeignKey>();
    	abandonedFKs.add(foreignKey);
	}

    private void addAbandonedConstraint(UniqueConstraint constraint) {
    	if(abandonedConstraints == Utils.EMPTY_LIST)
    		abandonedConstraints = new ArrayList<UniqueConstraint>();
    	abandonedConstraints.add(constraint);
	}

    private void addModifiedFK(KeyModification modification) {
    	if(modifiedFKs == Utils.EMPTY_LIST)
    		modifiedFKs = new ArrayList<KeyModification>();
    	modifiedFKs.add(modification);
	}

    private void addModifiedConstraint(KeyModification modification) {
    	if(modifiedConstraints == Utils.EMPTY_LIST)
    		modifiedConstraints = new ArrayList<KeyModification>();
    	modifiedConstraints.add(modification);
	}
    
    private void addModifiedIndex(IndexModification modification) {
    	if (modifiedIndexes==Utils.EMPTY_LIST) {
    		modifiedIndexes=new ArrayList<IndexModification>();
    	}
    	modifiedIndexes.add(modification);
    }
    
    private class IndexModification {
    	// if index >-1 then the column was deleted, else the index was abandoned
    	final int colIndex;
    	final Index index;
    	
		
    	public IndexModification(final int colIndex, final Index index) {
			super();
			this.colIndex = colIndex;
			this.index = index;
		}
    	
    	
    }
}
