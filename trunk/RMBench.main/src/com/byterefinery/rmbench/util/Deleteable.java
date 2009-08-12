/*
 * created 30.07.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: Deleteable.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.util;

import org.eclipse.core.commands.operations.IUndoableOperation;

/**
 * interface for objects that can be the target of a delete or remove operation
 * 
 * @author cse
 */
public interface Deleteable {

    IUndoableOperation getDeleteOperation();
    IUndoableOperation getRemoveOperation();
}
