/*
 * created 13.05.2005
 * 
 * $Id: URLSetupGroup.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.external.database.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.byterefinery.rmbench.external.IURLSetupGroup;

/**
 * This is a convenience superclass for implementors of the {@link com.byterefinery.rmbench.external.IURLSetupGroup}
 * interface. It will create a group box or simple composite, depending on the return value
 * of the {@link #getGroupTitle()} method. Subclasses can then add the individual edit widgets
 * by implementing the {@link #createEditArea(Composite)} method
 * 
 * @author cse
 */
public abstract class URLSetupGroup implements IURLSetupGroup {

    private final List<Listener> completeListeners = new ArrayList<Listener>(2);
    
    protected IURLSetupGroup.Context context;
    
    protected Composite mainComposite; 
        
    /**
     * @context the configuration context
     */
    public URLSetupGroup(IURLSetupGroup.Context context) {
        
        this.context = context;
    }

    /**
     * create the widgets that are displayed to the user.
     * <em>Note: this will call the {@link #createEditArea} method which must be 
     * implemented by subclasses</em>
     * 
     * @param parent the parent composite
     */
    public final void createWidgets(Composite parent) {
    	
        mainComposite = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        mainComposite.setLayout(layout);
        
    	Composite widgetArea;
        String title = getGroupTitle();
        if(title != null) {
            Group group = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        	group.setText(title);
        	widgetArea = group;
        }
        else {
        	widgetArea = new Composite(mainComposite, SWT.NONE);
        }
        widgetArea.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL | 
                GridData.VERTICAL_ALIGN_BEGINNING |
                GridData.FILL_VERTICAL));
        
        layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        widgetArea.setLayout(layout);
        
        createEditArea(widgetArea);
    }
    
    public void addListener(Listener listener) {
        completeListeners.add(listener);
    }
    
    public void removeListener(Listener listener) {
        completeListeners.remove(listener);
    }
    
    public void disposeWidgets() {
        mainComposite.dispose();
    }

    public Control getControl() {
        return mainComposite;
    }
    
    /**
     * notify all listeners about a change in the completed state
     */
    protected void fireCompleteEvent(boolean completed) {
        for (Listener listener : completeListeners) {
            listener.inputCompleted(completed);
        }
    }
    
    /**
     * notify all listeners about an input error
     */
    protected void fireErrorEvent(String errorMessage) {
        for (Listener listener : completeListeners) {
            listener.errorOccured(errorMessage);
        }
    }
    
    /**
     * returns the title for the enclosing group box, or <code>null</code> if no group
     * box is to be displayed.
     * @return <code>null</code>
     */
    protected String getGroupTitle() {
    	return null;
    }
    
    /**
     * create the area with the driver setup input widgets
     * @param parent the parent of all widgets created
     */
    protected abstract void createEditArea(Composite parent);
    
}
