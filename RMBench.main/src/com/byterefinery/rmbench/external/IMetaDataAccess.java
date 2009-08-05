/*
 * created 11.08.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external;

import java.sql.Connection;
import java.sql.SQLException;

import com.byterefinery.rmbench.external.model.IColumn;
import com.byterefinery.rmbench.external.model.IDataType;

/**
 * interface that encapsulates all access to database metadata performed by RMBench during schema import. 
 * It is modelled mostly after {@link java.sql.DatabaseMetaData} 
 * 
 * @author cse
 */
public interface IMetaDataAccess {
	
    /**
     * @see java.sql.DatabaseMetaData#columnNoNulls
     */
    int columnNoNulls = 0;
    
    /**
     * @see java.sql.DatabaseMetaData#tableIndexStatistic
     */
    short tableIndexStatistic = 0;
    
	/**
	 * factory for creating IMetaDataAccess objects
	 */
	public interface Factory {
		IMetaDataAccess createMetaData(Connection connection, boolean option) throws SQLException;
	}
	
	/**
	 * result set adapter used by IMetaDataAccess
	 */
	public interface ResultSet {
		String getString(int index) throws SQLException;
		int getInt(int index) throws SQLException;
		long getLong(int index) throws SQLException;
		boolean getBoolean(int index) throws SQLException;
		boolean next() throws SQLException;
		void close() throws SQLException;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[]) 
	 */
	ResultSet getTables(String catalogName, String schemaName, String[] types) throws SQLException;
	/**
	 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
	 */
	ResultSet getColumns(String catalogName, String schemaName, String tableName) throws SQLException;
	/**
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String) 
	 */
	ResultSet getPrimaryKeys(String catalogName, String schemaName, String tableName) throws SQLException;
	/**
	 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String) 
	 */
	ResultSet getImportedKeys(String catalogName, String schemaName, String tableName) throws SQLException;
	/**
	 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean) 
	 */
	ResultSet getIndexInfo(String catalogName, String schemaName, String tableName) throws SQLException;
	/**
	 * @see java.sql.DatabaseMetaData#getSchemas() 
	 */
	ResultSet getSchemas() throws SQLException;
	
	/**
	 * loads the extradata of a data type which uses more than the scale/size data and stores it directly in the datatype
	 * @param column column using the data type
	 * @param dataType the data type which needs the extra data
	 */
	void loadExtraData(IColumn column, IDataType dataType);
}
