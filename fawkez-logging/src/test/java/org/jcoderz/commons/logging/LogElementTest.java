/*
 * $Id: LogElementTest.java 1279 2009-02-11 20:13:00Z amandel $
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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jcoderz.commons.BaseException;
import org.jcoderz.commons.BaseRuntimeException;
import org.jcoderz.commons.LogFormatterOutputTest;


/**
 * This class is used for testing the LogElement wrapper of a LogRecord. It
 * installs a LogFormatter, which allocates a LogElement for each LogRecord to
 * publish and uses LogFormatterOutputTest for generating the log messages.
 *
 */
public class LogElementTest
      extends LogFormatterOutputTest
{
   private static final String CLASSNAME = FormatTest.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private static final Logger ROOT_LOGGER = Logger.getLogger("");

   private Handler mHandler = null;

   private static class LogElementHandler
         extends Handler
   {
      /**
       * Closes this Handler, this is a NOP.
       *
       * @see java.util.logging.Handler#close()
       */
      public void close ()
            throws SecurityException
      {
         // nop
      }

      /**
       * Flushes this Handler, this is a NOP.
       *
       * @see java.util.logging.Handler#flush()
       */
      public void flush ()
      {
         // nop
      }

      /**
       * Just allocates a new LogElement from the log record.
       *
       * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
       *
       * TODO Check LogElement elements.
       */
      public void publish (LogRecord record)
      {
         final LogElement element = new LogElement(record);
         LoggerUtil.checkLogItem(element, record);
      }
   }

   /**
    * Creates a new test case instance.
    */
   public LogElementTest ()
   {
      super();
   }

   public void testSimpleLogRecord ()
   {
      logger.info("testSimpleLogRecord: " + getClass().getName());
   }

   /**
    * Installs an additional Handler, which publishes RocRecords using
    * LogElements.
    *
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp ()
   {
      mHandler = new LogElementHandler();
      ROOT_LOGGER.addHandler(mHandler);
      Logger.getLogger(BaseException.class.getName()).setLevel(Level.INFO);
      Logger.getLogger(BaseRuntimeException.class.getName()).setLevel(Level.INFO);
   }

   /**
    * Deinstalls the additional Handler.
    *
    * @see junit.framework.TestCase#setUp()
    */
   protected void tearDown ()
   {
      if (mHandler != null)
      {
         ROOT_LOGGER.removeHandler(mHandler);
         mHandler = null;
      }
   }
}
