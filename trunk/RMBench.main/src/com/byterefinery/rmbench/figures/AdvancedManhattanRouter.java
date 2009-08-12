/*
 * created Apr 12, 2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id$
 */
package com.byterefinery.rmbench.figures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.geometry.Rectangle;

public class AdvancedManhattanRouter extends AbstractRouter {

    private final int MIN_TABLE_DISTANCE = 10;
    private static Ray  UP      = new Ray(0, -1),
                        DOWN    = new Ray(0, 1),
                        LEFT    = new Ray(-1, 0),
                        RIGHT   = new Ray(1, 0);
    
    /** layer which contains the TableFigures */
    private final IFigure layer;
    
    /** list containing the points created from the start point*/
    private final List<Point> startPoints;
        
    private PolylineConnectionEx connection;
    
        
    public AdvancedManhattanRouter(IFigure layer) {
        super();
        this.layer = layer;
        startPoints = new ArrayList<Point>();
    }

   
    
    public void route(Connection connection) {
        PointList pointList = new PointList();
        Point start;
        Point end;
        
        this.connection = (PolylineConnectionEx) connection;
        
        startPoints.clear();
        
        start=new Point(getStartPoint(connection));
        connection.translateToRelative(start);
        end=new Point(getEndPoint(connection));
        connection.translateToRelative(end);
        
        startPoints.add(start);
   
        Ray startDir = getStartDirection(connection);
        Point nextStart=new Point(start);
        RotatableDecoration tmpDeco=this.connection.getSourceDecoration();
        int distance = (tmpDeco!=null) ? tmpDeco.getBounds().height+2 : 2;
        if (startDir==LEFT) {
            nextStart.x -= distance;
        } else if (startDir==RIGHT) {
            nextStart.x += distance;
        } else if (startDir==UP) {
            nextStart.y -= distance;
        } else if (startDir==DOWN) {
            nextStart.y += distance;
        }
        
        Ray endDir = getEndDirection(connection);
        Point nextEnd=new Point(end);
        tmpDeco=this.connection.getTargetDecoration();
        distance = (tmpDeco!=null) ? tmpDeco.getBounds().height+2 : 2;
        if (endDir==LEFT) {
            nextEnd.x -= distance;
        } else if (endDir==RIGHT) {
            nextEnd.x += distance;
        } else if (endDir==UP) {
            nextEnd.y -= distance;
        } else if (endDir==DOWN) {
            nextEnd.y += distance;
        }
        
        startPoints.add(nextStart);
        connectPoints(nextStart, nextEnd, startDir);
        
        for (int i=0; i<startPoints.size(); i++)
            pointList.addPoint((Point) startPoints.get(i));        
        
        pointList.addPoint(end);
        connection.setPoints(pointList);
    }

    /**
     * Checks if the line specified by the start and end point goes over one or mor table figures. <br/>
     * 
     * The returnes point isn't exactly the position of the collision. The method moves the point a about 10px away so the next segment of the 
     * connection won't draw on the table border.
     *  
     * @param start startPoint of the line
     * @param end endPoint of the line
     * @param direction 
     * @return null, or the point where the collision occures
     */
    private Point getCollisionPoint(Point start, Point end, Ray direction) {
        Point collPoint = null;
        for (Iterator <?> it=layer.getChildren().iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof IFigure) {
                IFigure figure = (IFigure) obj;
                
                Rectangle rec = figure.getBounds().getCopy();
                connection.translateToRelative(rec);
                if ((start.x==end.x) && (start.y>end.y)) {
                    //check bottom line of figure
                    if ( (start.x>=rec.x) && (start.x<=(rec.x+rec.width))) {
                        int bottomY = rec.y+rec.height;
                        if ( (start.y>bottomY) && (end.y<bottomY) ) {
                            //we have a collision
                            //int newX = ((end.x-start.x)<0) ? rec.x-MIN_TABLE_DISTANCE : rec.x+rec.width+MIN_TABLE_DISTANCE;
                            int newX = (direction==LEFT) ? rec.x-MIN_TABLE_DISTANCE : rec.x+rec.width+MIN_TABLE_DISTANCE;
                            if (collPoint==null)
                                collPoint=new Point(newX, start.y);
                            else if ( (direction==LEFT && collPoint.x<newX) ||
                                      (direction==RIGHT && collPoint.x>newX) ) {
                                collPoint.x = newX;
                                collPoint.y = start.y;
                            }
                        }
                    }
                  
                } else if ((start.x==end.x) && (start.y<end.y)) {
                    //check top line
                    if ( (start.x>=rec.x) && (start.x<=(rec.x+rec.width))) {
                        if ( (start.y>rec.y) && (end.y<rec.y) ) {
                            //we have a collision
                            //int newX = ((end.x-start.x)<0) ? rec.x-MIN_TABLE_DISTANCE : rec.x+rec.width+MIN_TABLE_DISTANCE;
                            int newX = (direction==LEFT) ? rec.x-MIN_TABLE_DISTANCE : rec.x+rec.width+MIN_TABLE_DISTANCE;
                            if (collPoint==null)
                                collPoint=new Point(newX, end.y);
                            else if ( (direction==LEFT && collPoint.x<newX) ||
                                      (direction==RIGHT && collPoint.x>newX) ) {
                                collPoint.x = newX;
                                collPoint.y = end.y;
                            }
                        }
                    }
                    
                } else if ((start.y==end.y) &&(start.x<end.x)) {
                    //check left line
                    if ( (start.y>=rec.y) && (start.y<=rec.y+rec.height)) {
                        if ( (start.x<rec.x) && (end.x>rec.x)) {
                            //collision found
                            int newY = (direction==UP) ? rec.y - MIN_TABLE_DISTANCE : rec.y+rec.height+MIN_TABLE_DISTANCE;
                            if (collPoint==null)
                                collPoint=new Point(start.x, newY);
                            else if ( (direction==UP && collPoint.y<newY) ||
                                    (direction==DOWN && collPoint.y>newY) ) {
                              collPoint.x = start.x;
                              collPoint.y = newY;
                          }
                        }
                    }
                    
                } /*else if (direction==RIGHT) {
                    
                }
                */

            }
        }
        return collPoint;
    }
    private void connectPoints(Point start, Point end, Ray direction) {
        Point tmpStart=start;
        Point tmpEnd;
        
        do {
            tmpEnd=new Point(end);
            //get straight line
            if (!isStraightLine(tmpStart, tmpEnd)) {
                int distance = Math.abs(tmpStart.x-tmpEnd.x);
                Ray newDirection = (tmpStart.x<tmpEnd.x) ? RIGHT : LEFT;
                
                if ( (distance<Math.abs(tmpStart.y-tmpEnd.y)) || (direction.x==(-newDirection.x)) ){
                     Ray tmp = (tmpStart.y<tmpEnd.y) ? DOWN : UP;
                     if (direction.y!=(-tmp.y)) //check if we go in the opposite direction where we come from, if no, okay
                         newDirection = tmp; 
                }
                
                direction = newDirection;
                
                if ((direction==LEFT) || (direction==RIGHT)) {
                    tmpEnd.y = tmpStart.y;
                }
                else {
                    tmpEnd.x = tmpStart.x;
                } 
            }
            //check collsion
            Point coll=getCollisionPoint(tmpStart, tmpEnd, direction);
            if (coll!=null) {
                tmpEnd=coll;
            }

            startPoints.add(tmpEnd);
            
            tmpStart = tmpEnd;
            
        } while (!tmpEnd.equals(end));
        
    }
    
    private boolean isStraightLine(Point start, Point end) {
        return ( (start.x==end.x) || (start.y==end.y) );            
    }
    
    
  

    /**
     * Returns the direction the point <i>p</i> is in relation to the given rectangle.
     * Possible values are LEFT (-1,0), RIGHT (1,0), UP (0,-1) and DOWN (0,1).
     * 
     * @param r the rectangle
     * @param p the point
     * @return the direction from <i>r</i> to <i>p</i>
     */
    protected Ray getDirection(Rectangle r, Point p) {
        int i, distance = Math.abs(r.x - p.x);
        Ray direction;
        
        direction = LEFT;

        i = Math.abs(r.y - p.y);
        if (i <= distance) {
            distance = i;
            direction = UP;
        }

        i = Math.abs(r.bottom() - p.y);
        if (i <= distance) {
            distance = i;
            direction = DOWN;
        }

        i = Math.abs(r.right() - p.x);
        if (i < distance) {
            distance = i;
            direction = RIGHT;
        }

        return direction;
    }
    
    protected Ray getDirection(Ray r, Point p) {
        int i, distance = Math.abs(r.x - p.x);
        Ray direction;
        
        direction = LEFT;

        i = Math.abs(r.y - p.y);
        if (i <= distance) {
            distance = i;
            direction = UP;
        }

        return direction;
    }
    
       
    protected Ray getEndDirection(Connection conn) {
        ConnectionAnchor anchor = conn.getTargetAnchor();
        Point p = getEndPoint(conn);
        Rectangle rect;
        if (anchor.getOwner() == null) {
            System.out.println("Acnhor owner == null");
            rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
        }
        else {
            rect = conn.getTargetAnchor().getOwner().getBounds().getCopy();
            conn.getTargetAnchor().getOwner().translateToAbsolute(rect);
        }
        return getDirection(rect, p);
    }
    
    protected Ray getStartDirection(Connection conn) {
        ConnectionAnchor anchor = conn.getSourceAnchor();
        Point p = getStartPoint(conn);
        Rectangle rect;
        if (anchor.getOwner() == null)
            rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
        else {
            rect = conn.getSourceAnchor().getOwner().getBounds().getCopy();
            conn.getSourceAnchor().getOwner().translateToAbsolute(rect);
        }
        return getDirection(rect, p);
    }
    
    
    
}
