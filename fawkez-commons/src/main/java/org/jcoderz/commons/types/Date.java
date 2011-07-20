/*
 * $Id: Date.java 1093 2008-07-24 06:30:14Z amandel $
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.jcoderz.commons.util.Constants;
import org.jcoderz.commons.util.HashCodeUtil;



/**
 * Immutable holder of a Date running through our system.
 *
 * This class also holds some time related constants to be used in the code.
 *
 * Years before 1 are not fully supported and might result in wrong string
 * representations.
 *
 * @author Andreas Mandel
 */
public final class Date
      implements Comparable<Date>, Serializable
{
   /** Date Formater to use for DATE_TIME_FORMAT_WITH_MILLIS format. */
   public static final ThreadLocal<?> DATE_TIME_FORMAT_WITH_MILLIS_FORMATER
         = new ThreadLocal<Object>()
         {
            /**
             * Thread local date formater.
             * @see java.lang.ThreadLocal#initialValue()
             */
            protected Object initialValue ()
            {
               final DateFormat formater
                  = new SimpleDateFormat(DATE_TIME_FORMAT_WITH_MILLIS,
                     Constants.SYSTEM_LOCALE);
               formater.setTimeZone(TIME_ZONE);
               formater.setLenient(false);
               return formater;
            }
         };

   /** Date Formater to use for DATE_TIME_FORMAT format. */
   public static final ThreadLocal<?> DATE_TIME_FORMAT_FORMATER
         = new ThreadLocal<Object>()
         {
            /**
             * Thread local date formater.
             * @see java.lang.ThreadLocal#initialValue()
             */
            protected Object initialValue ()
            {
               final DateFormat formater
                     = new SimpleDateFormat(DATE_TIME_FORMAT,
                        Constants.SYSTEM_LOCALE);
               formater.setTimeZone(TIME_ZONE);
               formater.setLenient(false);
               return formater;
            }
         };

   /**
    * The name of this type.
    */
   public static final String TYPE_NAME = "Date";

   /**
    * Timezone used by the protocol.
    */
   public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

   /** Number of nano seconds in a milli second. */
   public static final long NANOS_PER_MILLI = 1000000L;

   /** Number of milli seconds in a second. */
   public static final int MILLIS_PER_SECOND = 1000;

   /** Number of nano seconds in a second. */
   public static final long NANOS_PER_SECOND
           = NANOS_PER_MILLI * MILLIS_PER_SECOND;

   /**
    * Number of seconds in a minute.
    */
   public static final int SECONDS_PER_MINUTE = 60;

   /**
    * Number of milli seconds in a minute.
    */
   public static final int MILLIS_PER_MINUTE
         = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;

   /**
    * Number of milli seconds in an hour.
    */
   public static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
   /**
    * Number of milliseconds per day.
    */
   public static final int MILLIS_PER_DAY = MILLIS_PER_MINUTE * 60 * 24;

   /**
    * Number of milliseconds per week.
    */
   public static final int MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;

   /**
    * Number of months per year.
    */
   public static final int MONTH_PER_YEAR = 12;

   /**
    * This date represents the 1&#46;1&#46;1970 00:00:00.000.
    */
   public static final Date OLD_DATE = new Date(0);

   /**
    * This date represents the largest possible date
    * (9999 * 365 * 24 * 60 * 60 * 1000).
    */
   public static final Date FUTURE_DATE = new Date(315328464000000L);

   /**
    * The format used to write schema dateTime types.
    */
   public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /**
    * The format used to write schema dateTime types, if milli seconds
    * are not equal 0.
    */
   public static final String DATE_TIME_FORMAT_WITH_MILLIS
         = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

   /**
    * The format used to parse and write schema date types.
    */
   public static final String DATE_FORMAT = "yyyy-MM-dd'Z'";

   /**
    * use this serialVersionUID for serialization.
    */
   static final long serialVersionUID = -5924234143761665938L;

   /**
    * The number of (milli)seconds since January 1, 1970, 00:00:00 GMT
    * represented by this date.
    * The millis are always reset to 0.
    */
   private final long mTime;

   /**
    * Holds the string representation once it was computed.
    */
   private transient String mString = null;


   /**
    * Creates a new instance of Date taking the given long as ms from
    * 1&#46;1&#46;1970.
    *
    * @param time the number of milliseconds.
    *
    */
   public Date (long time)
   {
      mTime = time;
   }


   /**
    * Creates a new instance of Date taking the given long as ms from
    * 1&#46;1&#46;1970.
    *
    * @param time the number of milliseconds.
    * @return a newly generated Date object taking the given long as ms from
    *       &#46;1&#46;1970.
    * @see java.util.Date#Date()
    */
   public static Date fromLong (long time)
   {
      return new Date(time);
   }

   /**
    * Creates a new instance of Date from a java&#46;util&#46;Date Object.
    * @param date the date as {@link java.sql.Date java.util.Date}.
    * @return a newly generated Date object representing the same time as the
    *         given date.
    */
   public static Date fromUtilDate (java.util.Date date)
   {
      return new Date(date.getTime());
   }

   /**
    * Creates a new instance of Date holding the value as found in the given
    * java&#46;sql&#46;Date.
    * @param date the date as {@link java.sql.Date java.sql.Date}.
    * @return a newly generated Date object representing the same time as the
    *         given date.
    */
   public static Date fromSqlDate (java.sql.Date date)
   {
      return new Date(date.getTime());
   }

   /**
    * Creates a new instance of Date holding the value as found in the given
    * java&#46;sql&#46;Timestamp.
    * @param timestamp the date as
    *        {@link java.sql.Timestamp java.sql.Timestamp}.
    * @return a newly generated Date object representing the same time as the
    *         given date.
    */
   public static Date fromSqlTimestamp (java.sql.Timestamp timestamp)
   {
      return new Date(timestamp.getTime());
   }

   /**
    * Parses the given String with the given format pattern.
    * NULL is returned if an empty string "" is given as parameter. (This is
    * to allow empty elements as used in some places.)
    * @param date the date in given notation
    * @param pattern the pattern to be used for parsing.
    * @return a newly generated Date object representing the time given in the
    *         date or NULL, if the given parameter was an empty string "".
    * @throws ParseException if the given date is not in the format of the
    *         given pattern.
    * @see SimpleDateFormat
    */
   public static Date fromString (String date, String pattern)
         throws ParseException
   {
      Date result = null;

      if (date.length() != 0)
      {
         final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern,
            Constants.SYSTEM_LOCALE);
         dateFormat.setLenient(false);
         dateFormat.setTimeZone(TIME_ZONE);
         result = Date.fromUtilDate(dateFormat.parse(date));
      }
      return result;
   }

   /**
    * Parses the given String as returned by the toString method.
    * @param date the date in String representation
    * @return a newly generated Date object representing the time given in the
    *         date, or null if the input string was null.
    * @throws ParseException if the given date is not in the format of the
    *         Date.toString() format.
    */
   public static Date fromString (String date)
         throws ParseException
   {
      Date result;
      if (date != null && date.length() != 0)
      {
         try
         {
            result = Date.fromUtilDate(
                  ((DateFormat) DATE_TIME_FORMAT_WITH_MILLIS_FORMATER.get()).
                     parse(date));
         }
         catch (ParseException x)
         {
            result = Date.fromUtilDate(
                  ((DateFormat) DATE_TIME_FORMAT_FORMATER.get()).
                     parse(date));
         }
      }
      else
      {
         result = null;
      }
      return result;
   }

   /**
    * Returns a Date object that holds the current time.
    * @return a newly generated Date object representing current time.
    */
   public static Date now ()
   {
      return new Date(System.currentTimeMillis());
   }

   /**
    * Returns a Date object that holds the current time plus the given
    * milliseconds in the future.
    * @param offset the number of millis to step into the future.
    * @return a newly generated Date object.
    */
   public static Date nowPlus (long offset)
   {
      return new Date(System.currentTimeMillis() + offset);
   }

   /**
    * Returns the number of days since the Unix Epoch (1970/01/01).
    * @return the number of days since the Unix Epoch (1970/01/01).
    */
   public static int getDaysSinceEpoch ()
   {
      return (int) (System.currentTimeMillis() / MILLIS_PER_DAY);
   }

   /**
    * Returns the number of days between Unix Epoch (1970/01/01) and
    * <code>d</code>.
    * @param d the date until when the number of days should be computed
    * @return the number of days between Unix Epoch and <code>d</code>.
    */
   public static int getDaysSinceEpoch (Date d)
   {
      return (int) (d.getTime() / MILLIS_PER_DAY);
   }

   /**
    * Returns the earliest of the two dates.
    * @param a date a to compare.
    * @param b date b to compare.
    * @return the earliest of the two dates.
    */
   public static Date earliest (Date a, Date b)
   {
      return a.before(b) ? a : b;
   }

   /**
    * Returns the latest of the two dates.
    * @param a date a to compare.
    * @param b date b to compare.
    * @return the earliest of the two dates.
    */
   public static Date latest (Date a, Date b)
   {
      return a.before(b) ? b : a;
   }

   /**
    * Returns a Date object that holds the time of this object plus the given
    * milliseconds in the future.
    * @param offset the number of millis to step into the future.
    * @return a newly generated Date object.
    */
   public Date plus (long offset)
   {
      return new Date(mTime + offset);
   }

   /**
    * Returns a Date object that holds the time of this object minus the given
    * milliseconds in the past.
    * @param offset the number of millis to step back from this date.
    * @return a newly generated Date object.
    */
   public Date minus (long offset)
   {
      return new Date(mTime - offset);
   }

   /**
    * Returns the date represented by this object as newly generated
    * {@link java.util.Date java.util.Date} object.
    * @return the date represented by this object as newly generated
    * {@link java.util.Date java.util.Date} object.
    */
   public java.util.Date toUtilDate ()
   {
      return new java.util.Date(mTime);
   }

   /**
    * Returns the date represented by this object as newly generated
    * {@link java.sql.Date java.sql.Date} object.
    * @return the date represented by this object as newly generated
    * {@link java.sql.Date java.sql.Date} object.
    */
   public java.sql.Date toSqlDate ()
   {
      return new java.sql.Date(mTime);
   }

   /**
    * Returns the date represented by this object as newly generated
    * {@link java.sql.Timestamp java.sql.Timestamp} object.
    * @return the date represented by this object as newly generated
    * {@link java.sql.Timestamp java.sql.Timestamp} object.
    */
   public java.sql.Timestamp toSqlTimestamp ()
   {
      return new java.sql.Timestamp(mTime);
   }

    /**
     * Returns a hash code value for this object. The result is the
     * exclusive OR of the two halves of the primitive <tt>long</tt>
     * value returned by the {@link Date#getTime()}
     * method. That is, the hash code is the value of the expression:
     * <blockquote><pre>
     * (int)(this.getTime()^(this.getTime() &gt;&gt;&gt; 32))</pre></blockquote>
     *
     * @return  a hash code value for this object.
     */
   public int hashCode ()
   {
      return HashCodeUtil.hash(HashCodeUtil.SEED, mTime);
   }

   /**
     * Compares two dates for equality.
     * @param obj the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
    */
   public boolean equals (Object obj)
   {
      return ((obj instanceof Date) && (((Date) obj).mTime == mTime));
   }

   /**
    * Returns the date as formatted string using the given pattern.
    * @param pattern the pattern describing the date and time format
    * @return the date as formatted string using the given pattern.
    * @see SimpleDateFormat
    */
   public String toString (String pattern)
   {
      final SimpleDateFormat dateFormat
            = new SimpleDateFormat(pattern, Constants.SYSTEM_LOCALE);
      dateFormat.setTimeZone(TIME_ZONE);
      return dateFormat.format(new java.util.Date(mTime));
   }

   /**
    * Returns the String representation format is according schema dateTime
    * representation.
    * @return the String representation format is according schema dateTime
    * representation.
    */
   public String toString ()
   {
      if (mString == null)
      {
         mString
               = ((DateFormat) DATE_TIME_FORMAT_WITH_MILLIS_FORMATER.get()).
                  format(toUtilDate());
      }
      return mString;
   }

   /**
    * Returns the String representation format is according schema date
    * representation.
    * @return the String representation format is according schema date
    * representation.
    */
   public String toDateString ()
   {
      // To gain speed we might use a thread local SimpleDateFormat
      return toString(DATE_FORMAT);
   }

   /**
    * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
    * represented by this Date object.
    * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT
    * represented by this Date object.
    */
   public long getTime ()
   {
      return mTime;
   }

    /**
     * Compares this Date to another Object.
     *
     * @param o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a Date
     *      equal to this Date; a value less than <code>0</code> if the
     *      argument is a Date after this Date; and a value greater than
     *      <code>0</code> if the argument is a Date before this Date.
     * @exception ClassCastException if the argument is not a
     *        <code>org.jcoderz.ipp.Date</code>.
     * @exception NullPointerException if the argument is <code>null</code>.
     * @see     java.lang.Comparable
     */
   public int compareTo (Date other)
         throws NullPointerException, ClassCastException
   {
      int result = 0;

      if (before(other))
      {
         result = -1;
      }
      else if (after(other))
      {
         result = 1;
      }
      return result;
   }

   /**
    * Checks if the this date is before the given date.
    * @param other the date to compare with.
    * @return true, if the this date is before the given date.
    */
   public boolean before (Date other)
   {
      return mTime < other.mTime;
   }

   /**
    * Checks if the this date is before or equal to the given date.
    * @param other the date to compare with.
    * @return true, if the this date is before or equal to the given date.
    */
   public boolean beforeOrEqual (Date other)
   {
      return mTime <= other.mTime;
   }

   /**
    * Checks if this date is after the given date.
    * @param other the date to compare with.
    * @return true, if this date is after the given date.
    */
   public boolean after (Date other)
   {
      return mTime > other.mTime;
   }

   /**
    * Checks if this date is after or equal to the given date.
    * @param other the date to compare with.
    * @return true, if this date is after or equal to the given date.
    */
   public boolean afterOrEqual (Date other)
   {
      return mTime >= other.mTime;
   }

   /**
    * Returns the elapsed number of milliseconds from this date to now.
    * @return the elapsed number of milliseconds from this date to now.
    */
   public long elapsedMillis ()
   {
      return System.currentTimeMillis() - mTime;
   }

   /**
    * Returns the elapsed number of milliseconds from this date to other.
    * The result is negative if <tt>other</tt> is before this date.
    * @param other the date to compare with this date.
    * @return the elapsed number of milliseconds from this date to other.
    */
   public long elapsedMillis (Date other)
   {
      return other.mTime - mTime;
   }

   /**
    * Read resolve method to ensure the class invariants.
    * @return a Date instance that surely fulfils the class invariants.
    */
   private Object readResolve ()
   {
      return new Date(mTime);
   }
}
