/*
 * $Id: LazyFileOutputStream.java 1068 2008-07-08 19:14:40Z amandel $
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Purpose of this class is to avoid writing a File if it already
 * exists and hold the same content.
 *
 * <p>This class in not about saving time for faster processing nor
 * does is perform anything quicker. It sole checks if the target
 * file exists, and if its content is already the same than
 * the content that should be written. If this is the case no
 * write operation is done at all. Therefore also the file content
 * to be written is held in memory until the file is closed OR
 * it is detected that the content did change.</p>
 *
 * <p>Instances of this class are NOT multi thread save.</p>
 *
 * @author Andreas Mandel
 */
public class LazyFileOutputStream
    extends OutputStream
{
    private static final String CLASSNAME
        = LazyFileOutputStream.class.getName();
    private static final Logger logger = Logger.getLogger(CLASSNAME);
    private boolean mBuffering;
    private OutputStream mOutput;
    private File mFile;
    private InputStream mInputStream;


    /** @see FileOutputStream#FileOutputStream(String, boolean) */
    public LazyFileOutputStream (File file, boolean append)
        throws FileNotFoundException
    {
        mFile = file;
        if (append)
        {
            mBuffering = false;
            mOutput = new FileOutputStream(file, append);
        }
        else
        {
            if (file.exists() && file.canRead())
            {   // Should be OK to let a possible exception fall through
                mInputStream =
                    new BufferedInputStream(new FileInputStream(file));
                mOutput = new ByteArrayOutputStream();
                mBuffering = true;
            }
            else
            {
                mOutput = new FileOutputStream(file, append);
                mBuffering = false;
            }
        }
    }

    /** @see FileOutputStream#FileOutputStream(File) */
    public LazyFileOutputStream (File file)
        throws FileNotFoundException
    {
        this(file, false);
    }

    /** @see FileOutputStream#FileOutputStream(String, boolean) */
    public LazyFileOutputStream (String name, boolean append)
        throws FileNotFoundException
    {
        this(new File(name), append);
    }

    /** @see FileOutputStream#FileOutputStream(String) */
    public LazyFileOutputStream (String name)
        throws FileNotFoundException
    {
        this(new File(name));
    }

    /**
     * Returns true if the file is still buffered.
     * The value might change from true to false until
     * the stream is closed. After this the returned value will not
     * change any more.
     * @return true if the actual out file was not touched (yet).
     */
    public boolean isBuffered ()
    {
        return mBuffering;
    }

    /** {@inheritDoc} */
    public void write (int b)
        throws IOException
    {
        ensureOpen();
        if (mBuffering &&  mInputStream.read() != b)
        {
            stopBuffering();
        }
        mOutput.write(b);
    }

    // TODO: For better performance implement the other write methods!

    /** {@inheritDoc} */
    public void close ()
        throws IOException
    {
        if (mBuffering && mInputStream.read() != -1)
        {   // new output is shorter...
            stopBuffering();
        }
        IoUtil.close(mInputStream);
        if (mOutput != null)
        {
            mOutput.close();
            mOutput = null;
            if (mBuffering)
            {
                logger.fine("Avoided to touch " + mFile);
            }
        }
    }

    /**
     * Cleans up the connection to the file, and ensures that the
     * <code>close</code> method of this file output stream is
     * called when there are no more references to this stream.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileInputStream#close()
     */
    protected void finalize ()
        throws IOException, Throwable
    {
        if (mOutput != null)
        {
            close();
        }
        super.finalize();
    }


    /**
     * Check to make sure that output stream has not been nulled
     * out due to close
     */
    private void ensureOpen ()
        throws IOException
    {
        if (mOutput == null)
        {
            throw new IOException("Stream closed");
        }
    }

    private void stopBuffering ()
        throws IOException
    {
        IoUtil.close(mInputStream);
        IoUtil.close(mOutput);
        // sanity
        if (!(mOutput instanceof ByteArrayOutputStream))
        {
            throw new RuntimeException("Internal inconsistency");
        }
        final byte[] data
            = ((ByteArrayOutputStream) mOutput).toByteArray();
        mOutput = new FileOutputStream(mFile);
        mOutput.write(data);
        mBuffering = false;
    }
}
