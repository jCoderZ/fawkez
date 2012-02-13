/*
 * $Id: FileSummaryTest.java 1481 2009-05-23 05:54:44Z amandel $
 *
 * Copyright 2006, The jCoderZ.org Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *    * Neither the name of the jCoderZ.org Project nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jcoderz.phoenix.report;

import junit.framework.TestCase;

/**
 * Test the FileSummary class.
 * @author Andreas Mandel
 */
public class FileSummaryTest
    extends TestCase
{
    /** Test the {@link FileSummary#getNumberOfFindings()} method. */
    public void testGetNumberOfFindings ()
    {
        final FileSummary testSummary = new FileSummary();

        assertEquals("No violation at all.", 0,
                testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.FILTERED);
        assertEquals("Filtered should not be count.", 0,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.OK);
        assertEquals("OK should not be count.", 0,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.COVERAGE);
        assertEquals("COVERAGE is not expected to be count.", 0,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.INFO);
        assertEquals("INFO is expected to be count.", 1,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.CODE_STYLE);
        assertEquals("CODE_STYLE is expected to be count.", 2,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.CPD);
        assertEquals("CPD is expected to be count.", 3,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.DESIGN);
        assertEquals("DESIGN is expected to be count.", 4,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.ERROR);
        assertEquals("ERROR is expected to be count.", 5,
            testSummary.getNumberOfFindings());
        testSummary.addViolation(Severity.WARNING);
        assertEquals("WARNING is expected to be count.", 6,
            testSummary.getNumberOfFindings());

    }
}
