/*
 * created 27.09.2005 by sell
 *
 * $Id: MarqueeSelectionTool2.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.gef.tools.MarqueeSelectionTool;

/**
 * This class provides a rather hacky way of allowing interested parties (e.g. column edit parts) 
 * to determine that a marquee selection is in progress, e.g. to avoid selection. GEF currently 
 * does not provide a way to distinguish selection requests that originate from marquee selection
 * (or, more generally, "sweeping" selection requests that include parent and child elements) from
 * regular requests. Therefore, ColumnEditPart cannot normally avoid to be selected when the 
 * owning table is selected by the marquee tool.
 * 
 * @author sell
 */
public class MarqueeSelectionTool2 extends MarqueeSelectionTool {

    public static boolean inProgress;
    
    protected boolean handleDragInProgress() {
        inProgress = true;
        return super.handleDragInProgress();
    }

    protected boolean handleButtonUp(int button) {
        try {
            return super.handleButtonUp(button);
        }
        finally {
            inProgress = false;
        }
    }
    
    public static class DragTracker extends MarqueeSelectionTool2 implements org.eclipse.gef.DragTracker {
        protected void handleFinished() { }
    }
}
