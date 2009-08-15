/*
 * created 30.01.2006
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

/**
 * extension of the {@link com.byterefinery.rmbench.external.model.IColumn} interface that 
 * adds mutator methods
 * 
 * @author cse
 */
public interface IColumn2 extends IColumn {
    /**
     * @param value the new default value
     */
    void setDefault(String value);

    /**
     * @param comment the new default comment
     */
    void setComment(String value);
}
