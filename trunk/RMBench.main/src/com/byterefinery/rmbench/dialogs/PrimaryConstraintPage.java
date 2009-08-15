/*
 * created 01.09.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: PrimaryConstraintPage.java 667 2007-10-02 18:54:16Z cse $
 */
package com.byterefinery.rmbench.dialogs;

import java.util.List;

import com.byterefinery.rmbench.model.schema.Column;
import com.byterefinery.rmbench.model.schema.PrimaryKey;

/**
 * wizard page for specifying a primary key constraint
 * 
 * @author cse
 */
public class PrimaryConstraintPage extends UniqueConstraintPage {

    public static final String PAGE_NAME = PrimaryKey.CONSTRAINT_TYPE;
    
    public PrimaryConstraintPage(List<Column> columns) {
        super(
                PAGE_NAME, 
                Messages.ConstraintWizard_PrimaryTitle, 
                Messages.ConstraintWizard_PrimaryDescription, 
                columns);
    }
}
