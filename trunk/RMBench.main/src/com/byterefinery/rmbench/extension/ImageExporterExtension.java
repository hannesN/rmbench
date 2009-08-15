/*
 * created 24.02.2006
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
package com.byterefinery.rmbench.extension;

import com.byterefinery.rmbench.external.IImageExporter;

/**
 * @author cse
 */
public class ImageExporterExtension extends NamedExtension {

    private final String description;
    private final IImageExporter imageExporter;
    
    public ImageExporterExtension(
            String namespace,
            String id, 
            String name, 
            String description, 
            IImageExporter imageExporter) {
        super(namespace, id, name);
        this.description = description;
        this.imageExporter = imageExporter;
    }

    public String getDescription() {
        return description;
    }

    public IImageExporter getImageExporter() {
        return imageExporter;
    }
}
