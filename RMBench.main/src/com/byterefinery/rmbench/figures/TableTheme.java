/*
 * created 17.09.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableTheme.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.swt.graphics.Color;

/**
 * encapsulates the color choices for a table figure 
 * 
 * @author cse
 */
public class TableTheme {

    public Color titleBackground;
    public Color titleForeground;
    public Color bodyBackground;
    public Color bodyForeground;
    
    public TableTheme(
            Color titleForeground,
            Color titleBackground,
            Color bodyForeground,
            Color bodyBackground) {
        
        this.titleBackground = titleBackground;
        this.titleForeground = titleForeground;
        this.bodyBackground = bodyBackground;
        this.bodyForeground = bodyForeground;
    }
}
