/*
 * created 29.09.2007
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

package com.byterefinery.rmbench.util;

import java.util.Collections;
import java.util.List;

/**
 * container class for generic utility functions
 * 
 * @author cse
 */
public class Utils {

	public static final List<?> EMPTY_LIST = Collections.EMPTY_LIST;
	
	/**
	 * this method is copied from {@link Collections} in oder to provide the guarantee
	 * that emptyList() == EMPTY_LIST.
	 * 
	 * @param <T>
	 * @return an unmodifiable list
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> emptyList() {
		return (List<T>) EMPTY_LIST;
	}
}
