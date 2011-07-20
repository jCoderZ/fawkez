/*
 * $Id: JarUtils.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;


/**
 * Jar Utility class.
 *
 * @author Michael Griffel
 * @author Andreas Mandel
 */
public final class JarUtils
{
    private static final int BUFFER_SIZE = 4096;

    /**
     * Utility class - no instances allowed.
     */
    private JarUtils ()
    {
        // no instances allowed -- provides only static helper functions
    }

    /**
     * Extract a jar archive into the base directory
     * <code>baseDir</code>.
     *
     * @param baseDir the root directory where the archive is extracted
     *        to.
     * @param archive jar file.
     * @throws IOException in case of an I/O error.
     */
    public static void extractJarArchive (File baseDir, File archive)
        throws IOException
    {
        final JarFile archiveFile = new JarFile(archive);
        final List<JarEntry> archiveEntries = Collections.list(archiveFile.entries());
        for (final Iterator<JarEntry> iterator = archiveEntries.iterator(); iterator
            .hasNext();)
        {
            InputStream in = null;
            FileOutputStream out = null;
            try
            {
                final ZipEntry e = (ZipEntry) iterator.next();
                in = archiveFile.getInputStream(e);
                final File f = new File(baseDir, e.getName());
                if (e.isDirectory())
                {
                    if (!f.exists())
                    {
                        if (!f.mkdirs())
                        {
                            throw new IOException("Cannot create directory "
                                + f);
                        }
                    }
                }
                else
                {
                    if (!f.getParentFile().exists())
                    {
                        if (!f.getParentFile().mkdirs())
                        {
                            throw new IOException("Cannot create directory "
                                + f.getParentFile());
                        }
                    }
                    out = new FileOutputStream(f);
                    copy(in, out);
                }
            }
            finally
            {
                IoUtil.close(out);
                IoUtil.close(in);
            }
        }
    }

    /**
     * Creates a jar archive from the directory.
     *
     * @param baseDir for the jar archive.
     * @param archive the jar file.
     * @throws IOException in case of an I/O error.
     */
    public static void createJarArchive (File baseDir, File archive)
        throws IOException
    {
        JarOutputStream jarArchive = null;
        try
        {
            jarArchive = new JarOutputStream(new FileOutputStream(archive));
            addFileToJar(baseDir, baseDir, jarArchive);
        }
        finally
        {
            IoUtil.close(jarArchive);
        }
    }

    private static void addFileToJar (File baseDir, File file,
        JarOutputStream archive)
        throws IOException
    {
        if (file == null)
        {
            // done
        }
        else if (file.isDirectory())
        {
            String path = FileUtils.getRelativePath(baseDir, file);
            if (!path.equals("/") && !path.equals(""))
            {
                if (!path.endsWith("/"))
                {
                    path += "/";
                }
                final JarEntry entry = new JarEntry(path);
                archive.putNextEntry(entry);
                archive.closeEntry();
            }
            final File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                addFileToJar(baseDir, files[i], archive);
            }
        }
        else
        {
            final String path = FileUtils.getRelativePath(baseDir, file);
            final JarEntry entry = new JarEntry(path);
            archive.putNextEntry(entry);
            InputStream in = null;
            try
            {
                in = new FileInputStream(file);
                copy(in, archive);
            }
            finally
            {
                IoUtil.close(in);
            }
            archive.closeEntry();
        }
    }

    /**
     * Copies the content of the input stream <code>in</code> to the
     * output stream <code>out</code>.
     *
     * @param in input stream.
     * @param out output stream.
     * @throws IOException in case of an I/O error.
     */
    private static void copy (InputStream in, OutputStream out)
        throws IOException
    {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int nread;
        while ((nread = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, nread);
        }
    }
}
