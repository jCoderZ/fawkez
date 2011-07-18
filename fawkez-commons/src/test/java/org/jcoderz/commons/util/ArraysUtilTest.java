/*
 * $Id: ArraysUtilTest.java 1561 2009-10-12 05:28:25Z amandel $
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


import junit.framework.TestCase;


/**
 * JUnit tests for class {@link org.jcoderz.commons.util.ArraysUtil}.
 *
 * @author Michael Griffel.
 */
public class ArraysUtilTest
      extends TestCase
{
   private static final String NULL_STRING = "null";
   private static final String EMPTY_ARRAY_STRING = "[]";

   /**
    * Tests the method {@link ArraysUtil#toString(Object[])}.
    */
   public final void testToStringObjectArray ()
   {
      final Object[] nullInput = null;
      final String nullInputResult = ArraysUtil.toString(nullInput);
      assertEquals("Expected a 'null' string for an array that is 'null'.",
            NULL_STRING, nullInputResult);

      final Object[] emptyArray = {};
      final String emptyArrayResult = ArraysUtil.toString(emptyArray);
      assertEquals("Expected a '" + EMPTY_ARRAY_STRING
            + "' string for an empty array.",
            EMPTY_ARRAY_STRING, emptyArrayResult);

      final Object[] inputArray = {null, "", "a string"};
      final String output = ArraysUtil.toString(inputArray);
      assertEquals("Unexpected string representation of array received.",
            "[null, , a string]", output);
   }

   /**
    * Tests the method {@link ArraysUtil#toString(Object)}.
    */
   public final void testToStringNativeArray ()
   {
      final int[] nullInput = null;
      final String nullInputResult = ArraysUtil.toString((Object) nullInput);
      assertEquals(
          "Expected a 'null' string for an native array that is 'null'.",
          NULL_STRING, nullInputResult);

      final int[] emptyArray = {};
      final String emptyArrayResult = ArraysUtil.toString(emptyArray);
      assertEquals("Expected a '" + EMPTY_ARRAY_STRING
            + "' string for an empty array.",
            EMPTY_ARRAY_STRING, emptyArrayResult);

      final int[] inputArray = {1, 0, -1};
      final String output = ArraysUtil.toString(inputArray);
      assertEquals("Unexpected string representation of array received.",
            "[1, 0, -1]", output);
   }

   /**
    * Tests the method {@link ArraysUtil#toString(Object)} with 
    * nested array.
    */
   public final void testToStringNested ()
   {
      final Object[] inputArray = {null, "test", new int[] {1, 0, -1}};
      final String output = ArraysUtil.toString((Object) inputArray);
      assertEquals(
          "Unexpected string representation of nested array received.",
          "[null, test, [1, 0, -1]]", output);
   }

   /**
    * Tests the method {@link ArraysUtil#toString(Object)} with 
    * nested array.
    */
   public final void testToStringNestedBoolean ()
   {
      final Object[] inputArray = {null, "test", new boolean[] {true, false}};
      final String output = ArraysUtil.toString(inputArray);
      assertEquals(
          "Unexpected string representation of nested array received.",
          "[null, test, [true, false]]", output);
   }

}
