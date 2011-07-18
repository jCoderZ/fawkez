/*
 * $Id: LogFormatter.java 1577 2009-12-07 15:44:44Z amandel $
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
package org.jcoderz.commons;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jcoderz.commons.logging.LogLineFormat;
import org.jcoderz.commons.logging.LogLineFormat.LogLineType;
import org.jcoderz.commons.logging.LogLineFormatFactory;


/**
 * This type implements a Formatter to be used for logging in a format, which
 * allows filtering of log files with standard tools and little effort. It
 * formats both standard a {@link java.util.logging.LogRecord} and instances of
 * {@link org.jcoderz.commons.Loggable}.
 *
 */
public class LogFormatter
      extends Formatter
{
    /**
     * Name of the logger that controls which log level is needed as minimum 
     * to trigger stack traces with log messages.
     */
    public static final String MSG_LOGGER_STACK_TRACE = "msgLoggerStackTrace";
    private static final Logger FWK_TRACE_LOGGER_LOGGER 
        = Logger.getLogger(MSG_LOGGER_STACK_TRACE);
    private final ThreadLocal mMessageFormatters = new ThreadLocal();

   /** {@inheritDoc} */
   public String format (LogRecord record)
   {
      final StringBuffer sb = new StringBuffer();
      Loggable loggable = null;
      if (record.getParameters() != null && record.getParameters().length > 0)
      {
         if (record.getParameters()[0] instanceof Loggable)
         {
            loggable = (Loggable) record.getParameters()[0];
         }
      }
      format(sb, record, loggable);
      return sb.toString();
   }

   /**
    * Gets the message format for a log line with <code>type</code> as type
    * specifier.
    *
    * @param type The type specifier for the requested message format.
    *
    * @return MessageFormat for log line of type <code>type</code>
    */
   private LogLineFormat getMessageFormat (
         final LogLineFormat.LogLineType type)
   {
      Map formatters = (Map) mMessageFormatters.get();
      if (formatters == null)
      {
         formatters = createMessageFormats();
         mMessageFormatters.set(formatters);
      }
      return (LogLineFormat) formatters.get(type);
   }

   private Map createMessageFormats ()
   {
      final Map rc = new HashMap();

      addMessageFormat(rc, LogLineFormat.TRACE_MESSAGE);
      addMessageFormat(rc, LogLineFormat.EXCEPTION_MESSAGE);
      addMessageFormat(rc, LogLineFormat.LOG_MESSAGE);
      addMessageFormat(rc, LogLineFormat.ERROR_MESSAGE);
      addMessageFormat(rc, LogLineFormat.STACKTRACE_MESSAGE);
      addMessageFormat(rc, LogLineFormat.PARAMETER_LINE);
      addMessageFormat(rc, LogLineFormat.NESTED_MESSAGE);

      return rc;
   }

   /**
    * Formats a LogRecord, which does not carry any parameters. In this case it
    * is a trace record, not a Loggable is logged.
    *
    * @param sb the StringBuffer where to append the formatted log record
    * @param record the log record to format
    * @param trackingIdSequence a list collecting all tracking ids of messages
    * being formatted by one call.
    */
   private void formatLogRecord (
         final StringBuffer sb,
         final LogRecord record,
         final List trackingIdSequence)
   {
      LogLineFormat.LogLineType type;
      if (record.getThrown() != null)
      {
         type = LogLineFormat.EXCEPTION_MESSAGE;
      }
      else
      {
         type = LogLineFormat.TRACE_MESSAGE;
      }
      final LogLineFormat format = getMessageFormat(type);
      format.format(sb, record, null, trackingIdSequence, null, null);
   }

   /**
    * Appends a full stack trace carried by the supplied LogRecord or Loggable
    * to the string buffer. If neither of them carries a Throwable, nothing is
    * done here. The stack trace appended by this contains the complete chain
    * of throwables.
    *
    * @param sb the StringBuffer to which to append the stack trace
    * @param record The LogRecord
    * @param loggable the Loggable, might be null.
    * @param trackingIdSequence the list collecting the sequence of tracking
    * ids, must not be null.
    */
   private void appendStackTrace (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List trackingIdSequence)
   {
      Throwable thrown = getTopLevelThrown(record, loggable);
      Throwable outerTrace = null;
      final LogLineFormat.LogLineType type = LogLineFormat.STACKTRACE_MESSAGE;
      final LogLineFormat format = getMessageFormat(type);
      while (thrown != null)
      {
         if (thrown instanceof Loggable)
         {
            addTrackingNumber(trackingIdSequence, (Loggable) thrown);
         }
         format.format(sb, record, loggable, trackingIdSequence,
               thrown, outerTrace);
         outerTrace = thrown;
         thrown = outerTrace.getCause();
      }
   }

   /**
    * Appends the parameters carried by the supplied LogRecord or Loggable
    * to the string buffer. If there are no parameters, nothing is done here.
    *
    * @param sb the StringBuffer to which to append the stack trace
    * @param loggable the Loggable, might be null.
    * @param trackingIdSequence the list collecting the sequence of tracking
    * ids, must not be null.
    */
   private void appendParameters (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List trackingIdSequence)
   {
      final LogLineFormat.LogLineType type = LogLineFormat.PARAMETER_LINE;
      final LogLineFormat format = getMessageFormat(type);
      format.format(sb, record, loggable, trackingIdSequence, null, null);
   }

   /**
    * This loops through the nested Loggables/throwables and formats the
    * complete message stack.
    *
    * @param sb The StringBuffer where to append the formatted message stack.
    * @param record The source LogRecord to format
    * @param loggable The first instance of Loggable, might be null if
    * <code>record</code> does not carry a Loggable.
    */
   private void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable)
   {
      List trackingIds = initialiseTrackingIds(record, loggable);
      Loggable currentLoggable = loggable;
      boolean isFirst = true;

      Throwable cause = null;
      while (isFirst || (! ((currentLoggable == null) && (cause == null))))
      {
         Throwable nestedCause = null;
         if (currentLoggable != null)
         {
            formatLoggable(sb, record, currentLoggable, trackingIds);
            nestedCause = currentLoggable.getCause();
         }
         else if (isFirst)
         {
            formatLogRecord(sb, record, trackingIds);
            nestedCause = record.getThrown();
         }
         isFirst = false;
         cause = (cause != null) ? cause.getCause() : nestedCause;
         currentLoggable = null;

         if (cause != null)
         {
            appendNestingLevel(sb, record, cause, trackingIds);
            if (cause instanceof Loggable)
            {
               currentLoggable = (Loggable) cause;
            }
         }
      }
      // for messages: do not log stack traces for log messages of level
      // below the FWK_TRACE_LOGGER_LOGGER log level.
      if (!(loggable instanceof LogEvent)
          || FWK_TRACE_LOGGER_LOGGER.isLoggable(record.getLevel()))
      {
          trackingIds = initialiseTrackingIds(record, loggable);
          appendStackTrace(sb, record, loggable, trackingIds);
      }
   }

   private void formatLoggable (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List trackingIds)
   {
      final LogLineFormat.LogLineType type = determineType(loggable);
      final LogLineFormat format = getMessageFormat(type);
      format.format(sb, record, loggable, trackingIds, null, null);
      appendParameters(sb, record, loggable, trackingIds);
   }

   /**
    * Creates the message format for the specified type and adds it to the
    * supplied map.
    *
    * @param msgFormats The map to which to add the new message format with
    * <code>type</code> as key.
    * @param type The type for which to create the format and add to the map.
    */
   private void addMessageFormat (
         final Map msgFormats,
         final LogLineFormat.LogLineType type)
   {
      final LogLineFormat format = LogLineFormatFactory.create(type);
      msgFormats.put(type, format);
   }

   /**
    * Determines the log line type for the supplied Loggable.
    *
    * @param loggable The Loggable for which to determine the logline type.
    *
    * @return The correct LogLineType for <code>loggable</code>.
    *
    * @see LogLineType
    */
   private LogLineFormat.LogLineType determineType (final Loggable loggable)
   {
      final Throwable cause;
      final LogLineFormat.LogLineType rc;

      if (loggable instanceof Throwable)
      {
         cause = (Throwable) loggable;
      }
      else
      {
         cause = loggable.getCause();
      }
      if ((cause != null) && ! (cause instanceof LogEvent))
      {
         rc = LogLineFormat.ERROR_MESSAGE;
      }
      else
      {
         rc = LogLineFormat.LOG_MESSAGE;
      }
      return rc;
   }

  /**
   * Appends a nesting level to the StringBuffer. This is performed if the
   * current Loggable carries a cause, which might be a Loggable itself.
   * In case the cause is a Loggable, the tracking id sequence is extended with
   * its tracking id and the symbol name is logged here. If the cause is not a
   * Loggable, its name and message are logged.
   *
   * @param sb The StringBuffer where to append the nesting level.
   * @param record The LogRecord currently formatted.
   * @param cause The Throwable causing the nesting level.
   * @param trackingIdSequence The list collecting the sequence of tracking ids.
   */
   private void appendNestingLevel (
         final StringBuffer sb,
         final LogRecord record,
         final Throwable cause,
         final List trackingIdSequence)
   {
      final Loggable loggable;
      if (cause instanceof Loggable)
      {
         loggable = (Loggable) cause;
      }
      else
      {
         loggable = null;
      }
      final LogLineFormat.LogLineType type = LogLineFormat.NESTED_MESSAGE;
      final LogLineFormat format = getMessageFormat(type);
      if (loggable == null)
      {
         format.format(sb, record, null, trackingIdSequence, null, cause);
      }
      else
      {
         addTrackingNumber(trackingIdSequence, loggable);
         format.format(sb, record, loggable, trackingIdSequence, null,
               loggable.getLogMessageInfo().getSymbol());
      }
   }

   /**
    * Initialises the list holding the sequence of tracking ids. Creates a new
    * list and fills it with the first tracking id, which is taken from the
    * supplied loggable. If this is null, the sequence number of the supplied
    * log record is taken.
    *
    * @param record The log record to format. Must not be null.
    * @param loggable The loggable being encapsulated by <code>record</code>,
    * might be null.
    *
    * @return List with first tracking id.
    */
   private List initialiseTrackingIds (
         final LogRecord record,
         final Loggable loggable)
   {
      final List rc = new ArrayList();

      if (loggable != null)
      {
         addTrackingNumber(rc, loggable);
      }
      else
      {
         addTrackingNumber(rc, record);
      }
      return rc;
   }

   /**
    * Adds the record's sequence number as new tracking id to the sequence of
    * tracking ids, if it is not already included as last element.
    *
    * @param trackingIds The list storing the sequence of tracking ids.
    * @param record The record for which to add the sequence number.
    */
   private void addTrackingNumber (
         final List trackingIds,
         final LogRecord record)
   {
      addTrackingNumber(trackingIds,
            Integer.toHexString((int) record.getSequenceNumber()));
   }

   /**
    * Adds the loggable's tracking number as new tracking id to the sequence of
    * tracking ids, if it is not already included as last element.
    *
    * @param trackingIds The list storing the sequence of tracking ids.
    * @param loggable The Loggable for which to add the tracking number.
    */
   private void addTrackingNumber (
         final List trackingIds,
         final Loggable loggable)
   {
      addTrackingNumber(trackingIds, loggable.getTrackingNumber());
   }

   /**
    * Adds the new tracking number as new tracking id to the sequence of
    * tracking ids, if it is not already included as last element.
    *
    * @param trackingIds The list storing the sequence of tracking ids.
    * @param newId The number to add to the sequence.
    */
   private void addTrackingNumber (
         final List trackingIds,
         final String newId)
   {
      if (! trackingIds.isEmpty())
      {
         if (! trackingIds.get(trackingIds.size() - 1).equals(newId))
         {
            trackingIds.add(newId);
         }
      }
      else
      {
         trackingIds.add(newId);
      }
   }

   /**
    * Gets the top level throwable from the supplied LogRecord and Loggable.
    * This is either the cause of <code>record or loggable</code> or
    * <code>loggable</code> itself.
    *
    * @param record The LogRecord currently formatted.
    * @param loggable The Loggable carried by <code>record</code>
    *
    * @return top level Throwable, might be null if no such.
    */
   private Throwable getTopLevelThrown (
         final LogRecord record,
         final Loggable loggable)
   {
      final Throwable thrown;

      if (loggable == null)
      {
         thrown = record.getThrown();
      }
      else
      {
         if (loggable instanceof Throwable)
         {
            thrown = (Throwable) loggable;
         }
         else
         {
            thrown = loggable.getCause();
         }
      }
      return thrown;
   }
}
