/*
 * created 02.08.2005 by sell
 *
 * $Id: Messages.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import org.eclipse.osgi.util.NLS;

/**
 * translatable strings for dialogs
 *  
 * @author sell
 */
public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.extension.Messages"; //$NON-NLS-1$
    static {
	       NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

    public static String ExtensionManager_invalidFactory;
    public static String ExtensionManager_invalidDatabase;
    public static String ExtensionManager_invalidNameGenerator;
    public static String ExtensionManager_invalidDBInfoReference;
    public static String ExtensionManager_invalidDDLGenerator;
    public static String ExtensionManager_invalidDDLWizardCreator;
    public static String ExtensionManager_invalidDDLFormatter;
    public static String ExtensionManager_invalidDDLScript;
    public static String ExtensionManager_invalidImageExporter;
	public static String ExtensionManager_invalidModelExporter;
	public static String ExtensionManager_invalidProviderExtension;
}
