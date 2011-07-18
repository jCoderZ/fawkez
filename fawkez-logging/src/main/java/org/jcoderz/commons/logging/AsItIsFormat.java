/*
 * $Id: AsItIsFormat.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.util.StringTokenizer;

/**
 * This Format class is used for formatting a String as it is, i.e. do no
 * formatting of the String. For enabling this as a parser for the format,
 * delimiters can be set when constructing an instance of this. But if this
 * functionality is actually desired, one has to be sure the delimiter is not
 * part of the string to format otherwise the result might not be as expected.
 *
 */
public final class AsItIsFormat
      extends Format
{
   private static final long serialVersionUID = 3905519414995792952L;

   private final String mDelimiters;

   /**
    * Constructor, saves the delimiters for parsing. When this is used for
    * parsing a String, the delimiters work as for a StringTokenizer.
    *
    * @param delimiters String containing delimiting chars, used when parsing.
    */
   public AsItIsFormat (final String delimiters)
   {
      if (delimiters == null || delimiters.length() == 0)
      {
         mDelimiters = null;
      }
      else
      {
         mDelimiters = delimiters;
      }
   }

   /** {@inheritDoc} */
   public Object parseObject (String source, ParsePosition pos)
   {
      String rc = null;

      final String string = source.substring(pos.getIndex());
      if (mDelimiters == null)
      {
         rc = string;
         pos.setIndex(source.length());
      }
      else
      {
         final StringTokenizer tokenizer = new StringTokenizer(
               string, mDelimiters);
         if (tokenizer.hasMoreTokens())
         {
            rc = tokenizer.nextToken();
            pos.setIndex(pos.getIndex() + rc.length());
         }
         else
         {
            pos.setErrorIndex(pos.getIndex());
         }
      }
      return rc;
   }

   /** {@inheritDoc} */
   public StringBuffer format (
         Object obj,
         StringBuffer toAppendTo,
         FieldPosition pos)
   {
      if (! (obj instanceof String))
      {
         throw new IllegalArgumentException("Supplied object to be formatted "
               + "must be a String but is " + obj.getClass().getName() + ": "
               + obj);
      }
      pos.setBeginIndex(0);
      pos.setEndIndex(0);
      toAppendTo.append(obj);
      return toAppendTo;
   }
}
