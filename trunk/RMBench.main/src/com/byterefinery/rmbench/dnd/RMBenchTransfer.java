/*
 * created 25.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
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
