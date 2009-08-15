/*
 * created 08.06.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableNameDirectEditManager.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editparts;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.byterefinery.rmbench.figures.TableFigure;

/**
 * direct editing support for table name labels
 * 
 * @author cse
 */
class TableNameDirectEditManager extends DirectEditManager {

    private final TableFigure tableFigure;
    private Font scaledFont;
    
    public TableNameDirectEditManager(GraphicalEditPart source, TableFigure tableFigure) {
        super(source, null, new LabelCellEditorLocator(tableFigure.getNameLabel()));
        this.tableFigure = tableFigure;
    }

    protected CellEditor createCellEditorOn(Composite composite) {
        return new TextCellEditor(composite, SWT.SINGLE);
    }
    
    protected void initCellEditor() {
        Text text = (Text)getCellEditor().getControl();
        String tableName = ((TableEditPart)getEditPart()).getTable().getName();
        getCellEditor().setValue(tableName);
        scaledFont = tableFigure.getFont();
        FontData data = scaledFont.getFontData()[0];
        Dimension fontSize = new Dimension(0, data.getHeight());
        tableFigure.getNameLabel().translateToAbsolute(fontSize);
        data.setHeight(fontSize.height);
        scaledFont = new Font(null, data);
        text.setFont(scaledFont);
        text.selectAll();
    }

    protected void bringDown() {
        super.bringDown();
        if (scaledFont != null) {
            scaledFont.dispose();
            scaledFont = null;
        }
    }

    /**
     * cell editor locator that will resize the cell edit field horizontally to 
     * accomodate the entered text 
     */
    final static class LabelCellEditorLocator implements CellEditorLocator {

        private final Label label;
        private static final int xOffset, wOffset, yOffset, hOffset;
        static {
            if (SWT.getPlatform().equalsIgnoreCase("gtk")) {
                xOffset = 0;
                wOffset = 0;
                yOffset = 0;
                hOffset = 0;
            }
            else if (SWT.getPlatform().equalsIgnoreCase("carbon")) {
                xOffset = -3;
                wOffset = 9;
                yOffset = -3;
                hOffset = 6;
            }
            else { //Windoze
                xOffset = -4;
                wOffset = 5;
                yOffset = 0;
                hOffset = 0;
            }
        }

        public LabelCellEditorLocator(Label label) {
            this.label = label;
        }

        public void relocate(CellEditor celleditor) {
            Text text = (Text) celleditor.getControl();
            
            Point preferred = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                
            Rectangle rect = label.getClientArea().getCopy();
            label.translateToAbsolute(rect);

            int width = Math.max(preferred.x, rect.width  + wOffset);
            text.setBounds(rect.x + xOffset, rect.y + yOffset, width, rect.height + hOffset);
        }
    }
}

