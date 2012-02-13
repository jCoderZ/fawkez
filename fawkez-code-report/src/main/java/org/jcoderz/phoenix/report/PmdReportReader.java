/*
 * $Id: PmdReportReader.java 627 2008-03-16 11:11:43Z amandel $
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.pmd.jaxb.FileType;
import org.jcoderz.phoenix.pmd.jaxb.Pmd;
import org.jcoderz.phoenix.pmd.jaxb.Violation;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * PMD Report Reader.
 *
 * @author Michael Griffel
 */
public final class PmdReportReader
      extends AbstractReportReader
{
    /** JAXB context path. */
    public static final String PMD_JAXB_CONTEXT_PATH
            = "org.jcoderz.phoenix.pmd.jaxb";

    private static final String CLASSNAME = PmdReportReader.class.getName();
    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private static final int PRIORITY_HIGH = 1;
    private static final int PRIORITY_MEDIUM_HIGH = 2;
    private static final int PRIORITY_MEDIUM = 3;
    private static final int PRIORITY_MEDIUM_LOW = 4;
    private static final int PRIORITY_LOW = 5;

    private Pmd mReportDocument;

    /**
     * Constructor.
     *
     * @throws JAXBException
     */
    public PmdReportReader ()
            throws JAXBException
    {
        super(PMD_JAXB_CONTEXT_PATH);
    }

    /** {@inheritDoc} */
    public void parse (File f)
            throws JAXBException, FileNotFoundException
    {
        logger.entering(CLASSNAME, "parse", f);
        mReportDocument = (Pmd) getUnmarshaller().unmarshal(
                new FileInputStream(f));
        logger.exiting(CLASSNAME, "parse");
    }

    /** {@inheritDoc} */
    protected Map<ResourceInfo, List<Item>> getItems ()
            throws JAXBException
    {
        logger.entering(CLASSNAME, "getItems()");
        final Map<ResourceInfo, List<Item>> result
            = new HashMap<ResourceInfo, List<Item>>();

        for (final Iterator<FileType> iterator = mReportDocument.getFile().iterator();
                iterator.hasNext();)
        {
            final FileType file = iterator.next();

            final String key = normalizeFileName(file.getName());
            final List<Item> items = createItemMap(file);
            final ResourceInfo info = ResourceInfo.lookup(key);
            if (info != null)
            {
                result.put(info, items);
            }
            else
            {
                logger.finer("Ingoring findings for resource " + key);
            }
        }
        logger.exiting(CLASSNAME, "getItems()", result);
        return result;
    }

    private List<Item> createItemMap (org.jcoderz.phoenix.pmd.jaxb.FileType file)
            throws JAXBException
    {
        final List<Item> items = new ArrayList<Item>();
        for (final Iterator<Violation> iterator = file.getViolation().iterator(); iterator
                .hasNext();)
        {
            final Violation violation = iterator.next();

            final Item item = new ObjectFactory().createItem();
            item.setMessage(violation.getValue().trim());
            item.setOrigin(Origin.PMD);
            item.setSeverity(mapPriority(violation));
            item.setFindingType(violation.getRule());
            item.setLine(violation.getBeginline());
            item.setEndLine(violation.getEndline());
            item.setColumn(violation.getBegincolumn());
            item.setEndColumn(violation.getEndcolumn());
            items.add(item);
        }
        return items;
    }

    private Severity mapPriority (Violation violation)
    {
        final Severity ret;

        switch (violation.getPriority())
        {
            case PRIORITY_HIGH:
                ret = Severity.ERROR;
                break;
            case PRIORITY_MEDIUM_HIGH:
                ret = Severity.WARNING;
                break;
            case PRIORITY_MEDIUM:
                ret = Severity.DESIGN;
                break;
            case PRIORITY_MEDIUM_LOW:
                ret = Severity.CODE_STYLE;
                break;
            case PRIORITY_LOW:
                ret = Severity.INFO;
                break;
            default:
                ret = Severity.WARNING;
        }
        return ret;
    }
}
