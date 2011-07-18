/*
 * $Id: LogElement.java 1537 2009-07-13 14:30:55Z amandel $
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

import org.jcoderz.commons.BusinessImpact;
import org.jcoderz.commons.Category;
import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.LoggableImpl;
import org.jcoderz.commons.types.Date;



/**
 * This is an implementation of LogItem based upon LogRecords. On instanciation
 * it creates a nested structure of LogElements according to the structure
 * of the LogRecord.
 *
 */
public class LogElement
      extends LogItem
{
   private static final String TRACEMSG = "TRACEMSG";

   private Loggable mLoggable;
   private final java.util.logging.LogRecord mLogRecord;
   private Throwable mThrown;

   /**
    * Creates a root LogElement from a LogRecord
    *
    * @param record The log record from which to create this.
    */
   LogElement (final java.util.logging.LogRecord record)
   {
      mThrown = null;
      mLogRecord = record;
      Loggable loggable = null;

      if (! ((record.getParameters() == null)
            || record.getParameters().length == 0))
      {
         if (record.getParameters()[0] instanceof Loggable)
          {
            loggable = (Loggable) record.getParameters()[0];
         }
      }
      if (loggable != null)
      {
         init(loggable);
      }
      else
      {
         init();
      }
   }

   /**
    * Creates a nested LogElement from a LogRecord and nested LogRecord
    * elements.
    *
    * @param record The log record from which to create this.
    * @param thrown The current nested element of LogRecord.
    * @param parent The parent element of this.
    */
   private LogElement (
         final java.util.logging.LogRecord record,
         final Throwable thrown,
         final LogElement parent)
   {
      mLogRecord = record;
      setParentItem(parent);
      init(thrown);
   }

   private void init ()
   {
      mLoggable = null;
      initData();

      final Throwable thrown = mLogRecord.getThrown();
      if (thrown != null)
      {
         setNestedItem(new LogElement(mLogRecord, thrown, this));
      }
   }

   private void init (Throwable thrown)
   {
      if (thrown instanceof Loggable)
      {
         init((Loggable) thrown);
      }
      else
      {
         final Throwable nestedThrown = thrown.getCause();
         mLoggable = null;
         mThrown = thrown;
         initData();
         if (nestedThrown != null)
         {
            setNestedItem(new LogElement(mLogRecord, nestedThrown, this));
         }
      }
   }

   private void init (Loggable loggable)
   {
      mLoggable = loggable;
      mThrown = null;
      initData();
      if (loggable.getCause() != null)
      {
         setNestedItem(new LogElement(mLogRecord, loggable.getCause(), this));
      }
   }

   private void initData ()
   {
      initType();
      setLoggerLevel(mLogRecord.getLevel());
      setSourceClass(mLogRecord.getSourceClassName());
      setSourceMethod(mLogRecord.getSourceMethodName());
      if (mLoggable != null)
      {
         setBusinessImpact(mLoggable.getLogMessageInfo().getBusinessImpact());
         setCategory(mLoggable.getLogMessageInfo().getCategory());
         setInstanceId(mLoggable.getInstanceId());
         setMessage(mLoggable.getMessage());
         setNodeId(mLoggable.getNodeId());
         setSolution(mLoggable.getLogMessageInfo().getSolution());
         setSymbol(mLoggable.getLogMessageInfo().getSymbol());
         setSymbolId(
               Integer.toHexString(mLoggable.getLogMessageInfo().toInt()));
         setThreadId(mLoggable.getThreadId());
         try
         {
             setThreadName(mLoggable.getThreadName());
          }
          catch (AbstractMethodError ex)
          {
              // We have a old loggable that does not support
              // thread name jet.
              setThreadName(Thread.currentThread().getName());
          }

         setTimestamp(Date.fromUtilDate(
               new java.util.Date(mLoggable.getEventTime())));
         setTrackingNumber(mLoggable.getTrackingNumber());
         setParameters();
         if (mLoggable instanceof Throwable)
         {
            initStackTrace((Throwable) mLoggable);
         }
      }
      else if (mThrown != null)
      {
         setMessage(mThrown.getMessage());
         initStackTrace(mThrown);
      }
      else
      {
         setBusinessImpact(BusinessImpact.UNDEFINED);
         setCategory(Category.TECHNICAL);
         setInstanceId(LoggableImpl.INSTANCE_ID);
         setMessage(mLogRecord.getMessage());
         setNodeId(LoggableImpl.NODE_ID);
         setSymbol(TRACEMSG);
         setSymbolId(TRACEMSG);
         setThreadId(mLogRecord.getThreadID());
         setThreadName(Thread.currentThread().getName());
         setTimestamp(Date.fromLong(mLogRecord.getMillis()));
         setTrackingNumber(Integer.toHexString(
               (int) mLogRecord.getSequenceNumber()));
      }
   }

   private void setParameters ()
   {
      if (mLoggable != null)
      {
         final Set names = mLoggable.getParameterNames();
         if (names != null && ! names.isEmpty())
         {
            for (final Iterator iter = names.iterator(); iter.hasNext(); )
            {
               final String name = (String) iter.next();
               if (! name.startsWith(INTERNAL_PARAMETER_PREFIX))
               {
                  final List parameters = mLoggable.getParameter(name);
                  addToParameters(name, parameters);
               }
            }
         }
      }
   }

   private void initStackTrace (final Throwable thrown)
   {
      // this is a nop, currently not interested in stack trace
   }


   private void initType ()
   {
      if (mLoggable == null)
      {
         if (getParentItem() == null)
         {
            if (mLogRecord.getThrown() == null)
            {
               setType(String.valueOf(
                     LogLineFormat.TRACE_MESSAGE.getTypeSpecifier()));
            }
            else
            {
               setType(String.valueOf(
                     LogLineFormat.EXCEPTION_MESSAGE.getTypeSpecifier()));
            }
         }
      }
      else
      {
         if ((mLoggable.getCause() == null) || (
               mLoggable.getCause() instanceof Loggable))
         {
            setType(String.valueOf(
                  LogLineFormat.LOG_MESSAGE.getTypeSpecifier()));
         }
         else
         {
            setType(String.valueOf(
                  LogLineFormat.ERROR_MESSAGE.getTypeSpecifier()));
         }
      }
   }
}
