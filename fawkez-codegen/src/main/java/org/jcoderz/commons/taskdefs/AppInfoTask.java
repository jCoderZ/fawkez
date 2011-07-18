/*
 * $Id: AppInfoTask.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.taskdefs;


import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Ant task that reads the master app-info.xml file, expands the XInclude
 * elements and writes the whole XML document to a file.
 * <p>
 * This task can also perform a XML schema validation and perform
 * some checks that cannot be done via the XML schema.
 *
 * @author Michael Griffel
 */
public final class AppInfoTask
      extends XsltBasedTask
{
   private static final String JAXP_SCHEMA_LANGUAGE
         = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

   private static final String W3C_XML_SCHEMA
         = "http://www.w3.org/2001/XMLSchema";

   private static final String JAXP_SCHEMA_SOURCE
         = "http://java.sun.com/xml/jaxp/properties/schemaSource";

   /** The default stylesheet name. */
   private static final String DEFAULT_STYLESHEET
         = "xinclude.xsl";

   private static final String APP_INFO_SCHEMA = "app-info.xsd";

   /** flag indicating if we should validate the app-info.xml. */
   private boolean mValidate = true;

   /**
    * Set whether we should validate the app-info.xml file or not.
    * Default is <tt>true</tt>.
    *
    * @param b Whether we should validate the app-info.xml file or not.
    */
   public void setValidate (boolean b)
   {
      mValidate  = b;
   }


   String getDefaultStyleSheet ()
   {
      return DEFAULT_STYLESHEET;
   }

   void checkAttributes ()
         throws BuildException
   {
      // we don'T need the destDir attribute,
      // so we don't call super.checkAttributes() here
      checkAttributeInFile();
      checkAttributeOutFile();
      checkAttributeXslFile();
   }


   void postExecute ()
   {
      if (mValidate)
      {
         performSchemaValidation(getOutFile());
      }
   }

   private void performSchemaValidation (File inFile)
   {
      try
      {
         // create a new XML parser
         final SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setValidating(true);
         final SAXParser parser = factory.newSAXParser();
         parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
         parser.setProperty(JAXP_SCHEMA_SOURCE,
               AppInfoTask.class.getResource(APP_INFO_SCHEMA).toExternalForm());

         final AppInfoSaxHandler handler = new AppInfoSaxHandler();
         parser.parse(new InputSource(new FileInputStream(inFile)), handler);
         if (handler.hasValidationErrors())
         {
            final SAXException e = handler.getParseException();
            throw new BuildException("XML Schema validation failed: " + e, e);
         }
         if (handler.hasWarningMessages())
         {
            final List<String> messages = handler.getWarningMessages();
            for (final Iterator<String> iterator = messages.iterator();
                  iterator.hasNext();)
            {
               final String msg = iterator.next();
               log(msg, Project.MSG_WARN);
            }
         }
         log(inFile + " validated successfully.", Project.MSG_INFO);
      }
      catch (Exception e)
      {
         throw new BuildException("XML Schema validation failed: " + e, e);
      }
   }
}
