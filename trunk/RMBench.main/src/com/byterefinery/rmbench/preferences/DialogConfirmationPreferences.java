/*
 * created 17-Feb-2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: DialogConfirmationPreferences.java 296 2006-03-15 17:09:46Z cse $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.byterefinery.rmbench.RMBenchPlugin;

/**
 * Preference dialog to activate and deavtivate the confirmation dialogs for several actions
 * @author Hannes Niederhausen
 *
 */
public class DialogConfirmationPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public DialogConfirmationPreferences() {
        super(0);
        
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(RMBenchPlugin.getDefault().getPreferenceStore());
        initialize();
    }

    protected void createFieldEditors() {
       
        //creating filedDialogs
        addField(
                new BooleanFieldEditor(
                        PreferenceHandler.PREF_HIDE_PK_DATATYPE_DIALOG, 
                        Messages.DialogConfirmationPreferences_PK_Datatype, 
                        getFieldEditorParent()));        
        addField(
                new BooleanFieldEditor(
                        PreferenceHandler.PREF_HIDE_PK_DELETION_DIALOG, 
                        Messages.DialogConfirmationPreferences_PK_DELETION, 
                        getFieldEditorParent()));
    }
}
