/*
 * $Id: WhitespaceFormat.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.util.Iterator;
import java.nio.CharBuffer;
import java.util.NoSuchElementException;


/**
 * This Formatter formats the whitespace of a string, The space char
 * <code>'\u0020'</code> is left untouched, all other whitespace chars are
 * replaced and compressed by exactly one space char in a row. It extends the
 * Format type in a unsymmetric way: A formatted String cannot be parsed in a
 * way that the result is equal to the source string.
 * If allocated with a sub format, first the sub format is used for formatting,
 * the rewsult will be formatted by this.
 * In addition to the Format implementation it offers static access methods
 * for the format functionality.
 *
 */
public final class WhitespaceFormat
      extends Format
{
   private static final long serialVersionUID = 1L;

   static final char PRESERVED_CHAR = '\u0020';

   private final Format mSubFormat;

   private static final class WhitespaceIterator
         implements Iterator<Object>
   {
      private final CharBuffer mBuffer;

      /**
       * Constructs a white space iterator fo the supplied text. Skips over
       * initial whitespace.
       *
       * @param text The text which should be read line by line.
       */
      private WhitespaceIterator (final CharBuffer buffer)
      {
         mBuffer = buffer.duplicate();
         skip();
      }

      /** {@inheritDoc} */
      public void remove ()
      {
         throw new UnsupportedOperationException();
      }

      /** {@inheritDoc} */
      public boolean hasNext ()
      {
         return mBuffer.remaining() > 0;
      }

      /**
       * Gets the next character sequence up to the next whitespace char
       * (excluding), which is not the space character <code>' '</code>.
       * The character sequence being returned by this will never contain a
       * whitespace char but the space char <code>'\u0020'</code>.
       *
       * @see java.util.Iterator#next()
       */
      public Object next ()
      {
         if (mBuffer.remaining() <= 0)
         {
            throw new NoSuchElementException();
         }
         boolean wsFound = false;
         boolean postWsFound = false;

         int startOfWs = 0;
         int endOfWs = 0;

         // CHECKME: on some platforms slice does not behave as stated in SDK
         // api, mBuffer is simply duplicated.
         final CharBuffer rc = mBuffer.slice();
         for (int i = 0; i < mBuffer.remaining() && ! (wsFound && postWsFound);
               ++i)
         {
            final char c = mBuffer.charAt(i);

            if (Character.isWhitespace(c) && ! (c == PRESERVED_CHAR))
            {
               if (! wsFound)
               {
                  startOfWs = i;
               }
               endOfWs = i;
               wsFound = true;
               postWsFound = false;
            }
            else
            {
               postWsFound = true;
            }
         }
         setPositions(rc, wsFound, postWsFound, startOfWs, endOfWs);
         return rc;
      }

      /**
       * Sets the positions and limits of supplied buffer and internal buffer
       * for the result of a next call.
       *
       * @param rc This buffer is returned to the <code>next()</code> caller.
       * @param wsFound Flag denoting whether whitespace to replace has been
       * found.
       * @param postWsFound Flag for denoting whether chars after whitespace
       * have been found.
       * @param startOfWs The start index of whitespace chars to replace.
       * @param endOfWs THe end index of teh whitespace chars to replace.
       */
      private void setPositions (
            final CharBuffer rc,
            final boolean wsFound,
            final boolean postWsFound,
            final int startOfWs,
            final int endOfWs)
      {
         if (wsFound)
         {
            // CHECKME: if slice does not work as said in the api, then position
            // is > 0 and idx has to be added to the position to get the new
            // limit
            if (rc.position() > 0)
            {
               rc.limit(rc.position() + startOfWs);
            }
            else
            {
               rc.limit(startOfWs);
            }
            if (! postWsFound)
            {
               mBuffer.position(mBuffer.limit());
            }
            else
            {
               mBuffer.position(mBuffer.position() + endOfWs + 1);
            }
         }
         else
         {
            mBuffer.position(mBuffer.limit());
         }
      }

      /**
       * Skips over initial whitespace, which is not
       * {@linkplain WhitespaceFormat#PRESERVED_CHAR}
       */
      private void skip ()
      {
         boolean wsFound = false;
         boolean postWsFound = false;
         boolean first = true;

         int endOfWs = 0;

         for (int i = 0; i < mBuffer.remaining()
            && ((wsFound ^ postWsFound) || first);
               ++i)
         {
            first = false;
            final char c = mBuffer.charAt(i);

            if (Character.isWhitespace(c) && ! (c == PRESERVED_CHAR))
            {
               wsFound = true;
               endOfWs = i;
            }
            else
            {
               postWsFound = wsFound;
            }
         }
         if (wsFound && ! postWsFound)
         {
            // only whitespace found
            mBuffer.position(mBuffer.limit());
         }
         else if (wsFound)
         {
            // found chars after whitespace, so the following is correct
            mBuffer.position(mBuffer.position() + endOfWs);
         }
      }
   }

   /**
    * Creates a new instance of this with no sub format.
    */
   public WhitespaceFormat ()
   {
      this(null);
   }

   /**
    * Creates a new instance of this with the supplied sub format.
    *
    * @param subFormat The sub format to use for first step formatting of an
    * object. This will be used for parsing an object as well. Might be null.
    */
   public WhitespaceFormat (final Format subFormat)
   {
      mSubFormat = subFormat;
   }

   /**
    * Replaces and reduces whitespace in the supplied message. The resulting
    * string will only have <code>'\u0020'</code> as white space. Any such
    * character in the source string is left untouched, all other whitespace
    * characters are replaced by <code>'\u0020'</code>, but with only one in a
    * row, so, for example, a sequence of 2 line separators will be replaced
    * by one <code>'\u0020'</code>.
    *
    * @param message The message in which to find and replace white space.
    *
    * @return String with replaced and reduced white space
    */
   public static String format (final String message)
   {
      return format(CharBuffer.wrap(message)).toString();
   }

   /**
    * Replaced and reduces whitespace in the supplied character buffer.
    *
    * @see #format(String)

    * @param message The message buffer in which to find and replace white
    * space.
    *
    * @return CharBuffer with replaced and reduced white space. This might be
    * <code>message</code> if it does not contain whitespace to replace.
    */
   public static CharBuffer format (final CharBuffer message)
   {
      final WhitespaceIterator iter = new WhitespaceIterator(message);

      CharBuffer rc = null;
      boolean isFirst = true;
      boolean flip = false;

      while (iter.hasNext())
      {
         final CharBuffer cb = (CharBuffer) iter.next();

         if (! (isFirst || (rc == null)))
         {
            rc.put(PRESERVED_CHAR);
            rc.put(cb);
         }
         else if (isFirst)
         {
            isFirst = false;
            if ((cb.limit() == message.limit())
                  && (cb.position() == message.position()))
            {
               rc = message.duplicate();
            }
            else
            {
               rc = CharBuffer.allocate(message.limit());
               rc.put(cb);
               flip = true;
            }
         }
         else
         {
            // should never occur
            throw new RuntimeException("More than one string parts and no "
                  + "target buffer is allocated");
         }
      }
      if (flip)
      {
         rc.flip();
      }
      return rc;
   }

   /**
    * If a sub format is set, it delegates parsing to the sub format. If no
    * subformat is set, it takes the source string until the first whitespace
    * char is found, which is not {@link #PRESERVED_CHAR}.
    *
    * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
    */
   public Object parseObject (final String source, final ParsePosition pos)
   {
      Object rc;
      if (mSubFormat == null)
      {
         int i = pos.getIndex();
         final int len = source.length();
         boolean endFound = false;

         while (i < len && ! endFound)
         {
            final char c = source.charAt(i);
            if (Character.isWhitespace(c) && c != PRESERVED_CHAR)
            {
               endFound = true;
            }
            else
            {
               ++i;
            }
         }
         if (endFound)
         {
            rc = source.substring(pos.getIndex(), i);
            pos.setIndex(i);
         }
         else
         {
            rc = source.substring(pos.getIndex());
            pos.setIndex(len);
         }
      }
      else
      {
         rc = mSubFormat.parseObject(source, pos);
      }
      return rc;
   }

   /**
    * If a sub format is set, it uses this to format the object and compresses
    * the whitespace within the result.
    * If no sub format is set, it expects a String object and compresses the
    * whitespace on that.
    *
    * @param obj The object to format.
    * @param toAppendTo The string buffer where to append to the formatted
    * object.
    * @param pos The field position for formatting.
    *
    * @return StringBuffer with formatted objects.
    *
    * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
    */
   public StringBuffer format (
         final Object obj,
         final StringBuffer toAppendTo,
         final FieldPosition pos)
   {
      if (mSubFormat == null)
      {
         if (! (obj instanceof String))
         {
            throw new IllegalArgumentException("Supplied object to be formatted"
                  + " must be a String but is "
                  + obj.getClass().getName() + ": " + obj);
         }
         toAppendTo.append(format((String) obj));
         if (pos != null)
         {
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
         }
      }
      else
      {
         StringBuffer sb = new StringBuffer();
         sb = mSubFormat.format(obj, sb, pos);
         toAppendTo.append(WhitespaceFormat.format(sb.toString()));
      }
      return toAppendTo;
   }
}
