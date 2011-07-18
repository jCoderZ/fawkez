/*
 * $Id: NumberUtilTest.java 1149 2008-09-09 19:16:09Z amandel $
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
package org.jcoderz.commons.util;

import junit.framework.TestCase;

public class NumberUtilTest
    extends TestCase
{
    public void testCountDigits ()
    {
        checkNumberOfDigits(12345);
    }

    public void testCountDigitsNegative ()
    {
        checkNumberOfDigits(-12345);
    }

    public void testCountDigitsZero ()
    {
        assertEquals("Wrong number-of digits for '" + 0 + "'.",
            1, NumberUtil.countDigits(1));
    }

    public void testCountDigitsHigh ()
    {
        checkNumberOfDigits(123452434);
    }

    public void testToStringLongInt ()
    {
       checkToString(1000, 1, "100.0");
    }

    public void testToStringLongIntZeroScale ()
    {
       checkToString(1000, 0, "1000");
    }

    public void testToStringLongIntScale2 ()
    {
       checkToString(10, 2, "0.10");
    }

    public void testToStringLongIntScale3 ()
    {
       checkToString(10, 3, "0.010");
    }

    public void testToStringLongIntHighScale ()
    {
       checkToString(10, 5, "0.00010");
    }

    public void testToStringLongIntHighScaleAndValue ()
    {
       checkToString(987654321, 5, "9876.54321");
    }

    public void testToStringLongIntErrorPattern ()
    {
       checkToString(58000, 5, "0.58000");
    }

    public void testToStringLongIntNegative ()
    {
       checkToString(-58000, 5, "-0.58000");
    }

    public void testToStringLongIntErrorPatternZero ()
    {
       checkToString(0, 5, "0.00000");
    }

    private void checkNumberOfDigits (long test)
    {
        final int result = 1 + (int) Math.log10(Math.abs(test));
        assertEquals("Wrong number-of digits for '" + test + "'.",
            result, NumberUtil.countDigits(test));
    }

    private void checkToString (int test, int scale, String string)
    {
        assertEquals("Wrong string digits for " + test + " with" +
                "scale " + scale + " .",
            string, NumberUtil.toString(test, scale));
    }
}
