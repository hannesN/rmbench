/*
 * created 24.02.2006
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
package com.byterefinery.rmbench.external;

import java.io.InputStream;

import org.eclipse.draw2d.IFigure;

/**
 * extension interface for image exporters
 * 
 * @author cse
 */
public interface IImageExporter {
    
    /**
     * @param figure the figure to export
     * @return a stream on the export data
     */
    public InputStream export(IFigure figure);
}
