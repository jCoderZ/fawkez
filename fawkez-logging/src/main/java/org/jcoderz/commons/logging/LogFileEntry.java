/*
 * $Id: LogFileEntry.java 1299 2009-03-23 20:06:23Z amandel $
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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;


/**
 * This class encapsulates the information of a log record being read from the
 * log file.
 * Note: This is not thread safe, one instance of this must not be used by
 * several threads in parallel.
 *
 */
public class LogFileEntry
      extends LogItem
{
   private static final ObjectPool POOL = new StackObjectPool();

   /* Sets the current entry level. Always the entry cursor of the root entry
      should be used .*/
   private LogFileEntry mEntryCursor;
   private LogFileEntry mRootEntry = null;

   private final Map<LogLineFormat.LogLineType, LogLineFormat> mFormatMap = new HashMap<LogLineFormat.LogLineType, LogLineFormat>();

   // Flag whether this stack of log file entries has already encountered a
   // stack trace line. This is set for the root element.
   private boolean mMetStackTraceLine = false;

   private final boolean mPooled;

   private static final class LogFileEntryFactory
         implements PoolableObjectFactory
   {
      /**
       * Hide default constructor.
       */
      private LogFileEntryFactory ()
      {
         // nop
      }

      /** {@inheritDoc} */
      public Object makeObject ()
      {
         return new LogFileEntry(true);
      }

      /** {@inheritDoc} */
      public void destroyObject (Object arg0)
      {
         // nop
      }

      /** {@inheritDoc} */
      public void activateObject (Object arg)
      {
         // nop
      }

      /**
       * Passivates an instance of LogFileEntry before it is put back into the
       * pool.
       * This is indirectly called by entry.release(), so do not call
       * entry.release() again, but release a nested entry.
       *
       * @param arg The LogFileEntry object to passivate.
       *
       * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
       */
      public void passivateObject (final Object arg)
      {
         final LogFileEntry entry = (LogFileEntry) arg;
         final LogFileEntry nested = (LogFileEntry) entry.getNestedItem();
         entry.setNestedEntry(null);
         entry.reset();
         if (nested != null)
         {
            nested.release();
         }
      }

      /**
       * Validates the LogFileEntry instance, there is nothing to check.
       *
       * @param arg The LogFileEntry instance to validate.
       *
       * @return true
       *
       * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
       */
      public boolean validateObject (Object arg)
      {
         return true;
      }
   }

   static
   {
      POOL.setFactory(new LogFileEntryFactory());
   }

   protected LogFileEntry ()
   {
      this(false);
   }

   /**
    * Creates a new instance of this.
    *
    * @param pooled Flag whether this instance is under pool control.
    */
   private LogFileEntry (final boolean pooled)
   {
      mEntryCursor = this;
      mRootEntry = this;
      mPooled = pooled;
   }

   /**
    * Gets a new instance of LogFileEntry. The object being returned should
    * be reset by calling {@linkplain #release()} when it is not needed anymore.
    *
    * @return instance of this. Might be newly created or reused.
    *
    * @throws LoggingException if an error occurs.
    */
   static LogFileEntry getLogFileEntry ()
   {
      try
      {
         return (LogFileEntry) POOL.borrowObject();
      }
      catch (Exception ex)
      {
         throw new LoggingException(
               "Error retrieving an instance of LogFileEntry", ex);
      }
   }

   /**
    * Adds a new line being read from the log file to this entry. The line is
    * parsed and checked whether it belongs to this entry or to a new entry.
    *
    * @param logLine The line to add to this.
    *
    * @return true, if the line actually belongs to this; false, if it belongs
    * to a new entry.
    *
    * @throws ParseException if an error occurs parsing the line.
    * @throws Exception if a generic error occurs.
    */
   boolean addLogLine (final StringBuffer logLine)
         throws ParseException
   {
      boolean rc = false;
      LogLineFormat.LogLineType type = null;
      if (logLine.length() == 0)
      {
         rc = true;
      }
      else
      {
         type = LogLineFormat.getLogLineType(logLine.charAt(0));
         final LogFileEntry entry = findEntry(type);

         if (entry != null)
         {
            entry.handleLogLine(type, logLine);
            rc = true;
         }
      }
      return rc;
   }

   /**
    * Adds a String containing the string representation of a stacktrace item
    * to the stacktrace stored by this. The stacktrace item could be a
    * 'caused by' line, an 'at ...' or an '... nnn more' line.
    *
    * @param stackTraceElement The String containing the string representation
    * of one element of the stack trace.
    */
   void addToStackTrace (final StackTraceInfo stackTraceElement)
   {
      if (stackTraceElement.isCauseLine())
      {
         // a nested element must exist in this case
         if (getNestedItem() == null)
         {
            throw new LoggingException("Found a 'caused-by' stack trace line, "
                  + "but have not got a nested element: "
                  + stackTraceElement.toString());
         }
         setCurrentEntry((LogFileEntry) getNestedItem());
      }
      else
      {
         getStackTraceLines().add(stackTraceElement);
      }
   }

   private LogFileEntry findEntry (final LogLineFormat.LogLineType type)
   {
      LogFileEntry entry = null;

      if (type != LogLineFormat.STACKTRACE_MESSAGE)
      {
         /* if not a stack trace line and his stack has already read such a
          * line, then it is for a new message. For test messages, where no
          * stack trace might exist, the type must not be set, otherwise
          * it is a new message. */
         if (! mRootEntry.mMetStackTraceLine
               && ((getCurrentEntry().getType() == null)
                  || (type != LogLineFormat.TRACE_MESSAGE
                        && type != LogLineFormat.EXCEPTION_MESSAGE
                        && type != LogLineFormat.LOG_MESSAGE
                        && type != LogLineFormat.ERROR_MESSAGE)))
         {
            entry = getCurrentEntry();
         }
      }
      else
      {
         if (! mRootEntry.mMetStackTraceLine)
         {
            // first stack trace line starts from root entry again.
            entry = mRootEntry;
            setCurrentEntry(entry);
            mRootEntry.mMetStackTraceLine = true;
         }
         else
         {
            entry = getCurrentEntry();
         }
      }
      return entry;
   }

   private LogLineFormat getFormat (final LogLineFormat.LogLineType type)
   {
      LogLineFormat rc = (LogLineFormat) mFormatMap.get(type);
      if (rc == null)
      {
         rc = LogLineFormatFactory.create(type);
         mFormatMap .put(type, rc);
      }
      return rc;
   }

   private void handleLogLine (
         final LogLineFormat.LogLineType type,
         final StringBuffer sb)
         throws ParseException
   {
      if ((type == LogLineFormat.TRACE_MESSAGE)
            || (type == LogLineFormat.LOG_MESSAGE)
            || (type == LogLineFormat.EXCEPTION_MESSAGE)
            || (type == LogLineFormat.ERROR_MESSAGE))
      {
         setType(String.valueOf(type.getTypeSpecifier()));
      }
      if (type == LogLineFormat.NESTED_MESSAGE)
      {
         final LogFileEntry entry = LogFileEntry.getLogFileEntry();
         setCurrentEntry(entry);
         setNestedEntry(entry);
         getFormat(type).parse(sb, entry);
      }
      else
      {
         getFormat(type).parse(sb, this);
      }
   }

   /**
    * Used for resetting this to an initial state so that this object could be
    * reused again.
    * Releases the nested entry, if there is such.
    *
    * @throws LoggingException if an error occurs.
    */
   public void reset ()
         throws LoggingException
   {
      final LogFileEntry nested = (LogFileEntry) getNestedItem();
      super.reset();
      mEntryCursor = this;
      mMetStackTraceLine = false;
      mRootEntry = this;

      if (nested != null)
      {
         nested.release();
      }
   }

   /**
    * By calling this a client signals he has finished using this instance. This
    * should be called for each instance not in use anymore.
    * Releases the nested entry as well, if there is such.
    *
    * @throws LoggingException if an error occurs.
    */
   void release ()
         throws LoggingException
   {
      try
      {
         reset();
         if (mPooled)
         {
            POOL.returnObject(this);
         }
      }
      catch (Exception ex)
      {
         throw new LoggingException("Error releasing this " + this, ex);
      }
   }

   /**
    * Sets the supplied entry as nested entry for this and this as parent for
    * the supplied entry.
    *
    * @param nestedEntry The LogFileEntry to set as nested entry for this.
    */
   private void setNestedEntry (final LogFileEntry nestedEntry)
   {
      nestedEntry.mRootEntry = this.mRootEntry;
      setNestedItem(nestedEntry);
   }

   private LogFileEntry getCurrentEntry ()
   {
      return mRootEntry.mEntryCursor;
   }

   private void setCurrentEntry (final LogFileEntry entry)
   {
      mRootEntry.mEntryCursor = entry;
   }

}
