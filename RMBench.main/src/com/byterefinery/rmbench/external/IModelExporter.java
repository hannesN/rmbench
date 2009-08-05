/*
 * created 09.04.2006
 *
 * Copyright 2006, DynaBEAN Consulting
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
