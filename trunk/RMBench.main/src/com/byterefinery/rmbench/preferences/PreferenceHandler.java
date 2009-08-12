/*
 * created 15.09.2005 by sell
 *
 * $Id: PreferenceHandler.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.extension.TableThemeExtension;
import com.byterefinery.rmbench.extension.TableTypeExtension;
import com.byterefinery.rmbench.figures.Decorations;
import com.byterefinery.rmbench.figures.TableFonts;
import com.byterefinery.rmbench.figures.TableTheme;
import com.byterefinery.rmbench.util.ColorRegistry;

/**
 * Handler for all things preferences
 * 
 * @author sell
 */
public class PreferenceHandler extends AbstractPreferenceInitializer {

    /** preference key for the table body background color */
    public static final String PREF_TABLE_BODYCOLOR = "table.bodycolor";
    /** preference key for the table title background color */
    public static final String PREF_TABLE_TITLECOLOR = "table.titlecolor";
    /** preference key for the table body text color */
    public static final String PREF_TABLE_BODYFGCOLOR = "table.body.fgcolor";
    /** preference key for the table title text color */
    public static final String PREF_TABLE_TITLEFGCOLOR = "table.title.fgcolor";
    /** preference key for the table title font */
    public static final String PREF_TABLE_TITLEFONT = "table.titlefont";
    /** preference key for the table body font */
    public static final String PREF_TABLE_BODYFONT = "table.bodyfont";
    /** preference key for the font used for column types */
    public static final String PREF_TABLE_TYPEFONT = "table.typefont";
    /** preference key whether a shadow should be shown along table figures */
    public static final String PREF_TABLE_SHADOW = "table.shadow";
    /** preference key whether data types should be shown in table bodies */
    public static final String PREF_TABLE_SHOWTYPES = "table.showtypes";
    /** preference key for syntax coloring of comments in DDL editors */
    public static final String PREF_DDL_COMMENTCOLOR = "ddl.commentcolor";
    /** preference key for syntax coloring of string constants in DDL editors */
    public static final String PREF_DDL_STRINGCOLOR = "ddl.stringcolor";
    /** preference key for syntax coloring of SQL code in DDL editors */
    public static final String PREF_DDL_CODECOLOR = "ddl.codecolor";
    /** preference key for syntax coloring of SQL keywords in DDL editors */
    public static final String PREF_DDL_KWCOLOR = "ddl.kwcolor";
    /** preference key for coloring of selection highlight in Model Compare editor */
    public static final String PREF_DDL_HIGHLIGHTCOLOR = "ddl.highlightcolor";
    /** preference key for the font to be used in DDL script editors (currently unused) */
    public static final String PREF_DDL_SCRIPTFONT = "ddl.scriptfont";
    /** preference key whether descriptive labels should appear along connections */
    public static final String PREF_CONNECTION_LABELS = "connection.labels";
    /** preference key for the choice of connection decorations */
    public static final String PREF_DECORATION_STYLE = "decoration.style";
    /** preference key for remember if foreignkey datatype change dialog should be shown*/
    public static final String PREF_HIDE_PK_DELETION_DIALOG = "messagedialog.hide.pk.delete";
    /** preference key for remember if primary datatype change dialog should be shown*/
    public static final String PREF_HIDE_PK_DATATYPE_DIALOG = "messagedialog.hide.pk.datatype";
    /** preference key whether anchors should be relocated after table move */
    public static final String PREF_DIAGRAM_RELOCATION = "diagram.relocation";
    /** 
     * preference key to store the last installed trial release. This preference should never
     * appear on the GUI. The name is intentionally misleading 
     */
    public static final String PREF_LAST_TRIAL = "diagram.datatype.";
    
    /** the default color for table title foreground */
    public static final Color DEFAULT_TABLE_TITLEFGCOLOR = new Color(null, 0, 0, 0); //black
    /** the default color for table title areas */
    public static final Color DEFAULT_TABLE_TITLECOLOR = new Color(null, 255, 255, 206); //yellow
    /** the default color for table body areas */
    public static final Color DEFAULT_TABLE_BODYCOLOR = new Color(null, 255, 255, 206);
    
    public static final Color DEFAULT_DDL_CODECOLOR = new Color(null, 0, 127, 0); //darkGreen
    public static final Color DEFAULT_DDL_COMMENTCOLOR = new Color(null,  64,  64,  64); //darkGray
    public static final Color DEFAULT_DDL_KWCOLOR = new Color(null,  0,  0, 127); //darkBlue
    public static final Color DEFAULT_DDL_STRINGCOLOR = new Color(null, 0, 255, 0); //green
    public static final Color DEFAULT_DDL_HLCOLOR = new Color(null, 192, 192, 192); //lightGray
    
    private static PreferenceHandler instance;
    private boolean initialized;
    
    
    public PreferenceHandler() {
        if(instance != null)
            initialized = instance.initialized;
        instance = this;
    }
    
    public void initializeDefaultPreferences() {
        if(!initialized) {
            IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
            
            initializeColor(store, PREF_TABLE_TITLECOLOR, DEFAULT_TABLE_TITLECOLOR);
            initializeColor(store, PREF_TABLE_TITLEFGCOLOR, DEFAULT_TABLE_TITLEFGCOLOR);
            initializeColor(store, PREF_TABLE_BODYCOLOR, DEFAULT_TABLE_BODYCOLOR);
            initializeColor(store, PREF_TABLE_BODYFGCOLOR, DEFAULT_TABLE_TITLEFGCOLOR);
            initializeColor(store, PREF_DDL_CODECOLOR, DEFAULT_DDL_CODECOLOR);
            initializeColor(store, PREF_DDL_COMMENTCOLOR, DEFAULT_DDL_COMMENTCOLOR);
            initializeColor(store, PREF_DDL_KWCOLOR, DEFAULT_DDL_KWCOLOR);
            initializeColor(store, PREF_DDL_STRINGCOLOR, DEFAULT_DDL_STRINGCOLOR);
            initializeColor(store, PREF_DDL_HIGHLIGHTCOLOR, DEFAULT_DDL_HLCOLOR);
            
            initializeFont(
                    store, PREF_TABLE_TITLEFONT, 
                    JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
            initializeFont(store, PREF_TABLE_BODYFONT, JFaceResources.getDefaultFont());
            initializeFont(store, PREF_TABLE_TYPEFONT, JFaceResources.getDefaultFont());
            initializeFont(store, PREF_DDL_SCRIPTFONT, JFaceResources.getTextFont());
            
            store.setDefault(PREF_TABLE_SHADOW, false);
            store.setDefault(PREF_TABLE_SHOWTYPES, false);
            store.setDefault(PREF_CONNECTION_LABELS, false);
            store.setDefault(PREF_DIAGRAM_RELOCATION, true);
            store.setDefault(PREF_DECORATION_STYLE, Decorations.PREF_VALUE_DEFAULT);
            
            TableThemeExtension[] themeExtensions = RMBenchPlugin.getExtensionManager().getTableThemeExtensions();
            for (int i = 0; i < themeExtensions.length; i++) {
                initializeColor(
                        store, themeExtensions[i].getTitleFgId(), themeExtensions[i].titleFgColor);
                initializeColor(
                        store, themeExtensions[i].getTitleBgId(), themeExtensions[i].titleBgColor);
                initializeColor(
                        store, themeExtensions[i].getBodyFgId(), themeExtensions[i].bodyFgColor);
                initializeColor(
                        store, themeExtensions[i].getBodyBgId(), themeExtensions[i].bodyBgColor);
            }
            
            //store if dialog is needed
            store.setDefault(PREF_HIDE_PK_DELETION_DIALOG, false);
            store.setDefault(PREF_HIDE_PK_DATATYPE_DIALOG, false);
            
            initialized = true;
        }
    }

    private void initializeColor(IPreferenceStore store, String preference, RGB color) {
        if(color != null)
            store.setDefault(preference, StringConverter.asString(color));
        String pref = store.getString(preference);
        if(!pref.equals(IPreferenceStore.STRING_DEFAULT_DEFAULT))
            RMBenchPlugin.getColorRegistry().put(preference, StringConverter.asRGB(pref));
    }
    
    private void initializeColor(IPreferenceStore store, String preference, Color color) {
        store.setDefault(preference, StringConverter.asString(color.getRGB()));
        RMBenchPlugin.getColorRegistry().put(
                preference, StringConverter.asRGB(store.getString(preference)));
    }

    private void initializeFont(IPreferenceStore store, String preference, Font font) {
        store.setDefault(preference, StringConverter.asString(font.getFontData()));
        RMBenchPlugin.getFontRegistry().put(
                preference, StringConverter.asFontDataArray(store.getString(preference)));
    }

    private static void initializeInstanceIfNeeded() {
        if(instance == null)
            new PreferenceHandler();
        instance.initializeDefaultPreferences();
    }

    /**
     * @return the current preferences setting for table border shadow 
     */
    public static boolean getBorderShadow() {
        return RMBenchPlugin.getDefault().getPreferenceStore().getBoolean(PREF_TABLE_SHADOW);
    }

    /**
     * @return the value of the {@link #PREF_LAST_TRIAL} preference, or <code>null</code>
     * if it is not defined
     */
    public static String getLastTrial(String version) {
        String val = RMBenchPlugin.getDefault().getPreferenceStore().getString(PREF_LAST_TRIAL+version);
        return val.length() > 0 ? val : null;
    }
    
    /**
     * @param trial the new value of the {@link #PREF_LAST_TRIAL} preference
     */
    public static void setLastTrial(String version, String value) {
        RMBenchPlugin.getDefault().getPreferenceStore().setValue(PREF_LAST_TRIAL+version, value);
    }
    
    /**
     * Change the value of a color preference. Changes to color preferences should always 
     * be done through this method, to ensure the color registry is maintained properly.
     *   
     * @param preference the preference name
     * @param rgb the RGB value
     */
    public static void setRGB(String preference, RGB rgb) {
        RGB oldValue = getRGB(preference);
        if(rgb == null) {
            if(oldValue != null) {
                RMBenchPlugin.getColorRegistry().put(preference, rgb);
                RMBenchPlugin.getDefault().getPreferenceStore().
                    setValue(preference, IPreferenceStore.STRING_DEFAULT_DEFAULT);
            }
        }
        else if(!rgb.equals(oldValue)) {
            RMBenchPlugin.getColorRegistry().put(preference, rgb);
            RMBenchPlugin.getDefault().getPreferenceStore().
                setValue(preference, StringConverter.asString(rgb));
        }
    }
    
    /**
     * Reset the value of a color preference to the default.
     * 
     * @param preference the preference name
     * @see #setRGB(String, RGB)
     */
    public static void setDefaultRGB(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        String rgbString = store.getDefaultString(preference);
        
        RGB rgb = rgbString.equals(IPreferenceStore.STRING_DEFAULT_DEFAULT) ? 
                null : StringConverter.asRGB(rgbString);
        RMBenchPlugin.getColorRegistry().put(preference, rgb);
        store.setToDefault(preference);
    }
    
    /**
     * @param preference a valid color preference name
     * @return the corresponding color value, ready registered with the color registry
     */
    public static Color getColor(String preference) {
        initializeInstanceIfNeeded();
        return RMBenchPlugin.getColorRegistry().get(preference);
    }

    /**
     * 
     * @param preference a valid color preference name
     * @return the current preference value, or <code>null</code> if not defined
     */
    public static RGB getRGB(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        String pref = store.getString(preference);
        return pref.equals(IPreferenceStore.STRING_DEFAULT_DEFAULT) ? null : StringConverter.asRGB(pref);
    }
    
    /**
     * @param preference a color preference name
     * @return the default color, or <code>null</code> if not defined
     */
    public static RGB getDefaultRGB(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        String pref = store.getDefaultString(preference);
        return pref.equals(IPreferenceStore.STRING_DEFAULT_DEFAULT) ? null : StringConverter.asRGB(pref);
    }

    /**
     * Change the value of a font preference. Changes to font preferences should always 
     * be done through this method, to ensure the font registry is maintained properly.
     *   
     * @param preference the preference name
     * @param fontData the font data
     */
    public static void setFontData(String preference, FontData[] fontData) {
        FontData[] oldValue = getFontData(preference);
        if (!fontData.equals(oldValue)) {
            RMBenchPlugin.getFontRegistry().put(preference, fontData);
            RMBenchPlugin.getDefault().getPreferenceStore().
                setValue(preference, StringConverter.asString(fontData));
        }
    }
    
    /**
     * Reset the value of a font preference to the default.
     * 
     * @param preference the preference name
     * @see #setFontData(String, FontData)
     */
    public static void setDefaultFontData(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        RMBenchPlugin.getFontRegistry().put(
                preference, 
                StringConverter.asFontDataArray(store.getDefaultString(preference)));
        store.setToDefault(preference);
    }
    
    /**
     * @param preferenceName a valid font preference name
     * @return the corresponding font
     */
    public static Font getFont(String preferenceName) {
        initializeInstanceIfNeeded();
        return RMBenchPlugin.getFontRegistry().get(preferenceName);
    }
    
    /**
     * @param preference a valid font preference name
     * @return the font
     */
    public static FontData[] getFontData(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        return StringConverter.asFontDataArray(store.getString(preference));
    }

    /**
     * @param preference a valid font preference name
     * @return the default font
     */
    public static FontData[] getDefaultFontData(String preference) {
        IPreferenceStore store = RMBenchPlugin.getDefault().getPreferenceStore();
        return StringConverter.asFontDataArray(store.getDefaultString(preference));
    }

    /**
     * @return a color object created from the default preference value, registered with the
     * given color registry
     */
    public static Color getDefaultColor(ColorRegistry colorRegistry, String preferenceName) {
        colorRegistry.put(preferenceName, getDefaultRGB(preferenceName));
        return colorRegistry.get(preferenceName);
    }

    /**
     * @return a font object created from the default preference value, registered with the
     * given font registry
     */
    public static Font getDefaultFont(FontRegistry fontRegistry, String preferenceName) {
        fontRegistry.put(preferenceName, getDefaultFontData(preferenceName));
        return fontRegistry.get(preferenceName);
    }
    
    /**
     * @return a table fonts object initialized from the current preferences
     */
    public static TableFonts getTableFonts() {
        return new TableFonts(
                getFont(PREF_TABLE_TITLEFONT),
                getFont(PREF_TABLE_BODYFONT),
                getFont(PREF_TABLE_TYPEFONT));
    }

    /**
     * Apply a font preference
     * @param preferenceName the preference key
     * @param fonts the fonts object to which the change will be applied
     * @return <code>true</code> if the given name is a font preference and was therefore 
     * successfully applied
     */
    public static boolean applyFontPreference(String preferenceName, TableFonts fonts) {
        if(PREF_TABLE_TITLEFONT.equals(preferenceName))
            fonts.titleFont = getFont(preferenceName);
        else if(PREF_TABLE_BODYFONT.equals(preferenceName))
            fonts.columnFont = getFont(preferenceName);
        else if(PREF_TABLE_TYPEFONT.equals(preferenceName))
            fonts.typeFont = getFont(preferenceName);
        else
            return false;
        
        // TODO V2: while type font is not UI configurable, make equal to columnFont
        fonts.typeFont = fonts.columnFont;
        return true;
    }
    
    /**
     * Apply a theme preference
     * @param preferenceName the preference key
     * @param theme the theme object to which the change will be applied
     * @return <code>true</code> if the given name is a theme preference and was therefore 
     * applied to the theme
     */
    public static boolean applyDefaultThemePreference(String preferenceName, TableTheme theme) {
        
        if(PREF_TABLE_BODYCOLOR.equals(preferenceName))
            theme.bodyBackground = getColor(preferenceName);
        else if(PREF_TABLE_BODYFGCOLOR.equals(preferenceName))
            theme.bodyForeground = getColor(preferenceName);
        else if(PREF_TABLE_TITLECOLOR.equals(preferenceName))
            theme.titleBackground = getColor(preferenceName);
        else if(PREF_TABLE_TITLEFGCOLOR.equals(preferenceName))
            theme.titleForeground = getColor(preferenceName);
        else 
            return false;
        
        return true;
    }
    
    /**
     * Apply a type theme preference
     * @return <code>true</code> if the given name is a type theme preference and was therefore 
     * applied to the theme
     */
    public static boolean applyTypeThemePreference(
            String property, TableThemeExtension themeExtension, TableTheme theme) {
        
        if(themeExtension.getTitleFgId().equals(property)) {
            theme.titleForeground = getColor(property);
            if(theme.titleForeground == null)
                theme.titleForeground = getColor(PREF_TABLE_TITLEFGCOLOR);
            return true;
        }
        if(themeExtension.getTitleBgId().equals(property)) {
            theme.titleBackground = getColor(property);
            if(theme.titleBackground == null)
                theme.titleBackground = getColor(PREF_TABLE_TITLECOLOR);
            return true;
        }
        if(themeExtension.getBodyFgId().equals(property)) {
            theme.bodyForeground = getColor(property);
            if(theme.bodyForeground == null)
                theme.bodyForeground = getColor(PREF_TABLE_BODYFGCOLOR);
            return true;
        }
        if(themeExtension.getBodyBgId().equals(property)) {
            theme.bodyBackground = getColor(property);
            if(theme.bodyBackground == null)
                theme.bodyBackground = getColor(PREF_TABLE_BODYCOLOR);
            return true;
        }
        return false;
    }
    
    /**
     * @param typeId the table type, or <code>null</code>
     * @return a table theme initialized from the current preferences
     */
    public static TableTheme getTableTheme(String typeId) {
        
        Color titleFgColor=null, titleBgColor=null, bodyFgColor=null, bodyBgColor=null;
        
        TableTypeExtension typeExtension = typeId != null ? 
                RMBenchPlugin.getExtensionManager().getTableTypeExtension(typeId) : null;
        if(typeExtension != null) {
            titleFgColor = getColor(typeExtension.themeExtension.getTitleFgId());
            titleBgColor = getColor(typeExtension.themeExtension.getTitleBgId());
            bodyFgColor = getColor(typeExtension.themeExtension.getBodyFgId());
            bodyBgColor = getColor(typeExtension.themeExtension.getBodyBgId());

            if(titleFgColor == null)
                titleFgColor = getColor(PREF_TABLE_TITLEFGCOLOR);
            if(titleBgColor == null)
                titleBgColor = getColor(PREF_TABLE_TITLECOLOR);
            if(bodyFgColor == null)
                bodyFgColor = getColor(PREF_TABLE_BODYFGCOLOR);
            if(bodyBgColor == null)
                bodyBgColor = getColor(PREF_TABLE_BODYCOLOR);
        }
        else {
            titleFgColor = getColor(PREF_TABLE_TITLEFGCOLOR);
            titleBgColor = getColor(PREF_TABLE_TITLECOLOR);
            bodyFgColor = getColor(PREF_TABLE_BODYFGCOLOR);
            bodyBgColor = getColor(PREF_TABLE_BODYCOLOR);
        }        
        return new TableTheme(titleFgColor, titleBgColor, bodyFgColor, bodyBgColor);
    }

    public static boolean getShowLabels() {
        return RMBenchPlugin.getDefault().getPreferenceStore().getBoolean(PREF_CONNECTION_LABELS);
    }

    public static boolean getShowTypes() {
        return RMBenchPlugin.getDefault().getPreferenceStore().getBoolean(PREF_TABLE_SHOWTYPES);
    }
    
    /**
     * @return the currently configured Decorations
     */
    public static Decorations getDecorations() {
        
        String prefValue = 
            RMBenchPlugin.getDefault().getPreferenceStore().getString(PREF_DECORATION_STYLE);
        return Decorations.getDecoration(prefValue);
    }

    /**
     * @param listener a listener to be notified of preference changes
     */
    public static void addPreferenceChangeListener(IPropertyChangeListener listener) {
        RMBenchPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to be removed
     */
    public static void removePreferenceChangeListener(IPropertyChangeListener listener) {
        RMBenchPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(listener);
    }
}
