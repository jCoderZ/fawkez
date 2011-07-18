/*
 * $Id: LoggerUtil.java 1302 2009-03-23 21:08:18Z amandel $
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


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.LogRecord;
import junit.framework.Assert;
import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.types.Date;

/**
 * This helper class provides utils for checking log items.
 *
 */
public final class LoggerUtil
{
   private LoggerUtil ()
   {
      // no instances allowed.
   }

   /**
    * This checks the structure of the LogItem against the LogRecord.
    *
    * @param item The log item to check.
    * @param record THe LogRecord to check against.
    */
   public static void checkLogItem (
         final LogItem item,
         final LogRecord record)
   {
      Throwable thrown = null;
      LogItem currentItem = item;

      if ((record.getParameters() != null)
            && record.getParameters()[0] instanceof Loggable)
      {
         final Loggable loggable = (Loggable) record.getParameters()[0];
         checkLoggable(item, loggable);
         thrown = loggable.getCause();
      }
      else
      {
         thrown = record.getThrown();
      }
      while (thrown != null)
      {
         currentItem = (LogItem) currentItem.getNestedItem();
         if (currentItem == null)
         {
            Assert.fail("LogRecord has still nested throwables, which are "
                  + "missing in LogElement");
         }
         else
         {
            checkNestedItem(currentItem, thrown);
         }
         thrown = thrown.getCause();
      }
      if (currentItem.getNestedItem() != null)
      {
         Assert.fail("LogElement has too much nested elements");
      }
   }

   private static void checkNestedItem (
         final LogItem item,
         final Throwable nested)
   {
      if (!((nested instanceof Loggable) || item.isExceptionItem()))
      {
         Assert.fail("Found exception, but LogElement is not exception item");
      }
      else if ((nested instanceof Loggable) && item.isExceptionItem())
      {
         Assert.fail("Found Loggable, but LogElement is exception item");
      }
      else if (nested instanceof Loggable)
      {
         checkLoggable(item, (Loggable) nested);
      }
   }

   private static void checkLoggable (
         final LogItem item,
         final Loggable loggable)
   {
      Assert.assertEquals("Business Impact must match",
            item.getBusinessImpact(),
            loggable.getLogMessageInfo().getBusinessImpact());
      Assert.assertEquals("Thread Name must match",
            item.getThreadName(),
            loggable.getThreadName());
      Assert.assertEquals("Instance ID must match",
            item.getInstanceId(), loggable.getInstanceId());
      Assert.assertEquals("Log level must match",
            item.getLoggerLevel(),
            loggable.getLogMessageInfo().getLogLevel());
      Assert.assertEquals("Message must match",
            String.valueOf(item.getMessage()),
            String.valueOf(loggable.getMessage()));
      Assert.assertEquals("Node Id must match",
            item.getNodeId(), loggable.getNodeId());
      Assert.assertEquals("Solution must match",
            item.getSolution(),
            loggable.getLogMessageInfo().getSolution());
      Assert.assertEquals("Symbol must match",
            item.getSymbol(), loggable.getLogMessageInfo().getSymbol());
      Assert.assertEquals("Symbol ID must match",
            item.getSymbolId(),
            Integer.toHexString(loggable.getLogMessageInfo().toInt()));
      Assert.assertEquals("Timestamp must match",
            item.getTimestamp(), new Date(loggable.getEventTime()));
      Assert.assertEquals("Tracking number must match",
            item.getTrackingNumber(), loggable.getTrackingNumber());
      Assert.assertEquals("Thread id must match",
            new Long(item.getThreadId()),
            new Long(loggable.getThreadId()));
      checkParameters(item, loggable);
   }

   private static void checkParameters (
      final LogItem item,
      final Loggable loggable)
   {
      final Set epNames = item.getParameterNames();
      final Set lpNames = loggable.getParameterNames();

      Assert.assertFalse("No parameters for loggable set, but log element "
            + "has parameters: " + epNames,
            (lpNames == null && epNames != null && ! epNames.isEmpty()));

      if (lpNames != null)
      {
         boolean epNamesIsEmpty = false;
         if (!((epNames == null) || epNames.isEmpty()))
         {
            if (!lpNames.containsAll(epNames))
            {
               epNames.removeAll(lpNames);
               Assert.fail(
                     "Formatted element contains parameters not in loggable: "
                     + epNames);
            }
         }
         else
         {
            epNamesIsEmpty = true;
         }
         for (final Iterator iter = lpNames.iterator(); iter.hasNext(); )
         {
            final String name = (String) iter.next();
            if (! name.startsWith(LogItem.INTERNAL_PARAMETER_PREFIX))
            {
               Assert.assertTrue("Item has no parameters, but must have: "
                     + name, ! epNamesIsEmpty);
               Assert.assertTrue("Item must contain parameter " + name,
                     epNames.contains(name));
               final List eparams = item.getParameterValues(name);
               final List lparams = loggable.getParameter(name);
               Assert.assertEquals("The parameter values must match for "
                     + name, eparams, lparams);
            }
         }
      }
   }
}
