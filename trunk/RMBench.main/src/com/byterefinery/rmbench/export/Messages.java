/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 670 2007-10-30 05:24:49Z cse $
 */
package com.byterefinery.rmbench.export;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
	
    public static String ScriptPane_Title;
    public static String StructurePane_Title;
    public static String ExecuteScript_Text;
    public static String ExecuteScript_Description;
    public static String Next_Text;
    public static String Next_Description;
    public static String Prev_Text;
    public static String Prev_Description;
    
    public static String CompareInput_beginTask;
    public static String CompareInput_Title;
    public static String CompareInput_compareFailed;
    public static String CompareInput_DialogTitle;
    public static String CompareInput_noDifferences;
    public static String CompareInput_subtaskLoad;
    
    public static String CompareValuesDialog_title;
    public static String CompareValuesDialog_databaseLabel;
    public static String CompareValuesDialog_modelLabel;
    
	public static String ModelDiffViewer_importText;
	public static String ModelDiffViewer_importDescription;
	public static String ModelDiffViewer_importOperation;
    
    public static String ExportInput_NameExt;
    public static String ExportInput_ToolTip;
    public static String ExportEditor_executeScript;
    public static String ExportEditor_executeStmt;
	public static String ExportEditor_executeError;
	public static String ExportEditor_executeError2;
    public static String ExportEditor_connectError;
    public static String ExportEditor_noStmt;
    public static String ExportEditor_infoTitle;
    public static String ExportEditor_noStmts;
    public static String ExportEditor_makeEditable;
    public static String ExportEditor_closeConnection;
    
    public static String CreateEditorError_Title;
    public static String CreateEditorError_MsgInput;
    public static String MCEditor_warning_save_delete;
    public static String MCEditor_error_save_title;
    public static String MCEditor_error_save_message;
    
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.export.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
