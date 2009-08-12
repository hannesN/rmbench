/*
 * created 19.09.2005 by sell
 *
 * $Id: TableFonts.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.swt.graphics.Font;

/**
 * encapsulates the font choices for a table figure 
 * 
 * @author cse
 */
public class TableFonts {

    public Font titleFont;
    public Font columnFont;
    public Font typeFont;
    
    public static TableFonts titleFont(Font font) {
        TableFonts fonts = new TableFonts();
        fonts.titleFont = font;
        return fonts;
    }

    public static TableFonts columnFont(Font font) {
        TableFonts fonts = new TableFonts();
        fonts.columnFont = font;
        return fonts;
    }
    
    public static TableFonts typeFont(Font font) {
        TableFonts fonts = new TableFonts();
        fonts.typeFont = font;
        return fonts;
    }
    
    private TableFonts() {}
    
    public TableFonts(Font titleFont, Font columnFont, Font typeFont) {
        this.titleFont = titleFont;
        this.columnFont = columnFont;
        this.typeFont = typeFont;
    }
}
