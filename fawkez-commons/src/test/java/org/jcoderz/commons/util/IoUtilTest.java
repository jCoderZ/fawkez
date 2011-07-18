/*
 * $Id: IoUtilTest.java 1638 2010-09-13 19:24:53Z mgriffel $
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;


/**
 * Unit Tests for the class {@link org.jcoderz.commons.util.IoUtil}.
 *
 * @author Michael Griffel
 */
public class IoUtilTest
      extends TestCase
{
   private static final Random RANDOM_GENERATOR = new Random();
   
   private static final int GAP_SIZE = 10 * 1024;

   /**
    * Tests the method {@link IoUtil#readFully(InputStream, int)}.
    * @throws IOException in case of an unexpected I/O error.
    */
   public void testReadFully ()
         throws IOException
   {
      read1kBytes();
      read0Bytes();
      readAhead();
      underread();
   }

   /**
    * Tests the method {@link IoUtil#close(InputStream)}.
    */
   public void testSafeCloseInputStream ()
   {
      final InputStream in = new ByteArrayInputStream(new byte[0])
         {
            public void close ()
                  throws IOException
            {
               throw new IOException();
            }
         };

      IoUtil.close(in);
      IoUtil.close(in);
      IoUtil.close((InputStream) null);
   }

   /**
    * Tests the method {@link IoUtil#close(OutputStream)}.
    */
   public void testSafeCloseOutputStream ()
   {
      final OutputStream out = new ByteArrayOutputStream()
         {
            public void close ()
                  throws IOException
            {
               throw new IOException();
            }
         };

      IoUtil.close(out);
      IoUtil.close(out);
      IoUtil.close((OutputStream) null);
   }

   /**
    * Tests the method {@link IoUtil#close(Reader)}.
    */
   public void testSafeCloseReader ()
   {
      final Reader reader = new Reader()
         {
            public void close ()
                  throws IOException
            {
               throw new IOException();
            }

            public int read (char[] cbuf, int off, int len)
            {
               return 0;
            }
         };

      IoUtil.close(reader);
      IoUtil.close(reader);
      IoUtil.close((Reader) null);
   }

   /**
    * Tests the method {@link IoUtil#close(Writer)}.
    */
   public void testSafeCloseWriter ()
   {
      final Writer writer = new Writer()
         {
            public void close ()
                  throws IOException
            {
               throw new IOException();
            }

            public void flush ()
            {
                // NOP
            }

            public void write (char[] cbuf, int off, int len)
            {
                // NOP
            }
         };

      IoUtil.close(writer);
      IoUtil.close(writer);
      IoUtil.close((Writer) null);
   }

   /**
    * Tests the method {@link IoUtil#close(RandomAccessFile)}.
    * @throws Exception in case of an unexpected error.
    */
   public void testSafeCloseRandomAccessFile ()
         throws Exception
   {
      final File tmp = File.createTempFile("tmp", "tmp");
      tmp.deleteOnExit();
      final RandomAccessFile file = new RandomAccessFile(tmp, "r")
         {
            public void close ()
                  throws IOException
            {
               throw new IOException();
            }
         };

      IoUtil.close(file);
      IoUtil.close(file);
      IoUtil.close((RandomAccessFile) null);
   }

   /**
    * Tests the method {@link IoUtil#close(Channel)}.
    * @throws Exception in case of an unexpected error.
    */
   public void testSafeCloseChannel ()
         throws Exception
   {
      final Channel channel = new ChannelImpl();
      IoUtil.close(channel);
      IoUtil.close(channel);
      IoUtil.close((Channel) null);
   }

   
   public void testSkip() throws Exception {
       final File tmp = File.createTempFile("fawkez", "tmp");
       FileOutputStream out = new FileOutputStream(tmp);
       out.write(1);
       out.write(new byte[GAP_SIZE]);
       out.write(2);
       out.close();
       final InputStream in = new BufferedInputStream(new FileInputStream((tmp)));
       int first = in.read();
       IoUtil.skip(in, GAP_SIZE); // instead of in.skip(GAP_SIZE);
       int second = in.read();
       assertEquals(3, first + second);
       
   }

   private void read1kBytes ()
         throws IOException
   {
      final byte[] inData = new byte[Constants.BYTES_PER_KILO_BYTE];
      final InputStream in = createRandomInputStream(inData);
      final byte[] outData = IoUtil.readFully(in, inData.length);
      assertTrue("bytes arrays should be equal",
            Arrays.equals(inData, outData));
   }

   private void read0Bytes ()
         throws IOException
   {
      final byte[] inData = new byte[0];
      final InputStream in = createRandomInputStream(inData);
      final byte[] outData = IoUtil.readFully(in, inData.length);
      assertTrue("bytes arrays should be equal",
            Arrays.equals(inData, outData));
   }

   private void readAhead ()
   {
      final byte[] inData = new byte[Constants.BYTES_PER_KILO_BYTE];
      final InputStream in = createRandomInputStream(inData);
      try
      {
         IoUtil.readFully(in, inData.length + 1);
         fail("Expected IOException for reading more bytes than available");
      }
      catch (IOException expected)
      {
         assertNotNull("Expected a real exception.", expected);
      }
   }
   private void underread ()
         throws IOException
   {
      final byte[] inData = new byte[Constants.BYTES_PER_KILO_BYTE];
      final InputStream in = createRandomInputStream(inData);
      final byte[] outData = IoUtil.readFully(in, inData.length - 1);
      final byte[] strippedInData = new byte[inData.length - 1];
      System.arraycopy(inData, 0, strippedInData, 0, inData.length - 1);
      assertTrue("bytes arrays should be equal except the last one",
            Arrays.equals(strippedInData, outData));
   }

   private InputStream createRandomInputStream (byte[] inData)
   {
      RANDOM_GENERATOR.nextBytes(inData);
      final InputStream in = new ByteArrayInputStream(inData);
      return in;
   }

   private static final class ChannelImpl
         implements ReadableByteChannel
   {
      private boolean mIsOpen = true;
      
      /** {@inheritDoc} */
      public int read (ByteBuffer dst)
      {
         return 0;
      }
      
      /** {@inheritDoc} */
      public void close ()
            throws IOException
      {
         if (mIsOpen)
         {
            mIsOpen = false;
         }
         else
         {
            throw new IOException();
         }
      }

      /** {@inheritDoc} */
      public boolean isOpen ()
      {
         return mIsOpen;
      }
   }


}
