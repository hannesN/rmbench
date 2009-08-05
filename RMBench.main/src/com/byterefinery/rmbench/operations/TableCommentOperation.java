/*
 * created 17.06.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableCommentOperation.java 668 2007-10-04 18:48:16Z cse $
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * historized operation for changing the table comment
 * @author cse
 */
public class TableCommentOperation extends RMBenchOperation {

    private Table table;
    private String oldComment;
    private String newComment;
    
    public TableCommentOperation(Table table) {
        super(Messages.Operation_ChangeTableComment);
        setTable(table);
    }
    
    public TableCommentOperation(Table table, String comment) {
        super(Messages.Operation_ChangeTableComment);
        setTable(table);
        setNewComment(comment);
    }
    
    public void setTable(Table table) {
        this.table = table;
        this.oldComment = table != null ? table.getComment() : null;
    }
    
    public void setNewComment(String newComment){
        this.newComment = newComment;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setComment(eventSource, newComment);
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setComment(this, newComment);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setComment(this, oldComment);
    }

    private IStatus setComment(Object eventSource, String comment) {
        table.setComment(comment);
        RMBenchPlugin.getEventManager().fireTableModified(
                eventSource, EventManager.Properties.COMMENT, table);
        return Status.OK_STATUS;
    }
}
