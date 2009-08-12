/*
 * created 08.02.2006
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.model.type;

import com.byterefinery.rmbench.external.model.type.Timestamp;

import junit.framework.TestCase;

/**
 * @author cse
 */
public class TimestampTest extends TestCase {

    public final String[] TESTS = {
            "timestamp (22) with time zone",
            "timestamp (22) with local time zone",
            "timestamp(33)",
            "TIMESTAMP (1)",
            "timestamp"
    };
    
    /*
     * Test method for 'com.byterefinery.rmbench.model.type.Timestamp.parse(String)'
     */
    public final void testParse() {
        Timestamp template = new Timestamp(44);
        Timestamp result, result2;

        for (int i = 0; i < TESTS.length; i++) {
            result = template.parse(TESTS[i]);
            result2 = template.parse(result.getDDLName());
            assertEquals(result, result2);

            result = template.parse(TESTS[i].toUpperCase());
            result2 = template.parse(result.getDDLName());
            assertEquals(result, result2);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TimestampTest.class);
    }
}
