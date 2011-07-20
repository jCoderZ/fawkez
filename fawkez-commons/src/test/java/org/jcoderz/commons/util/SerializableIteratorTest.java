/*
 * $Id: SerializableIteratorTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests the Serializable Iterator.
 * @author Albrecht Messner
 */
public class SerializableIteratorTest
      extends TestCase
{
   /**
    * Tests serialization of the SerializableIterator.
    * @throws Exception if the testcase fails
    */
   public void testSerialization ()
         throws Exception
   {
      final String[] testData
            = new String[] {"foo", "bar", "bingo", "bongo", "hallo"};
      final SerializableIterator si = SerializableIterator.fromArray(testData);
      final byte[] serialized = serialize(si);
      final SerializableIterator si2
            = (SerializableIterator) deserialize(serialized);
      int count = 0;
      while (si2.hasNext())
      {
         si2.next();
         count++;
      }
      assertEquals("wrong # of items in iterator", testData.length, count);
   }

   public void testContentsAndBoundaries ()
         throws Exception
   {
      final String[] testData = new String[] {"a", "b", "c"};
      final SerializableIterator si = SerializableIterator.fromArray(testData);

      final List<String> testDataAsList = new ArrayList<String>(Arrays.asList(testData));

      for (int i = 0; i < testData.length; i++)
      {
         final String s = (String) si.next();
         assertTrue("Result should be in test data list",
               testDataAsList.contains(s));
         testDataAsList.remove(s);
      }

      try
      {
         si.next();
         fail("Should be at end of iterator");
      }
      catch (NoSuchElementException x)
      {
         // expected
      }
   }

   public void testWithCollection ()
   {
      final Set<String> hs = new HashSet<String>();
      hs.add("gandalf");
      hs.add("frodo");
      hs.add("bilbo");
      hs.add("aragorn");

      final SerializableIterator it = SerializableIterator.fromCollection(hs);
      while (it.hasNext())
      {
         final String s = (String) it.next();
         assertTrue("String must be in test data", hs.contains(s));
      }
   }

   private byte[] serialize (Object o)
         throws IOException
   {
      final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
      final ObjectOutputStream objOutStream
            = new ObjectOutputStream(byteOutStream);
      objOutStream.writeObject(o);
      objOutStream.flush();
      objOutStream.close();
      return byteOutStream.toByteArray();
   }

   private Object deserialize (byte [] b)
         throws IOException, ClassNotFoundException
   {
      final ByteArrayInputStream byteInStream = new ByteArrayInputStream(b);
      final ObjectInputStream objInStream = new ObjectInputStream(byteInStream);
      return objInStream.readObject();
   }
}
