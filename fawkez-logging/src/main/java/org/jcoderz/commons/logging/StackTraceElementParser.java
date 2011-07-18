/*
 * $Id: StackTraceElementParser.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This helper class is used to parse a CharBuffer containing data of one
 * line of a StackTrace.
 * A Stacktrace line is one of:
 * <ul>
 * <li><code>at packagename.classname.methodname(</code>...<code>)</code>
 * <li><code>Caused by: </code>...
 * <li><code>...</code> nn <code>more</code>
 * <li><code>exception message</code>
 * </ul>
 *
 */
public final class StackTraceElementParser
{
   private static final Pattern FULL_LOCATION_PATTERN = Pattern.compile(
         "^\\s*at\\s+[^\\s\\.]([^\\s\\(]+\\.)*[^\\s\\(]+\\(.*\\)\\s*$");
   private static final Pattern LOCATION_PATTERN = Pattern.compile(
         "^\\s*at\\s+");
   private static final Pattern CLASS_PATTERN = Pattern.compile(
         "([^\\s\\(]+\\.)*");
   private static final Pattern METHOD_PATTERN = Pattern.compile(
         "[^\\s\\(]+\\s*\\(");
   private static final Pattern LINE_PATTERN = Pattern.compile(
         "[^\\d\\)]+");

   private static final Pattern FULL_CAUSE_PATTERN = Pattern.compile(
         "^\\s*Caused by\\:\\s*.*$");
   private static final Pattern CAUSE_PATTERN = Pattern.compile(
         "^\\s*Caused by\\:\\s*");

   private static final Pattern FULL_MORE_PATTERN = Pattern.compile(
         "^\\s*\\.+\\s*\\d+\\s+more\\s*$");
   private static final Pattern MORE_PATTERN = Pattern.compile(
         "^\\s*\\.+\\s*");

   private static final Pattern FULL_EXCEPTION_TEXT_PATTERN = Pattern.compile(
         "^\\s*.*$");
   private static final Pattern EXCEPTION_TEXT_PATTERN = Pattern.compile(
         "^\\s*");

   /**
    * Hide the default constructor.
    */
   private StackTraceElementParser ()
   {
      // nop
   }

   /**
    * Parses the supplied buffer for the parameters of a stacktrace element and
    * returns the information as StackTraceInfo.
    *
    * @param buffer The buffer containing data of one stack trace line.
    *
    * @return A new instance of StackTraceInfo containing the information of the
    * parsed line.
    *
    * @throws ParseException if an error occurs.
    */
   public static StackTraceInfo parse (final CharBuffer buffer)
         throws ParseException
   {
      StackTraceInfo rc = null;
      StackTraceInfo stInfo = null;

      if ((stInfo = parseLocation(buffer)) != null)
      {
         rc = stInfo;
      }
      else if ((stInfo = parseCause(buffer)) != null)
      {
         rc = stInfo;
      }
      else if ((stInfo = parseMore(buffer)) != null)
      {
         rc = stInfo;
      }
      else if ((stInfo = parseMore(buffer)) != null)
      {
         rc = stInfo;
      }
      else if ((stInfo = parseExceptionText(buffer)) != null)
      {
         rc = stInfo;
      }
      else
      {
         throw new ParseException(
               "Buffer does not match any of the defined patterns: "
               + buffer, buffer.position());
      }
      return rc;
   }

   private static StackTraceInfo parseLocation (final CharBuffer buffer)
         throws ParseException
   {
      StackTraceInfo rc = null;

      Matcher matcher = FULL_LOCATION_PATTERN.matcher(buffer);

      if (matcher.matches())
      {
         final int savePos = buffer.position();

         matcher = LOCATION_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException("Cannot parse correctly: " + buffer,
                  buffer.position());
         }
         int pos = matcher.end();
         buffer.position(buffer.position() + pos);
         matcher = CLASS_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException("Cannot parse correctly: " + buffer,
                  buffer.position());
         }
         pos = matcher.end();
         final CharBuffer classname = buffer.asReadOnlyBuffer();
         classname.limit(classname.position() + pos - 1);

         buffer.position(buffer.position() + pos);
         matcher = METHOD_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException("Cannot parse correctly: " + buffer,
                  buffer.position());
         }
         pos = matcher.end();
         final CharBuffer methodname = buffer.asReadOnlyBuffer();
         methodname.limit(methodname.position() + pos - 1);
         int line = -1;
         matcher = LINE_PATTERN.matcher(buffer);
         // this time there need not to be a match
         if (matcher.lookingAt())
         {
            pos = matcher.end();
            buffer.position(buffer.position() + pos);
            boolean digit = false;
            int i = 0;
            while (Character.isDigit(buffer.charAt(i)))
            {
               ++i;
               digit = true;
            }
            if (digit)
            {
               line = Integer.parseInt(buffer.subSequence(0, i).toString());
            }
         }
         buffer.position(savePos);
         rc = new StackTraceInfo(
               buffer.asReadOnlyBuffer(), classname, methodname, line);
      }
      return rc;
   }

   private static StackTraceInfo parseCause (final CharBuffer buffer)
         throws ParseException
   {
      StackTraceInfo rc = null;
      Matcher matcher = FULL_CAUSE_PATTERN.matcher(buffer);
      if (matcher.matches())
      {
         final int savePos = buffer.position();
         matcher = CAUSE_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException("Cannot parse a caused-by correctly: "
                  + buffer, buffer.position());
         }
         final int pos = matcher.end();
         buffer.position(buffer.position() + pos);
         final CharBuffer cause = buffer.asReadOnlyBuffer();
         buffer.position(savePos);
         rc = new StackTraceInfo(buffer.asReadOnlyBuffer(), cause, true);
      }
      return rc;
   }

   private static StackTraceInfo parseExceptionText (final CharBuffer buffer)
         throws ParseException
   {
      StackTraceInfo rc = null;
      Matcher matcher = FULL_EXCEPTION_TEXT_PATTERN.matcher(buffer);
      if (matcher.matches())
      {
         final int savePos = buffer.position();
         matcher = EXCEPTION_TEXT_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException(
                  "Cannot parse an exception text correctly: " + buffer,
                  buffer.position());
         }
         final int pos = matcher.end();
         buffer.position(buffer.position() + pos);
         final CharBuffer exceptionText = buffer.asReadOnlyBuffer();
         buffer.position(savePos);
         rc = new StackTraceInfo(
               buffer.asReadOnlyBuffer(), exceptionText, false);
      }
      return rc;
   }

   private static StackTraceInfo parseMore (final CharBuffer buffer)
         throws ParseException
   {
      StackTraceInfo rc = null;
      Matcher matcher = FULL_MORE_PATTERN.matcher(buffer);
      if (matcher.matches())
      {
         final int savePos = buffer.position();
         matcher = MORE_PATTERN.matcher(buffer);
         if (! matcher.lookingAt())
         {
            throw new ParseException("Cannot parse a more-line correctly: "
                  + buffer, buffer.position());
         }
         final int pos = matcher.end();
         buffer.position(buffer.position() + pos);
         boolean digit = false;
         int i = 0;
         while (Character.isDigit(buffer.charAt(i)))
         {
            ++i;
            digit = true;
         }
         if (! digit)
         {
            throw new ParseException("Number of more lines is missing: "
                  + buffer, buffer.position());
         }
         final int moreLines = Integer.parseInt(
               buffer.subSequence(0, i).toString());
         buffer.position(savePos);
         rc = new StackTraceInfo(buffer.asReadOnlyBuffer(), moreLines);
      }
      return rc;
   }
}
