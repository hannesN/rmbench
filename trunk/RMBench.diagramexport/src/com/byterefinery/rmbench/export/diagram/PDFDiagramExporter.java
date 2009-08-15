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
 * $Id: PDFDiagramExporter.java 472 2006-08-21 14:44:02Z cse $
 */
package com.byterefinery.rmbench.export.diagram;

import java.awt.Graphics2D;
import java.io.OutputStream;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.render.internal.graphics.GraphicsToGraphics2DAdaptor;

import com.byterefinery.rmbench.export.ExportPlugin;
import com.byterefinery.rmbench.external.export.AbstractDiagramExporter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * an exporter that will create a PDF file from a diagram
 * 
 * @author cse
 */
public class PDFDiagramExporter extends AbstractDiagramExporter {

    protected void doExport(OutputStream out, IFigure figure) {
        
        Rectangle bounds = getBounds(figure);
        Document document = new Document(new com.lowagie.text.Rectangle(bounds.width, bounds.height));

        PdfWriter pdf;
        try {
            pdf = PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Chunk(" "));
        }
        catch (DocumentException e) {
            ExportPlugin.logError(e);
            return;
        }
        PdfContentByte contentbytes = pdf.getDirectContent();
        PdfTemplate template = contentbytes.createTemplate(bounds.width, bounds.height);
        Graphics2D graphics2d = template.createGraphics(bounds.width, bounds.height);
        try {
            GraphicsToGraphics2DAdaptor graphics = new GraphicsToGraphics2DAdaptor(graphics2d, bounds
                    .getTranslated(bounds.getLocation().negate()));
            graphics.translate(bounds.getLocation().negate());
            figure.paint(graphics);
        }
        finally {
            graphics2d.dispose();
            contentbytes.addTemplate(template, 0, 0);
            document.close();
        }
    }
}
