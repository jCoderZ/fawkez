/*
 * $Id: WhitespaceFormatTest.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.text.Format;
import java.text.ParseException;

import junit.framework.TestCase;

/**
 * This class tests the whitespace formatter.
 *
 */
public class WhitespaceFormatTest
      extends TestCase
{
   private static final char PRESERVED_CHAR = '\u0020';

   /**
    * Tests formatting of strings, which contains only '\u00A0' as white space.
    */
   public void testSimpleWhiteSpace ()
   {
      final String t1 = "This is a string.";
      final String t2 = " This is  a string   with long    whitespace.";
      final String r1 = WhitespaceFormat.format(t1);
      final String r2 = WhitespaceFormat.format(t2);
      final CharBuffer tb1 = CharBuffer.wrap(t1);
      final CharBuffer tb2 = CharBuffer.wrap(t2);
      final CharBuffer rb1 = WhitespaceFormat.format(tb1);
      final CharBuffer rb2 = WhitespaceFormat.format(tb2);
      compare(t1, r1);
      compare(t2, r2);
      compare(tb1, rb1);
      compare(tb2, rb2);
   }

   /**
    * Tests formatting of strings, with tabs as white space.
    */
   public void testTabWhiteSpace ()
   {
      final String t1 = "This is a string.";
      final String t2 = "\tThis is\t\ta\tstring \twith\t\t  tabs.";
      reformatAndCheck(t1, t2);
      parseAndCompare(String.valueOf(PRESERVED_CHAR), PRESERVED_CHAR + t2);
   }


   /**
    * Tests formatting of strings, with line feeds as white space.
    */
   public void testLfWhiteSpace ()
   {
      final String t1 = "This is a string.";
      final String t2 = "\n\nThis is\na string with\n  line feeds.";
      reformatAndCheck(t1, t2);
      parseAndCompare(
            String.valueOf(PRESERVED_CHAR) + String.valueOf(PRESERVED_CHAR),
            String.valueOf(PRESERVED_CHAR) + String.valueOf(PRESERVED_CHAR) + t2
            );
   }

   /**
    * Tests formatting of strings, with mixed whitespaces.
    */
   public void testMixedWhiteSpace ()
   {
      final String t1 = "This is a string.";
      final String t2
            = "This is\na\tstring\t \twith\n  mixed\n\n \twhite\nspace.";

      reformatAndCheck(t1, t2);
   }


   private void reformatAndCheck (final String t1, final String t2)
   {
       final String r1 = WhitespaceFormat.format(t1);
         final String r2 = WhitespaceFormat.format(t2);
         final CharBuffer tb1 = CharBuffer.wrap(t1);
         final CharBuffer tb2 = CharBuffer.wrap(t2);
         final CharBuffer rb1 = WhitespaceFormat.format(tb1);
         final CharBuffer rb2 = WhitespaceFormat.format(tb2);
         compare(t1, r1);
         parseAndCompare(t1, r1);
         compare(t2, r2);
         parseAndCompare(t2, r2);
         compare(tb1, rb1);
         compare(tb2, rb2);
   }

   private void compare (final String source, final String formatted)
   {
      assertTrue("Formatted string must not be longer than source string.",
            source.length() >= formatted.length());

      assertNotNull("The formatted string must not be null", formatted);

      int fIdx = 0;
      boolean firstWs = true;

      for (int sIdx = 0; sIdx < source.length(); ++sIdx)
      {
         final char sc = source.charAt(sIdx);
         final char fc = formatted.charAt(fIdx);

         if (Character.isWhitespace(sc) && (sc != PRESERVED_CHAR))
         {
            if (firstWs)
            {
               assertEquals("Formatted char must be whitespace",
                     String.valueOf(fc), String.valueOf(PRESERVED_CHAR));
               fIdx++;
               firstWs = false;
            }
         }
         else
         {
            assertEquals("Formatted char must match source char",
                  String.valueOf(fc), String.valueOf(sc));
            fIdx++;
            firstWs = true;
         }
      }
   }

   private void compare (final CharBuffer source, final CharBuffer formatted)
   {
      compare(source.toString(), formatted.toString());
   }

   private void parseAndCompare (final String source, final String formatted)
   {
      final Format format = new WhitespaceFormat();
      String parsed = null;
      try
      {
         parsed = (String) format.parseObject(formatted);
      }
      catch (ParseException ex)
      {
         fail("Got a parse exception: " + ex);
      }
      if (parsed != null)
      {
         compare(source, parsed);
      }
   }
}
