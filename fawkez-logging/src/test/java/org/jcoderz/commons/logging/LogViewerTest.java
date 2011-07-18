/*
 * $Id: LogViewerTest.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.logging;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.types.Date;
import org.jcoderz.commons.types.Period;



/**
 *
 * Tests several static methods of the class
 * {@linkplain org.jcoderz.commons.logging.LogViewer}.
 *
 */
public class LogViewerTest
      extends TestCase
{
   /** The full qualified name of this class. */
   private static final String CLASSNAME = LogViewerTest.class.getName();
   /** The logger to use. */
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   /**
    * Tests the method
    * {@link LogViewer#getDateFromOptValue(String, boolean)}.
    */
   public void testGetDateFromOptValue ()
   {
      checkGetDateFromOptValueBad("wrong string");
      checkGetDateFromOptValueBad("2005");
      checkGetDateFromOptValueBad("2005-11");
      checkGetDateFromOptValueBad("2005/11/01");
      checkGetDateFromOptValueBad("2005-11-30T14:33:24.123");
      checkGetDateFromOptValueBad("2005-11-30T14:33:24");

      final String expected = "2005-11-30T00:00:00.000Z";
      checkGetDateFromOptValueGood("2005-11-30", expected);
      checkGetDateFromOptValueGood("2005-11-30Z", expected);
      String same = "2005-11-30T14:33:24Z";
      checkGetDateFromOptValueGood(same, same);
      same = "2005-11-30T14:33:24.123Z";
      checkGetDateFromOptValueGood(same, same);
   }

   /**
    * Tests the method {@link LogViewer#getDateFrom(String)}.
    */
   public void testGetDateFrom ()
   {
      try
      {
         assertDate(LogViewer.getDateFrom(null), Date.OLD_DATE);
         assertDate(LogViewer.getDateFrom(""), Date.OLD_DATE);
      }
      catch (ParseException e)
      {
         logErr("LogViewer.getDateFrom ", e);
      }
   }

   /**
    * Tests the method {@link LogViewer#getDateTo(String)}.
    */
   public void testGetDateTo ()
   {
      try
      {
         assertDate(LogViewer.getDateTo(null), Date.FUTURE_DATE);
         assertDate(LogViewer.getDateTo(""), Date.FUTURE_DATE);
      }
      catch (ParseException e)
      {
         logErr("LogViewer.getDateTo ", e);
      }
   }

   /**
    * Tests the method
    * {@linkplain LogViewer#getPeriodsFromOptionValues(String[])}.
    */
   public void testGetPeriodsFromOptionsValues ()
   {
      final String start = "2005-11-30T00:00:00.000Z";
      final String end = "2005-11-30T00:00:00.000Z";

      assertPeriod(start, end, start, end);
      assertPeriod(start, null, start, Date.FUTURE_DATE.toString());
      assertPeriod(start, "", start,  Date.FUTURE_DATE.toString());
      assertPeriod(null, end, Date.OLD_DATE.toString(), end);
      assertPeriod("", end, Date.OLD_DATE.toString(), end);
   }

   private void assertPeriod (String start, String end, String expectedStart,
         String expectedEndend)
   {
      final StringBuffer  sb = new StringBuffer();
      if (start != null)
      {
         sb.append(start);
      }
      sb.append(',');
      if (end != null)
      {
         sb.append(end);
      }
      final String [] values = {sb.toString()};

      try
      {
         final Period [] expectedPeriod = new Period [] {
               Period.createPeriod(Date.fromString(expectedStart),
                     Date.fromString(expectedEndend))};
         final Period [] p = LogViewer.getPeriodsFromOptionValues(values);
         Assert.assertNotNull("LogViewer.getPeriodsFromOptionValues returned null", p);
         Assert.assertEquals("Period array has an unexpected length", p.length,
               expectedPeriod.length);
         Assert.assertEquals("Got unexcpected period", p[0], expectedPeriod[0]);
      }
      catch (ArgumentMalformedException e)
      {
         logErr(errMsgGetPeriodsFromOptionsValues(values[0]), e);
      }
      catch (ParseException e)
      {
         logErr(errMsgGetPeriodsFromOptionsValues(values[0]), e);
      }
   }

   private void assertDate (Date result, Date expected)
   {
      assertNotNull("Returned value must not be null", result);
      Assert.assertEquals("Unexpected date", result, expected);
   }

   private void checkGetDateFromOptValueBad (String str)
   {
      try
      {
         LogViewer.getDateFromOptValue(str, true);
         fail(errMsgGetDateFromOptValue(str) + "must throw the ParseException");
      }
      catch (ParseException e)
      {
         // expected
      }
   }

   private void checkGetDateFromOptValueGood (String str, String expected)
   {
      Date result = null;
      Date expDate = null;
      try
      {
         expDate = Date.fromString(expected);
         result = LogViewer.getDateFromOptValue(str, true);
      }
      catch (Exception e)
      {
         logErr(errMsgGetDateFromOptValue(str), e);
      }
      assertNotNull("Got a null date", result);
      Assert.assertEquals(errMsgGetDateFromOptValue(str)
            + "returned unexcpected date.", result, expDate);
   }

   private String errMsgGetDateFromOptValue (String str)
   {
      return "LogViewer.getDateFromOptValue(" + str + ", true) ";
   }

   private String errMsgGetPeriodsFromOptionsValues (String str)
   {
      return "LogViewer.getPeriodsFromOptionsValues(" + str + ") ";
   }

   private void logErr (String str, Exception e)
   {
      final String msg = str + " failed due to unexpected Exception "
            + e.getMessage();
      logger.log(Level.SEVERE, msg, e);
      fail(msg);
   }
}
