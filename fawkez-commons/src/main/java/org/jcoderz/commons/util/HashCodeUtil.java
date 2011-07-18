/*
 * $Id: HashCodeUtil.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.lang.reflect.Array;


/**
 * Collected methods which allow easy implementation of
 * <code>hashCode</code>. Example use case:
 *
 * <pre>
 * public int hashCode ()
 * {
 *    int result = HashCodeUtil.SEED;
 *    //collect the contributions of various fields
 *    result = HashCodeUtil.hash(result, mPrimitive);
 *    result = HashCodeUtil.hash(result, mObject);
 *    result = HashCodeUtil.hash(result, mArray);
 *    return result;
 * }
 * </pre>
 *
 * @author Michael Griffel
 */
public final class HashCodeUtil
{
   /**
    * An initial value for a <code>hashCode</code>, to which is added
    * contributions from fields. Using a non-zero value decreases
    * collisons of <code>hashCode</code> values.
    */
   public static final int SEED = 23;

   /**
    * An factor that is used in the first term to multiply the <i>old</i>
    * hash code value.
    */
   private static final int ODD_PRIME_NUMBER = 37;

   /**
    * No instances allowed.
    */
   private HashCodeUtil ()
   {
      // Utility class  -- provides only static helper methods.
   }

   /**
    * Constructs a new seed using the given Boolean and the previous seed.
    * @param aSeed the previous seed value.
    * @param aBoolean the Boolean that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, boolean aBoolean)
   {
      return firstTerm(aSeed) + (aBoolean ? 1 : 0);
   }

   /**
    * Constructs a new seed using the given boolean and the previous seed.
    * @param aSeed the previous seed value.
    * @param aChar the character that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, char aChar)
   {
      return firstTerm(aSeed) + aChar;
   }

   /**
    * Constructs a new seed using the given integer and the previous seed.
    * Note that byte and short are handled by this method,
    * through implicit conversion.
    * @param aSeed the previous seed value.
    * @param aInt the integer that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, int aInt)
   {
      return firstTerm(aSeed) + aInt;
   }

   /**
    * Constructs a new seed using the given long and the previous seed.
    * @param aSeed the previous seed value.
    * @param aLong the long value that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, long aLong)
   {
      return firstTerm(aSeed) + (int) (aLong
            ^ (aLong >>> Constants.BITS_PER_INTEGER));
   }

   /**
    * Constructs a new seed using the given float and the previous seed.
    * @param aSeed the previous seed value.
    * @param aFloat the float that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, float aFloat)
   {
      return hash(aSeed, Float.floatToIntBits(aFloat));
   }

   /**
    * Constructs a new seed using the given double and the previous seed.
    * @param aSeed the previous seed value.
    * @param aDouble the double that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, double aDouble)
   {
      return hash(aSeed, Double.doubleToLongBits(aDouble));
   }

   /**
    * Constructs a new seed using the given object and the previous seed.
    *
    * <code>aObject</code> is a possibly-null object field, and possibly
    * an array. If <code>aObject</code> is an array, then each element
    * may be a primitive or a possibly-null object.
    *
    * @param aSeed the previous seed value.
    * @param aObject the integer that should be added to the new seed.
    * @return the new seed.
    */
   public static int hash (int aSeed, Object aObject)
   {
      int result = aSeed;

      if (aObject == null)
      {
         result = hash(result, 0);
      }
      else if (!isArray(aObject))
      {
         result = hash(result, aObject.hashCode());
      }
      else
      {
         final int length = Array.getLength(aObject);
         for (int i = 0; i < length; ++i)
         {
            final Object item = Array.get(aObject, i);
            //recursive call!
            result = hash(result, item);
         }
         result = hash(result, length);
      }
      return result;
   }

   /**
    * Calculates the first part (term) of the seed.
    * @param aSeed the old seed value.
    * @return the part of the new seed.
    */
   private static int firstTerm (int aSeed)
   {
      return ODD_PRIME_NUMBER * aSeed;
   }

   /**
    * Returns <code>true</code> if the given object represents an array.
    * @return <code>true</code> if the given object represents an array;
    *       <code>false</code> otherwise.
    */
   private static boolean isArray (Object aObject)
   {
      return aObject.getClass().isArray();
   }
}
