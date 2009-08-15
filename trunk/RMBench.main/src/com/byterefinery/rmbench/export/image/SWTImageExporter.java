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
package com.byterefinery.rmbench.export.image;

import java.io.OutputStream;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import com.byterefinery.rmbench.external.export.AbstractDiagramExporter;

/**
 * Abstract base class for the standard image exporters based on SWT facilities.
 * Only JPEG and BMP formats seem to work
 */
public abstract class SWTImageExporter extends AbstractDiagramExporter {

    public static class JPEG extends SWTImageExporter {
        protected int getImageFormat() {
            return SWT.IMAGE_JPEG;
        }
    }
    
    public static class BMP extends SWTImageExporter {
        protected int getImageFormat() {
            return SWT.IMAGE_BMP_RLE;
        }
    }
    
    /**
     * @return the the SWT image format to produce, normally one of
     * <ul>
     * <li>{@link SWT#IMAGE_JPEG}</li>
     * <li>{@link SWT#IMAGE_BMP_RLE}</li>
     * </ul>
     */
    protected abstract int getImageFormat();

    /**
     * @param out the stream to write to
     * @param figure the figure to export
     */
    protected void doExport(OutputStream out, IFigure figure) {
        
        Rectangle bounds = getBounds(figure);
        Image image = new Image(Display.getDefault(), bounds.width, bounds.height);
        GC gc = new GC(image);
        // gc.setAntialias(SWT.ON);
        gc.setTextAntialias(SWT.ON);
        Graphics graphics = new SWTGraphics(gc);
        graphics.translate(bounds.getLocation().negate());
        figure.paint(graphics);
        gc.dispose();

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[] { image.getImageData() };
        imageLoader.save(out, getImageFormat());
        
        image.dispose();
    }
}
