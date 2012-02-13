/*
 * $Id: SystemFindingType.java 820 2008-05-02 07:17:49Z amandel $
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


/**
 * Enumeration type for system internal findings.
 *
 * @author Andreas Mandel
 */
public final class SystemFindingType
      extends FindingType
{
   private final Severity mSeverity;

   /** Problem in merge. */
   public static final SystemFindingType SYS_PARSE_ERROR =
      new SystemFindingType("SYS_PARSE_ERROR",
         "Failed to parse input file.",
         "Mergin the reports into one result failed. No input from the "
         + "affected sourcew will be available.",
         Severity.ERROR);

   /** Problem in merge. */
   public static final SystemFindingType SYS_ERROR =
      new SystemFindingType("SYS_ERROR",
         "Error during processing.",
         "Please check details.",
         Severity.ERROR);


   private SystemFindingType (String symbol, String shortText,
         String description, Severity severity)
   {
      super(symbol, shortText, description);
      mSeverity = severity;
   }

   /** @return the severity assigned to findings of this type by default. */
   public Severity getSeverity ()
   {
       return mSeverity;
   }

   /**
    * Init of the system finding type.
    */
   public static void initialize ()
   {
       // nothing to initialize yet
   }
}
