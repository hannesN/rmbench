/*
 * created 09.10.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: Decorations.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;

import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.model.schema.Table;

/**
 * handles different decoration styles
 * 
 * @author cse
 */
public abstract class Decorations {

    public static final String PREF_VALUE_DEFAULT = "default";
    public static final String PREF_VALUE_UML = "uml";
    public static final String PREF_VALUE_IDEF1X = "idef1x";
    public static final String PREF_VALUE_IE = "ie";
    
    private static final PointList DIAMOND = new PointList();
    static {
        DIAMOND.addPoint(0,0);
        DIAMOND.addPoint(-2,2);
        DIAMOND.addPoint(-4,0);
        DIAMOND.addPoint(-2,-2);
    }
    
    public static final PointList CROWSFOOT = new PointList();
    static {
        CROWSFOOT.addPoint(0, 1);
        CROWSFOOT.addPoint(-1, 0);
        CROWSFOOT.addPoint(0, -1);
    }
    
    public static Decorations getDecoration(String prefValue) {
        if(PREF_VALUE_UML.equals(prefValue))
            return UML;
        else if(PREF_VALUE_IDEF1X.equals(prefValue))
            return IDEF1X;
        else if(PREF_VALUE_IE.equals(prefValue))
            return IE;
        else
            return DEFAULT;
    }

    /**
     * @return <code>true</code> if this decoration affects the table border. Default is 
     * <code>false</code>
     * @see #decorate(Table, TableBorder)
     */
	public boolean affectsBorder() {
		return false;
	}
    /**
     * decorate the table border. Default is to do nothing
     * @see #affectsBorder()
     */
    public void decorate(Table table, TableBorder border) {
    }
    /**
     * decorate a connection. Default is to do nothing
     */
    public void decorate(ForeignKey foreignKey, PolylineConnection conn) {
    }

    /**
     * UML decoration style
     */
    private static final Decorations UML = new Decorations() {
        
        public void decorate(ForeignKey foreignKey, PolylineConnection conn) {
            
            PolygonDecoration diamond = new PolygonDecoration();
            diamond.setBackgroundColor(
                    foreignKey.isExistential() ? ColorConstants.black : ColorConstants.white);
            diamond.setTemplate(DIAMOND);
            conn.setTargetDecoration(diamond);
            
            PolylineDecoration arrow = new PolylineDecoration();
            arrow.setScale(12, 4);
            conn.setSourceDecoration(arrow);
        }
    };
    
    /**
     * IDEF1X decoration style
     */
    private static final Decorations IDEF1X = new Decorations() {

        public boolean affectsBorder() {
			return true;
		}

		public void decorate(Table table, TableBorder border) {
            for (ForeignKey foreignKey : table.getForeignKeys()) {
                if(foreignKey.isIdentifying()) {
                    border.setRounded(true);
                    return;
                }
            }
        }

        public void decorate(ForeignKey foreignKey, PolylineConnection conn) {
            
            if(!foreignKey.isExistential()) {
                PolygonDecoration targetDeco = new PolygonDecoration();
                targetDeco.setBackgroundColor(ColorConstants.white);
                targetDeco.setTemplate(DIAMOND);
                conn.setTargetDecoration(targetDeco);
            }
            if(!foreignKey.isIdentifying())
                conn.setLineStyle(SWT.LINE_DASH);
            
            conn.setSourceDecoration(new CircleDecoration());
        }
    };
    
    /**
     * IE/CrowsFoot decoration style
     */
    private static final Decorations IE = new Decorations() {

        public void decorate(ForeignKey foreignKey, PolylineConnection conn) {
            
            if(foreignKey.isExistential()) {
                conn.setTargetDecoration(new IEOneMandatoryDecoration());
            }
            else {
                conn.setTargetDecoration(new IEOneOptionalDecoration());
            }
            PolylineDecoration crowsfoot = new PolylineDecoration();
            crowsfoot.setTemplate(CROWSFOOT);
            crowsfoot.setScale(15, 5);
            conn.setSourceDecoration(crowsfoot);
        }
    };
    
    /**
     * simple decoration style
     */
    private static final Decorations DEFAULT = new Decorations() {

        public void decorate(ForeignKey foreignKey, PolylineConnection conn) {
            PolygonDecoration arrow = new PolygonDecoration();
            arrow.setScale(10, 4);
            conn.setTargetDecoration(arrow);
        }
    };
}
