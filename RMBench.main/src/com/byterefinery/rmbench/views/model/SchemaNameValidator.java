/*
 * created 14-Feb-2006
 * 
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: SchemaNameValidator.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.views.model;

import org.eclipse.jface.dialogs.IInputValidator;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.extension.DatabaseExtension;
import com.byterefinery.rmbench.model.Model;

/**
 * The name validator for a schema name. It first checks for duplicate schema 
 * names, then calls the database info's validate method.
 * 
 * @author Hannes Niederhausen
 */
public class SchemaNameValidator implements IInputValidator {

    public String isValid(String schemaName) {
        Model model = RMBenchPlugin.getModelManager().getModel(); 
        if(model.getSchema(schemaName) != null)
            return ExceptionMessages.duplicateName;
        
        if(schemaName.length() == 0)
            return "";
        
        String errorKey = model.getDatabaseInfo().validateSchemaName(schemaName);
        if(errorKey != null) {
            DatabaseExtension extension = 
                RMBenchPlugin.getExtensionManager().getDatabaseExtension(model.getDatabaseInfo());
            return extension.getMessageFormatter().getMessage(errorKey, schemaName);
        }
        else
            return null; 
    }
}
