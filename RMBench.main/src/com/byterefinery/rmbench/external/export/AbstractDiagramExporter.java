/*
 * created 24.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.IImageExporter;

/**
 * abstract implementation of the IImageExporter API. The export will be done to
 * a piped stream in order to optimize the use of resources. However, subclasses
 * can request that the export proper be called in the UI thread by overriding the
 * {@link #doThreaded()} method
 * 
 * @author cse
 */
public abstract class AbstractDiagramExporter implements IImageExporter {

    /**
     * @return whether this exporter can be executed in a parallel thread, which 
     * is more resource efficient but requires the export algorithm to be independet 
     * of the UI. By default, return <code>false</code>
     */
    protected boolean doThreaded() {
        return false;
    }
    
    /**
     * perform export. This method may be executed in a separate, non-UI thread, depending 
     * on the {@link #doThreaded()} method.
     * 
     * @param out the stream to write to
     * @param figure the figure to export
     */
    protected abstract void doExport(OutputStream out, IFigure figure);
    
    /**
     * this implementation will create PipedStreams and start a separate thread to 
     * write to the pipe.
     */
    public final InputStream export(IFigure figure) {
        
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        try {
            out = new PipedOutputStream(in);
        }
        catch (IOException e) {
            //shouldnt happen, as we have fresh streams
            RMBenchPlugin.logError(e);
            return null;
        }
        Runnable runnable;
        if(doThreaded()) {
            runnable = new Exporter(out, figure);
        }
        else {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            doExport(bout, figure);
            runnable = new Copier(bout, out);
        }
        Thread saverThread = new Thread(runnable);
        saverThread.start();
        
        return in;
    }

    /*
     * runnable that simply copies already exported data to the piped stream
     */
    private static class Copier implements Runnable {
        
        private final ByteArrayOutputStream bout;
        private final OutputStream out;
        
        public Copier(ByteArrayOutputStream bout, OutputStream out) {
            this.bout = bout;
            this.out = out;
        }
        
        public void run() {
            try {
                bout.writeTo(out);
                bout.close();
            }
            catch (IOException e) {
                RMBenchPlugin.logError("error during image export", e);
            }
            finally {
                try {
                    out.close();
                }
                catch (IOException e) {
                    RMBenchPlugin.logError("error closing export stream", e);
                }
            }
        }
    }
    
    /*
     * runnable that exports directly to the piped stream
     */
    private class Exporter implements Runnable {

        private final OutputStream out;
        private final IFigure figure;
        
        public Exporter(OutputStream out, IFigure figure) {
            this.out = out;
            this.figure = figure;
        }

        public void run() {
            try {
                doExport(out, figure);
            } 
            catch(Exception x) {
                RMBenchPlugin.logError("error during image export", x);
            } 
            finally {
                try {
                    out.close();
                }
                catch (IOException e) {
                    RMBenchPlugin.logError("error closing export stream", e);
                }
            }
        }
    }

    /**
     * Utility method for use by subclasses
     * @return the figure's bounds in order to crop an image to the actual diagram's extents
     */
    protected Rectangle getBounds(IFigure figure) {
        Rectangle bounds;
        if (figure instanceof FreeformFigure) {
            FreeformFigure freeformFigure = (FreeformFigure) figure;
            bounds = freeformFigure.getFreeformExtent();
        }
        else {
            bounds = figure.getBounds();
        }
        return bounds;
    }
}
