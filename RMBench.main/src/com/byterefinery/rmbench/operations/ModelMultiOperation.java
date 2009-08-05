/*
 * created 25.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ModelMultiOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.schema.Schema;

/**
 * @author cse
 */
public abstract class ModelMultiOperation extends RMBenchOperation {

    protected  Object[] objects;
    protected final DiagramEditPart diagramPart;
    
    protected static final Set<Schema> EMPTY_SCHEMAS = Collections.emptySet();
    protected static final Set<DTable> EMPTY_TABLES = Collections.emptySet();
    
    protected Set<Schema> savedSchemas = EMPTY_SCHEMAS;
    protected Set<DTable> savedTables = EMPTY_TABLES;

    /**
     * @param label displayable label
     * @param diagramPart target diagram part, possibly <code>null</code>
     * @param objects objects to be processed
     */
    public ModelMultiOperation(String label, DiagramEditPart diagramPart, Object[] objects) {
        super(label);

        this.diagramPart = diagramPart;
        this.objects = objects;
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        initializeLists();
        return processObjects();
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

        for(DTable dtable : savedTables) {
            removeTable(dtable);
        }
        for(Schema schema : savedSchemas) {
            removeSchema(schema);
        }
        // TODO V2: views, sequences
        return Status.OK_STATUS;
    }

    protected abstract void removeTable(DTable dtable);
    protected abstract void removeSchema(Schema schema);
    protected abstract IStatus processObjects();
    
    /**
     * should be called by subclasses to store processed schema objects for undo
     */
    protected void saveSchema(Schema schema) {
        if(savedSchemas == EMPTY_SCHEMAS)
            savedSchemas = new HashSet<Schema>();
        savedSchemas.add(schema);
    }

    /**
     * should be called by subclasses to store processed table objects for undo
     */
    protected void saveTable(DTable table) {
        if(savedTables == EMPTY_TABLES)
            savedTables = new HashSet<DTable>();
        savedTables.add(table);
    }

    private void initializeLists() {
        if(savedTables != EMPTY_TABLES)
            savedTables.clear();
        if(savedSchemas != EMPTY_SCHEMAS)
            savedSchemas.clear();
    }
}
