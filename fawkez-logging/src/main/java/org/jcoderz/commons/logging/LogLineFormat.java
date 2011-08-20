/*
 * $Id: LogLineFormat.java 1535 2009-07-12 08:31:31Z amandel $
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


import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;

/**
 * This is the base class for various log line formats. It gives the defined
 * types of log lines and a common interface for formatting and parsing
 * different types of log lines.
 *
 */
public abstract class LogLineFormat
{
   /** Used for standard LogRecord logs. */
   public static final LogLineType TRACE_MESSAGE = new LogLineType('T');

   /** Used for standard LogRecord logs carrying a Throwable. */
   public static final LogLineType EXCEPTION_MESSAGE
         = new LogLineType('F');
   /** Used for Loggable logs. */
   public static final LogLineType LOG_MESSAGE
         = new LogLineType('M');
   /** Used for Loggable logs carrying a Throwable or being derived from a
    * Throwable. */
   public static final LogLineType ERROR_MESSAGE
         = new LogLineType('E');

   /** Used for nested loggables. */
   public static final LogLineType NESTED_MESSAGE
         = new LogLineType('N');
   /** Used for stack trace elements of logged Throwables. */
   public static final LogLineType STACKTRACE_MESSAGE
         = new LogLineType('S');

   /** Used for logging parameters as name and value list. */
   public static final LogLineType PARAMETER_LINE
         = new LogLineType('P');

   protected static final Format [] EMPTY_FORMATTERS = new Format[0];

   /** Index for source class name within String array as it is returned by
    * {@linkplain #getLogSource(String)}. */
   protected static final int SOURCECLASS_INDEX = 0;

   /** Index for source method name within String array as it is returned by
    * {@linkplain #getLogSource(String)}. */
   protected static final int SOURCEMETHOD_INDEX = 1;


   /* length of fixed length fields */
   private static final int NODEID_LENGTH = 15;
   private static final int INSTANCEID_LENGTH = 10;
   private static final int THREADID_LENGTH = 5;
   private static final int LOGGERLEVEL_LENGTH = 8;
   private static final int TRACKINGID_LENGTH = 8;
   private static final int SYMBOL_LENGTH = 8;
   private static final int BUSINESS_IMPACT_LENGTH = 9;
   private static final int CATEGORY_LENGTH = 9;

   private static final int NUMBER_OF_SOURCE_ELEMENTS = 2;

   private Object [] mLineItems;

   private final MessageFormat mMessageFormat;

   /**
    * This helper class is used as type safe enumeration for all defined
    * log line types.
    *
    */
   public static final class LogLineType
         implements Comparable<Object>
   {
      private static int sOrdinal = 0;
      private static final Map<Character, LogLineType> TYPE_CODE_MAPPING = new HashMap<Character, LogLineType>();

      private final int mOrdinal;

      private final char mTypeSpecifier;

      /**
       * Creates a new instance of this.
       *
       * @param typeSpecifier The code of this.
       */
      private LogLineType (final char typeSpecifier)
      {
         mTypeSpecifier = typeSpecifier;
         mOrdinal = sOrdinal++;
         TYPE_CODE_MAPPING.put(new Character(mTypeSpecifier), this);
      }

      /**
       * Gets the code of this.
       *
       * @return the code of this.
       */
      public char getTypeSpecifier ()
      {
         return mTypeSpecifier;
      }

      /**
       * Compares this to the supplied object.
       *
       * @param o The object to compare with this.
       *
       * @return result of compare as defined for {@link Comparable}.
       *
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo (Object o)
      {
         return mOrdinal - ((LogLineType) o).mOrdinal;
      }
   }

   /**
    * Creates and initializes a new instance of this.
    *
    * @param type The type of this.
    *
    * @param format The MessageFormat used for formatting and parsing a log line
    * of this type.
    *
    * @param numberOfArguments The number of arguments for the supplied message
    * format.
    */
   protected LogLineFormat (
         final LogLineType type,
         final MessageFormat format,
         final int numberOfArguments)
   {
      //UNUSED: mLogLineType = type;
      mMessageFormat = format;
      mLineItems = new Object[numberOfArguments];
   }

   /**
    * The common interface for all log line formatters. Not all parameters might
    * be used for implementations of this.
    * Common for all implementation is that they append a line feed after the
    * data has been formatted into the StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted data.
    * @param record THe LogRecord to format.
    * @param loggable The Loggable to format.
    * @param trackingIdSequence The sequence of contributing tracking ids.
    * @param thrown The Throwable to format.
    * @param parameter An additional parameter, which might be required for
    * an implementation of this.
    */
   public abstract void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final Object parameter);

   /**
    * The common interface for all log line formatters. A log line is parsed
    * and the data being retrieved is set within the supplied LogFileEntry.
    *
    * @param sb The StringBuffer containing the log line to parse from the
    * current position to the end.
    * @param entry The LogFileEntry which gets the data being parsed.
    *
    * @throws ParseException if an error occurs parsing the log line.
    */
   public abstract void parse (
         final StringBuffer sb,
         final LogFileEntry entry)
         throws ParseException;

   /**
    * Gets the LogLineType matching the supplied code.
    *
    * @param code The code for the LogLineType to return.
    *
    * @return LogLineType with code matching <code>code</code>.
    *
    * @throws IllegalArgumentException if no such LogLineType.
    */
   public static LogLineType getLogLineType (final char code)
       throws IllegalArgumentException
   {
      final LogLineType rc = (LogLineType) LogLineType.TYPE_CODE_MAPPING
            .get(new Character(code));
      if (rc == null)
      {
         throw new IllegalArgumentException("There is no LogLineType with "
               + "code " + code);
      }
      return rc;
   }


   /**
    * Sets the contributing formats for the encapsulated MessageFormat.
    *
    * @param formats The formats to set.
    */
   protected final void setFormats (Format[] formats)
   {
      mMessageFormat.setFormats(formats);
   }

   /**
    * Sets a parameter at the specified position to be used when formatting.
    *
    * @param index The index at which to set the parameter. It must hold true
    * <code>0 <= index < num parameters</code> with num parameter being the
    * number set when creating this.
    * @param obj The object to set at the supplied position.
    */
   protected final void setParameter (final int index, final Object obj)
   {
      mLineItems[index] = obj;
   }

   /**
    * Gets the parameter at the specified position.
    *
    * @param index The index from which to get the parameter. It must hold true
    * <code>0 <= index < num parameters</code> with num parameter being the
    * number set when creating this.
    *
    * @return The object at the supplied position.
    */
   protected final Object getParameter (final int index)
   {
      return mLineItems[index];
   }

   /**
    * Formats all parameters set so far into the supplied StringBuffer using the
    * encapsulated MessageFormat.
    *
    * @param sb The StringBuffer into which to format the data.
    */
   protected final void format (final StringBuffer sb)
   {
      mMessageFormat.format(mLineItems, sb, null);
   }

   /**
    * Parses the supplied StringBuffer from beginning to end with the
    * encapsulated MessageFormat. The parsed objects can be accessed by
    * calling {@linkplain #getParameter(int)} with the appropriate index.
    *
    * @param sb The StringBuffer from which to parse the parameter values.
    *
    * @throws ParseException if an error occurs parsing the string.
    */
   protected final void parse (final StringBuffer sb)
         throws ParseException
   {
      mLineItems = mMessageFormat.parse(sb.toString());
   }

   /**
    * Gets the format to use for formatting a thread id element.
    *
    * @return Format for formatting the thread id.
    */
   protected static final Format getThreadIdFormat ()
   {
      return new FixLengthFormat(
            THREADID_LENGTH, FixLengthFormat.LEFT_PADDING);
   }

   /**
    * Gets the format to use for formatting a timestamp element.
    *
    * @return Format for formatting the timestamp.
    */
   protected static final Format getTimestampFormat ()
   {
      return new TimestampFormat();
   }

   /**
    * Gets the format to use for formatting a node id element.
    *
    * @return Format for formatting the node id.
    */
   protected static final Format getNodeIdFormat ()
   {
      return new FixLengthFormat(
            NODEID_LENGTH, FixLengthFormat.LEFT_PADDING);
   }

   /**
    * Gets the format to use for formatting an instance id element.
    *
    * @return Format for formatting the instance id.
    */
   protected static final Format getInstanceIdFormat ()
   {
      return new FixLengthFormat(
            INSTANCEID_LENGTH, FixLengthFormat.RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting the logger /severity level element.
    *
    * @return Format for formatting the logger level.
    */
   protected static final Format getLoggerLevelFormat ()
   {
      return new FixLengthFormat(
            LOGGERLEVEL_LENGTH, FixLengthFormat.RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting a symbol id element.
    *
    * @return Format for formatting the message symbol.
    */
   protected static final Format getMessageSymbolFormat ()
   {
      return new FixLengthFormat(
            SYMBOL_LENGTH, FixLengthFormat.RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting the business impact element.
    *
    * @return Format for formatting the business impact.
    */
   protected static final Format getBusinessImpactFormat ()
   {
      return new FixLengthFormat(
            BUSINESS_IMPACT_LENGTH, FixLengthFormat.RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting the category element.
    *
    * @return Format for formatting the category.
    */
   protected static final Format getCategoryFormat ()
   {
      return new FixLengthFormat(
            CATEGORY_LENGTH, FixLengthFormat.RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting the thread name element.
    *
    * @return Format for formatting the category.
    */
   protected static final Format getThreadNameFormat ()
   {
      return new FixLengthFormat(
            CATEGORY_LENGTH, FixLengthFormat.LEFT_CUT_RIGHT_PADDING);
   }

   /**
    * Gets the format to use for formatting the tracking numbers
    *
    * @return Format for formatting the tracking numbers.
    */
   protected static final Format getTrackingNumberFormat ()
   {
      return new CollectionFormat(new FixLengthFormat(
            TRACKINGID_LENGTH, FixLengthFormat.LEFT_PADDING, '0'),
            null, null, ">-");
   }

   /**
    * Gets the source class name and source method name where the Log record was
    * logged from the supplied string.
    * Use {@linkplain #SOURCECLASS_INDEX} and {@linkplain #SOURCEMETHOD_INDEX}
    * for accessing the appropriate values in the string array being returned.
    *
    * @param source the log source in format classname.methodname
    *
    * @return String array with source class name as first and source method
    * name as second parameter.
    */
   protected final String [] getLogSource (final String source)
   {
      int afterMethodName = source.lastIndexOf('(');
      // if the loggable has not yet filled stack trace the source does not
      // contain the '()' part, thus we will take the whole string length.
      if (afterMethodName == -1)
      {
         afterMethodName = source.length() - 1;
      }
      final int beforeMethodName = source.lastIndexOf('.', afterMethodName);
      final String[] splittedSource = new String[NUMBER_OF_SOURCE_ELEMENTS];
      splittedSource[SOURCECLASS_INDEX]
                     = source.substring(0, beforeMethodName);
      if (beforeMethodName + 1 < source.length())
      {
          splittedSource[SOURCEMETHOD_INDEX]
                            = source.substring(beforeMethodName + 1);
      }
      else
      {
          splittedSource[SOURCEMETHOD_INDEX] = "";
      }
      return splittedSource;
   }
}
