/*
 * $Id: LazyFileOutputStreamTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * Test LazyFileOutputStream class.
 *
 * @author Andreas Mandel
 */
public class LazyFileOutputStreamTest
    extends TestCase
{
    public void testNewFile ()
        throws IOException
    {
        final File file = File.createTempFile("test", "new");
        try
        {
            final LazyFileOutputStream out = new LazyFileOutputStream(file);
            out.write(1);
            assertFalse("New stream should never be buffered.",
                out.isBuffered());
            out.close();
            assertFalse("New stream should not be buffered after close.",
                out.isBuffered());
            assertContent(file, new byte[] {1});
            assertTrue("Could not delete temp '" + file + "'.",
                file.delete());
        }
        finally
        {
            file.delete();
        }
    }

    public void testShorterFile ()
        throws IOException
    {
        final File file = File.createTempFile("test", "new");
        try
        {
            fillTestData(file);
            final LazyFileOutputStream out = new LazyFileOutputStream(file);
            out.write(0);
            assertTrue("Same data should be buffered.",
                out.isBuffered());
            out.close();
            assertFalse("Shorter new stream must not be buffered.",
                out.isBuffered());
            assertContent(file, new byte[] { 0 });
            assertTrue("Could not delete temp '" + file + "'.",
                file.delete());
        }
        finally
        {
            file.delete();
        }
    }

    public void testLongerFile ()
        throws IOException
    {
        final File file = File.createTempFile("test", "new");
        try
        {
            fillTestData(file);

            final LazyFileOutputStream out = new LazyFileOutputStream(file);
            out.write(0);
            out.write(1);
            out.write(0);
            assertTrue("Same data should be buffered.",
                out.isBuffered());
            out.write(0);
            assertFalse("Longer new stream must not be buffered.",
                out.isBuffered());
            assertContent(file, new byte[] {0, 1, 0, 0});
            out.close();
            assertFalse("Longer new stream must not be buffered.",
                out.isBuffered());
            assertTrue("Could not delete temp '" + file + "'.",
                file.delete());
        }
        finally
        {
            file.delete();
        }
    }

    public void testSameFile ()
        throws IOException
    {
        File file = File.createTempFile("test", "new");
        try
        {
            fillTestData(file);

            final LazyFileOutputStream out = new LazyFileOutputStream(file);
            out.write(0);
            out.write(1);
            out.write(0);
            assertTrue("Same data should be buffered.",
                out.isBuffered());
            out.close();
            assertTrue("Same data should be buffered.",
                out.isBuffered());
            assertContent(file, new byte[] {0, 1, 0});
            assertTrue("Could not delete temp '" + file + "'.",
                file.delete());
        }
        finally
        {
            file.delete();
        }
    }

    public void testDifferentFile ()
        throws IOException
    {
        File file = File.createTempFile("test", "new");
        try
        {
            fillTestData(file);

            final LazyFileOutputStream out = new LazyFileOutputStream(file);
            out.write(0);
            out.write(1);
            assertTrue("Same data should be buffered.",
                out.isBuffered());
            out.write(1);
            assertFalse("Different data should not be buffered.",
                out.isBuffered());
            out.close();
            assertContent(file, new byte[] {0, 1, 1});
            assertTrue("Could not delete temp '" + file + "'.",
                file.delete());
        }
        finally
        {
            file.delete();
        }
    }

    private void fillTestData (File file)
        throws FileNotFoundException, IOException
    {
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(0);
        fos.write(1);
        fos.write(0);
        fos.close();
    }

    private void assertContent (File file, byte[] bs)
        throws IOException
    {
        final InputStream is = new FileInputStream(file);
        try
        {
            for (int i = 0; i < bs.length; i++)
            {
                final int c = is.read();
                assertEquals("File content unexpected.", bs[i], c);
            }
            assertEquals("Stream to long?", -1, is.read());
        }
        finally
        {
            IoUtil.close(is);
        }
    }
}
