/*
 * $Id: YearMonthTest.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.types;

import junit.framework.TestCase;
import org.jcoderz.commons.ArgumentMalformedException;

/**
 * Tests the YearMonth class.
 * @author Andreas Mandel
 */
public class YearMonthTest
      extends TestCase
{

   /** Tests the fromString method (positive).  */
   public final void testFromStringPositive ()
   {
      testGood("2005-02Z", "2005-02Z");
      testGood("2005-03UTC", "2005-03Z");
      testGood("2005-04GMT", "2005-04Z");
      testGood("-2005-02", "-2005-02Z");
      testGood("2005-12", "2005-12Z");
      testGood("12005-01", "12005-01Z");
      testGood("0005-12", "0005-12Z");
      testGood("-0005-12", "-0005-12Z"); //
   }

   /** Tests the fromString method (negative).  */
   public final void testFromStringNegative ()
   {
      testBad(null);
      testBad("");
      testBad("2005");
      testBad("5-02");
      testBad("2005-2");
      testBad("2005-2Z");
      testBad("2005--2");
      testBad("2005-02PST");
      testBad("2005--02");
//      testBad("2005-+2");
      testBad("0000-01");
   }

   /**
    * Test the to period method.
    * @throws Exception in case of a testcase error.
    */
   public final void testToPeriod ()
         throws Exception
   {
      final YearMonth ym = YearMonth.fromString("1999-04");
      final Period p = ym.toPeriod();
      assertEquals("Start date of period unexpected",
            Date.fromString("1999-04-01T00:00:00Z"), p.getStartTime());
      assertEquals("End date of period unexpected",
            Date.fromString("1999-04-30T23:59:59.999Z"), p.getEndTime());

      final YearMonth ym2 = YearMonth.fromString("2000-02");
      final Period p2 = ym2.toPeriod();
      assertEquals("Start date of period unexpected (leep year)",
            Date.fromString("2000-02-01T00:00:00Z"), p2.getStartTime());
      assertEquals("End date of period unexpected (leep year)",
            Date.fromString("2000-02-29T23:59:59.999Z"), p2.getEndTime());
   }

   private void testGood (String str, String ref)
   {
      assertEquals("String representation unexpected.",
            ref, YearMonth.fromString(str).toString());
   }

   private void testBad (String str)
   {
      try
      {
         YearMonth.fromString(str);
         fail("YearMonth shold not be valid: '" + str + "'.");
      }
      catch (ArgumentMalformedException ex)
      {
         // OK
      }
   }

}
