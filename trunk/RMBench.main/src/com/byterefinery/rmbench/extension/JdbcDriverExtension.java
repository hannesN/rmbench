/*
 * created 14.05.2005
 * 
 * $Id: JdbcDriverExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.jdbc.IDriverInfo;

/**
 * a description of a JDBC driver with classname, edit control and 
 * associated native database
 * 
 * @author cse
 */
public class JdbcDriverExtension extends NamedExtension implements IDriverInfo {

    private final String className;
    private final IURLSetupGroup.Factory factory;
    private final DatabaseExtension databaseExtension;
    private final IJdbcConnectAdapter.Factory connectAdapterFactory;
    private final IMetaDataAccess.Factory databaseMetaDataFactory;
    
    private final boolean userIdNeeded;
    private final boolean passwordNeeded;
    
    public JdbcDriverExtension(
            String namespace,
            String id,
            String name, 
            String className,
            IURLSetupGroup.Factory factory,
            DatabaseExtension databaseExtension,
            IJdbcConnectAdapter.Factory connectAdapterFactory,
            IMetaDataAccess.Factory databaseMetaDataFactory,
            boolean userIdNeeded,
            boolean passwordNeeded) {
        
        super(namespace, id, name);
        this.className = className;
        this.factory = factory;
        this.databaseExtension = databaseExtension;
        this.connectAdapterFactory = connectAdapterFactory;
        this.databaseMetaDataFactory = databaseMetaDataFactory;
        this.userIdNeeded = userIdNeeded;
        this.passwordNeeded = passwordNeeded;
    }

    /**
     * @return the driver class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the factory for createing the edit control
     */
    public IURLSetupGroup.Factory getSetupGroupFactory() {
        return factory;
    }
    
    /**
     * @return the native database
     */
    public IDatabaseInfo getDatabaseInfo() {
        return databaseExtension.getDatabaseInfo();
    }

    public IJdbcConnectAdapter.Factory getConnectAdapterFactory() {
        return connectAdapterFactory;
    }
    
	public IMetaDataAccess.Factory getMetaDataFactory() {
		return databaseMetaDataFactory;
	}
    
    public boolean isPasswordNeeded() {
        return passwordNeeded;
    }

    public boolean isUserIdNeeded() {
        return userIdNeeded;
    }
}
