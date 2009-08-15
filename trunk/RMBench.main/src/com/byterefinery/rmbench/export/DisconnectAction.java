/*
 * created 26.01.2006
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
package com.byterefinery.rmbench.export;


import org.eclipse.jface.action.Action;

import com.byterefinery.rmbench.RMBenchPlugin;
import com.byterefinery.rmbench.exceptions.SystemException;
import com.byterefinery.rmbench.external.IDBAccess;
import com.byterefinery.rmbench.util.ImageConstants;

/**
 * an action for closing an open database conection
 * 
 * @author cse
 */
public class DisconnectAction extends Action implements IDBAccess.Listener {

    public static final String ID = "ExecuteScriptAction";

    private IDBAccess.Executor executor;
    
    public DisconnectAction() {
        super();
        
        setId(ID);
        setText(Messages.ExportEditor_closeConnection);
        setToolTipText(Messages.ExportEditor_closeConnection);
        setImageDescriptor(RMBenchPlugin.getImageDescriptor(ImageConstants.DISCONNECT));
    }

    public void setExecutor(IDBAccess.Executor executor) {
        if(this.executor != null)
            this.executor.removeListener(this);
        this.executor = executor;
        if(this.executor != null)
            this.executor.addListener(this);
        
        setEnabled(executor != null && executor.isConnected());
    }

    public void run() {
        try {
            executor.close();
        }
        catch (SystemException e) {
            RMBenchPlugin.logError(e);
        }
    }

    public void connected(boolean state) {
        setEnabled(state);
    }
}
