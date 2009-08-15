/*
 * created 09.04.2006, cse
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
package com.byterefinery.rmbench.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.byterefinery.rmbench.external.IExportable;
import com.byterefinery.rmbench.external.IModelExporter;
import com.byterefinery.rmbench.external.IExportable.ModelExport;

/**
 * Test implementation of IModelExporter
 * 
 * @author cse
 */
public class TestModelExporter implements IModelExporter {

    public static final class FactoryImpl implements IModelExporter.Factory {

        public IModelExporter getExporter(ModelExport modelExport) {
            return new TestModelExporter(modelExport);
        }
    }
    
    private final IExportable.ModelExport modelExport;
    private String text;
    
    public TestModelExporter(ModelExport modelExport) {
        this.modelExport = modelExport;
    }

    public void setText(String value) {
        this.text = value;
    }
    
    public void export(File directory) throws IOException {
        
        File exportFile = new File(directory, "test.export");
        PrintWriter writer = new PrintWriter(new FileWriter(exportFile)); 
        writer.println(text);
        writer.println("The model has "+modelExport.getModel().getSchemas().length+" schemas.");
        writer.close();
    }
}
