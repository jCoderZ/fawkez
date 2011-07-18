/*
 * $Id: ObjectUtilTest.java 1286 2009-03-07 20:06:15Z amandel $
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
 * JUnit test for {@link org.jcoderz.commons.util.ObjectUtil}.
 *
 * @author Michael Griffel
 */
public class ObjectUtilTest
      extends TestCase
{
   /**
    * Test the method {@link ObjectUtil#equals(Object, Object)}.
    */
   public void testEquals ()
   {
      final Object a = "Foo";
      final Object b = "Bar";
      assertTrue("Same instance should be equals", ObjectUtil.equals(a, a));
      assertFalse("Different instances should not be equal",
            ObjectUtil.equals(a, b));
      assertTrue("two null references should be equal",
            ObjectUtil.equals(null, null));
      assertFalse("Should not be equals, if one reference is null",
            ObjectUtil.equals(a, null));
      assertFalse("Should not be equals, if one reference is null",
            ObjectUtil.equals(null, a));
   }

   /** Tests the {@link ObjectUtil#toString(Object)} method. */
   public void testToString ()
   {
      assertNull("ObjectUtil.toString(null) should return null.",
            ObjectUtil.toString(null));
      assertEquals("ObjectUtil.toString(\"foo\") should return \"foo\".",
            "foo", ObjectUtil.toString("foo"));
   }

   /** Tests the {@link ObjectUtil#toStringOrEmpty(Object)} method. */
   public void testToStringOrEmpty ()
   {
      assertEquals("ObjectUtil.toStringOrEmpty(null) should return \"\".",
            "", ObjectUtil.toStringOrEmpty(null));
      assertEquals("ObjectUtil.toStringOrEmpty(\"foo\") should return \"foo\".",
            "foo", ObjectUtil.toStringOrEmpty("foo"));
   }
}
