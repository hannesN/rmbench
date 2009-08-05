/*
 * created 25-Jan-2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id: TableStubTooltipFigure.java 414 2006-07-12 18:42:24Z hannesn $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.schema.Table;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * figure that shows the external table references 
 * 
 * @author cse
 */
public class TableStubTooltipFigure extends Figure {

    public TableStubTooltipFigure() {
        super();
        ToolbarLayout layout = new ToolbarLayout(false);
        setLayoutManager(layout);
    }
    
    public void addIncoming(Table table) {
        Label label = new Label();
        label.setIcon(RMBenchPlugin.getImage(ImageConstants.LEFT_ARROW));
        label.setText(table.getFullName());
        add(label);
    }
    
    public void addOutgoing(Table table) {
        Label label = new Label();
        label.setText(table.getFullName());
        label.setIcon(RMBenchPlugin.getImage(ImageConstants.RIGHT_ARROW));
        add(label);
    }
    
    public void addInAndOutgoing(Table table) {
        Label label = new Label();
        label.setText(table.getFullName());
        label.setIcon(RMBenchPlugin.getImage(ImageConstants.LEFT_RIGHT_ARROW));
        add(label);
    }
}
