/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench.actions;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.actions.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
    
    public static String Layout_text;
    public static String Layout_description;
    public static String Delete_Label;
    public static String Delete_Tooltip;
    public static String Remove_Label;
    public static String Remove_Tooltip;
    public static String Delete_GroupLabel;
    public static String Remove_GroupLabel;
    public static String Print_Label;
    public static String Print_Tooltip;
    public static String ForeignKey_Label;
    public static String ForeignKey_Description;
    public static String TableType_None;
    public static String TablesDiagram_Label;
    public static String TablesDiagram_Description;
    public static String TableDetails_Label;
    public static String TableDetails_Description;
    public static String Cut_Label;
    public static String Copy_Label;
    public static String Paste_Label;
    public static String ToggleGrid_Label;
    public static String DiagramMenu_Label;
    public static String TableTypesSubmenu_ActionLabelText;
    public static String PageOutlineAction_Title;
    public static String PrinterSetupAction_Title;
    public static String Export_Label;
    public static String Export_Tooltip;
	public static String DiagramExportAction_Title;
    public static String AddStubbedTablesAction_Text;
    public static String No_PrimaryKey_matches;
}
