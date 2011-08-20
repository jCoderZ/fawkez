/*
 * $Id: StackTraceFormat.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.nio.CharBuffer;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;

/**
 * This class is used for formatting the stack trace of a Throwable or for
 * parsing one line of stack trace.
 *
 */
public final class StackTraceFormat
      extends ContinuationLineFormat
{
   /** The number of fields added to the basic continuation line format.
     * The cause field is added.
     */
   private static final int NUMBER_OF_ADDITIONAL_PARAMETERS = 1;

   private static final int TEXT_INDEX = NUMBER_OF_PARAMETERS;

   /** The fields added to the basic format. Parameter name and parameter value
    * list are added.
    */
   private static final String ADDITIONAL_LOGLINE_FORMAT_PATTERN
        = " {" + TEXT_INDEX + "}";

   private static final String CAUSED_BY_CLAUSE = "Caused by: ";
   private static final String AT_CLAUSE = "  at ";

   /**
    * Creates a new instance of this and initializes the message format.
    */
   public StackTraceFormat ()
   {
      super(LogLineFormat.STACKTRACE_MESSAGE, ADDITIONAL_LOGLINE_FORMAT_PATTERN,
            NUMBER_OF_ADDITIONAL_PARAMETERS);
      setFormats(getFormatList(null, true));
   }

   /**
    * Gets the formats as array for formatting a stack trace line.
    *
    * @param options The display options specifying which fields to display.
    * Will be ignored and ,ight be null if <code>ignoreOptions == true</code>.
    * @param ignoreOptions flag whether to ignore the supplied options and
    * return the formats for all fields.
    *
    * @return array filled with formats for each selected field. Might be empty
    * array, never null.
    */
   static Format[] getFormatList (
         final DisplayOptions options,
         final boolean ignoreOptions)
   {
      final List<Format> formatList = new ArrayList<Format>();
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
      formatList.add(new AsItIsFormat("\r\n"));
      return (Format[]) formatList.toArray(EMPTY_FORMATTERS);
   }

   /**
    * Formats a Throwable.
    * Append a line feed after the data has been formatted into the
    * StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted data.
    * @param record The LogRecord to format, if <code>loggable</code> is not
    * null, this is unused and might be null.
    * @param loggable The Loggable to format, might be null, but then
    * <code>record</code> must not be null.
    * @param trackingIdSequence The sequence of contributing tracking ids.
    * @param thrown The Throwable to format.
    * @param parameter The Throwable containing <code>thrown</code> as cause.
    * Might be null, if no such. Must be instance of Throwable if not null.
    */
   public void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final Object parameter)
   {
      if ((parameter != null) && ! (parameter instanceof Throwable))
      {
         throw new IllegalArgumentException(
               "Parameter must be null or a Throwable, but is: " + parameter);
      }
      final Throwable parentThrown = (Throwable) parameter;

      appendThrown(sb, record, loggable, trackingIdSequence, thrown,
            parentThrown == null);
      appendStackTrace(sb, record, loggable, trackingIdSequence, thrown,
            parentThrown);
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
         parse(sb);
         entry.addToStackTrace(
               StackTraceElementParser.parse(CharBuffer.wrap(getText())));
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

   private void appendThrown (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final boolean topLevel)
   {
      final StringBuffer text = new StringBuffer();
      if (! topLevel)
      {
         text.append(CAUSED_BY_CLAUSE);
      }
      text.append(thrown.toString());
      setText(text.toString());
      basicFormat(sb, record, loggable, trackingIdSequence);
   }

   private void appendStackTrace (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final Throwable outerTrace)
   {
      // Compute number of frames in common between this and caused
      final StackTraceElement[] trace = thrown.getStackTrace();
      final int framesInCommon;
      int uniqueFrames = trace.length - 1;
      if (outerTrace != null)
      {
         final StackTraceElement[] causedTrace = outerTrace.getStackTrace();
         int n = causedTrace.length - 1;
         while (uniqueFrames >= 0 && n >= 0
               && trace[uniqueFrames].equals(causedTrace[n]))
         {
            uniqueFrames--; n--;
         }
         framesInCommon = trace.length - 1 - uniqueFrames;
      }
      else
      {
         framesInCommon = 0;
      }

      for (int i = 0; i <= uniqueFrames; ++i)
      {

//         sb.append(Constants.LINE_SEPARATOR);

         setText(AT_CLAUSE + trace[i].toString());

         basicFormat(sb, record, loggable, trackingIdSequence);
      }
      if (framesInCommon != 0)
      {
//         sb.append(Constants.LINE_SEPARATOR);
         setText("..." + framesInCommon + " more");
         basicFormat(sb, record, loggable, trackingIdSequence);
//         format(sb);
      }
   }

   private void setText (final String text)
   {
      setParameter(TEXT_INDEX, text);
   }

   private String getText ()
   {
      return (String) getParameter(TEXT_INDEX);
   }
}
