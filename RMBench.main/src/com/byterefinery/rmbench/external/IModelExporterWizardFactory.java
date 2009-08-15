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
