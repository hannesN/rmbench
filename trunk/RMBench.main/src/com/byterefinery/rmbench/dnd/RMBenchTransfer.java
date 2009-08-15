/*
 * created 25.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id: RMBenchTransfer.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.dnd;

import org.eclipse.gef.dnd.SimpleObjectTransfer;
import org.eclipse.gef.requests.CreationFactory;

/**
 * abstract superclass for intra-VM drag & drop transfer. Instances of subclasses
 * also serve as creation factories in the drop target context

 * @author cse
 */
public abstract class RMBenchTransfer extends SimpleObjectTransfer implements CreationFactory {

    public Object getNewObject() {
        return getObject();
    }
}
