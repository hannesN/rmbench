/*
 * created 14.05.2005
 * 
 * $Id: IDriverInfo.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.jdbc;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IMetaDataAccess;
import com.byterefinery.rmbench.external.IURLSetupGroup;
import com.byterefinery.rmbench.external.database.jdbc.IJdbcConnectAdapter;

/**
 * specification of a plugin extension element that describes a JDBC 
 * driver along with a configuration facility. This interface is not 
 * implemented by external parties
 *  
 * @author cse
 */
public interface IDriverInfo {

    /**
     * @return driver class name
     */
    String getClassName();
    
    /**
     * @return the database info 
     */
    IDatabaseInfo getDatabaseInfo();

    /**
     * @return a factory for creating a URL setup input group
     */
    IURLSetupGroup.Factory getSetupGroupFactory();
    
    /**
     * @return a factory for creating MetaData access objects
     */
    IMetaDataAccess.Factory getMetaDataFactory();
    
    /**
     * @return a factory for the connect adapter for the underlying driver
     */
    IJdbcConnectAdapter.Factory getConnectAdapterFactory();
    
    /**
     * @return <code>true</code> if a userId is required. This will cause a text input field
     * to be displayed in the connection wizard dialog, whose value is passed in the <i>user</i>
     * connection property
     */
    boolean isUserIdNeeded();
    
    /**
     * @return <code>true</code> if a password is required. This will cause a text input field
     * to be displayed in the connection wizard dialog, whose value is passed in the <i>password</i>
     * connection property
     */
    boolean isPasswordNeeded();
}
