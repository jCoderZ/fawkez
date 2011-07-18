/*
 * $Id: LuhnAlgorithm.java 1011 2008-06-16 17:57:36Z amandel $
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
 * The Luhn algorithm.
 *
 * The Luhn algorithm or Luhn formula, also known as the
 * <b>modulus 10</b> or <b>mod 10</b> algorithm, was developed
 * in the 1960s as a method of validating identification numbers.
 * It is a simple checksum formula used to validate a variety
 * of account numbers, such as credit card numbers and Canadian
 * Social Insurance Numbers.
 * Much of its notoriety comes from credit card companies' adoption
 * of it shortly after its creation in the late 1960s by
 * IBM scientist Hans Peter Luhn (1896-1964).
 *
 * The algorithm is in the public domain and is in wide use today.
 * It is not intended to be a cryptographically secure hash function;
 * it protects against random error, not malicious attack.
 * Most credit cards and many government identification numbers
 * use the algorithm as a simple method of distinguishing valid
 * numbers from collections of random digits.
 *
 * @author Michael Griffel
 */
public final class LuhnAlgorithm
{
   private static final int MAX_DIGIT = 9;
   private static final int MODULUS = 10;

   /**
    * Private Constructor.
    */
   private LuhnAlgorithm ()
   {
      // provides only static methods - no instances allowed.
   }

   /**
    * Execute the Luhn (Mod10) card check on a given card number.
    *
    * @param cardNumber the card number to check.
    * @return <tt>true</tt> if the Luhn check succeeds;
    *       <tt>false</tt> otherwise.
    */
   public static boolean check (String cardNumber)
   {
      final String s = reverseString(cardNumber);
      int crossSum = 0;
      for (int i = 0; i < s.length(); i++)
      {
         final int digit = s.charAt(i) - '0';

         if (isOdd(i))
         {
            crossSum += computeCrossSum(digit);
         }
         else
         {
            crossSum += digit;
         }
      }

      return (crossSum % MODULUS == 0);
   }

   /**
    * Computes the Luhn check digit for a given card number.
    *
    * @param cardNumberWithoutLastDigit the card number without
    *       the last digit.
    * @return the Luhn check digit for the given card number.
    */
   public static int computeLuhnCardNumber (String cardNumberWithoutLastDigit)
   {
      final String s = reverseString(cardNumberWithoutLastDigit);

      int crossSum = 0;
      for (int i = 0; i < s.length(); i++)
      {
         final int digit = s.charAt(i) - '0';
         if (isEven(i))
         {
            crossSum += computeCrossSum(digit);
         }
         else
         {
            crossSum += digit;
         }
      }
      crossSum %= MODULUS;

      return (MODULUS - crossSum) % MODULUS;
   }


   private static boolean isEven (int i)
   {
      return (i & 1) == 0;
   }

   private static boolean isOdd (int i)
   {
      return (i & 1) == 1;
   }

   private static int computeCrossSum (int number)
   {
      if (number > MAX_DIGIT)
      {
         throw new ArgumentMalformedException("number",
               String.valueOf(number), "Number must be between 0 and 9");
      }
      final int i = number << 1;
      return (i > MAX_DIGIT ? i - MAX_DIGIT  : i);
   }

   private static String reverseString (String s)
   {
      final char[] source = s.toCharArray();
      final char[] reversed = new char[source.length];
      for (int i = 0; i < reversed.length; i++)
      {
         reversed[i] = source[reversed.length - i - 1];
      }
      return String.valueOf(reversed);
   }
}
