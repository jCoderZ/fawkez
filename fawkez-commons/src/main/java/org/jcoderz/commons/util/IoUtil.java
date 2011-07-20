/*
 * $Id: IoUtil.java 1638 2010-09-13 19:24:53Z mgriffel $
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

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collects some I/O utility functions.
 *
 * @author Michael Griffel
 * @author Andreas Mandel
 */
public final class IoUtil
{
   /** class name for use in logger */
   private static final String CLASSNAME = IoUtil.class.getName();

   /** logging facility */
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private static final int BUFFER_SIZE = 4096;

   /**
    * Constructor.
    */
   private IoUtil ()
   {
      // Utility class -- only static methods
   }

   /**
    * Closes the input stream (safe).
    *
    * This method tries to close the given input stream and
    * if an IOException occurs a message with the level
    * {@link Level#FINE} is logged. It's safe to pass a
    * <code>null</code> reference for the argument.
    *
    * @param in the input stream that should be closed.
    */
   public static void close (InputStream in)
   {
      if (in != null)
      {
         try
         {
            in.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(
                  InputStream.class, in.getClass(), x);
         }
      }
   }

   /**
    * Closes the output stream (safe).
    *
    * This method tries to close the given output stream and
    * if an IOException occurs a message with the level
    * {@link Level#FINE} is logged. It's safe to pass a
    * <code>null</code> reference for the argument.
    *
    * @param out the output stream that should be closed.
    */
   public static void close (OutputStream out)
   {
      if (out != null)
      {
         try
         {
            out.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(
                  OutputStream.class, out.getClass(), x);
         }
      }
   }

   /**
    * Closes the reader (safe).
    *
    * This method tries to close the given reader and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param reader the reader that should be closed.
    */
   public static void close (Reader reader)
   {
      if (reader != null)
      {
         try
         {
            reader.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(
                  Reader.class, reader.getClass(), x);
         }
      }
   }

   /**
    * Closes the RandomAccessFile (safe).
    *
    * This method tries to close the given RandomAccessFile and if an
    * IOException occurs a message with the level {@link Level#FINE}
    * is logged. It's safe to pass a <code>null</code> reference
    * for the argument.
    *
    * @param randomAccessFile the randomAccessFile that should be closed.
    */
   public static void close (RandomAccessFile randomAccessFile)
   {
      if (randomAccessFile != null)
      {
         try
         {
            randomAccessFile.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(
                  Reader.class, randomAccessFile.getClass(), x);
         }
      }
   }

   /**
    * Closes the writer (safe).
    *
    * This method tries to close the given writer and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param writer the writer that should be closed.
    */
   public static void close (Writer writer)
   {
      if (writer != null)
      {
         try
         {
            writer.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(
                  Writer.class, writer.getClass(), x);
         }
      }
   }

   /**
    * Closes the channel (safe).
    *
    * This method tries to close the given channel and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param channel the channel that should be closed.
    */
   public static void close (Channel channel)
   {
      if (channel != null)
      {
         try
         {
            channel.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(Channel.class, channel.getClass(), x);
         }
      }
   }

   /**
    * Closes the socket (safe).
    *
    * This method tries to close the given socket and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param socket the socket that should be closed.
    */
   public static void close (Socket socket)
   {
      if (socket != null)
      {
         try
         {
             socket.close();
         }
         catch (IOException x)
         {
            logCloseFailedWarningMessage(Channel.class, socket.getClass(), x);
         }
      }
   }

   /**
    * Reads <code>expectedLength</code> bytes from the input
    * stream <code>in</code>.
    *
    * @param in the input stream to read from.
    * @param expectedLength the expected size.
    * @return an byte array with <code>expectedLength</code> bytes.
    * @throws IOException in an I/O error occurs or if the read size is not the
    *       expected size.
    */
   public static byte[] readFully (InputStream in, int expectedLength)
         throws IOException
   {
      final byte[] buffer = new byte[expectedLength];
      int pos = 0;
      int read = 0;

      while ((read = in.read(buffer, pos, expectedLength - pos)) >= 0)
      {
         pos += read;
         if (expectedLength == pos)
         {
            break;
         }
      }

      if (expectedLength != pos)
      {
         throw new IOException(
               "Buffer underread. Could not read " + expectedLength
               + " bytes from stream. Expected was " + expectedLength
               + " bytes, but got only " + pos + " bytes");
      }

      return buffer;
   }

   /**
    * Reads all bytes from the input stream <code>in</code>.
    *
    * @param in the input stream to read from.
    * @return an byte array with read bytes.
    * @throws IOException in an I/O error occurs.
    */
   public static byte[] readFully (InputStream in)
         throws IOException
   {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int read = 0;
      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      while ((read = in.read(buffer)) >= 0)
      {
         out.write(buffer, 0, read);
      }

      return out.toByteArray();
   }

   /**
    * Reads all characters from the reader <code>in</code>.
    *
    * @param in the Reader to read from.
    * @return an String containing the read data
    * @throws IOException in an I/O error occurs.
    */
   public static String readFully (Reader in)
         throws IOException
   {
      final char[] buffer = new char[BUFFER_SIZE];
      int read = 0;
      final StringWriter out = new StringWriter();

      while ((read = in.read(buffer)) >= 0)
      {
         out.write(buffer, 0, read);
      }

      return out.toString();
   }


   /**
    * Reads all characters from the reader <code>in</code> and
    * normalizes newlines to single '\n'.
    *
    * @param in the Reader to read from.
    * @return an String containing the read data
    * @throws IOException in an I/O error occurs.
    */
   public static String readFullyNormalizeNewLine (Reader in)
         throws IOException
   {
      final StringWriter out = new StringWriter();

      int c;
      int last = 0;
      while ((c = in.read()) != -1)
      {
         if (c == '\n')
         {
             if (last != '\r')
             {
                 out.write(c);
             }
         }
         else if (c == '\r' || c == '\u0085' 
             || c == '\u2028' || c == '\u2029')
         {
             if (last != '\n')
             {
                 out.write('\n');
             }
         }
         else
         {
             out.write(c);
         }
         last = c;
      }

      return out.toString();
   }

   /**
    * Reads all data from in and copies it to the out stream.
    * @param in the stream to read.
    * @param out the stream to write.
    * @throws IOException if an I/O issue occurs.
    */
   public static void copy (InputStream in, OutputStream out)
         throws IOException
   {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int read;

      while ((read = in.read(buffer)) >= 0)
      {
         out.write(buffer, 0, read);
      }
   }

   /**
    * Copies file <tt>src</tt> to file <tt>dest</tt>.
    * @param src the source file.
    * @param dest the destination file.
    * @throws IOException if an I/O issue occurs.
    */
   public static void copy (File src, File dest)
         throws IOException
   {
      final InputStream in = new FileInputStream(src);
      try
      {
          final OutputStream out = new FileOutputStream(dest);
          try
          {
             copy(in, out);
          }
          finally
          {
             close(out);
          }
      }
      finally
      {
         close(in);
      }
   }
   
   /**
    * Ensures that the given number of bytes are skipped from the given 
    * input stream.
    * @param in the input stream.
    * @param bytes the number of bytes to skip.
    * @throws IOException if the stream does not support seek,
    *              or if some other I/O error occurs.
    * @see InputStream#skip             
    */
    public static void skip (InputStream in, int bytes)
        throws IOException
    {
        long remaining = bytes;
        while (remaining != 0)
        {
            final long skipped = in.skip(remaining);
            if (skipped == 0)
            {
                throw new EOFException();
            }
            remaining -= skipped;
        }
    }


   private static void logCloseFailedWarningMessage (
         Class<?> resource, Class<?> clazz, IOException x)
   {
      logger.log(Level.FINE, "Error while closing " + resource.getName() + ": "
            + clazz.getName() + ".close()", x);
   }

}
