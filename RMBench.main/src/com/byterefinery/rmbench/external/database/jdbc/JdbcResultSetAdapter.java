/*
 * created 11.08.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.database.jdbc;

import java.sql.SQLException;

import com.byterefinery.rmbench.external.IMetaDataAccess;

/**
 * this class wraps a JDBC result set and provides the API used during Metadata import. It can be used 
 * by JDBC-based metadata importers
 * 
 * @see com.byterefinery.rmbench.external.database.jdbc.JdbcMetaDataAdapter
 * @author cse
 */
public class JdbcResultSetAdapter implements IMetaDataAccess.ResultSet {

	private final java.sql.ResultSet resultSet;
	
	public JdbcResultSetAdapter(java.sql.ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	public String getString(int index) throws SQLException {
		return resultSet.getString(index);
	}
	public int getInt(int index) throws SQLException {
		return resultSet.getInt(index);
	}
	public long getLong(int index) throws SQLException {
		return resultSet.getLong(index);
	}
	public boolean getBoolean(int index) throws SQLException {
		return resultSet.getBoolean(index);
	}
	public boolean next() throws SQLException {
		return resultSet.next();
	}
	public void close() throws SQLException {
		resultSet.close();
	}
}
