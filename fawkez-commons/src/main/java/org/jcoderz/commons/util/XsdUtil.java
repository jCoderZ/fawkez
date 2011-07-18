/*
 * $Id: XsdUtil.java 1392 2009-04-04 13:16:54Z amandel $
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

import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.jcoderz.commons.types.Date;

/**
 * This class encapsulates util methods used for schema data type handling.
 *
 * @author Andreas Mandel
 */
public final class XsdUtil
{
   /** No instances allowed. */
   private XsdUtil ()
   {
      // utility class -- only static methods.
   }

   /**
    * Parses the given string into a BigInteger.
    * This method uses {@link BigInteger#BigInteger(java.lang.String)} to
    * parse the string, but allows a optional leading '+' for positive
    * values.
    * @param str the string to be parsed.
    * @return a BigInteger representing the same value as the string.
    * @throws NumberFormatException if the string can not be parsed.
    * @throws NullPointerException if the string is null.
    */
   public static BigInteger integerFromString (String str)
         throws NumberFormatException, NullPointerException
   {
      if (str.length() == 0)
      {
         throw new NumberFormatException();
      }
      final char startChar = str.charAt(0);
      // this is different in JDK1.4.2 vs. 1.5.0!
      // we need to do it consistent, as defined with the schema spec.
      if ((startChar == '+' || startChar == '-')
            && str.length() > 1
            && (str.charAt(1) < '0' || str.charAt(1) > '9'))
      {
         throw new NumberFormatException();
      }
      final String argument;
      if (startChar == '+')
      {
         argument = str.substring(1);
      }
      else
      {
         argument = str;
      }

      return new BigInteger(argument);
   }

   /**
    * Parses the given string into a int.
    * This method uses {@link Integer#parseInt(java.lang.String)} to
    * parse the string, but allows a optional leading '+' for positive
    * values.
    * @param str the string to be parsed.
    * @return a int representing the same value as the string.
    * @throws NumberFormatException if the string can not be parsed.
    * @throws NullPointerException if the string is null.
    */
   public static int intFromString (String str)
         throws NumberFormatException, NullPointerException
   {
      final String argument;
      if (str.startsWith("+") && str.length() > 1
            && str.charAt(1) != '-' && str.charAt(1) != '+')
      {
         argument = str.substring(1);
      }
      else
      {
         argument = str;
      }
      return Integer.parseInt(argument);
   }

   /**
    * Parses the given string into a long.
    * This method uses {@link Long#parseLong(java.lang.String)} to
    * parse the string, but allows a optional leading '+' for positive
    * values.
    * @param str the string to be parsed.
    * @return a long representing the same value as the string.
    * @throws NumberFormatException if the string can not be parsed.
    * @throws NullPointerException if the string is null.
    */
   public static long longFromString (String str)
         throws NumberFormatException, NullPointerException
   {
      final String argument;
      if (str.startsWith("+") && str.length() > 1
            && str.charAt(1) != '-' && str.charAt(1) != '+')
      {
         argument = str.substring(1);
      }
      else
      {
         argument = str;
      }
      return Long.parseLong(argument);
   }

   /**
    * Checks if the given string complies to the XML schema token restrictions.
    * <p>
    * <b>XML Schema Definition:</b> token represents tokenized strings.
    * The <i>value space</i> of token is the set of strings that do not
    * contain the carriage return (#xD), line feed (#xA) nor tab (#x9)
    * characters, that have no leading or trailing spaces (#x20) and
    * that have no internal sequences of two or more spaces.
    * For more information about the XML Schema datatype definition of
    * a <code>token</code> see
    * <a href="http://www.w3.org/TR/xmlschema-2/datatypes.html#token">
    * XML Schema datatype: token</a>
    * @param token the string to be checked
    * @return <code>true</code> if the given string complies to the
    *       XML schema token restrictions; <code>false</code> otherwise.
    */
   public static boolean isValidToken (String token)
   {
      boolean result = true;
      if (token == null)
      {
         result = false;
      }
      else
      {
         char lastChar = Constants.SPACE_CHAR;
         for (int i = 0; i < token.length() && result; ++i)
         {
            final char currentChar = token.charAt(i);
            result = isValidTokenCharacter(lastChar, currentChar);
            lastChar = currentChar;
         }
         // trailing spaces?
         if (lastChar == Constants.SPACE_CHAR && token.length() != 0)
         {
            result = false;
         }
      }
      return result;
   }

   /**
    * Checks if the given character complies to the XML schema token
    * restrictions.
    * The character is <b>not</b> valid XML schema token character if it
    * is a carriage return (#xD), line feed (#xA) or tab (#x9)
    * characters. This method also checks that there are no leading
    * spaces (#x20) and that there are no internal sequences of
    * two spaces.
    * @return <code>true</code> if the given character complies to the
    *       XML schema token restrictions; <code>false</code> otherwise.
    * @param lastChar the character before the current character.
    * @param currentChar the current character in the character sequence.
    */
   private static boolean isValidTokenCharacter (
         final char lastChar, final char currentChar)
   {
      boolean result = true;
      switch (currentChar)
      {
         case Constants.CARRIAGE_RETURN_CHAR:
            /* falls through */
         case Constants.LINE_FEED_CHAR:
            /* falls through */
         case Constants.TAB_CHAR:
            result = false;
            break;
         case Constants.SPACE_CHAR:
            // leading spaces or sequence of two or more spaces?
            if (lastChar == Constants.SPACE_CHAR)
            {
               result = false;
            }
            break;
         default:
            /* valid character */
            break;
      }
      return result;
   }

   /**
    * Parses the given String as schema date time representation and returns
    * a Date object holding the given time.
    * This method must only be used after a JAXBContext has been initialized,
    * otherwise it is likely that a NullpointerException is thrown.
    * @param date the date time in schema dateTime
    * @return a newly generated Date object representing the time given in the
    *         date.
    */
   public static Date fromDateTimeString (String date)
   {
      final Calendar cal = DatatypeConverter.parseDateTime(date);
      cal.setLenient(false);
      return new Date(cal.getTimeInMillis());
   }

   /**
    * Parses the given String as schema date representation and returns
    * a Date object holding the given time.
    * This method must only be used after a JAXBContext has been initialized,
    * otherwise it is likely that a NullpointerException is thrown.
    * @param date the date in schema date representation
    * @return a newly generated Date object representing the time given in the
    *         date.
    */
   public static Date fromDateString (String date)
   {
      final Calendar cal = DatatypeConverter.parseDate(date);
      cal.setLenient(false);
      return new Date(cal.getTimeInMillis());
   }
   
}
