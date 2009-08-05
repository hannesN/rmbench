/*
 * created 17.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColumnCommentOperation.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Column;

/**
 * Operation for changing the name of a column
 * @author cse
 */
public class ColumnCommentOperation extends RMBenchOperation {

    private final Column column;
    private final String oldComment, newComment;
    
    public ColumnCommentOperation(Column column, String newComment) {
        super(Messages.Operation_ModifyColumn);
        this.column = column;
        this.oldComment = column.getComment();
        this.newComment = 
            newComment == null || newComment.length() == 0 ? null : newComment;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setComment(newComment);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_COMMENT, column);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        column.setComment(oldComment);
        RMBenchPlugin.getEventManager().fireColumnModified(
                column.getTable(), EventManager.Properties.COLUMN_COMMENT, column);
        return Status.OK_STATUS;
    }
}
