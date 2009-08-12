/*
 * created 03.08.2005 by cse
 *
 * $Id: ConnectionLabelLocator.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Locator for connections that will locate the label in the middle of a line 
 * such that it does not cross the line
 * 
 * @author cse
 */
public class ConnectionLabelLocator extends ConnectionLocator {

    private final Label label;
    private Dimension dimension;
    
    /**
     * @param connection the connection on which to place the label
     */
    public ConnectionLabelLocator(Connection connection, Label label) {
        super(connection);
        this.label = label;
    }

    protected Point getLocation(PointList points) {
        if(dimension == null) {
            dimension = label.getPreferredSize();
        }
        if (points.size() % 2 == 0) {
            int i = points.size() / 2;
            int j = i - 1;
            Point p1 = points.getPoint(j);
            Point p2 = points.getPoint(i);
            Dimension d = p2.getDifference(p1);
            int height = Math.max(dimension.height, d.height);
            return Point.SINGLETON.setLocation(p1.x + d.width / 2, p1.y + height / 2);
        } else {
            int i = (points.size() - 1) / 2;
            return points.getPoint(Point.SINGLETON, i);
        }
    }
}
