/*
 * $Id: ObjectUtil.java 1286 2009-03-07 20:06:15Z amandel $
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


/**
 * This class provides object related utility functions.
 *
 * @author Michael Griffel
 */
public final class ObjectUtil
{
   private ObjectUtil ()
   {
      // utility class - only static methods.
   }

   /**
    *  Returns <tt>true</tt> if the two specified objects are
    * <i>equal</i> to one another. Two objects <tt>a</tt>
    * and <tt>b</tt> are considered <i>equal</i> if <tt>(a==null ? b == null
    * : a.equals(b))</tt>. Also, two object references are considered
    * equal if both are <tt>null</tt>.
    *
    * This method allow easy implementation of the <code>equals</code> method.
    *
    * @param a one object to be tested for equality.
    * @param b the other object to be tested for equality.
    * @return <tt>true</tt> if the two objects are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (Object a, Object b)
   {
      return (a == null ? b == null : a.equals(b));
   }

   /**
    * Returns <tt>true</tt> if the two values are <i>equal</i> to one another.
    *
    * This method allow easy implementation of the <code>equals</code> method.
    *
    * @param a value to be tested for equality.
    * @param b the other value to be tested for equality.
    * @return <tt>true</tt> if the two value are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (long a, long b)
   {
      return a == b;
   }

   /**
    * Returns <tt>true</tt> if the two values are <i>equal</i> to one another.
    *
    * This method allow easy implementation of the <code>equals</code> method.
    *
    * @param a value to be tested for equality.
    * @param b the other value to be tested for equality.
    * @return <tt>true</tt> if the two value are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (int a, int b)
   {
      return a == b;
   }

   /**
    * Returns <tt>true</tt> if the two values are <i>equal</i> to one another.
    *
    * This method allow easy implementation of the <code>equals</code> method.
    *
    * @param a value to be tested for equality.
    * @param b the other value to be tested for equality.
    * @return <tt>true</tt> if the two value are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (char a, char b)
   {
      return a == b;
   }

   /**
    * Returns <tt>true</tt> if the two values are <i>equal</i> to one another.
    *
    * This method allow easy implementation of the <code>equals</code> method.
    *
    * @param a value to be tested for equality.
    * @param b the other value to be tested for equality.
    * @return <tt>true</tt> if the two value are equal; <tt>false</tt>
    *       otherwise.
    */
   public static boolean equals (boolean a, boolean b)
   {
      return a == b;
   }

   /**
    * Returns the string representation of the object or
    * <code>null</code> if the object is null.
    * @param obj the object to be converted or <code>null</code>.
    * @return the string representation of the object or
    * <code>null</code> if the object is null.
    */
   public static String toString (Object obj)
   {
      return obj == null ? null : obj.toString();
   }

   /**
    * Returns the string representation of the object or
    * an empty string if the object is null.
    * @param obj the object to be converted or <code>null</code>.
    * @return the string representation of the object or
    * an empty string if the object is null.
    */
   public static String toStringOrEmpty (Object obj)
   {
      return obj == null ? "" : obj.toString();
   }
}
