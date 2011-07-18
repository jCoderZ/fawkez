/*
 * $Id: StackTraceInfo.java 1011 2008-06-16 17:57:36Z amandel $
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

/**
 * This helper class wraps the information of one stack trace line.
 *
 */
public class StackTraceInfo
{
   private static final String CURRENT_LINE_ERROR_TEXT
         = "The current line is not a ";

   private final CharBuffer mStackTraceLine;

   /** Flag whether an 'at ...' line. */
   private final boolean mIsLocationLine;
   /** Flag whether an 'Caused by: ' line. */
   private final boolean mIsCauseLine;
   /** Flag whether an exception message line. */
   private final boolean mIsExceptionMessageLine;
   /** Flag whether an '... nnn more ' line. */
   private final boolean mIsMoreLine;

   private final CharBuffer mClassName;
   private final CharBuffer mMethodName;
   private final int mLine;
   private final int mMoreLines;

   private final CharBuffer mExceptionText;

   /**
    * Constructs this and sets the information of an 'at ...' stack trace line.
    *
    * @param lineData Contains the original line data.
    * @param className The parsed class name.
    * @param methodName The parsed method name.
    * @param line The parsed line number, is <= 0 if not available.
    */
   StackTraceInfo (
         final CharBuffer lineData,
         final CharBuffer className,
         final CharBuffer methodName,
         final int line)
   {
      mStackTraceLine = lineData;

      mIsLocationLine = true;
      mIsCauseLine = false;
      mIsExceptionMessageLine = false;
      mIsMoreLine = false;
      mClassName = className;
      mMethodName = methodName;
      mLine = line;
      mMoreLines = -1;
      mExceptionText = null;
   }

   /**
    * Constructs this and sets the information of an '... nnn more' stack trace
    * line.
    *
    * @param lineData Contains the original line data.
    * @param lines The parsed number of lines being omitted.
    */
   StackTraceInfo (
         final CharBuffer lineData,
         final int lines)
   {
      mStackTraceLine = lineData;

      mIsLocationLine = false;
      mIsCauseLine = false;
      mIsExceptionMessageLine = false;
      mIsMoreLine = true;
      mClassName = null;
      mMethodName = null;
      mLine = -1;
      mMoreLines = lines;
      mExceptionText = null;
   }

   /**
    * Constructs this and sets the information of an stack trace line, which
    * contains the exception message.
    *
    * @param lineData Contains the original line data.
    * @param message The exception message as it is parsed from
    * <code>lineData</code>.
    * @param isCause Flag whether the exception is a cause, i.e. whether the
    * message ahs a 'Caused by: ' prefix.
    */
   StackTraceInfo (
         final CharBuffer lineData,
         final CharBuffer message,
         final boolean isCause)
   {
      mStackTraceLine = lineData;

      mIsLocationLine = false;
      mIsCauseLine = isCause;
      mIsExceptionMessageLine = ! isCause;
      mIsMoreLine = false;
      mClassName = null;
      mMethodName = null;
      mLine = -1;
      mMoreLines = -1;
      mExceptionText = message;
   }

   /**
    * Returns the wrapped stack trace line as string.
    *
    * @return The stack trace line.
    *
    * @see java.lang.Object#toString()
    */
   public String toString ()
   {
      return String.valueOf(mStackTraceLine);
   }

   /**
    * Gets flag whether the current line is a caused-by line.
    *
    * @return true if the current line is a caused-by line; false, else.
    */
   boolean isCauseLine ()
   {
      return mIsCauseLine;
   }

   /**
    * Gets flag whether the current line contained the exception message with or
    * without the 'Caused by:' prefix. So whenever {@linkplain #isCauseLine()}
    * returns true, this returns true as well.
    *
    * @return true if the current line contained the exception message with or
    * without the cause-by prefix; false, else
    */
   boolean isExceptionMessageLine ()
   {
      return (mIsExceptionMessageLine || mIsCauseLine);
   }

   /**
    * Gets the exception message if the last line has been a cause line or a
    * exception message line.
    *
    * @return Returns the exception message.
    *
    * @throws IllegalStateException if the recent line was not a cause line or
    * exception message line.
    */
   String getExceptionMessage ()
   {
      if (! (mIsCauseLine || mIsExceptionMessageLine))
      {
         throw new IllegalStateException("The current line did not contain the "
               + "exception message");
      }
      return mExceptionText.toString();
   }

   /**
    * Gets flag whether the current line is an at ... line.
    *
    * @return true if the current line is an at ... line; false, else.
    */
   boolean isLocationLine ()
   {
      return mIsLocationLine;
   }

   /**
    * Gets the class name from an at-line.
    *
    * @return class name of an at-line
    *
    * @throws IllegalStateException if the recent line is not an at-line.
    */
   String getClassName ()
   {
      if (! mIsLocationLine)
      {
         throw new IllegalStateException(CURRENT_LINE_ERROR_TEXT
               + "'at ...' line");
      }
      return mClassName.toString();
   }

   /**
    * Gets the method name from an at-line.
    *
    * @return method of an at-line
    *
    * @throws IllegalStateException if the recent line is not an at-line.
    */
   String getMethodName ()
   {
      if (! mIsLocationLine)
      {
         throw new IllegalStateException(CURRENT_LINE_ERROR_TEXT
               + "'at ...' line");
      }
      return mMethodName.toString();
   }

   /**
    * Gests the line info of an at-line. This might not be accessible, in which
    * case 0 is returned.
    *
    * @return line information of at-line or 0 if no such.
    *
    * @throws IllegalStateException if the recent line is not an at-line.
    */
   int getLine ()
   {
      if (! mIsLocationLine)
      {
         throw new IllegalStateException(CURRENT_LINE_ERROR_TEXT
               + "'at ...' line");
      }
      return mLine;
   }

   /**
    * Gets flag whether the current line is an ... nnn more line.
    *
    * @return true if the current line is an ... more line; false, else.
    */
   boolean isMoreLine ()
   {
      return mIsMoreLine;
   }

   /**
    * Gets the number of stack trace items being concentrated into the more
    * line.
    *
    * @return number of stack trace lines being neglected.
    *
    * @throws IllegalStateException if the recent line was not a more-line.
    */
   int getMoreLines ()
   {
      if (! mIsMoreLine)
      {
         throw new IllegalStateException(CURRENT_LINE_ERROR_TEXT
               + "'... nnn more' line");
      }
      return mMoreLines;
   }
}
