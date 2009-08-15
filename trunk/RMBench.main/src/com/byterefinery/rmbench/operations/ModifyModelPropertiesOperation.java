/*
 * created 09.03.2006
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
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.model.Model;
import com.byterefinery.rmbench.util.ModelTranslator;

/**
 * an undoable operation that modifies properties of the model. Should eventually
 * merge with {@link com.byterefinery.rmbench.operations.ModifyModelOperation}
 * 
 * @author cse
 */
public class ModifyModelPropertiesOperation extends RMBenchOperation {
    
    private Model model;
    String name, oldName;
    private IDatabaseInfo dbInfo, oldDBinfo;
    private INameGenerator nameGenerator, oldNameGenerator;
    
    public ModifyModelPropertiesOperation(Model model, String modelName, INameGenerator nameGenerator, IDatabaseInfo dbInfo) {
        super(Messages.Operation_ModifyModelProperties);
        this.model = model;
        this.name = modelName;
        this.dbInfo = dbInfo;
        this.nameGenerator = nameGenerator;
        this.oldName = model.getName();
        this.oldDBinfo = model.getDatabaseInfo();
        this.oldNameGenerator = model.getNameGenerator();
    }

    public IStatus execute(IProgressMonitor monitor, IAdaptable info)  throws ExecutionException {
        model.setName(name);
        model.setNameGenerator(nameGenerator);
        if(oldDBinfo != dbInfo){
            if(model.isEmpty())
                model.setDatabaseInfo(dbInfo);
            else
                new ModelTranslator().translate(model, dbInfo);
        }
        RMBenchPlugin.getEventManager().fireModelPropertiesChanged(model);
        return Status.OK_STATUS;
    }

    public IStatus undo(IProgressMonitor monitor, IAdaptable info)  throws ExecutionException {
        model.setName(oldName);
        model.setNameGenerator(oldNameGenerator);
        if(model.getDatabaseInfo() != oldDBinfo){
            if(model.isEmpty())
                model.setDatabaseInfo(oldDBinfo);
            else
                new ModelTranslator().translate(model, oldDBinfo);
        }
        RMBenchPlugin.getEventManager().fireModelPropertiesChanged(model);
        return Status.OK_STATUS;
    }

}
