/*
 * $Id: EmailAddressTest.java 1354 2009-03-29 11:04:22Z amandel $
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
package org.jcoderz.commons.types;

import junit.framework.TestCase;

import org.jcoderz.commons.ArgumentMalformedException;

/**
 * This test case checks basic functionality of the Email type.
 *
 * @author Michael Rumpf
 */
public class EmailAddressTest extends TestCase
{
  public void testNullParameter ()
  {
    try
    {
      new EmailAddress(null);
      fail("ArgumentMalformedException should be thrown");
    }
    catch (ArgumentMalformedException ex)
    {
      // expected
    }
  }

  public void testEmptyParameter ()
  {
    try
    {
      new EmailAddress("");
      fail("ArgumentMalformedException should be thrown");
    }
    catch (ArgumentMalformedException ex)
    {
      // expected
    }
  }

  public void testNoAtSign ()
  {
    try
    {
      new EmailAddress("aaabbb.com");
      fail("ArgumentMalformedException should be thrown");
    }
    catch (ArgumentMalformedException ex)
    {
      // expected
    }
  }

  public void testInvalidLocalPart ()
  {
    try
    {
      new EmailAddress("xx(yy)zz@achievo.com");
      fail("ArgumentMalformedException should be thrown");
    }
    catch (ArgumentMalformedException ex)
    {
      // expected
    }
  }

  public void testGoodEmail ()
  {
    final EmailAddress email = new EmailAddress("test@example.com");
    assertEquals(
        "Unexpected name in valid email address.", "test", email.getName());
    assertEquals(
        "Unexpected domain part in valid email address.", "example.com",
        email.getDomain());
  }
}
