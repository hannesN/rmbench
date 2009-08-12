/*
 * created 12.03.2005
 * 
 * $Id:TableFigure.java 2 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import java.util.Iterator;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.model.diagram.DTable;
import com.byterefinery.rmbench.operations.CollapseTableOperation;
import com.byterefinery.rmbench.util.ImageConstants;


/**
 * a figure representing a database table, with collapse icon to toggle display of columns
 * 
 * @author cse
 */
public class TableFigure extends Figure {

    private final DTable dtable;
    private final TableTheme tableTheme;
    private final TableFonts tableFonts;
    private final TitleFigure titleFigure;
    private final ColumnsFigure columnsFigure;
    
    public TableFigure(DTable dtable, TableTheme theme, TableFonts fonts, Border border) {
        
        ToolbarLayout layout = new ToolbarLayout();
        layout.setStretchMinorAxis(true);
        layout.setVertical(true);
        setLayoutManager(layout);

        setBorder(border);
        setForegroundColor(ColorConstants.black);
        setOpaque(false);

        this.dtable = dtable;
        this.tableTheme = theme;
        this.tableFonts = fonts;
        this.titleFigure = new TitleFigure();
        this.columnsFigure = new ColumnsFigure();
        
        setCollapsed(dtable.isCollapsed());
        
        add(titleFigure);
        add(columnsFigure);
    }
    
    /**
     * @return the editable label for the table name
     */
    public Label getNameLabel() {
        return titleFigure.getNameLabel();
    }
    
    /**
     * @return the figure that holds the columns
     */
    public IFigure getColumnsFigure() {
        return columnsFigure;
    }
    
    public DTable getDTable() {
        return dtable;
    }

    /**
     * change displayed theme values
     * @param theme a theme from which all non-null properties will be copied
     */
    public void setTheme(TableTheme theme) {
        if(theme.bodyForeground != null) {
            tableTheme.bodyForeground = theme.bodyForeground;
            columnsFigure.updateForeground();
        }
        if(theme.bodyBackground != null) {
            tableTheme.bodyBackground = theme.bodyBackground;
            columnsFigure.setBackgroundColor(theme.bodyBackground);
        }
        if(theme.titleForeground != null) {
            tableTheme.titleForeground = theme.titleForeground;
            titleFigure.updateForeground();
        }
        if(theme.titleBackground != null) {
            tableTheme.titleBackground = theme.titleBackground;
            titleFigure.setBackgroundColor(theme.titleBackground);
        }
    }
    
    /**
     * change displayed font values
     * @param fonts a fonts object from which all non-null properties will be copied
     */
    public void setFonts(TableFonts fonts) {
        if(fonts.titleFont != null) {
            tableFonts.titleFont = fonts.titleFont;
            titleFigure.updateFont();
        }
        if(fonts.columnFont != null) {
            tableFonts.columnFont = fonts.columnFont;
            columnsFigure.updateColumnFont();
        }
        if(fonts.typeFont != null) {
            tableFonts.typeFont = fonts.typeFont;
            columnsFigure.updateTypeFont();
        }
    }
    
    public void setCollapsed(boolean collapsed) {
        titleFigure.collapseIcon.setImage(RMBenchPlugin.getImage(
                collapsed ? ImageConstants.VIEW_MAX : ImageConstants.VIEW_MIN));
    }

   
    /**
     * figure that serves as a container for the title area, with title label 
     * and minimize/maximize button
     */
    private class TitleFigure extends Figure {
        
        private final Label nameLabel;
        private final ImageFigure collapseIcon;
        
        public TitleFigure() {
            TitleLayout layout = new TitleLayout(this);
            setLayoutManager(layout);
            
            nameLabel = new Label(dtable.getTable().getName());
            nameLabel.setForegroundColor(tableTheme.titleForeground);
            nameLabel.setFont(tableFonts.titleFont);
            nameLabel.setBorder(new MarginBorder(2, 5, 2, 5));
            nameLabel.setLabelAlignment(PositionConstants.LEFT);
            nameLabel.setLabelAlignment(PositionConstants.CENTER);
            
            collapseIcon = new ImageFigure();
            collapseIcon.setAlignment(PositionConstants.NORTH | PositionConstants.EAST);
            
            collapseIcon.addMouseListener(new MouseListener() {

                public void mousePressed(MouseEvent me) {
                    new CollapseTableOperation(dtable).execute(TableFigure.this);
                }
                public void mouseReleased(MouseEvent me) {
                }
                public void mouseDoubleClicked(MouseEvent me) {
                }
            });
            
            setOpaque(true);
            setBackgroundColor(tableTheme.titleBackground);
            
            add(nameLabel);
            add(collapseIcon);
        }
        
        public void updateFont() {
            nameLabel.setFont(tableFonts.titleFont);
        }

        public void updateForeground() {
            nameLabel.setForegroundColor(tableTheme.titleForeground);
        }

        public Label getNameLabel() {
            return nameLabel;
        }
    }
    
    /**
     * figure that serves as a container for the columns
     */
    private class ColumnsFigure extends Figure {

        public ColumnsFigure() {
            
            ToolbarLayout layout = new ToolbarLayout(false);
            layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
            layout.setStretchMinorAxis(true);
            layout.setSpacing(5);
            setLayoutManager(layout);
            setBorder(new ColumnsFigureBorder());
            setForegroundColor(ColorConstants.black);
            setEnabled(false);
            
            setOpaque(true);
            setBackgroundColor(tableTheme.bodyBackground);
        }

        public void add(IFigure figure, Object constraint, int index) {
            ColumnFigure columnFigure = (ColumnFigure)figure;
            columnFigure.setForegroundColor(tableTheme.bodyForeground);
            columnFigure.setNameFont(tableFonts.columnFont);
            columnFigure.setTypeFont(tableFonts.typeFont);
            super.add(figure, constraint, index);
        }
        
        public void updateColumnFont() {
            for (Iterator<?> it = getChildren().iterator(); it.hasNext();) {
                ColumnFigure child = (ColumnFigure) it.next();
                child.setNameFont(tableFonts.columnFont);
            }
        }

        public void updateTypeFont() {
            for (Iterator<?> it = getChildren().iterator(); it.hasNext();) {
                ColumnFigure child = (ColumnFigure) it.next();
                child.setTypeFont(tableFonts.typeFont);
            }
        }

        public void updateForeground() {
            for (Iterator<?> it = getChildren().iterator(); it.hasNext();) {
                IFigure child = (IFigure) it.next();
                child.setForegroundColor(tableTheme.bodyForeground);
            }
        }
    }
    
    private class ColumnsFigureBorder extends AbstractBorder {

        public Insets getInsets(IFigure figure) {
            return dtable.isCollapsed() ?
                    new Insets(0, 0, 0, 0) :
                    new Insets(5, 5, 5, 5);
        }

        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(
                    getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
        }
    }
    
    /**
     * a layout that positions the control icon in the top right corner of the
     * title figure
     */
    private static class TitleLayout extends AbstractHintLayout {

        private final TitleFigure figure;
        
        TitleLayout(TitleFigure figure) {
            this.figure = figure;
        }
        
        protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
            Rectangle textBounds = figure.nameLabel.getTextBounds();
            Insets border = figure.nameLabel.getBorder().getInsets(null);
            
            org.eclipse.swt.graphics.Rectangle imageBounds = 
                figure.collapseIcon.getImage().getBounds();
            
            return new Dimension(
                    textBounds.width + imageBounds.width + border.left + border.right, 
                    Math.max(textBounds.height, imageBounds.height) + border.top + border.bottom);
        }

        public void layout(IFigure container) {
            Rectangle titleBounds = container.getBounds();
            
            org.eclipse.swt.graphics.Rectangle iconRect = figure.collapseIcon.getImage().getBounds();
            int labelWidth = titleBounds.width - iconRect.width;
            
            figure.nameLabel.setBounds(new Rectangle(
                    titleBounds.x, titleBounds.y, labelWidth, titleBounds.height));
            figure.collapseIcon.setBounds(new Rectangle(
                    titleBounds.x+labelWidth, titleBounds.y, iconRect.width, titleBounds.height));
        }
    }

    /**
     * @return true if and only if the given location lies within the name label child 
     * figure
     */
    public boolean isNameLabelHit(Point location) {
        
        Label nameLabel = getNameLabel();
        Point loc2 = location.getCopy();
        nameLabel.translateToRelative(loc2);
        
        return nameLabel.containsPoint(loc2);
    }
}
