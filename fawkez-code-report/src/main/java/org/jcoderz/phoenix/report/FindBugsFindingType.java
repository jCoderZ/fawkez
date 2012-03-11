/*
 * $Id: FindBugsFindingType.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jcoderz.phoenix.findbugs.message.jaxb.BugPatternType;
import org.jcoderz.phoenix.findbugs.message.jaxb.MessageCollectionType;


/**
 * Holds and registers findbugs specific detectors.
 * 
 * @author Michael Griffel
 */
public final class FindBugsFindingType extends FindingType
{
    private static final String CLASSNAME 
        = FindBugsFindingType.class.getName();

    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private static final String FINDBUGS_MESSAGE_JAXB_CONTEXT 
        = "org.jcoderz.phoenix.findbugs.message.jaxb";

    /** FindBugs coreplugin. */
    private static final String FINDBUGS_MESSAGE_FILE 
        = "org/jcoderz/phoenix/findbugs/messages.xml";

    /** FindBugs fb-contrib plugin. */
    private static final String FB_CONTRIB_MESSAGE_FILE 
        = "org/jcoderz/phoenix/findbugs/fb-contrib-messages.xml";

    private final String mMessagePattern;

    private FindBugsFindingType (String symbol, String shortText,
            String description, String messagePattern)
    {
        super(symbol, shortText, description);
        mMessagePattern = messagePattern;
    }

    private FindBugsFindingType (BugPatternType e)
    {
        this(e.getType(), e.getShortDescription(), e.getDetails(), 
                e.getLongDescription());
    }

    /**
     * Call this method to register all findbugs detectors.
     */
    public static void initialize ()
    {
        try
        {
            registerDetectors(FINDBUGS_MESSAGE_FILE);
            registerDetectors(FB_CONTRIB_MESSAGE_FILE);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(
                    "Cannot initialize FindBugsFindingTypes", e);
        }
    }

    /**
     * @return the message pattern associated to this finding type.
     */
    public String getMessagePattern ()
    {
        return mMessagePattern;
    }

    private static void registerDetectors (String messagesFile)
            throws JAXBException
    {
        final JAXBContext jaxbContext 
            = JAXBContext.newInstance(FINDBUGS_MESSAGE_JAXB_CONTEXT,
                    FindBugsFindingType.class.getClassLoader());

        logger.finest("Try to unmarshalling " + messagesFile);

        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final MessageCollectionType messageCollection 
            = (MessageCollectionType) ((JAXBElement<MessageCollectionType>) unmarshaller.unmarshal(
                    FindBugsFindingType.class.getClassLoader()
                        .getResourceAsStream(messagesFile))).getValue();

        for (Object obj : messageCollection.getContent()) 
        {
            if (obj instanceof BugPatternType)
            {
                new FindBugsFindingType((BugPatternType) obj);
            }
        }
    }
}
