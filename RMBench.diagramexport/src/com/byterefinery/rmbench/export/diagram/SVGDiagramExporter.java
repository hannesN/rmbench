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
 * $Id: SVGDiagramExporter.java 472 2006-08-21 14:44:02Z cse $
 */
package com.byterefinery.rmbench.export.diagram;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.render.internal.svg.export.GraphicsSVG;

import com.byterefinery.rmbench.export.ExportPlugin;
import com.byterefinery.rmbench.external.export.AbstractDiagramExporter;

/**
 * an exporter that will create a SVG file from a diagram
 * 
 * @author cse
 */
public class SVGDiagramExporter extends AbstractDiagramExporter {

    protected void doExport(OutputStream out, IFigure figure) {

        Rectangle bounds = getBounds(figure);
        GraphicsSVG graphics = GraphicsSVG.getInstance(bounds.getTranslated(bounds.getLocation().negate()));
        graphics.translate(bounds.getLocation().negate());
        figure.paint(graphics);
        
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            graphics.getSVGGraphics2D().stream(writer);
        }
        catch (SVGGraphics2DIOException e) {
            ExportPlugin.logError(e);
        } 
        finally {
            graphics = null;
        }
    }
}
