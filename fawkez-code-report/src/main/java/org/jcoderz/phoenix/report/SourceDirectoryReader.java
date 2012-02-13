/*
 * $Id: SourceDirectoryReader.java 1509 2009-06-07 20:14:07Z amandel $
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
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.report.jaxb.Item;

/**
 * @author Michael Griffel
 */
public final class SourceDirectoryReader
      extends AbstractReportReader
{
   private static final String CLASSNAME
           = SourceDirectoryReader.class.getName();

   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private final Map<ResourceInfo, List<Item>> mSources 
       = new HashMap<ResourceInfo, List<Item>>();
   
   /** 
    * This collection holds directory names that should never 
    * contain any source files. 
    **/
   private static final Collection<String> BLACKLISTED_DIR_NAMES 
       = Collections.unmodifiableCollection(
           Arrays.asList(new String[] {".svn", "CVS"}));

   SourceDirectoryReader ()
         throws JAXBException
   {
      super(JcoderzReport.JCODERZ_JAXB_CONTEXT_PATH);
   }

   /** {@inheritDoc} */
   protected Map<ResourceInfo, List<Item>> getItems ()
         throws JAXBException
   {
      return Collections.unmodifiableMap(mSources);
   }

   /** {@inheritDoc} */
   public void parse (File f)
         throws JAXBException, FileNotFoundException
   {
      if (! f.isDirectory())
      {
         throw new RuntimeException(
             "The given source directory '" + f.getAbsolutePath() 
             + "' is not a valid directory.");
      }
      addSourceFiles(f, null, f.getAbsolutePath());
   }

   private void addSourceFiles (File directory, String pkg, String sourceDir)
   {
      final File[] files = directory.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         final String resourceName = files[i].getAbsolutePath();
         if (files[i].isDirectory())
         {
            if (BLACKLISTED_DIR_NAMES.contains(files[i].getName()))
            {
                logger.finer("Ignoring source dir: '" + files[i] + "'");
            }
            else
            {
                final String subpkg;
                if (pkg == null)
                {
                   subpkg = files[i].getName();
                }
                else
                {
                   subpkg = pkg + "." + files[i].getName();
                }
                addSourceFiles(files[i], subpkg, sourceDir);
            }
         }
         else 
         {
            addResource(pkg, sourceDir, resourceName);
         }
      }
      // register package.html if not already registered (only in **/src/java**)
      final String packageHtml 
          = directory.getAbsolutePath() + File.separator + "package.html";
      if (ResourceInfo.lookup(packageHtml) == null
            && packageHtml.matches(".*/src/java.*"))
      {
         ResourceInfo.register(packageHtml, pkg, sourceDir);
      }
   }

   private void addResource (
            String pkg, String sourceDir, final String resourceName)
   {
      final ResourceInfo info
            = ResourceInfo.register(resourceName, pkg, sourceDir);
      mSources.put(info, Collections.EMPTY_LIST);
   }
}
