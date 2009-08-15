/*
 * created 30.07.2005
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
