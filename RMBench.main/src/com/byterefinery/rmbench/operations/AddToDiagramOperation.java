/*
 * created 25.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: AddToDiagramOperation.java 665 2007-09-29 15:31:59Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.editparts.AbstractTableEditPart;
import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.Diagram;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Schema;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.LayoutHelper;

/**
 * operation for adding model elements (tables or schemas) to a diagram. The tables
 * will be automatically layed out within the diagram.
 * 
 * @author cse
 */
public class AddToDiagramOperation extends ModelMultiOperation {

    private interface Handler {
        IStatus processObjects();
    }
    private final Handler handler;
    
    /**
     * add the given schema objects (schemas or tables) at the given location. If an object is 
     * a schema, the tables contained therein will be added. Tables are layed out around the 
     * given location.
     * 
     * @para diagramPart the target diagram part
     * @param objects schema objects, such as schemas, tables, views, etc.
     * @param location the preferred location, around which objects should be layed out
     */
    public AddToDiagramOperation(DiagramEditPart diagramPart, Object[] objects, Point location) {
        super(Messages.Operation_AddToDiagram, diagramPart, objects);
        this.handler = new MultiObjectsLayoutHandler(location);
        filterObjects();
    }

    /**
     * add the given tables to the diagram, using the given pre-computed locations
     * 
     * @para diagramPart the target diagram part
     * @param the tables to add
     * @param locations table locations
     */
    public AddToDiagramOperation(DiagramEditPart diagramPart, Table[] tables, Point[] tableLocations) {
        super(Messages.Operation_AddToDiagram, diagramPart, tables);
        this.handler = new TablesNoLayoutHandler(tableLocations);
        filterObjects();
    }
    
    protected IStatus processObjects() {
        return handler.processObjects();
    }

    protected void removeSchema(Schema schema) {
        for (Table table : schema.getTables()) {
            diagramPart.getDiagram().removeTable(table);
        }
    }

    protected void removeTable(DTable dtable) {
        diagramPart.getDiagram().removeTable(dtable);
    }

    /*
     * processes heterogenous database objects (schemas and tables), performs automatic 
     * layout afterwards
     */
    private class MultiObjectsLayoutHandler implements Handler {

        private final Point location;
        
        public MultiObjectsLayoutHandler(Point location) {
            this.location = location;
        }

        public IStatus processObjects() {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Schema) {
                    Schema schema = (Schema) objects[i];
                    for(Table table : schema.getTables()) {
                        DTable dtable = new DTable(table, location);
                        saveTable(dtable);
                    }
                }
                else if (objects[i] instanceof Table) {
                    DTable dtable = new DTable((Table) objects[i], location);
                    saveTable(dtable);
                }
                else if (objects[i] instanceof DTable) {
                    //we create a new DTable, cause the the surly is in use,
                    // using it would lead to strange behaviour positioning it
                    DTable dtable = new DTable(((DTable)objects[i]).getTable(), location);
                    saveTable(dtable);
                }
                // TODO V2: views, sequences
            }
            IStatus status = LayoutHelper.addAndLayoutTables(diagramPart, savedTables, location);
      
            diagramPart.getDiagram().updateTables();
            diagramPart.refreshTablesStubs();
            diagramPart.getDiagram().fireDiagrammPropertyChange(Diagram.PROPERTY_TABLE);
            refreshOthers();
            return status;
        }
    }
    
    /*
     * processes homogenous array of tables, with pre-assigned locations
     */
    private class TablesNoLayoutHandler implements Handler {

        private final Point[] tableLocations;
        
        public TablesNoLayoutHandler(Point[] tableLocations) {
            if(tableLocations.length != objects.length)
                throw new IllegalArgumentException("number of locations != number of tables");
            this.tableLocations = tableLocations;
        }

        public IStatus processObjects() {
            for (int i = 0; i < objects.length; i++) {
                DTable dtable = new DTable((Table) objects[i], tableLocations[i]);
                diagramPart.getDiagram().addTable(dtable);
                saveTable(dtable);
            }
            refreshOthers();

            diagramPart.getDiagram().updateTables();
            diagramPart.refreshTablesStubs();
            diagramPart.getDiagram().fireDiagrammPropertyChange(Diagram.PROPERTY_TABLE);
            return Status.OK_STATUS;
        }
    }

    /*
     * refresh connections for those tables that are related to the added tables. This handles the
     * case where a relationship becomes visible because a participant table has been newly added  
     */
    protected void refreshOthers() {
        for (DTable dtable : savedTables) {
            
        	//refeshing blind tables
            AbstractTableEditPart tablePart = (AbstractTableEditPart) diagramPart.getViewer()
                    .getEditPartRegistry().get(diagramPart.getDiagram().getTableStub(dtable));
            if (tablePart!=null)
                tablePart.refresh();
            for(ForeignKey foreignKey : dtable.getTable().getForeignKeys()) {
                 tablePart = (TableEditPart) diagramPart.getViewer().getEditPartRegistry().get(
                        foreignKey.getTargetTable());
                if(tablePart != null) {
                    tablePart.refreshTargetConnections();
                    //need to refresh source connection too, because the stub is 
                    //a table source connection
                    tablePart.refreshSourceConnections();
                    
                }
            }
            for (ForeignKey foreignKey : dtable.getTable().getReferences()) {
                tablePart = (TableEditPart) diagramPart.getViewer().getEditPartRegistry().get(
                        foreignKey.getTable());
                if (tablePart != null)
                    tablePart.refreshSourceConnections();
            }
        }
    }
    /**
     * This method will check the given object list if there are any tables which are already assigned to this diagram.
     * After filtering the object list it will only contain Table and DTable objects 
     *
     */
    private void filterObjects(){
        
        ArrayList<Object> tmpObjectList = new ArrayList<Object>();       
        
        for(int i = 0; i < objects.length; i++){
            if(objects[i] instanceof Schema){
                Iterator<Table> iter = ((Schema) objects[i]).getTables().iterator();
                while(iter.hasNext()){
                    Table table = (Table)iter.next();
                    // No corresponding table found..
                    if(diagramPart.getDiagram().getDTable(table) == null)
                        tmpObjectList.add(table);
                }
            }
            if(objects[i] instanceof Table){
                Table table = (Table)objects[i];
                if(diagramPart.getDiagram().getDTable(table) == null)
                    tmpObjectList.add(table);
            }
            if(objects[i] instanceof DTable){
                tmpObjectList.add(objects[i]);
            }                        
        }
        
        objects = tmpObjectList.toArray();
    }
    
    /**
     * This operation will only be executable if at least one of the given tables don't exists in the diagram.
     * 
     * @see org.eclipse.core.commands.operations.AbstractOperation#canExecute()
     */
    public boolean canExecute() {              
        // if there any objects left after filtering the list the operation can be executed.
        return (objects.length > 0);
    }
}
