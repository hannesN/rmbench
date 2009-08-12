/*
 * created 24.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
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
