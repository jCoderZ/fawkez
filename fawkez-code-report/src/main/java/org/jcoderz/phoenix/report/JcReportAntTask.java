/*
 * $Id: JcReportAntTask.java 1466 2009-05-10 18:37:30Z amandel $
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Environment.Variable;
import org.jcoderz.commons.taskdefs.AntTaskUtil;
import org.jcoderz.commons.types.Date;
import org.jcoderz.commons.util.ArraysUtil;
import org.jcoderz.commons.util.FileUtils;
import org.jcoderz.commons.util.StringUtil;

/**
 * This is the Ant task for the Jcoderz Report.
 * This task forks all processing steps as separate processes
 * so that memory for each process can be controlled separately.
 *
 * TODO: Why are the inner classes static + take a JcReportAntTask?
 *
 * @author Michael Rumpf
 */
public class JcReportAntTask
   extends Task
{
   private static final int DEFAULT_MAX_HEAP = 256;
   private static final Date CREATION_TIMESTAMP = Date.now();
   private static final int DEFAULT_CPUS = 2;

   private NestedReportsElement mReports = null;
   private NestedMappingsElement mMappings = null;
   private NestedToolsElement mTools = null;
   private final NestedFiltersElement mFilterElements
       = new NestedFiltersElement();
   private NestedLogfilesElement mLogfilesElements
       = new NestedLogfilesElement();

   private String mName = null;
   private File mDest = null;
   private File mOldReportFile = null;
   private String mWikiBase = null;
   private String mWebRcsBase = null;
   private String mWebRcsSuffix = null;
   private String mPackageBase = null;
   private String mProjectBase = null;
   private String mStylesheet = null;
   private File mTempfolder = null;
   private int mMaxHeap = DEFAULT_MAX_HEAP;
   private int mCpus = DEFAULT_CPUS;
   private Charset mSourceEncoding = null;
   private boolean mDebug = false;

   private File mWorkingDir = null;

   /** The global Java Commandline instance */
   private final CommandlineJava mCommandline = new CommandlineJava();
   private int mMaxInner;

   /**
    * @return the number of cpus to put load on.
    */
    public int getCpus ()
    {
        return mCpus;
    }

    /**
     * @param cpus the cpus to set
     */
    public void setCpus (int cpus)
    {
        mCpus = cpus;
    }

    /**
     * @return the sourceEncoding
     */
    public String getEncoding ()
    {
        return mSourceEncoding.name();
    }

    /**
     * @param encoding the sourceEncoding to set
     */
    public void setEncoding (String encoding)
    {
        mSourceEncoding = Charset.forName(encoding);
    }

    /**
    * Returns the working directory.
    *
    * @return the working directory.
    */
   public File getWorkingDir ()
   {
      return mWorkingDir;
   }

   /**
    * Sets the maximum heap value.
    * If not defined in the Ant task the default value of 512MB will be used.
    *
    * @param maxheap the max heap value.
    */
   public void setMaxHeap (String maxheap)
   {
      mMaxHeap = Integer.parseInt(maxheap);
   }


   /**
    * Sets the name of the report.
    *
    * @param name the report name.
    */
   public void setName (String name)
   {
      mName = name;
   }


   /**
    * Sets the destination of the report.
    *
    * @param dest the report destination.
    */
   public void setDest (String dest)
   {
      mDest = new File(dest);
      AntTaskUtil.ensureDirectory(mDest);
   }


   public void setPackageBase (String packageBase)
   {
      mPackageBase = packageBase;
   }


   public void setProjectBase (String projectBase)
   {
      mProjectBase = projectBase;
   }


   public void setWebRcsBase (String webRcsBase)
   {
      mWebRcsBase = webRcsBase;
   }


   public void setWebRcsSuffix (String webRcsSuffix)
   {
      mWebRcsSuffix = webRcsSuffix;
   }


   public void setWikiBase (String wikiBase)
   {
      mWikiBase = wikiBase;
   }

   public void setOldReportFile (String oldReportFile)
   {
      mOldReportFile = new File(oldReportFile);
   }

   /**
    * Sets the stylesheet to be used for the report.
    *
    * @param stylesheet the report stylesheet.
    */
   public void setStylesheet (String stylesheet)
   {
      mStylesheet = stylesheet;
   }


   /**
    * Sets the temporary folder.
    *
    * @param tempfolder the temporary folder.
    */
   public void setTempfolder (String tempfolder)
   {
      mTempfolder = new File(tempfolder);
   }


   /**
    * Sets the debug parameter.
    *
    * @param debug the debug parameter.
    */
   public void setDebug (Boolean debug)
   {
      mDebug = debug.booleanValue();
   }


   public Path createClasspath ()
   {
      return mCommandline.createClasspath(getProject()).createPath();
   }


   /**
    * This method is called by Ant for executing this task.
    *
    * @throws BuildException whenever a problem occurs.
    */
   public void execute ()
      throws BuildException
   {
      try
      {
         // Always show this line
         super.log("Executing JcReportAntTask...");

         checkParameters();

         // Delete the dest folder in case it exists so that we don't mix
         // already deleted files. And create a fresh folder afterwards again.
         if (mDest.exists())
         {
            FileUtils.rmdir(mDest);
            AntTaskUtil.ensureDirectory(mDest);
         }

         // Now start processing the different reports
         log("Processing reports...");

         final int max
             = Math.min(mCpus + 1 , mReports.getReports().size());
         mMaxInner = 1 + (mCpus / max);
         super.log("Decided to have " + max + " report types with "
             + mMaxInner + " reports each in parallel.");
         final CompletionService<File> service
             = new ExecutorCompletionService<File>(
                 new ThreadPoolExecutor(max, max, 0, TimeUnit.SECONDS,
                     new ArrayBlockingQueue<Runnable>(
                         mReports.getReports().size())));

         final List<Future<File>> jcReports = new ArrayList<Future<File>>();
         final Iterator<NestedReportElement> iterReport
             = mReports.getReports().iterator();
         while (iterReport.hasNext())
         {
            final NestedReportElement nre = iterReport.next();
            log("Processing report '" + nre.getName() + "' ...");
            final Future<File> jcReport  = service.submit(
                new Callable<File> ()
                {
                    public File call ()
                        throws InterruptedException, ExecutionException,
                            IOException, JAXBException, TransformerException
                    {
                        final File result;
                        log("Starting: " + nre.getName()
                            + " for " + nre.getSourcePath() + ".");
                        result = performNestedReport(nre);
                        log("Done: " + nre.getName()
                            + " got " + nre.getSourcePath() + ".");
                        return result;
                    }
                }
            );
            jcReports.add(jcReport);
         }

         final File jcReport = executeReportMerger(jcReports);
         executeJava2Html(jcReport);
      }
      catch (Exception ex)
      {
         log(ex.toString(), ex, Project.MSG_ERR); // CHECKME!
         throw new BuildException("An unexpected exception occured!", ex);
      }
   }


    private File performNestedReport (final NestedReportElement nre)
        throws InterruptedException, ExecutionException, IOException,
            JAXBException, TransformerException
    {
        // Create a temp folder for this report
        final File reportTmpDir = new File(mWorkingDir, nre.getName());
        AntTaskUtil.ensureDirectory(reportTmpDir);
        final File srcDir = new File(nre.getSourcePath());
        final File clsDir = new File(nre.getClassPath());

        final CompletionService<File> service
            = new ExecutorCompletionService<File>(
                new ThreadPoolExecutor(mMaxInner, mMaxInner, 0,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(5))); // 2 max threads?


        File pmdXml = null;
        final Future<File> pmdResult
            = submit(mTools.getPmd(), reportTmpDir,
                srcDir, clsDir, service);

        File checkstyleXml = null;
        final Future<File> checkstyleResult
            = submit(mTools.getCheckstyle(), reportTmpDir,
                srcDir, clsDir, service);

        File findbugsXml = null;
        final Future<File> findbugsResult
            = submit(mTools.getFindbugs(), reportTmpDir,
                srcDir, clsDir, service);

        File cpdXml = null;
        final Future<File> cpdResult
            = submit(mTools.getCpd(), reportTmpDir,
                srcDir, clsDir, service);

        File coberturaXml = null;
        final Future<File> coberturaResult
            = submit(mTools.getCobertura(), reportTmpDir,
                srcDir, clsDir, service);

        // now get the results....
        if (checkstyleResult != null)
        {   // EXCEPTION?
            checkstyleXml = checkstyleResult.get();
        }
        if (findbugsResult != null)
        {   // EXCEPTION?
            findbugsXml = findbugsResult.get();
        }
        if (pmdResult != null)
        {   // EXCEPTION?
            pmdXml = pmdResult.get();
        }
        if (cpdResult != null)
        {   // EXCEPTION?
            cpdXml = cpdResult.get();
        }
        if (coberturaResult != null)
        {   // EXCEPTION?
            coberturaXml = coberturaResult.get();
        }

        final File emmaFile;
        if (mTools.getEmma() != null)
        {   // EXCEPTION?
            emmaFile = new File(mTools.getEmma().mDatafile);
        }
        else
        {
            emmaFile = null;
        }

        // Merge the different reports into one jcoderz-report.xml
        // This must be done on a level by level basis
        return executeReportNormalizer(srcDir, reportTmpDir,
              nre.getLevel(), checkstyleXml, findbugsXml, pmdXml,
              cpdXml, coberturaXml, emmaFile);
    }


    private Future<File> submit (final NestedToolElement nce,
        final File reportTmpDir, final File srcDir, final File clsDir,
        CompletionService<File> service)
    {
        Future<File> result = null;
        if (nce != null)
        {
           result = service.submit(
               new Callable<File> ()
               {
                   public File call ()
                   {
                       final File result;
                       log("Starting: " + nce.toString() + " for " + srcDir
                           + ".");
                       result = nce.execute(reportTmpDir, srcDir, clsDir);
                       log("Done: " + nce.toString() + " got " + result + ".");
                       return result;
                   }
               }
           );
        }
        return result;
    }


   private void checkParameters ()
   {
       if (mTempfolder == null)
       {
           throw new BuildException("You must specify a temporary folder!",
               getLocation());
       }
       AntTaskUtil.ensureDirectory(mTempfolder);
       mWorkingDir = new File(mTempfolder, mName);
       AntTaskUtil.ensureDirectory(mWorkingDir);

       // Check that the names of the reports differ!
       final Set<String> reportNames = new HashSet<String>();
       final Iterator<NestedReportElement> iterReport
           = mReports.getReports().iterator();
       while (iterReport.hasNext())
       {
           final NestedReportElement nre = iterReport.next();
           reportNames.add(nre.getName());
       }
       if (reportNames.size() != mReports.getReports().size())
       {
           throw new BuildException("Reports must not have the same names!",
               getLocation());
       }
   }


   /**
    * Executes the report normalizer in a separate process.
    *
    * The following command line parameters are supported:
    * <ul>
    *   <li><code>-cobertura coberturareport.xml</code>
    *   (http://cobertura.sf.net)</li>
    *   <li><code>-checkstyle checkstylereport.xml</code>
    *   (http://checkstyle.sf.net)</li>
    *   <li><code>-findbugs findbugsreport.xml</code>
    *   (http://findbugs.sf.net)</li>
    *   <li><code>-pmd pmdreport.xml</code>
    *   (http://pmd.sf.net)</li>
    *   <li><code>-cpd cpdreport.xml</code>
    *   (http://pmd.sf.net)</li>
    *   <li><code>-projectHome</code></li>
    *   <li><code>-srcDir</code></li>
    *   <li><code>-projectName</code></li>
    *   <li><code>-level PROD|TEST|MISC</code> The weight level</li>
    *   <li><code>-out</code></li>
    * </ul>
    */
   private File executeReportNormalizer (File srcDir, File reportDir,
            ReportLevel level, File checkstyleXml,
            File findbugsXml, File pmdXml, File cpdXml, File coberturaXml,
            File emmaSummary)
       throws IOException, JAXBException, TransformerException
   {
      // INLINE failed, got java.lang.OutOfMemoryError: PermGen space
      log("Creating report normalizer command line...");
      final CommandlineJava cmd = createCommandlineJava(mCommandline, mMaxHeap);

      cmd.setClassname("org.jcoderz.phoenix.report.ReportNormalizer");

      cmd.createArgument().setValue("-srcDir");
      cmd.createArgument().setFile(srcDir);

      cmd.createArgument().setValue("-level");
      cmd.createArgument().setValue(level.toString());

      if (mDebug)
      {
         cmd.createArgument().setValue("-loglevel");
         cmd.createArgument().setValue("FINEST");
      }

      cmd.createArgument().setValue("-projectName");
      cmd.createArgument().setValue(mName);

      cmd.createArgument().setValue("-out");
      cmd.createArgument().setFile(reportDir);

      if (checkstyleXml != null)
      {
         cmd.createArgument().setValue("-checkstyle");
         cmd.createArgument().setFile(checkstyleXml);
      }

      if (findbugsXml != null)
      {
         cmd.createArgument().setValue("-findbugs");
         cmd.createArgument().setFile(findbugsXml);
      }

      if (pmdXml != null)
      {
         cmd.createArgument().setValue("-pmd");
         cmd.createArgument().setFile(pmdXml);
      }

      if (cpdXml != null)
      {
         cmd.createArgument().setValue("-cpd");
         cmd.createArgument().setFile(cpdXml);
      }

      if (coberturaXml != null)
      {
         cmd.createArgument().setValue("-cobertura");
         cmd.createArgument().setFile(coberturaXml);
      }

      if (emmaSummary != null)
      {
         cmd.createArgument().setValue("-emma");
         cmd.createArgument().setFile(emmaSummary);
      }

      for (NestedLogfileElement nge : mLogfilesElements.mGenericReaders)
      {
          cmd.createArgument().setValue("-generic");
          cmd.createArgument().setFile(nge.getFile());
          cmd.createArgument().setValue(nge.getType());
      }

      forkToolProcess(this, cmd, new LogStreamHandler(this, Project.MSG_INFO,
         Project.MSG_WARN));

      return new File(reportDir, ReportNormalizer.JCODERZ_REPORT_XML);
   }


   private File executeReportMerger (List<Future<File>> jcReports)
       throws InterruptedException, ExecutionException, IOException,
           JAXBException, TransformerException
   {
      log("Preparing report merger...");
      final ReportMerger merger = new ReportMerger();
      if (mDebug)
      {
          merger.setLogLevel(Level.ALL);
      }
      merger.setOutFile(mWorkingDir);
      final Iterator<Future<File>> jcReportIter = jcReports.iterator();
      while (jcReportIter.hasNext())
      {
         final File jcReport = jcReportIter.next().get();
         merger.addReport(jcReport);
      }
      final Iterator<NestedFilterElement> filterIter
          = mFilterElements.getFilters().iterator();
      while (filterIter.hasNext())
      {
         final NestedFilterElement filterElement = filterIter.next();
         merger.addFilter(filterElement.getFile());
      }
      merger.merge();
      merger.filter();
      if (mOldReportFile != null)
      {
          merger.setOldFile(mOldReportFile);
          merger.flagNewFindings();
      }

      final File outFile = new File(mWorkingDir,
         ReportNormalizer.JCODERZ_REPORT_XML);
      try
      {
         FileUtils.copy(outFile, mDest);
      }
      catch (IOException e)
      {
         throw new BuildException("Could not copy '" + outFile
               + "' to destination folder '" + mDest + "'!", e, getLocation());
      }

      return outFile;
   }


   /**
    * Executes the Java2Html tool in a separate process.
    *
    * The following command line parameters are supported:
    * <pre>
    * -outDir
    * -report
    * -projectHome
    * -projectName
    * -cvsBase
    * -timestamp
    * -wikiBase
    * -reportStyle
    * -packageBase
    * </pre>
    */
   private void executeJava2Html (File jcReport)
   {
      log("Creating java2html command line...");

      final CommandlineJava cmd = createCommandlineJava(mCommandline, mMaxHeap);

      cmd.setClassname("org.jcoderz.phoenix.report.Java2Html");

      // let it run in headless mode to avoid exceptions because of a missing X
      cmd.createVmArgument().setValue("-Djava.awt.headless=true");

      cmd.createArgument().setValue("-outDir");
      cmd.createArgument().setFile(mDest);

      cmd.createArgument().setValue("-report");
      cmd.createArgument().setFile(jcReport);

      cmd.createArgument().setValue("-timestamp");
      cmd.createArgument().setValue(CREATION_TIMESTAMP.toString(
            "yyyyMMddHHmmss"));

      if (mProjectBase != null)
      {
         cmd.createArgument().setValue("-projectHome");
         cmd.createArgument().setValue(mProjectBase);
      }

      if (mStylesheet != null)
      {
         cmd.createArgument().setValue("-reportStyle");
         cmd.createArgument().setValue(mStylesheet);
      }

      cmd.createArgument().setValue("-projectName");
      cmd.createArgument().setValue(mName);

      cmd.createArgument().setValue("-cvsBase");
      cmd.createArgument().setValue(mWebRcsBase);

      if (!StringUtil.isNullOrBlank(mWebRcsSuffix))
      {
          cmd.createArgument().setValue("-cvsSuffix");
          cmd.createArgument().setValue(mWebRcsSuffix);
      }

      cmd.createArgument().setValue("-wikiBase");
      cmd.createArgument().setValue(mWikiBase);

      if (mPackageBase != null)
      {
         cmd.createArgument().setValue("-packageBase");
         cmd.createArgument().setValue(mPackageBase);
      }

      if (mSourceEncoding != null)
      {
          cmd.createArgument().setValue("-sourceEncoding");
          cmd.createArgument().setValue(getEncoding());
      }
      if (mDebug)
      {
         cmd.createArgument().setValue("-loglevel");
         cmd.createArgument().setValue("FINEST");
      }

      forkToolProcess(this, cmd, new LogStreamHandler(this, Project.MSG_INFO,
         Project.MSG_WARN));
   }


   //
   // Reports section
   //


   /**
    * This method is called by Ant to create an instance of the
    * NestedReportsElement class when the 'reports' tag is read.
    *
    * @return the new instance of type NestedReportsElement.
    */
   public NestedReportsElement createReports ()
   {
      mReports = new NestedReportsElement(this);
      return mReports;
   }


   public static class NestedReportsElement
   {
      private List<NestedReportElement> mReports
          = new ArrayList<NestedReportElement>();
      private JcReportAntTask mTask;

      public NestedReportsElement (JcReportAntTask task)
      {
         mTask = task;
      }


      public NestedReportElement createReport ()
      {
         mTask.log("Creating report element...");
         final NestedReportElement nre = new NestedReportElement();
         mReports.add(nre);
         return nre;
      }


      public List<NestedReportElement> getReports ()
      {
         return Collections.unmodifiableList(mReports);
      }
   }


   /**
    * This class represents a &lt;report&gt; tag in an Ant
    * <code>build.xml</code> file.
    *
    * @author Michael Rumpf
    */
   public static class NestedReportElement
   {
      private String mName;
      private ReportLevel mLevel;
      private String mSourcePath;
      private String mClassPath;

      public String getName ()
      {
         return mName;
      }


      public void setName (String name)
      {
         mName = name;
      }

      public ReportLevel getLevel ()
      {
         return mLevel;
      }


      public void setLevel (String level)
      {
         mLevel = ReportLevel.fromString(level);
      }


      public String getClassPath ()
      {
         return mClassPath;
      }


      public void setClassPath (String classPath)
      {
         mClassPath = classPath;
      }


      public String getSourcePath ()
      {
         return mSourcePath;
      }


      public void setSourcePath (String sourcePath)
      {
         mSourcePath = sourcePath;
      }
   }


   //
   // Mappings section
   //


   /**
    * This method is called by Ant to create an instance of the
    * NestedMappingsElement class when the 'mappings' tag is read.
    *
    * @return the new instance of type NestedMappingsElement.
    */
   public NestedMappingsElement createMappings ()
   {
      mMappings = new NestedMappingsElement(this);
      return mMappings;
   }


   public static class NestedMappingsElement
   {
      private List<NestedMappingElement> mMappings
          = new ArrayList<NestedMappingElement>();
      private JcReportAntTask mTask;

      public NestedMappingsElement (JcReportAntTask task)
      {
         mTask = task;
      }

      public NestedMappingElement createWebRcs ()
      {
         mTask.log("Creating mapping element...");
         final NestedMappingElement nme = new NestedMappingElement();
         mMappings.add(nme);
         return nme;
      }

      public List<NestedMappingElement> getMappings ()
      {
         return mMappings;
      }
   }


   public static class NestedMappingElement
   {
      private String mPattern;
      private String mUrl;
      private String mSuffix;


      public String getPattern ()
      {
         return mPattern;
      }


      public void setPattern (String pattern)
      {
         mPattern = pattern;
      }


      public String getSuffix ()
      {
         return mSuffix;
      }


      public void setSuffix (String suffix)
      {
         mSuffix = suffix;
      }


      public String getUrl ()
      {
         return mUrl;
      }


      public void setUrl (String url)
      {
         mUrl = url;
      }
   }


   //
   // Tools section
   //


   /**
    * This method is called by Ant to create an instance of the
    * NestedToolsElement class when the 'tools' tag is read.
    *
    * @return the new instance of type NestedToolsElement.
    */
   public NestedToolsElement createTools ()
   {
      mTools = new NestedToolsElement(this);
      return mTools;
   }


   public static class NestedToolsElement
   {
      private JcReportAntTask mTask;
      private NestedPmdElement mPmd = null;
      private NestedCpdElement mCpd = null;
      private NestedFindbugsElement mFindbugs = null;
      private NestedCheckstyleElement mCheckstyle = null;
      private NestedCoberturaElement mCobertura = null;
      private NestedEmmaElement mEmma = null;

      public NestedToolsElement (JcReportAntTask task)
      {
         mTask = task;
      }


      public NestedPmdElement createPmd ()
      {
         mTask.log("Creating Pmd element...");
         mPmd = new NestedPmdElement(mTask);
         return mPmd;
      }


      public NestedPmdElement getPmd ()
      {
         return mPmd;
      }


      public NestedCpdElement createCpd ()
      {
         mTask.log("Creating Cpd element...");
         mCpd = new NestedCpdElement(mTask);
         return mCpd;
      }


      public NestedCpdElement getCpd ()
      {
         return mCpd;
      }


      public NestedFindbugsElement createFindbugs ()
      {
         mTask.log("Creating Findbugs element...");
         mFindbugs = new NestedFindbugsElement(mTask);
         return mFindbugs;
      }


      public NestedFindbugsElement getFindbugs ()
      {
         return mFindbugs;
      }


      public NestedCheckstyleElement createCheckstyle ()
      {
         mTask.log("Creating Checkstyle element...");
         mCheckstyle = new NestedCheckstyleElement(mTask);
         return mCheckstyle;
      }


      public NestedCheckstyleElement getCheckstyle ()
      {
         return mCheckstyle;
      }


      public NestedCoberturaElement createCobertura ()
      {
         mTask.log("Creating Cobertura element...");
         mCobertura = new NestedCoberturaElement(mTask);
         return mCobertura;
      }


      public NestedCoberturaElement getCobertura ()
      {
         return mCobertura;
      }

      public NestedEmmaElement createEmma ()
      {
         mTask.log("Creating Emma element...");
         mEmma = new NestedEmmaElement(mTask);
         return mEmma;
      }

      public NestedEmmaElement getEmma ()
      {
         return mEmma;
      }
   }


   /**
    * This is the base class for all tool elements.
    * It provides support for the maxheap attribute
    * and the nested classpath element.
    *
    * @author Michael Rumpf
    */
   public abstract static class NestedToolElement
   {
      protected JcReportAntTask mTask;
      protected Path mPath;
      protected int mMaxHeap;

      /** The global Java Commandline instance */
      protected final CommandlineJava mCommandline = new CommandlineJava();

      public NestedToolElement (JcReportAntTask task)
      {
          mTask = task;
          mMaxHeap = mTask.mMaxHeap;
      }

      /**
       * Sets the maximum heap value.
       * If not defined in the Ant task the default value of 512MB will be used.
       *
       * @param maxheap the max heap value.
       */
      public void setMaxheap (String maxheap)
      {
         mMaxHeap = Integer.parseInt(maxheap);
      }


      /**
       * Creates a classpath for the tool element.
       *
       * @return the created classpath.
       */
      public Path createClasspath ()
      {
         mPath = mCommandline.createClasspath(mTask.getProject()).createPath();
         return mPath;
      }

      public abstract File execute (File reportDir, File srcDir, File clsDir);
   }


   public static class NestedPmdElement
         extends NestedToolElement
   {
      private String mConfig;
      private String mTargetjdk;
      private String mEncoding;

      public NestedPmdElement (JcReportAntTask task)
      {
         super(task);
         mCommandline.setClassname("net.sourceforge.pmd.PMD");
      }


      public void setConfig (String config)
      {
         mConfig = config;
      }


      public void setTargetjdk (String targetjdk)
      {
         mTargetjdk = targetjdk;
      }


      public void setEncoding (String encoding)
      {
         mEncoding = encoding;
      }


      public File execute (File reportDir, File srcDir, File clsDir)
      {
         mTask.log("Creating pmd command line...");
         final CommandlineJava cmd
             = createCommandlineJava(mCommandline, mMaxHeap);

         cmd.createArgument().setFile(srcDir);

         // We always write pmd reports in XML format
         cmd.createArgument().setValue("xml");

         if (mConfig != null)
         {
            cmd.createArgument().setFile(new File(mConfig));
         }

         if (mEncoding != null)
         {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(mEncoding);
         }
         else if (mTask.getEncoding() != null)
         {
             cmd.createArgument().setValue("-encoding");
             cmd.createArgument().setValue(mTask.getEncoding());
         }

         if (mTargetjdk != null)
         {
            cmd.createArgument().setValue("-targetjdk");
            cmd.createArgument().setValue(mTargetjdk);
         }

         final File outFile = new File(reportDir, "pmd.xml");
         FileOutputStream fos = null;
         try
         {
            fos = new FileOutputStream(outFile);
         }
         catch (IOException e)
         {
            throw new BuildException("Could not find output file: "
                  + outFile.getAbsolutePath(), e, mTask.getLocation());
         }

         forkToolProcess(mTask, cmd, new PumpStreamHandler(fos, System.err));

         return outFile;
      }
   }


   public static class NestedCpdElement
         extends NestedToolElement
   {
      private static final int DEFAULT_MINIMUM_TOKENS = 100;
      private int mMinimumtokens = DEFAULT_MINIMUM_TOKENS;
      private String mEncoding = null;
      private String mOutputEncoding = "UTF-8";

      public NestedCpdElement (JcReportAntTask task)
      {
          super(task);
         mCommandline.setClassname("net.sourceforge.pmd.cpd.CPD");
      }

      public void setMinimumtokens (String minimumtokens)
      {
         mMinimumtokens = Integer.parseInt(minimumtokens);
      }

      public void setEncoding (String encoding)
      {
         mEncoding = encoding;
      }

      public void setOutputEncoding (String encoding)
      {
         mOutputEncoding = encoding;
      }

      /**
       * Executes the cpd tool in a separate process.
       *
       * The following command line switches are supported by this method:
       * <pre>
       * CPD --minimum-tokens xxx --files xxx
       * </pre>
       */
      public File execute (File reportDir, File srcDir, File clsDir)
      {
         mTask.log("Creating cpd command line...");
         final CommandlineJava cmd
             = createCommandlineJava(mCommandline, mMaxHeap);

         final Variable var = new Variable();
         var.setKey("file.encoding");
         var.setValue(mOutputEncoding);
         cmd.getSystemProperties().addVariable(var);

         cmd.createArgument().setFile(srcDir);

         // We always write pmd reports in XML format
         cmd.createArgument().setValue("--format");
         cmd.createArgument().setValue("net.sourceforge.pmd.cpd.XMLRenderer");

         if (mEncoding != null)
         {
             cmd.createArgument().setValue("--encoding");
             cmd.createArgument().setValue(mEncoding);
         }
         else if (mTask.getEncoding() != null)
         {
             cmd.createArgument().setValue("--encoding");
             cmd.createArgument().setValue(mTask.getEncoding());
         }


         cmd.createArgument().setValue("--language");
         cmd.createArgument().setValue("java");

         cmd.createArgument().setValue("--files");
         cmd.createArgument().setFile(srcDir);

         cmd.createArgument().setValue("--minimum-tokens");
         cmd.createArgument().setValue(String.valueOf(mMinimumtokens));

         final File outFile = new File(reportDir, "cpd.xml");
         FileOutputStream fos = null;
         try
         {
            fos = new FileOutputStream(outFile);
         }
         catch (IOException e)
         {
            throw new BuildException("Could not find output file: "
                  + outFile.getAbsolutePath(), e, mTask.getLocation());
         }

         forkToolProcess(mTask, cmd, new PumpStreamHandler(fos, System.err));

         return outFile;
      }
   }


   public static class NestedFindbugsElement
         extends NestedToolElement
   {
      private String mConfig;
      private String mWarninglevel = "medium";
      private String mEffort = "default";
      private String mOmitVisitors = "";
      private Path mAuxPath;
      private boolean mFindBugsDebug = false;
      /**
       * Path of the findbugs plugin jar files. Must at least contain
       * the coreplugin.jar
       */
      private Path mPluginList;

      public NestedFindbugsElement (JcReportAntTask task)
      {
         super(task);
         mCommandline.setClassname("edu.umd.cs.findbugs.FindBugs2");
      }


      /**
       * Sets the debug parameter.
       * @param debug the debug parameter.
       */
      public void setDebug (Boolean debug)
      {
          mFindBugsDebug = debug.booleanValue();
      }

      public void setOmitVisitors (String omitVisitors)
      {
         mOmitVisitors = omitVisitors;
      }

      public void setConfig (String config)
      {
         mConfig = config;
      }

      public void setEffort (String effort)
      {
         if ("min".equals(effort) || "default".equals(effort)
               || "max".equals(effort))
         {
            mEffort = effort;
         }
         else
         {
            mTask.log("Invalid effort value '" + effort + "!'");
         }
      }


      public void setWarninglevel (String warninglevel)
      {
         if ("experimental".equals(warninglevel) || "low".equals(warninglevel)
               || "medium".equals(warninglevel) || "high".equals(warninglevel))
         {
            mWarninglevel = warninglevel;
         }
         else
         {
            mTask.log("Invalid warninglevel value '" + warninglevel + "!'");
         }
      }


      /**
       * The findbugs tool needs an list of jar files where all the plugins are
       * defined in. Minimum plugin list contains the coreplugin.
       *
       * @return the created plugin list path.
       */
      public Path createPluginlist ()
      {
         mPluginList = new Path(mTask.getProject());
         return mPluginList;
      }

      /**
       * The findbugs tool needs an auxiliary classpath with all the classes,
       * referenced from the project class files.
       *
       * @return the created auxiliary classpath.
       */
      public Path createAuxclasspath ()
      {
         mAuxPath = new Path(mTask.getProject());
         return mAuxPath;
      }


      /**
       * Executes the findbugs tool in a separate process.
       * <pre>
       * maxheap:
       *   -maxHeap size    Maximum Java heap size in megabytes (default=256)
       *
       * effort:
       *   -effort[:min|default|max] set analysis effort level
       *
       *  warninglevel:
       *     -experimental   report all warnings including experimental bug
       *                     patterns
       *     -low            report all warnings
       *     -medium         report only medium and high priority warnings
       *                     [default]
       *     -high           report only high priority warnings
       *
       * config:
       *   -exclude &lt;filter file>     include only bugs matching given filter
       *
       * internally:
       *   -outputFile &lt;filename>       Save output in named file
       *   -xml[:withMessages]          XML output (optionally with messages)
       *
       * auxclasspath:
       *   -auxclasspath &lt;classpath>    set aux classpath for analysis
       *
       * report: sourcepath
       *   -sourcepath &lt;source path>    set source path for analyzed classes
       * </pre>
       * The target assumes that all libs needed by findbugs are on the
       * classpath and the plugins are set via pluginlist element.
       *
       */
      public File execute (File reportDir, File srcDir, File clsDir)
      {
         mTask.log("Creating findbugs command line...");
         final CommandlineJava cmd
             = createCommandlineJava(mCommandline, mMaxHeap);

          if (mFindBugsDebug)
          {
              cmd.createVmArgument().setValue("-Dfindbugs.debug=true");
          }

         if (mPluginList != null)
         {
             cmd.createArgument().setValue("-pluginList");
             cmd.createArgument().setPath(mPluginList);
         }

         if (!StringUtil.isEmptyOrNull(mOmitVisitors))
         {
             cmd.createArgument().setValue("-omitVisitors");
             cmd.createArgument().setValue(mOmitVisitors);
         }


         final File outFile = new File(reportDir, "findbugs.xml");
         cmd.createArgument().setValue("-output");
         cmd.createArgument().setFile(outFile);

         cmd.createArgument().setValue("-sourcepath");
         cmd.createArgument().setFile(srcDir);

         // We always write findbugs reports in XML format
         cmd.createArgument().setValue("-xml:withMessages");

         if (mConfig != null)
         {
            cmd.createArgument().setValue("-exclude");
            cmd.createArgument().setFile(new File(mConfig));
         }

         if (mAuxPath != null)
         {
            cmd.createArgument().setValue("-auxclasspath");
            cmd.createArgument().setPath(mAuxPath);
         }

         cmd.createArgument().setValue("-" + mWarninglevel);

         cmd.createArgument().setValue("-effort:" + mEffort);

         cmd.createArgument().setFile(clsDir);

         // TODO: use PumpStreamHandler to suppress info messages from FindBugs
         forkToolProcess(mTask, cmd, new LogStreamHandler(mTask,
            Project.MSG_INFO, Project.MSG_WARN));

         return outFile;
      }

   }


   public static class NestedCheckstyleElement
         extends NestedToolElement
   {
      private String mConfig;
      private String mProperties;

      public NestedCheckstyleElement (JcReportAntTask task)
      {
         super(task);
         mCommandline.setClassname("com.puppycrawl.tools.checkstyle.Main");
      }


      public void setConfig (String config)
      {
         mConfig = config;
      }


      public void setProperties (String properties)
      {
         mProperties = properties;
      }

      public String toString ()
      {
          return "Checkstyle";
      }

      public File execute (File reportDir, File srcDir, File clsPath)
      {
         mTask.log("Creating checkstyle command line...");
         final CommandlineJava cmd
             = createCommandlineJava(mCommandline, mMaxHeap);

         cmd.createArgument().setValue("-o");
         final File outFile = new File(reportDir, "checkstyle.xml");
         cmd.createArgument().setFile(outFile);

         if (mConfig == null)
         {
            throw new BuildException("The 'config' attribute is mandatory"
                  + " for the checkstyle task!", mTask.getLocation());
         }
         cmd.createArgument().setValue("-c");
         cmd.createArgument().setFile(new File(mConfig));

         // We always write checkstyle reports in XML format
         cmd.createArgument().setValue("-f");
         cmd.createArgument().setValue("xml");

         if (mProperties != null)
         {
            cmd.createArgument().setValue("-p");
            cmd.createArgument().setFile(new File(mProperties));
         }

         cmd.createArgument().setValue("-r");
         cmd.createArgument().setFile(srcDir);

         if (mTask.getEncoding() != null)
         {
             final Variable var = new Variable();
             var.setKey("file.encoding");
             var.setValue(mTask.getEncoding());
             cmd.getSystemProperties().addVariable(var);
         }

         forkToolProcess(mTask, cmd, new LogStreamHandler(mTask,
            Project.MSG_INFO, Project.MSG_WARN));

         return outFile;
      }
   }


   public static class NestedCoberturaElement
         extends NestedToolElement
   {
      private String mDatafile;

      public NestedCoberturaElement (JcReportAntTask task)
      {
         super(task);
         mCommandline.setClassname("net.sourceforge.cobertura.reporting.Main");
      }


      public void setDatafile (String datafile)
      {
         mDatafile = datafile;
      }


      /**
       * Executes the cobertura tool in a separate process.
       *
       * <pre>
       * [--datafile file]
       * [--destination dir]
       * source code directory [...]
       * </pre>
       */
      public File execute (File reportDir, File srcDir, File clsPath)
      {
         mTask.log("Creating cobertura command line...");
         final CommandlineJava cmd
             = createCommandlineJava(mCommandline, mMaxHeap);

         File dataFile = null;
         if (mDatafile == null)
         {
            throw new BuildException("The datafile attribute is mandatory!",
               mTask.getLocation());
         }
         dataFile = new File(mDatafile);
         if (!dataFile.exists())
         {
             throw new BuildException(
                 "The datafile '" + mDatafile + "' was not found!",
                 mTask.getLocation());
         }

         cmd.createArgument().setValue("--destination");
         final File outFile = new File(reportDir, "coverage.xml");
         cmd.createArgument().setFile(reportDir);

         // We always write checkstyle reports in XML format
         cmd.createArgument().setValue("--format");
         cmd.createArgument().setValue("xml");

         cmd.createArgument().setValue("--datafile");
         cmd.createArgument().setFile(dataFile);

         cmd.createArgument().setFile(srcDir);

         forkToolProcess(mTask, cmd, new LogStreamHandler(mTask,
            Project.MSG_INFO, Project.MSG_WARN));

         return outFile;
      }
   }

   public static class NestedEmmaElement
        extends NestedToolElement
    {
        private String mDatafile;

        public NestedEmmaElement (JcReportAntTask task)
        {
            super(task);
        }

        public void setDatafile (String datafile)
        {
            mDatafile = datafile;
        }

        /**
         * Nothing to be done for emma.
         */
        public File execute (File reportDir, File srcDir, File clsPath)
        {
            return new File(mDatafile);
        }
    }

   //
   // Generic input
   //
   /**
    * This method is called by Ant to create an instance of the
    * NestedLogfilesElement class when the 'logfiles' tag is read.
    *
    * @return the new instance of type NestedFiltersElement.
    */
   public NestedLogfilesElement createLogfiles ()
   {
      return mLogfilesElements;
   }


   public static class NestedLogfilesElement
   {
      private List<NestedLogfileElement> mGenericReaders
          = new ArrayList<NestedLogfileElement>();


      public void addLogfile (NestedLogfileElement nge)
      {
          mGenericReaders.add(nge);
      }


      public List<NestedLogfileElement> getLogfile ()
      {
         return mGenericReaders;
      }
   }

   public static class NestedLogfileElement
   {
       private String mType;
       private File mFile;
       
       public File getFile ()
       {
            return mFile;
       }
    
        public String getType ()
        {
            return mType;
        }

       public void setFile (File file)
       {
           mFile = file;
       }
    
       public void setType (String type)
       {
           mType = type;
       }
    }
   //
   // Filters section
   //


   /**
    * This method is called by Ant to create an instance of the
    * NestedFiltersElement class when the 'filters' tag is read.
    *
    * @return the new instance of type NestedFiltersElement.
    */
   public NestedFiltersElement createFilters ()
   {
      return mFilterElements;
   }


   public static class NestedFiltersElement
   {
      private List<NestedFilterElement> mFilters
          = new ArrayList<NestedFilterElement>();


      public void addFilter (NestedFilterElement nfe)
      {
         mFilters.add(nfe);
      }


      public List<NestedFilterElement> getFilters ()
      {
         return mFilters;
      }
   }


   public static class NestedFilterElement
   {
      private File mFile;

      public File getFile ()
      {
         return mFile;
      }


      public void setFile (String file)
      {
         mFile = new File(file);
      }
   }


   //
   // Helper methods
   //


   /**
    * Creates a copy of the global command line instance
    * and sets the maximum heap vm parameter.
    *
    * @param cmdline the global command line instance.
    * @param maxHeap the maximum heap size for the process.
    * @return a copy of the global command line instance.
    */
   private static CommandlineJava createCommandlineJava (
            CommandlineJava cmdline, int maxHeap)
   {
      final CommandlineJava cmd;
      try
      {
          cmd = (CommandlineJava) cmdline.clone();
      }
      catch (CloneNotSupportedException unexpected)
      {
          throw new RuntimeException(
                  "Ups, CommandLineJava doesn't support the method clone()",
                  unexpected);
      }

      cmd.createVmArgument().setValue("-Xmx" + maxHeap + "m");
      return cmd;
   }


   /**
    * Forks the tool as external process.
    *
    * @param cmdline the command line.
    * @param psh the pump stream handler for redirecting the process streams.
    */
   private static void forkToolProcess (JcReportAntTask task,
            CommandlineJava cmdline, PumpStreamHandler psh)
   {
      final Execute execute = new Execute(psh);
      execute.setCommandline(cmdline.getCommandline());

      try
      {
         task.logCommandLine(cmdline.getCommandline());
         final int exitCode = execute.execute();
         if (exitCode != 0)
         {
            task.log("Process returned with exit code: " + exitCode);
         }
      }
      catch (IOException e)
      {
         throw new BuildException(
             "Process fork failed. " 
                 + ArraysUtil.toString(cmdline.getCommandline()), e,
            task.getLocation());
      }
   }


   /**
    * This is a special logging method to print the array of command
    * line parameters to the ant logging sub-system.
    *
    * @param cmdLine the command line parameter array.
    */
   private void logCommandLine (String[] cmdLine)
   {
      log("Command line: ");
      for (int i = 0; i < cmdLine.length; i++)
      {
         log("   " + cmdLine[i]);
      }
   }


   /**
    * Overwrites the method from the super class in order to
    * check for debug mode.
    *
    * @param msg the message to log.
    */
   public void log (String msg)
   {
      if (mDebug)
      {
         super.log(msg);
      }
   }
}
