/*
 * created 07.04.2005
 * 
 * $Id: SQL99.java 666 2007-10-01 19:32:51Z cse $
 */
package com.byterefinery.rmbench.external.database.sql99;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.byterefinery.rmbench.external.database.DatabaseInfo;
import com.byterefinery.rmbench.external.model.IDataType;
import com.byterefinery.rmbench.external.model.type.IntegralDataType;
import com.byterefinery.rmbench.external.model.type.Interval;
import com.byterefinery.rmbench.external.model.type.SizeDataType;
import com.byterefinery.rmbench.external.model.type.SizeScaleDataType;
import com.byterefinery.rmbench.external.model.type.Timestamp;

/**
 * representation of the SQL99 standard. This class cannot be used as a concrete database
 * (i.e., for schema import or export purposes)
 * 
 * @author cse
 */
public final class SQL99 extends DatabaseInfo {

    public static Pattern IDENTIFIER_PATTERN = Pattern.compile("[A-Z]\\w*", Pattern.CASE_INSENSITIVE);
    
    private static final String KEYWORDS_RESOURCE = "sql99_words";

    public static final IDataType INTEGER = new IntegralDataType(new String[]{"INTEGER", "INT"}); 
    public static final IDataType SMALLINT = new IntegralDataType("SMALLINT"); 
    public static final IDataType NUMERIC = 
        new SizeScaleDataType(new String[]{"NUMERIC"}, 15, false, 10, 15, false, 2); 
    public static final IDataType DECIMAL = 
        new SizeScaleDataType(new String[]{"DECIMAL", "DEC"}, 15, false, 10, 15, false, 2); 
    public static final IDataType FLOAT = new IntegralDataType("FLOAT"); 
    public static final IDataType REAL = new IntegralDataType("REAL");
    public static final IDataType DOUBLE_PRECISION = 
        new IntegralDataType(new String[]{"DOUBLE", "DOUBLE PRECISION"}); 
    public static final IDataType BIT = new SizeDataType("BIT", IDataType.UNLIMITED_SIZE, false, 10); 
    public static final IDataType BIT_VARYING = 
        new SizeDataType("BIT VARYING", IDataType.UNLIMITED_SIZE, false, 100); 
    public static final IDataType BLOB = 
        new SizeDataType(
                new String[]{"BLOB", "BINARY LARGE OBJECT"}, 
                IDataType.UNLIMITED_SIZE, 
                false,
                IDataType.UNSPECIFIED_SIZE);
    public static final IDataType CHAR = 
        new SizeDataType(new String[]{"CHAR", "CHARACTER"}, IDataType.UNLIMITED_SIZE, false, 10); 
    public static final IDataType VARCHAR = 
        new SizeDataType(new String[]{"VARCHAR", "CHARACTER VARYING"}, IDataType.UNLIMITED_SIZE, false, 100); 
    public static final IDataType NCHAR = 
        new SizeDataType(
                new String[]{"NCHAR", "NATIONAL CHAR", "NATIONAL CHARACTER"}, 
                IDataType.UNLIMITED_SIZE, false, 10);
    public static final IDataType NVARCHAR = 
        new SizeDataType(
                new String[]{"NCHAR VARYING", "NATIONAL CHAR VARYING", "NATIONAL CHARACTER VARYING"}, 
                IDataType.UNLIMITED_SIZE, false, 100); 
    public static final IDataType CLOB = 
        new SizeDataType(
                new String[]{"CLOB", "CHARACTER LARGE OBJECT"}, 
                IDataType.UNLIMITED_SIZE, false, IDataType.UNSPECIFIED_SIZE); 
    public static final IDataType NCLOB = 
        new SizeDataType(
                new String[]{"NCLOB", "NATIONAL CHARACTER LARGE OBJECT"}, 
                IDataType.UNLIMITED_SIZE, false, IDataType.UNSPECIFIED_SIZE); 
    public static final IDataType DATE = new IntegralDataType("DATE");
    public static final IDataType TIME = 
        new SizeDataType("TIME", IDataType.UNLIMITED_SIZE, false, IDataType.UNSPECIFIED_SIZE); 
    public static final IDataType TIME_TZ = 
        new SizeDataType("TIME WITH TIME ZONE", 
                IDataType.UNLIMITED_SIZE, false, IDataType.UNSPECIFIED_SIZE);
    public static final IDataType TIMESTAMP = new Timestamp(IDataType.UNLIMITED_SIZE); 
    public static final IDataType TIMESTAMP_TZ = Timestamp.withTimeZone(IDataType.UNLIMITED_SIZE);
    public static final IDataType INTERVAL_YTOM = Interval.yearToMonth();
    public static final IDataType INTERVAL_DTOS = Interval.dayToSecond();
    public static final IDataType BOOLEAN = new IntegralDataType("BOOLEAN"); 
    
    public static final IDataType UNKNOWN = 
        new SizeScaleDataType(new String[]{"UNKNOWN"}, IDataType.UNSPECIFIED_SIZE, false, 0, IDataType.UNSPECIFIED_SCALE, false, 0); 
    
    public static SQL99 instance;
    
    private final Set<String> keywords = new HashSet<String>();
    
    public SQL99() {
        registerDataType(INTEGER, null, 1);
        registerDataType(SMALLINT, null, 1);
        registerDataType(NUMERIC, null, 1);
        registerDataType(DECIMAL, null, 1);
        registerDataType(FLOAT, null, 1);
        registerDataType(REAL, null, 1);
        registerDataType(DOUBLE_PRECISION, null, 1);
        registerDataType(BIT, null, 1);
        registerDataType(BIT_VARYING, null, 1);
        registerDataType(BLOB, null, 1);
        registerDataType(CHAR, null, 1);
        registerDataType(VARCHAR, null, 1);
        registerDataType(NCHAR, null, 1);
        registerDataType(NVARCHAR, null, 1);
        registerDataType(CLOB, null, 1);
        registerDataType(NCLOB, null, 1);
        registerDataType(DATE, null, 1);
        registerDataType(TIME, null, 1);
        registerDataType(TIMESTAMP, null, 1);
        registerDataType(TIME_TZ, null, 1);
        registerDataType(TIMESTAMP_TZ, null, 1);
        registerDataType(Interval.year(), null, 1);
        registerDataType(INTERVAL_YTOM, null, 1);
        registerDataType(Interval.month(), null, 1);
        registerDataType(Interval.day(), null, 1);
        registerDataType(Interval.dayToHour(), null, 1);
        registerDataType(Interval.dayToMinute(), null, 1);
        registerDataType(INTERVAL_DTOS, null, 1);
        registerDataType(Interval.hourToMinute(), null, 1);
        registerDataType(Interval.hourToSecond(), null, 1);
        registerDataType(Interval.minute(), null, 1);
        registerDataType(Interval.minuteToSecond(), null, 1);
        registerDataType(BOOLEAN, null, 1);
        
        try {
            loadKeywords();
        }
        catch (IOException e) {
        }
        instance = this;
    }

    private void loadKeywords() throws IOException {
        InputStream in = getClass().getResourceAsStream(KEYWORDS_RESOURCE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        String line;
        while((line = reader.readLine()) != null) {
            String kw = line.trim();
            keywords.add(kw);
            keywords.add(kw.toLowerCase());
        }
    }

    public String validateIdentifier(String identifier) {
        return IDENTIFIER_PATTERN.matcher(identifier).matches() ? 
                null : MessageKeys.INVALID_IDENTIFIER;
    }

    public Set<String> getReservedWords() {
        return keywords;
    }

    /**
     * @return the parameter type itself, as the standard type IS native to this database
     */
    public IDataType getNativeDataType(IDataType standardType) {
        return standardType;
    }

    /**
     * this implementation will match the datatype to the JDBC constant passed as the 
     * first parameter. If this is not possible, a special UNKNOWN type is returned
     */
    public IDataType getDataType(int jdbcType, String typeName, long size, int scale) {
    	
    	IDataType type;
    	
    	switch(jdbcType) {
    	case Types.BIT:
    		type = BIT;
    		break;
		case Types.TINYINT:
    		type = SMALLINT;
    		break;
		case Types.SMALLINT:
    		type = SMALLINT;
    		break;
		case Types.INTEGER:
			type = INTEGER;
    		break;
		case Types.BIGINT:
			type = INTEGER;
    		break;
		case Types.FLOAT:
			type = FLOAT;
    		break;
		case Types.REAL:
			type = REAL;
    		break;
		case Types.DOUBLE:
			type = DOUBLE_PRECISION;
    		break;
		case Types.NUMERIC:
			type = NUMERIC;
    		break;
		case Types.DECIMAL:
			type = DECIMAL;
    		break;
		case Types.CHAR:
			type = CHAR;
    		break;
		case Types.VARCHAR:
			type = VARCHAR;
    		break;
		case Types.LONGVARCHAR:
			type = VARCHAR;
    		break;
		case Types.DATE:
			type = DATE;
    		break;
		case Types.TIME:
			type = TIME;
    		break;
		case Types.TIMESTAMP:
			type = TIMESTAMP;
    		break;
		case Types.BINARY:
			type = BIT;
    		break;
		case Types.VARBINARY:
			type = BIT_VARYING;
    		break;
		case Types.LONGVARBINARY:
			type = BIT_VARYING;
    		break;
        case Types.BLOB:
        	type = BLOB;
    		break;
        case Types.CLOB:
        	type = CLOB;
    		break;
   	    case Types.BOOLEAN:
   	    	type = BOOLEAN;
    		break;
    	default:
    		type = UNKNOWN;
    	}
    	type = type.concreteInstance();
    	if(type.acceptsSize())
    		type.setSize(size);
    	if(type.acceptsScale())
    		type.setScale(scale);
    	
    	return type;
    }
}
