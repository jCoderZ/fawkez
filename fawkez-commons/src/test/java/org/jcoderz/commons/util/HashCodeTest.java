/*
 * $Id: HashCodeTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.Collections;
import junit.framework.TestCase;

/**
 * JUnit tests for class {@link org.jcoderz.commons.util.HashCode}.
 *
 * @author Michael Griffel
 */
public class HashCodeTest
      extends TestCase
{
   /**
    * Tests the method {@link org.jcoderz.commons.util.HashCode#toInt()}.
    */
   public void testToInt ()
   {
      final HashCode hashCode = new HashCode();
      hashCode.hash(Constants.BITS_PER_INTEGER);
      hashCode.hash("string");
      assertEquals("toInt should not modify the hashCode",
            hashCode.toInt(), hashCode.toInt());
   }

   /**
    * Tests the method {@link org.jcoderz.commons.util.HashCode#toInt()}.
    */
   public void testToIntInitialValue ()
   {
      final HashCode hashCode = new HashCode();
      assertEquals("toInt should be initialized to an initial prime number",
            HashCodeUtil.SEED, hashCode.toInt());

   }

   /**
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(boolean)}.
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(char)}.
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(double)}.
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(float)}.
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(int)}.
    * Tests the method {@link org.jcoderz.commons.util.HashCode#hash(Object)}.
    */
   public void testHash ()
   {
      final HashCode hashCode = new HashCode();
      hashCode.hash(true);
      hashCode.hash('?');
      hashCode.hash(Math.PI);
      hashCode.hash(Float.POSITIVE_INFINITY);
      hashCode.hash(0);
      hashCode.hash(Byte.MAX_VALUE);
      hashCode.hash(Character.MAX_VALUE);
      hashCode.hash("foo");
      hashCode.hash(null);
      hashCode.hash(0L);
      hashCode.hash(Collections.EMPTY_LIST.toArray());
      hashCode.hash(Collections.singletonList("foo").toArray());
   }

}
