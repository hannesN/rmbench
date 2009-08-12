/*
 * created 24.04.2005
 * 
 * $Id: PageOutlineLayer.java 646 2007-08-30 09:31:13Z cse $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;


/**
 * a layer for displaying the outlines of printable pages
 * 
 * @author cse
 */
public class PageOutlineLayer extends FreeformLayer {

	public static final String LAYER_ID = "PrintablePageLayer";
	
    private Rectangle page;
	private Insets printMargin = new Insets(0, 0, 0, 0);
    private LayerManager lm;
    
    public PageOutlineLayer(boolean enabled) {
        super();
		super.setEnabled(enabled);
    }

	/**
	 * @return whether the printer has been configured
	 */
	public boolean isConfigured() {
		return page != null;
	}
	
     /**
     * set the client area defined the given printer, such that
     * the page outline will be drawn accordingly
     * This is sets a printer in TILE printMode and with a margin of 0,0,0,0 
     * @param area the printer client area
     * @see org.eclipse.swt.printing.Printer#getClientArea()
     */
    public void setPrinter(Printer printer) {
        setPrinter(printer, PrintFigureOperation.TILE, new Insets(0,0,0,0));
    }
    
    
    /**
     * set the client area defined the given printer, such that
     * the page outline will be drawn accordingly
     * 
     * @param area the printer client area
     * @param the print mode (TILE, FIT_TO_PAGE, FIT_TO_WIDTH, FIT_TO_HEIGHT) @see org.eclipse.draw2d.PrintFigureOperation
     * @param the margin of the view area 
     * @see org.eclipse.swt.printing.Printer#getClientArea()
     */
    public void setPrinter(Printer printer, int printMode, Insets margin) {
		double dpiScale = (double)printer.getDPI().x / Display.getCurrent().getDPI().x;

        printMargin = margin;
        int bannerMargin = 0;
        
        //need to do this, because the GEF people wanted their own rectangle class...
		org.eclipse.swt.graphics.Rectangle rec = printer.getClientArea();
        page = new Rectangle();
        page.setLocation(rec.x, rec.y);
        page.width = (int)(rec.width/dpiScale) - (printMargin.left+printMargin.right);
        page.height = (int)(rec.height/dpiScale) - (printMargin.top+printMargin.bottom) - bannerMargin;
        
		if(page.width <= 0 || page.height <= 0) {
			page = null;
		}
    }
    
	/**
	 * Overridden to indicate no preferred size.
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension();
	}

    public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		repaint();
	}
	
    public void setLayerManager(LayerManager lm){
        this.lm = lm;
    }
    
    protected void paintFigure(Graphics graphics) {
        super.paintFigure(graphics);

		if(isEnabled() && page != null) {
	        Color rgb = graphics.getBackgroundColor();
	        Color shadow = FigureUtilities.darker(rgb);
	        
	        graphics.setLineStyle(Graphics.LINE_DOT);
	        graphics.setLineWidth(1);
	        graphics.setXORMode(false);
	        graphics.setForegroundColor(shadow);
	        
	        paintGrid(graphics);
		}
    }
    

    /*
     * copied from FigureUtilities, modified to avoid drawing lines at 0 offset 
     */
    private void paintGrid(Graphics g) {
        Rectangle clip = g.getClip(Rectangle.SINGLETON);
        
        int x = page.x; 
        int y = page.y;
        
        if (x > clip.x)
            while (x - page.width >= clip.x)
                x -= page.width;
        else
            while (x <= clip.x)
                x += page.width;
        for (int i = x; i < clip.x + clip.width; i += page.width)
            g.drawLine(i, clip.y, i, clip.y + clip.height);
        
        if (y > clip.y)
            while (y - page.height >= clip.y)
                y -= page.height;
        else
            while (y <= clip.y)
                y += page.height;
        for (int i = y; i < clip.y + clip.height; i += page.height)
            g.drawLine(clip.x, i, clip.x + clip.width, i);
    }
}
