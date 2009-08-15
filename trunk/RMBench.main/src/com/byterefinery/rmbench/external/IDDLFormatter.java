/*
 * created 09.01.2006
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
package com.byterefinery.rmbench.external;


/**
 * a formatter lays out statement text and prints it to a script
 *  
 * @author cse
 */
public interface IDDLFormatter {

    public interface Factory {
        IDDLFormatter createFormatter(IDDLGenerator generator);
    }
    
    /**
     * format the text of the given statement, using the API provided by the script writer
     * 
     * @param statement a generated DDL statement
     * @param terminator the statement terminator to use
     * @param writer the script writer
     */
    void format(IDDLScript.Statement statement, String terminator, IDDLScript.Writer writer);
}
