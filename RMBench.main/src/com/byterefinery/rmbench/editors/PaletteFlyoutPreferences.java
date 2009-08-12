/*
 * created 13.03.2005
 * 
 * $Id: PaletteFlyoutPreferences.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editors;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;

import com.byterefinery.rmbench.RMBenchPlugin;


/**
 * @author cse
 */
public class PaletteFlyoutPreferences implements FlyoutPreferences {
    
    public static final int DEFAULT_PALETTE_WIDTH = 150;
    public static final int DEFAULT_PALETTE_LOCATION = PositionConstants.WEST;

    protected static final String PALETTE_DOCK_LOCATION = "Dock location";
    protected static final String PALETTE_SIZE = "Palette Size";
    protected static final String PALETTE_STATE = "Palette state";

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#getDockLocation()
    public int getDockLocation() {
        int location = RMBenchPlugin.getDefault().getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
        if (location == 0) {
            return DEFAULT_PALETTE_LOCATION;
        }
        return location;
    }

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#getPaletteState()
    public int getPaletteState() {
        return RMBenchPlugin.getDefault().getPreferenceStore().getInt(PALETTE_STATE);
    }

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#getPaletteWidth()
    public int getPaletteWidth() {
        int width = RMBenchPlugin.getDefault().getPreferenceStore().getInt(PALETTE_SIZE);
        if (width == 0)
            return DEFAULT_PALETTE_WIDTH;
        return width;
    }

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#setDockLocation(int)
    public void setDockLocation(int location) {
        RMBenchPlugin.getDefault().getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
    }

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#setPaletteState(int)
    public void setPaletteState(int state) {
        RMBenchPlugin.getDefault().getPreferenceStore().setValue(PALETTE_STATE, state);
    }

    //@see org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences#setPaletteWidth(int)
    public void setPaletteWidth(int width) {
        RMBenchPlugin.getDefault().getPreferenceStore().setValue(PALETTE_SIZE, width);
    }
}
