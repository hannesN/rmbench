/*
 * created 28.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: LayoutHelper.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.jface.viewers.StructuredSelection;

import com.byterefinery.rmbench.editparts.DiagramEditPart;
import com.byterefinery.rmbench.editparts.TableEditPart;
import com.byterefinery.rmbench.editparts.TableStubEditPart;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.model.diagram.DTableStub;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * layout utility class. Internally, it uses a {@link org.eclipse.draw2d.graph.DirectedGraphLayout}
 * to do the actual work.
 * 
 * @author cse
 * @see org.eclipse.draw2d.graph.DirectedGraphLayout
 */
public final class LayoutHelper {

    private LayoutHelper() {
    }

    /**
     * layout the given graph around the given center point
     * @param graph the graph, whose nodes will be updated
     * @param location the target location
     */
    public static void layout(DirectedGraph graph, Point location) {
        
        new DirectedGraphLayout().visit(graph);
        Rectangle newExtent = new Rectangle();
        for (Iterator<?> it = graph.nodes.iterator(); it.hasNext();) {
            Node node = (Node) it.next();
            newExtent.union(node.x, node.y);
        }
        Dimension move = location.getDifference(newExtent.getCenter());
        for (Iterator<?> it = graph.nodes.iterator(); it.hasNext();) {
            Node node = (Node) it.next();
            node.x += move.width;
            node.y += move.height;
        }
    }
    
    /**
     * layout the list of tables within the given diagram before adding them to both the 
     * underlying diagram model object and the diagram edit part.<p>
     * The layout is done through a {@link DirectedGraphLayout} and applied to tables only (i.e., 
     * relationships are left to automatic routing)
     * 
     * @param diagramPart the target diagram part
     * @param dtables a list of DTables to add
     * @param location the target location around which to layout the tables
     * 
     * @return OK_STATUS if all went through, CANCEL_STATUS if tables is empty or contained only
     * tables that were already part of the diagram
     */
    @SuppressWarnings("unchecked")
	public static IStatus addAndLayoutTables(
            DiagramEditPart diagramPart, Collection<DTable> dtables, Point location) {
        
        if(dtables.isEmpty())
            return Status.CANCEL_STATUS;

        final DirectedGraph graph = new DirectedGraph();
        
        //this is the dummy root node
        final Node layoutNode = new Node();
        layoutNode.width = 1;
        layoutNode.height = 0;
        layoutNode.setPadding(new Insets());
        graph.nodes.add(layoutNode);
        
        diagramPart.ignoreDiagramEvents();
        diagramPart.getViewer().deselectAll();
        
        //first add all tables to diagram, so the displayable references can be computed correctly
        diagramPart.getDiagram().addNewTables(dtables);
        if(dtables.isEmpty())
            return Status.CANCEL_STATUS;
        
        //now prepare the graph, by first creating parts for all tables nodes 
        //and connecting them to the dummy node
        Map<Table, Node> nodeMap = new HashMap<Table, Node>(dtables.size());
        for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
            DTable dtable = (DTable) it.next();
            TableEditPart tablePart = (TableEditPart)diagramPart.addChild(dtable);
            
            Node node = tablePart.createLayoutNode();
            nodeMap.put(dtable.getTable(), node);
            graph.nodes.add(node);
            graph.edges.add(new Edge(layoutNode, node));
        }
        //now create edges for all foreign keys
        for (Iterator<DTable> it = dtables.iterator(); it.hasNext();) {
            DTable dtable = it.next();
            for (Iterator<DForeignKey> i2 = dtable.getForeignKeys().iterator(); i2.hasNext();) {
                ForeignKey fk = i2.next().getForeignKey();
                
                if (fk.getTable()!=fk.getTargetTable()) {
	                Node source = (Node)nodeMap.get(fk.getTable());
	                Node target = (Node)nodeMap.get(fk.getTargetTable());
	                
	                if(target != null)
	                    graph.edges.add(new Edge(fk, source, target));
                }
            }
        }
        //compute the layout
        LayoutHelper.layout(graph, location);

        //apply the layout
        List<TableEditPart> selection = new ArrayList<TableEditPart>(graph.nodes.size());
        for (Iterator<?> it = graph.nodes.iterator(); it.hasNext();) {
            Node node = (Node) it.next();
            if(node != layoutNode) {
                TableEditPart tablePart = (TableEditPart)node.data;
                tablePart.applyLayoutNode(true);
                
                selection.add(tablePart);
            }
        }
        //now create parts for the new stubs and notify them about the position of the owner
        for (Iterator<DTableStub> it = diagramPart.getDiagram().getTableStubs().iterator(); it.hasNext();) {
            DTableStub stub = (DTableStub) it.next();

            Node ownerNode = (Node)nodeMap.get(stub.getDTable().getTable());
            if(ownerNode != null) {
                TableStubEditPart stubPart = (TableStubEditPart)diagramPart.addChild(stub);
                TableEditPart ownerPart = (TableEditPart)ownerNode.data;
                
                //the owner figure position is not yet set - we need to use the node positions 
                Rectangle rect = ownerPart.getFigure().getBounds().getCopy();
                rect.x = ownerNode.x;
                rect.y = ownerNode.y;
                stubPart.setOwnerBounds(rect.x, rect.y, rect.width, rect.height);
            }
        }
        diagramPart.getViewer().setSelection(new StructuredSelection(selection.toArray()));
        diagramPart.watchDiagramEvents();
        return Status.OK_STATUS;
    }
}
