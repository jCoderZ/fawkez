/*
 * $Id: DevelopersLogFormatter.java 1360 2009-03-29 12:06:12Z amandel $
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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jcoderz.commons.types.Date;
import org.jcoderz.commons.util.ArraysUtil;
import org.jcoderz.commons.util.StringUtil;

/**
 * This type implements a Formatter to be used for logging onto
 * the console. It tries to fulfill developers need during development
 * as console output and is not meant to be a production system logger.
 * It formats both standard a {@link java.util.logging.LogRecord} and
 * instances of {@link org.jcoderz.commons.Loggable}.
 *
 */
public class DevelopersLogFormatter
      extends Formatter
{
   private final ThreadLocal mMessageFormatters = new ThreadLocal();

   private static final String LINE_SEPARATOR
       = System.getProperty("line.separator");
   private static final int LOCATION_LENGTH = 30;

   private static final long DUMP_TIME_DIFF = 60 * 1000;

   // THREADSAVE?
   private long mLastCall = 0;


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

      if (record.getMillis() - mLastCall >= DUMP_TIME_DIFF)
      {
          mLastCall = record.getMillis();
          formatDumpTime(sb, record.getMillis());
      }

      if (record.getLevel() == Level.FINER)
      {
          if (record.getMessage() == "THROW")
          {
              formatThrown(sb, record);
          }
          else if (record.getMessage() == "ENTRY")
          {
              formatEntering(sb, record);
          }
          else if (record.getMessage() == "ENTRY {0}")
          {
              formatEntering(sb, record);
          }
          else if (record.getMessage().startsWith("ENTRY {0} {1}"))
          {
              formatEntering(sb, record);
          }
          else if (record.getMessage() == "RETURN")
          {
              formatExiting(sb, record);
          }
          else if (record.getMessage() == "RETURN {0}")
          {
              formatExiting(sb, record);
          }
          else
          {
              format(sb, record);
          }
      }
      else
      {
          format(sb, record);
      }
      sb.append(LINE_SEPARATOR);

      return sb.toString();
   }

   private void formatDumpTime (StringBuffer sb, long millis)
   {
       sb.append(new Date(millis).toString());
       sb.append(LINE_SEPARATOR);
   }

   private void format (StringBuffer sb, LogRecord record)
   {
       formatLevelMarkerStart(sb, record.getLevel());
       sb.append(getChar(record.getLevel()));
       formatLocation(sb, record);
       sb.append(": ");
       formatMessage(sb, record);
       if (record.getThrown() != null)
       {
           sb.append(LINE_SEPARATOR);
           formatStackTrace(sb, record.getThrown());
       }
       formatLevelMarkerEnd(sb, record.getLevel());
   }


   private void formatStackTrace (StringBuffer sb, Throwable thrown)
   {   // TODO: Refine
       final StringWriter sw = new StringWriter();
       final PrintWriter pw = new PrintWriter(sw);
       thrown.printStackTrace(pw);
       sb.append(sw.toString().trim());
   }

   private void formatMessage (StringBuffer sb, LogRecord record)
   {
       final Object [] parameters = record.getParameters();
       final String msg = record.getMessage();
       if (parameters != null && parameters.length != 0 &&
           !StringUtil.isEmptyOrNull(msg) && msg.indexOf('{') >= 0)
       {
           try
           {
               final MessageFormat formatter = new MessageFormat(msg);
               formatter.format(parameters, sb, null);
           }
           catch (IllegalArgumentException ex)
           {
               sb.append(ArraysUtil.toString(parameters));
               sb.append(' ');
               sb.append(msg);
           }
       }
       else
       {
           sb.append(msg);
       }
   }


   private static void formatEntering (StringBuffer sb, LogRecord record)
   {
       sb.append('>'); // Indent?
       formatLocation(sb, record);
       sb.append('(');
       formatArgumentsCompact(sb, record.getParameters());
       sb.append(')');
   }

   private static void formatExiting (StringBuffer sb, LogRecord record)
   {
       sb.append('<'); // Indent?
       formatLocation(sb, record);
       sb.append(' ');
       formatArgumentsCompact(sb, record.getParameters());
   }

   private static void formatArgumentsCompact (StringBuffer sb,
       Object[] parameters)
   {
       final Iterator i = Arrays.asList(parameters).iterator();
       while (i.hasNext())
       {
           Object parameter = i.next();
           sb.append(parameter);
           if (i.hasNext())
           {
               sb.append(", ");
           }
       }
   }

   private static void formatThrown (StringBuffer sb, LogRecord record)
   {
       sb.append('!'); // Throwing!
       formatLocation(sb, record);
       sb.append(": ");
       formatThrowableCompact(sb, record.getThrown());
   }

    private static void formatThrowableCompact (StringBuffer sb,
        Throwable thrown)
    {
        Throwable ex = thrown;
        sb.append(String.valueOf(ex));
        while (ex.getCause() != null)
        {
            sb.append(" CAUSED BY: ");
            sb.append(String.valueOf(ex));
            ex = ex.getCause();
        }
    }

    private static void formatLocation (StringBuffer sb, LogRecord record)
    {   // TODO: Refine!!

        final String className = record.getSourceClassName();
        final StringBuffer b = new StringBuffer();
        if (!StringUtil.isEmptyOrNull(className))
        {
            b.append(className);
        }
        else
        {
            b.append('?');
        }
        b.append('.');
        final String methodName = record.getSourceMethodName();
        if (!StringUtil.isEmptyOrNull(methodName))
        {
            b.append(methodName);
        }
        else
        {
            b.append('?');
        }
        while (b.length() < LOCATION_LENGTH)
        {   // IMPROVEME
            b.insert(0, ' ');
        }
        if (b.length() > LOCATION_LENGTH)
        {
            b.replace(0, b.length() - LOCATION_LENGTH , "");
        }
        sb.append(b);
    }

    private char getChar (Level level)
    {
        final int value = level.intValue();
        final char result;
        if (value <= Level.FINEST.intValue())
        {
            result = '.';
        }
        else if (value <= Level.FINER.intValue())
        {
            result = 'o';
        }
        else if (value <= Level.FINE.intValue())
        {
            result = 'O';
        }
        else if (value <= Level.CONFIG.intValue())
        {
            result = '#';
        }
        else if (value <= Level.INFO.intValue())
        {
            result = ' ';
        }
        else if (value <= Level.WARNING.intValue())
        {
            result = '!';
        }
        else if (value <= Level.SEVERE.intValue())
        {
            result = '*';
        }
        else
        {
            result = '|';
        }
        return result;
    }

    private void formatLevelMarkerStart (StringBuffer sb, Level level)
    {
        final int value = level.intValue();
        if (value > Level.INFO.intValue())
        {
            sb.append("+----------------------------------");
            sb.append(LINE_SEPARATOR);
        }
    }

    private void formatLevelMarkerEnd (StringBuffer sb, Level level)
    {
        final int value = level.intValue();
        if (value > Level.INFO.intValue())
        {
            sb.append(LINE_SEPARATOR);
            sb.append("+----------------------------------");
        }
    }
}
