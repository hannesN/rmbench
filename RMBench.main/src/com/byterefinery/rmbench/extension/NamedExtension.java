/*
 * created 29.08.2005 by sell
 *
 * $Id: NamedExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

/**
 * utility superclass for extensions that consist of an id and a name
 * 
 * @author sell
 */
public class NamedExtension {

    protected final String namespace;
    protected final String id;
    protected final String name;
    
    protected NamedExtension(String namespace, String id, String name) {
        this.namespace = namespace;
        this.id = id;
        this.name = name;
    }

    /**
     * @return the namespace, which is identical to the ID of the defining plugin
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return the ID, which is unique within the namespace
     */
    public String getId() {
        return id;
    }

    /**
     * @return the user-readable name
     */
    public String getName() {
        return name;
    }
}
