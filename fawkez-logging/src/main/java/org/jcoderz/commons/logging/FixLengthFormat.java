/*
 * $Id: FixLengthFormat.java 1299 2009-03-23 20:06:23Z amandel $
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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * This is used to format a String to a fixed length String. Strings, which are
 * are longer than the configured length, are split after the appropriate number
 * of chars; Strings which are shorter, are left padded with the configured
 * padding char. When parsing a String, the configured number of chars is read
 * from the source String and the padding char is left trimmed from the
 * resulting String.
 *
 */
public class FixLengthFormat
      extends Format
{
   /**
    * The string is left padded with the padding char to get the full sized
    * string.
    */
   public static final Padding LEFT_PADDING = new Padding();

   /**
    * The string is right padded with the padding char to get the full sized
    * string.
    */
   public static final Padding RIGHT_PADDING = new Padding();

   /**
    * The string is right padded with the padding char to get the full sized
    * string or cut on the left side if it dues not fit the length.
    */
   public static final Padding LEFT_CUT_RIGHT_PADDING = new Padding();

   private static final Padding[] PADDINGS = {LEFT_PADDING, RIGHT_PADDING,
       LEFT_CUT_RIGHT_PADDING};

   private static final long serialVersionUID = 3256720688978278194L;

   private static final char DEFAULT_PADDING_CHAR = ' ';

   private final char[] mAllPadding;

   private final Padding mType;

   /**
    * This class implements a typesafe enumeration for the padding type.
    *
    */
   private static final class Padding
         implements Comparable, Serializable
   {
      private static final long serialVersionUID = 3258410642577831730L;

      private static int sOrdinal = 0;
      private final int mOrdinal;

      private Padding ()
      {
         mOrdinal = sOrdinal++;
      }

      private Object readResolve ()
            throws ObjectStreamException
      {
         return PADDINGS[mOrdinal];
      }

      /** {@inheritDoc} */
      public int compareTo (Object o)
      {
         return mOrdinal - ((Padding) o).mOrdinal;
      }
   }

   /**
    * Generates an instance of this with the default padding character, which
    * is a space.
    *
    * @param fixedLength all formatted strings will have this length, either
    * they are cut or padded with spaces.
    * @param type the type of padding, either {@linkplain #LEFT_PADDING} or
    * {@linkplain #RIGHT_PADDING}
    *
    * @throws IllegalArgumentException if <code>fixedLength <= 0</code>
    */
   public FixLengthFormat (final int fixedLength, final Padding type)
   {
      this(fixedLength, type, DEFAULT_PADDING_CHAR);
   }

   /**
    * Generates an instance of this with <code>paddingChar</code> as padding
    * character.
    *
    * @param fixedLength all formatted strings will have this length, either
    * they are cut or padded with <code>paddingChar</code>
    * @param type the type of padding, either {@linkplain #LEFT_PADDING} or
    * {@linkplain #RIGHT_PADDING}
    * @param paddingChar the character to use for padding.
    *
    * @throws IllegalArgumentException if <code>fixedLength <= 0</code>
    *
    */
   public FixLengthFormat (
         final int fixedLength,
         final Padding type,
         final char paddingChar)
   {
      super();
      if (fixedLength <= 0)
      {
         throw new IllegalArgumentException("Fixed length must be >= 0, but is "
               + fixedLength);
      }

      mType = type;
      mAllPadding = new char[fixedLength];
      Arrays.fill(mAllPadding, paddingChar);
   }

   /**
    * Parses the source string for a padded string from the position given by
    * <code>pos</code>. It reads the configured number of chars and trims
    * all chars equal to the padding character, either from left or from right,
    * according to the configured type of this.
    * If the given source string is not long enough, the index of
    * <code>pos</code> is not modified, but the error index is set to its
    * current index and null is returned.
    *
    * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
    *
    * @param source the string to parse
    * @param pos the parse position where to start parsing within
    * <code>source</code>. This will be updated after parsing.
    *
    * @return null if not enough chars to read from <code>source</code>;
    * else parsed String, one-side trimmed from padding char.
    */
   public Object parseObject (String source, ParsePosition pos)
   {
      String rc = null;

      if (pos.getIndex() + mAllPadding.length > source.length())
      {
         pos.setErrorIndex(pos.getIndex());
      }
      else
      {
         final CharBuffer paddedString = CharBuffer.wrap(source,
               pos.getIndex(), pos.getIndex() + mAllPadding.length);

         if (paddedString.length() != mAllPadding.length)
         {
            pos.setErrorIndex(pos.getIndex());
         }
         else
         {
            rc = parse(paddedString, pos).toString();
         }
      }
      return rc;
   }

   /**
    * Formats a string to be of a certain length. If the string is longer than
    * the configured length; it is cut and the first part being appended to the
    * StringBuffer, if it is shorter, this call pads it with the configured
    * char.
    *
    * @param obj the String to be formatted, must not be null.
    * @param toAppendTo the StringBuffer to which to append the formatted String
    * @param pos field position
    *
    * @return StringBuffer where the formatted object has been appended to.
    *
    * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
    */
   public StringBuffer format (Object obj, StringBuffer toAppendTo,
         FieldPosition pos)
   {
      if (obj == null)
      {
         throw new NullPointerException(
               "Supplied object to format must not be null");
      }
      else if (! (obj instanceof String))
      {
         throw new IllegalArgumentException("Supplied object to be formatted "
               + "must be a String but is "
               + obj.getClass().getName() + ": " + obj);
      }
      else
      {
         pos.setBeginIndex(0);
         pos.setEndIndex(0);
         final String toBeFormatted = (String) obj;
         final int len = toBeFormatted.length();
         if (len > mAllPadding.length && mType == LEFT_CUT_RIGHT_PADDING)
         {
             toAppendTo.append(toBeFormatted.substring(
                 len - mAllPadding.length));
         }
         else if (len > mAllPadding.length)
         {
            toAppendTo.append(toBeFormatted.substring(0, mAllPadding.length));
         }
         else if (len < mAllPadding.length)
         {
            if (mType == LEFT_PADDING)
            {
               toAppendTo.append(mAllPadding, 0, mAllPadding.length - len);
               toAppendTo.append(toBeFormatted);
            }
            else
            {
               toAppendTo.append(toBeFormatted);
               toAppendTo.append(mAllPadding, 0, mAllPadding.length - len);
            }
         }
         else
         {
            toAppendTo.append(toBeFormatted);
         }
      }
      return toAppendTo;
   }

   private CharSequence parse (
         final CharBuffer paddedString,
         final ParsePosition pos)
   {
      CharSequence rc = null;

      pos.setIndex(pos.getIndex() + mAllPadding.length);

      final char pad = mAllPadding[0];
      if (mType == LEFT_PADDING)
      {
         int i = 0;
         while ((i < mAllPadding.length)
               && (paddedString.charAt(i) == pad))
         {
            ++i;
         }
         rc = paddedString.subSequence(i, mAllPadding.length);
      }
      else
      {
         int i = mAllPadding.length - 1;
         while ((i >= 0) && (paddedString.charAt(i) == pad))
         {
            --i;
         }
         if (i < 0)
         {
            rc = "";
         }
         else
         {
            rc = paddedString.subSequence(0, i + 1);
         }
      }
      return rc;
   }
}
