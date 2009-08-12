/*
 * created 14.03.2005
 * 
 * $Id: RMBenchPlugin.java 679 2008-02-22 08:42:26Z cse $
 */
package com.byterefinery.rmbench;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.extension.DDLGeneratorExtension;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.extension.ExtensionManager;
import com.byterefinery.rmbench.extension.NameGeneratorExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.external.IExternalJdbcProvider;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.external.IExternalJdbcProvider.Connector;
import com.byterefinery.rmbench.external.model.IModel;
import com.byterefinery.rmbench.external.model.IModelElement;
import com.byterefinery.rmbench.jdbc.ExternalJdbcAccess;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.ColorRegistry;
import com.byterefinery.rmbench.util.ImageConstants;
import com.byterefinery.rmbench.util.LicenseManager;
import com.byterefinery.rmbench.util.ModelManager;
import com.byterefinery.rmbench.util.PrintState;
import com.byterefinery.rmbench.views.model.ModelView;
import com.byterefinery.rmbench.views.table.TableDetailsView;

/**
 * The main plugin class to be used in the desktop.
 */
public class RMBenchPlugin extends AbstractUIPlugin {
    
	public static final String VERSION = "1.0";

    public static final String RMBENCH_HOME = "http://www.rmbench.com";
    public static final String RMBENCH_HOME_HREF = "<a href=\"http://www.rmbench.com\">www.rmbench.com</a>";
    
    private static final URL RMBENCH_URL;
    static {
        URL url = null;
        try {
            url = new URL(RMBENCH_HOME);
        }
        catch (MalformedURLException e) {
        }
        RMBENCH_URL = url;
    }
    
    //the singleton instance
    private static RMBenchPlugin plugin;
    
    //the version of the eclipse.ui plugin
    private static String eclipseUIVersion;
    
    private final PrintState printState = new PrintState();
    private final LicenseManager licenseManager;
    private final ModelManager modelManager;
    private final ExtensionManager extensionManager;
    private final EventManager eventManager;
    private final RMBenchSaveParticipant saveParticipant;
    
    private ColorRegistry colorRegistry = new ColorRegistry();
    
    private MessageConsole messageConsole;
    
    protected final List<DBModel> dbmodels = new ArrayList<DBModel>();
    private boolean externalModelsLoaded;

    private final IExternalJdbcProvider.Listener externalJdbcProviderListener = 
        new IExternalJdbcProvider.Listener() {

        public void added(Connector connector) {
            addDBModel(new DBModel(connector.getName(), new ExternalJdbcAccess(connector)));
        }

        public void removed(String name) {
            removeDBModel(name);
        }
    };
    
	public RMBenchPlugin() {
		super();
		plugin = this;
        licenseManager = new LicenseManager();
        extensionManager = new ExtensionManager();
        eventManager = new EventManager();
        modelManager = new ModelManager();
        saveParticipant = new RMBenchSaveParticipant();
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
        super.start(context);
        
        ISavedState lastState = 
            ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);
        
        if (lastState != null) {
            IPath location = lastState.lookup(new Path(RMBenchSaveParticipant.RMBENCHSTATE_XML));
            if (location == null)
                return;
            File stateFile = getStateLocation().append(location).toFile();
            saveParticipant.readPluginState(stateFile, this);
        }
        licenseManager.activate();
        modelManager.activate();
    }

    /*
     * load jdbc providers that adapt external connection definitions
     */
    private void initExternalJdbcProviders() {
        if(externalModelsLoaded) {
            return;
        }
        externalModelsLoaded = true;
        for (IExternalJdbcProvider provider : extensionManager.externalJdbcProviders()) {
            for (IExternalJdbcProvider.Connector connector : provider.getConnectors()) {
                dbmodels.add(
                        new DBModel(
                                connector.getName(), 
                                new ExternalJdbcAccess(connector)));
            }
            provider.activate(externalJdbcProviderListener);
		}
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
        licenseManager.deactivate();
        modelManager.deactivate();
        for (IExternalJdbcProvider provider : extensionManager.externalJdbcProviders()) {
            provider.deactivate(externalJdbcProviderListener);
        }
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RMBenchPlugin getDefault() {
		return plugin;
	}

    /**
     * @return the manager for coordinating plugin-global events 
     */
    public static EventManager getEventManager() {
        return plugin.eventManager;
    }

    /**
     * @return the history for plugin operations
     */
    public static IOperationHistory getOperationHistory() {
        return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
    }
    
    /**
     * @return the console for writing notification messages
     */
    public MessageConsole getMessageConsole() {
        if(messageConsole == null) {
            messageConsole = new MessageConsole(RMBenchMessages.MessageConsole_name, null);
            ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});
        }
        return messageConsole;
    }
    
    /**
     * output an error message
     * @param message
     */
    public static void logError(String message) {

        plugin.getLog().log(new Status(
                IStatus.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, null));
    }
    
    /**
     * format and output an error message
     * @param message
     * @param params
     */
    public static void logError(String message, Object[] arguments) {

        MessageFormat.format(message, arguments);
        plugin.getLog().log(new Status(
                IStatus.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, null));
    }
    
    /**
     * convenience method for writing an error log entry
     * 
     * @see org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)
     */
    public static void logError(String message, Throwable exception) {
        
        plugin.getLog().log(new Status(
                IStatus.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, exception));
    }
    
    /**
     * convenience method for writing an error log entry
     * 
     * @see org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)
     */
    public static void logError(Throwable exception) {
        
        String message = exception.getMessage();
        if(message == null) message = ExceptionMessages.errorOccurred;
        
        plugin.getLog().log(new Status(
				IStatus.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, exception));
    }
	
    /**
     * output a warning message
     * @param message
     */
    public static void logWarning(String message) {
        
        plugin.getLog().log(new Status(
                IStatus.WARNING, RMBenchConstants.PLUGIN_ID, 0, message, null));
    }
    
    /**
     * format and output a warning message
     * @param message
     * @param params
     */
    public static void logWarning(String message, Object[] arguments) {
        
        MessageFormat.format(message, arguments);
        plugin.getLog().log(new Status(
                IStatus.WARNING, RMBenchConstants.PLUGIN_ID, 0, message, null));
    }
    
    /**
     * convenience method for writing an error log entry
     * 
     * @see org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)
     */
    public static void logInfo(String message) {
        plugin.getLog().log(new Status(IStatus.INFO, RMBenchConstants.PLUGIN_ID, 0, message, null));
    }
    
    /**
     * @param message the status message
     * @return a new <code>ERROR</code> status object for this plugin
     */
    public static IStatus newErrorStatus(Exception e) {
    	return new Status(IStatus.ERROR, RMBenchConstants.PLUGIN_ID, e.getMessage(), e);
    }
    
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
        return plugin.getImageRegistry().getDescriptor(path);
	}
    
    public static Image getImage(String path) {
        return plugin.getImageRegistry().get(path);
    }

    protected void initializeImageRegistry(ImageRegistry registry) {
        ImageConstants.initializeRegistry(registry);
    }

    public static ColorRegistry getColorRegistry() {
    	return plugin.colorRegistry;
    }
    
    public static FontRegistry getFontRegistry() {
        return JFaceResources.getFontRegistry();
    }
    
    /**
     * @return a array containing all currently defined connection specifications
     */
    public DBModel[] getDBModels() {
        initExternalJdbcProviders();
		return dbmodels.toArray(new DBModel[dbmodels.size()]);
    }


    /**
     * @param databaseInfo
     * @return a array containing all connection specifications whose database info matches
     * the parameter
     */
    public DBModel[] getDBModels(IDatabaseInfo databaseInfo) {
        initExternalJdbcProviders();
        
        List<DBModel> result = new ArrayList<DBModel>(dbmodels.size());
        for (Iterator<DBModel> it = dbmodels.iterator(); it.hasNext();) {
            DBModel connection = it.next();
            if(connection.getDatabaseInfo() == databaseInfo)
                result.add(connection);
        }
        return result.toArray(new DBModel[result.size()]);
    }
    
    /**
     * add a newly defined connection, so that it is stored with the plugin state
     * @param connection the new connection
     */
    public static void addDBModel(DBModel connection) {
        plugin.dbmodels.add(connection);
        plugin.eventManager.fireDBModelAdded(plugin, connection);
        plugin.saveParticipant.setChanged(RMBenchSaveParticipant.CHANGED_CONNECTIONS);
    }

    /**
     * mark the given connection as changed, so that it gets saved with the plugin state
     * @param connection a connection, which is assumed to already be held by the receiver, 
     * or <code>null</code> if unspecified
     * @param newConnection a new connection object which represents the changed state
     */
    public static void dbModelChanged(DBModel connection, DBModel newConnection) {
        int index = plugin.dbmodels.indexOf(connection);
        if(index < 0)
            throw new IllegalArgumentException();
        plugin.dbmodels.remove(index);
        plugin.dbmodels.add(index, newConnection);
        plugin.saveParticipant.setChanged(RMBenchSaveParticipant.CHANGED_CONNECTIONS);
        getEventManager().fireDBModelsChanged(plugin, connection);
    }
    
    /**
     * @param dbmodel the connection to be removed
     */
    public static void removeDBModel(DBModel dbmodel) {
        plugin.dbmodels.remove(dbmodel);
        plugin.eventManager.fireDBModelRemoved(plugin, null);
        plugin.saveParticipant.setChanged(RMBenchSaveParticipant.CHANGED_CONNECTIONS);
    }
    
    /**
     * @param name the name of the connection to be removed
     */
    public static void removeDBModel(String name) {
        for (DBModel dbmodel : plugin.dbmodels) {
            if(dbmodel.getName().equals(name)) {
                removeDBModel(dbmodel);
                return;
            }
        }
    }
    
    /**
     * @return the extension manager
     */
    public static ExtensionManager getExtensionManager() {
        return plugin.extensionManager;
    }
    
    /**
     * @return the standard database info
     */
    public static IDatabaseInfo getStandardDatabaseInfo() {
        return getStandardDatabaseExtension().getDatabaseInfo();
    }
    
    /**
     * @return the standard database extension
     */
    public static DatabaseExtension getStandardDatabaseExtension() {
        return plugin.extensionManager.getDatabaseExtension("sql99");
    }

    /**
     * @param dbInfo a database info
     * @return all DDL generator extensions that support the given database
     */
    public static DDLGeneratorExtension[] getDDLGeneratorExtensions(IDatabaseInfo dbInfo) {
        DDLGeneratorExtension[] extensions = plugin.extensionManager.getDDLGeneratorExtensions();
        if(extensions == null)
            return new DDLGeneratorExtension[0];
        
        DatabaseExtension dbext = getExtensionManager().getDatabaseExtension(dbInfo);
        
        List<DDLGeneratorExtension> result = new ArrayList<DDLGeneratorExtension>(extensions.length);
        for (int i = 0; i < extensions.length; i++) {
            if(extensions[i].belongsToDatabase(dbext.getId()))
                result.add(0, extensions[i]);
            else if(extensions[i].supportsDatabase(dbext.getId()))
                result.add(extensions[i]);
        }
        return result.toArray(new DDLGeneratorExtension[result.size()]);
    }
    
    /**
     * @return the default name generator extension
     */
    public static NameGeneratorExtension getDefaultNameGeneratorExtension() {
        return plugin.extensionManager.getNameGeneratorExtension("default");
    }
    
    /**
     * @return the default name generator
     */
    public static INameGenerator getDefaultNameGenerator() {
        return getDefaultNameGeneratorExtension().getNameGenerator();
    }
    
    /**
     * @return the currently active model
     */
    public static Model getActiveModel() {
        return plugin.modelManager.getModel();
    }

    /**
     * @return a ModelExport for the currently active model
     */
    public IExportable.ModelExport getModelExport() {
    	final Model model = plugin.modelManager.getModel();
    	if(model != null) {
	    	return new IExportable.ModelExport() {
				public List<IModelElement> getModelElements() {
					return null;
				}
				public IModel getModel() {
					return model.getIModel();
				}
	    	};
    	}
    	else
    		return null;
    }
    
    /**
     * @return the model view
     */
    public static ModelView getModelView() {
        return (ModelView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelView.VIEW_ID);
    }
    
    /**
     * @return the global model manager
     */
    public static ModelManager getModelManager() {
        return plugin.modelManager;
    }
    
    /**
     * @return the license manager singleton
     */
    public static LicenseManager getLicenseManager() {
        return plugin.licenseManager;
    }
    

    /**
     * @return the PrintState object maintained by the plugin instance
     */
    public static PrintState getPrintState() {
        return plugin.printState;
    }
    
    /**
     * @return the version number of the <em>org.eclipse.ui</em> bundle
     */
    public static String getEclipseUIVersion() {
    	if(eclipseUIVersion == null)
    		eclipseUIVersion = (String)Platform.getBundle("org.eclipse.ui").getHeaders().get("Bundle-Version");
    	return eclipseUIVersion;
    }
    
    /**
     * @return true if this is an eclipse 3.1.x version
     */
    public static boolean isEclipse31() {
    	return getEclipseUIVersion().startsWith("3.1");
    }
    
    /**
     * convenience method - show the table details view in the currently displayed page
     * @return the view, or null if an error occurred
     */
    public static TableDetailsView showTableDetailsView() {
        try {
            TableDetailsView view = (TableDetailsView)RMBenchPlugin.getDefault().getWorkbench().
                getActiveWorkbenchWindow().getActivePage().showView(TableDetailsView.VIEW_ID);
            return view;
        } 
        catch (PartInitException e) {
            RMBenchPlugin.logError(e);
            return null;
        }
    }

    /**
     * open the external web browser on the rmbench home page
     * 
     * @param shell the parent shell in case of error dialogs
     */
    public static void externalBrowseHomepage(final Shell shell) {
        try {
            IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
            browser.openURL(RMBENCH_URL);
        }
        catch (PartInitException x) {
            RMBenchPlugin.logError("error opening web browser", x);
            MessageDialog.openError(shell, "error opening the web browser", x.getMessage());
        }
    }
}
