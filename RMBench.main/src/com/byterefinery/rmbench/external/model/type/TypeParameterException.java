/*
 * created 11.09.2005
 *
 * Copyright 2005, DynaBEAN Consulting
 * 
 * $Id: TypeParameterException.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external.model.type;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * exception that is thrown when a type parameter value is written that exceeds the
 * associated maximum
 * 
 * @author cse
 */
public class TypeParameterException extends IllegalArgumentException {

    private static final long serialVersionUID = -9137339140681558173L;

    public TypeParameterException(IDataType type, String token, long size, long max) {
        super(type.getPrimaryName()+"["+token+"]: "+size+" max:"+max);
    }

    public TypeParameterException(IDataType type, String token, int scale, int max) {
        super(type.getPrimaryName()+"["+token+"]: "+scale+" max:"+max);
    }
}
