/*
 * $Id: Java2Html.java 1238 2008-11-03 12:37:53Z amandel $
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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.util.XMLUtilities;
import org.jcoderz.commons.util.IoUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Responsible to load the catalog file that bundles the 
 * jEdit mode files.
 * Inspired by the ModeCatalogHandler that comes with jEdit. 
 * 
 * The mode files and the catalog are expected to be packed as 
 * resources with the jEdit class in a <code>modes</code> 
 * package to be found. There s no additional flexibility to
 * provide own or modified modes files.
 * 
 * Currently with jEdit4.3pre16 there is no way to use a own 
 * ModeProvider to be able to
 * overload the <code>loadMode(String)</code> method because the
 * ModeProvider is hard linked in the Mode class. So we can not 
 * use our own XModeHandler and overload error handling there.  
 * 
 * Use {@link #loadModes()} to load the catalog and all modes.
 * 
 * @author Andreas Mandel
 */
public final class SyntaxModeCatalogHandler    
    extends DefaultHandler
{
    private static final String CLASSNAME 
        = SyntaxModeCatalogHandler.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
    
    /**
     * A new SyntaxModeCatalogHandler all mode files are retrieved via 
     * class path and expected to resist in the modes package.
     */
    private SyntaxModeCatalogHandler ()
    {
        // use static method to load the data
    }

    /**
     * Load all modes referred by the catalog file provided.
     */
    public static void loadModes ()
    {
        final InputStream in 
            = jEdit.class.getResourceAsStream("/modes/catalog");
        try
        {
            XMLUtilities.parseXML(in, new SyntaxModeCatalogHandler());
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.WARNING, "Failed to load modes catalog, " 
                + "no syntax highlighting will be available in the output.", 
                ex);
        }
        finally
        {
            IoUtil.close(in);
        }
    }

    /**
     * Takes care to find the jEdit catalog.dtd.
     * {@inheritDoc}
     */
    public InputSource resolveEntity (String publicId, String systemId)
    {
        return XMLUtilities.findEntity(systemId, "catalog.dtd", jEdit.class);
    }

    /**
     * Handles the mode elements in the catalog and loads the modes
     * listed there.
     *  
     * {@inheritDoc}
     */
    public void startElement (String uri, String localName,
        String qName, Attributes attrs)
    {
        if ("MODE".equals(qName))
        {
            final String modeName = attrs.getValue("NAME");
            final String file = attrs.getValue("FILE");
            if (file == null)
            {
                LOGGER.log(Level.WARNING, "Mode '" + modeName 
                    + "' doesn't have a FILE attribute");
            }

            Mode mode = ModeProvider.instance.getMode(modeName);
            if (mode == null)
            {
                mode = new Mode(modeName);
                ModeProvider.instance.addMode(mode);
            }

            mode.setProperty("file", "/modes/" + file);

            final String filenameGlob = attrs.getValue("FILE_NAME_GLOB");
            if (filenameGlob != null)
            {
                mode.setProperty("filenameGlob", filenameGlob);
            }
            else
            {
                mode.unsetProperty("filenameGlob");
            }

            final String firstlineGlob = attrs.getValue("FIRST_LINE_GLOB");
            if (firstlineGlob != null)
            {
                mode.setProperty("firstlineGlob", firstlineGlob);
            }
            else
            {
                mode.unsetProperty("firstlineGlob");
            }

            mode.init();
        }
    }
}
