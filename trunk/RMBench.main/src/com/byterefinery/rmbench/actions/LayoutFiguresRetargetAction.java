/*
 * created 04.04.2005
 * 
 * $Id: LayoutFiguresRetargetAction.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.RetargetAction;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * Retarget action for laying out the currently selected figures, or all figures in a 
 * diagram if none are selected
 * 
 * @author cse
 */
public class LayoutFiguresRetargetAction extends RetargetAction {

    public LayoutFiguresRetargetAction() {
        super(LayoutFiguresAction.ACTION_ID, Messages.Layout_text, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Layout_description);
        
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.LAYOUT));
    }
}
