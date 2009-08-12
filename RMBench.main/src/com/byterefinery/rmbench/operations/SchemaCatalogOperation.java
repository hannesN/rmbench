/*
 * created 26.05.2005
 *
 * &copy; 2005, DynaBEAN Consulting
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
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * an operation that changes a schema catalog name
 * 
 * @author cse
 */
public class SchemaCatalogOperation extends RMBenchOperation {

    private Schema schema;
    private String oldName;
    private String newName;
    
    
    public SchemaCatalogOperation(Schema schema) {
        super(Messages.Operation_ChangeSchemaCatalog);
        setSchema(schema);
    }
    
    public SchemaCatalogOperation(Schema schema, String newName) {
        super(Messages.Operation_ChangeSchemaCatalog);
        setSchema(schema);
        setNewName(newName);
    }

    public void setSchema(Schema schema) {
        this.schema=schema;
        oldName = (schema!=null) ? schema.getCatalogName() : null;
        
    }
    
    public void setNewName(String newName) {
        this.newName=newName;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setName(this, newName);
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return setName(this, oldName);
    }

    private IStatus setName(Object eventSource, String name) {
        schema.setCatalogName(name);
        RMBenchPlugin.getEventManager().fireSchemaModified(this, EventManager.Properties.CATALOG, schema);
        return Status.OK_STATUS;
    }
}
