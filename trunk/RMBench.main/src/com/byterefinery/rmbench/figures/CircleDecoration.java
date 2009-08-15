/*
 * created 08.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: CircleDecoration.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * circle decoration to be placed on the child (source) end of a connection  
 * 
 * @author cse
 */
public class CircleDecoration extends Ellipse implements RotatableDecoration {

    private static final int DIAMETER = 10;
    private static final int RADIUS = 5;
    
    public CircleDecoration() {
        setBounds(new Rectangle(0, 0, DIAMETER, DIAMETER));
        setOpaque(true);
        setBackgroundColor(ColorConstants.black);
    }
    
    public void setReferencePoint(Point ref) {
        Point location = getLocation();
        
        if(ref.x == location.x)
            location.x -= RADIUS;
        else if(ref.x < location.x)
            location.x -= DIAMETER;
        
        if(ref.y == location.y)
            location.y -= RADIUS;
        else if(ref.y < location.y)
            location.y -= DIAMETER;
        
        setLocation(location);
    }
}