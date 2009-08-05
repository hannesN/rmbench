package com.byterefinery.rmbench.database.derby;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.byterefinery.rmbench.database.derby.Messages"; //$NON-NLS-1$

    static {
           NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    public static String EmbeddedURLSetupGroup_database_path;
    public static String EmbeddedURLSetupGroup_browse_button_label;
    public static String EmbeddedURLSetupGroup_database_name;
    public static String EmbeddedURLSetupGroup_create_button_label;
    public static String EmbeddedURLSetupGroup_error_db_path_needed;
    public static String EmbeddedURLSetupGroup_error_db_name_needed;
    public static String EmbeddedURLSetupGroup_error_db_not_exists;
    public static String EmbeddedURLSetupGroup_info_creating_db;
    public static String EmbeddedURLSetupGroup_invalid_database_name;
    public static String EmbeddedURLSetupGroup_invalid_path;
}
