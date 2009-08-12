/**
 * created 30.04.2005, cse
 * 
 * $Id:SystemException.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.exceptions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.byterefinery.rmbench.RMBenchConstants;

/**
 * a checked exception which supports message formatting. This exception type
 * is designed for wrapping exceptions from underlying technical subsystems 
 * which cannot be resolved internally. Norrmally, such exceptions will be 
 * logged and/or displayed interactively, and the offending operation will be 
 * cancelled. 
 * 
 * @author cse
 */
public class SystemException extends Exception {

	private static final long serialVersionUID = 3546079155419625015L;

	public SystemException(String message) {
		super(message);
	}

	public SystemException(Throwable cause) {
		super(computeCause(cause));
	}

	public SystemException(String message, Throwable cause) {
		super(message, computeCause(cause));
	}

    public SystemException(String message, String argument) {
		super(MessageFormat.format(message, new Object[]{argument}));
	}

	public SystemException(String message, Object argument, Throwable cause) {
		super(MessageFormat.format(message, new Object[]{argument}), computeCause(cause));
	}

	public SystemException(String message, Object[] arguments) {
		super(MessageFormat.format(message, arguments));
	}

	public SystemException(String message, Object[] arguments, Throwable cause) {
		super(MessageFormat.format(message, arguments), computeCause(cause));
	}

    public IStatus getStatus(String message) {
        Throwable cause = getCause();
        return new Status(
                Status.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, 
                cause != null ? cause : this);
    }

    public static IStatus getStatus(Throwable exception, String message) {
        return new Status(Status.ERROR, RMBenchConstants.PLUGIN_ID, 0, message, exception);
    }

    private static Throwable computeCause(Throwable cause) {
        while(cause instanceof SystemException && cause.getCause() != null)  {
            cause = cause.getCause();
        }
        return cause;
    }
}
