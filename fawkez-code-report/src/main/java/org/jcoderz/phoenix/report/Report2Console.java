/*
 * $Id: Java2Html.java 816 2008-05-01 20:04:04Z amandel $
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
/*
 * Java2HTML v0.1 alpha converts a java source code into HTML with
 * syntax highlighting for keywords, comments, strings and chars
 *
 * The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights
 * and limitations under the License.
 *
 * The Original Code is Java2HTML Converter v0.1 alpha.
 * The Initial Developer of the Original Code is Borislav Manolov.
 * Portions created by Borislav Manolov are Copyright (C) 2003,
 * Borislav Manolov. All Rights Reserved.
 *
 * Submit bugs and comments at manfear@web.de
 * 03/03/03
 */
package org.jcoderz.phoenix.report;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jcoderz.commons.util.LoggingUtils;
import org.jcoderz.phoenix.report.jaxb.Report;

/**
 * Read the report and output this to the console in a eclipse
 * friendly way.
 *
 * This is a early 'prototype' version.
 *
 * @author Andreas Mandel
 */
public final class Report2Console
{
   /** Name of this class. */
   private static final String CLASSNAME = Report2Console.class.getName();

   /** The logger used for technical logging inside this class. */
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private static final int SEVERITY_INDENT = 12;
   private java.io.File mInputData;
   private Level mLogLevel = Level.INFO;

   /**
    * Main entry point.
    *
    * @param args The command line arguments.
    * @throws IOException an io exception occurs.
    * @throws JAXBException if the xml can not be parsed.
    */
   public static void main (String[] args)
         throws IOException, JAXBException
   {
      final Report2Console engine = new Report2Console();

      engine.parseArguments(args);
      // Turn on logging
      Logger.getLogger("org.jcoderz.phoenix.report").setLevel(Level.FINEST);
      engine.process();
   }

   /**
    * The input file containing the jcoderz report.
    * @param file input file containing the jcoderz report.
    * @throws IOException if access to the file fails.
    */
   public void setInputFile (java.io.File file) throws IOException
   {
     mInputData = file.getCanonicalFile();
     if (!mInputData.canRead())
     {
        throw new RuntimeException("Can not read report file '"
              + mInputData + "'.");
     }
     logger.config("Using report file " + mInputData + ".");
   }

   /**
    * Starts the actual generation process.
    * @throws JAXBException if the xmp parsing fails.
    * @throws IOException if a IO problem occurs.
    */
   public void process ()
         throws JAXBException, IOException
   {
      final JAXBContext jaxbContext
            = JAXBContext.newInstance("org.jcoderz.phoenix.report.jaxb",
                  this.getClass().getClassLoader());
      final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setValidating(true);
      final Report report = (Report) unmarshaller.unmarshal(mInputData);

      for (final org.jcoderz.phoenix.report.jaxb.File file
          : (List<org.jcoderz.phoenix.report.jaxb.File>) report.getFile())
      {
         try
         {
            outputForFile(file);
         }
         catch (Exception ex)
         {
            logger.log(Level.SEVERE,
                  "Failed to generate report for '" + file.getName() + "'.",
                  ex);
         }
      }

      logger.fine("Done.");
   }

   private void outputForFile (org.jcoderz.phoenix.report.jaxb.File file)
   {
       final String locatorStart
           = file.getPackage() + "." + file.getClassname() + ".";
       final String locatorFile
           = "(" + file.getName().substring(
               file.getName().lastIndexOf("\\") +1);
       final String filler = "                    ";
       for (final org.jcoderz.phoenix.report.jaxb.Item item
           : (List<org.jcoderz.phoenix.report.jaxb.Item>) file.getItem())
       {
           final Severity severity = item.getSeverity();
           PrintStream out = System.out;
           if (Severity.FILTERED.equals(severity)
               || Severity.OK.equals(severity))
           {
               continue;
           }
           else if (Severity.INFO.compareTo(severity) < 0)
           {
               out = System.out;
           }
           else
           {
               out = System.err;
           }
           String severityString = item.getSeverity().toString();
           out.println(severityString
               + filler.substring(0, SEVERITY_INDENT - severityString.length())
               + ": " + item.getMessage());
           out.println(
               "  at " + locatorStart +
               item.getFindingType() + locatorFile + ":"
               + item.getLine() + ")");
           out.flush();
       }
   }

   private void parseArguments (String[] args)
   {
      try
      {
         for (int i = 0; i < args.length; )
         {
            if ("-report".equals(args[i]))
            {
               setInputFile(new java.io.File(args[i + 1]));
            }
            else if ("-loglevel".equals(args[i]))
            {
               setLoglevel(args[i + 1]);
            }
            else
            {
               throw new IllegalArgumentException(
                       "Invalid argument '" + args[i] + "'");
            }
            i += 1 /* command */ + 1 /* argument */;
         }
      }
      catch (IndexOutOfBoundsException e)
      {
         final IllegalArgumentException ex
               = new IllegalArgumentException("Missing value for "
                  + args[args.length - 1]);
         ex.initCause(e);
         throw ex;
      }
      catch (Exception e)
      {
         final IllegalArgumentException ex = new IllegalArgumentException(
               "Problem with argument value for " + args[args.length - 1]);
         ex.initCause(e);
         throw ex;
      }
   }

   private void setLoglevel (String loglevel)
   {
       mLogLevel = Level.parse(loglevel);
       LoggingUtils.setGlobalHandlerLogLevel(Level.ALL);
       logger.fine("Setting log level: " + mLogLevel);
       logger.setLevel(mLogLevel);
   }

}
