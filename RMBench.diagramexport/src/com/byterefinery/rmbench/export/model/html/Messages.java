/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 395 2006-06-30 21:21:11Z cse $
 */
package com.byterefinery.rmbench.export.model.html;

import org.eclipse.osgi.util.NLS;

/**
 * translatable messages
 *  
 * @author sell
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.export.model.html.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
    
	public static String HTMLExporter_Description;
	public static String HTMLExporter_Title;
	public static String HTMLExporter_LabelImageSize;
	public static String HTMLExporter_ImageSizeCustom;
	public static String HTMLExporter_LabelWidth;
	public static String HTMLExporter_LabelHeight;
	public static String HTMLExporter_Group_UseTemplate;
	public static String HTMLExporter_Button_2Frame;
	public static String HTMLExporter_Button_3Frame;
}
