/*
 * $Id: HashCode.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.Serializable;

/**
 * The class can be used to easily implement {@link Object#hashCode()}.
 *
 * @author Michael Griffel
 *
 */
public final class HashCode
      implements Serializable
{
   private static final long serialVersionUID = 1688234599683404302L;

   private int mHashCode = HashCodeUtil.SEED;

   /**
    * Updates the internal hash code with the given Boolean.
    * @param aBoolean the Boolean to add to the hash code.
    */
   public void hash (boolean aBoolean)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aBoolean);
   }

   /**
    * Updates the internal hash code with the given character.
    * @param aChar the character to add to the hash code.
    */
   public void hash (char aChar)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aChar);
   }

   /**
    * Updates the internal hash code with the given double.
    * @param aDouble the double to add to the hash code.
    */
   public void hash (double aDouble)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aDouble);
   }

   /**
    * Updates the internal hash code with the given float.
    * @param aFloat the float to add to the hash code.
    */
   public void hash (float aFloat)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aFloat);
   }

   /**
    * Updates the internal hash code with the given integer.
    * @param aInt the integer to add to the hash code.
    */
   public void hash (int aInt)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aInt);
   }

   /**
    * Updates the internal hash code with the given long.
    * @param aLong the long to add to the hash code.
    */
   public void hash (long aLong)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aLong);
   }

   /**
    * Updates the internal hash code with the given object.
    * <code>aObject</code> is a possibly-null object field, and possibly
    * an array. If <code>aObject</code> is an array, then each element
    * may be a primitive or a possibly-null object.
    *
    * @param aObject the object to add to the hash code.
    */
   public void hash (Object aObject)
   {
      mHashCode = HashCodeUtil.hash(mHashCode, aObject);
   }

   /**
    * Returns the current snapshot of the hash code.
    * @return the current snapshot of the hash code.
    */
   public int toInt ()
   {
      return mHashCode;
   }
}
