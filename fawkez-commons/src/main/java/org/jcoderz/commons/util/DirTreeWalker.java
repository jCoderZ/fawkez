/*
 * $Id: EmptyIterator.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.util;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Intention of this class is to traverse a whole directory with
 * all its file and sub-directory content.
 * 
 * @author Andreas Mandel
 */
public class DirTreeWalker
{
   private static final String CLASSNAME 
      = DirTreeWalker.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);
   private final File mBaseDir;
   private final DirTreeListener mListener;
   private int mDirLevel = 0;

   
   public DirTreeWalker(File baseDir, DirTreeListener listener)
   {
      mBaseDir = baseDir;
      mListener = listener;
   }

   public void start ()
   {
      handleDir(mBaseDir);
   }

   /**
    * Get the current depth from starting dir.
    * @return the dirLevel
    */
   public int getDirLevel ()
   {
       return mDirLevel;
   }

   private void handleDir (File dir)
   {
      mListener.enteringDir(dir);
      File[] files = dir.listFiles();
      if (files != null)
      {
         Arrays.sort(files);
         for (int i = 0; i < files.length; i++)
         {
            handle(files[i]);
         }
      }
      else
      {
    	  logger.fine("Empty dir " + dir);
      }
      mListener.exitingDir(dir);
      mDirLevel--;
   }

   private void handle (File file)
   {
      if (file.isDirectory())
      {
          mDirLevel++;
          handleDir(file);
      }
      else
      {
         handleFile(file);
      }
   }

   private void handleFile (File file)
   {
      mListener.file(file);
   }

}


