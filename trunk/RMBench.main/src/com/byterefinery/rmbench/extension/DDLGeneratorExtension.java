/*
 * created 29.08.2005 by sell
 *
 * $Id: DDLGeneratorExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import org.eclipse.jface.wizard.IWizardPage;

import com.byterefinery.rmbench.external.IDDLFormatter;
import com.byterefinery.rmbench.external.IDDLGenerator;
import com.byterefinery.rmbench.external.IDDLGeneratorWizardFactory;
import com.byterefinery.rmbench.external.IDDLScript;
import com.byterefinery.rmbench.external.IDatabaseInfo;

/**
 * representation of a DDLGenerator extension
 * 
 * @author sell
 */
public class DDLGeneratorExtension extends NamedExtension {
    
    private final IDDLGenerator.Factory generatorFactory;
    private final IDDLGeneratorWizardFactory wizardCreator;
    private final IDDLFormatter.Factory formatterFactory;
    private final IDDLScript.Factory scriptFactory;
    private final DatabaseExtension[] supportedDatabases;
    private final DatabaseExtension nativeDatabase;
    
    public DDLGeneratorExtension(
            String namespace,
            String id, 
            String name, 
            IDDLGenerator.Factory generatorFactory,
            IDDLGeneratorWizardFactory wizardCreator,
            IDDLFormatter.Factory formatterFactory,
            IDDLScript.Factory scriptFactory,
            DatabaseExtension nativeDatabase,
            DatabaseExtension[] supportedDatabases) {
        
        super(namespace, id, name);
        
        this.generatorFactory = generatorFactory;
        this.wizardCreator = wizardCreator;
        this.formatterFactory = formatterFactory;
        this.scriptFactory = scriptFactory;
        this.supportedDatabases = supportedDatabases;
        this.nativeDatabase = nativeDatabase;
    }

    public IDDLGenerator getDDLGenerator(IDatabaseInfo database) {
        return generatorFactory.getGenerator(database);
    }

    /**
     * @return a new wizard page, or <code>null</code> if a wizard page creator was 
     * not specified by this extension
     */
    public IWizardPage createGeneratorWizardPage(IDDLGenerator generator) {
        return wizardCreator != null ? wizardCreator.getWizardPage(generator) : null;
    }
    
    public IDDLFormatter createFormatter(IDDLGenerator generator) {
        return formatterFactory.createFormatter(generator);
    }
    
    public IDDLScript createScript(IDDLGenerator generator) {
        return scriptFactory.createScript(generator);
    }
    
    public DatabaseExtension[] getDatabaseExtensions() {
        return supportedDatabases;
    }

    /**
     * @param id a database extension id
     * @return true if the given database is in the list of supported databases
     */
    public boolean supportsDatabase(String id) {
        for (int i = 0; i < supportedDatabases.length; i++) {
            if(supportedDatabases[i].getId().equals(id))
                return true;
        }
        return false;
    }

    /**
     * @param id a database extension id
     * @return true if the given database is the native database for this generator
     */
    public boolean belongsToDatabase(String id) {
        return nativeDatabase != null && nativeDatabase.getId().equals(id);
    }
}
