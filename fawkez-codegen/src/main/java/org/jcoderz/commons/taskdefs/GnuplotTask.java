/*
 * $Id: GnuplotTask.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.io.IOException;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;

/**
 * Ant task for the 'gnuplot' command.
 *
 * @author Michael Griffel
 */
public class GnuplotTask
      extends Task
{
   /** The input files. */
   private File[] mInFiles;
   /** terminate ant build on error. */
   private boolean mFailOnError;
   private final Commandline mCommand = new Commandline();

   /**
    * Sets the gnuplot input file to be processed.
    * @param f the gnuplot input file (log message info).
    */
   public void setIn (File f)
   {
      mInFiles = new File[] {f};

   }

   /**
    * Set whether we should fail on an error.
    * @param b Whether we should fail on an error.
    */
   public void setFailonerror (boolean b)
   {
      mFailOnError = b;
   }

   /**
    * Execute this task.
    *
    * @throws BuildException An building exception occurred.
    */
   public void execute ()
         throws BuildException
   {
      try
      {
         checkAttributes();

         mCommand.setExecutable("gnuplot");

         for (int i = 0; i < mInFiles.length; i++)
         {
             mCommand.createArgument().setValue(mInFiles[i].getAbsolutePath());
         }

         final Execute exe = new Execute(new LogStreamHandler(
               this, Project.MSG_VERBOSE, Project.MSG_WARN), null);
         exe.setCommandline(mCommand.getCommandline());
         log(mCommand.describeCommand(), Project.MSG_VERBOSE);
         try
         {
             exe.execute();
         }
         catch (IOException e)
         {
             throw new BuildException(e, getLocation());
         }
      }
      catch (BuildException e)
      {
         if (mFailOnError)
         {
            throw e;
         }
         log(e.getMessage(), Project.MSG_ERR);
      }
   }

   /**
    * Checks the attributes provided by this class.
    * @throws BuildException
    */
   private void checkAttributes ()
         throws BuildException
   {
      checkAttributeInFile();
   }

   private void checkAttributeInFile ()
   {
      if (mInFiles == null)
      {
         throw new BuildException(
               "Missing mandatory attribute 'in'.", getLocation());
      }

      for (int i = 0; i < mInFiles.length; i++)
      {
          if (!mInFiles[i].exists())
          {
             throw new BuildException(
                   "Input file '" + mInFiles[i] + "' not found.",
                   getLocation());
          }
       }
   }

    /**
     * Set the input files.
     *
     * @param inFiles the input files.
     */
    public void setInFiles (File[] inFiles)
    {
        mInFiles = inFiles;
    }

}
