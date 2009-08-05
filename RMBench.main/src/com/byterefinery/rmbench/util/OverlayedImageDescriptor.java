/*
 * created 08.04.2005
 * 
 * $Id: OverlayedImageDescriptor.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * @author cse
 */
public class OverlayedImageDescriptor extends CompositeImageDescriptor {

    private ImageData baseImage;
    private ImageData overlay;
    private Point imageSize;
    
    public OverlayedImageDescriptor(ImageDescriptor overlay) {
        this(RMBenchPlugin.getImageDescriptor(ImageConstants.BLANK).getImageData(), 
                overlay.getImageData()); 
    }
    
    public OverlayedImageDescriptor(ImageDescriptor baseImage, ImageDescriptor overlay) {
        this(baseImage.getImageData(), overlay.getImageData()); 
    }

    public OverlayedImageDescriptor(ImageData baseImage, ImageData overlay) {
        super();
        this.baseImage = baseImage; 
        this.overlay = overlay;
    }
    
    protected void drawCompositeImage(int width, int height) {
        drawImage(baseImage, 0, 0);
        if(overlay != null) {
            int x= getSize().x;
            int y = 0;
            x -= overlay.width;
            drawImage(overlay, x, y);
        }
    }

    protected Point getSize() {
        if (imageSize == null) {
            imageSize = new Point(baseImage.width, baseImage.height);
        }
        return imageSize;
    }
}
