/*
 * created 26.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: MoveTableOperation.java 668 2007-10-04 18:48:16Z cse $
 */
package com.byterefinery.rmbench.operations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.diagram.DForeignKey;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.preferences.PreferenceHandler;

/**
 * operation that moves a table to a new location, also updating the corresponding editpart if any
 * @author cse
 */
public class MoveTableOperation extends RMBenchOperation {

    private final DTable dtable;

    private final Point oldLocation;

    private final Point newLocation;

    private final Point deltaLoaction;

    private Map<DForeignKey, AnchorState> dForeignKeys  = null;

    public MoveTableOperation(DTable dtable, Point newLocation) {
        super(Messages.Operation_Move);

        this.dtable = dtable;
        this.oldLocation = dtable.getLocation();
        this.newLocation = newLocation;
        this.deltaLoaction = new Point(newLocation.x - oldLocation.x, newLocation.y - oldLocation.y);
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
       
        // find tablereferences and check relative position
        if (RMBenchPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceHandler.PREF_DIAGRAM_RELOCATION)) {
            
            for (DForeignKey dfk : dtable.getReferences())
                addToForeignKeys(dfk);
            
            for (DForeignKey dfk : dtable.getForeignKeys()) {
                addToForeignKeys(dfk);
        }
    }
            
            
        
        return redo(monitor, info);
    }    
    
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        dtable.setLocation(newLocation);
        Point loc;
        if (dtable.getTableStub().isValid()) {
            loc = dtable.getTableStub().getLocation();
            dtable.getTableStub().setLocation(new Point(loc.x+deltaLoaction.x, loc.y+deltaLoaction.y));
        }
        
        if (dForeignKeys!=null) {
            for (DForeignKey dForeignKey : dForeignKeys.keySet()) {
                
                dForeignKey.setTargetValid(false);
                dForeignKey.setSourceValid(false);
            }
        }
        
        return Status.OK_STATUS;
    }
    
    
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        dtable.setLocation(oldLocation);

        if (dForeignKeys!=null) {
            for (DForeignKey dForeignKey : dForeignKeys.keySet()) {
                AnchorState state = (AnchorState) dForeignKeys.get(dForeignKey);
                
                
                dForeignKey.setTargetEdge(state.targetEdge);
                dForeignKey.setTargetSlot(state.targetSlot);
                dForeignKey.setTargetValid(true);
                
                dForeignKey.setSourceEdge(state.sourceEdge);
                dForeignKey.setSourceSlot(state.sourceSlot);
                dForeignKey.setSourceValid(true);
            }
        }
        
        Point loc;
        DTable table = (DTable) dtable;
        if (table.getTableStub().isValid()) {
            loc = table.getTableStub().getLocation();
            table.getTableStub().setLocation(
                    new Point(loc.x - deltaLoaction.x, loc.y - deltaLoaction.y));
        }
        return Status.OK_STATUS;
    }
    
    private void addToForeignKeys(DForeignKey dForeignKey) {
        if (dForeignKeys==null) {
            dForeignKeys = new HashMap<DForeignKey, AnchorState>();
        }
        
        AnchorState state = new AnchorState(dForeignKey.getSourceSlot(), dForeignKey.getSourceEdge(), dForeignKey.getTargetSlot(), dForeignKey.getTargetEdge());
        dForeignKeys.put(dForeignKey, state);
    }
    
    
    private class AnchorState {
        int sourceSlot;
        int sourceEdge;
        int targetSlot;
        int targetEdge;
        
        public AnchorState(int sourceSlot, int sourceEdge, int targetSlot, int targetEdge) {
            super();
            this.sourceSlot = sourceSlot;
            this.sourceEdge = sourceEdge;
            this.targetSlot = targetSlot;
            this.targetEdge = targetEdge;
        }
    }
}
