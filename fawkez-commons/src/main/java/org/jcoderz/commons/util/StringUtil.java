/*
 * $Id: StringUtil.java 1587 2009-12-15 15:11:38Z amandel $
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
package org.jcoderz.commons.util;

import org.jcoderz.commons.ArgumentMalformedException;


/**
 * This class provides string related utility functions.
 *
 * @author Michael Griffel
 */
public final class StringUtil
{
   /**
    * The empty string <code>&quot;&quot;</code>.
    */
   public static final String EMPTY_STRING = "";

   private static final int ASCII_MAX_VALUE = 127;

   /**
    * No instance of this class allowed.
    */
   private StringUtil ()
   {
      // only static utility methods.
   }

   /**
    * Converts a byte array to a String using UTF-8 encoding.
    * If the given byte array is <code>null</code>, the returned string
    * is also <code>null</code>.
    *
    * @param bytes The byte array to be converted.
    * @return The string representation of the byte array or
    *       <code>null</code> if the given byte array is <code>null</code>.
    */
   public static String toString (byte[] bytes)
   {
      return bytes == null ? null : toString(bytes, 0, bytes.length);
   }

   /**
    * Converts a byte array to a String using UTF-8 encoding.
    * If the given byte array is <code>null</code>, the returned string
    * is also <code>null</code>.
    *
    * @param bytes The byte array to be converted.
    * @param offset The index of the first byte to encode.
    * @param length The number of bytes to encode.
    * @return The string representation of the byte array or
    *       <code>null</code> if the given byte array is <code>null</code>.
    * @throws RuntimeException if the UTF-8 encoding is not supported by the
    *       JDK.
    */
   public static String toString (byte[] bytes, int offset, int length)
       throws RuntimeException
   {
      final String result;
      try
      {
         if (bytes == null)
         {
            result = null;
         }
         else
         {
            result = new String(bytes, offset, length, Constants.ENCODING_UTF8);
         }
      }
      catch (java.io.UnsupportedEncodingException e)
      {
         // this should not occur because the UTF-8 encoder is always
         // supported by the JDK
         throw new RuntimeException(
               "UTF-8 character encoding not supported?", e);
      }
      return result;
   }

   /**
    * Converts a String to an byte array using UTF-8 encoding.
    *
    * @param s The string to be converted.
    * @return The bytes of the given string in UTF-8 encoding.
    * @throws RuntimeException if the UTF-8 encoding is not supported by the
    *       JDK.
    */
   public static byte[] toBytes (String s)
       throws RuntimeException
   {
      try
      {
         return s.getBytes(Constants.ENCODING_UTF8);
      }
      catch (java.io.UnsupportedEncodingException e)
      {
         // this should not occur because the UTF-8 encoder is always
         // supported by the JDK
         throw new RuntimeException(
               "UTF-8 character encoding not supported?", e);
      }
   }

   /**
    * Converts a byte array to a String using ASCII encoding.
    * If the given byte array is <code>null</code>, the returned string
    * is also <code>null</code>.
    *
    * @param bytes The byte array to be converted.
    * @param offset The index of the first byte to encode.
    * @param length The number of bytes to encode.
    * @return The String representation of the byte array.
    */
   public static String asciiToString (byte[] bytes, int offset, int length)
   {
      final String result;

      try
      {
         if (bytes != null)
         {
            result = new String(
                  bytes, offset, length, Constants.ENCODING_ASCII);
         }
         else
         {
            result = null;
         }
      }
      catch (java.io.UnsupportedEncodingException e)
      {
         // this should not occur because the ASCII encoder is always
         // supported by the JDK
         throw new RuntimeException(
               "ASCII character encoding not supported?", e);
      }
      return result;
   }

   /**
    * Converts a byte array to a String using ASCII encoding.
    * If the given byte array is <code>null</code>, the returned string
    * is also <code>null</code>.
    *
    * @param bytes The byte array to be converted.
    * @return The String representation of the byte array.
    */
   public static String asciiToString (byte[] bytes)
   {
      return bytes == null ? null : asciiToString(bytes, 0, bytes.length);
   }

   /**
    * Tests if the given character is an ASCII character, i.e. if it's
    * integer value is less than or equal to 127.
    *
    * @param c the character to test.
    * @return <code>true</code> if c &lt;= 127, <code>false</code> otherwise.
    */
   public static boolean isAscii (final char c)
   {
      return (c <= ASCII_MAX_VALUE);
   }

   /**
    * Determines if the specified string consists only of ASCII characters.
    *
    * @param c the characters to check.
    * @return <code>true</code> if the specified characters are 7-bit
    *         ASCII clean; <code>false</code> otherwise.
    */
   public static boolean isAscii (CharSequence c)
   {
      boolean result = true;
      for (int i = c.length() - 1; i >= 0; --i)
      {
         if (!isAscii(c.charAt(i)))
         {
            result = false;
            break;
         }
      }
      return result;
   }

   /**
    * Returns <code>true</code> if given string is <code>null</code> or the
    * length is zero (empty string).
    * @param s the string to test.
    * @return Returns <code>true</code> if given string is <code>null</code>
    *       or the length is zero (empty string); <code>false</code> otherwise.
    */
   public static boolean isNullOrEmpty (String s)
   {
      return (s == null || s.length() == 0);
   }

   /**
    * Returns <code>true</code> if given string is <code>null</code> or the
    * length is zero (empty string).
    * @param s the string to test.
    * @return Returns <code>true</code> if given string is <code>null</code>
    *       or the length is zero (empty string); <code>false</code> otherwise.
    */
   public static boolean isEmptyOrNull (String s)
   {
      return isNullOrEmpty(s);
   }

   /**
    * Returns <code>true</code> if given string is <code>null</code>, the
    * length is zero (empty string) or if it only contains white spaces.
    * The whitespace check is done using Character.isWhitespace().
    * @param s the string to test.
    * @return Returns <code>true</code> if given string is <code>null</code>,
    *       the length is zero (empty string) or the String contains only
    *       whitespace characters; <code>false</code> otherwise.
    */
   public static boolean isNullOrBlank (String s)
   {
       boolean result = true;
       if (s != null)
       {
           final int length = s.length();
           for (int i = 0; i < length; i++)
           {
               if (!Character.isWhitespace(s.charAt(i)))
               {
                   result = false;
                   break;
               }
           }
      }
      return result;
   }

   /**
    * Returns <code>true</code> if given string is <code>null</code>, the
    * length is zero (empty string) or if it only contains white spaces.
    * The whitespace check is done using Character.isWhitespace().
    * @param s the string to test.
    * @return Returns <code>true</code> if given string is <code>null</code>,
    *       the length is zero (empty string) or the String contains only
    *       whitespace characters; <code>false</code> otherwise.
    */
   public static boolean isBlankOrNull (String s)
   {
      return isNullOrBlank(s);
   }

   /**
    * Returns <tt>true</tt> if the two specified strings are
    * <i>equal</i> to one another. Two strings <tt>a</tt>
    * and <tt>b</tt> are considered <i>equal</i> if <tt>(a==null ? b == null
    * : a.equals(b))</tt>. Also, two string references are considered
    * equal if both are <tt>null</tt>.
    *
    * @param a one string to be tested for equality.
    * @param b the other string to be tested for equality.
    * @return <tt>true</tt> if the two strings are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (String a, String b)
   {
      final boolean result;
      if (a == b)
      {
         result = true;
      }
      else if (a == null || b == null)
      {
         result = false;
      }
      else
      {
         result = a.equals(b);
      }
      return result;
   }

   /**
    * Pads or truncates the given argument to be at least minLength chars
    * and at most maxLength chars long.
    * @param s the string to pad or truncate
    * @param minLength the minimum length of the string
    * @param maxLength the maximum length of the string
    * @return a string padded with spaces if its length is less than
    *       minLength, or truncated to be no longer than maxLength,
    *       or the string itself if its length is between minLength
    *       and maxLength
    * @throws ArgumentMalformedException if the string argument is null,
    *       or if minLength is greater than maxLength
    */
   public static String fitToLength (String s, int minLength, int maxLength)
         throws ArgumentMalformedException
   {
      Assert.notNull(s, "s");
      if (minLength > maxLength)
      {
         throw new ArgumentMalformedException(
               "minLength", String.valueOf(minLength),
               "minLength must be less than or equal to maxLength");
      }
      final StringBuffer sbuf = new StringBuffer(s);
      while (sbuf.length() < minLength)
      {
         sbuf.append(' ');
      }
      if (sbuf.length() > maxLength)
      {
         sbuf.setLength(maxLength);
      }
      return sbuf.toString();
   }

   /**
    * Adds the character <tt>pad</tt> to the left-side of the string
    * <tt>s</tt> until the string size will be <tt>size</tt>.
    * @param s the string to pad.
    * @param pad the padding character.
    * @param size the final string size.
    * @return the padded string.
    */
   public static String padLeft (String s, char pad, int size)
   {
      final StringBuffer sb = new StringBuffer(s);
      while (sb.length() < size)
      {
         sb.insert(0, pad);
      }
      return sb.toString();
   }

   /**
    * Trims the length of the given string to the given maxlength, if
    * the string length is below the given maxlength the string
    * returned unmodified.
    * @param str the string to trim.
    * @param maxLength the maximum length
    * @return the string, trimmed to a maximum length of maxLength
    */
   public static String trimLength (String str, int maxLength)
   {
      Assert.assertTrue("maxLength must not be negative.", maxLength >= 0);
      final String result;
      if (str != null && str.length() > maxLength)
      {
         result = str.substring(0, maxLength);
      }
      else
      {
         result = str;
      }
      return result;
   }

   /**
    * Trims the length of the given string to the given maxlength by cutting of
    * data at the left side (beginning) of the string, if
    * the string length is below the given maxlength the string is returned
    * unmodified.
    * @param str the string to trim.
    * @param maxLength the maximum length
    * @return the string, trimmed to a maximum length of maxLength
    */
   public static String trimLengthLeft (String str, int maxLength)
   {
      Assert.assertTrue("maxLength must not be negative.", maxLength >= 0);
      final String result;
      if (str != null && str.length() > maxLength)
      {
         result = str.substring(str.length() - maxLength);
      }
      else
      {
         result = str;
      }
      return result;
   }

   /**
    * Returns true if the first argument string
    * contains the second argument string, and otherwise returns false.
    * This is no regular expression matching!
    *
    * @param str the string to test.
    * @param subString the substring to look for in <code>str</code>.
    *
    * @return true if the first argument string
    *   contains the second argument string, and otherwise returns false.
    */
   public static boolean contains (String str, String subString)
   {
       Assert.notNull(str, "str");
       Assert.notNull(subString, "subString");
       return str.indexOf(subString) >= 0;
   }
}
