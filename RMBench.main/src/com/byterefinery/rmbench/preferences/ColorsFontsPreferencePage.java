/*
 * created 15.09.2005 by cse
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id: ColorsFontsPreferencePage.java 165 2006-02-10 13:40:34Z cse $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.util.ColorRegistry;

/**
 * ugly monstrous preference page for diagram presentation items
 * 
 * @author sell
 */
public class ColorsFontsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private class FontRegistry extends org.eclipse.jface.resource.FontRegistry {
        public void dispose() {
            PlatformUI.getWorkbench().getDisplay().asyncExec(displayRunnable);
        }
    }
    private FontRegistry fontRegistry = new FontRegistry();
    private ColorRegistry colorRegistry = new ColorRegistry();
    
    private ColorSelector titleColorSelector;
    private ColorSelector titleFgColorSelector;
    private ColorSelector bodyColorSelector;
    private ColorSelector bodyFgColorSelector;
    
    private boolean defaultTitleColor;
    private boolean defaultTitleFgColor;
    private boolean defaultBodyColor;
    private boolean defaultBodyFgColor;
    private boolean defaultTitleFont;
    private boolean defaultBodyFont;
    private boolean defaultTypeFont;
    
    private Text titleFontPreview;
    private Text bodyFontPreview;
    private Text typeFontPreview;
    private Button titleFontButton;
    private Button bodyFontButton;
    private Button typeFontButton;
    
    private Font titleFont;
    private Font bodyFont;
    private Font typeFont;

    
    private SelectionAdapter fontSelectionAdapter = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            Button sourceButton = (Button) e.getSource();
            FontDialog fontDialog = new FontDialog(getShell());
            
            if (sourceButton == titleFontButton)
                fontDialog.setFontList(titleFontPreview.getFont().getFontData());
            else if (sourceButton == bodyFontButton)
                fontDialog.setFontList(bodyFontPreview.getFont().getFontData());
            else if (sourceButton == typeFontButton)
                fontDialog.setFontList(typeFontPreview.getFont().getFontData());

            if (fontDialog.open() != null) {
                FontData[] fontData = fontDialog.getFontList();
                if (sourceButton == titleFontButton) {
                    defaultTitleFont = false;
                    fontRegistry.put(PreferenceHandler.PREF_TABLE_TITLEFONT, fontData);
                    titleFont = fontRegistry.get(PreferenceHandler.PREF_TABLE_TITLEFONT);
                    titleFontPreview.setFont(titleFont);
                }
                else if (sourceButton == bodyFontButton) {
                    defaultBodyFont = false;
                    fontRegistry.put(PreferenceHandler.PREF_TABLE_BODYFONT, fontData);
                    bodyFont = fontRegistry.get(PreferenceHandler.PREF_TABLE_BODYFONT);
                    bodyFontPreview.setFont(bodyFont);
                }
                else if (sourceButton == typeFontButton) {
                    defaultTypeFont = false;
                    fontRegistry.put(PreferenceHandler.PREF_TABLE_TYPEFONT, fontData);
                    typeFont = fontRegistry.get(PreferenceHandler.PREF_TABLE_TYPEFONT);
                    typeFontPreview.setFont(typeFont);
                }
            }
        }
    };

    protected Control createContents(Composite parent) {

        Composite mainComposite = new Composite(parent, SWT.NULL);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        mainComposite.setLayout(layout);

        createDefaultThemeGroup(mainComposite);
        createFontGroup(mainComposite);

        return mainComposite;
    }

    private void createFontGroup(Composite parent) {
        Label label;
        GridData data;

        Group fontGroup = new Group(parent, SWT.NULL);
        fontGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        fontGroup.setLayout(layout);
        fontGroup.setText(Messages.fontGroup);

        label = new Label(fontGroup, SWT.LEFT);
        label.setText(Messages.titleFont);

        titleFontPreview = new Text(fontGroup, SWT.CENTER | SWT.READ_ONLY);
        titleFontPreview.setText(Messages.titlePreview);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        titleFontPreview.setLayoutData(data);
        titleFontPreview.setBackground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_TITLECOLOR));
        titleFontPreview.setForeground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR));
        titleFontPreview.setFont(PreferenceHandler.getFont(PreferenceHandler.PREF_TABLE_TITLEFONT));

        titleFontButton = new Button(fontGroup, SWT.PUSH);
        titleFontButton.setText(Messages.buttonChange);
        titleFontButton.addSelectionListener(fontSelectionAdapter);

        label = new Label(fontGroup, SWT.LEFT);
        label.setText(Messages.bodyFont);
        bodyFontPreview = new Text(fontGroup, SWT.CENTER | SWT.READ_ONLY);
        bodyFontPreview.setText(Messages.bodyPreview);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        bodyFontPreview.setLayoutData(data);
        bodyFontPreview.setBackground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_BODYCOLOR));
        bodyFontPreview.setForeground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
        bodyFontPreview.setFont(PreferenceHandler.getFont(PreferenceHandler.PREF_TABLE_BODYFONT));

        bodyFontButton = new Button(fontGroup, SWT.PUSH);
        bodyFontButton.setText(Messages.buttonChange);
        bodyFontButton.addSelectionListener(fontSelectionAdapter);

        label = new Label(fontGroup, SWT.LEFT);
        label.setText(Messages.typeFont);
        typeFontPreview = new Text(fontGroup, SWT.CENTER | SWT.READ_ONLY);
        typeFontPreview.setText(Messages.typePreview);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        typeFontPreview.setLayoutData(data);
        typeFontPreview.setBackground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_BODYCOLOR));
        typeFontPreview.setForeground(PreferenceHandler
                .getColor(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
        typeFontPreview.setFont(PreferenceHandler.getFont(PreferenceHandler.PREF_TABLE_TYPEFONT));

        typeFontButton = new Button(fontGroup, SWT.PUSH);
        typeFontButton.setText(Messages.buttonChange);
        typeFontButton.addSelectionListener(fontSelectionAdapter);
    }

    private void createDefaultThemeGroup(Composite mainComposite) {

        Group defaultThemeGroup = new Group(mainComposite, SWT.NULL);
        defaultThemeGroup.setText(Messages.default_theme);

        defaultThemeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        defaultThemeGroup.setLayout(layout);

        Label label = new Label(defaultThemeGroup, SWT.LEFT);
        label = new Label(defaultThemeGroup, SWT.LEFT);
        label.setText(Messages.background);
        label = new Label(defaultThemeGroup, SWT.LEFT);
        label.setText(Messages.foreground);

        label = new Label(defaultThemeGroup, SWT.LEFT);
        label.setText(Messages.default_title);
        titleColorSelector = new ColorSelector(defaultThemeGroup);
        titleColorSelector.setColorValue(PreferenceHandler
                .getRGB(PreferenceHandler.PREF_TABLE_TITLECOLOR));
        titleColorSelector.addListener(new ColorChangeListener(titleColorSelector));

        titleFgColorSelector = new ColorSelector(defaultThemeGroup);
        titleFgColorSelector.setColorValue(PreferenceHandler
                .getRGB(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR));
        titleFgColorSelector.addListener(new ColorChangeListener(titleFgColorSelector));

        label = new Label(defaultThemeGroup, SWT.LEFT);
        label.setText(Messages.default_body);
        bodyColorSelector = new ColorSelector(defaultThemeGroup);
        bodyColorSelector.setColorValue(PreferenceHandler
                .getRGB(PreferenceHandler.PREF_TABLE_BODYCOLOR));
        bodyColorSelector.addListener(new ColorChangeListener(bodyColorSelector));

        bodyFgColorSelector = new ColorSelector(defaultThemeGroup);
        bodyFgColorSelector.setColorValue(PreferenceHandler
                .getRGB(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
        bodyFgColorSelector.addListener(new ColorChangeListener(bodyFgColorSelector));
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(RMBenchPlugin.getDefault().getPreferenceStore());
    }

    protected void performDefaults() {
        titleColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_TABLE_TITLECOLOR));
        titleFgColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR));
        bodyColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_TABLE_BODYCOLOR));
        bodyFgColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));

        titleFontPreview.setBackground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_TITLECOLOR));
        titleFontPreview.setForeground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_TITLEFGCOLOR));
        titleFontPreview.setFont(PreferenceHandler.getDefaultFont(fontRegistry,
                PreferenceHandler.PREF_TABLE_TITLEFONT));

        bodyFontPreview.setBackground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_BODYCOLOR));
        bodyFontPreview.setForeground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
        bodyFontPreview.setFont(PreferenceHandler.getDefaultFont(fontRegistry,
                PreferenceHandler.PREF_TABLE_BODYFONT));

        typeFontPreview.setBackground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_BODYCOLOR));
        typeFontPreview.setForeground(PreferenceHandler.getDefaultColor(colorRegistry,
                PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
        typeFontPreview.setFont(PreferenceHandler.getDefaultFont(fontRegistry,
                PreferenceHandler.PREF_TABLE_TYPEFONT));

        defaultTitleColor = defaultTitleFgColor = defaultBodyColor = defaultBodyFgColor = true;
        defaultBodyFont = defaultTitleFont = defaultTypeFont = true;

        super.performDefaults();
    }

    public boolean performOk() {

        if (defaultTitleColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_TABLE_TITLECOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_TABLE_TITLECOLOR, 
                    titleColorSelector.getColorValue());

        if (defaultTitleFgColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_TABLE_TITLEFGCOLOR,
                    titleFgColorSelector.getColorValue());

        if (defaultBodyColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_TABLE_BODYCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_TABLE_BODYCOLOR, 
                    bodyColorSelector.getColorValue());

        if (defaultBodyFgColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_TABLE_BODYFGCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_TABLE_BODYFGCOLOR, 
                    bodyFgColorSelector.getColorValue());

        if (defaultTitleFont)
            PreferenceHandler.setDefaultFontData(PreferenceHandler.PREF_TABLE_TITLEFONT);
        else if (titleFont != null)
            PreferenceHandler.setFontData(
                    PreferenceHandler.PREF_TABLE_TITLEFONT, 
                    titleFont.getFontData());

        if (defaultBodyFont)
            PreferenceHandler.setDefaultFontData(PreferenceHandler.PREF_TABLE_BODYFONT);
        else if (bodyFont != null)
            PreferenceHandler.setFontData(
                    PreferenceHandler.PREF_TABLE_BODYFONT, bodyFont.getFontData());

        if (defaultTypeFont)
            PreferenceHandler.setDefaultFontData(PreferenceHandler.PREF_TABLE_TYPEFONT);
        else if (typeFont != null)
            PreferenceHandler.setFontData(
                    PreferenceHandler.PREF_TABLE_TYPEFONT, typeFont.getFontData());
        
        return super.performOk();
    }

    public void dispose() {
        colorRegistry.dispose();
        fontRegistry.dispose();
        super.dispose();
    }

    private class ColorChangeListener implements IPropertyChangeListener {

        private final ColorSelector owner;

        ColorChangeListener(ColorSelector owner) {
            this.owner = owner;
        }

        public void propertyChange(PropertyChangeEvent event) {
            if (owner == titleColorSelector) {
                colorRegistry.put(PreferenceHandler.PREF_TABLE_TITLECOLOR, titleColorSelector
                        .getColorValue());
                titleFontPreview.setBackground(colorRegistry.get(PreferenceHandler.PREF_TABLE_TITLECOLOR));
                defaultTitleColor = false;
            }
            else if (owner == titleFgColorSelector) {
                colorRegistry.put(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR, titleFgColorSelector
                        .getColorValue());
                titleFontPreview.setForeground(colorRegistry.get(PreferenceHandler.PREF_TABLE_TITLEFGCOLOR));
                defaultTitleFgColor = false;
            }
            if (owner == bodyColorSelector) {
                colorRegistry.put(PreferenceHandler.PREF_TABLE_BODYCOLOR, bodyColorSelector
                        .getColorValue());
                bodyFontPreview.setBackground(colorRegistry.get(PreferenceHandler.PREF_TABLE_BODYCOLOR));
                typeFontPreview.setBackground(colorRegistry.get(PreferenceHandler.PREF_TABLE_BODYCOLOR));
                defaultBodyColor = false;
            }
            if (owner == bodyFgColorSelector) {
                colorRegistry.put(PreferenceHandler.PREF_TABLE_BODYFGCOLOR, bodyFgColorSelector
                        .getColorValue());
                bodyFontPreview.setForeground(colorRegistry.get(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
                typeFontPreview.setForeground(colorRegistry.get(PreferenceHandler.PREF_TABLE_BODYFGCOLOR));
                defaultBodyFgColor = false;
            }
        }
    }
}
