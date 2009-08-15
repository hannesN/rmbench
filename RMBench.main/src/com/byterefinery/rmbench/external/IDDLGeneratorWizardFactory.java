/*
 * created 02.12.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: IDDLGeneratorWizardFactory.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * a IDDLGeneratorWizardFactory is responsible for creating configuration wizard pages for 
 * DDL generators
 *  
 * @author cse
 */
public interface IDDLGeneratorWizardFactory {
    
    IWizardPage getWizardPage(IDDLGenerator generator);
}
