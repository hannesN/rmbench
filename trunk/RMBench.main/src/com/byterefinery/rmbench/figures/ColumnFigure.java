/*
 * created 14.10.2005
 * 
 * $Id: ColumnFigure.java 163 2006-02-09 14:38:48Z cse $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * figure that displays column data as two labels (name and type) which are layed out 
 * along the left and right edges
 *  
 * @author cse
 */
public class ColumnFigure extends Figure {

    private static final Border ICON_BORDER = new MarginBorder(0, 15, 0, 0);
    
    private Label nameLabel = new Label();
    private Label typeLabel = new Label();
    
    public ColumnFigure() {
        super();
        nameLabel.setLabelAlignment(PositionConstants.LEFT);
        nameLabel.setTextAlignment(PositionConstants.LEFT);
        nameLabel.setIconTextGap(0);
        
        typeLabel.setLabelAlignment(PositionConstants.LEFT);
        typeLabel.setTextAlignment(PositionConstants.LEFT);
        
		BorderLayout layout = new BorderLayout();
		layout.setHorizontalSpacing(7);
	    setLayoutManager(layout);	
	    add(nameLabel, BorderLayout.LEFT);	
	    add(typeLabel, BorderLayout.RIGHT);
	}

    public void setColumnName(String name) {
        nameLabel.setText(name);
    }

    public void setColumnType(String type) {
        typeLabel.setText(type);
    }

    public void setNameFont(Font font) {
        nameLabel.setFont(font);
    }

    public void setTypeFont(Font font) {
        typeLabel.setFont(font);
    }
    
    /**
     * @param image the icon image to show in front of the column name
     */
    public void setIcon(Image image) {
        
        if(image != null) {
            nameLabel.setBorder(null);
            nameLabel.setIcon(image);
        }
        else {
            nameLabel.setBorder(ICON_BORDER);
            nameLabel.setIcon(null);
        }
    }
}
