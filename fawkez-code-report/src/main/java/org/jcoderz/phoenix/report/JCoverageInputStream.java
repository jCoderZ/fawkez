/*
 * $Id: JCoverageInputStream.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * 
 * @author Michael Griffel
 */
public final class JCoverageInputStream 
   extends InputStream
{
   private static final String CLASSNAME = JCoverageInputStream.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);
   
   private InputStream mPatchInputStream;
   
   public JCoverageInputStream (InputStream in)
      throws IOException
   {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      int c = 0;
      boolean isString = false;
      while ((c = in.read()) != -1)
      {
         if (c == '\"')
         {
            isString = ! isString;
         }
         
         if (isString)
         {
            switch (c)
            {
               case '>':
                  out.write("&gt;".getBytes());
                  break;

               case '<':
                  out.write("&lt;".getBytes());
                  break;

               default :
                  out.write(c);
                  break;
            }
         }
         else
         {
            out.write(c);
         }
      }
      mPatchInputStream = new ByteArrayInputStream(out.toByteArray());
   }
   
   /** {@inheritDoc} */
   public int read () throws IOException
   {
      return mPatchInputStream.read();
   }

   /** {@inheritDoc} */
   public int read (byte[] b, int off, int len) throws IOException
   {
      return mPatchInputStream.read(b, off, len);
   }
   
   /** {@inheritDoc} */
   public void close () 
         throws IOException
   {
      if (mPatchInputStream != null)
      {
         mPatchInputStream.close();
         mPatchInputStream = null;
      }
   }
   
}
