/*
 * $Id: TraceLineFormat.java 1535 2009-07-12 08:31:31Z amandel $
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
import java.util.List;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.util.ArraysUtil;
import org.jcoderz.commons.util.ObjectUtil;
import org.jcoderz.commons.util.StringUtil;

/**
 * This formats a standard LogRecord and parses a log line with a formatted
 * log record.
 *
 */
public class TraceLineFormat
      extends BasicLogLineFormat
{
   /** The number of fields added to the basic format.
   The source and text field is added. */
   private static final int NUMBER_OF_ADDITIONAL_PARAMETERS = 2;

   private static final int LOGSOURCE_INDEX = NUMBER_OF_PARAMETERS;

   private static final int TEXT_INDEX = LOGSOURCE_INDEX + 1;

   /** The fields added to the basic format. No fields are added. */
   private static final String ADDITIONAL_LOGLINE_FORMAT_PATTERN
        = " {" + LOGSOURCE_INDEX + "} {" + TEXT_INDEX + "}";

   private static final String EMPTY_MSG = " ";

   /**
    * Creates a new instance of this and sets the default line type specifier.
    */
   TraceLineFormat ()
   {
      this(LogLineFormat.TRACE_MESSAGE);
   }

   /**
    * Creates a new instance of this and sets the supplied line type specifier.
    * This might be used by more specialized subtypes of this.
    *
    * @param type The LineTypeSpecifier to set.
    */
   protected TraceLineFormat (final LogLineType type)
   {
      super(type, ADDITIONAL_LOGLINE_FORMAT_PATTERN,
            NUMBER_OF_ADDITIONAL_PARAMETERS);
      setFormats(getFormatList(null, true));
   }

   /**
    * Formats the supplied LogRecord with the encapsulated message format.
    * Appends a line feed after the data is formatted into the StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted LogRecord.
    * @param record The LogRecord to format.
    * @param loggable Unused by this, might be null.
    * @param trackingIdSequence The list containing the sequence of tracking
    * ids contributing to this log message.
    * @param thrown Unused by this, might be null.
    * @param parameter Unused by this, might be null.
    *
    * @see LogLineFormat#format(StringBuffer, LogRecord, Loggable, List, Throwable, Object)
    */
   public void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final Object parameter)
   {
      if (record.getParameters() == null)
      {
         setMessageText(record.getMessage());
      }
      else
      {
        setMessageText(
          formatMessage(record.getMessage(), record.getParameters()));
      }
      setLogSource(record.getSourceClassName(), record.getSourceMethodName());
      basicFormat(sb, record, loggable, trackingIdSequence);
   }

   /**
    * Parses a log line, which must be formatted by this, and sets the
    * appropriate values of the supplied LogFileEntry.
    *
    * @param sb The StringBuffer containing the current log line.
    * @param entry The LogFileEntry for which to parse the log line.
    *
    * @throws ParseException if an error occurs.
    *
    * @see LogLineFormat#parse(StringBuffer, LogFileEntry)
    */
   public void parse (StringBuffer sb, LogFileEntry entry)
         throws ParseException
   {
      try
      {
         basicParse(sb, entry);
         entry.setMessage(getMessageText());
         final String[] logSource = getLogSource();

         entry.setSourceClass(logSource[SOURCECLASS_INDEX]);
         entry.setSourceMethod(logSource[SOURCEMETHOD_INDEX]);
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

   /**
    * Gets the formats as array for formatting a trace line.
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
      final List<Format> formatList = getBasicFormatList(options, ignoreOptions);
      if (ignoreOptions || options.displaySourceClass()
            || options.displaySourceMethod())
      {
         formatList.add(new AsItIsFormat(" \t"));
      }
      formatList.add(new WhitespaceFormat(new AsItIsFormat("\r\n")));
//      formatList.add(new AsItIsFormat("\r\n"));
      return (Format[]) formatList.toArray(EMPTY_FORMATTERS);
   }

   /**
    * Sets the message to format. If the supplied string is null or empty, a
    * space is set.
    *
    * @param text The message text.
    */
   protected final void setMessageText (final String text)
   {
      if (text == null || text.length() <= 0)
      {
         setParameter(TEXT_INDEX, EMPTY_MSG);
      }
      else
      {
         setParameter(TEXT_INDEX, text);
      }
   }

   /**
    * Gets the message text of a parsed log line.
    *
    * @return Message text of parsed log line.
    */
   protected final String getMessageText ()
   {
      return (String) getParameter(TEXT_INDEX);
   }

   /**
    * Sets the log source as classname.methodname()
    *
    * @param clazz The name of the class.
    * @param method The name of the method.
    */
   protected final void setLogSource (
         final String clazz, final String method)
   {
       final boolean needParentheses 
           = !StringUtil.isNullOrEmpty(method) && method.indexOf('(') < 0;
       setParameter(LOGSOURCE_INDEX,
           ObjectUtil.toStringOrEmpty(clazz) + "." 
               + ObjectUtil.toStringOrEmpty(method) 
               + (needParentheses ? "()" : ""));
   }

   /**
    * Gets the source class name and source method name where the Log record was
    * logged of a parsed log line.
    *
    * @return String array with source class name as first and source method
    * name as second parameter.
    */
   protected final String[] getLogSource ()
   {
      return getLogSource((String) getParameter(LOGSOURCE_INDEX));
   }

   private static final String formatMessage (String pattern, Object[] params)
   {
      String result;
      if (params != null && params.length != 0)
      {
          try
          {
              final MessageFormat formatter = new MessageFormat(pattern);
              result
                  = formatter.format(
                      params, new StringBuffer(), null).toString();
          }
          catch (IllegalArgumentException ex)
          {
              result
                  = ArraysUtil.toString(params) + " " + pattern;
          }
      }
      else
      {
          result = pattern;
      }
      return result;
   }

}
