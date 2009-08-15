/*
 * created 20.11.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.views.property;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.byterefinery.rmbench.operations.RMBenchOperation;

/**
 * a custom property sheet page that enables the RMBench undo/redo actions
 * 
 * @author cse
 */
public class RMBenchPropertySheetPage extends PropertySheetPage {

    private static RMBenchPropertySheetPage instance;
    
    private UndoRedoActionGroup undoRedoGroup;
    
    public RMBenchPropertySheetPage() {
        super();
        setRootEntry(new RMBenchPropertySheetEntry());
    }
    
    public void setActionBars(IActionBars actionBars) {
        super.setActionBars(actionBars);
        
        IWorkbenchPartSite partSite = getSite().getPage().getActivePart().getSite();
        undoRedoGroup = new UndoRedoActionGroup(partSite, RMBenchOperation.CONTEXT, false);
        undoRedoGroup.fillActionBars(actionBars);
    }

    public void dispose() {
        instance = null;
        super.dispose();
    }

    /**
     * @return the current instance, possibly creating it first
     */
    public static synchronized RMBenchPropertySheetPage getInstance() {
        if(instance == null) {
            instance = new RMBenchPropertySheetPage();
        }
        return instance;
    }
}
