/*
 * created 30.12.2005
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
package com.byterefinery.rmbench.export;

import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDDLScript.Range;

/**
 * implementation of a statement, which also stores the range in the generated
 * and formatted script
 * 
 * @author cse
 */
public class DDLStatement implements IDDLScript.Statement {

    private final String kind;
    private final StringBuffer buffer;
    private Range range;
    
    DDLStatement(String kind) {
        this.kind = kind;
        this.buffer = new StringBuffer();
    }
    
    public void append(String text) {
        buffer.append(text);
    }

	public void append(char character) {
        buffer.append(character);
	}

    public void setRange(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public String getKind() {
        return kind;
    }

    public String getString() {
        return buffer.toString();
    }
}
