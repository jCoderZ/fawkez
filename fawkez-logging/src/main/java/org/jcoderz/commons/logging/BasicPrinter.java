/*
 * $Id: BasicPrinter.java 1535 2009-07-12 08:31:31Z amandel $
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


import java.io.PrintWriter;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcoderz.commons.util.Assert;

/**
 * This printer prints the log information as lines of text and uses the display
 * options for selecting the fields to display.
 * The format is similar to the log file format.
 *
 */
public class BasicPrinter
      extends LogPrinter
{
   private static final String SOURCE_TAG = "_LOGGED_AT_";
   private static final String SOLUTION_TAG = "_SOLUTION_";
   private static final String SYMBOL_TAG = "_SYMBOL_";

   private static final String FIRST_MSG_ITEM = "{0}";

   private MessageFormat mStackTraceFormat = null;
   private MessageFormat mTraceLineFormat = null;
   private MessageFormat mLogMessageFormat = null;
   private MessageFormat mParameterLineFormat = null;
   private MessageFormat mNestedLineFormat = null;

   private Object[] mStackTraceData = null;
   private Object[] mTraceLineData = null;
   private Object[] mLogMessageData = null;
   private Object[] mParameterLineData = null;
   private Object[] mNestedLineData = null;

   private final StringBuffer mBuffer = new StringBuffer();

   /** {@inheritDoc} */
   public void setDisplayOptions (DisplayOptions options)
   {
      super.setDisplayOptions(options);
      setStackTraceFormat();
      setTraceLineFormat();
      setLogMessageFormat();
      setParameterLineFormat();
      setNestedLineFormat();
   }

   /** {@inheritDoc} */
   public void print (
         final PrintWriter printer,
         final LogItem entry)
   {
      LogItem currentEntry = entry;

      Assert.notNull(printer, "Printer");
      Assert.notNull(entry, "entry");
      Assert.notNull(entry.getType(), "entry.getType()");
      final List<String> trackingIds = new ArrayList<String>();
      while (currentEntry != null)
      {
         printEntry(printer, currentEntry, trackingIds);
         currentEntry = currentEntry.getNestedItem();
         if (currentEntry != null)
         {
            printNesting(printer, currentEntry, trackingIds);
         }
      }
      currentEntry = entry;
      trackingIds.clear();
      while (currentEntry != null)
      {
         if (displayStackTrace(currentEntry))
         {
            printStackTrace(printer, currentEntry, trackingIds);
         }
         currentEntry = currentEntry.getNestedItem();
      }
   }

   /**
    * @param printer
    * @param currentEntry
    * @param trackingIds
    */
   private void printEntry (
         final PrintWriter printer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      if (! entry.isExceptionItem())
      {
         addTrackingNumber(trackingIds, entry.getTrackingNumber());
         final LogLineFormat.LogLineType type
               = LogLineFormat.getLogLineType(entry.getType().charAt(0));
         if ((type == LogLineFormat.TRACE_MESSAGE)
               || (type == LogLineFormat.EXCEPTION_MESSAGE))
         {
            printTraceLine(printer, entry, trackingIds);
         }
         else if ((type == LogLineFormat.LOG_MESSAGE)
               || (type == LogLineFormat.ERROR_MESSAGE))
         {
            printMessageLine(printer, entry, trackingIds);
            printParameters(printer, entry, trackingIds);
         }
      }
   }

   private void setStackTraceFormat ()
   {
      final Format[] formats = StackTraceFormat.getFormatList(
            getDisplayOptions(), false);

      mStackTraceData = new Object[formats.length];

      final StringBuffer pattern = new StringBuffer(FIRST_MSG_ITEM);
      for (int i = 1; i < formats.length; ++i)
      {
         pattern.append(" {").append(i).append('}');
      }
      mStackTraceFormat = new MessageFormat(pattern.toString());
      mStackTraceFormat.setFormats(formats);
   }

   private void setTraceLineFormat ()
   {
      final Format[] formats = TraceLineFormat.getFormatList(
            getDisplayOptions(), false);
      mTraceLineData = new Object[formats.length];

      final StringBuffer pattern = new StringBuffer(FIRST_MSG_ITEM);
      for (int i = 1; i < formats.length; ++i)
      {
         pattern.append(" {").append(i).append('}');
      }
      mTraceLineFormat = new MessageFormat(pattern.toString());
      mTraceLineFormat.setFormats(formats);
   }

   private void setLogMessageFormat ()
   {
      final Format[] formats = MessageLineFormat.getFormatList(
            getDisplayOptions(), false);
      mLogMessageData = new Object[formats.length];

      final StringBuffer pattern = new StringBuffer(FIRST_MSG_ITEM);
      for (int i = 1; i < formats.length; ++i)
      {
         pattern.append(" {").append(i).append('}');
      }
      mLogMessageFormat = new MessageFormat(pattern.toString());
      mLogMessageFormat.setFormats(formats);
   }

   private void setParameterLineFormat ()
   {
      final Format[] formats = ParameterLineFormat.getFormatList(
            getDisplayOptions(), false);

      mParameterLineData = new Object[formats.length];

      final StringBuffer pattern = new StringBuffer(FIRST_MSG_ITEM);
      int i;
      for (i = 1; i < formats.length - 1; ++i)
      {
         pattern.append(" {").append(i).append('}');
      }
      pattern.append(": \t{").append(i).append('}');
      mParameterLineFormat = new MessageFormat(pattern.toString());
      mParameterLineFormat.setFormats(formats);
   }

   private void setNestedLineFormat ()
   {
      final Format[] formats = NestedLineFormat.getFormatList(
            getDisplayOptions(), false);
      mNestedLineData = new Object[formats.length];

      final StringBuffer pattern = new StringBuffer();
      int i = 0;
      for (i = 0; i < formats.length - 1; ++i)
      {
         pattern.append('{').append(i).append("} ");
      }
      pattern.append("Caused by: {").append(i).append('}');
      mNestedLineFormat = new MessageFormat(pattern.toString());
      mNestedLineFormat.setFormats(formats);
   }

   private void printTraceLine (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      int i = 0;
      i = setTimeStamp(i, mTraceLineData, entry);
      i = setNodeId(i, mTraceLineData, entry);
      i = setInstanceId(i, mTraceLineData, entry);
      i = setThreadId(i, mTraceLineData, entry);
      i = setLoggerLevel(i, mTraceLineData, entry);
      i = setSymbolId(i, mTraceLineData, entry);
      i = setBusinessImpact(i, mTraceLineData, entry);
      i = setThreadName(i, mTraceLineData, entry);
      i = setCategory(i, mTraceLineData, entry);
      i = setTrackingNumber(i, mTraceLineData, trackingIds);
      i = setSource(i, mTraceLineData, entry);
      i = setMessage(i, mTraceLineData, entry);
      mBuffer.setLength(0);
      mTraceLineFormat.format(mTraceLineData, mBuffer, null);
      writer.println(mBuffer);
   }

   private void printMessageLine (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      int i = 0;
      i = setTimeStamp(i, mLogMessageData, entry);
      i = setNodeId(i, mLogMessageData, entry);
      i = setInstanceId(i, mLogMessageData, entry);
      i = setThreadId(i, mLogMessageData, entry);
      i = setLoggerLevel(i, mLogMessageData, entry);
      i = setSymbolId(i, mLogMessageData, entry);
      i = setBusinessImpact(i, mLogMessageData, entry);
      i = setThreadName(i, mTraceLineData, entry);
      i = setCategory(i, mLogMessageData, entry);
      i = setTrackingNumber(i, mLogMessageData, trackingIds);
      i = setMessage(i, mLogMessageData, entry);
      mBuffer.setLength(0);
      mLogMessageFormat.format(mLogMessageData, mBuffer, null);
      writer.println(mBuffer);
   }

   private void printParameterLine (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds,
         final String parameterName,
         final List<?> parameterValues)
   {
      int i = 0;
      i = setThreadId(i, mParameterLineData, entry);
      i = setTrackingNumber(i, mParameterLineData, trackingIds);

      mParameterLineData[i++] = parameterName;
      mParameterLineData[i++] = parameterValues;

      mBuffer.setLength(0);
      mParameterLineFormat.format(mParameterLineData, mBuffer, null);
      writer.println(mBuffer);
   }

   private void printStackTraceLine (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds,
         final StackTraceInfo stacktraceLine)
   {
      int i = 0;
      i = setThreadId(i, mStackTraceData, entry);
      i = setTrackingNumber(i, mStackTraceData, trackingIds);

      mStackTraceData[i++] = stacktraceLine.toString();

      mBuffer.setLength(0);
      mStackTraceFormat.format(mStackTraceData, mBuffer, null);
      writer.println(mBuffer);
   }

   private void printNesting (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      int i = 0;
      i = setThreadId(i, mNestedLineData, entry);
      i = setTrackingNumber(i, mNestedLineData, trackingIds);
      i = setMessage(i, mNestedLineData, entry);

      mBuffer.setLength(0);
      mNestedLineFormat.format(mNestedLineData, mBuffer, null);
      writer.println(mBuffer);
   }

   private void printParameters (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      final StringBuffer source = getPrintableSource(
            entry.getSourceClass(), entry.getSourceMethod());
      if (source != null)
      {
         printParameterLine(
               writer, entry, trackingIds, SOURCE_TAG, Arrays.asList(
                     new String[]{source.toString()}));
      }
      if (getDisplayOptions().displaySymbol())
      {
         printParameterLine(
               writer, entry, trackingIds, SYMBOL_TAG, Arrays.asList(
                     new String[]{entry.getSymbol()}));
      }
      if (getDisplayOptions().displaySolution())
      {
         printParameterLine(
               writer, entry, trackingIds, SOLUTION_TAG, Arrays.asList(
                     new String[]{entry.getSolution()}));
      }
      if (getDisplayOptions().displayParameters())
      {
         final Set<String> names = entry.getParameterNames();
         for (final Iterator<String> nameIter = names.iterator(); nameIter.hasNext(); )
         {
            final String name = nameIter.next();
            printParameterLine(writer, entry, trackingIds,
                  name, entry.getParameterValues(name));
         }
      }
   }

   private void printStackTrace (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds)
   {
      for (final Iterator<StackTraceInfo> iter = entry.getStackTraceLines().iterator();
            iter.hasNext(); )
      {
         final StackTraceInfo info = (StackTraceInfo) iter.next();
         if (! info.isMoreLine())
         {
            printStackTraceLine(writer, entry, trackingIds, info);
         }
         else
         {
            printMoreStackTrace(writer, entry, trackingIds, info);
         }
      }
   }

   private void printMoreStackTrace (
         final PrintWriter writer,
         final LogItem entry,
         final List<String> trackingIds,
         final StackTraceInfo info)
   {
      final LogItem stackTraceEntry = getEntryForMoreStackTrace(entry, info);
      if (stackTraceEntry == null)
      {
         throw new IllegalStateException("Did not find correct stack trace to "
               + "display for " + entry);
      }
      else if (entry == stackTraceEntry)
      {
         printStackTraceLine(writer, entry, trackingIds, info);
      }
      else
      {
         printStackTrace(writer, stackTraceEntry, trackingIds);
      }
   }

   private int setTimeStamp (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayTimestamp())
      {
         data[rc++] = entry.getTimestamp();
      }
      return rc;
   }

   private int setNodeId (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayNodeId())
      {
         data[rc++] = entry.getNodeId();
      }
      return rc;
   }

   private int setInstanceId (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayInstanceId())
      {
         data[rc++] = entry.getInstanceId();
      }
      return rc;
   }

   private int setThreadId (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayThreadId())
      {
         data[rc++] = String.valueOf(entry.getThreadId());
      }
      return rc;
   }

   private int setLoggerLevel (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayLoggerLevel())
      {
         data[rc++] = entry.getLoggerLevel().toString();
      }
      return rc;
   }

   private int setSymbolId (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displaySymbolId())
      {
         data[rc++] = entry.getSymbolId();
      }
      return rc;
   }

   private int setBusinessImpact (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayBusinessImpact())
      {
         data[rc++] = entry.getBusinessImpact().toString();
      }
      return rc;
   }

   private int setCategory (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      if (getDisplayOptions().displayCategory())
      {
         data[rc++] = entry.getCategory().toString();
      }
      return rc;
   }

   private int setThreadName (
       final int i,
       final Object[] data,
       final LogItem entry)
 {
    int rc = i;
    if (getDisplayOptions().displayThreadName())
    {
       data[rc++] = entry.getThreadName();
    }
    return rc;
 }

   private int setTrackingNumber (
         final int i,
         final Object[] data,
         final List<String> trackingIds)
   {
      int rc = i;
      if (getDisplayOptions().displayTrackingNumber())
      {
         data[rc++] = trackingIds;
      }
      return rc;
   }

   private int setSource (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      final StringBuffer source
         = getPrintableSource(entry.getSourceClass(), entry.getSourceMethod());
      if (source != null)
      {
         data[rc++] = source.toString();
      }
      return rc;
   }

   /**
    * Gets source class and method appended within one string buffer according
    * to the display options currently set.
    *
    * @param sourceClass The name of the source class.
    * @param sourceMethod The name of the source method.
    *
    * @return null if none of source class or method is to display;
    * StringBuffer containing the displayable parts of source class name
    * and source method name, else
    */
   private StringBuffer getPrintableSource (
         final String sourceClass,
         final String sourceMethod)
   {
      StringBuffer rc = null;
      final StringBuffer source = new StringBuffer();

      if (getDisplayOptions().displaySourceClass())
      {
         rc = source.append(sourceClass);
      }
      if (getDisplayOptions().displaySourceMethod())
      {
         if (getDisplayOptions().displaySourceClass())
         {
            rc = source.append('.');
         }
         source.append(sourceMethod);
         if (sourceMethod.indexOf('(') < 0)
         {
            rc = source.append("()");
         }
      }
      return rc;
   }

   private int setMessage (
         final int i,
         final Object[] data,
         final LogItem entry)
   {
      int rc = i;
      data[rc++] = entry.getMessage();
      return rc;
   }

   /**
    * Adds the new tracking number as new tracking id to the sequence of
    * tracking ids, if it is not already included as last element.
    *
    * @param trackingIds The list storing the sequence of tracking ids.
    * @param newId The number to add to the sequence.
    */
   private void addTrackingNumber (
         final List<String> trackingIds,
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
}

