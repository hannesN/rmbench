/*
 * created 15.09.2005 by sell
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: DiagramPreferencePage.java 360 2006-05-14 16:25:40Z hannesn $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.figures.Decorations;

/**
 * root preference page for the RMBench plugin
 * 
 * @author sell
 */
public class DiagramPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private BooleanFieldEditor showTypesEditor;
    private BooleanFieldEditor showConnectionLabelsEditor;
    private BooleanFieldEditor showTableShadowEditor;
    private BooleanFieldEditor relocateAnchorsEditor;
    private RadioGroupFieldEditor decorationsEditor;
    
    protected Control createContents(Composite parent) {
        
        Composite mainComposite = new Composite(parent, SWT.NULL);
        
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        mainComposite.setLayoutData(data);
        mainComposite.setLayout(new GridLayout());

        decorationsEditor = new RadioGroupFieldEditor(
                PreferenceHandler.PREF_DECORATION_STYLE,
                Messages.DecorationStyle,
                1,
                new String[][]{
                        {"IDEF1X", Decorations.PREF_VALUE_IDEF1X}, //$NON-NLS-1$
                        {"IE (Crow's Foot)", Decorations.PREF_VALUE_IE}, //$NON-NLS-1$
                        {"UML", Decorations.PREF_VALUE_UML}, //$NON-NLS-1$
                        {"Simple", Decorations.PREF_VALUE_DEFAULT} //$NON-NLS-1$
                },
                mainComposite, true);
        
        decorationsEditor.setPage(this);
        decorationsEditor.setPreferenceStore(getPreferenceStore());
        decorationsEditor.load();

        showTypesEditor = new BooleanFieldEditor(
                PreferenceHandler.PREF_TABLE_SHOWTYPES, 
                Messages.ShowTableDataTypes,
                mainComposite);
        
        showTypesEditor.setPage(this);
        showTypesEditor.setPreferenceStore(getPreferenceStore());
        showTypesEditor.load();
        
        showConnectionLabelsEditor = new BooleanFieldEditor(
                PreferenceHandler.PREF_CONNECTION_LABELS, 
                Messages.ShowConnectionLabels, 
                mainComposite);
        
        showConnectionLabelsEditor.setPage(this);
        showConnectionLabelsEditor.setPreferenceStore(getPreferenceStore());
        showConnectionLabelsEditor.load();
        
        showTableShadowEditor = new BooleanFieldEditor(
                PreferenceHandler.PREF_TABLE_SHADOW, 
                Messages.ShowTableShadow,
                mainComposite);
        
        showTableShadowEditor.setPage(this);
        showTableShadowEditor.setPreferenceStore(getPreferenceStore());
        showTableShadowEditor.load();
        
        relocateAnchorsEditor = new BooleanFieldEditor(
                PreferenceHandler.PREF_DIAGRAM_RELOCATION, 
                Messages.DiagramPreferencePage_RelocateAnchors,
                mainComposite);
        
        relocateAnchorsEditor.setPage(this);
        relocateAnchorsEditor.setPreferenceStore(getPreferenceStore());
        relocateAnchorsEditor.load();
        
        return mainComposite;
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(RMBenchPlugin.getDefault().getPreferenceStore());
    }

    protected void performDefaults() {
        showTypesEditor.loadDefault();
        showConnectionLabelsEditor.loadDefault();
        showTableShadowEditor.loadDefault();
        decorationsEditor.loadDefault();
        relocateAnchorsEditor.loadDefault();
        
        super.performDefaults();
    }

    public boolean performOk() {
        showTypesEditor.store();
        showConnectionLabelsEditor.store();
        showTableShadowEditor.store();
        decorationsEditor.store();
        relocateAnchorsEditor.store();
        
        return super.performOk();
    }
}
