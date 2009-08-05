/*
 * created 14.05.2005
 * 
 * $Id: MutableDriverInfo.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.jdbc;

import com.byterefinery.rmbench.RMBenchMessages;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.IMetaDataAccess.Factory;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcConnectAdapter;
import com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter;
import com.byterefinery.rmbench.util.database.SimpleURLSetupGroup;

/**
 * a generic, editable driver info which is used in cases where no predefined
 * driver info applies
 * 
 * @author cse
 */
public class MutableDriverInfo implements IDriverInfo {

    private String className;
    private DatabaseExtension databaseExtension;
    
    private final boolean userIdNeeded;
    private final boolean passwordNeeded;
    
    public MutableDriverInfo() {
        userIdNeeded = true;
        passwordNeeded = true;
    }
    
    public MutableDriverInfo(String className, DatabaseExtension database) {
        this.className = className;
        this.databaseExtension = database;
        userIdNeeded = true;
        passwordNeeded = true;
    }
    
    public MutableDriverInfo(String className, DatabaseExtension database,
            boolean userIdNeeded, boolean passwordNeeded) {
        this.className = className;
        this.databaseExtension = database;
        this.userIdNeeded = userIdNeeded;
        this.passwordNeeded = passwordNeeded;
    }
    
    public String getName() {
        return RMBenchMessages.DriverInfo_generic_Jdbc;
    }

    public String getClassName() {
        return className;
    }

    
    public IURLSetupGroup.Factory getSetupGroupFactory() {
        return SimpleURLSetupGroup.getFactory(null);
    }

    public IDatabaseInfo getDatabaseInfo() {
        return databaseExtension.getDatabaseInfo();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setDatabaseExtension(DatabaseExtension database) {
        this.databaseExtension = database;
    }

    public IJdbcConnectAdapter.Factory getConnectAdapterFactory() {
        return JdbcConnectAdapter.FACTORY;
    }
    
	public Factory getMetaDataFactory() {
		return JdbcMetaDataAdapter.FACTORY;
	}

    public boolean isUserIdNeeded() {
        return userIdNeeded;
    }

    public boolean isPasswordNeeded() {
        return passwordNeeded;
    }
}
