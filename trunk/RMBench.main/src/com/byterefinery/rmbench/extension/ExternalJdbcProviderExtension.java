/*
 * created 04.02.2008 by sell
 *
 * $Id$
 */
package com.byterefinery.rmbench.extension;

import com.byterefinery.rmbench.external.IExternalJdbcProvider;

public class ExternalJdbcProviderExtension extends NamedExtension {

	private final IExternalJdbcProvider provider;
	
	protected ExternalJdbcProviderExtension(
            String namespace,
	        String id, 
	        String name, 
	        IExternalJdbcProvider connectAdapter) {
		super(namespace, id, name);
		this.provider = connectAdapter;
	}
	
	public IExternalJdbcProvider getProvider() {
	    return provider;
	}
}
