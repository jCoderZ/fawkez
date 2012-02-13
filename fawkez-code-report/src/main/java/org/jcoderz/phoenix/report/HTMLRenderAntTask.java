/*
 * $Id: HTMLRenderAntTask.java 1496 2009-06-07 17:28:52Z amandel $
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.jcoderz.commons.util.FileUtils;

/**
 * Ant Task rendering <code>jcoderz-report.xml</code> files
 * into HTML files.
 *
 * @author Michael Rumpf (Michael.Rumpf@jcoderz.org)
 */
public final class HTMLRenderAntTask
      extends MatchingTask
{
   private static final String CLASSNAME = HTMLRenderAntTask.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private File mBaseDir = null;
   /** Output directory for XML/HTML report. */
   private File mOut = null;
   /** The report name. */
   private String mName = null;
   /** The base package name. */
   private String mPackageBase = null;
   private Charset mSourceEncoding = null;

   /**
    * Sets the output directory to given <code>dir</code>.
    * This directory is used to store the XML/HTML report file(s).
    *
    * @param dir The output directory to set
    */
   public void setOut (File dir)
   {
      mOut = dir;
   }

   public void setBaseDir (File dir)
   {
      mBaseDir = dir;
   }

   public void setName (String name)
   {
      mName = name;
   }

   public void setPackageBase (String pkgBase)
   {
     mPackageBase = pkgBase;
   }

   public void setSourceEncoding (String srcEncoding)
   {
     mSourceEncoding = Charset.forName(srcEncoding);
   }

   /**
    * Execute this task.
    *
    * @throws BuildException An building exception occurred
    */
   public void execute ()
         throws BuildException
   {
      try
      {
         final List<String> argList = new ArrayList<String>();

         // FIXME: . is not correct if the build process was started from a
         //    different dir (which is true in cc build)
         final File outDir = mOut == null 
               ? new File(mBaseDir , "build/report-" + mName) : mOut;
         FileUtils.mkdirs(outDir);
         argList.add("-outDir");
         argList.add(outDir.getCanonicalPath());

         argList.add("-report");
         final File reportFile 
             = new File(outDir, "jcoderz-merged-report.xml");
         argList.add(reportFile.getCanonicalPath());

         // FIXME: . is not correct if the build process was started from a
         //    different dir (which is true in cc build)
         argList.add("-projectHome");
         argList.add(mBaseDir.getCanonicalPath());

         argList.add("-projectName");
         argList.add(mName);
         
         if (mPackageBase != null)
         {
           argList.add("-packageBase");
           argList.add(mPackageBase);
         }

         if (mSourceEncoding != null)
         {
             argList.add("-sourceEncoding");
             argList.add(mSourceEncoding.name());
         }
         logger.fine("Calling Java2Html with args '" + argList + "'");
         final String[] java2HtmlArgs
            = (String[]) argList.toArray(new String[argList.size()]);
         Java2Html.main(java2HtmlArgs);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         logger.severe("Could not render HTML!");
      }
   }
}
