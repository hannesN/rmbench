/*
 * created 13.05.2005
 * 
 * $Id: RMBenchConstants.java 664 2007-09-28 17:28:39Z cse $
 */
package com.byterefinery.rmbench;

/**
 * @author cse
 */
public interface RMBenchConstants {
    
    /** the plugin ID as registered in plugin.xml*/
    public static final String PLUGIN_ID = "com.byterefinery.rmbench";
    
    /** the registered file extension for RMBench model files  */
    public static final String MODEL_FILE_EXTENSION = "rmb";
    
    /** used internally to signify an unspecified position */
    public static final int UNSPECIFIED_POSITION = -1;
    
    public interface HelpContexts {
    	String TableStubSelectionDialog = PLUGIN_ID+".TableStubSelectionDialog";
    	String TargetTableChooser = PLUGIN_ID+".TargetTableChooser";
    }
}
