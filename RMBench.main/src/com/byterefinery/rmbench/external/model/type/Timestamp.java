/*
 * created 06.09.2005 by sell
 *
 * $Id: Timestamp.java 471 2006-08-21 14:41:12Z cse $
 */
package com.byterefinery.rmbench.external.model.type;

import com.byterefinery.rmbench.external.model.IDataType;

/**
 * SQL99: timestamp [with [local] time zone]
 * 
 * @author sell
 */
public class Timestamp extends SizeDataType {

    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String WITH_LOCAL_TIMEZONE = "WITH LOCAL TIME ZONE";
    private static final String WITH_TIMEZONE = "WITH TIME ZONE";

    /**
     * @param maxPrecision
     * @return a <code>timestamp with time zone</code> type
     */
    public static Timestamp withTimeZone(long maxPrecision) {
        return new Timestamp(true, false, maxPrecision);
    }

    /**
     * @param maxPrecision
     * @return a <code>timestamp with local time zone</code> type
     */
    public static Timestamp withLocalTimeZone(long maxPrecision) {
        return new Timestamp(true, true, maxPrecision);
    }

    private final boolean isTimeZone;
    private final boolean isLocal;

    
    private Timestamp(boolean isTimeZone, boolean isLocal, long maxPrecision) {
        super(createDisplayName(isTimeZone, isLocal), maxPrecision, false, IDataType.UNSPECIFIED_SIZE);
        this.isTimeZone = isTimeZone;
        this.isLocal = isLocal;
    }

    /*
     * return the name for the selection list
     */
    private static String createDisplayName(boolean timeZone, boolean local) {
        if(!timeZone)
            return TIMESTAMP;
        else {
            StringBuffer buf = new StringBuffer(TIMESTAMP);
            buf.append(" ");
            buf.append(local ? WITH_LOCAL_TIMEZONE : WITH_TIMEZONE);
            return buf.toString();
        }
    }

    /**
     * @param maxPrecision the maximum precision
     */
    public Timestamp(long maxPrecision) {
        super(TIMESTAMP, maxPrecision, false, IDataType.UNSPECIFIED_SIZE);
        this.isTimeZone = false;
        this.isLocal = false;
    }

    public IDataType concreteInstance() {
        return new Timestamp(isTimeZone, isLocal, maxSize);
    }

    /**
     * Parse the given string into a new timestamp value. The input value is expected to be in
     * valid SQL99 timestamp syntax. Not all errors are detected.<p/>
     * Non-mutable values (e.g. <code>maxSize</code>) are copied from this object. 
     * 
     * @param name a type conforming to the SQL99 timestamp syntax
     * @return a timestamp value, or <code>null</code> if the input does not start with the 
     * <code>timestamp</code> keyword
     * @throws IllegalArgumentException if the syntax is found to be invalid 
     */
    public Timestamp parse(String name) {
        if(!name.startsWith(TIMESTAMP) && !name.startsWith(TIMESTAMP.toLowerCase()))
            return null;
        long precision = parseNumber(name, 0);
        
        boolean timeZone = name.indexOf("ZONE") > 0 || name.indexOf("zone") > 0;
        boolean local = timeZone && (name.indexOf("LOCAL") > 0 || name.indexOf("local") > 0);
        
        Timestamp result = new Timestamp(timeZone, local, maxSize);
        result.setSize(precision);
        return result;
    }

    public String getDDLName() {
        if(!isTimeZone)
            return super.getDDLName();
        
        StringBuffer buf = new StringBuffer(TIMESTAMP);
        if(size != IDataType.UNSPECIFIED_SIZE) {
            buf.append(" (");
            buf.append(getSize());
            buf.append(")");
        }
        buf.append(" ");
        if(isLocal)
            buf.append(WITH_LOCAL_TIMEZONE);
        else
            buf.append(WITH_TIMEZONE);
        return buf.toString();
    }
}
