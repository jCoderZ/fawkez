/*
 * $Id: ReportMerger.java 1533 2009-07-06 20:20:15Z amandel $
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.types.Date;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.FileUtils;
import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.commons.util.LoggingUtils;
import org.jcoderz.commons.util.ObjectUtil;
import org.jcoderz.commons.util.StringUtil;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;
import org.jcoderz.phoenix.report.jaxb.Report;

/**
 * Provides merging and filtering of various jcoderz-report.xml files.
 * It combines parts of the functions from ReportNormalizer and XmlMergeAntTask.
 *
 * @author Michael Rumpf
 */
public class ReportMerger
{

   /** The Constant CLASSNAME. */
   private static final String CLASSNAME = ReportNormalizer.class.getName();

   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   /** The length of an unique part of a c&p finding message. */
   private static final int CPD_UNIQUE_STRING_LENGTH 
       = "Copied and pasted code. 341 equal".length();
   
   /** The log level. */
   private Level mLogLevel;

   /** The out file. */
   private File mOutFile = null;

   /** The reports. */
   private final List<File> mReports = new ArrayList<File>();

   /** The filters. */
   private final List<File> mFilters = new ArrayList<File>();

   /** The old Report. */
   private File mOldReport;

   /** The old Report. */
   private final Date mReportDate = Date.now();

   /**
    * Merge input reports.
    * @throws JAXBException if a xml handling error occurs.
    * @throws FileNotFoundException in case of an IO issue.
    */
   public void merge ()
       throws JAXBException, FileNotFoundException
   {
     logger.log(Level.FINE, "Merging jcoderz-report.xml files...");
     // merge the reports
     final Report mergedReport = new ObjectFactory().createReport();
     for (final File reportFile : mReports)
     {
        logger.log(Level.FINE, "Report: " + reportFile);
        try
        {
           final Report report = (Report) new ObjectFactory()
                 .createUnmarshaller().unmarshal(reportFile);
           mergedReport.getFile().addAll(report.getFile());
        }
        catch (JAXBException ex)
        {
           // TODO: ADD ISSUE AS system ITEM TO THE REPORT
           ex.printStackTrace();
        }
     }
     writeResult(mergedReport, mOutFile);
   }


   /**
    * Filters the report XML file using the JDK XSL processor.
    * @throws TransformerException if the transformation fails.
    * @throws IOException if an io operation fails.
    */
   public void filter () throws TransformerException, IOException
   {
       logger.log(Level.FINE, "Filtering jcoderz-report.xml files...");
       for (final File filterFile : mFilters)
       {
           logger.log(Level.FINE, "Filter: " + filterFile);
           final TransformerFactory tFactory
               = TransformerFactory.newInstance();

           final Transformer transformer 
               = tFactory.newTransformer(new StreamSource(filterFile));

           final File tempOutputFile 
               = new File(mOutFile.getCanonicalPath() + ".tmp");
           FileUtils.createNewFile(tempOutputFile);

           final FileOutputStream out = new FileOutputStream(tempOutputFile);
           transformer.transform(new StreamSource(mOutFile),
               new StreamResult(out));
           IoUtil.close(out);
           FileUtils.copyFile(tempOutputFile, mOutFile);
           FileUtils.delete(tempOutputFile);
       }
   }

   /**
    * Searches for new findings based on the old jcReport and increases the
    * severity of such findings to NEW. 
    */
   public void flagNewFindings () 
   {
       logger.log(Level.FINE, "Searching for NEW findings...");
       try
       {
           final Report currentReport 
               = (Report) new ObjectFactory().createUnmarshaller().unmarshal(
                   mOutFile);
           final Report oldReport 
               = (Report) new ObjectFactory().createUnmarshaller().unmarshal(
                   mOldReport);
           for (org.jcoderz.phoenix.report.jaxb.File newFile 
               : (List<org.jcoderz.phoenix.report.jaxb.File>) 
                   currentReport.getFile())
           {
               final org.jcoderz.phoenix.report.jaxb.File oldFile 
                   = findFile(newFile, oldReport);
               if (oldFile != null)
               {
                   findNewFindings(newFile, oldFile);
               }
               else
               {
                   flaggAllAsNew(newFile.getItem());
               }
           }
           
           writeResult(currentReport, mOutFile);
       }
       catch (Exception ex)
       {
           logger.log(Level.WARNING, 
               "Failed to flagNewFindings. Cause " + ex.getMessage(), ex);
       }
   }

    private void findNewFindings (org.jcoderz.phoenix.report.jaxb.File newFile,
        org.jcoderz.phoenix.report.jaxb.File oldFile)
    {
        final List<Item> newFindings 
            = new ArrayList<Item>((List<Item>) newFile.getItem());
        final List<Item> oldFindings 
            = new ArrayList<Item>((List<Item>) oldFile.getItem());

        filterLowSeverity(newFindings);
        filterLowSeverity(oldFindings);
        filterFullMatches(newFindings, oldFindings);
        filterPartialMatches(newFindings, oldFindings);

        // the rest...
        flaggAllAsNew(newFindings);
        for (Item item : oldFindings)
        {
            addAsOld(newFile.getItem(), item);
        }
        
    }

    private void flaggAllAsNew (final List<Item> newFindings)
    {
        for (Item item : newFindings)
        {
            if (item.getSeverity().getPenalty() > 0
                && item.getSeverity() != Severity.COVERAGE)
            {
                flagAsNew(item);
            }
        }
    }

    private void addAsOld (List<Item> newFindings, Item item)
    {
        if (item.getSeverity().getPenalty() > 0
            && item.getSeverity() != Severity.COVERAGE)
        {
            item.setSeverity(Severity.OK);
            item.unsetNew();
            item.setOld(true);
            newFindings.add(item);
        }
    }


    private void filterFullMatches (final List<Item> newFindings,
        final List<Item> oldFindings)
    {
        // Filter 100% matches:
        final Iterator<Item> newIterator = newFindings.iterator();
        while (newIterator.hasNext())
        {
            final Item newItem = newIterator.next();
            final Iterator<Item> oldIterator = oldFindings.iterator();
            while (oldIterator.hasNext())
            {
                final Item oldItem = oldIterator.next();
                if (isSameFinding(newItem, oldItem))
                {
                    newItem.setSince(oldItem.getSince());
                    newIterator.remove();
                    oldIterator.remove();
                    break;
                }
            }
        }
    }


    /* private */ static boolean isSameFinding (Item newItem, Item oldItem)
    {
        final boolean result;
        if (oldItem.getFindingType().equals(newItem.getFindingType()))
        {
            if (oldItem.getOrigin().equals(Origin.CPD))
            {
                // Fuzzy compare CPD Findings
                // see also http://www.jcoderz.org/fawkez/ticket/71
                result = oldItem.getLine() == newItem.getLine()
                    && oldItem.getMessage().regionMatches(
                        0, newItem.getMessage(), 0, CPD_UNIQUE_STRING_LENGTH);
            }
            else
            {
                result = oldItem.getLine() == newItem.getLine()
                    && oldItem.getColumn() == newItem.getColumn()
                    && oldItem.getMessage().equals(newItem.getMessage())
                    && oldItem.getCounter() <= newItem.getCounter();
            }
        }
        else
        {
            result = false;
        }
        return result;
    }

    private void filterPartialMatches (final List<Item> newFindings,
        final List<Item> oldFindings)
    {
        // Filter matches that 'moved' within the file. 
        // There is for sure a better algorithm possible..
        final Iterator<Item> newIterator = newFindings.iterator();
        while (newIterator.hasNext())
        {
            final Item newItem = newIterator.next();
            final Iterator<Item> oldIterator = oldFindings.iterator();
            while (oldIterator.hasNext())
            {
                final Item oldItem = oldIterator.next();
                if (isPartialSameFinding(newItem, oldItem))
                {
                    newItem.setSince(oldItem.getSince());
                    newIterator.remove();
                    oldIterator.remove();
                    break;
                }
            }
        }
    }


    private boolean isPartialSameFinding (Item newItem, Item oldItem)
    {
        final boolean result;
        if (oldItem.getFindingType().equals(newItem.getFindingType()))
        {
            if (oldItem.getOrigin().equals(Origin.CPD))
            {
                // Fuzzy compare CPD Findings
                // see also http://www.jcoderz.org/fawkez/ticket/71
                // The or is by intention due to resistant findings
                // reported as new frequently.
                result = oldItem.getLine() == newItem.getLine()
                    || oldItem.getMessage().regionMatches(
                        0, newItem.getMessage(), 0, CPD_UNIQUE_STRING_LENGTH);
            }
            else
            {
                result = oldItem.getMessage().equals(newItem.getMessage())
                    && oldItem.getCounter() <= newItem.getCounter();
            }
        }
        else
        {
            result = false;
        }
        return result;
    }

    private void filterLowSeverity (final List<Item> newFindings)
    {
        final Iterator<Item> i = newFindings.iterator();
        while (i.hasNext())
        {
            final Item item = i.next();
            if (item.getSeverity().getPenalty() == 0
                || item.getSeverity() == Severity.COVERAGE)
            {
                i.remove();
            }
        }
    }

    private void flagAsNew (Item item)
    {
        item.unsetOld();
        item.setNew(true);
        item.setSince(mReportDate);
    }


   // This could be done faster, might be restructure the data first for 
   // faster lookup.
   private org.jcoderz.phoenix.report.jaxb.File findFile (
       org.jcoderz.phoenix.report.jaxb.File newFile, Report oldReport)
   {
       final String className = newFile.getClassname();
       final String packageName = newFile.getPackage();
       final String fileName = newFile.getName();
       org.jcoderz.phoenix.report.jaxb.File result = null;
       for (org.jcoderz.phoenix.report.jaxb.File file 
           : (List<org.jcoderz.phoenix.report.jaxb.File>) oldReport.getFile())
       {
           if (ObjectUtil.equals(file.getName(), fileName) 
               || (!StringUtil.isEmptyOrNull(className) 
                   && packageName != null
                   && ObjectUtil.equals(file.getClassname(), className) 
                   && ObjectUtil.equals(file.getPackage(), packageName)))
           {
               result = file;
               break;
           }
       }
       return result;
   }


   /**
    * Parses the arguments.
    *
    * @param args the args
    */
   private void parseArguments (String[] args)
   {
      try
      {
         for (int i = 0; i < args.length; )
         {
            logger.fine("Parsing argument '" + args[i] + "' = '"
                  + args[i + 1] + "'");

            if ("-jcreport".equals(args[i]))
            {
               addReport(new File(args[i + 1]));
            }
            else if ("-filter".equals(args[i]))
            {
               addFilter(new File(args[i + 1]));
            }
            else if ("-old".equals(args[i]))
            {
               setOldFile(new File(args[i + 1]));
            }
            else if ("-loglevel".equals(args[i]))
            {
                setLogLevel(Level.parse(args[i + 1]));
            }
            else if ("-out".equals(args[i]))
            {
                setOutFile(new File(args[i + 1]));
            }
            else
            {
               throw new IllegalArgumentException(
                       "Invalid argument '" + args[i]  + "'");
            }

            ++i;
            ++i;
         }
      }
      catch (IndexOutOfBoundsException e)
      {
         final IllegalArgumentException ex = new IllegalArgumentException(
            "Missing value for " + args[args.length - 1]);
         ex.initCause(e);
         throw ex;
      }
      catch (IOException e)
      {
         final IllegalArgumentException ex = new IllegalArgumentException(
            "Wrong out folder " + args[args.length - 1]);
         ex.initCause(e);
         throw ex;
      }
   }

   /**
    * The main method.
    *
    * @param args the arguments
    * @throws Exception in case of a technical issue.
    */
   public static void main (String[] args)
       throws Exception
   {
      final ReportMerger rm = new ReportMerger();
      rm.parseArguments(args);
      rm.merge();
      rm.filter();
   }

    /**
     * Adds the report.
     * @param report the report
     */
    public void addReport (File report)
    {
        mReports.add(report);
    }

    /**
     * Adds the filter.
     * @param filter the filter
     */
    public void addFilter (File filter)
    {
        mFilters.add(filter);
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public Level getLogLevel ()
    {
        return mLogLevel;
    }


    /**
     * Sets the log level.
     *
     * @param logLevel the new log level
     */
    public void setLogLevel (Level logLevel)
    {
        mLogLevel = logLevel;
        LoggingUtils.setGlobalHandlerLogLevel(mLogLevel);
        logger.fine("Setting log level: " + mLogLevel);
        logger.setLevel(mLogLevel);
    }


    /**
     * Gets the out file.
     *
     * @return the out file
     */
    public File getOutFile ()
    {
        return mOutFile;
    }


    /**
     * Set the old report to compare with.
     * @param file old report file.
     * @throws IOException if the file name conversion fails
     */
    public void setOldFile (File file)
        throws IOException
    {
        Assert.notNull(file, "file");
        if (mOldReport != null)
        {
            throw new ArgumentMalformedException("old", file,
                "Old Report File has already set to '" + mOldReport + "'.");
        }
        mOldReport = file.getCanonicalFile();
    }
    
    /**
     * Sets the out file.
     *
     * @param outFile the new out file
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void setOutFile (File outFile)
        throws IOException
    {
        if (mOutFile != null)
        {
            throw new ArgumentMalformedException("outFile", outFile,
                "Out File already set to '" + mOutFile + "'.");
        }
        mOutFile = outFile;
        if (mOutFile.isDirectory())
        {
            FileUtils.mkdirs(mOutFile);
            mOutFile = new File(mOutFile,
                ReportNormalizer.JCODERZ_REPORT_XML).getCanonicalFile();
        }
        else
        {
           mOutFile = mOutFile.getCanonicalFile();
        }

    }

    private void writeResult (final Report mergedReport, File outFile)
        throws JAXBException, PropertyException, FileNotFoundException
    {
        // create the file
         final JAXBContext mJaxbContext
             = JAXBContext.newInstance("org.jcoderz.phoenix.report.jaxb",
           this.getClass().getClassLoader());
         final Marshaller marshaller = mJaxbContext.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                 Boolean.TRUE);
         final FileOutputStream out = new FileOutputStream(outFile);
         try
         {
             marshaller.marshal(mergedReport, out);
         }
         finally
         {
             IoUtil.close(out);
         }
    }
    

}
