/*
 * created 14.02.2008
 *
 * Copyright 2008, ByteRefinery
 * 
 * $Id$
 */

package com.byterefinery.rmbench.external;

import com.byterefinery.rmbench.RMBenchConstants;

/**
 * an extension identifier consisting of a namespace (aka plugin ID) and an
 * ID which is unique within the namespace
 * 
 * @author cse
 */
public final class ExtensionId {

    public final String namespace;
    public final String id;
    
    /**
     * create an identifier inside an arbitrary namespace
     */
    public ExtensionId(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
    }

    /**
     * create an identifier inside the RMBench plugin namespace
     */
    public ExtensionId(String id) {
        this.namespace = RMBenchConstants.PLUGIN_ID;
        this.id = id;
    }
}
