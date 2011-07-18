/*
 * $Id: PeriodTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.text.ParseException;
import java.util.Calendar;
import junit.framework.TestCase;
import org.jcoderz.commons.ArgumentMalformedException;


/**
 * Implements test-cases for the {@link org.jcoderz.commons.types.Period}
 * class.
 *
 * @author Michael Rumpf
 */
public class PeriodTest
      extends TestCase
{
   /** One day in milli-seconds: 24*60*60*1000. */
   public static final long ONE_DAY_IN_MSEC = Date.MILLIS_PER_DAY;

   /** A constant for 10 msec. */
   public static final int TEN_MSEC = 10;
   /** A constant for 20 msec. */
   public static final int TWENTY_MSEC = 20;
   /** A constant for 30 msec. */
   public static final int THIRTY_MSEC = 30;

   private static final String UNEXPECTED_EMPTY_UNION
         = "The defined periods should not return an empty union set!";
   private static final String UNEXPECTED_EMPTY_INTERSECTION
         = "The defined periods should not return an empty "
            + "intersection set!";
   private static final String EXPECTED_EMPTY_UNION
         = "The defined periods should return an empty union set!";
   private static final String EXPECTED_EMPTY_INTERSECTION
         = "The defined periods should return an empty intersection set!";

   /**
    * Tries to create a period with the largest possible
    * start and end dates.
    */
   public void testLargestPeriod ()
   {
      try
      {
         Period.createDayPeriod(Date.OLD_DATE, Date.FUTURE_DATE);
      }
      catch (Exception ex)
      {
         fail("Unexpected exception occured!");
         ex.printStackTrace();
      }
   }

   /**
    * Test the constructor argument combinations and milli-second resolution.
    */
   public void testFactoryMethods ()
   {
      badFactory(null, null);
      final Date date = new Date(System.currentTimeMillis());
      badFactory(null, date);
      badFactory(date, null);
      final Date later = Date.nowPlus(1);
      badFactory(later, date);

      try
      {
         checkStartEnd(Date.now());
         checkStartEnd(Date.fromString("2004-09-03T12:11:33.785Z"));
      }
      catch (ParseException e)
      {
         fail("Testcase internal error. Caught a ParseException "
               + e.getMessage());
      }
   }

   /**
    * Test the constructor argument combinations and milli-second resolution.
    */
   public void testDayFactoryMethod ()
   {
      badDayFactory(null, null);
      final Date date = new Date(System.currentTimeMillis());
      badDayFactory(null, date);
      badDayFactory(date, null);
      final Date later = Date.nowPlus(ONE_DAY_IN_MSEC);
      badDayFactory(later, date);
   }

   /**
    * Tests the method {@link Period#createDayPeriod(Date)}.
    */
   public void testCreateDayPeriod ()
   {
      final Period a = Period.createDayPeriod(Date.now(), Date.now());
      final Period b = Period.createDayPeriod(Date.now());
      assertEquals("createDayPeriod(Date, Date)=" + a + " should be equal to "
            + " createDayPeriod(Date)=" + b, a, b);
   }

   /**
    * Tests the method {@link Period#createMonthPeriod(Date)}.
    */
   public void testCreateMonthPeriod ()
   {
      final Date date = dateFromString("2005-10-06T11:11:11.111Z");
      final Period shouldBePeriod = Period.createPeriod(
            dateFromString("2005-10-01T00:00:00.000Z"),
            dateFromString("2005-10-31T23:59:59.999Z"));
      final Period p = Period.createMonthPeriod(date);
      assertEquals("createMonthPeriod: " + p + ", should be " + shouldBePeriod
            + ", date " + date, p, shouldBePeriod);
   }

   /**
    * Test the union method of the Period class in milli-second resolution.
    */
   public void testPeriodUnion ()
   {
      final Date now = Date.now();

      //  a1 |-----| b1
      //     a2 |-----| b2    -->   a1 |--------| b2
      Date a1 = now;
      Date b1 = new Date(now.getTime() + TWENTY_MSEC);
      Date a2 = new Date(now.getTime() + TEN_MSEC);
      Date b2 = new Date(now.getTime() + THIRTY_MSEC);
      Period p = createUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(a1, b2, p);

      //  a1 |-----| b1
      //        a2 |-----| b2    -->   a1 |----------| b2
      a1 = now;
      a2 = new Date(now.getTime() + TEN_MSEC);
      b1 = new Date(now.getTime() + TEN_MSEC);
      b2 = new Date(now.getTime() + THIRTY_MSEC);
      p = createUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(a1, b2, p);

      //     a1 |-----| b1    -->   a2 |--------| b1
      //  a2 |-----| b2
      a1 = new Date(now.getTime() + TEN_MSEC);
      b1 = new Date(now.getTime() + THIRTY_MSEC);
      a2 = now;
      b2 = new Date(now.getTime() + TWENTY_MSEC);
      p = createUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(a2, b1, p);

      //        a1 |-----| b1    -->   a2 |----------| b1
      //  a2 |-----| b2
      a1 = new Date(now.getTime() + TEN_MSEC);
      b1 = new Date(now.getTime() + THIRTY_MSEC);
      a2 = now;
      b2 = new Date(now.getTime() + TEN_MSEC);
      p = createUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(a2, b1, p);
   }

   /**
    * Test the union method of the Period class in milli-second resolution.
    */
   public void testEmptyPeriodUnion ()
   {
      final Date now = Date.now();

      //  a1 |-----| b1
      //             a2 |-----| b2    -->   null
      Date a1 = now;
      Date b1 = new Date(now.getTime() + TEN_MSEC);
      Date a2 = new Date(now.getTime() + TWENTY_MSEC);
      Date b2 = new Date(now.getTime() + THIRTY_MSEC);
      Period p = createUnion(a1, b1, a2, b2);
      assertNull(EXPECTED_EMPTY_UNION, p);

      //             a1 |-----| b1    -->   null
      //  a2 |-----| b2
      a2 = now;
      a1 = new Date(now.getTime() + TWENTY_MSEC);
      b1 = new Date(now.getTime() + THIRTY_MSEC);
      b2 = new Date(now.getTime() + TEN_MSEC);
      p = createUnion(a1, b1, a2, b2);
      assertNull(EXPECTED_EMPTY_UNION, p);
   }

   /**
    * Test the union method of the Period class in day resolution.
    */
   public void testDayPeriodUnion ()
   {
      //  a1 |-----| b1
      //             a2 |-----| b2    -->   null
      Date a1 = Date.now();
      Date b1 = new Date(a1.getTime() + TEN_MSEC);
      Date a2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      Date b2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      Period p = createUnion(a1, b1, a2, b2);
      assertNull(EXPECTED_EMPTY_UNION, p);

      //  a1 |-----| b1
      //     a2 |-----| b2    -->   a1 |--------| b2
      a1 = Date.now();
      b1 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      a2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TEN_MSEC);
      b2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      p = createDayUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(getMinDayPeriod(a1), getMaxDayPeriod(b2), p);

      //  a1 |-----| b1
      //        a2 |-----| b2    -->   a1 |----------| b2
      a1 = Date.now();
      a2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TEN_MSEC);
      b1 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TEN_MSEC);
      b2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      p = createDayUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(getMinDayPeriod(a1), getMaxDayPeriod(b2), p);

      //     a1 |-----| b1    -->   a2 |--------| b1
      //  a2 |-----| b2
      a2 = Date.now();
      a1 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + TEN_MSEC);
      b2 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      b1 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      p = createDayUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(getMinDayPeriod(a2), getMaxDayPeriod(b1), p);

      //        a1 |-----| b1    -->   a2 |----------| b1
      //  a2 |-----| b2
      b2 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + TEN_MSEC);
      p = createDayUnion(a1, b1, a2, b2);
      assertNotNull(UNEXPECTED_EMPTY_UNION, p);
      assertPeriode(getMinDayPeriod(a2), getMaxDayPeriod(b1), p);

      //             a1 |-----| b1    -->   null
      //  a2 |-----| b2
      a1 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      b2 = new Date(a2.getTime() + TWENTY_MSEC);
      p = createDayUnion(a1, b1, a2, b2);
      assertNull(EXPECTED_EMPTY_UNION, p);
   }

   /**
    * Test the intersection method of the Period class in milli-second
    * resolution.
    */
   public void testPeriodIntersection ()
   {
      //  a1 |-----| b1
      //             a2 |-----| b2    -->   null
      Date a1 = Date.now();
      Date b1 = new Date(a1.getTime() + TEN_MSEC);
      Date a2 = new Date(a1.getTime() + TWENTY_MSEC);
      Date b2 = new Date(a1.getTime() + THIRTY_MSEC);
      Period p1 = Period.createPeriod(a1, b1);
      Period p2 = Period.createPeriod(a2, b2);

      Period p = p1.intersection(p2);
      assertNull(EXPECTED_EMPTY_INTERSECTION, p);

      //  a1 |-----| b1
      //     a2 |-----| b2    -->   a2 |--| b1
      a2 = new Date(a1.getTime() + TEN_MSEC);
      b1 = new Date(a1.getTime() + TWENTY_MSEC);
      p1 = Period.createPeriod(a1, b1);
      p2 = Period.createPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull("Periods do intersect!" + p1 + ", " + p2, p);
      assertPeriode(a2, b1, p);
      assertNotNull("Period does intersect." + p2 + ", " + p2,
            p2.intersection(p2));

      //  a1 |-----| b1
      //        a2 |-----| b2    -->   a2 | b1
      a1 = Date.now();
      b1 = new Date(a1.getTime() + TEN_MSEC);
      b2 = new Date(a1.getTime() + THIRTY_MSEC);
      p1 = Period.createPeriod(a1, b1);
      p2 = Period.createPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(a2, b1, p);

      //     a1 |-----| b1    -->   a1 |--| b2
      //  a2 |-----| b2
      a2 = Date.now();
      a1 = new Date(a2.getTime() + TEN_MSEC);
      b2 = new Date(a2.getTime() + TWENTY_MSEC);
      b1 = new Date(a2.getTime() + THIRTY_MSEC);
      p1 = Period.createPeriod(a1, b1);
      p2 = Period.createPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(a1, b2, p);

      //        a1 |-----| b1    -->   a1 | b2
      //  a2 |-----| b2
      b2 = new Date(a2.getTime() + TEN_MSEC);
      p1 = Period.createPeriod(a1, b1);
      p2 = Period.createPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(a1, b2, p);

      //             a1 |-----| b1    -->   null
      //  a2 |-----| b2
      a2 = Date.now();
      b2 = new Date(a2.getTime() + TEN_MSEC);
      a1 = new Date(a2.getTime() + TWENTY_MSEC);
      b1 = new Date(a2.getTime() + THIRTY_MSEC);
      p1 = Period.createPeriod(a1, b1);
      p2 = Period.createPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNull(EXPECTED_EMPTY_INTERSECTION, p);
   }

   /**
    * Test the intersection method of the Period class in day resolution.
    */
   public void testDayPeriodIntersection ()
   {
      //  a1 |-----| b1
      //             a2 |-----| b2    -->   null
      Date a1 = Date.now();
      Date b1 = new Date(a1.getTime() + TEN_MSEC);
      Date a2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      Date b2 = new Date(a1.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      Period p1 = Period.createDayPeriod(a1, b1);
      Period p2 = Period.createDayPeriod(a2, b2);

      Period p = p1.intersection(p2);
      assertNull(EXPECTED_EMPTY_INTERSECTION, p);

      //  a1 |-----| b1
      //     a2 |-----| b2    -->   a2 |--| b1
      a2 = new Date(a1.getTime() + TEN_MSEC);
      b1 = new Date(a1.getTime() + TWENTY_MSEC);
      p1 = Period.createDayPeriod(a1, b1);
      p2 = Period.createDayPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(getMinDayPeriod(a2), getMaxDayPeriod(b1), p);

      //  a1 |-----| b1
      //        a2 |-----| b2    -->   a2 | b1
      a1 = Date.now();
      b1 = new Date(a1.getTime() + TEN_MSEC);
      b2 = new Date(a1.getTime() + THIRTY_MSEC);
      p1 = Period.createDayPeriod(a1, b1);
      p2 = Period.createDayPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(getMinDayPeriod(a2), getMaxDayPeriod(b1), p);

      //     a1 |-----| b1    -->   a1 |--| b2
      //  a2 |-----| b2
      a2 = Date.now();
      a1 = new Date(a2.getTime() + TEN_MSEC);
      b2 = new Date(a2.getTime() + TWENTY_MSEC);
      b1 = new Date(a2.getTime() + THIRTY_MSEC);
      p1 = Period.createDayPeriod(a1, b1);
      p2 = Period.createDayPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(getMinDayPeriod(a1), getMaxDayPeriod(b2), p);

      //        a1 |-----| b1    -->   a1 | b2
      //  a2 |-----| b2
      b2 = new Date(a2.getTime() + TEN_MSEC);
      p1 = Period.createDayPeriod(a1, b1);
      p2 = Period.createDayPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNotNull(UNEXPECTED_EMPTY_INTERSECTION, p);
      assertPeriode(getMinDayPeriod(a1), getMaxDayPeriod(b2), p);

      //             a1 |-----| b1    -->   null
      //  a2 |-----| b2
      a2 = Date.now();
      b2 = new Date(a2.getTime() + TEN_MSEC);
      a1 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + TWENTY_MSEC);
      b1 = new Date(a2.getTime() + ONE_DAY_IN_MSEC + THIRTY_MSEC);
      p1 = Period.createDayPeriod(a1, b1);
      p2 = Period.createDayPeriod(a2, b2);

      p = p1.intersection(p2);
      assertNull(EXPECTED_EMPTY_INTERSECTION, p);
   }

   /**
    * Test the isIncluded() method of the Period class in milli-second
    * resolution.
    */
   public void testIsIncludedPeriod ()
   {
      final Date a1 = Date.now();
      final Date b1 = new Date(a1.getTime() + THIRTY_MSEC);
      final Date c  = new Date(a1.getTime() + TEN_MSEC);
      final Period p = Period.createDayPeriod(a1, b1);
      assertTrue("The timestamp '"
            + c + "' does not fall into the period:" + p, p.isIncluded(c));
   }

   /**
    * Test the isIncluded() method of the Period class in day resolution.
    */
   public void testIsIncludedDayPeriod ()
   {
      final Date a1 = Date.now();
      final Date b1 = new Date(a1.getTime() + THIRTY_MSEC);
      final Date c  = new Date(a1.getTime() + TEN_MSEC);
      final Period p = Period.createDayPeriod(a1, b1);
      assertTrue("The timestamp '"
            + c + "' does not fall into the period:" + p, p.isIncluded(c));
   }

   /**
    * Test the {@link Period#getNextPeriodStartTime()} and
    * {@link Period#getPrevPeriodEndTime()} methods.
    */
   public void testGetNextPrevPeriodTime ()
   {
      final Date start = Date.now();
      final Date end = start.plus(Date.MILLIS_PER_DAY);
      Date shouldBeNext = end.plus(1);
      Date shouldBePrev = start.minus(1);

      // Regular period
      Period p = Period.createPeriod(start, end);

      assertEquals("StartTime of the next period is not correct.",
            shouldBeNext, p.getNextPeriodStartTime());
      assertEquals("EndTime of the previous period is not correct.",
            shouldBePrev, p.getPrevPeriodEndTime());

      // Day based period
      p = Period.createDayPeriod(start, end);

      shouldBeNext = p.getEndTime().plus(1);
      shouldBePrev = p.getStartTime().minus(1);

      assertEquals("StartTime of the next period is not correct.",
            shouldBeNext, p.getNextPeriodStartTime());
      assertEquals("EndTime of the previous period is not correct.",
            shouldBePrev, p.getPrevPeriodEndTime());
   }

   /**
    * Tests the method {@link Period#next()}.
    */
   public void testNext ()
   {
      final Period org = Period.createPeriod(Date.now(),
            Date.nowPlus(Date.MILLIS_PER_DAY));
      final Period next = org.next();
      assertTrue("Start time of the next period " + next.getStartTime()
            + " should be equal to the NextPeriodStartTime "
            + org.getNextPeriodStartTime(),
            org.getNextPeriodStartTime().equals(next.getStartTime()));
      assertTrue("End time of the next period " + next.getStartTime()
            + " should be equal to the NextPeriodStartTime + duration "
            + org.getNextPeriodStartTime().plus(org.duration()),
            org.getNextPeriodStartTime().plus(org.duration()).equals(
                  next.getEndTime()));
   }

   /**
    * Tests the method {@link Period#previous()}.
    */
   public void testPrevious ()
   {
      final Period org = Period.createPeriod(Date.now(),
            Date.nowPlus(Date.MILLIS_PER_DAY));
      final Period previous = org.previous();
      assertTrue("End time of the previous period " + previous.getEndTime()
            + " should be equal to the PrevPeriodEndTime "
            + org.getPrevPeriodEndTime(),
            org.getPrevPeriodEndTime().equals(previous.getEndTime()));
      assertTrue("Start time of the previous period " + previous.getStartTime()
            + " should be equal to the PrevPeriodEndTime - duration "
            + org.getPrevPeriodEndTime().minus(org.duration()),
            org.getPrevPeriodEndTime().minus(org.duration()).equals(
                  previous.getStartTime()));
   }

   /**
    * Tests the methods {@link Period#nextHour()} and
    * {@link Period#nextHour(Date)}.
    */
   public void testNextHour ()
   {
      try
      {
         // regular
         Date timeDate = Date.fromString("2004-09-04T10:04:22.788Z");
         Period shouldBePeriod = Period.createPeriod(
               Date.fromString("2004-09-04T11:00:00Z"),
               Date.fromString("2004-09-04T11:59:59.999Z"));
         checkNextHour(timeDate, shouldBePeriod);

         // end of a day
         timeDate = Date.fromString("2005-10-05T23:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-10-06T00:00:00.000Z"),
               Date.fromString("2005-10-06T00:59:59.999Z"));
         checkNextHour(timeDate, shouldBePeriod);

         // end of a month
         timeDate = Date.fromString("2005-10-31T23:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-11-01T00:00:00.000Z"),
               Date.fromString("2005-11-01T00:59:59.999Z"));
         checkNextHour(timeDate, shouldBePeriod);
      }
      catch (ParseException e)
      {
         fail("Testcase internal error. Got a ParseException "
               + e.getMessage());
      }
   }

   /**
    * Tests the methods {@link Period#previousHour()} and
    * {@link Period#previousHour(Date)}.
    */
   public void testPreviousHour ()
   {
      try
      {
         // regular
         Date timeDate = Date.fromString("2004-09-04T10:04:22.788Z");
         Period shouldBePeriod = Period.createPeriod(
               Date.fromString("2004-09-04T09:00:00Z"),
               Date.fromString("2004-09-04T09:59:59.999Z"));
         checkPreviousHour(timeDate, shouldBePeriod);

         // first hour of a day
         timeDate = Date.fromString("2005-10-05T00:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-10-04T23:00:00.000Z"),
               Date.fromString("2005-10-04T23:59:59.999Z"));
         checkPreviousHour(timeDate, shouldBePeriod);

         // first day of a month
         timeDate = Date.fromString("2005-11-01T00:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-10-31T23:00:00.000Z"),
               Date.fromString("2005-10-31T23:59:59.999Z"));
         checkPreviousHour(timeDate, shouldBePeriod);
      }
      catch (ParseException e)
      {
         fail("Testcase internal error. Got a ParseException "
               + e.getMessage());
      }
   }

   /**
    * Tests the methods {@link Period#nextDay()} and
    * {@link Period#nextDay(Date)}.
    */
   public void testNextDay ()
   {
      try
      {
         // regular
         Date timeDate = Date.fromString("2004-09-04T10:04:22.788Z");
         Period shouldBePeriod = Period.createPeriod(
               Date.fromString("2004-09-05T00:00:00Z"),
               Date.fromString("2004-09-05T23:59:59.999Z"));
         checkNextDay(timeDate, shouldBePeriod);

         // end of a month
         timeDate = Date.fromString("2005-10-31T22:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-11-01T00:00:00.000Z"),
               Date.fromString("2005-11-01T23:59:59.999Z"));
         checkNextDay(timeDate, shouldBePeriod);
      }
      catch (ParseException e)
      {
         fail("Testcase internal error. Got a ParseException "
               + e.getMessage());
      }
   }

   /**
    * Tests the methods {@link Period#previousDay()} and
    * {@link Period#previousDay(Date)}.
    */
   public void testPreviousDay ()
   {
      try
      {
         // regular
         Date timeDate = Date.fromString("2004-09-04T10:04:22.788Z");
         Period shouldBePeriod = Period.createPeriod(
               Date.fromString("2004-09-03T00:00:00Z"),
               Date.fromString("2004-09-03T23:59:59.999Z"));
         checkPreviousDay(timeDate, shouldBePeriod);

         // first day of a month
         timeDate = Date.fromString("2005-11-01T11:04:22.788Z");
         shouldBePeriod = Period.createPeriod(
               Date.fromString("2005-10-31T00:00:00.000Z"),
               Date.fromString("2005-10-31T23:59:59.999Z"));
         checkPreviousDay(timeDate, shouldBePeriod);
      }
      catch (ParseException e)
      {
         fail("Testcase internal error. Got a ParseException "
               + e.getMessage());
      }
   }

   /**
    * Tests the methods {@link Period#nextMonth()} and
    * {@link Period#nextMonth(Date)}.
    */
   public void testNextMonth ()
   {
      // regular
      Date date = dateFromString("2005-10-06T11:11:11.111Z");
      Period shouldBePeriod = Period.createPeriod(
            dateFromString("2005-11-01T00:00:00.000Z"),
            dateFromString("2005-11-30T23:59:59.999Z"));
      checkNextMonth(date, shouldBePeriod);

      // leap day
      date = dateFromString("2008-01-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2008-02-01T00:00:00.000Z"),
            dateFromString("2008-02-29T23:59:59.999Z"));
      checkNextMonth(date, shouldBePeriod);

      // leap day
      date = dateFromString("2007-01-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2007-02-01T00:00:00.000Z"),
            dateFromString("2007-02-28T23:59:59.999Z"));
      checkNextMonth(date, shouldBePeriod);

      // last month
      date = dateFromString("2005-12-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2006-01-01T00:00:00.000Z"),
            dateFromString("2006-01-31T23:59:59.999Z"));
      checkNextMonth(date, shouldBePeriod);
   }

   /**
    * Tests the methods {@link Period#previousMonth()} and
    * {@link Period#previousDay(Date)}.
    */
   public void testPreviousMonth ()
   {
      // regular
      Date date = dateFromString("2005-10-06T11:11:11.111Z");
      Period shouldBePeriod = Period.createPeriod(
            dateFromString("2005-09-01T00:00:00.000Z"),
            dateFromString("2005-09-30T23:59:59.999Z"));
      checkPreviousMonth(date, shouldBePeriod);

      // leap day
      date = dateFromString("2008-03-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2008-02-01T00:00:00.000Z"),
            dateFromString("2008-02-29T23:59:59.999Z"));
      checkPreviousMonth(date, shouldBePeriod);

      // leap day
      date = dateFromString("2007-03-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2007-02-01T00:00:00.000Z"),
            dateFromString("2007-02-28T23:59:59.999Z"));
      checkPreviousMonth(date, shouldBePeriod);

      // first month
      date = dateFromString("2007-01-06T11:11:11.111Z");
      shouldBePeriod = Period.createPeriod(
            dateFromString("2006-12-01T00:00:00.000Z"),
            dateFromString("2006-12-31T23:59:59.999Z"));
      checkPreviousMonth(date, shouldBePeriod);
   }

   private void checkNextMonth (final Date date, final Period shouldBePeriod)
   {
      final Period current = Period.createPeriod(
            date.minus(Date.MILLIS_PER_WEEK), date);
      Period next = current.nextMonth();
      assertEquals("current period: " + current
            + ", current.nextMonth() period: " + next + ", should be "
            + shouldBePeriod, next, shouldBePeriod);
      next = Period.nextMonth(date);
      assertEquals("date: " + date  + ", Period.nextMonth(date): " + next
            + ", should be " + shouldBePeriod, next, shouldBePeriod);
   }

   private void checkPreviousMonth (final Date date,
         final Period shouldBePeriod)
   {
      final Period current = Period.createPeriod(
            date, date.plus(Date.MILLIS_PER_WEEK));
      Period prev = current.previousMonth();
      assertEquals("current period: " + current
            + ", current.previousMonth() period: " + prev + ", should be "
            + shouldBePeriod, prev, shouldBePeriod);
      prev = Period.previousMonth(date);
      assertEquals("date: " + date  + ", Period.previousMonth(date): " + prev
            + ", should be " + shouldBePeriod, prev, shouldBePeriod);
   }

   private void checkNextHour (Date timeDate, Period shouldBePeriod)
   {
      final Period nextHour = Period.nextHour(timeDate);
      final Period timePeriod = Period.createPeriod(
            timeDate.minus(Date.MILLIS_PER_HOUR), timeDate);
      assertTrue("The next hour period from " + timeDate + " should be "
            + shouldBePeriod + ", got period " + nextHour,
               nextHour.equals(shouldBePeriod));
      assertTrue("Period.nextHour(Date) should return the same "
            + "period as period.nextHour(), Date='" + timeDate
            + "', Period.nextHour(Date)='" + nextHour.toString()
            + ", period.nextHour(), period " + timePeriod.toString()
            + ", result " + timePeriod.nextHour(),
               timePeriod.nextHour().equals(nextHour));
   }

   private void checkPreviousHour (Date timeDate, Period shouldBePeriod)
   {
      final Period prevHour = Period.previousHour(timeDate);
      final Period timePeriod = Period.createPeriod(
            timeDate, timeDate.plus(Date.MILLIS_PER_HOUR));

      assertTrue("The previous hour period from " + timeDate + " should be "
            + shouldBePeriod + ", got period " + prevHour,
            prevHour.equals(shouldBePeriod));
      assertTrue("Period.previousHour(Date) should return the same "
            + "period as period.previousHour(), Date='" + timeDate
            + "', Period.previousHour(Date)='" + prevHour.toString()
            + ", period.previousHour(), period " + timePeriod.toString()
            + ", result " + timePeriod.previousHour(),
               timePeriod.previousHour().equals(prevHour));
   }

   private void checkNextDay (Date timeDate, Period shouldBePeriod)
   {
      final Period nextDay = Period.nextDay(timeDate);
      final Period timePeriod = Period.createPeriod(
            timeDate.minus(Date.MILLIS_PER_HOUR), timeDate);
      assertTrue("The next day period from " + timeDate + " should be "
            + shouldBePeriod + ", got period " + nextDay,
               nextDay.equals(shouldBePeriod));
      assertTrue("Period.nextDay(Date) should return the same "
            + "period as period.nextDay(), Date='" + timeDate
            + "', Period.nextDay(Date)='" + nextDay.toString()
            + ", period.nextDay(), period " + timePeriod.toString()
            + ", result " + timePeriod.nextDay(),
               timePeriod.nextDay().equals(nextDay));
   }

   private void checkPreviousDay (Date timeDate, Period shouldBePeriod)
   {
      final Period prevDay = Period.previousDay(timeDate);
      final Period timePeriod = Period.createPeriod(
            timeDate, timeDate.plus(Date.MILLIS_PER_HOUR));

      assertTrue("The previous day period from " + timeDate + " should be "
            + shouldBePeriod + ", got period " + prevDay,
            prevDay.equals(shouldBePeriod));
      assertTrue("Period.previousDay(Date) should return the same "
            + "period as period.previousDay(), Date='" + timeDate
            + "', Period.previousDay(Date)='" + prevDay.toString()
            + ", period.previousDay(), period " + timePeriod.toString()
            + ", result " + timePeriod.previousDay(),
               timePeriod.previousDay().equals(prevDay));
   }

   private void assertPeriode (Date expectedStart, Date expectedEnd, Period p)
   {
      assertEquals("StartTime of period is not correct.",
            expectedStart, p.getStartTime());
      assertEquals("EndTime of period is not correct.",
            expectedEnd, p.getEndTime());
   }

   private Period createDayUnion (Date a1, Date b1, Date a2, Date b2)
   {
      final Period p1 = Period.createDayPeriod(a1, b1);
      final Period p2 = Period.createDayPeriod(a2, b2);
      return p1.union(p2);
   }

   private void badFactory (Date start, Date end)
   {
      try
      {
         Period.createPeriod(start, end);
         fail("Period should not accept the parameters start = '" + start
               + "' and end = '" + end + "'.");
      }
      catch (ArgumentMalformedException ex)
      {
         // this is correct
      }
   }
   private void badDayFactory (Date start, Date end)
   {
      try
      {
         Period.createDayPeriod(start, end);
         fail("Period should not accept the parameters start = '" + start
               + "' and end = '" + end + "'.");
      }
      catch (ArgumentMalformedException ex)
      {
         // this is correct
      }
   }

   private Period createUnion (Date a1, Date b1, Date a2, Date b2)
   {
      final Period p1 = Period.createPeriod(a1, b1);
      final Period p2 = Period.createPeriod(a2, b2);
      return p1.union(p2);
   }

   private Date getMinDayPeriod (Date time)
   {
      final Calendar s = Period.getCalendarInstance(time);
      final int year  = s.get(Calendar.YEAR);
      final int month = s.get(Calendar.MONTH);
      final int day   = s.get(Calendar.DAY_OF_MONTH);
      s.set(year, month, day, s.getMinimum(Calendar.HOUR_OF_DAY),
            s.getMinimum(Calendar.MINUTE), s.getMinimum(Calendar.SECOND));
      s.set(Calendar.MILLISECOND, s.getMinimum(Calendar.MILLISECOND));
      return new Date(s.getTimeInMillis());
   }

   private Date getMaxDayPeriod (Date time)
   {
      final Calendar e = Period.getCalendarInstance(time);
      final int year  = e.get(Calendar.YEAR);
      final int month = e.get(Calendar.MONTH);
      final int day   = e.get(Calendar.DAY_OF_MONTH);
      e.set(year, month, day, e.getMaximum(Calendar.HOUR_OF_DAY),
            e.getMaximum(Calendar.MINUTE), e.getMaximum(Calendar.SECOND));
      e.set(Calendar.MILLISECOND, e.getMaximum(Calendar.MILLISECOND));
      return new Date(e.getTimeInMillis());
   }

   private void checkStartEnd (Date s)
   {
      checkStartEnd(s, s.plus(Date.MILLIS_PER_SECOND));
      checkStartEnd(s, s.plus(Date.MILLIS_PER_MINUTE));
      checkStartEnd(s, s.plus(Date.MILLIS_PER_HOUR));
      checkStartEnd(s, s.plus(Date.MILLIS_PER_DAY));
      checkStartEnd(s, s.plus(Date.MILLIS_PER_WEEK));
   }


   private void checkStartEnd (Date s, Date e)
   {
      final Period p = Period.createPeriod(s, e);
      assertTrue("Period's start time (" + p.getStartTime()
            + ")should be equals to " + s, s.equals(p.getStartTime()));
      assertTrue("Period's start time (" + p.getEndTime()
            + ")should be equals to " + e, e.equals(p.getEndTime()));
   }

   private Date dateFromString (final String s)
   {
      Date result = null;
      try
      {
         result = Date.fromString(s);
      }
      catch (ParseException e)
      {
         e.printStackTrace();
         fail("Testcase internal error, invalid date '" + s
               + "', got a ParseException " + e.getMessage());
      }
      return result;
   }
}
