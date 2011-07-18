/*
 * $Id: NumberUtil.java 1149 2008-09-09 19:16:09Z amandel $
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


/**
 * Utility methods around Numbers.
 *
 * Mainly for BigDecimals, used by the restricted number
 * strong types.
 *
 * @author Andreas Mandel
 */
public final class NumberUtil
{
    private static final int LONG_MIN_VALUE_DIGITS = 19;
    private static final long[] DIGITS =
    {
        0L,
        10L,
        100L,
        1000L,
        10000L,
        100000L,
        1000000L,
        10000000L,
        100000000L,
        1000000000L,
        10000000000L,
        100000000000L,
        1000000000000L,
        10000000000000L,
        100000000000000L,
        1000000000000000L,
        10000000000000000L,
        100000000000000000L,
        1000000000000000000L
    };

    private static final char [] ZEROS
        = "000000000000000000".toCharArray();

    private NumberUtil ()
    {
        // no instances
    }

    /**
     * Counts the number of decimal digits needed to represent
     * the given value.
     * @param value the value to count for.
     * @return the number of decimal digits needed to represent
     * the given value.
     */
    public static int countDigits (long value)
    {
        int result;

        if (value == Long.MIN_VALUE)
        {
            result = LONG_MIN_VALUE_DIGITS;
        }
        else
        {
            final long test = Math.abs(value);
            for (result = 0; result < DIGITS.length; result++)
            {
                if (test < DIGITS[result])
                {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Convert the given unscaled long with the given scale to
     * it's string representation.
     * @param unscaledValue the scaled long.
     * @param scale the scale to be applied.
     * @return string representation of the scaled value using a dot
     *    '.' as decimal separator.
     */
    public static String toString (long unscaledValue, int scale)
    {
        Assert.assertTrue("Scale must not be negative.", scale >= 0);
        final StringBuffer sb = new StringBuffer();
        sb.append(Math.abs(unscaledValue));
        final int missingDigits = 1 + scale - sb.length();
        if (missingDigits > 0)
        {
            sb.insert(0, ZEROS, 0, 1 + scale - sb.length());
        }
        if (scale > 0)
        {
            sb.insert(sb.length() - scale, '.');
        }
        if (unscaledValue < 0)
        {
            sb.insert(0, '-');
        }
        return sb.toString();
    }
}
