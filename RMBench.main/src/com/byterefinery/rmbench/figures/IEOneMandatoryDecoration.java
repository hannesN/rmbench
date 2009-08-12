/*
 * created 10.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: IEOneMandatoryDecoration.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;

/**
 * represents a mandatory to-one reference in IE (crow's foot) notation by drawing
 * 2 orthogonal lines near the connections end
 * 
 * @author cse
 */
public class IEOneMandatoryDecoration extends TemplateDecoration {

    protected static final PointList TEMPLATE = new PointList();
    static {
        TEMPLATE.addPoint(-7, 10);
        TEMPLATE.addPoint(-7, -10);
        TEMPLATE.addPoint(-11, 10);
        TEMPLATE.addPoint(-11, -10);
    }

    //@see com.byterefinery.rmbench.figures.TemplateDecoration#getTemplate()
    protected PointList getTemplate() {
        return TEMPLATE;
    }

    //@see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
    protected void outlineShape(Graphics graphics) {
        graphics.drawLine(getPoint(0), getPoint(1));
        graphics.drawLine(getPoint(2), getPoint(3));
    }
}
