/*
 * $Id: ContinuationLineFormat.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.util.Constants;


/**
 * This is the basic format for all formatters formatting continuation lines.
 *
 */
public abstract class ContinuationLineFormat
      extends LogLineFormat
{
   /** The number of parameters of the basic format. */
   protected static final int NUMBER_OF_PARAMETERS = 2;

   private static final int THREADID_INDEX = 0;
   private static final int TRACKINGID_INDEX = 1;

   /**
    * Common format for CA Unicenter log lines.
    * Has the fields in following order:
    * <ul>
    * <li>thread id
    * <li>tracking number
    * </ul>
    */
   private static final String LOGLINE_FORMAT_PATTERN
         = "{0} {1}";

   @SuppressWarnings("unused")
   private final int mNumParameters;
   @SuppressWarnings("unused")
   private final String mLogFormatPattern;


   /**
    * Creates a new instance of this and initializes the message format.
    *
    * @param type THe log line type.
    * @param additionalPattern The pattern for additional fields not included by
    * this. Must start with the desired delimiter before the first field.
    * @param numAdditionalParameters The number of additional fields.
    */
   protected ContinuationLineFormat (
         final LogLineType type,
         final String additionalPattern,
         final int numAdditionalParameters)
   {
      super(type, new MessageFormat(type.getTypeSpecifier() + " "
            + LOGLINE_FORMAT_PATTERN + additionalPattern),
            NUMBER_OF_PARAMETERS + numAdditionalParameters);

      mNumParameters = NUMBER_OF_PARAMETERS + numAdditionalParameters;
      mLogFormatPattern = LOGLINE_FORMAT_PATTERN + additionalPattern;
   }

   /**
    * Gets the formats as array for formatting all elements of a basic log line.
    *
    * @param options The display options specifying which fields to display.
    * Will be ignored and might be null if <code>ignoreOptions == true</code>.
    * @param ignoreOptions flag whether to ignore the supplied options and
    * return the formats for all fields.
    *
    * @return List filled with formats for each selected field. Might be empty,
    * never null.
    */
   protected static List<Format> getBasicFormatList (
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
      return formatList;
   }

   /**
    * Formats the supplied LogRecord with the encapsulated basic message format.
    * Data for additional fields has to be set before calling this.
    * Appends a line feed after the data is formatted into the StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted LogRecord.
    * @param record The LogRecord to format.
    * @param loggable Unused by this, might be null.
    * @param trackingIdSequence The list containing the sequence of tracking
    * ids contributing to this log message.
    */
   protected final void basicFormat (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence)
   {
      setTrackingIds(trackingIdSequence);

      if (loggable != null)
      {
         setThreadId(loggable.getThreadId());
      }
      else
      {
         setThreadId(record.getThreadID());
      }
      format(sb);
      sb.append(Constants.LINE_SEPARATOR);
   }

   /**
    * Parses a log line, which must be formatted by this, and sets the
    * appropriate basic values of the supplied LogFileEntry. Derived types might
    * retrieve specific field values after this has been called.
    *
    * @param sb The StringBuffer containing the current log line.
    * @param entry The LogFileEntry for which to parse the log line.
    *
    * @throws ParseException if an error occurs parsing <code>sb</code>.
    *
    * @see org.jcoderz.commons.logging.LogLineFormat#parse(java.lang.StringBuffer, org.jcoderz.commons.logging.LogFileEntry)
    */
   protected final void basicParse (StringBuffer sb, LogFileEntry entry)
         throws ParseException
   {
      try
      {
         parse(sb);
         // this is a continuation line, so thread id and tracking id have been
         // set already
/*
         entry.setThreadId(getThreadId());
         final List trackingIds = getTrackingIds();
         entry.setTrackingNumber((String) trackingIds.get(
               trackingIds.size() - 1));
*/
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
    * Sets the thread id from the message to dump.
    *
    * @param threadId The thread id to dump.
    */
   protected final void setThreadId (final long threadId)
   {
      setParameter(THREADID_INDEX, String.valueOf(threadId));
   }

   /**
    * Gets the thread id of a parsed log line.
    *
    * @return Thread id of parsed log line.
    */
   protected final long getThreadId ()
   {
      return Long.parseLong((String) getParameter(THREADID_INDEX));
   }

   /**
    * Sets the list of tracking ids to dump.
    *
    * @param trackingIds The sequence of tracking ids to dump.
    */
   protected final void setTrackingIds (final List<String> trackingIds)
   {
      setParameter(TRACKINGID_INDEX, trackingIds);
   }

   /**
    * Gets the sequence of tracking ids of a parsed log line.
    *
    * @return Sequence of tracking ids of parsed log line.
    */
   protected final List<String> getTrackingIds ()
   {
	   @SuppressWarnings("unchecked")
       List<String> result = (List<String>) getParameter(TRACKINGID_INDEX);
	   return result;
   }
}
