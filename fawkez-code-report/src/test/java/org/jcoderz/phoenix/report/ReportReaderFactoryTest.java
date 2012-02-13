/*
 * $Id: ReportReaderFactoryTest.java 1450 2009-05-09 22:54:06Z amandel $
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

import junit.framework.TestCase;


/**
 * 
 * @author Michael Griffel
 */
public class ReportReaderFactoryTest extends TestCase
{
    /**
     * Constructor for ReportReaderFactoryTest.
     * @param arg0
     */
    public ReportReaderFactoryTest (String arg0)
    {
       super(arg0);
    }


   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(ReportReaderFactoryTest.class);
   }

   /** {@inheritDoc} */
   protected void setUp () throws Exception
   {
      super.setUp();
   }

   /** {@inheritDoc} */
   protected void tearDown () throws Exception
   {
      super.tearDown();
   }

   public void testCreateReader ()
   {
      assertNotNull(ReportReaderFactory.createReader(
          new ReportNormalizer.SourceReport(ReportFormat.CHECKSTYLE, null)));
      assertNotNull(ReportReaderFactory.createReader(
          new ReportNormalizer.SourceReport(ReportFormat.JCOVERAGE, null)));
   }

}
