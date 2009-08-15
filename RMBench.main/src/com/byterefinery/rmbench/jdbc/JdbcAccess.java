/*
 * created 29.04.2005
 * 
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: JdbcAccess.java 683 2008-03-06 22:38:03Z cse $
 */
package com.byterefinery.rmbench.jdbc;

import java.sql.Driver;
import java.util.Properties;

import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IMetaDataAccess.Factory;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;

/**
 * establishes a connection to a database via a RMBench-defined JDBC connection configuration
 * and imports schema information
 * 
 * @author cse
 */
public class JdbcAccess extends AbstractJdbcAccess {

    /*
     * an executor for test purposes, which prints the statements to System.out
     */
    private static final Executor nullExecutor = new Executor() {

        public void executeDDL(String ddl) throws SystemException {
            System.out.println(ddl);
            try {
                Thread.sleep(600);
            }
            catch (InterruptedException e) {
            }
        }
        public boolean isConnected() {
            return true;
        }
        public void connect(String password) throws SystemException {
        }
        public void close() throws SystemException {
        }
        public void addListener(Listener listener) {
        }
        public void removeListener(Listener listener) {
        }
    };

    /*
     * an executor which will not load the driver jars until actual connection 
     * establishment 
     */
    private final class LazyExecutor extends AbstractExecutor {
        
        protected IJdbcConnectAdapter getConnectAdapter() throws SystemException {
            return createConnectAdapter(password);
        }
    }
    
    private final String url;
    private final String user;
	private final IDriverInfo driverInfo;
    private final String[] jarFileNames;
    
    private String password;
    
    public JdbcAccess(
            String userid, 
            String password, 
            String url, 
            IDriverInfo driverInfo, 
            String[] fileNames,
            boolean loadIndexes,
            boolean loadKeyIndexes,
            boolean loadComments) {

        super(loadIndexes, loadKeyIndexes, loadComments);
        this.user = userid;
        this.password = password;
        this.url = url;
        this.driverInfo = driverInfo;
        this.jarFileNames = fileNames;
    }

    public IDriverInfo getDriverInfo() {
        return driverInfo;
    }

    public IDatabaseInfo getDatabaseInfo() {
        return driverInfo.getDatabaseInfo();
    }
    
    public String[] getJarFileNames() {
        return jarFileNames;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public boolean getLoadIndexes() {
		return loadIndexes;
	}

	public boolean getLoadKeyIndexes() {
		return loadKeyIndexes;
	}

	public boolean getLoadComments() {
		return loadComments;
	}
	
	/**
     * @return the executor. Note that if the system property "dumpDDL" is set, SQL statements 
     * will be written to System.out instead of executed
	 * @throws SystemException 
     */
	public Executor getExecutor() {
        String dumpDDL = System.getProperty("dumpDDL");
		return "y".equalsIgnoreCase(dumpDDL) ? nullExecutor : new LazyExecutor();
	}
	
    private Properties createConnectProperties() {
        
        Properties properties = new Properties();
        if (user!=null)
            properties.setProperty("user", user);
        
        String passwd = password != null ? password : this.password;
        if (passwd!=null)
            properties.setProperty("password", passwd);
        
        return properties;
    }

    private IJdbcConnectAdapter createConnectAdapter(String password) throws SystemException {
        try {
            Driver driver = DriverUtil.loadDriver(jarFileNames, driverInfo.getClassName());
            return driverInfo.getConnectAdapterFactory().create(driver, url, createConnectProperties());
        }
        catch (Exception e) {
            throw new SystemException(Messages.JDBCInvalidDriver, e);
        }
    }

    protected IJdbcConnectAdapter getConnectAdapter() throws SystemException {
        return createConnectAdapter(password);
    }

    protected Factory getMetaDataFactory() {
        return driverInfo.getMetaDataFactory();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConfigurable() {
        return true;
    }
}
