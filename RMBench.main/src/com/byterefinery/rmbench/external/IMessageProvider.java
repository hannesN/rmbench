/*
 * created 07.09.2005 by sell
 *
 * $Id: IMessageProvider.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external;

import com.byterefinery.rmbench.model.schema.Column;

/**
 * A message provider may be associated with any message producer (e.g., a database info). 
 * It has the responsibility of translating error keys and values to user displayable messages.
 * There must be a common error key vocabulary between the message producer and the message 
 * provider.<p/>
 * <em>It is advisable to implement the methods defined by this interface such that in 
 * case a message key is unknown, the key itself is returned</em> 
 * 
 * 
 * @author sell
 */
public interface IMessageProvider {

    /**
     * @param key a message key  
     * @param column a database column
     * @param value the value that caused the message
     * @return a ready formatted message.
     */
    String getMessage(String key, Column column, Object value);
    /**
     * @param key a message key
     * @param argument a formatting argument
     * @return the ready formatted message
     */
    String getMessage(String key, String argument);
}
