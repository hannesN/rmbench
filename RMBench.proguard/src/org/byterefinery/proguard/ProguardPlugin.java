package org.byterefinery.proguard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ProguardPlugin extends AbstractUIPlugin {

    private MessageConsole messageConsole;
    
	//The shared instance.
	private static ProguardPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ProguardPlugin() {
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
	public static ProguardPlugin getDefault() {
		return plugin;
	}
    
    /**
     * @return the console for writing notification messages
     */
    public MessageConsole getMessageConsole() {
        if(messageConsole == null) {
            messageConsole = new MessageConsole("ProGuard Message Console", null);
            ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});
        }
        return messageConsole;
    }

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.byterefinery.retrace", path);
	}
}
