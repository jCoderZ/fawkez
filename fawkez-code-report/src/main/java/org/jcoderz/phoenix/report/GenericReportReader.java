/*
 * $Id: SourceDirectoryReader.java 1408 2009-04-14 16:06:46Z amandel $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.Constants;
import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.commons.util.JaxbUtil;
import org.jcoderz.commons.util.ObjectUtil;
import org.jcoderz.commons.util.StringUtil;
import org.jcoderz.commons.util.JaxbUtil.UnmarshalResult;
import org.jcoderz.phoenix.report.ftf.jaxb.FindingDescription;
import org.jcoderz.phoenix.report.ftf.jaxb.FindingTypeFormat;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.xml.sax.InputSource;

/**
 * Reads reports with format definitions described in the
 * finding-type-format-definition.xds.
 *
 * To find the finding type format definition for requested format
 * the following locations are used:
 *
 * The name is converted to lower case.
 *
 * A file <i>name</i>.xml is searched in the
 * <code>org.jcoderz.phoenix.report.ftf</code> package. If
 * this is not found the file is searched in the <code>ftf</code>
 * directory. The directory must be available through the classpath.
 *
 *
 * @author Andreas Mandel
 *
 */
public final class GenericReportReader implements ReportReader
{
    private static final int MAX_DEBUG_TEXT_CHARS = 100;
    private static final String CLASSNAME
        = GenericReportReader.class.getName();
    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private static final Pattern CODE_LINE_PATTERN
        = Pattern.compile("^.*$", Pattern.MULTILINE);

    private static final Pattern CARET_LINE_PATTERN
        = Pattern.compile("^\\s*\\^$", Pattern.MULTILINE);

    private static final Map<Origin, GenericReportReader> GENERIC_REPORT_TYPES
        = new HashMap<Origin, GenericReportReader>();


    private final List<GenericFindingType> mFindingTypes
        = new ArrayList<GenericFindingType>();

    private Map<ResourceInfo, List<Item>> mItems;

    private SourceFile mSourceFile;

    private final Pattern mMessagePattern;
    private final FindingTypeFormat mFindingTypeFormatDescription;

    private final int mTextPos;
    private final Origin mOrigin;
    private final int mFilePos;
    private final int mLineStart;
    private final Severity mDefaultSeverity;

    private Matcher mRootMatcher = null;

    private GenericReportReader (Origin type)
        throws JAXBException
    {
        mOrigin = type;
        mFindingTypeFormatDescription = loadFormatDescription(type);
        initializeFindingTypes();
        final FindingDescription root
            = mFindingTypeFormatDescription.getRootType();
        mMessagePattern
            = Pattern.compile(root.getPattern(),
                Pattern.MULTILINE);
        mTextPos =  Integer.parseInt(root.getTextPos());
        mFilePos =  Integer.parseInt(root.getFilenamePos());
        mLineStart = root.isSetLineStartPos()
            ? Integer.parseInt(root.getLineStartPos()) : -1;
        mDefaultSeverity = root.isSetSeverity()
            ? root.getSeverity() : Severity.CODE_STYLE;
    }

    /**
     * Initializes the selected finding type.
     * Might return <code>null</code> if the initialization fails.
     * CHECKME: Should return a null object?
     * @param findingType the type to load.
     * @return the loaded finding type.
     */
    public static GenericReportReader initialize (Origin findingType)
    {
        GenericReportReader result = null;
        synchronized (GENERIC_REPORT_TYPES)
        {
            if (!GENERIC_REPORT_TYPES.containsKey(findingType))
            {
                try
                {
                    result = new GenericReportReader(findingType);
                }
                catch (Exception ex)
                {
                    // TODO: collect this an add it to the findings map later!
                    logger.log(Level.WARNING,
                        "Could not load finding type for '" + findingType
                        + "' failed with " + ex.getMessage() + ".", ex);
                }
                GENERIC_REPORT_TYPES.put(findingType, result);
            }
            result = GENERIC_REPORT_TYPES.get(findingType);
        }
        return result;
    }

    private static FindingTypeFormat loadFormatDescription (Origin type)
        throws JAXBException
    {
        FindingTypeFormat findingTypeFormatDescription = null;
        InputStream in = null;
        try
        {
            final String filename
                = type.toString().toLowerCase(Constants.SYSTEM_LOCALE)
                    + ".xml";
            in = GenericReportReader.class.getResourceAsStream(
                "ftf/" + filename);
            if (in == null)
            {
                in = GenericReportReader.class.getResourceAsStream(
                    "/ftf/" + filename);
            }
            if (in == null)
            {
                try
                {
                    in = new FileInputStream(filename);
                }
                catch (FileNotFoundException ex)
                {
                    // in = null;
                }
            }
            Assert.notNull(in, "report type description " + type);
            final UnmarshalResult unmarshal
                = JaxbUtil.unmarshal(new InputSource(in),
                    "org.jcoderz.phoenix.report.ftf.jaxb");

            findingTypeFormatDescription
                = (FindingTypeFormat) unmarshal.getParsedData();
        }
        finally
        {
            IoUtil.close(in);
        }
        return findingTypeFormatDescription;
    }

    /** {@inheritDoc} */
    public void parse (File f)
        throws JAXBException
    {
        try
        {
            mSourceFile = new SourceFile(f);
            mRootMatcher = mMessagePattern.matcher(mSourceFile.getContent());
        }
        catch (IOException ex)
        {
            throw new JAXBException("Failed to read '" + f + "'.", ex);
        }
    }

    /** {@inheritDoc} */
    public void merge (Map<ResourceInfo, List<Item>> items)
        throws JAXBException
    {
        mItems = items;
        while (!mSourceFile.readFully())
        {
            parseNext();
        }
    }

    /**
     * Reads the given message and tries to find a matching finding type.
     * @param message the message to read.
     * @return the finding type matching to the message, or null if no such
     *   type was found.
     * @throws JAXBException if item creation fails.
     */
    public Item detectFindingTypeForMessage (String message)
        throws JAXBException
    {
       Item result = null;
       for (final GenericFindingType type : mFindingTypes)
       {
           result = type.createItem(mSourceFile, message);
           if (result != null)
           {
              if (type.isSourceColumnByCaret())
              {
                  addPositionByCaret(result);
              }
              break;
           }
       }
       if (logger.isLoggable(Level.FINE))
       {
           logger.fine("For text: '"
               + StringUtil.trimLength(message, MAX_DEBUG_TEXT_CHARS)
               + "' matched finding: "
               + (result == null ? "null" : result.getFindingType()
               + "'. End at " + mSourceFile.getPos()));
       }
       return result;
    }

    private void addPositionByCaret (final Item i)
    {
        final String text
            = mSourceFile.getContent().substring(mSourceFile.getPos());
        final Matcher codeMat
            = CODE_LINE_PATTERN.matcher(text);
        if (codeMat.lookingAt())
        {
            final String textAfterCode
                = mSourceFile.getContent().substring(
                    mSourceFile.getPos() + codeMat.end() + 1);
            final Matcher caretMat
                = CARET_LINE_PATTERN.matcher(textAfterCode);
            if (caretMat.lookingAt())
            {
                i.setColumn(caretMat.end());
                mSourceFile.setPos(
                    mSourceFile.getPos()
                    + codeMat.end() + 1
                    + caretMat.end() + 1);
            }
            else
            {
                logger.fine("Caret defined but not found for '"
                    + i.getFindingType()
                    + "' Code Line: '" + codeMat + "' caretLine: '"
                    + caretMat + "'. text: '"
                    + StringUtil.trimLength(
                        textAfterCode, MAX_DEBUG_TEXT_CHARS) + "'.");
            }
        }
        else
        {
            logger.fine("Caret defined but not found for '"
                + i.getFindingType()
                + "' Code Line: '" + codeMat + "'. text: '"
                + StringUtil.trimLength(
                    text, MAX_DEBUG_TEXT_CHARS) + "'.");
        }
    }

    private void parseNext ()
        throws JAXBException
    {
        if (mRootMatcher.find())
        {
            final String text = mRootMatcher.group(mTextPos);
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Main pattern matched for: '"
                    + StringUtil.trimLength(text, MAX_DEBUG_TEXT_CHARS)
                    + "'. End at " + mRootMatcher.end());
            }
            mSourceFile.setPos(mRootMatcher.start(mTextPos));
            final Item item = detectFindingTypeForMessage(text);
            if (item == null)
            {
                final int pos
                    = mSourceFile.getContent().indexOf(
                        '\n', mSourceFile.getPos());
                if (pos != -1)
                {
                    mSourceFile.setPos(pos + 1);
                }
                else
                {
                    mSourceFile.setPos(mSourceFile.getContent().length());
                }
            }
            else
            {
                item.setOrigin(mOrigin);
                if (!item.isSetSeverity())
                {
                    item.setSeverity(mDefaultSeverity);
                }
                if (!item.isSetLine() && mLineStart != -1
                    && mRootMatcher.group(mLineStart) != null)
                {
                    item.setLine(
                        Integer.parseInt(mRootMatcher.group(mLineStart)));
                }
                if (!item.isSetFindingType())
                {
                    item.setFindingType(mOrigin.toString());
                }
                if (!item.isSetMessage())
                {
                    item.setMessage(mRootMatcher.group(mTextPos));
                }
                if (mFindingTypeFormatDescription.getRootType().isGlobal())
                {
                    item.setGlobal(true);
                }
                addItemToResource(mRootMatcher.group(mFilePos), item);
            }
            mRootMatcher.region(
                mSourceFile.getPos(), mSourceFile.getContent().length());
        }
        else
        {
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("No match after " + mSourceFile.getPos()
                    + " '" + StringUtil.trimLength(
                        mSourceFile.getContent().substring(
                            mSourceFile.getPos()),
                            MAX_DEBUG_TEXT_CHARS));
            }
            // set pos to end of file
            mSourceFile.setPos(mSourceFile.getContent().length());
        }
    }

    private void addItemToResource (String resourceFilename, Item item)
    {
        final ResourceInfo info = ResourceInfo.lookup(resourceFilename);
        if (info != null || item.isGlobal())
        {
            final List<Item> l;
            if (mItems.containsKey(info))
            {
                l = mItems.get(info);
            }
            else
            {
                l = new ArrayList<Item>();
                mItems.put(info, l);
            }
            // sometimes javadoc reports the same thing twice...
            final Iterator<Item> i = l.iterator();
            while (i.hasNext())
            {
                final Item it = i.next();
                if (it.getLine() == item.getLine()
                    && it.getColumn() == item.getColumn()
                    && it.getOrigin() == item.getOrigin()
                    && ObjectUtil.equals(it.getMessage(), item.getMessage()))
                {
                    i.remove();
                    break;
                }
            }
            l.add(item);
        }
        else
        {
            logger.finer("Ignore findings for resource '"
                + resourceFilename + "' type was "
                + item.getFindingType() + ".");
        }
    }

    private void initializeFindingTypes ()
    {
        final FindingDescription root
            = mFindingTypeFormatDescription.getRootType();
        final List<FindingDescription> findingTypes
            = mFindingTypeFormatDescription.getFindingType();
        for (FindingDescription findingDesc : findingTypes)
        {
            final GenericFindingType gft
                = new GenericFindingType(root, findingDesc);
            mFindingTypes.add(gft);
        }
        Collections.sort(
            mFindingTypes, new GenericFindingType.OrderByPriority());
    }


    static final class SourceFile
    {
        private final File mFile;
        private final String mContent;
        private int mPos;

        public SourceFile (File file)
            throws IOException
        {
            mFile = file;
            Reader in = null;
            Reader buffered = null;
            try
            {
                in = new FileReader(file);
                buffered = new BufferedReader(in);
                mContent = IoUtil.readFullyNormalizeNewLine(buffered);
            }
            finally
            {
                IoUtil.close(buffered);
                IoUtil.close(in);
            }
            mPos = 0;
        }

        /**
         * @return the pos
         */
        public int getPos ()
        {
            return mPos;
        }

        /**
         * @param pos the pos to set
         */
        public void setPos (int pos)
        {
            mPos = pos;
        }

        /**
         * @return the file
         */
        public File getFile ()
        {
            return mFile;
        }

        /**
         * @return the content
         */
        public String getContent ()
        {
            return mContent;
        }

        public boolean readFully ()
        {
            return mPos >= mContent.length();
        }
    }
}
