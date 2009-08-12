/*
 * created 02.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
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
