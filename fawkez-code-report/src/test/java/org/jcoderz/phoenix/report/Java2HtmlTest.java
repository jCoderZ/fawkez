/*
 * $Id: ReportReaderFactoryTest.java 1450 2009-05-09 22:54:06Z amandel $
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

import java.text.ParseException;

import org.jcoderz.commons.types.Date;

import junit.framework.TestCase;

/**
 * Test part of the {@link Java2Html} class.
 * @author amandel
 */
public class Java2HtmlTest
    extends TestCase
{
    private static final Date END_TEST_WEEK;
    private static final Date START_TEST_WEEK;

    static
    {
        try
        {
            START_TEST_WEEK = Date.fromString("2009-05-18T00:00:00.000Z");
            END_TEST_WEEK = Date.fromString("2009-05-25T00:00:00.000Z");
        }
        catch (ParseException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Test method for {@link Java2Html#getPeriodStart(ReportInterval, Date)}.
     * @throws ParseException in case of an internal error.
     */
    public void testGetPeriodStart ()
        throws ParseException
    {
        assertEquals("Unexpected start for week.",
            START_TEST_WEEK,
            Java2Html.getPeriodStart(ReportInterval.WEEK,
                Date.fromString("2009-05-22T20:20:20.200Z")));
        assertEquals("Unexpected start for week.",
            START_TEST_WEEK,
            Java2Html.getPeriodStart(ReportInterval.WEEK,
                Date.fromString("2009-05-21T20:20:20.200Z")));
        assertEquals("Unexpected start for week.",
            START_TEST_WEEK,
            Java2Html.getPeriodStart(ReportInterval.WEEK,
                Date.fromString("2009-05-20T20:20:20.200Z")));
        assertEquals("Unexpected start for week.",
            START_TEST_WEEK,
            Java2Html.getPeriodStart(ReportInterval.WEEK,
                Date.fromString("2009-05-19T20:20:20.200Z")));
        assertEquals("Unexpected start for week.",
            START_TEST_WEEK,
            Java2Html.getPeriodStart(ReportInterval.WEEK,
                Date.fromString("2009-05-18T20:20:20.200Z")));
    }

    /**
     * Test method for {@link Java2Html#getPeriodEnd(ReportInterval, Date)}.
     * @throws ParseException in case of an internal error.
     */
    public void testGetPeriodEnd ()
        throws ParseException
    {
        assertEquals("Unexpected end for week.",
            END_TEST_WEEK,
            Java2Html.getPeriodEnd(ReportInterval.WEEK,
                Date.fromString("2009-05-22T20:20:20.200Z")));
        assertEquals("Unexpected end for week.",
            END_TEST_WEEK,
            Java2Html.getPeriodEnd(ReportInterval.WEEK,
                Date.fromString("2009-05-23T20:20:20.200Z")));
        assertEquals("Unexpected end for week.",
            END_TEST_WEEK,
            Java2Html.getPeriodEnd(ReportInterval.WEEK,
                Date.fromString("2009-05-24T20:20:20.200Z")));
        assertEquals("Unexpected end for week.",
            Date.fromString("2009-06-01T00:00:00.000Z"),
            Java2Html.getPeriodEnd(ReportInterval.WEEK,
                Date.fromString("2009-05-25T20:20:20.200Z")));
        assertEquals("Unexpected end for week.",
            Date.fromString("2009-06-01T00:00:00.000Z"),
            Java2Html.getPeriodEnd(ReportInterval.WEEK,
                Date.fromString("2009-05-31T23:20:20.200Z")));
    }
}
