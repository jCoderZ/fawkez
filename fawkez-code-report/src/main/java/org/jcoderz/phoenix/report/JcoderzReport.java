/*
 * $Id: JcoderzReport.java 1454 2009-05-10 11:06:43Z amandel $
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
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;
import org.jcoderz.phoenix.report.jaxb.Report;

/**
 * This class implements writing of <code>jcoderz-report.xml</code> files.
 *
 * @author Michael Griffel
 */
public final class JcoderzReport
   extends AbstractReportReader
{
    /** JAXB context path. */
    public static final String JCODERZ_JAXB_CONTEXT_PATH
       = "org.jcoderz.phoenix.report.jaxb";


    private static final String CLASSNAME = JcoderzReport.class.getName();

    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private final Report mReport = new ObjectFactory().createReport();

    /** The report level. */
    private ReportLevel mLevel = ReportLevel.PROD;


   JcoderzReport ()
         throws JAXBException
   {
      super(JCODERZ_JAXB_CONTEXT_PATH);
   }


   /** {@inheritDoc} */
   public Map getItems ()
      throws JAXBException
   {
      throw new NoSuchMethodError();
   }


   /**
    * Sets the report level.
    *
    * @param level The level of the report
    */
   public void setLevel (ReportLevel level)
   {
      mLevel = level;
   }


   /** {@inheritDoc} */
   public void parse (File f)
      throws JAXBException
   {
      // TODO
      throw new RuntimeException("Method not implemented. (TODO)");
   }


   /**
    * Writes the report to the specified stream by using JAXB.
    *
    * @param out where to write the jCoderZ report to.
    * @param items the file items. The items are a Map of ResourceInfo and
    *    a List of the type jCoderZ Item (org.jcoderz.phoenix.report.jaxb.Item)
    * @throws JAXBException for JAXB errors
    */
   public void write (OutputStream out, Map<ResourceInfo, List<Item>> items)
      throws JAXBException
   {
      addItems(mLevel, items);
      writeReport(out);
   }


   /**
    * Just add items of a specific level to the report, do not write the file
    * to persistent storage yet.
    *
    * @param level The level under which to add the items.
    * @param items A map of items.
    * @throws JAXBException When the JAXB file representation can not
    *      be created.
    */
   public void addItems (ReportLevel level, Map<ResourceInfo, List<Item>> items)
      throws JAXBException
   {
      final Map<ResourceInfo, List<Item>> files = items;

      for (Entry<ResourceInfo, List<Item>> entry : files.entrySet())
      {
         final ResourceInfo info = entry.getKey();
         final List<Item> itemList = entry.getValue();
         final org.jcoderz.phoenix.report.jaxb.File f
            = new org.jcoderz.phoenix.report.jaxb.ObjectFactory().createFile();
         if (info != null)
         {
             f.setName(info.getResourceName());
             f.setClassname(info.getClassname());
             f.setPackage(info.getPackage());
             f.setSrcDir(info.getSourcDir());
             f.setLoc(info.getLinesOfCode());
         }
         f.setLevel(level);
         f.getItem().addAll(itemList);

         mReport.getFile().add(f);
      }
   }


   /**
    * Write the report to persistent storage.
    *
    * @param out The output stream to write the report to.
    * @throws JAXBException In case a marshalling exception occurs.
    */
   public void writeReport (OutputStream out)
      throws JAXBException
   {
      getMarshaller().marshal(mReport, out);
   }


   /**
    * Sets the project's home folder.
    *
    * @param s the home folder
    */
   public void setProjectHome (String s)
   {
      mReport.setProjectHome(s);
   }


   /**
    * Gets the project's home folder.
    *
    * @return the home folder
    */
   public String getProjectHome ()
   {
      return mReport.getProjectHome();
   }


   /**
    * Sets the project name.
    *
    * @param s the name of the project
    */
   public void setProjectName (String s)
   {
      mReport.setName(s);
   }


   /**
    * Gets the project name.
    *
    * @return the name of the project
    */
   public String getProjectName ()
   {
      return mReport.getName();
   }

   public void addSystemLevelIssue (String message, Throwable e,
       ResourceInfo res)
   {
       try
       {
           final Item item = new ObjectFactory().createItem();
           item.setMessage(message);
           item.setSeverity(Severity.ERROR);
           item.setFindingType(SystemFindingType.SYS_ERROR.getSymbol());
           item.setOrigin(Origin.SYSTEM);
           addItems(ReportLevel.PROD, Collections.singletonMap(res,
               Collections.singletonList(item)));
       }
       catch (JAXBException ex)
       {
           // give up.. it is about time
           throw new RuntimeException(
               "Failed to add detail for " + e, ex);
       }
   }

}
