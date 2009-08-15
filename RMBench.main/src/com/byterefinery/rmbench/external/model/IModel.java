/*
 * created 01.03.2006
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
