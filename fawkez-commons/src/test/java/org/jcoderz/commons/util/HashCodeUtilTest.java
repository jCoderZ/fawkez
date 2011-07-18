/*
 * $Id: HashCodeUtilTest.java 1011 2008-06-16 17:57:36Z amandel $
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
 * JUnit test for the class {@link org.jcoderz.commons.util.HashCodeUtil}.
 *
 * @author Michael Griffel
 */
public class HashCodeUtilTest
      extends TestCase
{
   private static final double DOUBLE_TEST_3 = -1.1d;
   private static final double DOUBLE_TEST_2 = 0.0d;
   private static final double DOUBLE_TEST_1 = 1.1d;
   private static final double FLOAT_TEST_3 = -1.1;
   private static final double FLOAT_TEST_2 = 0.0;
   private static final double FLOAT_TEST_1 = 1.1;
   /**
    * Test the method {@link HashCodeUtil#hash}.
    */
   public void testHashCode ()
   {
      final int hashCode = HashCodeUtil.SEED;

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, true));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, false));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, 1));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, 0));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, -1));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, 1L));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, 0L));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, -1L));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, 'c'));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, null));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, "foo"));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, ""));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, FLOAT_TEST_1));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, FLOAT_TEST_2));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, FLOAT_TEST_3));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, DOUBLE_TEST_1));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, DOUBLE_TEST_2));
      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, DOUBLE_TEST_3));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode, new Object[]{}));

      assertNotEquals(hashCode, HashCodeUtil.hash(hashCode,
            new Object[]{"foo", "bar"}));
   }

   /**
    * Asserts that the expected value is equals the current value.
    * @param expectedValue expected value
    * @param currentValue current value
    */
   private void assertNotEquals (int expectedValue, int currentValue)
   {
      assertFalse("hashcode should be changed, but is equals " + expectedValue,
            expectedValue == currentValue);

   }
}
