/*
 * $Id: ReportNormalizer.java 1468 2009-05-11 16:49:58Z amandel $
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jcoderz.commons.util.FileUtils;
import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.commons.util.LoggingUtils;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * Provides merging of findbugs, pmd, checkstyle, cpd, and cobertura
 * XML files into a single XML representation.
 *
 * @author Michael Griffel
 * @author Michael Rumpf
 */
public final class ReportNormalizer
{

    /** The Constant JCODERZ_REPORT_XML. */
    public static final String JCODERZ_REPORT_XML
        = "jcoderz-report.xml";

    /** The Constant CLASSNAME. */
    private static final String CLASSNAME
        = ReportNormalizer.class.getName();

    /** The Constant logger. */
    private static final Logger logger
        = Logger.getLogger(CLASSNAME);

    /** The project home. */
    private File mProjectHome;

    /** The project name. */
    private String mProjectName = "Unknown Project";

    /** The out file. */
    private File mOutFile;

    /** The log level. */
    private Level mLogLevel = Level.INFO;

    /** The report level. */
    private ReportLevel mLevel = ReportLevel.PROD;

    /** The report list. */
    private List<SourceReport> mReportList
        = new ArrayList<SourceReport>();

    /** The src list. */
    private List<SourceReport> mSrcList
        = new ArrayList<SourceReport>();

    /**
     * The XSL stylesheet that can be used to filter the
     * jcoderz-report XML file.
     */
    private File mFilterFile = null;

    /**
     * Constructor.
     *
     * @throws IOException in case of any error.
     */
    public ReportNormalizer ()
        throws IOException
    {
        mProjectHome = new File(".").getCanonicalFile();
        mOutFile = new File(JCODERZ_REPORT_XML);
    }

    /**
     * Main method.
     *
     * @param args arguments.
     *
     * @throws Exception the exception
     */
    public static void main (String[] args)
        throws Exception
    {
        try
        {
            final ReportNormalizer rn = new ReportNormalizer();
            rn.parseArguments(args);
            rn.run();
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Failed in Normalizer.", e);
            throw e;
        }
    }

    /**
     * Run ReportNormalizer.
     */
    public void run () throws JAXBException, IOException, TransformerException
    {
        logger.fine("Running report normalizer on #" + mReportList.size()
            + " reports ...");
        final Map<ResourceInfo, List<Item>> items
            = new HashMap<ResourceInfo, List<Item>>();
        for (SourceReport report : mSrcList)
        {
            handleReport(report, items);
        }
        for (SourceReport report : mReportList)
        {
            handleReport(report, items);
        }

        final JcoderzReport myReport = new JcoderzReport();
        myReport.setProjectHome(mProjectHome.getAbsolutePath());
        myReport.setProjectName(mProjectName);
        myReport.setLevel(mLevel);

        // XML report
        final OutputStream out = new FileOutputStream(mOutFile);
        try
        {
            myReport.write(out, items);
        }
        finally
        {
            IoUtil.close(out);
        }

        // apply filters to the report
        if (mFilterFile != null)
        {
            filter();
        }
    }

    /**
     * Handle report.
     *
     * @param report the report
     * @param items the items
     *
     * @throws JAXBException the JAXB exception
     */
    private void handleReport (SourceReport report,
        final Map<ResourceInfo, List<Item>> items)
            throws JAXBException
    {
        try
        {
            logger.fine("Processing report " + report.getReportFormat()
                + " '" + report.getFilename() + "'");
            
            
            
            if (report.getFilename().length() != 0
                || report.getFilename().isDirectory())
            {
                final ReportReader reportReader
                    = ReportReaderFactory.createReader(report);
                reportReader.parse(report.getFilename());
                reportReader.merge(items);
            }
            else
            {
                logger.fine("Good job, no findings reported by "
                    + report.getReportFormat());
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error while processing", e);
            final Item item = new ObjectFactory().createItem();
            item.setMessage("Error while Processing '"
                + report.getReportFormat() + "' '"
                + report.getFilename() + "' got Exception: '" + e + "'.");
            item.setSeverity(Severity.ERROR);
            item.setFindingType(SystemFindingType.SYS_PARSE_ERROR.getSymbol());
            item.setOrigin(Origin.SYSTEM);
            final ResourceInfo res
                = ResourceInfo.register(report.getFilename().getAbsolutePath(),
                    "", report.getFilename().getAbsolutePath());
            if (items.containsKey(res))
            {
                items.get(res).add(item);
            }
            else
            {
                final List<Item> list = new ArrayList<Item>();
                list.add(item);
                items.put(res, list);
            }
        }
    }

    /**
     * Filters the report XML file using the JDK XSL processor.
     *
     * @throws TransformerFactoryConfigurationError
     *      the transformer factory configuration error
     * @throws TransformerConfigurationException
     *      the transformer configuration exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TransformerException the transformer exception
     * @throws FileNotFoundException the file not found exception
     */
    private void filter ()
        throws TransformerFactoryConfigurationError,
            TransformerConfigurationException, IOException,
            TransformerException, FileNotFoundException
    {
        logger.log(Level.FINE, "Filter: " + mFilterFile);
        final TransformerFactory tFactory = TransformerFactory.newInstance();

        final Transformer transformer = tFactory.newTransformer(
            new StreamSource(mFilterFile));

        final File tempOutputFile = new File(
            mOutFile.getCanonicalPath() + ".tmp");
        FileUtils.createNewFile(tempOutputFile);

        final FileOutputStream out = new FileOutputStream(tempOutputFile);
        try
        {
            transformer.transform(new StreamSource(mOutFile),
                new StreamResult(out));
        }
        finally
        {
            IoUtil.close(out);
        }
        FileUtils.copyFile(tempOutputFile, mOutFile);
        FileUtils.delete(tempOutputFile);
    }

    /**
     * The following parameters select the different reports
     * to combine into a single report.
     *
     * <ul>
     * <li><code>-jcoverage jvoveragereport.xml</code> (http://???)</li>
     * <li><code>-cobertura coberturareport.xml</code> (http://???)</li>
     * <li><code>-checkstyle checkstylereport.xml</code>
     * (http://checkstyle.sf.net)</li>
     * <li><code>-findbugs findbugsreport.xml</code>
     * (http://findbugs.sf.net)</li>
     * <li><code>-pmd pmdreport.xml</code> (http://pmd.sf.net)</li>
     * <li><code>-cpd cpdreport.xml</code> (http://))))</li>
     * <li><code>-generic javadoc javadoc.log</code> (http://))))</li>
     * </ul>
     *
     * <ul>
     * <li><code>-projectHome</code></li>
     * <li><code>-filter filter.xsl</code></li>
     * <li><code>-srcDir</code></li>
     * <li><code>-projectName</code></li>
     * <li><code>-level PROD|TEST|MISC</code> The weight level</li>
     * <li><code>-loglevel</code></li>
     * <li><code>-out</code></li>
     * </ul>
     *
     * @param args The command line arguments
     *
     * @return The list of reports to normalize
     *
     * @throws IOException When the filter file cannot be found
     */
    private void parseArguments (String[] args)
        throws IOException
    {
        try
        {
            for (int i = 0; i < args.length; )
            {
                logger.fine("Parsing argument '" + args[i] + "' = '"
                    + args[i + 1] + "'");

                if (args[i].equals("-jcoverage"))
                {
                    addReport(ReportFormat.JCOVERAGE, args[i + 1]);
                }
                else if (args[i].equals("-cobertura"))
                {
                    addReport(ReportFormat.COBERTURA, args[i + 1]);
                }
                else if (args[i].equals("-checkstyle"))
                {
                    addReport(ReportFormat.CHECKSTYLE, args[i + 1]);
                }
                else if (args[i].equals("-findbugs"))
                {
                    addReport(ReportFormat.FINDBUGS, args[i + 1]);
                }
                else if (args[i].equals("-pmd"))
                {
                    addReport(ReportFormat.PMD, args[i + 1]);
                }
                else if (args[i].equals("-emma"))
                {
                    addReport(ReportFormat.EMMA, args[i + 1]);
                }
                else if (args[i].equals("-cpd"))
                {
                    addReport(ReportFormat.CPD, args[i + 1]);
                }
                else if (args[i].equals("-generic"))
                {
                    addReport(ReportFormat.GENERIC, args[++i], args[i + 1]);
                }
                else if (args[i].equals("-projectHome"))
                {
                    setProjectHome(new File(args[i + 1]));
                }
                else if (args[i].equals("-filter"))
                {
                    setFilterFile(new File(args[i + 1]));
                }
                else if (args[i].equals("-srcDir"))
                {
                    addReport(ReportFormat.SOURCE_DIRECTORY, args[i + 1]);
                }
                else if (args[i].equals("-projectName"))
                {
                    setProjectName(args[i + 1]);
                }
                else if (args[i].equals("-level"))
                {
                    setLevel(ReportLevel.fromString(args[i + 1]));
                }
                else if (args[i].equals("-loglevel"))
                {
                    setLogLevel(Level.parse(args[i + 1]));
                }
                else if (args[i].equals("-out"))
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
    }

    /**
     * Adds the report.
     *
     * @param format the format
     * @param file the file
     */
    public void addReport (ReportFormat format, String file)
    {
        addReport(format, new File(file));
    }
    
    /**
     * Adds the report with a given flavor.
     * The flavor is used for generic reports to detect the type of file.
     *
     * @param format the format
     * @param file the file
     * @param flavor the flavor of the report.
     */
    public void addReport (ReportFormat format, String file, String flavor)
    {
        mReportList.add(new SourceReport(format, new File(file), flavor));
    }

    /**
     * Adds the report.
     *
     * @param format the format
     * @param file the file
     */
    public void addReport (ReportFormat format, File file)
    {
        if (format == ReportFormat.SOURCE_DIRECTORY)
        {
            addSource(file);
        }
        else
        {
            mReportList.add(new SourceReport(format, file));
        }
    }


    public void addSource (File srcDir)
    {
        mSrcList.add(new SourceReport(
            ReportFormat.SOURCE_DIRECTORY, srcDir));
    }

    /**
     * Gets the project home.
     *
     * @return the project home
     */
    public File getProjectHome ()
    {
        return mProjectHome;
    }

    /**
     * Sets the project home.
     *
     * @param projectHome the new project home
     */
    public void setProjectHome (File projectHome)
    {
        mProjectHome = projectHome;
    }

    /**
     * Gets the project name.
     *
     * @return the project name
     */
    public String getProjectName ()
    {
        return mProjectName;
    }

    /**
     * Sets the project name.
     *
     * @param projectName the new project name
     */
    public void setProjectName (String projectName)
    {
        mProjectName = projectName;
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
     * Sets the out file.
     *
     * @param outFile the new out file
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void setOutFile (File outFile) throws IOException
    {
        if (outFile.isDirectory())
        {
            mOutFile
            = new File(outFile, JCODERZ_REPORT_XML).getCanonicalFile();
        }
        else
        {
            mOutFile = outFile.getCanonicalFile();
        }
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
        logger.config("Setting log level: " + mLogLevel);
        Logger.getLogger("org.jcoderz.phoenix.report")
            .setLevel(mLogLevel);
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public ReportLevel getLevel ()
    {
        return mLevel;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel (ReportLevel level)
    {
        mLevel = level;
    }

    /**
     * Gets the filter file.
     *
     * @return the filter file
     */
    public File getFilterFile ()
    {
        return mFilterFile;
    }

    /**
     * Sets the filter file.
     *
     * @param filterFile the new filter file
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void setFilterFile (File filterFile)
        throws IOException
    {
        mFilterFile = filterFile;
        // Do not fail here if the argument is invalid.
        if (!mFilterFile.exists())
        {
            throw new IOException("Filter file '" + mFilterFile
                + "' does not exists.");
        }
    }

    /**
     * The Class SourceReport.
     */
    public static final class SourceReport
    {

        /** The report format. */
        private final ReportFormat mReportFormat;

        /** The filename. */
        private final File mFilename;

        /** The flavor. */
        private final String mFlavor;
        
        /**
         * Instantiates a new source report.
         * No check here. Let the report parsing fail.
         * @param r the ReportFormat
         * @param f the File
         */
        SourceReport (ReportFormat r, File f)
        {
            mReportFormat = r;
            mFilename = f;
            mFlavor = null;
        }

        /**
         * Instantiates a new source report.
         * No check here. Let the report parsing fail.
         * @param r the ReportFormat
         * @param f the File
         * @param flavor the report flavor
         */
        SourceReport (ReportFormat r, File f, String flavor)
        {
            mReportFormat = r;
            mFilename = f;
            mFlavor = flavor;
        }

        /**
         * Returns the filename.
         *
         * @return the filename.
         */
        File getFilename ()
        {
            return mFilename;
        }

        /**
         * Returns the reportFormat.
         *
         * @return the reportFormat.
         */
        ReportFormat getReportFormat ()
        {
            return mReportFormat;
        }

        /**
         * Returns the report flavor.
         *
         * @return the report flavor.
         */
        public String getFlavor ()
        {
            return mFlavor;
        }
    }
}
