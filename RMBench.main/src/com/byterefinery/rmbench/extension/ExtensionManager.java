/*
 * created 13.05.2005
 * 
 * $Id: ExtensionManager.java 683 2008-03-06 22:38:03Z cse $
 */
package com.byterefinery.rmbench.extension;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import com.byterefinery.rmbench.RMBenchConstants;
import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.export.DDLFormatter;
import com.byterefinery.rmbench.export.DDLScript;
import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLGeneratorWizardFactory;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDataTypeEditorFactory;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IExternalJdbcProvider;
import com.byterefinery.rmbench.external.IImageExporter;
import com.byterefinery.rmbench.external.IMessageProvider;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.IModelExporter;
import com.byterefinery.rmbench.external.IModelExporterWizardFactory;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.DBMessageProvider;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.util.GenericURLValueParser;
import com.byterefinery.rmbench.util.URLValueParser;
import com.byterefinery.rmbench.util.database.DynamicURLSetupGroup;
import com.byterefinery.rmbench.util.database.SimpleURLSetupGroup;

/**
 * all-static helper class which maintains descriptors for the extensions supported 
 * by this plugin
 * 
 * @author cse
 * @version $Revision$
 * @TODO: more graceful error handling. Currently, excceptions are thrown and the plugin load process
 * is aborted
 */
public class ExtensionManager 
{
    private static final String EXT_NAME_GENERATOR = "nameGenerator";
    private static final String EXT_DDL_GENERATOR = "DDLGenerator";
    private static final String EXT_DRIVER_INFO = "jdbcDriverInfo";
    private static final String EXT_DATABASE_INFO = "databaseInfo";
    private static final String EXT_TABLE_THEMES = "tableThemes";
    private static final String EXT_TABLE_TYPES = "tableTypes";
    private static final String EXT_IMAGE_EXPORTER = "imageExporter";
    private static final String EXT_MODEL_EXPORTER = "modelExporter";
    private static final String EXT_EXTJDBC_PROVIDER = "externalJdbcProvider";
    
    private static final String ELEM_VARIABLE = "variable";
    private static final String ELEM_SETUPWIDGET = "setupWidget";
    private static final String ELEM_GENERICURL = "genericURL";
    private static final String ELEM_JDBCURL = "jdbcURL";
    private static final String ELEM_DATABASEMETADATA = "databaseMetaData";
    private static final String ELEM_CONNECTADAPTER = "connectAdapter";
    
    private static final String ATT_DEFAULT = "default";
    private static final String ATT_USE = "use";
    private static final String ATT_TYPE = "type";
    private static final String ATT_RESOURCE_NAME = "resourceName";
    private static final String ATT_FACTORY = "factory";
    private static final String ATT_DATABASE = "database";
    private static final String ATT_CLASSNAME = "className";
    private static final String ATT_NAME = "name";
    private static final String ATT_PROVIDER = "messageProvider";
    private static final String ATT_PATTERN = "pattern";
    private static final String ATT_ID = "id";
    private static final String ATT_LABEL = "label";
    private static final String ATT_TITLE_COLOR = "titleBackground";
    private static final String ATT_TITLE_FGCOLOR = "titleForeground";
    private static final String ATT_BODY_COLOR = "bodyBackground";
    private static final String ATT_BODY_FGCOLOR = "bodyForeground";
    private static final String ATT_THEME = "theme";
    private static final String ATT_DATABASES = "databases";
    private static final String ATT_EXCLUDE_DATABASES = "excludeDatabases";
    private static final String ATT_WIZARDFACTORY = "wizardFactory";
    private static final String ATT_FORMATTERNAME = "formatter";
    private static final String ATT_SCRIPTNAME = "script";
    private static final String ATT_DESCRIPTION = "description";
    private static final String ATT_DEFAULTHOST = "defaulthost";
    private static final String ATT_DEFAULTPORT = "defaultport";
    private static final String ATT_DEFAULTDB = "defaultdb";
    private static final String ATT_PROTOCOL = "protocol";
    private static final String ATT_QUERYUSERID = "queryUserId";
    private static final String ATT_QUERYPASSWORD = "queryPassword";
    
    
    private static final String VALUE_ALL = "*";
    
    private JdbcDriverExtension[] jdbcDriverExtensions;
    private DatabaseExtension[] databaseExtensions;
    private NameGeneratorExtension[] nameGeneratorExtensions;
    private DDLGeneratorExtension[] ddlGeneratorExtensions;
    private TableThemeExtension[] themeExtensions;
    private TableTypeExtension[] typeExtensions;
    private ImageExporterExtension[] imageExporterExtensions;
    private ModelExporterExtension[] modelExporterExtensions;
    private ExternalJdbcProviderExtension[] jdbcProviderExtensions;

    
    private void initializeDatabaseInfos() throws CoreException {
        
        databaseExtensions = new DatabaseExtension[0];
        
        IExtensionPoint databaseInfoXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_DATABASE_INFO);
        
        IExtension[] extensions = databaseInfoXP.getExtensions();
        List<DatabaseExtension> infoList = new ArrayList<DatabaseExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String className = elements[i2].getAttribute(ATT_CLASSNAME);
                String providerName = elements[i2].getAttribute(ATT_PROVIDER);
                
                try {
                    Class<?> databaseClass = bundle.loadClass(className);
                    IDatabaseInfo info = (IDatabaseInfo)databaseClass.newInstance();
                    IMessageProvider formatter = null;
                    if(providerName != null) {
                        Class<?> providerClass = bundle.loadClass(providerName);
                        formatter = (IMessageProvider)providerClass.newInstance();
                    }
                    else {
                    	formatter = new DBMessageProvider();
                    }
                    IConfigurationElement[] editorElements = elements[i2].getChildren();
                    Map<String, IDataTypeEditorFactory> editorMap = new HashMap<String, IDataTypeEditorFactory>(editorElements.length);
                    for (int i3 = 0; i3 < editorElements.length; i3++) {
                        
                        String typeName = editorElements[i3].getAttribute(ATT_TYPE);
                        className = editorElements[i3].getAttribute(ATT_FACTORY);
                        Class<?> editorFactoryClass = bundle.loadClass(className);
                        IDataTypeEditorFactory editorFactory = (IDataTypeEditorFactory)editorFactoryClass.newInstance();
                        editorMap.put(typeName, editorFactory);
                    }
                    infoList.add(new DatabaseExtension(namespace, id, name, info, editorMap, formatter));
                } 
                catch(Exception x) {
                    logError(
                            Messages.ExtensionManager_invalidDatabase, 
                            namespace, 
                            new Object[]{name}, x);
                }
            }            
        }
        Collections.sort(infoList, new NamedExtensionComparator());
        databaseExtensions = 
            (DatabaseExtension[])infoList.toArray(new DatabaseExtension[infoList.size()]);
    }

    private void initializeJdbcDriverInfos() throws CoreException {
        
        jdbcDriverExtensions = new JdbcDriverExtension[0];
        
        IExtensionPoint driverInfoXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_DRIVER_INFO);
        
        IExtension[] extensions = driverInfoXP.getExtensions();
        List<JdbcDriverExtension> infoList = new ArrayList<JdbcDriverExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String className = elements[i2].getAttribute(ATT_CLASSNAME);
                
                boolean queryUserId = false;
                boolean queryPassword = false;
                
                if ( (elements[i2].getAttribute(ATT_QUERYUSERID)==null) 
                        || (elements[i2].getAttribute(ATT_QUERYUSERID).equals("true")) ) {
                    queryUserId = true;
                } 
                if ( (elements[i2].getAttribute(ATT_QUERYUSERID)==null) 
                        || (elements[i2].getAttribute(ATT_QUERYPASSWORD).equals("true")) ) {
                    queryPassword = true;
                }
                
                String dbId = elements[i2].getAttribute(ATT_DATABASE);
                DatabaseExtension dbExt = RMBenchPlugin.getExtensionManager().getDatabaseExtension(dbId);
                if(dbExt == null) {
                    throw makeException(
                            Messages.ExtensionManager_invalidDatabase,
                            namespace,
                            new Object[]{dbExt.getId()}, null);
                }
                //determine the connect adapter
                IConfigurationElement[] connectAdapterElem = elements[i2].getChildren(ELEM_CONNECTADAPTER);
                IJdbcConnectAdapter.Factory connectAdapterFactory = null;
                if(connectAdapterElem.length > 0) {
                    try {
                        String factoryClassName = connectAdapterElem[0].getAttribute(ATT_FACTORY);
                        Class<?> factoryClass = bundle.loadClass(factoryClassName);
                        connectAdapterFactory = (IJdbcConnectAdapter.Factory)factoryClass.newInstance();
                    } catch (Exception e) {
                        throw makeException(
                                Messages.ExtensionManager_invalidDatabase,
                                namespace,
                                new Object[]{dbExt.getId()}, e);
                    }
                }
                else //if unspecified, use the simple JDBC wrapper
                    connectAdapterFactory = JdbcConnectAdapter.FACTORY;
                
                //determine the Metadata adpater
                IConfigurationElement[] databaseMetaData = elements[i2].getChildren(ELEM_DATABASEMETADATA);
                IMetaDataAccess.Factory dbMetaDataFactory = null;
                if(databaseMetaData.length > 0) {
                    try {
                        String factoryClassName = databaseMetaData[0].getAttribute(ATT_FACTORY);
                        Class<?> factoryClass = bundle.loadClass(factoryClassName);
                        dbMetaDataFactory = (IMetaDataAccess.Factory)factoryClass.newInstance();
                    } catch (Exception e) {
                        throw makeException(
                                Messages.ExtensionManager_invalidDatabase,
                                namespace,
                                new Object[]{dbExt.getId()}, e);
                    }
                }
                else //if unspecified, use the simple JDBC wrapper
                	dbMetaDataFactory = JdbcMetaDataAdapter.FACTORY;
                
                //generic URL
                IConfigurationElement[] setupURL = elements[i2].getChildren(ELEM_GENERICURL);
                if(setupURL.length > 0) {
                    VariableDescriptor[] vars = readVariableDescriptors(setupURL[0]);
                    
                    String pattern = setupURL[0].getAttribute(ATT_PATTERN);
                    GenericURLValueParser parser = new GenericURLValueParser(pattern, vars);
                    
                    if(parser.getVariableCount() > 0) {
                        infoList.add(new JdbcDriverExtension(
                                namespace,
                                id,
                                name, 
                                className,
                                DynamicURLSetupGroup.getFactory(parser, vars), 
                                dbExt,
                                connectAdapterFactory,
                                dbMetaDataFactory, 
                                queryUserId, queryPassword));
                    } else {
                        infoList.add(new JdbcDriverExtension(
                                namespace,
                                id,
                                name, 
                                className,
                                SimpleURLSetupGroup.getFactory(pattern), 
                                dbExt,
                                connectAdapterFactory,
                                dbMetaDataFactory,
                                queryUserId, queryPassword));
                    }
                }
                //OR JDBC Url
                IConfigurationElement[] jdbcURL = elements[i2].getChildren(ELEM_JDBCURL);
                if(jdbcURL.length > 0) {
                    VariableDescriptor[] vars = new VariableDescriptor[3];
                    
                    
                    String protocol = jdbcURL[0].getAttribute(ATT_PROTOCOL);
                    String pattern = "jdbc:"+protocol+"://${host}:${port}/${database}";
                    
                    vars[0] = new VariableDescriptor("host", 
                                        VariableDescriptor.TYPE_STRING,
                                        VariableDescriptor.USE_OPTIONAL,
                                        jdbcURL[0].getAttribute(ATT_DEFAULTHOST),
                                        "Host:");
                    
                    vars[1] = new VariableDescriptor("port", 
                            VariableDescriptor.TYPE_INT,
                            VariableDescriptor.USE_OPTIONAL,
                            jdbcURL[0].getAttribute(ATT_DEFAULTPORT),
                            "Port:");
                    
                    vars[2] = new VariableDescriptor("database", 
                            VariableDescriptor.TYPE_STRING,
                            VariableDescriptor.USE_REQUIRED,
                            jdbcURL[0].getAttribute(ATT_DEFAULTDB),
                            "Database:");
                    
                    
                    URLValueParser parser = new URLValueParser(pattern, vars);
                    
                    if(parser.getVariableCount() > 0) {
                        infoList.add(new JdbcDriverExtension(
                                namespace,
                                id,
                                name, 
                                className,
                                DynamicURLSetupGroup.getFactory(parser, vars), 
                                dbExt,
                                connectAdapterFactory,
                                dbMetaDataFactory,
                                queryUserId, queryPassword));
                    } else {
                        infoList.add(new JdbcDriverExtension(
                                namespace,
                                id,
                                name, 
                                className,
                                SimpleURLSetupGroup.getFactory(pattern), 
                                dbExt,
                                connectAdapterFactory,
                                dbMetaDataFactory,
                                queryUserId, queryPassword));
                    }
                }
                
                //URL Config UI
                IConfigurationElement[] setupControl = elements[i2].getChildren(ELEM_SETUPWIDGET);
                if(setupControl.length > 0) {
                    String factoryName = setupControl[0].getAttribute(ATT_FACTORY);
                    try {
                        Class<?> factoryClass = bundle.loadClass(factoryName);
                        IURLSetupGroup.Factory factory = 
                            (IURLSetupGroup.Factory)factoryClass.newInstance();
                        infoList.add(new JdbcDriverExtension(
                                namespace,
                                id,
                                name, 
                                className, 
                                factory, 
                                dbExt,
                                connectAdapterFactory,
                                dbMetaDataFactory,
                                queryUserId, queryPassword));
                    }
                    catch(Exception x) {
                        throw makeException(
                                Messages.ExtensionManager_invalidFactory,
                                namespace,
                                new Object[]{className}, x);
                    }
                }
            }
        }
        Collections.sort(infoList, new NamedExtensionComparator());
        jdbcDriverExtensions = 
            (JdbcDriverExtension[])infoList.toArray(new JdbcDriverExtension[infoList.size()]);
    }

    private void initializeThemesAndTypes() {
        initializeTableThemes();
        initializeTableTypes();
        finalizeTableThemes();
    }
    
    private void initializeTableTypes() {
        
        typeExtensions = new TableTypeExtension[0];
        
        IExtensionPoint tableTypesXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_TABLE_TYPES);
        
        IExtension[] extensions = tableTypesXP.getExtensions();
        List<TableTypeExtension> typesList = new ArrayList<TableTypeExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            String namespace = extensions[i1].getNamespaceIdentifier();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String label = elements[i2].getAttribute(ATT_LABEL);
                String themeId = elements[i2].getAttribute(ATT_THEME);
                
                TableThemeExtension themeExt = getTableThemeExtension(themeId);
                if(themeExt == null) {
                    //TODO V1: log error. Make error handling in ExtensionManager overall more robust
                    themeExt = new TableThemeExtension(namespace, themeId);
                }
                typesList.add(new TableTypeExtension(namespace, id, label, themeExt));
            }            
        }
        Collections.sort(typesList, new Comparator<TableTypeExtension>() {
            public int compare(TableTypeExtension o1, TableTypeExtension o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        typeExtensions =
            (TableTypeExtension[])typesList.toArray(new TableTypeExtension[typesList.size()]);
    }

    private void initializeTableThemes() {
        
        themeExtensions = new TableThemeExtension[0];
        
        IExtensionPoint tableThemesXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_TABLE_THEMES);
        
        IExtension[] extensions = tableThemesXP.getExtensions();
        List<TableThemeExtension> themesList = new ArrayList<TableThemeExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            String namespace = extensions[i1].getNamespaceIdentifier();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String label = elements[i2].getAttribute(ATT_LABEL);
                String titleColor = elements[i2].getAttribute(ATT_TITLE_COLOR);
                String titleFgColor = elements[i2].getAttribute(ATT_TITLE_FGCOLOR);
                String bodyColor = elements[i2].getAttribute(ATT_BODY_COLOR);
                String bodyFgColor = elements[i2].getAttribute(ATT_BODY_FGCOLOR);
                
                themesList.add(
                        new TableThemeExtension(
                                namespace,
                                id, label, titleColor, titleFgColor, bodyColor, bodyFgColor));
            }            
        }
        Collections.sort(themesList, new Comparator<TableThemeExtension>() {
            public int compare(TableThemeExtension o1, TableThemeExtension o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        themeExtensions =
            (TableThemeExtension[])themesList.toArray(new TableThemeExtension[themesList.size()]);
    }

    private void finalizeTableThemes() {
        Map<TableThemeExtension, List<TableTypeExtension>> themesMap = new HashMap<TableThemeExtension, List<TableTypeExtension>>();
        for (int i = 0; i < typeExtensions.length; i++) {
            TableThemeExtension theme = typeExtensions[i].themeExtension;
            List<TableTypeExtension> typesList = themesMap.get(theme);
            if(typesList == null) {
                typesList = new ArrayList<TableTypeExtension>();
                themesMap.put(theme, typesList);
            }
            typesList.add(typeExtensions[i]);
        }
        for (Iterator<TableThemeExtension> it = themesMap.keySet().iterator(); it.hasNext();) {
            TableThemeExtension theme = it.next();
            List<TableTypeExtension> types = themesMap.get(theme);
            theme.typeExtensions = types.toArray(new TableTypeExtension[types.size()]);
        }
    }

    private void initializeNameGenerators() throws CoreException {
        
        nameGeneratorExtensions = new NameGeneratorExtension[0];
        
        IExtensionPoint nameGeneratorXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_NAME_GENERATOR);
        
        IExtension[] extensions = nameGeneratorXP.getExtensions();
        List<NameGeneratorExtension> generatorList = new ArrayList<NameGeneratorExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String className = elements[i2].getAttribute(ATT_CLASSNAME);
                try {
                    Class<?> generatorClass = bundle.loadClass(className);
                    INameGenerator generator = (INameGenerator)generatorClass.newInstance();
                    generatorList.add(new NameGeneratorExtension(
                            namespace, id, name, generator));
                } 
                catch(Exception x) {
                    throw makeException(
                            Messages.ExtensionManager_invalidNameGenerator,
                            namespace,
                            new Object[]{className}, x);
                }
            }            
        }
        Collections.sort(generatorList, new NamedExtensionComparator());
        nameGeneratorExtensions = generatorList.toArray(
                new NameGeneratorExtension[generatorList.size()]);
    }
    
    private void initializeExternalJdbcProviders() throws CoreException {
        
    	jdbcProviderExtensions = new ExternalJdbcProviderExtension[0];
    	
        IExtensionPoint connectionProviderXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_EXTJDBC_PROVIDER);
        
        IExtension[] extensions = connectionProviderXP.getExtensions();
        List<ExternalJdbcProviderExtension> providerList = new ArrayList<ExternalJdbcProviderExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String className = elements[i2].getAttribute(ATT_CLASSNAME);
                try {
                    Class<?> adapterClass = bundle.loadClass(className);
                    IExternalJdbcProvider provider = (IExternalJdbcProvider)adapterClass.newInstance();
                    providerList.add(new ExternalJdbcProviderExtension(
                            namespace, id, name, provider));
                } 
                catch(Exception x) {
                    throw makeException(
                            Messages.ExtensionManager_invalidProviderExtension,
                            namespace,
                            new Object[]{className}, x);
                }
            }            
        }
        Collections.sort(providerList, new NamedExtensionComparator());
        jdbcProviderExtensions = providerList.toArray(
                new ExternalJdbcProviderExtension[providerList.size()]);
    }

    private void initializeDDLGenerators() throws CoreException {
        
        ddlGeneratorExtensions = new DDLGeneratorExtension[0];
        
        IExtensionPoint ddlGeneratorXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_DDL_GENERATOR);
        
        IExtension[] extensions = ddlGeneratorXP.getExtensions();
        List<DDLGeneratorExtension> generatorList = new ArrayList<DDLGeneratorExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String dbIds = elements[i2].getAttribute(ATT_DATABASES);
                String exclDbIds = elements[i2].getAttribute(ATT_EXCLUDE_DATABASES);
                String factoryName = elements[i2].getAttribute(ATT_FACTORY);
                String wizardName = elements[i2].getAttribute(ATT_WIZARDFACTORY);
                String formatterName = elements[i2].getAttribute(ATT_FORMATTERNAME);
                String scriptName = elements[i2].getAttribute(ATT_SCRIPTNAME);
                
                //read the referenced database infos
                DatabaseExtension[] supportedDbs;
                DatabaseExtension nativeDb = null;
                
                if(VALUE_ALL.equals(dbIds)) {
                    supportedDbs = getDatabaseExtensions();
                    if (exclDbIds!=null) {
	                    List<DatabaseExtension> tmpExtList = new ArrayList<DatabaseExtension>(supportedDbs.length);
	                    //filling list with all databases
	                    for (int i=0; i<supportedDbs.length; i++) {
	                    	tmpExtList.add(supportedDbs[i]);
	                    }
	                    String dbs[] = exclDbIds.split(",");
	                    for (int i=0; i<dbs.length; i++) {
	                    	DatabaseExtension dbExt = getDatabaseExtension(dbs[i].trim());
	                    	tmpExtList.remove(dbExt);
	                    }
	                    supportedDbs = tmpExtList.toArray(new DatabaseExtension[tmpExtList.size()]);
                    }
                    
                }
                else {
                    StringTokenizer tokenizer = new StringTokenizer(dbIds, ",");
                    List<DatabaseExtension> infoList = new ArrayList<DatabaseExtension>(tokenizer.countTokens());
                    while(tokenizer.hasMoreTokens()) {
                        String dbName = tokenizer.nextToken().trim();
                        DatabaseExtension dbext = getDatabaseExtension(dbName);
                        if(dbext == null) {
                            throw makeException(
                                    Messages.ExtensionManager_invalidDBInfoReference,
                                    namespace,
                                    new Object[]{dbName}, null);
                        }
                        else {
                            infoList.add(dbext);
                        }
                    }
                    supportedDbs = infoList.toArray(new DatabaseExtension[infoList.size()]);
                    nativeDb = supportedDbs[0];
                }
                //instantiate the wizard page creator
                IDDLGeneratorWizardFactory wizardCreator = null;
                if(wizardName != null) {
                    try {
                        Class<?> wizardClass = bundle.loadClass(wizardName);
                        wizardCreator = (IDDLGeneratorWizardFactory)wizardClass.newInstance();
                    } 
                    catch(Exception x) {
                        throw makeException(
                                Messages.ExtensionManager_invalidDDLWizardCreator,
                                namespace,
                                new Object[]{wizardName}, x);
                    }
                }
                //instantiate the DDL formatter factory
                IDDLFormatter.Factory formatterFactory;
                if(formatterName == null || formatterName.equals(DDLFormatter.Factory.class.getName())) {
                    formatterFactory = new DDLFormatter.Factory();
                }
                else {
                    try {
                        Class<?> formatterClass = bundle.loadClass(formatterName);
                        formatterFactory = (IDDLFormatter.Factory)formatterClass.newInstance();
                    } 
                    catch(Exception x) {
                        throw makeException(
                                Messages.ExtensionManager_invalidDDLFormatter,
                                namespace,
                                new Object[]{formatterName}, x);
                    }
                }
                //instantiate the DDL script factory
                IDDLScript.Factory scriptFactory;
                if(scriptName == null || scriptName.equals(DDLScript.Factory.class.getName())) {
                    scriptFactory = new DDLScript.Factory();
                }
                else {
                    try {
                        Class<?> scriptClass = bundle.loadClass(scriptName);
                        scriptFactory = (IDDLScript.Factory)scriptClass.newInstance();
                    } 
                    catch(Exception x) {
                        throw makeException(
                                Messages.ExtensionManager_invalidDDLScript,
                                namespace,
                                new Object[]{scriptName}, x);
                    }
                }
                //instantiate the implementation class
                try {
                    Class<?> generatorClass = bundle.loadClass(factoryName);
                    IDDLGenerator.Factory generatorFactory = 
                        (IDDLGenerator.Factory)generatorClass.newInstance();
                    generatorList.add(
                            new DDLGeneratorExtension(
                                    namespace,
                                    id, 
                                    name, 
                                    generatorFactory, 
                                    wizardCreator, 
                                    formatterFactory, 
                                    scriptFactory,
                                    nativeDb,
                                    supportedDbs));
                } 
                catch(Exception x) {
                    throw makeException(
                            Messages.ExtensionManager_invalidDDLGenerator,
                            namespace,
                            new Object[]{factoryName}, x);
                }
            }            
        }
        Collections.sort(generatorList, new NamedExtensionComparator());
        ddlGeneratorExtensions = generatorList.toArray(new DDLGeneratorExtension[generatorList.size()]);
    }
    
    private void initializeImageExporters() throws CoreException {
        
        imageExporterExtensions = new ImageExporterExtension[0];
        
        IExtensionPoint imageExporterXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_IMAGE_EXPORTER);
        
        IExtension[] extensions = imageExporterXP.getExtensions();
        List<ImageExporterExtension> exporterList = new ArrayList<ImageExporterExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String description = elements[i2].getAttribute(ATT_DESCRIPTION);
                String className = elements[i2].getAttribute(ATT_CLASSNAME);
                try {
                    Class<?> exporterClass = bundle.loadClass(className);
                    IImageExporter exporter = (IImageExporter)exporterClass.newInstance();
                    exporterList.add(new ImageExporterExtension(
                            namespace, id, name, description, exporter));
                } 
                catch(Exception x) {
                    throw makeException(
                            Messages.ExtensionManager_invalidImageExporter,
                            namespace,
                            new Object[]{className}, x);
                }
            }            
        }
        Collections.sort(exporterList, new NamedExtensionComparator());
        imageExporterExtensions = (ImageExporterExtension[])exporterList.toArray(
                new ImageExporterExtension[exporterList.size()]);
    }
    
    private void initializeModelExporters() throws CoreException {
        
        modelExporterExtensions = new ModelExporterExtension[0];
        
        IExtensionPoint imageExporterXP = Platform.getExtensionRegistry().getExtensionPoint(
                RMBenchConstants.PLUGIN_ID, EXT_MODEL_EXPORTER);
        
        IExtension[] extensions = imageExporterXP.getExtensions();
        List<ModelExporterExtension> exporterList = new ArrayList<ModelExporterExtension>();
        
        for(int i1 = 0; i1 < extensions.length; i1++) {
            String namespace = extensions[i1].getNamespaceIdentifier();
            Bundle bundle = Platform.getBundle(namespace); 
            IConfigurationElement[] elements = extensions[i1].getConfigurationElements();
            
            for(int i2=0; i2<elements.length; i2++) {
                String id = elements[i2].getAttribute(ATT_ID);
                String name = elements[i2].getAttribute(ATT_NAME);
                String description = elements[i2].getAttribute(ATT_DESCRIPTION);
                String className = elements[i2].getAttribute(ATT_FACTORY);
                String wizardClassName = elements[i2].getAttribute(ATT_WIZARDFACTORY);
                try {
                    Class<?> exporterClass = bundle.loadClass(className);
                    IModelExporter.Factory exporterFactory = (IModelExporter.Factory)exporterClass.newInstance();
                    IModelExporterWizardFactory wizardFactory = null;
                    if(wizardClassName != null) {
                        Class<?> wizardFactoryClass = bundle.loadClass(wizardClassName);
                        wizardFactory = (IModelExporterWizardFactory)wizardFactoryClass.newInstance();
                    }
                    exporterList.add(new ModelExporterExtension(
                            namespace, id, name, description, exporterFactory, wizardFactory));
                } 
                catch(Exception x) {
                    throw makeException(
                            Messages.ExtensionManager_invalidModelExporter,
                            namespace,
                            new Object[]{className}, x);
                }
            }            
        }
        Collections.sort(exporterList, new NamedExtensionComparator());
        modelExporterExtensions = (ModelExporterExtension[])exporterList.toArray(
                new ModelExporterExtension[exporterList.size()]);
    }
    
    private VariableDescriptor[] readVariableDescriptors(IConfigurationElement element) {
        Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
        ResourceBundle resources = Platform.getResourceBundle(bundle);
        
        IConfigurationElement[]  vars = element.getChildren(ELEM_VARIABLE);
        VariableDescriptor[] variables = new VariableDescriptor[vars.length];
        for (int i = 0; i < vars.length; i++) {
            String varName = vars[i].getAttribute(ATT_NAME);
            String resName = vars[i].getAttribute(ATT_RESOURCE_NAME);
            String type = vars[i].getAttribute(ATT_TYPE);
            String use = vars[i].getAttribute(ATT_USE);
            String defaultValue = vars[i].getAttribute(ATT_DEFAULT);
            
            String label = null;
            if(resources != null) {
                if(resName != null)
                    label = resources.getString(resName);
                if(label == null)
                    label = resources.getString(varName);
            }
            if(label == null)
                label = resName;
            
            variables[i] = new VariableDescriptor(varName, type, use, defaultValue, label);
        }
        return variables;
    }

    private static void logError(
            String namespace, String message, Object[] arguments, Throwable exception) {
        
        String msg = MessageFormat.format(message, arguments);
        RMBenchPlugin.getDefault().getLog().log(
                new Status(IStatus.ERROR, namespace, -1, msg, exception));
    }
    
    private static CoreException makeException(
            String message, String namespace, Object[] arguments, Throwable exception) {
        
        return new CoreException(
            new Status(
                IStatus.ERROR,
                namespace, -1,
                MessageFormat.format(message, arguments),
                exception));
    }

    /**
     * @return the currently defined database info extensions
     */
    public DatabaseExtension[] getDatabaseExtensions() {
        if (databaseExtensions==null) {
            try {
                initializeDatabaseInfos();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);   
            }
        }
        return databaseExtensions;
    }

    /**
     * @param namespace the amespace to search
     * @param name the ID of the database extension
     * @return the database info, or <code>null</code>
     */
    public JdbcDriverExtension getJdbcDriverExtension(String namespace, String id) {
        for (JdbcDriverExtension jdbc : getJdbcDriverExtensions()) {
            if(jdbc.getNamespace().equals(namespace) && jdbc.getId().equals(id))
                return jdbc;
        }
        return null;
    }
    
    /**
     * @return the currently defined driver info extensions
     */
    public JdbcDriverExtension[] getJdbcDriverExtensions() {
        if (jdbcDriverExtensions==null)
            try {
                initializeJdbcDriverInfos();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
            }
        return jdbcDriverExtensions;
    }

    /**
     * @return the currently defined name generator extensions
     */
    public NameGeneratorExtension[] getNameGeneratorExtensions() {
        if (nameGeneratorExtensions==null) {
            try {
                initializeNameGenerators();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return nameGeneratorExtensions;
    }

    /**
     * @return the currently defined DDL generator extensions
     */
    public DDLGeneratorExtension[] getDDLGeneratorExtensions() {
        if (ddlGeneratorExtensions==null) {
            try {
                initializeDDLGenerators();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return ddlGeneratorExtensions;
    }

    /**
     * @return the currently defined Image exporter extensions
     */
    public ImageExporterExtension[] getImageExporterExtensions() {
        if (imageExporterExtensions == null) {
            try {
                initializeImageExporters();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return imageExporterExtensions;
    }

    /**
     * @return the currently defined Model exporter extensions
     */
	public ModelExporterExtension[] getModelExporterExtensions() {
        if (modelExporterExtensions == null) {
            try {
                initializeModelExporters();
            }
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
            }
        }
        
        return modelExporterExtensions;
	}
	
    /**
     * @return the currently defined table theme extensions
     */
    public TableThemeExtension[] getTableThemeExtensions() {
        if (themeExtensions==null) {
            initializeThemesAndTypes();
        }
        
        return themeExtensions;
    }

    /**
     * @return the currently defined table type extensions
     */
    public TableTypeExtension[] getTableTypeExtensions() {
        if (typeExtensions==null) {
            initializeThemesAndTypes();
        }
        
        return typeExtensions;
    }

    /**
     * @return the currently defined table type extensions
     */
    public ExternalJdbcProviderExtension[] getExternalJdbcProviderExtensions() {
        if (jdbcProviderExtensions == null) {
            try {
				initializeExternalJdbcProviders();
			} 
            catch (CoreException e) {
                RMBenchPlugin.logError(e);
			}
        }
        return jdbcProviderExtensions;
    }

    /**
     * @param name the unique ID of the driver extension
     * @return the driver info, or <code>null</code>
     */
    public JdbcDriverExtension getJdbcDriverExtension(String id) {
        return getJdbcDriverExtension(RMBenchConstants.PLUGIN_ID, id);
    }

    /**
     * find an extension from the local plugin namespace
     * 
     * @param name the ID of the database extension
     * @return the database info, or <code>null</code>
     */
    public DatabaseExtension getDatabaseExtension(String id) {
        return getDatabaseExtension(RMBenchConstants.PLUGIN_ID, id);
    }
    
    /**
     * @param namespace the amespace to search
     * @param name the ID of the database extension
     * @return the database info, or <code>null</code>
     */
    public DatabaseExtension getDatabaseExtension(String namespace, String id) {
        for (DatabaseExtension dbExtension : getDatabaseExtensions()) {
            if(dbExtension.getNamespace().equals(namespace) && dbExtension.getId().equals(id))
                return dbExtension;
        }
        return null;
    }
    
    /**
     * @param databaseInfo
     * @return the extension object that declares the given database info
     * @throws IllegalArgumentException if no such extension
     */
    public DatabaseExtension getDatabaseExtension(IDatabaseInfo databaseInfo) {
        for (DatabaseExtension db : getDatabaseExtensions()) {
            if(db.getDatabaseInfo() == databaseInfo)
                return db;
        }
        throw new IllegalArgumentException();
    }
    
    /**
     * @param id the unique ID of the name generator
     * @return the name generator, or <code>null</code>
     */
    public NameGeneratorExtension getNameGeneratorExtension(String id) {
        for (NameGeneratorExtension ext : getNameGeneratorExtensions()) {
            if(ext.getId().equals(id)) {
                return ext;
            }
        }
        return null;
    }

    /**
     * @param id the unique ID of the DDL generator
     * @return the DDL generator, or <code>null</code>
     */
    public DDLGeneratorExtension getDDLGeneratorExtension(String namespace, String id) {
        
        for (DDLGeneratorExtension ddl : getDDLGeneratorExtensions()) {
            if(ddl.getNamespace().equals(namespace) && ddl.getId().equals(id)) {
                return ddl;
            }
        }
        return null;
    }
    
    /**
     * @param id the unique ID of the DDL generator
     * @return the DDL generator, or <code>null</code>
     */
    public DDLGeneratorExtension getDDLGeneratorExtension(String id) {
        return getDDLGeneratorExtension(RMBenchConstants.PLUGIN_ID, id);
    }

    /**
     * @param id the unique ID of the theme extension
     * @return the theme extension, or <code>null</code>
     */
    public TableThemeExtension getTableThemeExtension(String themeId) {
        for (TableThemeExtension ext : getTableThemeExtensions()) {
            if(ext.getId().equals(themeId))
                return ext;
        }
        return null;
    }

    /**
     * @param typeId a type extension id
     * @return the type extension, or <code>null</code>
     */
    public TableTypeExtension getTableTypeExtension(String typeId) {
        return getTableTypeExtension(RMBenchConstants.PLUGIN_ID, typeId);
    }
    
    /**
     * @param typeId a type extension id
     * @return the type extension, or <code>null</code>
     */
    public TableTypeExtension getTableTypeExtension(String namespace, String typeId) {
        for (TableTypeExtension ext : getTableTypeExtensions()) {
            if(ext.getNamespace().equals(namespace) && ext.getId().equals(typeId))
                return ext;
        }
        return null;
    }
    
    /**
     * @param nameGenerator 
     * @return the extension object that declares the given generator
     * @throws IllegalArgumentException if no such extension
     */
    public NameGeneratorExtension getNameGeneratorExtension(INameGenerator nameGenerator) {
        for (NameGeneratorExtension ext : getNameGeneratorExtensions()) {
            if(ext.getNameGenerator() == nameGenerator)
                return ext;
        }
        throw new IllegalArgumentException();
    }
    
    private class NamedExtensionComparator implements Comparator<NamedExtension> {
        public int compare(NamedExtension o1, NamedExtension o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

	/**
	 * @return an iterator over the connection providers which are loaded from 
	 * extension plugins
	 */
	public Iterable<IExternalJdbcProvider> externalJdbcProviders() {
		return new ExternalJdbcProviders();
	}
	
	private final class ExternalJdbcProviders 
	implements Iterable<IExternalJdbcProvider>, Iterator<IExternalJdbcProvider> {

        private int index = 0;
        private final ExternalJdbcProviderExtension[] extensions = 
            getExternalJdbcProviderExtensions();
        
        public Iterator<IExternalJdbcProvider> iterator() {
            return this;
        }

        public boolean hasNext() {
            return index < extensions.length;
        }

        public IExternalJdbcProvider next() {
            return extensions[index++].getProvider();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
	}
}
