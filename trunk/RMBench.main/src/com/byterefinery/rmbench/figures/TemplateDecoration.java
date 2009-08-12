/*
 * created 11.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TemplateDecoration.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Transform;

/**
 * superclass for template-based connection decorations. The points for the drawing of
 * the actual shape are made available by transforming/rotating the points from the 
 * template as appropriate. 
 * 
 * @author cse
 */
public abstract class TemplateDecoration extends Shape implements RotatableDecoration {
    
    private final Point location = new Point();
    private final Transform transform  = new Transform();
    
    private PointList points;

    /**
     * @return the template PointList that describes the outline of the decoration 
     */
    protected abstract PointList getTemplate();
    
    /**
     * Only to be called in drawing routines
     * @return the ready transformed point that corresponds to the template at
     * the given index
     * @see #getTemplate()
     */
    protected Point getPoint(int index) {
        return points.getPoint(index);
    }
    
    /**
     * optional hook for subclasses
     * @param points the newly transformed points
     */
    protected void pointsRecomputed(PointList points) {
    }

    public void setLocation(Point p) {
        points = null;
        bounds = null;
        location.setLocation(p);
        transform.setTranslation(p.x, p.y);
    }
    
    public Rectangle getBounds() {
        if (bounds == null) {
            bounds = getPoints()
                .getBounds()
                .getExpanded(lineWidth / 2, lineWidth / 2);
        }
        return bounds;
    }

    private PointList getPoints() {
        if (points == null) {
            points = new PointList(getTemplate().size());
            for (int i = 0; i < getTemplate().size(); i++) {
                points.addPoint(transform.getTransformed(getTemplate().getPoint(i)));
            }
            pointsRecomputed(points);
        }
        return points;
    }
    
    //@see org.eclipse.draw2d.RotatableDecoration#setReferencePoint(org.eclipse.draw2d.geometry.Point)
    public void setReferencePoint(Point ref) {
        points = null;
        bounds = null;
        
        Point pt = Point.SINGLETON;
        pt.setLocation(ref);
        pt.negate().translate(location);
        transform.setRotation(Math.atan2(pt.y, pt.x));
    }

    //@see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
    protected void fillShape(Graphics graphics) {
    }

    public void primTranslate(int x, int y) {
    }
}
