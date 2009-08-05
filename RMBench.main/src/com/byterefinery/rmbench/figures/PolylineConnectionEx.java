/*
 * created 25.04.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 *
 * $Id$
 */
/**
 * 
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;

/**
 * subclassed to make methods public for use in ManhattanRouter
 * 
 * @author Hannes Niederhausen
 */
public class PolylineConnectionEx extends PolylineConnection {

    public RotatableDecoration getTargetDecoration() {
        return super.getTargetDecoration();
    }
    
    public RotatableDecoration getSourceDecoration() {
        return super.getSourceDecoration();
    }
}
