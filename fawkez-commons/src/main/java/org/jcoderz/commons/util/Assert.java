/*
 * $Id: Assert.java 1270 2009-01-25 15:49:03Z amandel $
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
import org.jcoderz.commons.AssertionFailedException;

/**
 * Utility class for assertions.
 *
 * @author Andreas Mandel
 */
public final class Assert
{
   /** Private constructor to avoid instantiation of utility class. */
   private Assert ()
   {
      // utility class -- no instances are allowed.
   }

   /**
    * Asserts that an object isn't null.
    * If it is a null reference an ArgumentMalformedException is
    * thrown with a appropriate message.
    *
    * @param parameter object to be tested against null.
    * @param argumentName name of the provided argument within the
    *        used interface.
    */
   public static void notNull (Object parameter, String argumentName)
   {
      if (parameter == null)
      {
         throw new ArgumentMalformedException(argumentName, null,
               "Argument " + argumentName + " must not be null");
      }
   }

   /**
    * Asserts that two integers are equal. If they are not
    * an AssertionFailedException is thrown with the given message.
    * @param message The message for the condition. This message is
    *       used in the exception if the integers are not equal.
    * @param expected the expected integer.
    * @param actual the actual integer.
    * @throws AssertionFailedException if the two objects are not equal.
    */
   public static void assertEquals (String message, int expected, int actual)
         throws AssertionFailedException
   {
      assertEquals(message, new Integer(expected), new Integer(actual));
   }

   /**
    * Asserts that two objects are equal. If they are not
    * an AssertionFailedException is thrown with the given message.
    * @param message The message for the condition. This message is
    *       used in the exception if the objects are not equal.
    * @param expected the expected object.
    * @param actual the actual object.
    * @throws AssertionFailedException if the two objects are not equal.
    */
   public static void assertEquals (String message, Object expected,
         Object actual)
         throws AssertionFailedException
   {
      if (!ObjectUtil.equals(expected, actual))
      {
         String newMessage = "";
         if (message != null)
         {
            newMessage = message + " ";
         }
         throw new AssertionFailedException(newMessage + "expected:<"
               + expected + "> but was:<" + actual + ">");
      }
   }

   /**
    * Asserts that a condition is <tt>true</tt>. If it isn't it throws
    * an AssertionFailedException with the given message.
    *
    * @param message The message for the condition. This message is
    *       used in the exception if the condition is <tt>false</tt>.
    * @param condition the condition to test.
    * @throws AssertionFailedException if the condition is <tt>false</tt>.
    */
   public static void assertTrue (String message, boolean condition)
         throws AssertionFailedException
   {
      if (!condition)
      {
         throw new AssertionFailedException(message);
      }
   }

   /**
    * Can be called if an assertion already failed. This can be used at
    * code positions that should never be reached at all. It throws
    * an AssertionFailedException with the given message.
    *
    * @param message The message to be used in the exception.
    * @throws AssertionFailedException always.
    */
   public static void fail (String message)
         throws AssertionFailedException
   {
      throw new AssertionFailedException(message);
   }

   /**
    * Can be called if an exception is unexpectedly caught. This can
    * be used at catch blocks that should never be reached at all.
    * It throws an AssertionFailedException with the given nested
    * exception and message.
    *
    * @param message The message to be used in the exception.
    * @param ex the exception that was not expected
    * @throws AssertionFailedException always.
    */
   public static void fail (String message, Throwable ex)
       throws AssertionFailedException
   {
       throw new AssertionFailedException(message, ex);
   }
}
