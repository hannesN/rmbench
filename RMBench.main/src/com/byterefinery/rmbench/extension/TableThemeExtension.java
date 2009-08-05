/*
 * created 19.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TableThemeExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import com.byterefinery.rmbench.RMBenchPlugin;


/**
 * a table theme definition holds color definitions for different aspects of
 * a table figure
 * 
 * @author cse
 */
public class TableThemeExtension extends NamedExtension {

    public RGB titleBgColor;
    public RGB titleFgColor;
    public RGB bodyBgColor;
    public RGB bodyFgColor;
    
    private String titleFgId;
    private String titleBgId;
    private String bodyFgId;
    private String bodyBgId;
    
    public TableTypeExtension[] typeExtensions;

    public TableThemeExtension(
            String namespace,
            String id, 
            String label, 
            String titleBgColor,
            String titleFgColor,
            String bodyBgColor,
            String bodyFgColor) {

        super(namespace, id, label);
        if(titleBgColor != null)
            this.titleBgColor = parseColor(titleBgColor);
        if(titleFgColor != null)
            this.titleFgColor = parseColor(titleFgColor);
        if(bodyBgColor != null)
            this.bodyBgColor = parseColor(bodyBgColor);
        if(bodyFgColor != null)
            this.bodyFgColor = parseColor(bodyFgColor);
        
        initIds();
    }

    public TableThemeExtension(String namespace, String id) {
        super(namespace, id, id);
        initIds();
    }
    
    private void initIds() {
        titleFgId = id+".titleFg";
        titleBgId = id+".titleBg";
        bodyFgId = id+".bodyFg";
        bodyBgId = id+".bodyBg";
    }

    private RGB parseColor(String rgbString) {
        try {
            return StringConverter.asRGB(rgbString);
        }
        catch(Exception x) {
            RMBenchPlugin.logError("could not parse RGB value "+rgbString);
            return null;
        }
    }

    public String getTitleFgId() {
        return titleFgId;
    }

    public String getTitleBgId() {
        return titleBgId;
    }

    public String getBodyFgId() {
        return bodyFgId;
    }

    public String getBodyBgId() {
        return bodyBgId;
    }
    
    public String getLabel() {
        return name;
    }
}
