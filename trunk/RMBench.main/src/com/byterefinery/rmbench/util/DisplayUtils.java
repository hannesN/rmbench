/*
 * created 08.08.2005 by sell
 *
 * $Id: DisplayUtils.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

/**
 * miscellaneous display-related utiliy functions
 * 
 * @author sell
 */
public class DisplayUtils {

    /**
     * @param control a GUI control
     * @param numChars the number of characters
     * @return the width in pixels used by the current font of the given control to display
     * the given number of chracters
     */
    public static int computeWidth(Control control, int numChars) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        int width = gc.getFontMetrics().getAverageCharWidth();
        gc.dispose();
        return width * numChars;
    }
}
