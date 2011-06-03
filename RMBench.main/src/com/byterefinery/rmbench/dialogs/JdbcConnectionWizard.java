/*
 * created 08.05.2005
 * 
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: JdbcConnectionWizard.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.jdbc.DriverUtil;
import com.byterefinery.rmbench.jdbc.IDriverInfo;
import com.byterefinery.rmbench.jdbc.JdbcAccess;
import com.byterefinery.rmbench.jdbc.MutableDriverInfo;
import com.byterefinery.rmbench.model.dbimport.DBModel;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * a wizard for creating a new JDBC connection with all required parameters 
 * 
 * @author cse
 */
public class JdbcConnectionWizard extends Wizard implements IURLSetupGroup.Context {

    private String connectionName;
    private IDriverInfo driverInfo;
    private Collection<String> driverFiles;
    private String userid;
    private String password;
    private String connectionUrl;
    private DBModel.SchemaRule schemaRule = DBModel.ALL_SCHEMAS;
    private boolean doPrompt;
    private boolean doIndexes = true, doKeyIndexes = false, doComments = true;
    
    private String[] savedSelectedSchemas;
    private DBModel dbmodel;
    
    
    /**
     * @see #JdbcConnectionWizard(DBModel)
     */
    public JdbcConnectionWizard() {
        this(null);
    }
    
    /**
     * @param dbModel a connection to edit, or <code>null</code> if a new 
     * connection should be created  
     */
    public JdbcConnectionWizard(DBModel dbModel) {
    	setWindowTitle(getPageTitle());
        setDefaultPageImageDescriptor(
                RMBenchPlugin.getImageDescriptor(ImageConstants.CONNECTION_WIZARD));
        
        initializeDBModel(dbModel);
    }
    
    private void initializeDBModel(DBModel dbModel) {
        this.dbmodel = dbModel;
        if(dbModel != null) {
	        this.connectionName = dbModel.getName();
	        
	        JdbcAccess dbAccess = (JdbcAccess)dbModel.getDBAccess();
	        this.driverInfo = dbAccess.getDriverInfo();
	        this.driverFiles = new ArrayList<String>(Arrays.asList(dbAccess.getJarFileNames()));
	        this.userid = dbAccess.getUser();
	        this.password = dbAccess.getPassword();
	        this.connectionUrl = dbAccess.getUrl();
	        this.schemaRule = dbModel.getSchemaRule();
	        this.doPrompt = dbModel.isDoPrompt();
	        this.doIndexes = dbAccess.getLoadIndexes();
	        this.doKeyIndexes = dbAccess.getLoadKeyIndexes();
	        this.doComments = dbAccess.getLoadComments();
        }
        else {
        	driverFiles = new ArrayList<String>();
        }
    }

    public boolean performFinish() {
        
        String[] fileNames = (String[])driverFiles.toArray(new String[driverFiles.size()]);
        
        JdbcAccess access;
        access = new JdbcAccess(
        		userid, 
        		password, 
        		connectionUrl, 
        		driverInfo, 
        		fileNames, 
        		doIndexes, 
        		doIndexes && doKeyIndexes,
        		doComments);
        
        if(dbmodel != null) {
            dbmodel = dbmodel.clone(connectionName, schemaRule, doPrompt, access);
        } else {
            dbmodel = new DBModel(connectionName, schemaRule, doPrompt, access);
        }
        return true;
    }
    
    /**
     * @return a DBModel built from the input data. <code>null</code> if the finish 
     * button was not pressed
     */
    public DBModel getDBModel() {
        return dbmodel;
    }
        
    public IJdbcConnectAdapter getConnectionAdapter(Properties properties, String url) 
    throws SystemException {
        Driver driver = DriverUtil.loadDriver(getDriverFiles(), driverInfo.getClassName());
        return driverInfo.getConnectAdapterFactory().create(driver, url, properties);
    }
    
    
    public IJdbcConnectAdapter getConnectionAdapter(String password) throws SystemException {
        
        Properties properties = new Properties();
        if (userid!=null)
            properties.setProperty("user", userid);
        if(password!=null)
            properties.setProperty("password", password);
        
        return getConnectionAdapter(properties, connectionUrl);
    }
    
    public void addPages() {
        addPage(new JdbcConnectionWizardPage1(this));
        addPage(new JdbcConnectionWizardPage2(this));
        addPage(new JdbcConnectionWizardPage3(this));
        addPage(new JdbcConnectionWizardPage4(this));
    }

    public void setConnectionName(String name) {
        connectionName = (name != null && name.length() > 0) ? name : null;
    }
    
    public String getConnectionName() {
        return connectionName;
    }

    public IDriverInfo getDriverInfo() {
        return driverInfo;
    }
    
    public String[] getDriverFiles() {
        return (String[])driverFiles.toArray(new String[driverFiles.size()]);
    }
    
    public DatabaseExtension getDatabaseExtension() {
        return driverInfo != null ? 
                RMBenchPlugin.getExtensionManager().getDatabaseExtension(driverInfo.getDatabaseInfo()) : null;
    }

    public void setDatabaseExtension(DatabaseExtension dbExt) {
        if(driverInfo instanceof MutableDriverInfo) {
            ((MutableDriverInfo)driverInfo).setDatabaseExtension(dbExt);
        }
    }

    public String getDriverName() {
        return driverInfo != null ? driverInfo.getClassName() : null;
    }

    public void setUserId(String userid) {
        this.userid = userid != null && userid.length() > 0 ? userid : null;
    }

    public void setPassword(String password) {
        this.password = password != null && password.length() > 0 ? password : null;
    }

    public void setPrompt(boolean prompt) {
        this.doPrompt = prompt;
        this.password = null;
    }

    public void setConnectionUrl(String url) {
        this.connectionUrl = url != null && url.length() > 0 ? url : null;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }

    public boolean getPrompt()  {
        return doPrompt;
    }
    
    public boolean getDoIndexes() {
		return doIndexes;
	}

	public void setDoIndexes(boolean doIndexes) {
		this.doIndexes = doIndexes;
	}

	public boolean getDoKeyIndexes() {
		return doKeyIndexes;
	}

	public void setDoKeyIndexes(boolean doKeyIndexes) {
		this.doKeyIndexes = doKeyIndexes;
	}

	public boolean getDoComments() {
		return doComments;
	}

	public void setDoComments(boolean doComments) {
		this.doComments = doComments;
	}

	public void removeDriverFile(String filename) {
        driverFiles.remove(filename); 
    }

    public void addDriverFile(String filename) {
        driverFiles.add(filename);
    }
    
    /**
     * @param rule the schema rule that specifies which schemas should be imported
     */
    public void setSchemaRule(DBModel.SchemaRule newRule) {
        if(schemaRule instanceof DBModel.SelectedSchemasRule) {
            savedSelectedSchemas = ((DBModel.SelectedSchemasRule)schemaRule).getSchemaNames();
        }
        else if(newRule instanceof DBModel.SelectedSchemasRule) {
            ((DBModel.SelectedSchemasRule)newRule).setSelectedSchemas(savedSelectedSchemas);
        }
        schemaRule = newRule;
    }

    /**
     * @return the schema rule that specifies which schemas should be imported
     */
    public DBModel.SchemaRule getSchemaRule() {
        return schemaRule;
    }
    
    public void setDriverName(String className) {
        IDriverInfo[] allInfos = RMBenchPlugin.getExtensionManager().getJdbcDriverExtensions();
        
        for(int i=0; i<allInfos.length; i++) {
            if(allInfos[i].getClassName().equals(className)) {
                driverInfo = allInfos[i];
                break;
            }
        }
        if(driverInfo == null) {
            driverInfo = new MutableDriverInfo(className);
        }
    }

    public IURLSetupGroup createSetupGroup() {
        return driverInfo.getSetupGroupFactory().createSetupGroup(connectionUrl, this);
    }

    /**
     * @return the currently selected schemas for this connection
     * @throws IllegalStateException if not in selected schemas mode
     */
    public String[] getSelectedSchemas() {
        return getSelectedSchemasRule().getSchemaNames();
    }

    /**
     * @param schemas the selected schemas to be imported
     * @throws IllegalStateException if not in selected schemas mode
     */
    public void setSelectedSchemas(String[] schemas) {
        getSelectedSchemasRule().setSelectedSchemas(schemas);
    }
    
    /**
     * @param add more schemas to be imported
     * @throws IllegalStateException if not in selected schemas mode
     */
    public void addSelectedSchemas(String[] schemaNames) {
        DBModel.SelectedSchemasRule rule =getSelectedSchemasRule();
        for (int i = 0; i < schemaNames.length; i++) {
            rule.addSchema(schemaNames[i]);
        }
    }

    /**
     * @param remove an importable schema
     * @throws IllegalStateException if not in selected schemas mode
     */
    public void removeSelectedSchema(String schemaName) {
        getSelectedSchemasRule().removeSchema(schemaName);
    }
    
    /**
     * @return the default title for subordinate pages
     */
	public String getPageTitle() {
        return getDBModel() != null ?
        		Messages.JdbcConnectionWizardPage_edit_title : Messages.JdbcConnectionWizardPage_new_title;
	}
	
    private DBModel.SelectedSchemasRule getSelectedSchemasRule() {
        if(schemaRule instanceof DBModel.SelectedSchemasRule)
            return (DBModel.SelectedSchemasRule)schemaRule;
        else
            throw new IllegalStateException(schemaRule.getClass().getName());
    }
}
