/*
 * created 20.10.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: BytesNode.java 154 2006-02-01 19:45:24Z csell $
 */
package com.byterefinery.rmbench.export.diff;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * a model node that represents a leaf value (e.g., column comment, datatype).
 * 
 * @author cse
 */
public abstract class BytesNode extends BaseNode implements IStreamContentAccessor {

	private boolean ignoreCase;
    private byte[] bytes;
    private String string;
    
    public BytesNode(String name, Image image) {
        super(name, image);
    }
    
    public BytesNode(String name) {
        super(name, null);
    }

    public void setIgnoreCase(boolean ignoreCase) {
		super.setIgnoreCase(ignoreCase);
		this.ignoreCase = ignoreCase;
		bytes = null;
		string = null;
	}

	public InputStream getContents() throws CoreException {
    	if(bytes == null) createValues();
        return new ByteArrayInputStream(bytes);
    }
    
    public String toString() {
    	if(string == null)createValues();
        return string;
    }

	private void createValues() {
		string = generateValue(ignoreCase);
		bytes = string.getBytes();
	}

	protected abstract String generateValue(boolean ignoreCase);
}
