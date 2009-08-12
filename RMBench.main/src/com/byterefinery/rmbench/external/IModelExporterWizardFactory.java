/*
 * created 02.12.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: IDDLGeneratorWizardFactory.java 148 2006-01-30 20:40:59Z csell $
 */
package com.byterefinery.rmbench.external;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * a IModelExporterWizardFactory is responsible for creating configuration wizard pages for 
 * a model exporter
 *  
 * @author cse
 */
public interface IModelExporterWizardFactory {
    
    IWizardPage[] getWizardPages(IModelExporter exporter);
}
