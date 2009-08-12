/*
 * created 29.08.2005 by sell
 *
 * $Id: NameGeneratorExtension.java 678 2008-02-17 22:52:09Z cse $
 */
package com.byterefinery.rmbench.extension;

import com.byterefinery.rmbench.external.INameGenerator;

/**
 * representation of a nameGenerator extension
 * 
 * @author sell
 */
public class NameGeneratorExtension extends NamedExtension {

    private final INameGenerator nameGenerator;
    
    protected NameGeneratorExtension(
            String namespace,
            String id, 
            String name, 
            INameGenerator nameGenerator) {
        super(namespace, id, name);
        this.nameGenerator = nameGenerator;
    }

    public INameGenerator getNameGenerator() {
        return nameGenerator;
    }
}
