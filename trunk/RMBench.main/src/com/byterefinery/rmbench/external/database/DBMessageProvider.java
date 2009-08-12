/*
 * created 07.09.2005 by sell
 *
 * $Id: DBMessageProvider.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external.database;

import java.text.MessageFormat;

import com.byterefinery.rmbench.exceptions.ExceptionMessages;
import com.byterefinery.rmbench.external.IDatabaseInfo;
import com.byterefinery.rmbench.external.IMessageProvider;
import com.byterefinery.rmbench.external.model.type.SizeDataType;
import com.byterefinery.rmbench.external.model.type.SizeScaleDataType;
import com.byterefinery.rmbench.model.schema.Column;

/**
 * default message provider that draws its messages from the 
 * {@link com.byterefinery.rmbench.exceptions.ExceptionMessages} bundle. All methods
 * will return the unchanged key if there is no associated message
 * 
 * @author sell
 */
public class DBMessageProvider implements IMessageProvider {

    public String getMessage(String key, Column column, Object value) {
        if(key.equals(SizeDataType.SIZE_TOO_BIG)) {
            long max = ((SizeDataType)column.getDataType()).getMaxSize();
            return MessageFormat.format(
                    ExceptionMessages.Message_SizeTooBig, 
                    new Object[]{new Long(max)});
        }
        else if(key.equals(SizeScaleDataType.SCALE_TOO_BIG)) {
            int max = ((SizeScaleDataType)column.getDataType()).getMaxScale();
            return MessageFormat.format(
                    ExceptionMessages.Message_ScaleTooBig, 
                    new Object[]{new Integer(max)});
        }
        return key;
    }

    public String getMessage(String key, String argument) {
        if(IDatabaseInfo.MessageKeys.INVALID_IDENTIFIER.equals(key)) {
            return MessageFormat.format(
                    ExceptionMessages.invalidName, 
                    new Object[]{argument});
        }
        else if(IDatabaseInfo.MessageKeys.RESERVED_NAME.equals(key)) {
            return MessageFormat.format(
                    ExceptionMessages.reservedName, 
                    new Object[]{argument});
        }
        return key;
    }
}
