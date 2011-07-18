/*
 * $Id: AntTaskUtil.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.taskdefs;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;


/**
 * Provides utility functions for Ant task.
 *
 * @author Michael Griffel
 */
public final class AntTaskUtil
{
    private static final String FORMAT_SVG = "svg";

    private static final int PACKET_SIZE = 1;

    private AntTaskUtil ()
    {
        // no instances allowed -- provides only static helper methods.
    }

    /**
     * Ensure the directory exists for a given directory name.
     *
     * @param directory the directory name that is required.
     * @exception BuildException if the directories cannot be created.
     */
    public static void ensureDirectory (File directory)
        throws BuildException
    {
        if (!directory.exists())
        {
            if (!directory.mkdirs())
            {
                throw new BuildException("Unable to create directory: "
                    + directory.getAbsolutePath());
            }
        }
    }

    /**
     * Ensure the directory exists for a given file.
     *
     * @param targetFile the file for which the directories are
     *        required.
     * @exception BuildException if the directories cannot be created.
     */
    public static void ensureDirectoryForFile (File targetFile)
        throws BuildException
    {
        ensureDirectory(targetFile.getParentFile());
    }

    /**
     * Strip file extension.
     *
     * @param fileWithExtension the file with extension
     * @return the string
     */
    public static String stripFileExtension (String fileWithExtension)
    {
        final int lastIndexOfDot = fileWithExtension.lastIndexOf('.');
        final String result;
        if (lastIndexOfDot != -1)
        {
            result = fileWithExtension.substring(0, lastIndexOfDot);
        }
        else
        {
            result = fileWithExtension;
        }
        return result;
    }

    /**
     * Render dot files.
     *
     * @param task the task
     * @param dotDir the dot dir
     * @param failOnError the fail on error
     */
    public static void renderDotFiles (Task task, File dotDir,
        boolean failOnError)
    {
        final File[] dotFiles = dotDir.listFiles(new FilenameFilter()
        {
            public boolean accept (File dir, String name)
            {
                final boolean result;
                if (name.endsWith(".dot"))
                {
                    result = true;
                }
                else
                {
                    result = false;
                }
                return result;
            }
        });
        if (dotFiles != null)
        {
            dots2svgs(task, failOnError, dotFiles);
        }
        else
        {
            task.log("No .dot files found to render", Project.MSG_VERBOSE);
        }
    }
    
    /**
     * Render .gnuplot files.
     *
     * @param task the task
     * @param gnuplotDir the gnuplot dir
     * @param failOnError the fail on error
     */
    public static void renderGnuplotFiles (Task task, File gnuplotDir,
        boolean failOnError)
    {
        final File[] gnuplotFiles = gnuplotDir.listFiles(new FilenameFilter()
        {
            public boolean accept (File dir, String name)
            {
                final boolean result;
                if (name.endsWith(".gnuplot"))
                {
                    result = true;
                }
                else
                {
                    result = false;
                }
                return result;
            }
        });
        if (gnuplotFiles != null)
        {
            gnuplots2svgs(task, failOnError, gnuplotFiles);
        }
        else
        {
            task.log("No .gnuplot files found to render", Project.MSG_VERBOSE);
        }
    }

    private static void dots2svgs (Task task, boolean failOnError,
        final File[] dotFiles)
    {
        /*
         * Windows command line size is limited, so we render up to
         * PACKET_SIZE files at once.
         */
        List dotPackets = createPackets(dotFiles);
        for (Iterator packetIter = dotPackets.iterator(); packetIter.hasNext();)
        {
            File[] dotPacket = (File[]) packetIter.next();
            final DotTask dot = new DotTask();
            dot.setProject(task.getProject());
            dot.setTaskName("dot");
            dot.setFailonerror(failOnError);
            dot.setFormat(FORMAT_SVG);
            dot.setInFiles(dotPacket);
            dot.execute();
        }
        // Silly Graphviz always appends the new extension.
        for (int i = 0; i < dotFiles.length; i++)
        {
            File generatedFile = new File(dotFiles[i].getParentFile(),
                dotFiles[i].getName() + "." + FORMAT_SVG);
            File targetFile = new File(dotFiles[i].getParentFile(),
                stripFileExtension(dotFiles[i].getName()) + "." + FORMAT_SVG);
            targetFile.delete();
            generatedFile.renameTo(targetFile);
        }
    }
    
    private static void gnuplots2svgs (Task task, boolean failOnError,
        final File[] dotFiles)
    {
        /*
         * Windows command line size is limited, so we render up to
         * PACKET_SIZE files at once.
         */
        List gnuplotPackets = createPackets(dotFiles);
        for (Iterator packetIter = gnuplotPackets.iterator(); packetIter.hasNext();)
        {
            File[] gnuplotPacket = (File[]) packetIter.next();
            final GnuplotTask dot = new GnuplotTask();
            dot.setProject(task.getProject());
            dot.setTaskName("gnuplot");
            dot.setFailonerror(failOnError);
            dot.setInFiles(gnuplotPacket);
            dot.execute();
        }
    }

    private static List createPackets (File[] dotFiles)
    {
        List result = new ArrayList();
        for (int i = 0; i < (dotFiles.length / PACKET_SIZE) + 1; i++)
        {
            final File[] packet;
            int remaining = dotFiles.length - (i * PACKET_SIZE);
            if (remaining < PACKET_SIZE)
            {
                packet = new File[remaining];
            }
            else
            {
                packet = new File[PACKET_SIZE];
            }
            for (int j = 0; j < packet.length; j++)
            {
                int index = (i * PACKET_SIZE) + j;
                if (index < dotFiles.length)
                {
                    packet[j] = dotFiles[index];
                }
            }
            result.add(packet);
        }
        return result;
    }
}
