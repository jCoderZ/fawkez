/*
 * $Id: StrongTypesTest.java 1547 2009-08-03 20:42:16Z amandel $
 *
 * Copyright 2008, The jCoderZ.org Project. All rights reserved.
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
package org.jcoderz.commons.types;

import junit.framework.TestCase;

import org.jcoderz.commons.ArgumentMaxLengthViolationException;
import org.jcoderz.commons.ArgumentMaxValueViolationException;
import org.jcoderz.commons.ArgumentMinLengthViolationException;
import org.jcoderz.commons.ArgumentMinValueViolationException;
import org.jcoderz.commons.config.ConfigurationKey;
import org.jcoderz.commons.test.RestrictedLong;

/**
 * Test the generated Strong Types.
 * @author Andreas Mandel
 */
public class StrongTypesTest
    extends TestCase
{
    private static final int LONG_STRING_LENGTH = 2048;
    private static final String LONG_STRING;

    static
    {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < LONG_STRING_LENGTH; i++)
        {
           sb.append(' ');
        }
        LONG_STRING = sb.toString();
    }

    /**
     * Testing the max length restriction in generated String types.
     */
    public void testToLong ()
    {
        try
        {
            final ConfigurationKey key
                = ConfigurationKey.fromString(LONG_STRING.substring(0,
                    ConfigurationKey.MAX_LENGTH + 1));
            fail("Expected exception!");
        }
        catch (ArgumentMaxLengthViolationException ex)
        {
            // expected;
        }
    }

    /**
     * Testing the min length restriction in generated String types.
     */
    public void testToShort ()
    {
        try
        {
            final ConfigurationKey key
                = ConfigurationKey.fromString("");
            fail("Expected exception!");
        }
        catch (ArgumentMinLengthViolationException ex)
        {
            // expected;
        }
    }

    /**
     * Testing the max value restriction in generated Long types.
     */
    public void testToHigh ()
    {
        try
        {
            final RestrictedLong lg
                = RestrictedLong.fromLong(RestrictedLong.MAX_VALUE + 1);
            fail("Expected exception!");
        }
        catch (ArgumentMaxValueViolationException ex)
        {
            // expected;
        }
    }

    /**
     * Testing the min value restriction in generated Long types.
     */
    public void testToLow ()
    {
        try
        {
            final RestrictedLong lg
                = RestrictedLong.fromLong(RestrictedLong.MIN_VALUE - 1);
            fail("Expected exception!");
        }
        catch (ArgumentMinValueViolationException ex)
        {
            // expected;
        }
    }

    /**
     * Test method for comparison.
     */
    public void testComparison ()
    {
        assertEquals("Comparing equal values", 0,
            RestrictedLong.fromString("15")
            .compareTo(RestrictedLong.fromString("15")));
        assertTrue("Comparing different values",
            RestrictedLong.fromString("15")
                .compareTo(RestrictedLong.fromString("21")) < 0);
        assertTrue("Comparing different values",
            RestrictedLong.fromString("21")
                .compareTo(RestrictedLong.fromString("15")) > 0);
    }

    /** Test for enumeration implements tag. */
    public void testImplementsTaggedColor ()
    {
        assertTrue("TaggedColor should implement Tagger interface", 
            TestTaggerInterface.class.isAssignableFrom(TaggedColor.class)); 
    }
    
    /** Test for restricted string implements tag. */
    public void testImplementsTaggedFooString ()
    {
        assertTrue("TaggedFooString should implement Tagger interface", 
            TestTaggerInterface.class.isAssignableFrom(TaggedFooString.class)); 
    }

    /** Test for value objects implements tag. */
    public void testImplementsTaggedValueObject ()
    {
        assertTrue(
            "TaggedSampleValueObject should implement Tagger interface", 
            TestTaggerInterface.class.isAssignableFrom(
                TaggedSampleValueObject.class)); 
        assertTrue(
            "TaggedPlainSampleValueObject should implement Tagger interface", 
            TestTaggerInterface.class.isAssignableFrom(
                TaggedPlainSampleValueObject.class)); 
        assertTrue(
            "TaggedSerializableSampleValueObject " +
            "should implement Tagger interface", 
            TestTaggerInterface.class.isAssignableFrom(
                TaggedSerializableSampleValueObject.class)); 
    }
}
