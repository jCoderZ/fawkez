/*
 * $Id: StringUtilTest.java 1268 2009-01-21 15:03:56Z amandel $
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

import java.io.UnsupportedEncodingException;
import junit.framework.TestCase;
import org.jcoderz.commons.ArgumentMalformedException;

/**
 * Tests the StringUtil class.
 *
 * @author Andreas Mandel
 */
public class StringUtilTest
      extends TestCase
{
   private static final String FOO_STRING = "foo";
   private static final String NULL_SHOULD_PRODUCE_NULL
         = "Null should produce null.";
   private static final int MIN_LENGTH = 10;
   private static final int MAX_LENGTH = 20;

   /** testAsciiBytesToString with null argument. */
   public void testAsciiToStringNull ()
   {
      assertEquals(NULL_SHOULD_PRODUCE_NULL, null,
            StringUtil.asciiToString(null));
      assertEquals(NULL_SHOULD_PRODUCE_NULL, null,
            StringUtil.asciiToString(null, 0, 0));
   }

   /** testAsciiBytesToString with valid argument. */
   public void testAsciiToString ()
   {
      final String testString = "ABCDE";
      assertEquals("Teststring unexpected result.", testString,
            StringUtil.asciiToString(testString.getBytes()));
      assertNull("null byte[] should return a null string",
            StringUtil.asciiToString(null));
      assertEquals("Teststring unexpected result.", testString,
            StringUtil.asciiToString(
                  testString.getBytes(), 0, testString.length()));
      assertNull("null byte[] should return a null string",
            StringUtil.asciiToString(null, 0, 0));
   }

   /**
    * Tests the {@link StringUtil#asciiToString(byte[])}
    * with non ASCII characters.
    */
   public void testAsciiToStringWithNonAsciiChars ()
   {
      final String testString = "\u00c4\u00d6\u00dc";
      try
      {
         assertEquals("String should contain '?' only.",
               "???", StringUtil.asciiToString(
               testString.getBytes(Constants.ENCODING_ASCII)));
      }
      catch (UnsupportedEncodingException e)
      {
         fail("Ups, ASCII encoding not supported?" + e);
      }
   }

   /** testAsciiBytesToString with null argument. */
   public void testToStringNull ()
   {
      assertEquals(NULL_SHOULD_PRODUCE_NULL, null,
            StringUtil.toString(null));
      assertEquals(NULL_SHOULD_PRODUCE_NULL, null,
            StringUtil.toString(null, 0 , 0));
   }

   /** test bytesToString with valid argument. */
   public void testToString ()
   {
      final String testString = "ABCDE";
      assertEquals("Teststring unexpected result.", testString,
            StringUtil.toString(testString.getBytes()));
   }

   /** test umlauteBytesToString with valid argument. */
   public void testToStringUmlaute ()
   {
      final String testString = "ABCDE\u00c4\u00d6\u00dc\u00e4\u00f6\u00fc"
            + "\u00df\u00e1\u00b5";
      assertEquals("Teststring with umlauts unexpected result.", testString,
            StringUtil.toString(StringUtil.toBytes(testString)));
   }

   /**
    * Tests the method {@link StringUtil#isAscii(char)}.
    */
   public void testIsAscii ()
   {
      // positive tests
      assertTrue("test with valid ASCII char",
            StringUtil.isAscii('A'));
      assertTrue("test with valid ASCII string",
            StringUtil.isAscii("The brown fox."));
      // negative tests
      assertFalse("test with invalid ASCII char",
            StringUtil.isAscii('\u00dc'));
      assertFalse("test with invalid ASCII string",
            StringUtil.isAscii("The brown \u00e4fox."));
   }

   /**
    * Tests the method {@link StringUtil#isNullOrEmpty(String)}.
    */
   public void testIsNullOrEmpty ()
   {
      assertTrue("null string should be true", StringUtil.isNullOrEmpty(null));
      assertTrue("empty string should be true", StringUtil.isNullOrEmpty(""));
      assertFalse("any string should be false",
            StringUtil.isNullOrEmpty(FOO_STRING));
   }

   /**
    * Tests the method {@link StringUtil#isEmptyOrNull(String)}.
    */
   public void testIsEmptyOrNull ()
   {
      assertTrue("null string should be true", StringUtil.isEmptyOrNull(null));
      assertTrue("empty string should be true", StringUtil.isEmptyOrNull(""));
      assertFalse("any string should be false",
            StringUtil.isEmptyOrNull(FOO_STRING));
   }

   /**
    * Tests the method {@link StringUtil#isNullOrBlank(String)}.
    */
   public void testIsNullOrBlank ()
   {
      assertTrue("null string should be true", StringUtil.isNullOrBlank(null));
      assertTrue("empty string should be true", StringUtil.isNullOrBlank(""));
      assertTrue("tab string should be true", StringUtil.isNullOrBlank("\t"));
      assertTrue("whitespace string should be true", StringUtil.isNullOrBlank(" "));
      assertFalse("any string should be false",
            StringUtil.isNullOrBlank(FOO_STRING));
      assertFalse("any string should be false",
          StringUtil.isNullOrBlank(" " + FOO_STRING + " "));
      assertFalse("'x ' string should be false",
          StringUtil.isNullOrBlank("x "));
      assertFalse("' x' string should be false",
          StringUtil.isNullOrBlank(" x"));
   }

   /**
    * Tests the method {@link StringUtil#equals(String, String)}.
    */
   public void testEquals ()
   {
      // equals == true
      assertTrue("two null string references should be equal",
            StringUtil.equals(null, null));
      assertTrue("same string reference should be equals",
            StringUtil.equals(FOO_STRING, FOO_STRING));

      // equals == false
      assertFalse("string reference equals null reference should be false",
            StringUtil.equals(null, FOO_STRING));
      assertFalse("string reference equals null reference should be false",
            StringUtil.equals(FOO_STRING, null));
      assertFalse("different string reference should not be equals",
            StringUtil.equals(FOO_STRING, "bar"));
   }

   /**
    * Tests the method {@link StringUtil#fitToLength(String, int, int)}.
    * @throws Exception in case of an unexpected error.
    */
   public void testFitToLength ()
         throws Exception
   {
      final String smallStr = "xxx";
      final String mediumStr = "xxxxxxxxxxxxxxx";
      //                        123456789012345
      final String bigStr = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
      //                     1234567890123456789012345678901234567890
      //                              1         2         3         4

      assertEquals("Result should be " + MIN_LENGTH + " chars long", MIN_LENGTH,
            StringUtil.fitToLength(smallStr, MIN_LENGTH, MAX_LENGTH).length());
      assertEquals("Result should be " + MAX_LENGTH + " chars long", MAX_LENGTH,
            StringUtil.fitToLength(bigStr, MIN_LENGTH, MAX_LENGTH).length());
      assertEquals("String that fits between MIN_LENGTH and MAX_LENGTH "
            + "should be returned unmodified", mediumStr,
            StringUtil.fitToLength(mediumStr, MIN_LENGTH, MAX_LENGTH));

      // boundary tests
      StringUtil.fitToLength(smallStr, MIN_LENGTH, MIN_LENGTH);
      try
      {
         StringUtil.fitToLength(smallStr, MAX_LENGTH, MIN_LENGTH);
         fail("Should throw exception if minLength is bigger than maxLength");
      }
      catch (ArgumentMalformedException x)
      {
         // expected
      }
      try
      {
         StringUtil.fitToLength(null, MIN_LENGTH, MAX_LENGTH);
         fail("Should throw exception if string argument is null");
      }
      catch (ArgumentMalformedException x)
      {
         // expected
      }
   }

   /**
    * Tests the method {@link StringUtil#trimLengthLeft(String, int)}.
    */
   public void testTrimLeft ()
   {
      final String trimmed = StringUtil.trimLengthLeft("12345", 1);
      assertEquals("Unexpected string length.",
            1, trimmed.length());
      assertEquals("Unexpected result from trimLength", "5", trimmed);
   }

   /**
    * Tests the method {@link StringUtil#padLeft(String, char, int)}.
    */
   public void testPadLeft ()
   {
      final String paddedString = StringUtil.padLeft("", '0', MAX_LENGTH);
      assertEquals("Modified string length should be " + MAX_LENGTH,
            MAX_LENGTH, paddedString.length());
      assertTrue("Modified string should contain only zeros, but was "
            + paddedString, paddedString.matches("[0]{" + MAX_LENGTH + "}"));
   }

   /**
    * Tests the method {@link StringUtil#contains(String, String)}.
    */
   public void testContains ()
   {
      assertTrue("'aaa' is contained in 'bbbaaaa'.",
              StringUtil.contains("bbbbaaaa", "aaa"));
      assertFalse("'ccc' is not contained in 'bbbaaaa'.",
          StringUtil.contains("bbbbaaaa", "ccc"));
   }

}
