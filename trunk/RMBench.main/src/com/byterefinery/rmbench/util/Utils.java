/*
 * created 29.09.2007
 *
 * Copyright 2007, ByteRefinery
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
