/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 519 2006-08-29 15:31:32Z cse $
 */
package com.byterefinery.rmbench.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
    
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.preferences.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
    public static String default_body;
    public static String default_title;
    public static String background;
    public static String foreground;
    public static String default_theme;
    public static String buttonChange;
    public static String titleFont;
    public static String bodyFont;
    public static String fontGroup;
    public static String titlePreview;
    public static String bodyPreview;
    public static String typeFont;
    public static String typePreview;
    public static String titleBgColor;
    public static String titleFgColor;
    public static String bodyBgColor;
    public static String bodyFgColor;
    public static String buttonDelete;
    
    public static String ShowConnectionLabels;
    public static String ShowTableShadow;
    public static String DecorationStyle;
    public static String ShowTableDataTypes;
    
    public static String DDLSourcePreferencePage_commentcolor;
    public static String DDLSourcePreferencePage_stringcolor;
    public static String DDLSourcePreferencePage_codecolor;
    public static String DDLSourcePreferencePage_kwcolor;
    public static String DDLSourcePreferencePage_syntaxColoring;
    public static String DDLSourcePreferencePage_highlightcolor;
    public static String DDLSourcePreferencePage_scriptFont;
    public static String DDLSourcePreferencePage_changeFont;
    
	public static String RMBenchPreferencePage_LicenseError;
    public static String RMBenchPreferencePage_broughtToYouBy;
    public static String RMBenchPreferencePage_licenseGroup;
    public static String RMBenchPreferencePage_installLicense;
    public static String RMBenchPreferencePage_fullLicense;
    public static String RMBenchPreferencePage_trialLicense;
    public static String RMBenchPreferencePage_communityLicense;
    public static String RMBenchPreferencePage_licenseAddendum;
    public static String RMBenchPreferencePage_userLicenseInfo;
    public static String RMBenchPreferencePage_licenseDetails;
	
    public static String DialogConfirmationPreferences_PK_Datatype;
    public static String DialogConfirmationPreferences_PK_DELETION;
    public static String DialogConfirmationPreferences_Grouptitle;
    public static String DiagramPreferencePage_RelocateAnchors;
}
