/*
 * $Id: AssertTest.java 1095 2008-07-24 09:29:24Z amandel $
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

import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.AssertionFailedException;

/**
 * Class to test the Assert utility class.
 *
 * @author Andreas Mandel
 */
public class AssertTest
      extends TestCase
{
   private static final String BAR_STRING = "bar";
   private static final String FOO_STRING = "foo";
   private static final String PARAMETER_NAME = "parameter-name";

   /** Test the not null method (positive). */
   public final void testNotNullPositive ()
   {
      Assert.notNull("not-null-parameter", PARAMETER_NAME);
   }

   /** Test the not null method (negative). */
   public final void testNotNullNegative ()
   {
      try
      {
         Assert.notNull(null, PARAMETER_NAME);
         fail("Assertion should not pass.");
      }
      catch (ArgumentMalformedException ex)
      {
         assertEquals("Parameter name should be as given.", PARAMETER_NAME,
               ex.getParameter(
                  "ARGUMENT_NAME").get(0));
         assertNull("Value must be null.",
               ex.getParameter(
                  "ARGUMENT_VALUE").get(
                     0));
      }
   }

   /** Test the not fail method. */
   public final void testFail ()
   {
      try
      {
         Assert.fail(BAR_STRING);
         fail("Assertion fail should not pass.");
      }
      catch (AssertionFailedException ex)
      {
         assertEquals("Parameter message should be as given.",
             BAR_STRING,
               ex.getParameter(
            		   "MESSAGE").get(0));
      }
   }

   /**
    * Tests the method {@link Assert#assertTrue(String, boolean)}.
    */
   public void testAssertTrue ()
   {
      Assert.assertTrue("true condition", true);
      Assert.assertTrue(null, true);
      assertTrueNegativeTest("false condition");
      assertTrueNegativeTest(null);
   }

   /**
    * Tests the method {@link Assert#assertEquals(String, Object, Object)}.
    */
   public void testAssertEqualsObjects ()
   {
      Assert.assertEquals("equal (null, null)", null, null);
      Assert.assertEquals("equal (foo, foo)", FOO_STRING, FOO_STRING);
      assertEqualsObjectsNegativeTest(FOO_STRING, BAR_STRING);
      assertEqualsObjectsNegativeTest(FOO_STRING, null);
      assertEqualsObjectsNegativeTest(null, BAR_STRING);
      assertEqualsObjectsNegativeTest(new Date(), Calendar.getInstance());
   }

   /**
    * Tests the method {@link Assert#assertEquals(String, int, int)}.
    */
   public void testAssertEqualsInts ()
   {
      Assert.assertEquals("equal", null, null);
      Assert.assertEquals("equal", 1, 1);
   }

   private void assertEqualsObjectsNegativeTest (Object expected, Object actual)
   {
      try
      {
         Assert.assertEquals("not equal", expected, actual);
         fail("Expected AssertionFailedException for objects "
               + "that are not equal");
      }
      catch (AssertionFailedException x)
      {
         // expected
      }
   }

   private void assertTrueNegativeTest (String message)
   {
      try
      {
         Assert.assertTrue(message, false);
         fail("Expected AssertionFailedException for a condition "
               + "that is false");
      }
      catch (AssertionFailedException x)
      {
         // expected
      }
   }
}
