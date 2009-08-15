/*
 * created 19.09.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: TableTypeExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;


/**
 * @author cse
 */
public class TableTypeExtension extends NamedExtension {

    public final TableThemeExtension themeExtension;
    
    public TableTypeExtension(
            String namespace, 
            String id, 
            String label, 
            TableThemeExtension themeExtension) {
        super(namespace, id, label);
        this.themeExtension = themeExtension;
    }
    
    public String getLabel() {
        return name;
    }
}
