/*
 * created 21.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: ColorRegistry.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.ResourceRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author cse
 * @see org.eclipse.jface.resource.ColorRegistry
 */
public class ColorRegistry extends ResourceRegistry {

    protected Display display;
    private List<Color> staleColors = new ArrayList<Color>();
    private Map<String, Color> stringToColor = new HashMap<String, Color>(7);
    private Map<String, RGB> stringToRGB = new HashMap<String, RGB>(7);

    protected Runnable displayRunnable = new Runnable() {
        public void run() {
            clearCaches();
        }
    };

    public ColorRegistry() {
        this(Display.getCurrent(), true);
    }

    public ColorRegistry(Display display, boolean cleanOnDisplayDisposal) {
        Assert.isNotNull(display);
        this.display = display;
        if (cleanOnDisplayDisposal)
            hookDisplayDispose();
    }

    private Color createColor(RGB rgb) {
        return new Color(display, rgb);
    }

    private void disposeColors(Iterator<Color> iterator) {
        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
    }

    public Color get(String symbolicName) {

        Assert.isNotNull(symbolicName);
        Object result = stringToColor.get(symbolicName);
        if (result != null)
            return (Color) result;

        Color color = null;

        result = stringToRGB.get(symbolicName);
        if (result == null)
            return null;

        color = createColor((RGB) result);

        stringToColor.put(symbolicName, color);

        return color;
    }

    public Set<?> getKeySet() {
        return Collections.unmodifiableSet(stringToRGB.keySet());
    }

    public RGB getRGB(String symbolicName) {
        Assert.isNotNull(symbolicName);
        return (RGB) stringToRGB.get(symbolicName);
    }
    
    public ColorDescriptor getColorDescriptor(String symbolicName) {
        return ColorDescriptor.createFrom(getRGB(symbolicName));
    }

    protected void clearCaches() {
        disposeColors(stringToColor.values().iterator());
        disposeColors(staleColors.iterator());
        stringToColor.clear();
        staleColors.clear();
    }

    public boolean hasValueFor(String colorKey) {
        return stringToRGB.containsKey(colorKey);
    }

    private void hookDisplayDispose() {
        display.disposeExec(displayRunnable);
    }

    public void put(String symbolicName, RGB colorData) {
        put(symbolicName, colorData, true);
    }

    /*
     * changed from former implementation to allow null colorData values
     */
    private void put(String symbolicName, RGB colorData, boolean update) {

        Assert.isNotNull(symbolicName);

        RGB existing = (RGB) stringToRGB.get(symbolicName);
        if (colorData != null && colorData.equals(existing))
            return;

        Color oldColor = (Color) stringToColor.remove(symbolicName);
        if(colorData != null)
            stringToRGB.put(symbolicName, colorData);
        else
            stringToRGB.remove(symbolicName);
        if (update)
            fireMappingChanged(symbolicName, existing, colorData);
        if (oldColor != null)
            staleColors.add(oldColor);
    }

    /**
     * New method. Dispose of this object
     */
    public void dispose() {
        display.asyncExec(displayRunnable);
    }
}
