/*
 * created 07.04.2005
 * 
 * $Id: UpdateableAction.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.Action;

/**
 * utility superclass for actions that respond to an update method by
 * computing and setting the enabled state
 * 
 * @author cse
 */
public abstract class UpdateableAction extends Action implements UpdateAction {
    
    protected UpdateableAction(String name, int style) {
        super(name, style);
    }

    protected UpdateableAction() {
        super();
    }
    
    public void update() {
        setEnabled(isEnabled());
    }
}