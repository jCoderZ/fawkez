/*
 * $Id: JCoverageReportReader.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.jcoverage.jaxb.Clazz;
import org.jcoderz.phoenix.jcoverage.jaxb.Coverage;
import org.jcoderz.phoenix.jcoverage.jaxb.Line;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * @author Michael Griffel
 */
public class JCoverageReportReader
      extends AbstractReportReader
{
    /** JAXB context path. */
    public static final String JCOVERAGE_JAXB_CONTEXT_PATH
       = "org.jcoderz.phoenix.jcoverage.jaxb";

   private static final String CLASSNAME 
           = JCoverageReportReader.class.getName();

   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private Coverage mReportDocument;


   JCoverageReportReader ()
      throws JAXBException
   {
      super(JCOVERAGE_JAXB_CONTEXT_PATH);
   }

   /** {@inheritDoc} */
   public final void parse (File f)
      throws JAXBException
   {
      try
      {
         mReportDocument = (Coverage) getUnmarshaller().unmarshal(
                 new FileInputStream(f));
      }
      catch (IOException e)
      {
         throw new JAXBException("Cannot read JCoverage report", e);
      }
   }

   /** {@inheritDoc} */
   public final Map getItems ()
      throws JAXBException
   {
      final Map itemMap = new HashMap();
      final List files = mReportDocument.getClazzes();
      final String baseDir = mReportDocument.getSrc() + File.separator;

      for (final Iterator iterator = files.iterator(); iterator.hasNext(); )
      {
         final Clazz clazz = (Clazz) iterator.next();
         logger.finer("Processing class '" + clazz.getName() + "'");
         final String javaFile = clazzname2Filename (clazz.getName());
         final List itemList = new ArrayList();

         for (final Iterator i = clazz.getCoveredLines().iterator(); 
             i.hasNext(); )
         {
            final Line line = (Line) i.next();
            final Item item = new ObjectFactory().createItem();
            item.setOrigin(Origin.COVERAGE);
            item.setCounter(line.getHits());
            item.setLine(line.getNumber());
            item.setSeverity(Severity.COVERAGE);
            item.setFindingType("coverage"); // FIXME: use type

            itemList.add(item);
         }
         final ResourceInfo info
               = ResourceInfo.lookup(normalizeFileName(baseDir + javaFile));

         if (info != null)
         {
            if (itemMap.containsKey(info))
            {
               final List l = (List) itemMap.get(info);
               l.addAll(itemList);
            }
            else
            {
               itemMap.put(info, itemList);
            }
         }
         else
         {
            logger.finer(
                    "Ignoring findings for resource " + baseDir + javaFile);
         }
      }

      return itemMap;
   }

   private final String clazzname2Filename (String c)
   {
      return c.replaceAll("\\.", "/") + ".java";
   }
}
