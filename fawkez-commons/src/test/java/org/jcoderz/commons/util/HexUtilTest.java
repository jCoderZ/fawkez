/*
 * $Id: HexUtilTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.Arrays;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests the HexUtil class.
 * @author Albrecht Messner
 */
public class HexUtilTest
      extends TestCase
{
   private static final int CHARS_PER_BYTE = 2;
   private static final int BYTE_UNSIGNED_MAX = 255;
   private static final Random RANDOM = new Random();
   private static final int TEN_K = 10240;
   private static final int DUMP_TEST_LENGTH = 16;

   /**
    * Main method to run test stand-alone.
    * @param args command line arguments
    */
   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(HexUtilTest.class);
   }

   /**
    * Tests the bytesToHex method of the HexUtil class.
    */
   public void testBytesToHex ()
   {
      for (int i = 0; i <= BYTE_UNSIGNED_MAX; i++)
      {
         final byte[] b = new byte[1];
         b[0] = (byte) i;
         final String s = HexUtil.bytesToHex(b, 0, b.length);
         assertEquals("Hex dump of a single byte must yield 2 chars",
               s.length(), CHARS_PER_BYTE);
      }
   }

   /**
    * Tests boundaries.
    */
   public void testVariousBytes ()
   {
      final byte[] testMin = {Byte.MIN_VALUE};
      final String minResult = "80";
      testB2H(testMin, minResult);

      final byte[] testMax = {Byte.MAX_VALUE};
      final String maxResult = "7F";
      testB2H(testMax, maxResult);

      final byte[] testNull = {0};
      final String nullResult = "00";
      testB2H(testNull, nullResult);

      final byte[] testMinusOne = {-1};
      final String minusOneResult = "FF";
      testB2H(testMinusOne, minusOneResult);
   }

   /**
    * Perform a cross check with the Integer.toHexString functionality.
    */
   public void testCrossCheck ()
   {
      for (int i = 0; i <= BYTE_UNSIGNED_MAX; i++)
      {
         String hex1 = Integer.toHexString(i);
         if (hex1.length() == 1)
         {
            hex1 = "0" + hex1;
         }
         hex1 = hex1.toUpperCase(Constants.SYSTEM_LOCALE);
         final byte[] b = new byte[1];
         b[0] = (byte) i;
         final String hex2 = HexUtil.bytesToHex(b, 0, b.length);
         assertEquals(
               "HexUtil must return same result as Integer.toHexString",
               hex1, hex2);
      }
   }

   /**
    * Test empty and null byte array.
    */
   public void testNullAndEmpty ()
   {
      final byte[] b = null;
      final String s = HexUtil.bytesToHex(b, 0, 0);
      assertNull("HexUtil must return null for null argument", s);

      final byte[] b2 = new byte[0];
      final String s2 = HexUtil.bytesToHex(b2, 0, 0);
      assertEquals(
            "HexUtil must return an empty string for a 0-byte long argument",
            s2.length(), 0);
   }

   /**
    * Tests a couple of valid hex strings.
    */
   public void testValidStrings ()
   {
      final String[] hex = {"00", "FF", "7F", "80"};
      final byte[] bin = {0, -1, Byte.MAX_VALUE, Byte.MIN_VALUE};
      for (int i = 0; i < hex.length; i++)
      {
         final byte[] b = HexUtil.stringToBytes(hex[i]);
         assertEquals("Expected exactly one byte", b.length, 1);
         assertEquals("Expected correct result", bin[i], b[0]);
      }
   }

   /**
    * Tests a couple of invalid hex strings.
    */
   public void testInvalidStrings ()
   {
      final String[] badHex = {"AX", "A", "BAR", "DROP", 
              "\u00ef\u00bf\u00bd\u00ef\u00bf\u00bd", "?\u00ef\u00bf\u00bd"};
      for (int i = 0; i < badHex.length; i++)
      {
         try
         {
            HexUtil.stringToBytes(badHex[i]);
            fail("Method should throw exception for invalid hex string "
                  + badHex[i]);
         }
         catch (IllegalArgumentException x)
         {
            // expected
         }
      }
   }

   /**
    * Test HexUtil with 10k of random data.
    */
   public void testWithRandomData ()
   {
      final byte[] data = new byte[TEN_K];
      RANDOM.nextBytes(data);
      final String s = HexUtil.bytesToHex(data);
      final byte[] b = HexUtil.stringToBytes(s);
      assertTrue("Input data must equal output data",
            Arrays.equals(data, b));
   }

   /**
    * Test the hexdump method and prints out the result for visual test.
    */
   public void testHexDump ()
   {
      final byte[] b = new byte[BYTE_UNSIGNED_MAX - 1];
      for (int i = 0; i < b.length; i++)
      {
         b[i] = (byte) i;
      }
      final String dump = HexUtil.dump(b);
      System.out.println(dump);
   }

   /**
    * Test hexdump with sixteen bytes from 'a' to 'p'.
    */
   public void testHexDump2 ()
   {
      final String expectedResult
            = "00000000 61 62 63 64 65 66 67 68  69 6A 6B 6C 6D 6E 6F 70 "
            + "|abcdefghijklmnop|"
            + Constants.LINE_SEPARATOR;
      final byte[] data = new byte[DUMP_TEST_LENGTH];
      for (int i = 0; i < data.length; i++)
      {
         data[i] = (byte) (i + 'a');
      }
      final String dump = HexUtil.dump(data);
      assertEquals("Dump must equal expected format", expectedResult, dump);
   }

   /**
    * Test hexdump with a zero-length byte array.
    */
   public void testHexDumpWithEmptyArray ()
   {
      final byte[] b = new byte[0];
      final String dump = HexUtil.dump(b);
      assertEquals("Dump of empty byte array must yield empty string",
            dump.length(), 0);
   }

   /**
    * Test hexdump with a null byte array.
    */
   public void testHexDumpWithNull ()
   {
      final byte[] b = null;
      final String dump = HexUtil.dump(b);
      assertNull("Hexdump of null byte array must return null", dump);
   }

   private String testB2H (final byte[] b, final String expectedResult)
   {
      final String result = HexUtil.bytesToHex(b, 0, b.length);
      assertEquals("Invalid hex representation", result, expectedResult);
      return result;
   }
}
