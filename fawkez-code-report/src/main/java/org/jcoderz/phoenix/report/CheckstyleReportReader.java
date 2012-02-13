/*
 * $Id: CheckstyleReportReader.java 1404 2009-04-14 12:34:34Z amandel $
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.phoenix.checkstyle.jaxb.Checkstyle;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * This class is used to read the XML report from checkstyle.
 *
 * The checkstyle report is also transformed to the jCoderZ.org format.
 *
 * @author Michael Griffel
 */
public final class CheckstyleReportReader
      extends AbstractReportReader
{
   /** JAXB context path. */
   public static final String CHECKSTYLE_JAXB_CONTEXT_PATH
      = "org.jcoderz.phoenix.checkstyle.jaxb";

   private static final String CLASSNAME
       = CheckstyleReportReader.class.getName();

   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private Checkstyle mReportDocument;

   CheckstyleReportReader ()
         throws JAXBException
   {
      super(CHECKSTYLE_JAXB_CONTEXT_PATH);
   }

   /** {@inheritDoc} */
   public void parse (File f)
         throws FileNotFoundException, JAXBException
   {
      final FileInputStream is = new FileInputStream(f);
      try
      {
          mReportDocument 
              = (Checkstyle) getUnmarshaller().unmarshal(is);
      }
      finally
      {
          IoUtil.close(is);
      }
   }

   protected Map getItems ()
         throws JAXBException
   {
      if (logger.isLoggable(Level.FINE))
      {
         logger.entering(CLASSNAME, "getItems");
      }

      final Map itemMap = new HashMap();

      final List files = mReportDocument.getFileOrErrorOrException();

      for (final Iterator iterator = files.iterator(); iterator.hasNext(); )
      {
         final Object o = iterator.next();

         if (o instanceof org.jcoderz.phoenix.checkstyle.jaxb.File)
         {
            final org.jcoderz.phoenix.checkstyle.jaxb.File f
               = (org.jcoderz.phoenix.checkstyle.jaxb.File) o;
            final String resourceFilename = normalizeFileName(f.getName());

            final List errors = f.getErrorOrException();
            final List items = createReportItems(resourceFilename, errors);

            addItemsToResource(itemMap, resourceFilename, items);
         }
      }

      if (logger.isLoggable(Level.FINE))
      {
         logger.exiting(CLASSNAME, "getItems", itemMap);
      }
      return itemMap;
   }

   private void addItemsToResource (
         Map itemMap, String resourceFilename, List items)
   {
      final ResourceInfo info = ResourceInfo.lookup(resourceFilename);
      if (info != null)
      {
         if (itemMap.get(info) != null)
         {
            final List l = (List) itemMap.get(info);
            l.addAll(items);
         }
         else
         {
            itemMap.put(info, items);
         }
      }
      else
      {
         logger.finer("Ignore findings for resource " + resourceFilename);
      }
   }

   private List createReportItems (String resourceFilename, List errors)
         throws JAXBException
   {
      final List ret = new ArrayList();

      for (final Iterator iterator = errors.iterator(); iterator.hasNext(); )
      {
         final Object o = iterator.next();

         if (o instanceof org.jcoderz.phoenix.checkstyle.jaxb.Error)
         {
            final org.jcoderz.phoenix.checkstyle.jaxb.Error error
               = (org.jcoderz.phoenix.checkstyle.jaxb.Error) o;

            final Item item = new ObjectFactory().createItem();

            if (error.isSetSeverity())
            {
               item.setSeverity(error.getSeverity());
            }
            item.setMessage(error.getMessage());
            item.setLine(error.getLine());
            item.setColumn(error.getColumn());
            item.setOrigin(Origin.CHECKSTYLE);

            final FindingType type = CheckstyleFindingType.
               detectFindingTypeForMessage(error.getMessage());
            if (type == null)
            {
               item.setFindingType(sourceToClass(error.getSource()));
               logger.log(Level.INFO, "Could not find finding type for "
                   + "Checkstyle finding with message '" + error.getMessage()
                   + "'.");
            }
            else
            {
               item.setFindingType(type.getSymbol());
               item.setSeverity(((CheckstyleFindingType) type).getSeverity());
            }

            ret.add(item);
         }
      }

      return ret;
   }

   private static String sourceToClass (String source)
   {
       final int i = source.lastIndexOf('.');
       return source.substring(i + 1);
   }

}
