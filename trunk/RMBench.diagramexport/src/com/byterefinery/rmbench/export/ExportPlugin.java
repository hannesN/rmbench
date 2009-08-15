/*
 * created 24.02.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: ExportPlugin.java 367 2006-05-23 19:29:53Z cse $
 */
package com.byterefinery.rmbench.export;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class ExportPlugin extends Plugin {

	private static final String PLUGIN_ID = "com.byterefinery.rmbench.diagramexport";
    
    
    //The shared instance.
	private static ExportPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ExportPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ExportPlugin getDefault() {
		return plugin;
	}

    /**
     * convenience method for writing an error log entry
     * 
     * @see org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)
     */
    public static void logError(Throwable exception) {
        
        String message = exception.getMessage();
        if(message == null) message = exception.getClass().getName();
        
        plugin.getLog().log(
                new Status(
                IStatus.ERROR, 
                PLUGIN_ID, 
                0, message, exception));
    }
}
