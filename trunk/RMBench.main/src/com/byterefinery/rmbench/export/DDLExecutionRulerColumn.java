/*
 * created 16.01.2006
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
package com.byterefinery.rmbench.export;

import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a vertical ruler column which paints an arrow marker at the currently executed DDL statement. The
 * viewer is also scrolled to make the statment visible 
 * 
 * @author cse
 */
public class DDLExecutionRulerColumn implements IVerticalRulerColumn {

    private Image fBuffer;
    private ITextViewer fCachedTextViewer;
    private StyledText fCachedTextWidget;
    private Canvas fCanvas;

    private Image execImage;
    
    private transient IDDLScriptContext.Statement statement;
    private int fScrollPos;
    
    private class WidgetListener implements IViewportListener, ITextListener {

        private boolean fCachedRedrawState= true;

        /*
         * @see IViewportListener#viewportChanged(int)
         */
        public void viewportChanged(int verticalPosition) {
            if (fCachedRedrawState && verticalPosition != fScrollPos)
                redraw();
        }

        /*
         * @see ITextListener#textChanged(TextEvent)
         */
        public void textChanged(TextEvent event) {

            fCachedRedrawState= event.getViewerRedrawState();
            if (!fCachedRedrawState)
                return;
            
            statement = null;
            redraw();
        }
    }
    private WidgetListener viewerListener= new WidgetListener();
    
    private final Runnable updater = new Runnable() {
        public void run() {
            redraw();
        }
    };
    
    public Control createControl(CompositeRuler parentRuler, Composite parentControl) {
        
        fCachedTextViewer = parentRuler.getTextViewer();
        fCachedTextWidget = fCachedTextViewer.getTextWidget();
        if(fCachedTextWidget != null) {
            fCachedTextViewer.addViewportListener(viewerListener);
            fCachedTextViewer.addTextListener(viewerListener);
        }
        
        fCanvas = new Canvas(parentControl, SWT.NONE);

        fCanvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                if (fCachedTextViewer != null)
                    doubleBufferPaint(event.gc);
            }
        });

        fCanvas.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                handleDispose();
                fCachedTextViewer= null;
                fCachedTextWidget= null;
            }
        });
        
        execImage = RMBenchPlugin.getImage(ImageConstants.DDL_EXEC);
        return fCanvas;
    }

    public void setStatement(IDDLScriptContext.Statement statement) {
        this.statement = statement;
        Display d= fCanvas.getDisplay();
        if (d != null) {
            d.asyncExec(updater);
        }
    }
    
    public void redraw() {
        if (fCanvas != null && !fCanvas.isDisposed()) {
            GC gc= new GC(fCanvas);
            doubleBufferPaint(gc);
            gc.dispose();
        }
    }

    public Control getControl() {
        return fCanvas;
    }

    public int getWidth() {
        return 16;
    }

    public void setModel(IAnnotationModel model) {
        //not interested
    }

    public void setFont(Font font) {
        //not interested
    }

    private void handleDispose() {
        if (fCachedTextViewer != null) {
            fCachedTextViewer.removeViewportListener(viewerListener);
            fCachedTextViewer.removeTextListener(viewerListener);
        }
        if (fBuffer != null) {
            fBuffer.dispose();
            fBuffer= null;
        }
    }

    /*
     * draw the ruler
     */
    private void doubleBufferPaint(GC dest) {

        Point size= fCanvas.getSize();

        if (size.x <= 0 || size.y <= 0)
            return;

        if (fBuffer != null) {
            Rectangle r= fBuffer.getBounds();
            if (r.width != size.x || r.height != size.y) {
                fBuffer.dispose();
                fBuffer= null;
            }
        }
        if (fBuffer == null)
            fBuffer= new Image(fCanvas.getDisplay(), size.x, size.y);

        GC gc= new GC(fBuffer);
        gc.setFont(fCanvas.getFont());

        try {
            gc.setBackground(fCanvas.getBackground());
            gc.fillRectangle(0, 0, size.x, size.y);

            if (fCachedTextViewer != null && statement != null)
                paintMarker(gc);

        } finally {
            gc.dispose();
        }

        dest.drawImage(fBuffer, 0, 0);
    }

    /*
     * draw the execution marker
     */
    private void paintMarker(GC gc) {

        int topLine= fCachedTextViewer.getTopIndex();
        fScrollPos= fCachedTextWidget.getTopPixel();
        int lineheight= fCachedTextWidget.getLineHeight();
        int partialLineHidden= fScrollPos % lineheight;

        if (partialLineHidden > 0 && topLine > 0) // widgetTopLine shows the first fully visible line
            -- topLine;

        int y = (statement.lineNumber - topLine) * lineheight - partialLineHidden;
        int canvasheight = fCanvas.getSize().y;

        if (y < canvasheight)
            gc.drawImage(execImage, 0, y);
    }
}
