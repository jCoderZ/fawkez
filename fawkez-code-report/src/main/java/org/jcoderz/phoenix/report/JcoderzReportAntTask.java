/*
 * $Id: JcoderzReportAntTask.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.phoenix.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;

/**
 * jCoderZ Report Ant Task.
 *
 * This Task takes none, one or more input reports such as Checkstyle or PMD
 * and generates a normalized report file (XML) for any Java source under
 * <code>srcdir</code>.
 *
 * @author Michael Griffel (Michael.Griffel@jcoderz.com)
 */
public final class JcoderzReportAntTask
      extends MatchingTask
{
   /** the current working directory when forking JVM. */
   private File mWorkingDir = null;

   /** Output directory for XML/HTML report. */
   private File mOut = new File(".");
   /** The Filter Filename. */
   private File mFilter = null;
   /** The project's name. */
   private String mName = "Not defined";
   /** The output format. */
   private OutputFormat mOutputFormat = OutputFormat.XML;
   /** Flag: exit build process if an error occurred. */
   private boolean mFailOnError = false;
   /** List of input report of type JcoderzReportAntTask.Report. */
   private final List mReportFiles = new ArrayList();
   /** The Java Commandline. */
   private final CommandlineJava mCommandline = new CommandlineJava();
   /**
    * List of source directories of type JcoderzReportAntTask.SourceDirectory.
    */
   private final List mSourceDirectories = new ArrayList();

   /** Debug output flag. */
   private boolean mDebug = false;


   /**
    * Sets the output directory.
    * This directory is used to store the report file(s).
    *
    * @param dir the output directory
    */
   public final void setOut (File dir)
   {
      mOut = dir;
   }

   /**
    * Sets the filter file.
    *
    * @param f the filter file
    */
   public final void setFilter (File f)
   {
      mFilter = f;
   }

   /**
    * Sets the projectName to given <code>projectName</code>.
    *
    * @param projectName the project name
    */
   public final void setName (String projectName)
   {
      mName = projectName;
   }

   /**
    * The directory to invoke the VM in. Ignored if no JVM is forked.
    *
    * @param dir the directory to invoke the JVM from
    */
   public void setDir (File dir)
   {
      mWorkingDir = dir;
   }

   /**
    * Set whether we should fail on an error.
    *
    * @param b whether we should fail on an error
    */
   public void setFailonerror (boolean b)
   {
      mFailOnError = b;
   }

   /**
    * Sets the output format.
    *
    * @param s the output format
    * @throws BuildException throws a BuildException when the format
    *       name is not valid
    */
   public void setFormat (String s)
         throws BuildException
   {
      try
      {
         mOutputFormat = OutputFormat.fromString(s);
      }
      catch (IllegalArgumentException e)
      {
         throw new BuildException("Unsupported output format '"
               + s + "'", e, getLocation());
      }
   }

   /**
    * Sets the debug flag.
    *
    * @param b the debug mode
    */
   public void setDebug (boolean b)
   {
      mDebug = b;
   }

   /**
    * Adds a classpath to the command line.
    *
    * @return the created classpath
    */
   public Path createClasspath ()
   {
      return mCommandline.createClasspath(getProject()).createPath();
   }

   /**
    * Adds a bootclasspath to the command line.
    *
    * @return the created bootclasspath
    */
   public Path createBootclasspath ()
   {
      return mCommandline.createBootclasspath(getProject()).createPath();
   }

   /**
    * Adds a system property.
    *
    * @param sysp system property
    */
   public void addSysproperty (Environment.Variable sysp)
   {
      mCommandline.addSysproperty(sysp);
   }

   /**
    * Adds a JVM argument.
    *
    * @return the created command line argument
    */
   public Commandline.Argument createJvmarg ()
   {
      return mCommandline.createVmArgument();
   }

   /**
    * Adds a report to the list of reports.
    *
    * @return the created report
    */
   public Report createReport ()
   {
      final Report ret = new Report();
      mReportFiles.add(ret);
      return ret;
   }

   /**
    * Adds a source directory to the list of directories.
    *
    * @return the created source directory
    */
   public SourceDirectory createSrcDir ()
   {
      final SourceDirectory ret = new SourceDirectory();
      mSourceDirectories.add(ret);
      return ret;
   }

   /**
    * Executes this task.
    *
    * @throws BuildException An building exception occurred.
    */
   public void execute ()
         throws BuildException
   {
      checkParameters();

      try
      {
         int exitValue;

         exitValue = executeAsForked();

         if (exitValue != 0)
         {
            final String msg = "ReportNormalizer returned with exit code '"
               + exitValue + "'";
            log(msg, Project.MSG_WARN);
            throw new BuildException(msg, getLocation());
         }
      }
      catch (BuildException e)
      {
         if (mFailOnError)
         {
            throw e;
         }
         log(e.getMessage(), e, Project.MSG_ERR);
      }
   }

   /**
    * Create a Java command line for executing the ReportNormalizer.
    *
    * @return the Java command line
    */
   private CommandlineJava createCommandline ()
   {
       final CommandlineJava cmd;
       try
       {
           cmd = (CommandlineJava) mCommandline.clone();
       }
       catch (CloneNotSupportedException unexpected)
       {
           throw new RuntimeException(
                   "Ups, CommandLineJava doesn't support the method clone()",
                   unexpected);
       }

      cmd.setClassname(ReportNormalizer.class.getName());

      cmd.createArgument().setValue("-out");
      cmd.createArgument().setFile(mOut);

      cmd.createArgument().setValue("-projectName");
      cmd.createArgument().setValue(mName);

      cmd.createArgument().setValue("-format");
      cmd.createArgument().setValue(mOutputFormat.toString());

      cmd.createArgument().setValue("-loglevel");
      if (mDebug)
      {
         cmd.createArgument().setValue("ALL");
      }
      else
      {
         cmd.createArgument().setValue("INFO");
      }

      if (mFilter != null)
      {
         cmd.createArgument().setValue("-filter");
         cmd.createArgument().setFile(mFilter);
      }

      for (final Iterator iterator = mSourceDirectories.iterator();
            iterator.hasNext();)
      {
         final SourceDirectory sourceDir = (SourceDirectory) iterator.next();
         cmd.createArgument().setValue("-srcDir");
         cmd.createArgument().setPath(
                  new Path(getProject(), sourceDir.getDir()));
      }

      for (final Iterator iterator = mReportFiles.iterator();
            iterator.hasNext();)
      {
         final Report reportFile = (Report) iterator.next();
         if (reportFile.testIfCondition())
         {
            cmd.createArgument().setValue("-" + reportFile.getFormat());
            cmd.createArgument().setFile(reportFile.getFile());
         }
      }

      return cmd;
   }

   private int executeAsForked ()
         throws BuildException
   {
      final Execute execute
         = new Execute(new LogStreamHandler(
                  this, Project.MSG_INFO, Project.MSG_WARN));

      final CommandlineJava cmd = createCommandline();
      execute.setCommandline(cmd.getCommandline());

      if (mWorkingDir != null)
      {
         log("Setting working directory to : "
               + mWorkingDir, Project.MSG_VERBOSE);
         execute.setWorkingDirectory(mWorkingDir);
         execute.setAntRun(getProject());
      }

      final Path classpath = mCommandline.getClasspath();
      if (classpath != null)
      {
         log("Using CLASSPATH " + classpath, Project.MSG_VERBOSE);
      }

      log("Executing: [fork] " + cmd.toString(), Project.MSG_VERBOSE);

      try
      {
         return execute.execute();
      }
      catch (IOException e)
      {
         throw new BuildException("Process fork failed.", e, getLocation());
      }
   }

   private void checkParameters ()
   {
      if (mSourceDirectories.size() == 0)
      {
         throw new BuildException(
               "at least one srcdir element must be specified!", getLocation());
      }
   }

   /** This class represents input report with a format and filename. */
   public final class Report
   {
      private ReportFormat mReportFormat;
      private File mReportFilename;
      private String mIfCondition = "";

      /**
       * Returns the report filename.
       *
       * @return the report filename
       */
      public File getFile ()
      {
         return mReportFilename;
      }

      /**
       * Sets the report filename.
       *
       * @param reportFilename the report filename
       */
      public void setFile (File reportFilename)
      {
         mReportFilename = reportFilename;
      }

      /**
       * Returns the report format.
       *
       * @return the report format
       */
      public ReportFormat getFormat ()
      {
         return mReportFormat;
      }

      /**
       * Sets the report format.
       *
       * @param reportFormat the report format
       */
      public void setFormat (String reportFormat)
      {
         mReportFormat = ReportFormat.fromString(reportFormat);
      }

      /**
       * Only use report file if the property is set to <code>true</code>.
       *
       * @param property the property name to check
       */
      public void setif (String property)
      {
         mIfCondition = (property == null) ? "" : property;
      }

      /**
       * Tests whether or not the "if" condition is satisfied.
       *
       * @return whether or not the "if" condition is satisfied. If no
       *         condition (or an empty condition) has been set,
       *         <code>true</code> is returned
       */
      private boolean testIfCondition ()
      {
         final boolean result;
         if ("".equals(mIfCondition))
         {
             result = true;
         }
         else
         {
             final String test = getProject().replaceProperties(mIfCondition);
             result = getProject().getProperty(test) != null;
         }
         return result;
      }
   }

   public static final class SourceDirectory
   {
      private String mSourceDir;

      /**
       * Returns the sourceDir.
       *
       * @return the source directory
       */
      public final String getDir ()
      {
         return mSourceDir;
      }

      /**
       * Sets the source directory.
       *
       * @param sourceDir the source directory
       */
      public final void setDir (String sourceDir)
      {
         mSourceDir = sourceDir;
      }
   }
}
