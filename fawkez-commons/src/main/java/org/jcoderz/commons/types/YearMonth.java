/*
 * $Id: YearMonth.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.util.Assert;


/**
 * Encapsulates the Year Month type.
 * Instances of this class are immutable.
 * Years before 1 are not fully supported and might result in wrong string
 * representations. Not parseable time zones might be ignored.
 * @author Andreas Mandel
 */
public final class YearMonth
      implements Serializable
{
   /** The name of this type. */
   public static final String TYPE_NAME = "YearMonth";

   /** Minimum length for the year. */
   public static final int MINIMUM_NUMBER_OF_YEAR_DIGITS = 4;

   /** Fixed length for the month. */
   public static final int MONTH_LENGTH = 2;

   private static final int TWO_DIGIT_MONTH = 10;

   /** The <code>serialVersionUID</code>. */
   private static final long serialVersionUID = 1L;

   private final int mYear;
   /** Month counting from 1 (January) to 12 (December). */
   private final int mMonth;

   // Lazy init members
   private transient Date mEndDate;
   private transient Date mStartDate;
   private transient int mHashCode;
   private transient String mString;
   private transient Period mPeriod;

   /**
    *
    */
   private YearMonth (int year, int month)
   {
      if (1 > month || month > Date.MONTH_PER_YEAR)
      {
         throw new ArgumentMalformedException(TYPE_NAME, String.valueOf(month),
               "Month must be between 1 and 12.");
      }
      if (year == 0)
      {
         throw new ArgumentMalformedException(TYPE_NAME, String.valueOf(year),
               "Value of Year must not be 0.");
      }
      mYear = year;
      mMonth = month;
   }

   /**
    * Parses a valid XML representation of the gYearMonth type.
    * The format is <tt>CCYY-MM</tt>.
    * @param str the string representing the year month.
    * @return a YearMonth object representing the given year month.
    */
   public static YearMonth fromString (String str)
   {
      Assert.notNull(str, TYPE_NAME);
      // find separating '-' char.
      final int minusPos = str.indexOf('-', 1);
      if (minusPos == -1)
      {
         throw new ArgumentMalformedException(TYPE_NAME, str,
               "MonthYear type must contain a '-' character. (CCYY-MM)");
      }
      if ((minusPos + 1) < MINIMUM_NUMBER_OF_YEAR_DIGITS)
      {
         throw new ArgumentMalformedException(TYPE_NAME, str,
               "MonthYear type have at least "
               + MINIMUM_NUMBER_OF_YEAR_DIGITS + " digits in front of the '-' "
               + "character. (CCYY-MM)");
      }
      final int year;
      try
      {
         year = Integer.parseInt(str.substring(0, minusPos));
      }
      catch (NumberFormatException ex)
      {
         throw new ArgumentMalformedException(TYPE_NAME, str,
               "Failed to parse year. (CCYY-MM)", ex);
      }
      if (str.length() - minusPos <= MONTH_LENGTH)
      {
         throw new ArgumentMalformedException(TYPE_NAME, str,
               "Month must be 2 digits long. (CCYY-MM)");
      }
      final int month;
      try
      {
         month = Integer.parseInt(str.substring(minusPos + 1,
               minusPos + 1 + MONTH_LENGTH));
      }
      catch (NumberFormatException ex)
      {
         throw new ArgumentMalformedException(TYPE_NAME, str,
               "Failed to parse month. (CCYY-MM)", ex);
      }
      if (str.length() > minusPos + 1 + MONTH_LENGTH)
      {
         // Check timezone
         final String tz = str.substring(minusPos + 1 + MONTH_LENGTH);
         final TimeZone timeZone = TimeZone.getTimeZone(tz);
         if (timeZone.getRawOffset() != 0)
         {
            throw new ArgumentMalformedException(TYPE_NAME, str,
                  "Only UTC is supported as time zone, not '" + tz + "'.");
         }
      }
      return new YearMonth(year, month);
   }

   /**
    * Returns the valid date that represents the beginning of the
    * month year type.
    * The date in time is the first second in the month year.
    * @return a date denoting the first second in the month year.
    */
   public Date toStartDate ()
   {
      if (mStartDate == null)
      {
         final Calendar cal = Calendar.getInstance(Date.TIME_ZONE);
         cal.setLenient(false);
         cal.clear();
         if (getYear() > 0)
         {
            cal.set(getYear(), getMonth() - 1, 1);
         }
         else
         {
            cal.set(-getYear(), getMonth() - 1, 1);
            cal.set(Calendar.ERA, GregorianCalendar.BC);
         }
         cal.set(Calendar.DAY_OF_MONTH,
            cal.getActualMinimum(Calendar.DAY_OF_MONTH));
         cal.set(Calendar.HOUR_OF_DAY,
            cal.getActualMinimum(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
         cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
         cal.set(Calendar.MILLISECOND,
               cal.getActualMinimum(Calendar.MILLISECOND));
         mStartDate = new Date(cal.getTimeInMillis());
      }
      return mStartDate;
   }

   /**
    * Returns the valid date that represents the end of the
    * month year type.
    * The date in time is the first second in the month year.
    * @return a date denoting the first second in the month year.
    */
   public Date toEndDate ()
   {
      if (mEndDate == null)
      {
         final Calendar cal = Calendar.getInstance(Date.TIME_ZONE);
         cal.setLenient(false);
         cal.clear();
         if (getYear() > 0)
         {
            cal.set(getYear(), getMonth() - 1, 1);
         }
         else
         {
            cal.set(-getYear(), getMonth() - 1, 1);
            cal.set(Calendar.ERA, GregorianCalendar.BC);
         }
         cal.set(Calendar.DAY_OF_MONTH,
            cal.getActualMaximum(Calendar.DAY_OF_MONTH));
         cal.set(Calendar.HOUR_OF_DAY,
            cal.getActualMaximum(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
         cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
         cal.set(Calendar.MILLISECOND,
               cal.getActualMaximum(Calendar.MILLISECOND));
         mEndDate = new Date(cal.getTimeInMillis());
      }
      return mEndDate;
   }

   /**
    * Returns this as period from start date to end date of this
    * YearMonth.
    * @return a period representing the time period of this year month.
    */
   public Period toPeriod ()
   {
      if (mPeriod == null)
      {
         mPeriod = Period.createPeriod(toStartDate(), toEndDate());
      }
      return mPeriod;
   }

   /**
    * Returns the month counting from 1 (January) to 12 (December).
    * @return The month counting from 1 (January) to 12 (December).
    */
   public int getMonth ()
   {
      return mMonth;
   }
   /**
    * @return Returns the year.
    */
   public int getYear ()
   {
      return mYear;
   }

   /**
    * Returns true if the given date is within this year/month.
    * @param date the point in time to check.
    * @return true if the given date is within this year/month.
    */
   public boolean isWithin (Date date)
   {
      final long current = date.getTime();
      return toStartDate().getTime() <= current
            && current <= toEndDate().getTime();
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      if (mString == null)
      {
         String year;
         if (mYear >= 0)
         {
            year = Integer.toString(mYear);
            if (year.length() < MINIMUM_NUMBER_OF_YEAR_DIGITS)
            {
               year = "0000".substring(year.length()) + year;
            }
         }
         else
         {
            year = Integer.toString(-mYear);
            if (year.length() < MINIMUM_NUMBER_OF_YEAR_DIGITS)
            {
               year = "0000".substring(year.length()) + year;
            }
            year = "-" + year;
         }
         if (mMonth < TWO_DIGIT_MONTH)
         {
            mString = year + "-0" + Integer.toString(mMonth) + 'Z';
         }
         else
         {
            mString = year + '-' + Integer.toString(mMonth) + 'Z';
         }
      }
      return mString;
   }

   /** {@inheritDoc} */
   public int hashCode ()
   {
      if (mHashCode == 0)
      {
         mHashCode = mYear * Date.MONTH_PER_YEAR + mMonth;
      }
      return mHashCode;
   }

   /** {@inheritDoc} */
   public boolean equals (Object o)
   {
      final boolean result;
      if (o instanceof YearMonth)
      {
         final YearMonth other =  (YearMonth) o;
         result = other.mMonth == mMonth && other.mYear == mYear;
      }
      else
      {
         result = false;
      }
      return result;
   }
}
