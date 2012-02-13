/*
 * $Id: FindBugsReportReader.java 1011 2008-06-16 17:57:36Z amandel $
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

import org.jcoderz.phoenix.findbugs.jaxb.BugCollection;
import org.jcoderz.phoenix.findbugs.jaxb.BugInstanceType;
import org.jcoderz.phoenix.findbugs.jaxb.Class;
import org.jcoderz.phoenix.findbugs.jaxb.Field;
import org.jcoderz.phoenix.findbugs.jaxb.Int;
import org.jcoderz.phoenix.findbugs.jaxb.Method;
import org.jcoderz.phoenix.findbugs.jaxb.SourceLine;
import org.jcoderz.phoenix.findbugs.jaxb.SourceLineType;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 *
 * @author Michael Griffel
 */
public final class FindBugsReportReader
   extends AbstractReportReader
{
   /** JAXB context path. */
   public static final String FINDBUGS_JAXB_CONTEXT_PATH
      = "org.jcoderz.phoenix.findbugs.jaxb";

   private static final String CLASSNAME = FindBugsReportReader.class.getName();

   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private BugCollection mReportDocument;


   FindBugsReportReader ()
      throws JAXBException
   {
      super(FINDBUGS_JAXB_CONTEXT_PATH);
   }

   /** {@inheritDoc} */
   public void parse (File f)
      throws JAXBException
   {
      try
      {
         mReportDocument = (BugCollection) getUnmarshaller().unmarshal(
               new JCoverageInputStream(new FileInputStream(f)));
      }
      catch (IOException e)
      {
         throw new JAXBException("Cannot read JCoverage report", e);
      }
   }

   /** {@inheritDoc} */
   public Map getItems ()
      throws JAXBException
   {
      final Map itemMap = new HashMap();

      final List bugInstances = mReportDocument.getBugInstance();
      logger.fine("Found #" + bugInstances.size() + " FindBugs bug instances!");

      final List sourceDirs
         = mReportDocument.getProject().getSrcDir();
      logger.finer("Using source dir '" + sourceDirs + "'");

      for (final Iterator iterator = bugInstances.iterator();
            iterator.hasNext();)
      {
         final BugInstanceType bugInstance = (BugInstanceType) iterator.next();

         final List list = bugInstance.getClassOrFieldOrMethod();
         final Item item = new ObjectFactory().createItem();
         final List objectMessageList = new ArrayList();

         item.setMessage(bugInstance.getLongMessage());
         boolean topLevelSourceLineRead = false;
         for (final Iterator iter = list.iterator(); iter.hasNext();)
         {
            final Object element = iter.next();
            objectMessageList.add(toString(element));
            if (element instanceof Class)
            {
               if (item.isSetOrigin())
               {
                  continue;
               }

               final Class c = (Class) element;

               final String clazz = c.getClassname();
               logger.finer("Processing class '" + clazz + "'");
               final String javaFile = convertToRelativeJavaFile(clazz);

               final ResourceInfo info = findResourceInfo(sourceDirs, javaFile);

               if (info != null)
               {
                  List itemList = (List) itemMap.get(info);
                  if (itemList == null)
                  {
                     itemList = new ArrayList();
                     itemMap.put(info, itemList);
                  }
                  item.setOrigin(Origin.FINDBUGS);
                  item.setSeverity(bugInstance.getPriority());
                  item.setFindingType(bugInstance.getType());
                  itemList.add(item);
                  logger.finest("Adding findings for resource " + javaFile);
               }
               else
               {
                   logger.finer("Ignoring findings for resource " + javaFile);
               }
            }
            else if (element instanceof SourceLine)
            {
               // Can be more specific info so allow override
               // if given data is not concrete. 
               // There are finders like IL_INFINITE_LOOP which
               // report additional SourceLine items that point to 
               // informative other lines rather than the buginstance.
               // Til we know how to get the correct line we should leave it
               // like that. (see also http://tinyurl.com/ycol9h ff.)
               if (topLevelSourceLineRead 
                   && item.isSetLine() && item.getLine() > 0)
               {
                   continue;
               }
               logger.finer("Adding source line information to item "
                     + item.getFindingType());
               final SourceLine sourceLine = (SourceLine) element;
               if (sourceLine.isSetStart())
               {
                   item.setLine(sourceLine.getStart());
                   topLevelSourceLineRead = true;
                   if (sourceLine.isSetEnd())
                   {
                       item.setEndLine(sourceLine.getEnd());
                   }
               }
            }
            else if (element instanceof Method)
            {
               if (item.isSetLine())
               {
                  continue;
               }
               if (((Method) element).isSetSourceLine())
               {
                  logger.finer("Adding source line information for method"
                        + " to item " + item.getFindingType());
                  final SourceLineType sourceLine
                        = ((Method) element).getSourceLine();
                  if (sourceLine.isSetStart())
                  {
                      item.setLine(sourceLine.getStart());
                      if (sourceLine.isSetEnd())
                      {
                          item.setEndLine(sourceLine.getEnd());
                      }
                  }
               }
            }
         }
      }

      return itemMap;
   }

   private ResourceInfo findResourceInfo (List sourceDirs, String javaFile)
   {
      ResourceInfo info = null;
      for (final Iterator iterator = sourceDirs.iterator();
            iterator.hasNext();)
      {
         final String srcDir = (String) iterator.next() + File.separator;
         final String key = normalizeFileName(srcDir + javaFile);
         logger.finest("Looking for file: " + key);
         info = ResourceInfo.lookup(key);
         if (info != null)
         {
            break;
         }
      }
      return info;
   }

   private String toString (Object element)
   {
      final String ret;
      if (element instanceof Class)
      {
         final Class c = (Class) element;
         ret = c.getClassname();
      }
      else if (element instanceof Method)
      {
         final Method m = (Method) element;
         ret = m.getName() + m.getSignature();
      }
      else if (element instanceof Field)
      {
         final Field f = (Field) element;
         ret = f.getName();
      }
      else if (element instanceof SourceLine)
      {
         final SourceLine sl = (SourceLine) element;
         ret = sl.getStart() + "-" + sl.getEnd();
      }
      else if (element instanceof Int)
      {
        final Int i = (Int) element;
        ret = String.valueOf(i.getValue());
      }
      else
      {
         ret = String.valueOf(element);
      }
      return ret;
   }

   private String convertToRelativeJavaFile (String clzznm)
   {
      String clazzname = clzznm;
      if (clazzname.indexOf('$') != -1) // inner clazz
      {
         clazzname = clazzname.substring(0, clazzname.indexOf('$'));
      }
      return clazzname.replace('.', File.separatorChar) + ".java";
   }
}
