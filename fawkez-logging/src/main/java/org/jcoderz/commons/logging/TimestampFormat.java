/*
 * $Id: TimestampFormat.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import org.jcoderz.commons.types.Date;



/**
 * This format is used for formatting and parsing an object of type
 * {@link org.jcoderz.commons.types.Date} with default settings.
 *
 */
public class TimestampFormat
      extends Format
{
   private static final long serialVersionUID = 3256719572219212851L;

   // All formatted default Date objects are assumed to be of this length.
   private static final int DATE_SIZE
         = Date.now().toString().length();

   /**
    * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
    *
    * Parses the source string for an instance of type
    * {@link org.jcoderz.commons.types.Date}. Assumes all default Dates to be
    * of the same fix length.
    *
    * @param source The string to parse.
    * @param pos the positionn within <code>source</code>.
    *
    * @return the Date object being parsed from <code>source</code>.
    */
   public Object parseObject (String source, ParsePosition pos)
   {
      Date rc = null;
      try
      {
         rc = Date.fromString(
               source.substring(pos.getIndex(), pos.getIndex() + DATE_SIZE));
         pos.setIndex(pos.getIndex() + DATE_SIZE);
      }
      catch (ParseException pex)
      {
         rc = null;
         pos.setErrorIndex(pos.getIndex());
      }
      return rc;
   }

   /**
    * Formats the supplied Date object by calling its toString() method.
    *
    * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
    *
    * @param obj the object to format, must be an instance of type
    * {@link org.jcoderz.commons.types.Date}.
    * @param toAppendTo the StringBuffer where to append to the formatted
    * object.
    * @param pos the field position.
    *
    * @return StringBuffer where the formatted Object has been appended to.
    */
   public StringBuffer format (Object obj, StringBuffer toAppendTo,
         FieldPosition pos)
   {
      if (! (obj instanceof Date))
      {
         throw new IllegalArgumentException("The supplied object must be a "
               + Date.class.getName() + " but is " + obj.getClass().getName());
      }
      pos.setBeginIndex(0);
      pos.setEndIndex(0);
      toAppendTo.append(((Date) obj).toString());
      return toAppendTo;
   }
}
