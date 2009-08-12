package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * an operation that changes a schemas name
 * 
 * @author cse
 */
public class SchemaNameOperation extends RMBenchOperation {

    private Schema schema;
    private String oldName;
    private String newName;
    
    
    public SchemaNameOperation(Schema schema) {
        super(Messages.Operation_ChangeSchemaName);
        setSchema(schema);
    }
    
    public SchemaNameOperation(Schema schema, String newName) {
        super(Messages.Operation_ChangeSchemaName);
        setSchema(schema);
        setNewName(newName);
    }
    

    public void setSchema(Schema schema) {
        this.schema=schema;
        oldName = (schema!=null) ? schema.getName() : null;
    }
    
    public void setNewName(String newName) {
        this.newName=newName;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return setName(this, newName);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return setName(this, oldName);
    }

    private IStatus setName(Object eventSource, String name) {
        schema.setName(name);
        RMBenchPlugin.getEventManager().fireSchemaModified(
                eventSource, EventManager.Properties.NAME, schema);
        return Status.OK_STATUS;
    }
}
