/*
 * created 05.01.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * a preference page for specifying values for syntax coloring in the DDL source viewer
 * 
 * @author cse
 */
public class DDLSourcePreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage, IPropertyChangeListener {

    private ColorSelector commentColorSelector;
    private ColorSelector stringColorSelector;
    private ColorSelector codeColorSelector;
    private ColorSelector kwColorSelector;
    private ColorSelector highlightColorSelector;
    
    private boolean defaultCommentColor;
    private boolean defaultStringColor;
    private boolean defaultCodeColor;
    private boolean defaultKwColor;
    private boolean defaultHighlightColor;
    
    public void init(IWorkbench workbench) {
        setPreferenceStore(RMBenchPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.DDLSourcePreferencePage_syntaxColoring);
    }

    protected Control createContents(Composite parent) {
        Composite fieldEditorParent = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);
        fieldEditorParent.setFont(parent.getFont());
        
        Label label;
        
        label = new Label(fieldEditorParent, SWT.LEFT);
        label.setText(Messages.DDLSourcePreferencePage_commentcolor);
        commentColorSelector = new ColorSelector(fieldEditorParent);
        commentColorSelector.setColorValue(PreferenceHandler.getRGB(PreferenceHandler.PREF_DDL_COMMENTCOLOR));
        commentColorSelector.addListener(this);
        computeButtonSize(commentColorSelector);
        
        label = new Label(fieldEditorParent, SWT.LEFT);
        label.setText(Messages.DDLSourcePreferencePage_stringcolor);
        stringColorSelector = new ColorSelector(fieldEditorParent);
        stringColorSelector.setColorValue(PreferenceHandler.getRGB(PreferenceHandler.PREF_DDL_STRINGCOLOR));
        stringColorSelector.addListener(this);
        computeButtonSize(stringColorSelector);
        
        label = new Label(fieldEditorParent, SWT.LEFT);
        label.setText(Messages.DDLSourcePreferencePage_codecolor);
        codeColorSelector = new ColorSelector(fieldEditorParent);
        codeColorSelector.setColorValue(PreferenceHandler.getRGB(PreferenceHandler.PREF_DDL_CODECOLOR));
        codeColorSelector.addListener(this);
        computeButtonSize(codeColorSelector);

        label = new Label(fieldEditorParent, SWT.LEFT);
        label.setText(Messages.DDLSourcePreferencePage_kwcolor);
        kwColorSelector = new ColorSelector(fieldEditorParent);
        kwColorSelector.setColorValue(PreferenceHandler.getRGB(PreferenceHandler.PREF_DDL_KWCOLOR));
        kwColorSelector.addListener(this);
        computeButtonSize(kwColorSelector);
        
        label = new Label(fieldEditorParent, SWT.LEFT);
        label.setText(Messages.DDLSourcePreferencePage_highlightcolor);
        highlightColorSelector = new ColorSelector(fieldEditorParent);
        highlightColorSelector.setColorValue(PreferenceHandler.getRGB(PreferenceHandler.PREF_DDL_HIGHLIGHTCOLOR));
        highlightColorSelector.addListener(this);
        computeButtonSize(highlightColorSelector);
        
        return fieldEditorParent;
    }

    protected void computeButtonSize(ColorSelector selector) {
        
        GridData gd = new GridData();
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        gd.widthHint = Math.max(
                widthHint, selector.getButton().computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        selector.getButton().setLayoutData(gd);
    }
    
    protected void performDefaults() {
        commentColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_DDL_COMMENTCOLOR));
        stringColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_DDL_STRINGCOLOR));
        codeColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_DDL_CODECOLOR));
        kwColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_DDL_KWCOLOR));
        highlightColorSelector.setColorValue(PreferenceHandler
                .getDefaultRGB(PreferenceHandler.PREF_DDL_HIGHLIGHTCOLOR));

        defaultCommentColor = defaultStringColor = defaultCodeColor = defaultKwColor = defaultHighlightColor = true;

        super.performDefaults();
    }

    public boolean performOk() {

        if (defaultCommentColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_DDL_COMMENTCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_DDL_COMMENTCOLOR,
                    commentColorSelector.getColorValue());

        if (defaultStringColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_DDL_STRINGCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_DDL_STRINGCOLOR,
                    stringColorSelector.getColorValue());

        if (defaultCodeColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_DDL_CODECOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_DDL_CODECOLOR, 
                    codeColorSelector.getColorValue());

        if (defaultKwColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_DDL_KWCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_DDL_KWCOLOR, 
                    kwColorSelector.getColorValue());
        
        if (defaultHighlightColor)
            PreferenceHandler.setDefaultRGB(PreferenceHandler.PREF_DDL_HIGHLIGHTCOLOR);
        else
            PreferenceHandler.setRGB(
                    PreferenceHandler.PREF_DDL_HIGHLIGHTCOLOR, 
                    highlightColorSelector.getColorValue());
        
        return super.performOk();
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() == commentColorSelector) {
            defaultCommentColor = false;
        }
        else if (event.getSource() == stringColorSelector) {
            defaultStringColor = false;
        }
        else if (event.getSource() == codeColorSelector) {
            defaultCodeColor = false;
        }
        else if (event.getSource() == kwColorSelector) {
            defaultKwColor = false;
        }
        else if (event.getSource() == highlightColorSelector) {
            defaultHighlightColor = false;
        }
    }
}
