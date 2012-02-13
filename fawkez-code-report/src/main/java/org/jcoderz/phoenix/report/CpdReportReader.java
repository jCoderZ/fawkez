/*
 * $Id: CpdReportReader.java 1011 2008-06-16 17:57:36Z amandel $
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

import org.jcoderz.phoenix.cpd.jaxb.Duplication;
import org.jcoderz.phoenix.cpd.jaxb.PmdCpd;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * CPD Report Reader.
 *
 * @author Michael Griffel
 */
public final class CpdReportReader
      extends AbstractReportReader
{
   /** JAXB context path. */
   public static final String CPD_JAXB_CONTEXT_PATH
      = "org.jcoderz.phoenix.cpd.jaxb";

   private static final String CLASSNAME = CpdReportReader.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private PmdCpd mReportDocument;

   public CpdReportReader ()
         throws JAXBException
   {
      super(CPD_JAXB_CONTEXT_PATH);
   }

   /** {@inheritDoc} */
   public void parse (File f) throws JAXBException, FileNotFoundException
   {
      mReportDocument = (PmdCpd) getUnmarshaller().unmarshal(
            new FileInputStream(f));
   }

   /** {@inheritDoc} */
   protected Map getItems () throws JAXBException
   {
      final Map result = new HashMap();

      for (final Iterator iterator
              = mReportDocument.getDuplication().iterator();
           iterator.hasNext();)
      {
         final Duplication duplication = (Duplication) iterator.next();
         final List filez = duplication.getFile();
         for (int i = 0; i < filez.size(); i++)
         {
            final org.jcoderz.phoenix.cpd.jaxb.File file
               = (org.jcoderz.phoenix.cpd.jaxb.File) filez.get(i);

            final String key = normalizeFileName(file.getPath());
            final ResourceInfo info = ResourceInfo.lookup(key);
            if (info != null)
            {
               final Item item = new ObjectFactory().createItem();
               item.setMessage(constructMessage(i, filez, duplication));
               item.setOrigin(Origin.CPD);
               item.setSeverity(Severity.CPD);
               item.setFindingType(CpdFindingType.NAME);
               item.setLine(file.getLine());
               item.setEndLine(file.getLine() + duplication.getLines());

               if (result.get(info) == null)
               {
                  final List list = new ArrayList();
                  list.add(item);
                  result.put(info, list);
               }
               else
               {
                  final List list = (List) result.get(info);
                  list.add(item);
               }
            }
            else
            {
               logger.finer("Ignoring findings for resource " + key);
            }
         }
      }
      return result;
   }

   /**
    * @param file
    * @param filez
    * @return
    */
   private String constructMessage (int currentIndex, List filez,
         Duplication duplication)
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("Copied and pasted code. ");
      sb.append(duplication.getTokens());
      sb.append(" equal tokens (");
      sb.append(duplication.getLines());
      sb.append(" lines) found in ");
      sb.append(filez.size()).append(" locations.  See also: ");

      for (int i = 0; i < filez.size(); i++)
      {
         if (i == currentIndex)
         {
            continue; // skip current finding
         }

         final org.jcoderz.phoenix.cpd.jaxb.File file
               = (org.jcoderz.phoenix.cpd.jaxb.File) filez.get(i);

         final ResourceInfo info = ResourceInfo.lookup(file.getPath());
         final String resourceName;
         if (info != null)
         {
            resourceName = info.getPackage() + "." + info.getClassname();
         }
         else
         {
            resourceName = file.getPath();
         }
         sb.append(resourceName + ":" + file.getLine() + " ");
      }
      return sb.toString();
   }

}
