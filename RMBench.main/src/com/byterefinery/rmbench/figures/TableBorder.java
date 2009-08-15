/*
 * created 10.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableBorder.java 41 2005-11-19 16:01:01Z hannesn $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * a line border that supports rounded edges and a shadow
 *  
 * @author cse
 */
public class TableBorder extends AbstractBorder {

    private static final Color dropshadow = 
        Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    
    private static final int SHADOW_WIDTH = 3;
    
    private boolean rounded;
    private boolean shadow;
    private int width = 1;
    private final Insets insets = new Insets(width);
    private Color color;

    public TableBorder(Color color, int width, boolean rounded, boolean shadow) {
        this.color = color;
        this.width = width;
        this.rounded = rounded;
        this.shadow = shadow;
        computeInsets();
    }

    public TableBorder() {
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
        computeInsets();
    }
    
    public boolean isRounded() {
        return rounded;
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        computeInsets();
    }

    public Insets getInsets(IFigure figure) {
        return insets;
    }

    public boolean isOpaque() {
        return true;
    }

    public void paint(IFigure figure, Graphics graphics, Insets insets) {
        Rectangle rect = getPaintRectangle(figure, insets);
        if (getWidth() % 2 == 1) {
            rect.width--;
            rect.height--;
        }
        rect.shrink(getWidth() / 2, getWidth() / 2);
        if(shadow) {
            rect.width  -= SHADOW_WIDTH;
            rect.height -= SHADOW_WIDTH;
        }
        graphics.setLineWidth(getWidth());
        if (getColor() != null)
            graphics.setForegroundColor(getColor());
        if(rounded) {
            graphics.drawRoundRectangle(rect, 8, 8);
        }
        else {
            graphics.drawRectangle(rect);
        }
        if(shadow) drawShadow(graphics, rect);
    }

    private void drawShadow(Graphics graphics, Rectangle rect) {
        //we draw rectangles instead of a thick line
        Color bgColor=graphics.getBackgroundColor();
        
        graphics.setBackgroundColor(dropshadow);
        
        graphics.fillRectangle(rect.x+SHADOW_WIDTH, rect.bottom()+1, rect.width+SHADOW_WIDTH-2, SHADOW_WIDTH);
        graphics.fillRectangle(rect.right()+1, rect.y+SHADOW_WIDTH, SHADOW_WIDTH, rect.height);
        
        graphics.setBackgroundColor(bgColor);
    }

//    public void paint2(IFigure figure, Graphics g, Insets insets) {
//        Rectangle r = getPaintRectangle(figure, insets);
//        r.width -= 5;
//        r.height -= 5;
//
//        g.setLineWidth(1);
//        g.setForegroundColor(ColorConstants.black);
//        g.drawRoundRectangle(r, 9, 9);
//
//        g.setForegroundColor(dropshadow);
//        g.drawLine(r.right() - 2, r.bottom(), r.right() + 1, r.bottom());
//        g.drawLine(r.right(), r.bottom() - 2, r.right(), r.bottom() + 1);
//        r.translate(1, 1);
//        int pos = 4;
//        int length = 2;
//        g.drawLine(r.x + pos, r.bottom(), r.right() + length, r.bottom());
//        g.drawLine(r.right(), r.y + pos, r.right(), r.bottom() + length);
//        r.translate(1, 1);
//        pos--;
//        length--;
//        g.drawLine(r.x + pos, r.bottom(), r.right() + length, r.bottom());
//        g.drawLine(r.right(), r.y + pos, r.right(), r.bottom() + length);
//        pos++;
//        length -= 2;
//        g.drawLine(r.x + pos, r.bottom() + 1, r.right() + length, r.bottom() + 1);
//        g.drawLine(r.right() + 1, r.y + pos, r.right() + 1, r.bottom() + length);
//    }
    
    private void computeInsets() {
        insets.top = width;
        insets.right = width;
        insets.bottom = width;
        insets.left = width;
        
        if(shadow) {
            insets.right += SHADOW_WIDTH;
            insets.bottom += SHADOW_WIDTH;
        }
    }
}
