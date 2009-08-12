/*
 * created 03.04.2005
 * 
 * $Id: TableTreeEditPart.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.byterefinery.rmbench.EventManager;
import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.EventManager.Event;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * edit part to represent table objects in the tree outline view. The outline only 
 * shows tables to allow easy navigation in the diagram. Columns are not shown
 * 
 * @author cse
 */
public class TableTreeEditPart extends AbstractTreeEditPart {
    
    private EventManager.Listener tableListener = new EventManager.Listener() {

        public void eventOccurred(int eventType, Event event) {
            if(event.element != getTable())
                return;
            if(event.info == EventManager.Properties.NAME)
                refreshVisuals();
        }

        public void register() {
            RMBenchPlugin.getEventManager().addListener(TABLE_MODIFIED, this);
        }
    };
	
    public TableTreeEditPart(TableEditPart mainPart) {
        super(mainPart.getTable());
    }

	public void activate() {
		super.activate();
        tableListener.register();
	}

	public void deactivate() {
		super.deactivate();
        tableListener.unregister();
	}

	protected Image getImage() {
        ImageDescriptor desc = 
            AbstractUIPlugin.imageDescriptorFromPlugin(
                    RMBenchConstants.PLUGIN_ID, "icons/table.gif");
        return desc.createImage();
    }

    protected String getText() {
        return getTable().getName();
    }

    private Table getTable() {
		return (Table)getModel();
	}
}
