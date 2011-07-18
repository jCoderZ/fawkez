/*
 * $Id: ParameterLineFormat.java 1518 2009-06-17 12:27:13Z amandel $
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.util.StringUtil;

/**
 * This class is used for formatting the parameters of a Loggable and parsing
 * one parameter log line.
 *
 */
public class ParameterLineFormat
      extends ContinuationLineFormat
{
   /** The number of fields added to the basic continuation line format.
     * The cause field is added.
     */
   private static final int NUMBER_OF_ADDITIONAL_PARAMETERS = 2;

   private static final int PARAMETER_NAME_INDEX = NUMBER_OF_PARAMETERS;
   private static final int PARAMETER_VALUE_INDEX = PARAMETER_NAME_INDEX + 1;

   /** The fields added to the basic format. Parameter name and parameter value
    * list are added.
    */
   private static final String ADDITIONAL_LOGLINE_FORMAT_PATTERN
        = " {" + PARAMETER_NAME_INDEX + "}: \t{" + PARAMETER_VALUE_INDEX + "}";

   /** The symbol name of a LogMessageInfo will be logged as parameter value
    * for a parameter with this name. */
   private static final String SYMBOL_TAG = LogItem.INTERNAL_PARAMETER_PREFIX
         + "SYMBOL_";

   /** The location where a Loggable is logged will be logged as parameter value
    * for a parameter with this name. */
   private static final String SOURCE_TAG = LogItem.INTERNAL_PARAMETER_PREFIX
         + "LOGGED_AT_";

   /** The possible solution for an error will be logged as parameter value
    * for a parameter with this name. */
   private static final String SOLUTION_TAG = LogItem.INTERNAL_PARAMETER_PREFIX
         + "SOLUTION_";

   /**
    * Creates a new instance of this and initializes the message format.
    */
   public ParameterLineFormat ()
   {
      super(LogLineFormat.PARAMETER_LINE, ADDITIONAL_LOGLINE_FORMAT_PATTERN,
            NUMBER_OF_ADDITIONAL_PARAMETERS);
      setFormats(getFormatList(null, true));
   }

   /**
    * Gets the formats as array for formatting a parameter line.
    *
    * @param options The display options specifying which fields to display.
    * Will be ignored and ,ight be null if <code>ignoreOptions == true</code>.
    * @param ignoreOptions flag whether to ignore the supplied options and
    * return the formats for all fields.
    *
    * @return array filled with formats for each selected field. Might be empty
    * array, never null.
    */
   static final Format[] getFormatList (
         final DisplayOptions options,
         final boolean ignoreOptions)
   {
      final List formatList = new ArrayList();
      if (ignoreOptions || options.displayThreadId())
      {
         // thread id
         formatList.add(getThreadIdFormat());
      }
      if (ignoreOptions || options.displayTrackingNumber())
      {
         // sequence of tracking id
         formatList.add(getTrackingNumberFormat());
      }
      // parameter name
      formatList.add(new AsItIsFormat(": \t"));
         // parameter value list, the list end char must be included in the
         // chars to escape.
      formatList.add(new WhitespaceFormat(
            new CollectionFormat(new StringEscapeFormat(",]"))));
      return (Format[]) formatList.toArray(EMPTY_FORMATTERS);
   }

   /**
    * Formats the parameters of a Loggable. Always adds the log location and
    * log message symbal as first parameters. This will format several lines
    * into the StringBuffer.
    * Append a line feed after the data has been formatted into the
    * StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted data.
    * @param record Unused, might be null.
    * @param loggable The Loggable to format.
    * @param trackingIdSequence The sequence of contributing tracking ids.
    * @param thrown Unused, might be null.
    * @param parameter Unused, might be null.
    */
   public void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List trackingIdSequence,
         final Throwable thrown,
         final Object parameter)
   {
      if ((parameter != null) && ! (parameter instanceof Throwable))
      {
         throw new IllegalArgumentException(
               "Parameter must be null or a Throwable, but is: " + parameter);
      }
      if (!StringUtil.isEmptyOrNull(loggable.getLogMessageInfo().getSymbol()))
      {
          setParameterName(SYMBOL_TAG);
          setParameterValues(Arrays.asList(
                new String[]{loggable.getLogMessageInfo().getSymbol()}));
          basicFormat(sb, record, loggable, trackingIdSequence);
      }

      final String solution = loggable.getLogMessageInfo().getSolution();
      if (!StringUtil.isEmptyOrNull(solution))
      {
          setParameterName(SOLUTION_TAG);
          setParameterValues(Arrays.asList(
                new String[]{solution}));
          basicFormat(sb, record, loggable, trackingIdSequence);
      }

      // log location
      final String location = getLogLocation(record);
      if (!".".equals(location))
      {
          setParameterName(SOURCE_TAG);
          setParameterValues(Arrays.asList(new String[]{location}));
          basicFormat(sb, record, loggable, trackingIdSequence);
      }

      appendParameters(sb, record, loggable, trackingIdSequence);
   }

   /**
    * Parses one line of log data and sets the data in the supplied
    * LogFileEntry.
    *
    * @param sb The StringBuffer containing the log line to parse from the
    * current position to the end.
    * @param entry The LogFileEntry which gets the data being parsed.
    *
    * @throws ParseException if an error occurs parsing the log line.
    */
   public void parse (StringBuffer sb, LogFileEntry entry)
         throws ParseException
   {
      try
      {
         basicParse(sb, entry);
         if (getParameterName().equals(SOURCE_TAG))
         {
            final String[] source
                  = getLogSource((String) getParameterValues().get(0));
            entry.setSourceClass(source[SOURCECLASS_INDEX]);
            entry.setSourceMethod(source[SOURCEMETHOD_INDEX]);
         }
         else if (getParameterName().equals(SOLUTION_TAG)
               && (getParameterValues() != null)
               && ! getParameterValues().isEmpty())
         {
            entry.setSolution((String) getParameterValues().get(0));
         }
         else if (getParameterName().equals(SYMBOL_TAG))
         {
            entry.setSymbol((String) getParameterValues().get(0));
         }
         else
         {
            entry.addToParameters(getParameterName(), getParameterValues());
         }
      }
      catch (ParseException pex)
      {
         // just rethrow
         throw pex;
      }
      catch (Exception ex)
      {
         final ParseException pex = new ParseException(
               "Got an error parsing " + sb, 0);
         pex.initCause(ex);
         throw pex;
      }
   }

   private void appendParameters (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List trackingIds)
   {
       final java.util.Set/*<String>*/ namesUnsorted
           = loggable.getParameterNames();
       final String[] names
           = (String[]) namesUnsorted.toArray(
               new String[namesUnsorted.size()]);
       Arrays.sort(names);

      for (final Iterator nameIter = Arrays.asList(names).iterator();
            nameIter.hasNext(); )
      {
         final String name = (String) nameIter.next();
         if (! name.startsWith(LogItem.INTERNAL_PARAMETER_PREFIX))
         {
            setParameterName(name);
            setParameterValues(loggable.getParameter(name));
            basicFormat(sb, record, loggable, trackingIds);
         }
      }
   }

   private void setParameterName (final String name)
   {
      setParameter(PARAMETER_NAME_INDEX, name);
   }

   private String getParameterName ()
   {
      return (String) getParameter(PARAMETER_NAME_INDEX);
   }

   private void setParameterValues (final List values)
   {
      setParameter(PARAMETER_VALUE_INDEX, values);
   }

   private List getParameterValues ()
   {
      return (List) getParameter(PARAMETER_VALUE_INDEX);
   }

   /**
    * Gets the log location as classname.methodname
    *
    * @param record The LogRecord wrapping the log location.
    *
    * @return the location where the log record was logged.
    */
   private String getLogLocation (final LogRecord record)
   {
      return record.getSourceClassName() + "." + record.getSourceMethodName();
   }
}
