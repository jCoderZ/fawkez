/*
 * $Id: StringEscapeFormat.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.text.ParsePosition;

import org.jcoderz.commons.util.Assert;


/**
 * This formatter is used to escape chars in the source string with a
 * configured escape char.The default escape char is '\'.
 *
 */
public class StringEscapeFormat
      extends Format
{
   static final char ESCAPE_CHAR = '\\';

   private static final long serialVersionUID = 3257567291389851442L;

   private final char mEscapeChar;
   private final String mCharsToEscape;

   /**
    * Creates an instance of this, which will use the default escape char
    * {@linkplain #ESCAPE_CHAR}.
    *
    * @param charsToEscape the characters which will be escaped by
    * {@linkplain #ESCAPE_CHAR}.
    */
   public StringEscapeFormat (final String charsToEscape)
   {
      this(charsToEscape, ESCAPE_CHAR);
   }

   /**
    * Creates an instance of this and sets the supplied escape char.
    *
    * @param charsToEscape The characters which will be escaped by
    * <code>escapeChar</code>.
    * @param escapeChar The char by which to escape <code>charToEscape</code>.
    * This must not be included in <code>charsToEscape</code> and must not be 0.
    *
    * @throws IllegalArgumentException if <code>escapeChar</code> is included
    * within <code>charsToEscape</code> or is <code>0</code>.
    */
   public StringEscapeFormat (
         final String charsToEscape,
         final char escapeChar)
   {
      super();
      Assert.notNull("charsToEscape", charsToEscape);
      mEscapeChar = escapeChar;
      mCharsToEscape = charsToEscape;
      if (escapeChar == 0)
      {
         throw new IllegalArgumentException(
               "The escape character must not be 0");
      }
      if ((charsToEscape != null) && (charsToEscape.indexOf(escapeChar) >= 0))
      {
         throw new IllegalArgumentException(
            "The escape character must not be one of the characters to escape");
      }
   }

   /**
    * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
    *
    * Parses the source string. All characters configured as characters to
    * escape are considered as delimiters (thus parsing is stopped at one of
    * those) if they are not escaped with the configured character.
    * A new string is created from this substring with all escape chars before
    * an char to escape removed. Escape chars not being followed by a character
    * to escape are not removed for the result string.
    *
    * @param source The string to parse.
    * @param pos the position within <code>source</code>.
    *
    * @return the String being parsed from <code>source</code> with escape
    *       chars removed.
    */
   public Object parseObject (String source, ParsePosition pos)
   {
      final int sourceLen = source.length();
      final StringBuffer rc = new StringBuffer();
      final int offs = pos.getIndex();

      int fromIndex = offs;
      boolean escaped = false;
      boolean delimiterFound = false;
      int index;
      for (index = offs; index < sourceLen && ! delimiterFound; ++index)
      {
         final char c = source.charAt(index);

         if (mCharsToEscape.indexOf(c) >= 0)
         {
            if (escaped)
            {
               if (fromIndex != index - 1)
               {
                  // skip over escape char
                  rc.append(source.substring(fromIndex, index - 1));
               }
               fromIndex = index;
            }
            else
            {
               delimiterFound = true;
               index--;
            }
            escaped = false;
         }
         else
         {
            escaped = (c == mEscapeChar);
         }
      }
      if (fromIndex < index)
      {
         rc.append(source.substring(fromIndex, index));
      }
      if (index > offs)
      {
         pos.setIndex(index);
      }
      return rc.toString();
   }

   /**
    * Formats the supplied object by calling the object's toString() method
    * and escaping the configured chars of this string with the configured
    * escape char.
    * @see Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
    *
    * @param obj the object to format.
    * @param toAppendTo the StringBuffer where to append to the formatted
    * string.
    * @param pos the field position.
    *
    * @return StringBuffer where the formatted Object has been appended to.
    */
   public StringBuffer format (Object obj, StringBuffer toAppendTo,
         FieldPosition pos)
   {
      final String objString = String.valueOf(obj);
      int fromIndex = 0;
      final int len = objString.length();
      for (int i = 0; i < len; ++i)
      {
         final char currentChar = objString.charAt(i);
         if (mCharsToEscape.indexOf(currentChar) >= 0)
         {
            if (fromIndex < i)
            {
               toAppendTo.append(objString.substring(fromIndex, i));
               fromIndex = i;
            }
            toAppendTo.append(mEscapeChar);
         }
      }
      if (fromIndex == 0)
      {
         toAppendTo.append(objString);
      }
      else
      {
         toAppendTo.append(objString.substring(fromIndex));
      }
      return toAppendTo;
   }
}
