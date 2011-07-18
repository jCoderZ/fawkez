/*
 * $Id: DateTest.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.jcoderz.commons.util.Constants;



/**
 * Test class to test the Date class.
 *
 */
public class DateTest
      extends TestCase
{
   /** Test value. */
   static final long SOME_TIME_IN_MILLIES = 12345000001L;
   /** Allowed difference for date now. */
   static final long MAX_DIFF_FOR_NOW = 500L;
   /** An other test value. */
   static final long SOME_TIME_IN_MILLIES_2 = 123456L;

   /** Successful test for "fromSqlDate". */
   public void testFromSqlDate ()
   {
      final java.sql.Date sqlDate = new java.sql.Date(SOME_TIME_IN_MILLIES);
      final Date date = new Date(SOME_TIME_IN_MILLIES);
      assertEquals("Value should not change when created via sql date.",
            date, Date.fromSqlDate(sqlDate));
   }

   /** Successful test for "fromSqlTimestamp". */
   public void testFromSqlTimestamp ()
   {
      final Timestamp sqlTimestamp = new Timestamp(SOME_TIME_IN_MILLIES);
      final Date date = new Date(sqlTimestamp.getTime());
      assertEquals("Value should not change when created via sql timestamp.",
            date, Date.fromSqlTimestamp(sqlTimestamp));
   }

   /** Successful test for "now". */
   public void testNow ()
   {
      final Date currentDate = new Date(System.currentTimeMillis());
      final long diff = Date.now().getTime() - currentDate.getTime();
      assertTrue("Now is to far from Date.now()", diff < MAX_DIFF_FOR_NOW);
   }

   /** Successful test for "nowPlus". */
   public void testNowPlus ()
   {
      final Date currentDate
            = new Date(System.currentTimeMillis() + SOME_TIME_IN_MILLIES_2);
      final long diff
            = Date.nowPlus(SOME_TIME_IN_MILLIES_2).getTime()
               - currentDate.getTime();
      assertTrue("NowPlus is to far from Date.now() + ...",
            diff < MAX_DIFF_FOR_NOW);
   }

   /**
    * Tests the method {@link Date#plus(long)}.
    */
   public void testPlus ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);
      final Date plusDate = new Date(time + SOME_TIME_IN_MILLIES);
      assertTrue("plusDate " + plusDate + " is not equals to date.plus() "
            + date.plus(SOME_TIME_IN_MILLIES),
            plusDate.equals(date.plus(SOME_TIME_IN_MILLIES)));
   }

   /**
    * Tests the method {@link Date#minus(long)}.
    */
   public void testMinus ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);
      final Date minusDate = new Date(time - SOME_TIME_IN_MILLIES);
      assertTrue("minusDate " + minusDate + " is not equals to date.minus() "
            + date.plus(SOME_TIME_IN_MILLIES),
            minusDate.equals(date.minus(SOME_TIME_IN_MILLIES)));
   }

   /**
    * Successful test for "fromString (String date, String pattern)".
    * @throws ParseException if the test case fails.
    */
   public void testFromString ()
         throws ParseException
   {
      // if date string is empty then null expected
      final Date currentDate = new Date(System.currentTimeMillis());
      final String pattern = "dd.MM.yyyy";
      final String date = currentDate.toString(pattern);
      assertEquals("Empty string should produce null result.",
            null, Date.fromString("", pattern));

      // if date string is not empty
      final SimpleDateFormat dateFormat
            = new SimpleDateFormat(pattern, Constants.SYSTEM_LOCALE);
      dateFormat.setTimeZone(Date.TIME_ZONE);
      final Date expected = Date.fromUtilDate(dateFormat.parse(date));
      assertEquals("Valid date not parsed correctly.", expected,
            Date.fromString(date, pattern));

      final String time = "2004-09-04T10:04:22.000Z";
      final Date timeDate = Date.fromString(time);
      assertEquals("should be the same string representation", time,
            timeDate.toString());

      assertNull("Should be null for null argument.", Date.fromString(null));
   }

   /** Successful test for "toString". */
   public void testToString ()
   {
      assertEquals("For day 0 string representation should be fix.",
            "1970-01-01T00:00:00.000Z", Date.OLD_DATE.toString());
   }

   /** Successful test for "toString". */
   public void testToStringWithMillies ()
   {
      assertEquals("For day 0 string representation should be fix.",
            "1970-01-01T00:00:00.001Z", new Date(1L).toString());
   }

   /** Successful test for "toString". */
   public void testDateString ()
   {
      assertEquals("String representation should be same for default pattern.",
            "1970-01-01Z", Date.OLD_DATE.toDateString());
   }

   /** Successful test for "toUtilDate". */
   public void testToUtilDate ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);

      assertEquals("Util Date differs from Date.",
            new java.util.Date(time).getTime(), date.toUtilDate().getTime());
   }

  /** Successful test for "toSqlDate". */
   public void testToSqlDate ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);

      assertEquals("Sql Date differs from Date.",
            new java.sql.Date(time).getTime(), date.toSqlDate().getTime());
   }

  /** Successful test for "toSqlTimestamp". */
   public void testToSqlTimestamp ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);

      assertEquals("Sql timestamp differs from Date.",
            time, date.toSqlTimestamp().getTime());
   }

  /** Successful test for "equals". */
   public void testEquals ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);
      Date date2 = new Date(time);
      final Date date3 = new Date(SOME_TIME_IN_MILLIES);
      assertEquals("Date equals created wrong result.",
            true, date.equals(date2));
      assertEquals("Date equals created wrong result.",
            false, date.equals(date3));
      date2 = new Date (SOME_TIME_IN_MILLIES - 1);
      assertEquals("Date equals created wrong result.",
            false, date.equals(date2));
   }

  /** Successful test for "compareTo". */
   public void testCompareTo ()
   {
      final long time = System.currentTimeMillis();
      final Date date = new Date(time);
      Date date2 = new Date(time);
      assertEquals("Date comparison created wrong result.",
            0, date.compareTo(date2));

      final Date date3 = new Date(time - SOME_TIME_IN_MILLIES_2);
      assertEquals("Date comparison created wrong result.",
            1, date.compareTo(date3));

      date2 = new Date (time + SOME_TIME_IN_MILLIES_2);
      assertEquals("Date comparison created wrong result.",
            -1, date.compareTo(date2));
   }

   /**
    * Simple test method to check basic serialization.
    * @throws ClassNotFoundException in case of an test case error
    * @throws IOException in case of an test case error
    */
   public void testSerialize ()
         throws IOException, ClassNotFoundException
   {
      final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      final ObjectOutputStream objOut = new ObjectOutputStream(bOut);
      ByteArrayInputStream bIn;
      ObjectInputStream objIn;

      final Date date = Date.now();

      objOut.writeObject(date);
      objOut.flush();
      bIn = new ByteArrayInputStream(bOut.toByteArray());
      objIn = new ObjectInputStream(bIn);
      final Date dateRead = (Date) objIn.readObject();

      assertEquals("Value changed during serialization.", date, dateRead);
   }

   /** Tests the sql timestamp handling. */
   public void testSqlTimestamp ()
   {
      final Date refDate = Date.now();
      final Timestamp test = new Timestamp(refDate.getTime());

      assertEquals("Refdate changed in timestamp representation.",
            refDate.getTime(), test.getTime());

      final Date testDate = Date.fromSqlTimestamp(test);

      assertEquals("Timestamp Value changed within Date type conversion.",
            test, testDate.toSqlTimestamp());
   }

   /** Tests the sql timestamp handling. */
   public void testSqlTimestampWithNanos ()
   {
      final Date refDate = Date.now();
      final Timestamp test = new Timestamp(refDate.getTime());

      test.setNanos(test.getNanos() + 1);

      assertEquals("Refdate changed in timestamp representation.",
            refDate.getTime(), test.getTime());

      final Date testDate = Date.fromSqlTimestamp(test);
      test.setNanos(test.getNanos() - 1);

      assertEquals("Timestamp Value changed within Date type conversion.",
            test, testDate.toSqlTimestamp());
   }

   /** Tests the {@link Date#getDaysSinceEpoch()} method. */
   public void testGetDaysSinceEpoch ()
   {
      final int days = Date.getDaysSinceEpoch();
      assertTrue("Result must be positive but was " + days, days > 0);
   }

   /** Tests the {@link Date#getDaysSinceEpoch(Date)} method. */
   public void testGetDaysSinceEpochDate ()
   {
      final int days = Date.getDaysSinceEpoch(Date.now());
      assertTrue("Result must be positive but was " + days, days > 0);
      assertEquals("No days passed.", 0, Date.getDaysSinceEpoch(new Date(0)));
   }

   /**
    * Method to test for {@link Date#hashCode()}.
    */
   public void testHashCode ()
   {
      assertEquals("two dates with the vaue should have the same "
            + "hashCode", new Date(SOME_TIME_IN_MILLIES).hashCode(),
            new Date(SOME_TIME_IN_MILLIES).hashCode());
   }

   /**
    * Tests the method {@link Date#elapsedMillis()}.
    */
   public void testElapsed ()
   {
      final Date currentDate = new Date(System.currentTimeMillis());
      final long diff = currentDate.elapsedMillis();
      assertTrue("Diff is to far from Date.now()", diff < MAX_DIFF_FOR_NOW);
      assertTrue("Diff is negative.", diff >= 0);
   }

   /**
    * Tests the method {@link Date#elapsedMillis(Date)}.
    */
   public void testElapsedDate ()
   {
      final Date currentDate = new Date(System.currentTimeMillis());
      final long diff = currentDate.elapsedMillis(
            new Date(currentDate.getTime() + SOME_TIME_IN_MILLIES_2));
      assertEquals("Diff is wrong", SOME_TIME_IN_MILLIES_2, diff);
   }

   /** Tests the after method. */
   public void testAfter ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertTrue("After comparison result unexpected (this < other)",
            date2.after(date1));
      assertFalse("After comparison result unexpected (this == other)",
            date2.after(date3));
      assertFalse("After comparison result unexpected (this > other)",
            date2.after(date4));
   }

   /** Tests the after or equal method. */
   public void testAfterOrEqual ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertTrue("AfterOrEqual comparison result unexpected (this < other)",
            date2.afterOrEqual(date1));
      assertTrue("AfterOrEqual comparison result unexpected (this == other)",
            date2.afterOrEqual(date3));
      assertFalse("AfterOrEqual comparison result unexpected (this > other)",
            date2.afterOrEqual(date4));
   }

   /** Tests the before method. */
   public void testBefore ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertFalse("Before comparison result unexpected (this < other)",
            date2.before(date1));
      assertFalse("Before comparison result unexpected (this == other)",
            date2.before(date3));
      assertTrue("Before comparison result unexpected (this > other)",
            date2.before(date4));
   }

   /** Tests the after method. */
   public void testBeforeOrEqual ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertFalse("BeforeOrEqual comparison result unexpected (this < other)",
            date2.beforeOrEqual(date1));
      assertTrue("BeforeOrEqual comparison result unexpected (this == other)",
            date2.beforeOrEqual(date3));
      assertTrue("BeforeOrEqual comparison result unexpected (this > other)",
            date2.beforeOrEqual(date4));
   }

   /** Tests the earliest method. */
   public void testEarliest ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertEquals("Earliest result unexpected (a < b)", date1,
            Date.earliest(date1, date2));
      assertEquals("Earliest result unexpected (a == b)", date2,
            Date.earliest(date2, date3));
      assertEquals("Earliest result unexpected (a > b)", date2,
            Date.earliest(date4, date2));
   }

   /** Tests the latest method. */
   public void testLatest ()
   {
      final long time = System.currentTimeMillis();
      final Date date1 = new Date(time - SOME_TIME_IN_MILLIES_2);
      final Date date2 = new Date(time);
      final Date date3 = new Date(time);
      final Date date4 = new Date(time + SOME_TIME_IN_MILLIES_2);
      assertEquals("Latest result unexpected (a < b)", date2,
            Date.latest(date1, date2));
      assertEquals("Latest result unexpected (a == b)", date2,
            Date.latest(date2, date3));
      assertEquals("Latest result unexpected (a > b)", date4,
            Date.latest(date2, date4));
   }
}
