/*
 * $Id: LogLevelFilter.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;


/**
 * Log filter that filters on fawkez loggables and on the log level
 * of the respective record.
 * @author Albrecht Messner
 */
public class LogLevelFilter
      extends LogFilterBase
{
   /** The full qualified name of this class. */
   private static final String CLASSNAME = LogLevelFilter.class.getName();

   private static final String LOGLEVEL_PROPERTY_NAME = CLASSNAME + ".level";

   private final Level mFilterLevel;

   /**
    * Creates a new instance of this and initiates the log level for messages,
    * which should pass this filter. If an error occurs retrieving the
    * configured level, <code>java.util.logging.Level.INFO</code> is set.
    *
    */
   public LogLevelFilter ()
   {
      final String levelStr
            = LogManager.getLogManager().getProperty(LOGLEVEL_PROPERTY_NAME);
      Level level;
      try
      {
         level = Level.parse(levelStr);
      }
      // possible exceptions: NullPointer, IllegalArgument
      catch (Exception x)
      {
         level = Level.INFO;
      }
      mFilterLevel = level;
   }

   /** {@inheritDoc} */
   public boolean isLoggable (LogRecord record)
   {
      final Loggable loggable = getLoggable(record);
      final boolean result;
      if (loggable != null
            && record.getLevel().intValue() >= mFilterLevel.intValue())
      {
         result = true;
      }
      else
      {
         result = false;
      }
      return result;
   }
}
