/*
 * created 19-Feb-2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id: ModifyModelOperation.java 471 2006-08-21 14:41:12Z cse $
 */
/**
 * 
 */
package com.byterefinery.rmbench.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.INameGenerator;
import com.byterefinery.rmbench.model.Model;

/**
 * an undoable operation that modifies properties of the model. Should eventually
 * merge with {@link com.byterefinery.rmbench.operations.ModifyModelPropertiesOperation}
 *  
 * @author Hannesn
 */
public class ModifyModelOperation extends RMBenchOperation {

    static public final int PROP_NAME = 1;
    static public final int PROP_DATABASE = 2;
    static public final int PROP_NAME_GENERATOR = 4;
    
    private Model model;
    private Object oldValue;
    private Object newValue;
    private int property;
    
    /**
     * @param label
     */
    public ModifyModelOperation(Model model, int property, Object newValue) {
        super(Messages.Operation_ModifyModel);
        this.model=model;
        this.newValue=newValue;
        this.property=property;
    }

    /* (non-Javadoc)
     * @see com.byterefinery.rmbench.operations.RMBenchOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        switch (property) {
            case PROP_NAME: oldValue = model.getName();
                            model.setName((String) newValue);
                            break;
            case PROP_NAME_GENERATOR:
                            oldValue = model.getNameGenerator();
                            model.setNameGenerator((INameGenerator) newValue);
                            break;
            case PROP_DATABASE:
                            oldValue = model.getDatabaseInfo();
                            model.setDatabaseInfo((IDatabaseInfo) newValue);
                            break;
            default:        return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    /* (non-Javadoc)
     * @see com.byterefinery.rmbench.operations.RMBenchOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        switch (property) {
            case PROP_NAME: model.setName((String) oldValue);
                            break;
            case PROP_NAME_GENERATOR:
                            model.setNameGenerator((INameGenerator) oldValue);
                            break;
            case PROP_DATABASE:
                            model.setDatabaseInfo((IDatabaseInfo) oldValue);
                            break;
            default:        return Status.CANCEL_STATUS;
        }
        
        return Status.OK_STATUS;
    }

}
