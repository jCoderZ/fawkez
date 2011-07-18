/*
 * $Id: LogLineFormatFactory.java 1011 2008-06-16 17:57:36Z amandel $
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

/**
 * This is a factory for creating an instance of a LogLineFormat for a certain
 * log line type.
 *
 */
public final class LogLineFormatFactory
{
   private LogLineFormatFactory ()
   {
      // no public default constructor
   }

   /**
    * Creates a format according to the supplied log line type.
    *
    * @param type The type for which to create the corresponding Format.
    *
    * @return The newly created format.
    *
    * @throws IllegalArgumentException if the format for the supplied type
    * is unknown.
    */
   public static LogLineFormat create (final LogLineFormat.LogLineType type)
         throws IllegalArgumentException
   {
      LogLineFormat rc = null;

      if (type == LogLineFormat.STACKTRACE_MESSAGE)
      {
         rc = new StackTraceFormat();
      }
      else if (type == LogLineFormat.PARAMETER_LINE)
      {
         rc = new ParameterLineFormat();
      }
      else if (type == LogLineFormat.ERROR_MESSAGE)
      {
         rc = new ErrorLineFormat();
      }
      else if (type == LogLineFormat.TRACE_MESSAGE)
      {
         rc = new TraceLineFormat();
      }
      else if (type == LogLineFormat.EXCEPTION_MESSAGE)
      {
         rc = new ExceptionLineFormat();
      }
      else if (type == LogLineFormat.LOG_MESSAGE)
      {
         rc = new MessageLineFormat();
      }
      else if (type == LogLineFormat.NESTED_MESSAGE)
      {
         rc = new NestedLineFormat();
      }
      else
      {
         throw new IllegalArgumentException("Unknown log line type " + type);
      }
      return rc;
   }
}
