/*
 * created 09.04.2006
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

import java.io.File;
import java.io.IOException;


/**
 * extension interface for model exporters
 * 
 * @author cse
 */
public interface IModelExporter {

    public interface Factory {
        IModelExporter getExporter(IExportable.ModelExport modelExport);
    }
    
    /**
     * @param directory the directory to export to
     */
	void export(File directory) throws IOException;
}
