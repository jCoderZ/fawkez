/*
 * $Id: Period.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.io.Serializable;
import java.util.Calendar;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.HashCodeUtil;



/**
 * A <code>Period</code> data type represents a period in time.
 * <p>
 * There are two types of periods. One that is day bound and the other one
 * is milli-second bound. The day bound period has just set the hours, minutes,
 * seconds, and milli-seconds set to zero. In other words, the first one is
 * in day resolution whereas the second one is in milli-second resolution.
 * <p>
 * TODO: Refine
 *
 * @author Michael Rumpf
 */
public final class Period
      implements Serializable
{
   /** The name of this type. */
   public static final String TYPE_NAME = "Period";

   /** use this serialVersionUID for serialization. */
   static final long serialVersionUID = 128446267044986064L;

   private static final String DATE_PARAMETER = "date";

   /** Lazy init hash code. */
   private transient int mHashCode = 0;

   /** The start time of the period. */
   private final Date mStartTime;

   /** The end time of the period. */
   private final Date mEndTime;

   /**
    * This is for easy creation of objects from two longs.
    *
    * @param start The start date.
    * @param end The end date.
    */
   private Period (Date start, Date end)
   {
      Assert.notNull(start, "start date");
      Assert.notNull(end, "end date");
      if (end.compareTo(start) < 0)
      {
         throw new ArgumentMalformedException(TYPE_NAME,
               "start: " + start + " end:" + end,
               "The end date must be larger or equal to the start date!");
      }
      mStartTime = start;
      mEndTime = end;
   }

   /**
    * A factory method to create a period from two timestamps of the type
    * {@link org.jcoderz.commons.types.Date}.
    *
    * @param start The start time to create the period from.
    * @param end The end time to create the period from.
    * @return The period created by the two timestamps.
    * @throws ArgumentMalformedException when the end date is before the start
    * date.
    */
   public static Period createPeriod (final Date start, final Date end)
         throws ArgumentMalformedException
   {
      return new Period(start, end);
   }

   /**
    * A factory method to create a period from two timestamps where the
    * hours, minutes, seconds, and milli-seconds are stripped off.
    *
    * @param start The start time to create the period from.
    * @param end The end time to create the period from.
    * @return The period created by the two timestamps.
    * @throws ArgumentMalformedException when the end date is before the start
    * date.
    */
   public static Period createDayPeriod (Date start, Date end)
         throws ArgumentMalformedException
   {
      Assert.notNull(start, "start date");
      Assert.notNull(end, "end date");

      final Calendar s = getCalendarInstance(start);
      int year  = s.get(Calendar.YEAR);
      int month = s.get(Calendar.MONTH);
      int day   = s.get(Calendar.DAY_OF_MONTH);
      s.set(year, month, day, s.getMinimum(Calendar.HOUR_OF_DAY),
            s.getMinimum(Calendar.MINUTE), s.getMinimum(Calendar.SECOND));
      s.set(Calendar.MILLISECOND, s.getMinimum(Calendar.MILLISECOND));
      final Calendar e = getCalendarInstance(end);
      year  = e.get(Calendar.YEAR);
      month = e.get(Calendar.MONTH);
      day   = e.get(Calendar.DAY_OF_MONTH);
      e.set(year, month, day, e.getMaximum(Calendar.HOUR_OF_DAY),
            e.getMaximum(Calendar.MINUTE), e.getMaximum(Calendar.SECOND));
      e.set(Calendar.MILLISECOND, e.getMaximum(Calendar.MILLISECOND));
      return new Period(
            Date.fromUtilDate(s.getTime()), Date.fromUtilDate(e.getTime()));
   }

   /**
    * A factory method to create a day period. This period starts and ends on
    * the same day as the given <code>date</code>. The duration of this period
    * is exactly {@link Date#MILLIS_PER_DAY} milliseconds.
    * Example: for the date 2004-09-03T11:52:03.437Z this method will return the
    * period 2004-09-03T00:00:00.000Z-2004-09-03T23:59:59.999Z.
    *
    * @param date The date that falls within the returned period.
    * @return The period created by the two timestamps.
    * @throws ArgumentMalformedException when the parameter <code>date</code> is
    * null.
    */
   public static Period createDayPeriod (Date date)
         throws ArgumentMalformedException
   {
      return Period.createDayPeriod(date, date);
   }

   /**
    * A factory method to create a month period. This period starts and ends on
    * the same month as the given <code>date</code>. The duration of this period
    * is exactly the duration of the month of the <code>date</code>.
    * Example: for the date 2004-09-03T11:52:03.437Z this method will return a
    * period 2004-09-01T00:00:00.000Z-2004-09-30T23:59:59.999Z.
    *
    * @param date The date that falls within the returned period.
    * @return The period created by the two timestamps.
    * @throws ArgumentMalformedException when the parameter <code>date</code> is
    * null.
    */
   public static Period createMonthPeriod (Date date)
         throws ArgumentMalformedException
   {
      Assert.notNull(date, DATE_PARAMETER);

      final Calendar c = getCalendarInstance(date);
      c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
      final Date s = new Date(c.getTimeInMillis());
      c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
      final Date e = new Date(c.getTimeInMillis());
      return Period.createDayPeriod(s, e);
   }

   /**
    * Returns the start date of the period.
    *
    * @return A Date marking the start of the period.
    */
   public Date getStartTime ()
   {
      return mStartTime;
   }

   /**
    * Returns the end date of the period.
    *
    * @return A Date marking the end of the period.
    */
   public Date getEndTime ()
   {
      return mEndTime;
   }

   /**
    * Returns the intersection of two periods.
    *
    * @param other The period that will be intersected with this instance.
    * @return A new period that is the intersection between the two periods.
    * Null is returned in case there is no intersection between the two periods.
    */
   public Period intersection (final Period other)
   {
      final Date start = Date.latest(mStartTime, other.mStartTime);
      final Date end = Date.earliest(mEndTime, other.mEndTime);

      final Period result;
      if (start.beforeOrEqual(end))
      {
         result = new Period(start, end);
      }
      else
      {
         result = null;
      }
      return result;
   }

   /**
    * Returns the union of two periods.
    *
    * @param other The period to create a union period with this instance.
    * @return A new period that contains the intersection between the two
    * periods or null if the two periods do not intersect.
    */
   public Period union (final Period other)
   {
      final Period result;
      if (overlap(other))
      {
         final Date start = Date.earliest(mStartTime, other.mStartTime);
         final Date end = Date.latest(mEndTime, other.mEndTime);
         result = new Period(start, end);
      }
      else
      {
         result = null;
      }
      return result;
   }

   /**
    * Checks whether a specified date falls into the period defined by the
    * instance.
    *
    * @param date The date that is checked whether it lies in the period.
    * @return true when the date falls into the period, false otherwise.
    */
   public boolean isIncluded (final Date date)
   {
      return mStartTime.beforeOrEqual(date) && mEndTime.afterOrEqual(date);
   }

   /**
    * Checks whether a specified period falls into the period defined by the
    * instance.
    *
    * @param other The period that is checked whether it lies in this period.
    * @return true when the period falls into the period, false otherwise.
    */
   public boolean isIncluded (final Period other)
   {
      return mStartTime.beforeOrEqual(other.getStartTime())
            && mEndTime.afterOrEqual(other.getEndTime());
   }

   /**
    * Checks whether a specified period overlaps with this period.
    *
    * @param period The period to check for overlap with this period.
    * @return true when the periods overlap, false otherwise.
    */
   public boolean overlap (final Period period)
   {
      return isIncluded(period.mStartTime) || period.isIncluded(mStartTime);
   }

   /**
    * Returns the start time for the next period.
    *
    * @return the start time for the next period.
    */
   public Date getNextPeriodStartTime ()
   {
      return mEndTime.plus(1);
   }

   /**
    * Returns the end time of the previous period.
    *
    * @return the end time of the previous period.
    */
   public Date getPrevPeriodEndTime ()
   {
      return mStartTime.minus(1);
   }

   /**
    * Returns the duration of this Period in milliseconds.
    *
    * @return The duration of this Period in milliseconds.
    */
   public long duration ()
   {
      return mEndTime.getTime() - mStartTime.getTime();
   }

   /**
    * Returns the next Period. The duration of the next period is exactly the
    * same as duration of this Period. The next period starts 1 millisecond
    * after the end time of this period.
    *
    * @return The next Period.
    */
   public Period next ()
   {
      return new Period(getNextPeriodStartTime(),
            getNextPeriodStartTime().plus(duration()));
   }

   /**
    * Returns the previous Period. The duration of the previous period is
    * exactly the same as duration of this Period. The previous period ends 1
    * millisecond before the start time of this period.
    *
    * @return The previous Period.
    */
   public Period previous ()
   {
      return new Period(getPrevPeriodEndTime().minus(duration()),
            getPrevPeriodEndTime());
   }

   /**
    * Returns a Period object whose start/end time lies exactly
    * <code>offset</code> milliseconds after the start/end time of this period.
    *
    * @param offset The offset in milliseconds.
    *
    * @return The Period object whose start/end time lies exactly
    * <code>offset</code> milliseconds after the start/end time of this period.
    */
   public Period plus (long offset)
   {
      return new Period(mStartTime.plus(offset), mEndTime.plus(offset));
   }

   /**
    * Returns the next clock hour period, that lies after the
    * given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-09-03T12:00:00.000Z-2004-09-03T12:59:59.999Z.
    *
    * @param date The date to get the next clock hour period.
    *
    * @return The next clock hour period, that lies after the given
    * <code>date</code>.
    */
   public static Period nextHour (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Calendar s = getCalendarInstance(date);
      s.add(Calendar.HOUR, 1);
      resetMinorFields(s);
      return createPeriod(s, Date.MILLIS_PER_HOUR);
   }

   /**
    * Returns the next clock hour period, that lies after this period.
    * This method returns Period.nextHour(getEndTime()).
    *
    * @return the next clock hour period, that lies after this period.
    */
   public Period nextHour ()
   {
      return Period.nextHour(mEndTime);
   }

   /**
    * Returns the previous clock hour period, that lies befor the
    * given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-09-03T10:00:00.000Z-2004-09-03T10:59:59.999Z.
    *
    * @param date The date to get the previous clock hour period.
    *
    * @return the previous clock hour period, that lies befor the
    * given <code>date</code>.
    */
   public static Period previousHour (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Calendar s = getCalendarInstance(date);
      s.add(Calendar.HOUR, -1);
      resetMinorFields(s);
      return createPeriod(s, Date.MILLIS_PER_HOUR);
   }

   /**
    * Returns the next clock hour period, that lies this this period.
    * This method returns Period.previousHour(getSrartTime()).
    *
    * @return the next clock hour period, that lies before this period.
    */
   public Period previousHour ()
   {
      return Period.previousHour(mStartTime);
   }

   /**
    * Returns the day period, that lies after the given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-09-04T00:00:00.000Z-2004-09-04T23:59:59.999Z.
    *
    * @param date The date to get the next day period.
    *
    * @return The day period, that lies after the given <code>date</code>.
    */
   public static Period nextDay (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Date nextDay = date.plus(Date.MILLIS_PER_DAY);
      return Period.createDayPeriod(nextDay, nextDay);
   }

   /**
    * Returns the day period, that lies after this period.
    * This method returns Period.nextDay(getEndTime()).
    * @see #nextDay(Date)
    *
    * @return The day period, that lies after the given <code>date</code>.
    */
   public Period nextDay ()
   {
      return Period.nextDay(mEndTime);
   }

   /**
    * Returns the day period, that lies before the given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-09-02T00:00:00.000Z-2004-09-02T23:59:59.999Z.
    *
    * @param date The date to get the previous day period.
    *
    * @return The day period, that lies before the given <code>date</code>.
    */
   public static Period previousDay (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Date prevDay = date.minus(Date.MILLIS_PER_DAY);
      return Period.createDayPeriod(prevDay, prevDay);
   }

   /**
    * Returns the day period, that lies before this period.
    * This method returns Period.previousDay(getStartTime()).
    * @see #previousDay(Date)
    *
    * @return The the day period, that lies before this period.
    */
   public Period previousDay ()
   {
      return Period.previousDay(mStartTime);
   }

   /**
    * Returns the month period, that lies after the given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-10-01T00:00:00.000Z-2004-10-31T23:59:59.999Z.
    *
    * @param date The date to get the next month period.
    *
    * @return The the month period, that lies after the given <code>date</code>.
    */
   public static Period nextMonth (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Calendar c = getCalendarInstance(date);
      c.add(Calendar.MONTH, 1);
      return Period.createMonthPeriod(new Date(c.getTimeInMillis()));
   }

   /**
    * Returns the month period, that lies after this period.
    * This method returns Period.nextMonth(getEndTime()).
    * @see #nextMonth(Date)
    *
    * @return The the month period, that lies after this period.
    */
   public Period nextMonth ()
   {
      return Period.nextMonth(mEndTime);
   }

   /**
    * Returns the month period, that lies before the given <code>date</code>.
    *
    * Example: for the date 2004-09-03T11:52:03 this method will return the
    * period 2004-08-01T00:00:00.000Z-2004-08-31T23:59:59.999Z.
    *
    * @param date The date to get the previous month period.
    *
    * @return The the month period, that lies before the given
    * <code>date</code>.
    */
   public static Period previousMonth (Date date)
   {
      Assert.notNull(date, DATE_PARAMETER);
      final Calendar c = getCalendarInstance(date);
      c.add(Calendar.MONTH, -1);
      return Period.createMonthPeriod(new Date(c.getTimeInMillis()));
   }

   /**
    * Returns the month period, that lies before this period.
    * This method returns Period.previousMonth(getStartTime()).
    * @see #previousMonth(Date)
    *
    * @return The the month period, that lies before this period.
    */
   public Period previousMonth ()
   {
      return Period.previousMonth(mStartTime);
   }

   /**
    * Returns a Period object whose start/end time lies exactly
    * <code>offset</code> milliseconds before the start/end time of this period.
    *
    * @param offset The offset in milliseconds.
    *
    * @return The Period object whose start/end time lies exactly
    * <code>offset</code> milliseconds before the start/end time of this period.
    */
   public Period minus (long offset)
   {
      return new Period(mStartTime.minus(offset), mEndTime.minus(offset));
   }

   /**
    * Checks if this period is before the given period.
    * @param other the period to compare with.
    * @return true, if this period is before the given period.
    */
   public boolean before (final Period other)
   {
      Assert.notNull(other, "other");

      return mEndTime.before(other.getStartTime());
   }

   /**
    * Checks if this period is after the given period.
    * @param other the period to compare with.
    * @return true, if this period is after the given period.
    */
   public boolean after (final Period other)
   {
      Assert.notNull(other, "other");

      return mStartTime.after(other.getEndTime());
   }

   /** {@inheritDoc} */
   public boolean equals (Object o)
   {
      final boolean result;
      if (o instanceof Period)
      {
         final Period other = (Period) o;
         result = mStartTime.equals(other.mStartTime)
               && mEndTime.equals(other.mEndTime);
      }
      else
      {
         result = false;
      }
      return result;
   }

   /** {@inheritDoc} */
   public int hashCode ()
   {
      if (mHashCode == 0)
      {
         mHashCode = HashCodeUtil.SEED;
         mHashCode = HashCodeUtil.hash(mHashCode, mStartTime);
         mHashCode = HashCodeUtil.hash(mHashCode, mEndTime);
      }
      return mHashCode;
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      return mStartTime.toString() + "-" + mEndTime.toString();
   }

   private static void resetMinorFields (final Calendar s)
   {
      s.set(Calendar.MILLISECOND, s.getMinimum(Calendar.MILLISECOND));
      s.set(Calendar.SECOND, s.getMinimum(Calendar.SECOND));
      s.set(Calendar.MINUTE, s.getMinimum(Calendar.MINUTE));
   }

   private static Period createPeriod (final Calendar s,
         final long periodDuration)
   {
      final Date start = new Date(s.getTimeInMillis());
      return new Period(start, start.plus(periodDuration - 1));
   }

   static Calendar getCalendarInstance (final Date date)
   {
      final Calendar s = Calendar.getInstance(Date.TIME_ZONE);
      s.setLenient(false);
      s.clear();
      s.setTimeInMillis(date.getTime());
      return s;
   }
}
