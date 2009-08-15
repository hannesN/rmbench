/*
 * created 10.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IEOneOptionalDecoration.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * represents an optional to-one reference in IE (crow's foot) notation, by drawing an
 * orthogonal line and a circle at the connection end
 * 
 * @author cse
 */
public class IEOneOptionalDecoration extends TemplateDecoration {

    protected static final PointList TEMPLATE = new PointList();
    static {
        TEMPLATE.addPoint(-7, 10);
        TEMPLATE.addPoint(-7, -10);
        TEMPLATE.addPoint(-10, 5);
        TEMPLATE.addPoint(-20, -5);
    }

    private Rectangle circleRect = new Rectangle();

    protected void pointsRecomputed(PointList points) {
        circleRect = new Rectangle(points.getPoint(3), points.getPoint(2));
    }

    protected PointList getTemplate() {
        return TEMPLATE;
    }
    
    //@see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
    protected void outlineShape(Graphics graphics) {
        graphics.drawLine(getPoint(0), getPoint(1));
        
        graphics.setBackgroundColor(ColorConstants.white);
        graphics.fillOval(circleRect);
        graphics.drawOval(circleRect);
    }
}
