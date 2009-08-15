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
package com.byterefinery.rmbench.test;

import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDDLScript.Statement;

/**
 * a test formatter that just writes statements through 
 * 
 * @author cse
 */
public class DDLFormatter implements IDDLFormatter {

    public static final class Factory implements IDDLFormatter.Factory {
        public IDDLFormatter createFormatter(IDDLGenerator generator) {
            return new DDLFormatter();
        }
    }
    
    public void format(Statement statement, String terminator, IDDLScript.Writer writer) {
        writer.print(statement.getString());
        writer.println();
    }
}
