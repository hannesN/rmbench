/*
 * created 24.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.extension;

import org.eclipse.jface.wizard.IWizardPage;

import com.byterefinery.rmbench.external.IModelExporter;
import com.byterefinery.rmbench.external.IModelExporterWizardFactory;
import com.byterefinery.rmbench.external.IExportable.ModelExport;

/**
 * @author cse
 */
public class ModelExporterExtension extends NamedExtension {

    private final String description;
    private final IModelExporter.Factory exporterFactory;
    private final IModelExporterWizardFactory wizardFactory;
    
    public ModelExporterExtension(
            String namespace,
            String id, 
            String name, 
            String description, 
            IModelExporter.Factory exporterFactory,
            IModelExporterWizardFactory wizardFactory) {
        
        super(namespace, id, name);
        this.description = description;
        this.exporterFactory = exporterFactory;
        this.wizardFactory = wizardFactory;
    }

    public String getDescription() {
        return description;
    }

    public IModelExporter getModelExporter(ModelExport export) {
        return exporterFactory.getExporter(export);
    }

    /**
     * @return the configuration pages as created by the pageFactory, or null if no pageFactory is defined
     */
    public IWizardPage[] createConfigPages(IModelExporter exporter) {
        return wizardFactory != null ? wizardFactory.getWizardPages(exporter) : null;
    }
}
