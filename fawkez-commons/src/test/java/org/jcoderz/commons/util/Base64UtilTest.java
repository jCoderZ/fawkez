/*
 * $Id: Base64UtilTest.java 1011 2008-06-16 17:57:36Z amandel $
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
import org.jcoderz.commons.ArgumentMalformedException;


/**
 * JUnit Test for the class {@link org.jcoderz.commons.util.Base64Util}.
 *
 * @author Andreas Mandel
 * @author Michael Griffel
 */
public class Base64UtilTest
      extends TestCase
{
   private static final int TESTDATA_LENGTH = 4097;
   private static final byte[] TESTDATA;

   private static final String TEST_BASE_64_ENCODED_1 = "dGVzdA==";
   private static final String TEST_BASE_64_DECODED_1 = "test";
   private static final String TEST_BASE_64_ENCODED_2
         = "VGhpcyBpcyBhIGxvbmcgdGVzdCBtZXNzYWdlLiBVc2VkIHRvIHRlc3QgdGhlIHRl"
            + "c3Qu";
   private static final String TEST_BASE_64_DECODED_2
         = "This is a long test message. Used to test the test.";
   private static final int PERFORMANCE_LOOP_COUNT = 10000;
   static
   {
      TESTDATA = new byte[TESTDATA_LENGTH];
      new Random().nextBytes(TESTDATA);
   }

   /** Test short string. */
   public void testString1 ()
   {
      assertEquals("Short test string.", TEST_BASE_64_DECODED_1,
            StringUtil.asciiToString(
               Base64Util.decode(TEST_BASE_64_ENCODED_1)));
   }

   /** Test long string. */
   public void testString2 ()
   {
      assertEquals("Long test string.", TEST_BASE_64_DECODED_2,
            StringUtil.asciiToString(
               Base64Util.decode(TEST_BASE_64_ENCODED_2)));
   }

   /** Test encode decode sequence. */
   public void testEncodeDecodeBinary ()
   {
      checkEncodeDecode(TESTDATA);
      byte[] testdata;
      testdata = new byte[TESTDATA.length - 1];
      System.arraycopy(TESTDATA, 0, testdata, 0, testdata.length);
      checkEncodeDecode(testdata);
      testdata = new byte[testdata.length - 1];
      System.arraycopy(TESTDATA, 0, testdata, 0, testdata.length);
      checkEncodeDecode(testdata);
      testdata = new byte[testdata.length - 1];
      System.arraycopy(TESTDATA, 0, testdata, 0, testdata.length);
      checkEncodeDecode(testdata);
      testdata = new byte[testdata.length - 1];
      System.arraycopy(TESTDATA, 0, testdata, 0, testdata.length);
      checkEncodeDecode(testdata);
      testdata = new byte[testdata.length - 1];
      System.arraycopy(TESTDATA, 0, testdata, 0, testdata.length);
      checkEncodeDecode(testdata);
   }

   /**
    * Encoding performance test.
    */
   public void xxxtestEncodePerformanceRef ()
   {
      final long start = System.currentTimeMillis();
      for (int i = 0; i < PERFORMANCE_LOOP_COUNT; ++i)
      {
//       TOOD: ref impl: Base64.encodeToString(TESTDATA, false);
      }
      final long diff = System.currentTimeMillis() - start;

      System.out.println("Base64 encoding (ref) "
            + (PERFORMANCE_LOOP_COUNT * TESTDATA.length) / diff + "kB/sec");
   }

   /**
    * Encoding performance test.
    */
   public void xxxtestEncodePerformance ()
   {
      final long start = System.currentTimeMillis();
      for (int i = 0; i < PERFORMANCE_LOOP_COUNT; ++i)
      {
         Base64Util.encode(TESTDATA);
      }
      final long diff = System.currentTimeMillis() - start;

      System.out.println("Base64 encoding (enc) "
            + (PERFORMANCE_LOOP_COUNT * TESTDATA.length) / diff + "kB/sec");
   }

   /**
    * Encoding performance test.
    */
   public void xxxtestEncodePerformanceRef2 ()
   {
      final long start = System.currentTimeMillis();
      for (int i = 0; i < PERFORMANCE_LOOP_COUNT; ++i)
      {
         // TOOD: ref impl: Base64.encodeToString(TESTDATA, false);
      }
      final long diff = System.currentTimeMillis() - start;

      System.out.println("Base64 encoding (ref2) "
            + (PERFORMANCE_LOOP_COUNT * TESTDATA.length) / diff + "kB/sec");
   }

   /**
    * Encoding performance test.
    */
   public void xxxtestEncodePerformance2 ()
   {
      final long start = System.currentTimeMillis();
      for (int i = 0; i < PERFORMANCE_LOOP_COUNT; ++i)
      {
         Base64Util.encode(TESTDATA);
      }
      final long diff = System.currentTimeMillis() - start;

      System.out.println("Base64 encoding (enc2) "
            + (PERFORMANCE_LOOP_COUNT * TESTDATA.length) / diff + "kB/sec");
   }
   /**
    * Decoding performance test.
    */
   public void xxxtestDecodePerformance ()
   {
      final long start = System.currentTimeMillis();

      final String TEST_ENCODE = Base64Util.encode(TESTDATA);
      for (int i = 0; i < PERFORMANCE_LOOP_COUNT; ++i)
      {
         Base64Util.decode(TEST_ENCODE);
      }
      final long diff = System.currentTimeMillis() - start;

      System.out.println("Base64 decoding "
            + (PERFORMANCE_LOOP_COUNT * TEST_ENCODE.length()) / diff
            + "kB/sec");
   }

   /**
    * Test vectors for encoding. Taken from the Apache commons project.
    */
   public void testKnownEncodings ()
   {
      assertEquals("test encoding with text",
            "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBk"
               + "b2dzLg==",
            Base64Util.encode("The quick brown fox jumped over the lazy dogs."
                  .getBytes()));
      assertEquals("test encoding with text",
            "SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0"
               + "IG9mIHRpbWVzLg==",
            Base64Util.encode(
               "It was the best of times, it was the worst of times."
                  .getBytes()));
      assertEquals("test encoding with URL",
            "aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==",
            Base64Util.encode("http://jakarta.apache.org/commmons".getBytes()));

      assertEquals("test encoding with all letters",
            "QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1"
               + "VnZXd1h4WXlaeg==",
            Base64Util.encode(
               "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
                  .getBytes()));
      assertEquals("test encoding with digits",
            "eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=",
            Base64Util.encode("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".getBytes()));
      assertEquals("eHl6enkh",
            Base64Util.encode("xyzzy!".getBytes()));
   }

   /**
    * Test vectors for decoding. Taken from the Apache commons project.
    */
   public void testKnownDecodings ()
   {
      assertEquals("test decoding with text",
            "The quick brown fox jumped over the lazy dogs.",
            StringUtil.asciiToString(Base64Util.decode(
               "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg"
               + "==")));
      assertEquals("test decoding with text",
            "It was the best of times, it was the worst of times.",
            StringUtil.asciiToString(Base64Util.decode(
               "SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIH"
               + "RpbWVzLg==")));
      assertEquals("test decoding with URL",
            "http://jakarta.apache.org/commmons",
            StringUtil.asciiToString(Base64Util.decode(
               "aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==")));

      assertEquals("test decoding with letters",
            "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz",
            StringUtil.asciiToString(Base64Util.decode(
               "QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1"
               + "h4WXlaeg==")));
      assertEquals("test decoding with digits",
            "{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }",
            StringUtil.asciiToString(Base64Util.decode(
               "eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=")));
      assertEquals("test decoding with another test vector",
            "xyzzy!",
            StringUtil.asciiToString(Base64Util.decode("eHl6enkh")));
   }

   /**
    * Test vectors for encoding. Taken from the Apache commons project.
    */
   public void testSingletons ()
   {
      final String[] testVectors = new String[]{
         "AA==", "AQ==", "Ag==",
         "Aw==", "BA==", "BQ==", "Bg==", "Bw==", "CA==", "CQ==", "Cg==",
         "Cw==", "DA==", "DQ==", "Dg==", "Dw==", "EA==", "EQ==", "Eg==",
         "Ew==", "FA==", "FQ==", "Fg==", "Fw==", "GA==", "GQ==", "Gg==",
         "Gw==", "HA==", "HQ==", "Hg==", "Hw==", "IA==", "IQ==", "Ig==",
         "Iw==", "JA==", "JQ==", "Jg==", "Jw==", "KA==", "KQ==", "Kg==",
         "Kw==", "LA==", "LQ==", "Lg==", "Lw==", "MA==", "MQ==", "Mg==",
         "Mw==", "NA==", "NQ==", "Ng==", "Nw==", "OA==", "OQ==", "Og==",
         "Ow==", "PA==", "PQ==", "Pg==", "Pw==", "QA==", "QQ==", "Qg==",
         "Qw==", "RA==", "RQ==", "Rg==", "Rw==", "SA==", "SQ==", "Sg==",
         "Sw==", "TA==", "TQ==", "Tg==", "Tw==", "UA==", "UQ==", "Ug==",
         "Uw==", "VA==", "VQ==", "Vg==", "Vw==", "WA==", "WQ==", "Wg==",
         "Ww==", "XA==", "XQ==", "Xg==", "Xw==", "YA==", "YQ==", "Yg==",
         "Yw==", "ZA==", "ZQ==", "Zg==", "Zw==", "aA=="
      };
      for (int i = 0; i < testVectors.length; i++)
      {
         assertEquals("single byte decoding test vector[" + i + "]",
               testVectors[i], Base64Util.encode(new byte[] {(byte) i}));
      }
   }

   /**
    * Test vectors with three bytes that tests the Base64 encoding.
    */
   public void testTriplets ()
   {
      final String[] testVectors = new String[]{
         "AAAA", "AAAB", "AAAC", "AAAD",
         "AAAE", "AAAF", "AAAG", "AAAH", "AAAI", "AAAJ", "AAAK", "AAAL",
         "AAAM", "AAAN", "AAAO", "AAAP", "AAAQ", "AAAR", "AAAS", "AAAT",
         "AAAU", "AAAV", "AAAW", "AAAX", "AAAY", "AAAZ", "AAAa", "AAAb",
         "AAAc", "AAAd", "AAAe", "AAAf", "AAAg", "AAAh", "AAAi", "AAAj",
         "AAAk", "AAAl", "AAAm", "AAAn", "AAAo", "AAAp", "AAAq", "AAAr",
         "AAAs", "AAAt", "AAAu", "AAAv", "AAAw", "AAAx", "AAAy", "AAAz",
         "AAA0", "AAA1", "AAA2", "AAA3", "AAA4", "AAA5", "AAA6", "AAA7",
         "AAA8", "AAA9", "AAA+", "AAA/"
      };
      for (int i = 0; i < testVectors.length; i++)
      {
         assertEquals("three byte encoding vector[" + i + "]",
               testVectors[i],
               Base64Util.encode(new byte[] {(byte) 0, (byte) 0, (byte) i}));
      }
   }

   public void testDecodeWithMalformedData ()
   {
      decodeWithMalformedDataTest(null);
      decodeWithMalformedDataTest("");
      decodeWithMalformedDataTest("a");
      decodeWithMalformedDataTest("ab");
      decodeWithMalformedDataTest("abc");
      decodeWithMalformedDataTest("====");
      decodeWithMalformedDataTest("3c=r");
      decodeWithMalformedDataTest("dGVzd===");
      decodeWithMalformedDataTest("3c==");
      decodeWithMalformedDataTest("dGVzd!==");
      decodeWithMalformedDataTest("3cd$");
      decodeWithMalformedDataTest("3c$d");
      decodeWithMalformedDataTest("3c$$");
   }

   private void decodeWithMalformedDataTest (String s)
   {
      try
      {
         final byte[] data = Base64Util.decode(s);
         fail("Expected ArgumentMalformedException for the malformed "
               + "base64 data : '" + s + "' result is "
               + HexUtil.bytesToHex(data));
      }
      catch (ArgumentMalformedException x)
      {
         // expected
      }
   }

   private void checkEncodeDecode (byte[] data)
   {
      assertTrue("Encode decode should be idempotent (" + data.length + ")",
            Arrays.equals(data, Base64Util.decode(Base64Util.encode(data))));
   }
}

