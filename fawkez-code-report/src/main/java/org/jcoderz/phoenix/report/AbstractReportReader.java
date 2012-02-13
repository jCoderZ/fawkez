/*
 * $Id: AbstractReportReader.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.jcoderz.phoenix.report.jaxb.Item;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Base Report Reader class.
 *
 * Every report reader must extend from this class.
 *
 * @author Michael Griffel
 */
public abstract class AbstractReportReader
        implements ReportReader, ValidationEventHandler
{
    private static final String CLASSNAME
        = AbstractReportReader.class.getName();
    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private final JAXBContext mJaxbContext;
    private Marshaller mMarshaller = null;
    private Unmarshaller mUnmarshaller = null;

    AbstractReportReader (String jaxbContext)
            throws JAXBException
    {
        mJaxbContext = JAXBContext.newInstance(jaxbContext,
              this.getClass().getClassLoader());
    }

    /**
     * Returns a Marshaller for this report reader.
     *
     * @return a Marshaller for this report reader.
     * @throws JAXBException if the creation of the marshaller failed.
     */
    public final Marshaller getMarshaller ()
            throws JAXBException
    {
        if (mMarshaller == null)
        {
            mMarshaller = mJaxbContext.createMarshaller();
            mMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            mMarshaller.setEventHandler(this);
        }
        return mMarshaller;
    }

    /**
     * Returns a Unmarshaller for this report reader.
     *
     * @return a Unmarshaller for this report reader.
     * @throws JAXBException if the creation of the unmarshaller failed.
     */
    public final Unmarshaller getUnmarshaller ()
            throws JAXBException
    {
        if (mUnmarshaller == null)
        {
            mUnmarshaller = mJaxbContext.createUnmarshaller();
            mUnmarshaller.setValidating(true);
            mUnmarshaller.setEventHandler(this);
        }
        return mUnmarshaller;
    }

    /**
     * Reads the XML data from the given InputStream <tt>in</tt> and unmarshal
     * it to the corresponding JAXB object.
     *
     * This method uses a native {@link XMLReader} to parse the XML data using
     * a simple {@link EntityResolver} that resolves any entity to an empty
     * string.
     *
     * @param in the input stream to read the XML data from.
     * @return the JAXB object.
     */
    public final Object unmarshall (InputStream in)
    {
        final Object result;
        try
        {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final XMLReader reader;
            try
            {
               reader = factory.newSAXParser().getXMLReader();
            }
            catch (SAXException e)
            {
                throw new RuntimeException(e);
            }
            catch (ParserConfigurationException e)
            {
                throw new RuntimeException(e);
            }
            final UnmarshallerHandler un
                = getUnmarshaller().getUnmarshallerHandler();
            reader.setEntityResolver(new DummyEntityResolver());
            reader.setContentHandler(un);
            reader.parse(new InputSource(in));
            result = un.getResult();
        }
        catch (Exception e)
        {
            throw new RuntimeException("FIXME", e);
        }
        return result;
    }

    /** {@inheritDoc} */
    public final void merge (Map<ResourceInfo, List<Item>> toItems)
            throws JAXBException
    {
        final Map<ResourceInfo, List<Item>> myResourceList = getItems();

        for (Entry<ResourceInfo, List<Item>> entry : myResourceList.entrySet())
        {
            final ResourceInfo info = entry.getKey();
            List<Item> items = toItems.get(info);
            if (items == null)
            {
                items = new ArrayList<Item>();
                toItems.put(info, items);
            }
            items.addAll(entry.getValue());
        }
    }

    /**
     * Returns the items of the input report as a Map of filename string
     * and of the type Item (org.jcoderz.phoenix.report.jaxb.Item).
     *
     * @return the items of the input report as a List of the type Item.
     * @throws JAXBException if an JAXB exception occures.
     */
    protected abstract Map<ResourceInfo, List<Item>> getItems ()
            throws JAXBException;

    /** {@inheritDoc} */
    public final boolean handleEvent (ValidationEvent e)
    {
        final ValidationEventLocator l = e.getLocator();
        final StringBuilder sb = new StringBuilder();
        sb.append("[ValidationEvent:");
        sb.append(", message=");
        sb.append(e.getMessage());
        sb.append(", severity=");
        sb.append(e.getSeverity());
        sb.append(", link exception=");
        sb.append(e.getLinkedException());
        sb.append(", message=");
        sb.append(l.getObject());
        sb.append(", column number=");
        sb.append(l.getColumnNumber());
        sb.append(", line number=");
        sb.append(l.getLineNumber());
        sb.append(", node=");
        sb.append(l.getNode());
        sb.append(", offset=");
        sb.append(l.getOffset());
        sb.append(", URL=");
        sb.append(l.getURL());
        sb.append(']');

        System.err.println(sb.toString());

        return false;
    }

    /**
     * Normalize the filename (platform dependend).
     *
     * @param filename the filename.
     * @return the normalized filename.
     */
    protected final String normalizeFileName (String filename)
    {
        final String newFilename;
        if (filename.indexOf('$') != -1)
        {
            newFilename = filename.substring(0, filename.indexOf('$'))
                    + ".java";
            logger.fine("Changing resource filename from " + filename + " to "
                    + newFilename);
        }
        else
        {
            newFilename = filename;
        }
        return new File(newFilename).getAbsolutePath();
    }

    // TODO: make public class (util package?) -
    // see also DummyEntityResolver in taskdef package
    private static class DummyEntityResolver
            implements EntityResolver
    {
        /** The full qualified name of this class. */
        private static final String CLASSNAME = DummyEntityResolver.class
                .getName();


        /** The logger to use. */
        private static final Logger logger = Logger.getLogger(CLASSNAME);


        public InputSource resolveEntity (String publicId, String systemId)
        {
            logger.finest(
                    "Resolving entity " + publicId + " SYSTEM " + systemId);
            // TODO: make public class EmptyInputStream
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }

    }
}
