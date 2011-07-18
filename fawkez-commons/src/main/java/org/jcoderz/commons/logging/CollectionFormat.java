/*
 * $Id: CollectionFormat.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This format is used for formatting a collection. The begin/end pair can be
 * set as well as the separator for the collection items and the formatter for
 * the items.
 *
 */
public class CollectionFormat
      extends Format
{
   private static final long serialVersionUID = 3258126947170400564L;

   /** The separator character between collection items. */
   private static final String SEPARATOR = ",";

   private static final String LIST_BEGIN = "[";
   private static final String LIST_END = "]";

   private final String mListBegin;
   private final String mListEnd;
   private final Format mListElementFormat;
   private final String mSeparator;

   private final int mLengthListBegin;
   private final int mLengthListEnd;
   private final int mLengthSeparator;


   /**
    * Creates a new instance of this and sets default values for the Strings
    * denoting the begin or end of the collection as well as the separator for
    * the collection items.
    *
    * @param elementFormat the Format to use for formatting each collection
    * element.
    */
   public CollectionFormat (final Format elementFormat)
   {
      this(elementFormat, LIST_BEGIN, LIST_END, SEPARATOR);
   }

   /**
    * Creates a new instance of this and configures values for the Strings
    * denoting the begin or end of the collection as well as the separator for
    * the collection items. If teh begin and empty markers are both empty or
    * null, then this might fail to parse empty lists.
    *
    * @param elementFormat the Format to use for formatting each collection
    * element. Must not be null.
    * @param begin The string to set as start of the collection, might be null,
    * then there will be no marker for the start of the collection.
    * @param end The string to set as end of the collection, might be null,
    * then there will be no marker for the end of the collection.
    * @param separator The string to set as separator between collection
    * elements, might be null, then ther will be no separator between the
    * elements.
    */
   public CollectionFormat (
         final Format elementFormat,
         final String begin,
         final String end,
         final String separator)
   {
      super();
      mListBegin = (begin != null) ? begin : "";
      mListEnd = (end != null) ? end : "";
      mListElementFormat = elementFormat;
      mSeparator = (separator != null) ? separator : "";
      mLengthListBegin = mListBegin.length();
      mLengthListEnd = mListEnd.length();
      mLengthSeparator = mSeparator.length();
   }

   /**
    * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
    *
    * Parses the supplied string for creating a list of object. If an error
    * occurs the position is unchanged, the error position is set to the
    * position where it occurred and null is returrned.
    * This expects the configured list begin sequence to start at the current
    * position, the list item to be delimited with the configured delimiter,
    * the latter not being parsed by the list item parser, and the list being
    * terminated by the list end sequence. If the list end sequence is set
    * to null or empty string, then empty lists are not detected.
    *
    * @param source The string to parse.
    * @param pos The current position within <code>source</code>
    *
    * @return null if an error occurs, the collection being parsed from the
    * source string else.
    */
   public Object parseObject (String source, ParsePosition pos)
   {
      List rc = null;
      final int len = source.length();
      boolean errorOccurred = false;

      final CharBuffer sourceBuffer
            = CharBuffer.wrap(source, pos.getIndex(), source.length());

      final int currentPos = pos.getIndex();

      // the string has to start with the configured list begin char, if it is
      // set
      if (beginOfList(sourceBuffer, pos))
      {
         rc = new ArrayList();

         if (! (mLengthListEnd != 0 && endOfList(sourceBuffer, pos)))
         {
            errorOccurred = parseListElements(
                  sourceBuffer, source, pos, len, rc);

            if (! (errorOccurred || endOfList(sourceBuffer, pos)))
            {
               pos.setErrorIndex(pos.getIndex());
               errorOccurred = true;
            }
         }
         if (errorOccurred)
         {
            rc.clear();
            rc = null;
            pos.setIndex(currentPos);
         }
      }
      else
      {
         pos.setErrorIndex(pos.getIndex());
      }
      return rc;
   }

   private boolean parseListElements (
         final CharBuffer sourceBuffer,
         final String source,
         final ParsePosition pos,
         final int len,
         final List elements)
   {
      boolean rc = false;

      // not an empty list
      do
      {
         final Object obj = mListElementFormat.parseObject(source, pos);
         if (obj == null)
         {
            rc = true;
         }
         else
         {
            elements.add(obj);
         }
      }
      while (separator(sourceBuffer, pos)
            && (pos.getIndex() < len)
            && ! rc);

      return rc;
   }

   /**
    * Formats a collection of strings.
    *
    * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
    *
    * @param obj the Object to format. Must be a Collection
    * @param toAppendTo the StringBuffer where to append to the formatted
    * Collection.
    * @param pos the field position.
    *
    * @return StringBuffer where the formatted collection has been appended to.
    *
    * @throws IllegalArgumentException if <code>obj</code> is not a Collection.
    */
   public StringBuffer format (
         Object obj,
         StringBuffer toAppendTo,
         FieldPosition pos)
   {
      if (! (obj instanceof Collection))
      {
         throw new IllegalArgumentException("The object to format must be "
               + " a java.util.Collection, but is: " + obj);
      }
      pos.setBeginIndex(0);
      pos.setEndIndex(0);

      final Collection elements = (Collection) obj;
      final StringBuffer tempBuffer = new StringBuffer(mListBegin);
      boolean first = true;
      for (final Iterator iter = elements.iterator(); iter.hasNext(); )
      {
         if (first)
         {
            first = false;
         }
         else
         {
            tempBuffer.append(mSeparator);
         }
         mListElementFormat.format(iter.next(), tempBuffer, pos);
      }
      tempBuffer.append(mListEnd);
      toAppendTo.append(tempBuffer);

      return toAppendTo;
   }

   /**
    * Sets the buffers position to the current parse position and checks whether
    * the buffer contains an begin-of-list at the current position and if so
    * updates its position and the index of the supplied parse position.
    *
    * @param buffer The buffer storing the line to parse.
    * @param pos The current parse postion.
    *
    * @return true if at end of list or no end of list specifier set;
    * false, else.
    */
   private boolean beginOfList (
         final CharBuffer buffer,
         final ParsePosition pos)
   {
      boolean rc = false;
      buffer.position(pos.getIndex());
      if (mLengthListBegin == 0)
      {
         rc = true;
      }
      else
      {
         if (buffer.remaining() >= mLengthListBegin)
         {
            boolean allOk = true;
            for (int i = 0; i < mLengthListBegin && allOk; ++i)
            {
               if (buffer.charAt(i) != mListBegin.charAt(i))
               {
                  allOk = false;
               }
            }
            rc = allOk;
            if (rc)
            {
               pos.setIndex(pos.getIndex() + mLengthListBegin);
               buffer.position(pos.getIndex());
            }
         }
      }
      return rc;
   }

   /**
    * Sets the buffers position to the current parse position and checks whether
    * the buffer contains an end-of-list at the current position and if so
    * updates its position and the index of the supplied parse position.
    *
    * @param buffer The buffer storing the line to parse.
    * @param pos The current parse postion.
    *
    * @return true if at end of list or no end of list specifier set;
    * false, else.
    */
   private boolean endOfList (
         final CharBuffer buffer,
         final ParsePosition pos)
   {
      boolean rc = false;
      buffer.position(pos.getIndex());
      if (mLengthListEnd == 0)
      {
         rc = true;
      }
      else
      {
         if (buffer.remaining() >= mLengthListEnd)
         {
            boolean allOk = true;
            for (int i = 0; i < mLengthListEnd && allOk; ++i)
            {
               if (buffer.charAt(i) != mListEnd.charAt(i))
               {
                  allOk = false;
               }
            }
            rc = allOk;
            if (rc)
            {
               pos.setIndex(pos.getIndex() + mLengthListEnd);
               buffer.position(pos.getIndex());
            }
         }
      }
      return rc;
   }

   /**
    * Sets the buffers position to the current parse position and checks whether
    * the buffer contains a separator at the current position and if so updates
    * its position and the index of the supplied parse position.
    *
    * @param buffer The buffer storing the line to parse.
    * @param pos The current parse postion.
    *
    * @return true if a separator exists at the current position; false, else.
    */
   private boolean separator (final CharBuffer buffer, final ParsePosition pos)
   {
      boolean rc = false;
      boolean allOk = true;

      buffer.position(pos.getIndex());

      if (buffer.remaining() >= mLengthSeparator)
      {
         for (int i = 0; i < mLengthSeparator && allOk; ++i)
         {
            if (buffer.charAt(i) != mSeparator.charAt(i))
            {
               allOk = false;
            }
         }
         rc = allOk;
         if (rc)
         {
            pos.setIndex(pos.getIndex() + mLengthSeparator);
            buffer.position(pos.getIndex());
         }
      }
      return rc;
   }
}
