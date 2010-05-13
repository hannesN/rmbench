/*
 * created 08.02.2006
 *
 * Copyright 2009, ByteRefinery
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package com.byterefinery.rmbench.model.type;

import com.byterefinery.rmbench.external.model.type.Interval;

import junit.framework.TestCase;

/**
 * @author cse
 */
public class IntervalTest extends TestCase {

    public static final String[] TESTS = {
        "interval year",
        "interval year(22)",
        "interval year ( 22 )",
        "interval year to month",
        "interval year (3) to month",
        "interval day (3) to hour",
        "interval day (3) to second (12)",
        "interval hour (3) to minute ",
        "interval hour (3) to second (3)",
        "INTERVAL DAY (3) TO MINUTE"
    };
    
    /*
     * Test method for 'com.byterefinery.rmbench.model.type.Interval.parse(String)'
     */
    public void testParse() {
        Interval template = Interval.year();
        Interval result, result2;

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
        junit.textui.TestRunner.run(IntervalTest.class);
    }
}
