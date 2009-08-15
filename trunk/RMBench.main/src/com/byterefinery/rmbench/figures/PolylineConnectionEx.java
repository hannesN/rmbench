/*
 * created 25.04.2006
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
