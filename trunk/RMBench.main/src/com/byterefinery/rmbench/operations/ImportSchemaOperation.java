/*
 * created 22.07.2005 by cse
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ImportSchemaOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBForeignKey;
import com.byterefinery.rmbench.model.dbimport.DBSchema;
import com.byterefinery.rmbench.model.dbimport.DBTable;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.LayoutHelper;
import com.byterefinery.rmbench.util.dbimport.ImportHelper;
import com.byterefinery.rmbench.util.dbimport.ImportMessages;

/**
 * an operation for importing schema objects from a database import view into the
 * model. Suppored objects are schemas, tables, views, sequences. 
 * 
 * @author sell
 */
public class ImportSchemaOperation extends ModelMultiOperation {

    private final Model model;
    private final Point location;
    private final ImportHelper importHelper = new ImportHelper();
    
	public ImportSchemaOperation(DiagramEditPart diagramPart, Object[] objects, Point location) {
        super(Messages.Operation_ImportObjects, diagramPart, objects);
        this.location = location;
        this.model = diagramPart.getDiagram().getModel();
	}

	public ImportSchemaOperation(Model model, Object[] objects) {
        super(Messages.Operation_ImportObjects, null, objects);
        this.model = model;
        this.location = null;
	}

    protected IStatus processObjects() {
        
        // diagram tables must be added AFTER foreign keys have been imported
        List<DTable> diagramTables = new ArrayList<DTable>();
        List<DBTable> tables = new ArrayList<DBTable>();
        List<DBForeignKey> foreignKeys = new ArrayList<DBForeignKey>();
        HashMap<String, DBSchema> schemas = new HashMap<String, DBSchema>();
        
        // analyse objectlist - schemas and tables are not at the same time in the objectlist.
        for(int i =0;i < objects.length;i++){
            if(objects[i] instanceof DBSchema){
            	DBSchema schema = (DBSchema)objects[i];
                schemas.put( schema.getName(), schema );
                tables.addAll(schema.getTables());
            }
            if(objects[i] instanceof DBTable){
            	DBTable table = (DBTable)objects[i];
                schemas.put( table.getSchema().getName(), table.getSchema() );
                tables.add(table);
            }
        }
        // get foreign keys
        for(DBTable table : tables)
            foreignKeys.addAll(table.getForeignKeys());
       
        // importSchemas
        processSchemas(schemas);
        // importTables
        processTables(tables, diagramTables);
        // import ForeignKeys
        processForeignKeys(foreignKeys);
        
        //visualize
        if(diagramPart != null) {
            LayoutHelper.addAndLayoutTables(diagramPart, diagramTables, location);
        }
        return Status.OK_STATUS;
    }
    
    private void processSchemas(HashMap<String, DBSchema> schemas){
        for(String schemaName : schemas.keySet()){            
            DBSchema schema = schemas.get(schemaName);
            Schema target = model.getSchema(schema.getName());
            //no schema --> create
            if(target == null){
                target = new Schema(schema.getCatalogName(),schema.getName(),model.getDatabaseInfo());
                model.addSchema(target);
                saveSchema(target);
                RMBenchPlugin.getEventManager().fireSchemaAdded(this, model, target);
            }
        }
    }
    
    private void processTables(List<DBTable> tables, List<DTable> diagramTables){
        for(DBTable table : tables){
            Schema schema = model.getSchema(table.getSchemaName());
            if(schema != null){
                if(schema.getTable(table.getName()) == null){
                    Table importedTable = importHelper.importTable(table,schema);                    
                    if(diagramPart != null)
                        diagramTables.add(new DTable(importedTable, location));
                    RMBenchPlugin.getEventManager().fireTableAdded(this,null,importedTable);
                }
                //else
                    // TODO update model table                
            }
            else{
                String message = MessageFormat.format(
                        ImportMessages.importTable_errorParentSchema,
                        new Object[]{table.getName(), table.getSchemaName()}
                        );
                RMBenchPlugin.logError(message);
            }
        }
    }
   
    private void processForeignKeys(List<DBForeignKey> foreignKeys){
        for(DBForeignKey fkey : foreignKeys){
            Schema targetSchema;            
            
            // check target schema
            if((targetSchema = model.getSchema(fkey.targetSchema)) == null){
                String msg = MessageFormat.format(
                        ImportMessages.importForeignKey_errorTargetSchema,
                        new Object[]{fkey.name, fkey.targetSchema}
                        );
                RMBenchPlugin. logError(msg);
                continue;
            }
            // check target table
            if(targetSchema.getTable(fkey.targetTable) == null){
                String msg = MessageFormat.format(
                        ImportMessages.importForeignKey_errorTargetTable,
                        new Object[]{fkey.name, fkey.targetTable}
                        );
                RMBenchPlugin.logError(msg);
                continue;
            }

            //target schema and table are ok --> check for duplicate entry in model
            Table modelTable = model.getSchema(fkey.table.getSchemaName()).getTable(fkey.table.getName());
            // Since this method has to be called AFTER processTables(), modelTable must exist.
            boolean doImport = true;
            for(ForeignKey fk : modelTable.getForeignKeys())
                if(fk.getName().equals(fkey.name)){
                    doImport = false;
                    break;
                }
            // everything ok --> import foreign key
            if(doImport)
                importHelper.importForeignKey(fkey, model);
        }
    }

    protected void removeSchema(Schema schema) {
        model.removeSchema(schema);
        RMBenchPlugin.getEventManager().fireSchemaDeleted(this, model, schema);
        
        if(diagramPart != null) {
            for (Table table : schema.getTables()) {
                diagramPart.getDiagram().removeTable(table);
            }
        }
    }

    protected void removeTable(DTable dtable) {
        
        dtable.getTable().abandon();
        RMBenchPlugin.getEventManager().fireTableDeleted(this, null, dtable.getTable());
        
        if(diagramPart !=  null) {
            diagramPart.getDiagram().removeTable(dtable);
        }
    }

    public void dispose() {
        importHelper.close();
    }
}
