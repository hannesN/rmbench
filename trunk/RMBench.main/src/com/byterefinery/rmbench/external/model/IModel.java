/*
 * created 01.03.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.external.model;

import com.byterefinery.rmbench.external.IDatabaseInfo;

/**
 * representation of the design model, which in essence is a container for schemas and diagrams
 * 
 * @author cse
 */
public interface IModel {

    IDatabaseInfo getDatabaseInfo();

    /**
     * @return the schemas in this model
     */
    ISchema[] getSchemas();

    /**
     * @return the name
     */
	String getName();

	/**
	 * @return the diagrams contained in this model
	 */
	IDiagram[] getDiagrams();
}
