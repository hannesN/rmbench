package com.byterefinery.rmbench.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.StringEncrypter;
import com.byterefinery.rmbench.util.StringEncrypter.EncryptionException;

public class RMBenchPrintOperation extends PrintGraphicalViewerOperation {

    private static final String PrintedUsingTheFreeEdition = 
		"0NTmuFqpfXUSu/P8v/b3iA/zLkc/OkPXPYs6L13wYusGPmwlWTgMSDl0skwu7NdXTsZ6OH6nvtGsLMWM7Pa6AX9fRjjGVW5HwiU8knT5CFcHW82J3WNiLw==";
	
	private int margin;
    private Printer printer;
    private final int[] pages;
    private double dpiScale;
    private double modeScale;
    private Dimension maxDim;
    private int bannerMargin = 0;
    private String bannerText;
    private int bannerTextWidth;
    
    public RMBenchPrintOperation(Printer p, int[] pages, GraphicalViewer g) {
        super(p, g);
        this.printer = p;
        this.margin = 0;
        this.pages = pages;
    }

    public void setMargin(int margin) {
        this.margin=margin;
    }
    
    /**
     * Prints the pages based on the current print mode.
     * @see org.eclipse.draw2d.PrintOperation#printPages()
     */
    protected void printPages() {
        Graphics graphics = getFreshPrinterGraphics();        
        IFigure figure = getPrintSource();
        
        maxDim = new Dimension();
        RMBenchPrintOperation.calcChildrensArea(figure, maxDim);
        
        PrinterData pdata = printer.getPrinterData();
        
        // number of copies and collate type        
        int numberOfRounds = 0;
        int numberOfCopies = 0;
        if(pdata.collate){
            numberOfRounds = pdata.copyCount;
            numberOfCopies = 1;
        }
        else{
            numberOfRounds = 1;
            numberOfCopies = pdata.copyCount;
        }
        
        setupPrinterGraphicsFor(graphics, figure);        
        graphics.scale(dpiScale * modeScale);
        Rectangle bounds = figure.getBounds();
        Rectangle clipRect = new Rectangle();
        int currentRound = 0;
        
        while(currentRound < numberOfRounds){
            int pageCount = 1;
            int copyCount = 0;
            int x = bounds.x - margin;
            int y = bounds.y - margin;        
            while (y < bounds.y + maxDim.height) {
                while (x < bounds.x + maxDim.width) {                
                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x+margin, y+margin);
                    clipRect.width -= 2*margin;
                    clipRect.height -= 2*margin + bannerMargin;
                    graphics.clipRect(clipRect);
                    // print only if current page is within print scope and contains printable Elements
                    if(containsPage(pageCount) && containsPrintableElements(figure, clipRect, 1.0)){
                        getPrinter().startPage();
                        figure.paint(graphics);
                        // print banner
                        if(bannerMargin > 0){
                            // create a copy of painting clip rectangle
                            Rectangle newRect = new Rectangle(clipRect);
                            // expand copy - remove horiz. margin and lower vertical margin
                            newRect.x -= margin;
                            newRect.width += 2 * margin;
                            newRect.height += bannerMargin + margin;
                            graphics.setClip(newRect);                            
                            graphics.drawText(bannerText, newRect.x + ((newRect.width -bannerTextWidth) / 2), newRect.y + newRect.height - bannerMargin);
                        }
                            
                        getPrinter().endPage();
                        copyCount++;
                    }
                    graphics.popState();
                    
                    if((copyCount == 0) ||  (copyCount >= numberOfCopies)){
                        x += clipRect.width;
                        pageCount++;
                        copyCount = 0;
                    }
                }
                x = bounds.x - margin;
                y += clipRect.height;
            }
            // next round :)
            currentRound++;
        }
    }
    
    /*
     * @return true if the given page should be printed
     */
    private boolean containsPage(int pageCount) {
        for (int i = 0; i < pages.length; i++) {
            if(pages[i] == pageCount)
                return true;
        }
        return false;
    }

    /**
     * Calculates the scale factors for dpi and mode scale that will be applied to the printing graphics object.
     *
     */
    private void initScaleFactors(){
        dpiScale = (double)getPrinter().getDPI().x / Display.getCurrent().getDPI().x;
        
        Rectangle printRegion = getPrintRegion();

        // put the print region in display coordinates
        printRegion.width /= dpiScale;
        printRegion.height /= dpiScale;
        
        // add margin
        printRegion.x += margin;
        printRegion.y += margin;
        printRegion.width -= 2*margin;
        printRegion.height -= 2*margin - bannerMargin;
        
        double xScale = (double)printRegion.width / maxDim.width;
        double yScale = (double)printRegion.height / maxDim.height;
        switch (getPrintMode()) {
            case FIT_PAGE:
                modeScale = Math.min(xScale, yScale); 
                break;
            case FIT_WIDTH:
                modeScale = xScale;
                break;
            case FIT_HEIGHT:
                modeScale = yScale;
                break;
            default:
                modeScale = 1;
        }

    }

    /**
     * Sets up Graphics object for the given IFigure.
     * @param graphics The Graphics to setup
     * @param figure The IFigure used to setup graphics
     * @see org.eclipse.draw2d.PrintFigureOperation#setupPrinterGraphicsFor()
     */
    protected void setupPrinterGraphicsFor(Graphics graphics, IFigure figure) {        
        graphics.setForegroundColor(figure.getForegroundColor());
        graphics.setBackgroundColor(figure.getBackgroundColor());
        graphics.setFont(figure.getFont());
        
        // we will assume that there is at least on FontData object assigned to this font.
        FontData fdata =  graphics.getFont().getFontData()[0];
        if(!RMBenchPlugin.getLicenseManager().isUserLicense()){
            this.bannerMargin = (int)fdata.height+8;
            try{
                StringEncrypter encrypter = new StringEncrypter(StringEncrypter.DES_ENCRYPTION_SCHEME);
                bannerText = encrypter.decrypt(PrintedUsingTheFreeEdition);
                
                // We will abuse a GC object configured with the current display resolution to calculate 
                //the width of the printed banner string
                GC gc = new GC(Display.getCurrent());
                gc.setFont(graphics.getFont());
                char[] characters = new char[bannerText.length()];
                bannerText.getChars(0, bannerText.length(),characters, 0);
                for(int i = 0; i < characters.length; i++)
                    bannerTextWidth += gc.getAdvanceWidth(characters[i]);
            }catch(EncryptionException ee){
                RMBenchPlugin.logError(ee);
            }
        }
        initScaleFactors();
    }
    
	@SuppressWarnings("unchecked")
	private static List<IFigure> getFigureList(IFigure  layer){        
        // get child elemends from figure
        List<IFigure> figures = new ArrayList<IFigure>();
        Object[] array = layer.getChildren().toArray();
        // first child is a FreeFormLayer
        figures.addAll(((FreeformLayer)((FreeformLayer)array[0]).getChildren().get(0)).getChildren());
        //second child is a ConnectionLayer
        figures.addAll(((ConnectionLayer)array[1]).getChildren());
        
        return figures;
    }
    
    public static Dimension calcChildrensArea(IFigure  layer, Dimension dim){
        
        List<IFigure> figures = getFigureList(layer);
        
        for(IFigure element: figures){
            Rectangle bounds = new Rectangle(element.getBounds());
            dim.width = Math.max(dim.width, bounds.x + bounds.width);
            dim.height = Math.max(dim.height, bounds.y + bounds.height);
        }
        
        return dim;
    }
    
    public static boolean containsPrintableElements(IFigure layer, Rectangle rect, double scaleFactor){                
        
        List<IFigure> figures = getFigureList(layer);
        
        for(IFigure element : figures){
            if(element instanceof PolylineConnection){
                PointList plist = ((PolylineConnection)element).getPoints();
                plist.performScale(scaleFactor);
                if(plist.intersects(rect))
                    return true;
            }
            else{
                Rectangle bounds = new Rectangle(element.getBounds());
                bounds.performScale(scaleFactor);
                if(bounds.intersects(rect))
                    return true;
            }
        }
        return false;
    }
}
