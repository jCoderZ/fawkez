/*
 * $Id: XsdUtilTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.math.BigInteger;
import junit.framework.TestCase;

/**
 * Tests the XsdUtil class.
 * @author Andreas Mandel
 */
public class XsdUtilTest
      extends TestCase
{
   /** Tests the integerFromString() method. */
   public final void testIntegerFromStringGood ()
   {
      testIntegerFromStringGood("0", BigInteger.ZERO);
      testIntegerFromStringGood("1", BigInteger.ONE);
      testIntegerFromStringGood("+0", BigInteger.ZERO);
      testIntegerFromStringGood("+1", BigInteger.ONE);
      testIntegerFromStringGood("-0", BigInteger.ZERO);
      testIntegerFromStringGood("-1", BigInteger.valueOf(-1));
      testIntegerFromStringGood("-01", BigInteger.valueOf(-1));
      testIntegerFromStringGood("00", BigInteger.ZERO);
      testIntegerFromStringGood("01", BigInteger.ONE);
      testIntegerFromStringGood("2147483647",
            BigInteger.valueOf(Integer.MAX_VALUE));
      testIntegerFromStringGood("-2147483648",
            BigInteger.valueOf(Integer.MIN_VALUE));
      testIntegerFromStringGood("9223372036854775807",
            BigInteger.valueOf(Long.MAX_VALUE));
      testIntegerFromStringGood("-9223372036854775808",
            BigInteger.valueOf(Long.MIN_VALUE));
   }

   /** Tests the integerFromString() method. */
   public final void testIntegerFromStringBad ()
   {
      testIntegerFromStringBad("++0");
      testIntegerFromStringBad(" +0");
      testIntegerFromStringBad("--0");
      testIntegerFromStringBad("-+0");
      testIntegerFromStringBad("+-0");
      testIntegerFromStringBad("FOO");
      testIntegerFromStringBad(null);
      testIntegerFromStringBad("0.1");
      testIntegerFromStringBad("");
      testIntegerFromStringBad("123E123");
   }

   /** Tests the intFromString() method. */
   public final void testIntFromStringGood ()
   {
      testIntFromStringGood("0", 0);
      testIntFromStringGood("1", 1);
      testIntFromStringGood("+0", 0);
      testIntFromStringGood("+1", 1);
      testIntFromStringGood("-0", 0);
      testIntFromStringGood("-1", -1);
      testIntFromStringGood("-01", -1);
      testIntFromStringGood("00", 0);
      testIntFromStringGood("01", 1);
      testIntFromStringGood("2147483647", Integer.MAX_VALUE);
      testIntFromStringGood("-2147483648", Integer.MIN_VALUE);
   }

   /** Tests the intFromString() method. */
   public final void testIntFromStringBad ()
   {
      testIntFromStringBad("++0");
      testIntFromStringBad(" +0");
      testIntFromStringBad("--0");
      testIntFromStringBad("-+0");
      testIntFromStringBad("+-0");
      testIntFromStringBad("FOO");
      testIntFromStringBad(null);
      testIntFromStringBad("0.1");
      testIntFromStringBad("");
      testIntFromStringBad("123E123");
      testIntFromStringBad("9223372036854775807");
      testIntFromStringBad("-9223372036854775808");
   }

   /** Tests the longFromString() method. */
   public final void testLongFromString ()
   {
      testLongFromStringGood("0", 0);
      testLongFromStringGood("1", 1);
      testLongFromStringGood("+0", 0);
      testLongFromStringGood("+1", 1);
      testLongFromStringGood("-0", 0);
      testLongFromStringGood("-1", -1);
      testLongFromStringGood("-01", -1);
      testLongFromStringGood("00", 0);
      testLongFromStringGood("01", 1);
      testLongFromStringGood("2147483647", Integer.MAX_VALUE);
      testLongFromStringGood("-2147483648", Integer.MIN_VALUE);
      testLongFromStringGood("9223372036854775807", Long.MAX_VALUE);
      testLongFromStringGood("-9223372036854775808", Long.MIN_VALUE);
   }

   /** Tests the longFromString() method. */
   public final void testLongFromStringBad ()
   {
      testLongFromStringBad("++0");
      testLongFromStringBad(" +0");
      testLongFromStringBad("--0");
      testLongFromStringBad("-+0");
      testLongFromStringBad("+-0");
      testLongFromStringBad("FOO");
      testLongFromStringBad(null);
      testLongFromStringBad("0.1");
      testLongFromStringBad("");
      testLongFromStringBad("123E123");
   }

   /**
    * Special test with no xs:token violations.
    */
   public void testIsValidSchemaToken ()
   {
      checkValidSchemaToken("XXXX");
      checkValidSchemaToken("XXX XXXX");
      checkValidSchemaToken("X X");
      checkValidSchemaToken("");
   }

   /**
    * Special test with xs:token violations.
    */
   public void testIsValidSchemaTokenFalse ()
   {
      checkBrokenXmlSchemaToken(" ");
      checkBrokenXmlSchemaToken(" X");
      checkBrokenXmlSchemaToken("X ");
      checkBrokenXmlSchemaToken("X  ");
      checkBrokenXmlSchemaToken("X  X");
      checkBrokenXmlSchemaToken("X\tX");
      checkBrokenXmlSchemaToken("X\nX");
      checkBrokenXmlSchemaToken("X\rX");
      checkBrokenXmlSchemaToken("X\r  X");
      checkBrokenXmlSchemaToken(" X\r");
      checkBrokenXmlSchemaToken(null);
   }

   private void checkBrokenXmlSchemaToken (String pattern)
   {
      if (XsdUtil.isValidToken(pattern))
      {
         fail("Pattern is invalid and should return false '"
               + pattern + "'.");
      }
   }

   private void checkValidSchemaToken (String pattern)
   {
      if (!XsdUtil.isValidToken(pattern))
      {
         fail("Pattern is valid and should return true '"
               + pattern + "'.");
      }
   }

   private void testIntegerFromStringGood (String str, BigInteger i)
   {
      assertEquals("From String value unexpected", i,
            XsdUtil.integerFromString(str));
   }

   private void testIntegerFromStringBad (String str)
   {
      try
      {
         XsdUtil.integerFromString(str);
         fail("String representation should fail for: '" + str + "'");
      }
      catch (NumberFormatException ex)
      {
         // OK
      }
      catch (NullPointerException ex)
      {
         // OK
      }
   }

   private void testIntFromStringGood (String str, int i)
   {
      assertEquals("From String value unexpected", i,
            XsdUtil.intFromString(str));
   }

   private void testIntFromStringBad (String str)
   {
      try
      {
         XsdUtil.intFromString(str);
         fail("String representation should fail for: '" + str + "'");
      }
      catch (NumberFormatException ex)
      {
         // OK
      }
      catch (NullPointerException ex)
      {
         // OK
      }
   }

   private void testLongFromStringGood (String str, long i)
   {
      assertEquals("From String value unexpected", i,
            XsdUtil.longFromString(str));
   }

   private void testLongFromStringBad (String str)
   {
      try
      {
         XsdUtil.longFromString(str);
         fail("String representation of long should fail for: '" + str + "'");
      }
      catch (NumberFormatException ex)
      {
         // OK
      }
      catch (NullPointerException ex)
      {
         // OK
      }
   }

}
