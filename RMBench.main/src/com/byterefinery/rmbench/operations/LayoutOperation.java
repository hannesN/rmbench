/*
 * created 27.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: LayoutOperation.java 667 2007-10-02 18:54:16Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalViewer;

import com.byterefinery.rmbench.editparts.AbstractTableEditPart;
import com.byterefinery.rmbench.editparts.ForeignKeyEditPart;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.editparts.TableStubEditPart;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.util.LayoutHelper;

/**
 * operation to automatically layout the currently selected tables, or the whole diagram 
 * if there is no selection
 * 
 * @author cse
 */
public class LayoutOperation extends RMBenchOperation {

    private Map<TableEditPart, Point> oldLocations = new HashMap<TableEditPart, Point>();
    private List<ConnectionEditPart> stubs = new ArrayList<ConnectionEditPart>();
    
    private final GraphicalViewer viewer;
    
    public LayoutOperation(GraphicalViewer viewer) {
        super(Messages.Operation_LayoutTables);
        this.viewer = viewer;
    }

    @SuppressWarnings("unchecked")
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        
        final DirectedGraph graph = new DirectedGraph();
        final Node layoutNode = new Node();
        layoutNode.width = 1;
        layoutNode.height = 0;
        layoutNode.setPadding(new Insets());
        graph.nodes.add(layoutNode);
        oldLocations.clear();
        
        Collection<?> selected = viewer.getSelectedEditParts();
        if(selected.isEmpty()) {
            selected = viewer.getEditPartRegistry().values();
        }

        //build the graph nodes
        Rectangle oldExtent = new Rectangle();
        for (Iterator<?> it = selected.iterator(); it.hasNext();) {
            Object next = it.next();
            if(next instanceof TableEditPart && !oldLocations.containsKey(next)) {
                TableEditPart tablePart = (TableEditPart)next;
                Point location = tablePart.getLocation();
                oldExtent.union(location);
                oldLocations.put(tablePart, location);
                
                Node node = tablePart.createLayoutNode();
                graph.nodes.add(node);
                graph.edges.add(new Edge(layoutNode, node));
            }
        }
        //build graph edges and remember stub connections
        stubs.clear();
        for (AbstractTableEditPart tablePart : oldLocations.keySet()) {
            for (Iterator<?> i2 = tablePart.getSourceConnections().iterator(); i2.hasNext();) {
                ConnectionEditPart connection = (ConnectionEditPart) i2.next();
                
                if(connection instanceof ForeignKeyEditPart && oldLocations.containsKey(connection.getTarget())) {
                	DForeignKey dFk=(DForeignKey) connection.getModel();
                	ForeignKey fk = dFk.getForeignKey(); 
                	if (fk.getTable()!=fk.getTargetTable()) {
	                	graph.edges.add(((ForeignKeyEditPart)connection).createLayoutEdge());
	                    // invalidate anchor positions, so that after layouting the position is computed with the chopbox algorithm	                    
	                    dFk.invalidate();
                	}
                }
                else {
                    stubs.add(connection);
                }
            }
        }
        //compute the layout
        LayoutHelper.layout(graph, oldExtent.getCenter());
        
        //apply the layout to the edit parts
        for (AbstractTableEditPart tablePart : oldLocations.keySet()) {
            tablePart.applyLayoutNode(false);
        }
        //apply positions to stubs
        for (ConnectionEditPart connection : stubs) {
            //TODO hannes V1: class cast exception!            
            TableStubEditPart stubPart = (TableStubEditPart)connection.getTarget();
            TableEditPart tablePart = (TableEditPart)connection.getSource();
            
            Node node = tablePart.getLayoutNode();
            stubPart.setOwnerBounds(node.x, node.y, node.width, node.height);
        }
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        //apply saved positions to tables
        for (Map.Entry<TableEditPart, Point> entry : oldLocations.entrySet()) {
            TableEditPart tablePart = entry.getKey();
            Point location = entry.getValue();
            
            tablePart.getDTable().setLocation(location);
        }
        //apply positions to stubs
        for (ConnectionEditPart connection : stubs) {
            TableStubEditPart stubPart = (TableStubEditPart)connection.getTarget();
            TableEditPart tablePart = (TableEditPart)connection.getSource();
            
            Point location = tablePart.getDTable().getLocation();
            stubPart.setOwnerBounds(location.x, location.y, -1, -1);
        }
        return Status.OK_STATUS;
    }
}
