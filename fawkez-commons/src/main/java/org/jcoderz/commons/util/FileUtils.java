/*
 * $Id: FileUtils.java 1331 2009-03-28 20:29:42Z amandel $
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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * This class collects some nifty file utility functions.
 *
 * TODO: Cleanup check vs. IoUtil
 *
 * @author Michael Griffel
 * @author Andreas Mandel
 */
public final class FileUtils
{
   private static final int RND_FILENAME_FACTOR = 100000;

   private static final int BUFFER_SIZE = 4096;

   /**
    * Only utility functions -- no instances allowed.
    */
   private FileUtils ()
   {
      // no instances allowed - provides only static utility functions
   }

   /**
    * Copies the file or directory <code>src</code> to the
    * directory <code>destinationDir</code>.
    *
    * @param src the source file or directory.
    * @param destinationDir the destination directory.
    * @throws IOException in case of an I/O error.
    */
   public static void copy (File src, File destinationDir)
         throws IOException
   {
      if (src.isFile())
      {
         copyFile(src, destinationDir);
      }
      else if (src.isDirectory())
      {
         final File subdir = new File(destinationDir, src.getName());
         if (!subdir.exists())
         {
            if (!subdir.mkdir())
            {
               throw new IOException("Failed to create subdir '" + subdir
                     + "'.");
            }
         }
         final File [] files = src.listFiles();
         for (int i = 0; i < files.length; i++)
         {
            copy(files[i], subdir);
         }
      }
   }

   /**
    * Copies the file <code>src</code> to the file or directory
    * <code>dest</code>.
    *
    * @param src The source file.
    * @param dest The destination file or directory.
    * @throws FileNotFoundException if the source file does not exists.
    * @throws IOException in case of an I/O error.
    */
   public static void copyFile (File src, File dest)
         throws FileNotFoundException, IOException
   {
      FileInputStream in = null;
      FileOutputStream out = null;
      try
      {
         in = new FileInputStream(src);
         if (dest.isDirectory())
         {
            out = new FileOutputStream(new File(dest, src.getName()));
         }
         else
         {
            out = new FileOutputStream(dest);
         }
         copy(in, out);
      }
      finally
      {
         close(in);
         close(out);
      }
   }


   /**
    * Copy from an input stream to an output stream.
    *
    * @param in The input stream.
    * @param out The output stream.
    * @throws IOException when an error happens during a read or a write
    *       operation.
    */
   public static void copy (InputStream in, OutputStream out)
         throws IOException
   {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int read;
      while ((read = in.read(buffer)) != -1)
      {
         out.write(buffer, 0, read);
      }
   }


   /**
    * Copy all files under <code>srcDir</code> to the directory
    * <code>dst</code>.
    *
    * Unix command:
    * <pre>
    * $ cp "$scrDir/*" "$dst"
    * </pre>
    *
    * @param srcDir the source directory.
    * @param dst the destination directory.
    * @throws IOException in case of an I/O error.
    */
   public static void copySlashStar (File srcDir, File dst)
         throws IOException
   {
      if (srcDir.isDirectory())
      {
         final File[] files = srcDir.listFiles();
         for (int i = 0; i < files.length; i++)
         {
            copy(files[i], dst);
         }
      }
      else
      {
         throw new IllegalArgumentException("Souce must be a directory. ('"
               + srcDir + "')");
      }
   }

   /**
    * Creates a temporary directory.
    * @param baseDir the base directory.
    * @param prefix prefix for the temporary directory.
    * @return a temporary directory.
    * @throws IOException in case of an I/O error.
    */
   public static File createTempDir (File baseDir, String prefix)
         throws IOException
   {
      final String dirname = prefix
            + String.valueOf((int) (Math.random() * RND_FILENAME_FACTOR));

      final File tempDir = new File(baseDir, dirname);

      if (! tempDir.mkdir())
      {
         throw new IOException("Cannot create temp directory '"
               + tempDir + "'");
      }
      return tempDir;
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
    * @deprecated use IoUtil.close(InputStream)
    */
   public static void close (InputStream in)
   {
       IoUtil.close(in);
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
    * @deprecated use IoUtil.close(OutputStream)
    */
   public static void close (OutputStream out)
   {
       IoUtil.close(out);
   }

   /**
    * Closes the reader (safe).
    *
    * This method tries to close the given reader and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param reader the reader that should be closed.
    * @deprecated use IoUtil.close(Reader)
    */
   public static void safeClose (Reader reader)
   {
       IoUtil.close(reader);
   }

   /**
    * Closes the writer (safe).
    *
    * This method tries to close the given writer and if an IOException occurs
    * a message with the level {@link Level#FINE} is logged. It's safe
    * to pass a <code>null</code> reference for the argument.
    *
    * @param writer the writer that should be closed.
    * @deprecated use IoUtil.close(Writer)
    */
   public static void safeClose (Writer writer)
   {
       IoUtil.close(writer);
   }


   /**
    * Search for files in a directory hierarchy.
    *
    * Unix command:
    * <pre>
    * find path -name pattern
    * </pre>
    * @param path root directory.
    * @param pattern filename pattern.
    * @return a list of files matching the given <code>pattern</code>
    *         under <code>path</code>.
    */
   public static List<File> findFile (File path, String pattern)
   {
      final List<File> ret = new ArrayList<File>();

      // Check whether the path exists
      if (!path.exists())
      {
         throw new IllegalArgumentException(
               "The specified path does not exist! ('"
               + path + "')");
      }

      findFile(path, pattern, ret);

      return ret;
   }

   private static void findFile (File file, String pattern, List<File> found)
   {
      if (file.isDirectory())
      {
         final File[] files = file.listFiles();
         for (int i = 0; i < files.length; i++)
         {
            findFile(files[i], pattern, found);
         }
      }
      else
      {
         if (file.getName().matches(pattern))
         {
            found.add(file);
         }
      }
   }

   /**
    * Remove file or directory.
    *
    * Unix command:
    * <pre>
    * rm -rf file
    * </pre>
    * @param file the file or directory to delete.
    * @throws IOException in case of an I/O error.
    */
   public static void rmdir (File file)
         throws IOException
   {
      if (file == null)
      {
         // done...
      }
      else if (file.isDirectory())
      {
         final File [] files = file.listFiles();
         for (int i = 0; i < files.length; i++)
         {
            rmdir(files[i]);
         }
         if (!file.delete())
         {
            throw new IOException("Failed to delete directory " + file + ".");
         }
      }
      else
      {
         if (!file.delete())
         {
            throw new IOException("Failed to delete file " + file + ".");
         }
      }
   }

   /**
    * Returns the relative path of <code>file</code> to the file
    * <code>basedir</code>.
    * @param baseDir the base directory or file.
    * @param file the file.
    * @return the relative path of the file to the basedir.
    * @throws IOException in case of an I/O error.
    */
   public static String getRelativePath (File baseDir, File file)
         throws IOException
   {
      final String base = baseDir.getCanonicalPath();
      String fileName = file.getCanonicalPath();

      if (fileName.startsWith(base))
      {
         fileName = fileName.substring(base.length());
         if (fileName.charAt(0) == '/')
         {
            fileName = fileName.substring(1);
         }
      }
      else
      {
         throw new RuntimeException("Cannot add file '" + file
               + "' with different baseDir '" + baseDir + "'.");
      }
      return fileName;
   }

   /**
    * Renames the file <code>aFile</code>.
    *
    * @param  aFile The file to be renamed.
    * @param  dest  The new abstract pathname for the named file
    * @throws IOException if the the renaming was not successful.
    */
   public static void rename (File aFile, File dest)
         throws IOException
   {
      if (!aFile.renameTo(dest))
      {
         throw new IOException("Failed to rename " + aFile + " to " + dest);
      }
   }

   /**
    * Deletes the given file <code>aFile</code>.
    *
    * @param  aFile The file to be deleted.
    * @throws IOException if the the deletion was not successful.
    */
   public static void delete (File aFile)
         throws IOException
   {
      if (aFile.exists())
      {
         if (!aFile.delete())
         {
            throw new IOException("Failed to delete " + aFile);
         }
      }
   }

   /**
    * Creates the given directories.
    *
    * @param dirs the directories to be created.
    * @throws IOException the directories could not be created.
    * @see File#mkdirs()
    */
   public static void mkdirs (File dirs)
         throws IOException
   {
      if (!dirs.exists() || !dirs.isDirectory())
      {
         if (!dirs.mkdirs())
         {
            throw new IOException("Failed to create directories " + dirs);
         }
      }
   }

   /**
    * Creates the given directory.
    *
    * @param dir the directory to be created.
    * @throws IOException if the file could not be created.
    * @see File#mkdir()
    */
   public static void mkdir (File dir)
         throws IOException
   {
      if (!dir.exists() || !dir.isDirectory())
      {
         if (!dir.mkdir())
         {
            throw new IOException("Failed to create directory " + dir);
         }
      }
   }

   /**
    * Creates the given file.
    * @param newFile the file to create
    * @throws IOException the file could not be created.
    * @see File#createNewFile()
    */
    public static void createNewFile (File newFile) 
        throws IOException
    {
        if (!newFile.createNewFile())
        {
            throw new IOException("Failed to create new File " + newFile);
        }
    }
}
