/*
 * created 29-Jan-2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id$
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;



/**
 * This Anchor locates the anchor in a selectable corner of the owning figure.
 * 
 * @author Hannes Niederhausen
 *
 */
public class CornerAnchor extends AbstractConnectionAnchor {

    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;
    
    private final int corner;
    
    /**
     * @param owner the owning figure
     * @param corner the corner in which to place the anchor - one of
     * <ul>
     * <li>{@link #TOP_LEFT}</li>
     * <li>{@link #TOP_RIGHT}</li>
     * <li>{@link #BOTTOM_LEFT}</li>
     * <li>{@link #BOTTOM_RIGHT}</li>
     * </ul>
     */
    public CornerAnchor(IFigure owner, int corner) {
        super(owner);
        this.corner = corner;
    }

    public Point getLocation(Point reference) {
        Point location;
        
        switch(corner) {
            case TOP_LEFT: {
                location = getOwner().getBounds().getTopLeft();
                break;
            }
            case TOP_RIGHT: {
                location = getOwner().getBounds().getTopRight();
                break;
            }
            case BOTTOM_LEFT: {
                location = getOwner().getBounds().getBottomLeft();
                break;
            }
            case BOTTOM_RIGHT: {
                location = getOwner().getBounds().getBottomRight();
                break;
            }
            default: {
                location = getOwner().getBounds().getTopRight();
                break;
            }
        }
        getOwner().translateToAbsolute(location);
        return location;
    }
}
