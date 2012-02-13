/*
 * $Id: FindingType.java 1509 2009-06-07 20:14:07Z amandel $
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jcoderz.commons.util.StringUtil;

/**
 * Base class identifies the unique type of a finding.
 * 
 * @author Andreas Mandel
 */
public class FindingType
{
    private static final String CLASSNAME = FindingType.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);  
    private static final Map<String, FindingType> 
        FINDING_TYPES = new HashMap<String, FindingType>();
    private static final Set<Origin> 
        INITIALIZED_FINDING_TYPES = new HashSet<Origin>();
    private final String mSymbol;
    private final String mShortText;
    private final String mDescription;


    protected FindingType (String symbol, String shortText, String description)
    {
        mSymbol = symbol.intern();
        mShortText 
            = StringUtil.isNullOrBlank(shortText) ? mSymbol : shortText;
        mDescription 
            = StringUtil.isNullOrBlank(description) ? mShortText : description;
        FINDING_TYPES.put(mSymbol, this);
    }

   /**
    * Retrieves the finding type based on it's symbol. 
    * @param symbol the symbol to look up.
    * @return the findings type that holds the given symbol.
    */
   public static FindingType fromString (String symbol)
   {
      // touch the class to get the static initializer to be called
      LazyInit.class.getName();

      FindingType result = FINDING_TYPES.get(symbol);
      if (result == null)
      {
         result = new FindingType(symbol, null, null);
      }
      return result;
   }

   /**
    * Initializes the findings with the given origin.
    * @param origin the class of findings to be initialized.
    */
   public static void initialize (Origin origin)
   {
       if (!INITIALIZED_FINDING_TYPES.contains(origin))
       {
           INITIALIZED_FINDING_TYPES.add(origin);
           if (Origin.CHECKSTYLE.equals(origin))
           {
               CheckstyleFindingType.initialize();
           }
           else if (Origin.COVERAGE.equals(origin))
           {
               // No stuff here
           }
           else if (Origin.FINDBUGS.equals(origin))
           {
               FindBugsFindingType.initialize();
           }
           else if (Origin.PMD.equals(origin))
           {
               PmdFindingType.initialize();
           }
           else if (Origin.CPD.equals(origin))
           {
               CpdFindingType.initialize();
           }
           else if (Origin.SYSTEM.equals(origin))
           {
               SystemFindingType.initialize();
           }
           else
           {
               GenericReportReader.initialize(origin);
           }
       }
   }
   
   /**
    * Returns the symbol of this finding type.
    * @return the symbol of this finding type.
    */
   public String getSymbol ()
   {
      return mSymbol;
   }

   /**
    * Returns the short text description of this finding type.
    * Should be a one liner.
    * @return the short text description of this finding type.
    */
   public String getShortText ()
   {
      return mShortText;
   }

   /**
    * Returns a long description of this finding type. Might 
    *   contain html markup.
    * @return the long description of this finding type.
    */
   public String getDescription ()
   {
      return mDescription;
   }

   /**
    * Returns the finding type symbol as its string representation.
    * @return the finding type symbol as its string representation.
    */
   public String toString ()
   {
      return mSymbol;
   }

   /** {@inheritDoc} */
   public int hashCode ()
   {
      return mSymbol.hashCode();
   }

   /** {@inheritDoc} */
   public boolean equals (Object o)
   {
      return (o instanceof FindingType)
              && mSymbol.equals(((FindingType) o).mSymbol);
   }

   protected static class LazyInit
   {
      static
      {
          initialize(Origin.CHECKSTYLE);
          initialize(Origin.FINDBUGS);
          initialize(Origin.PMD);
          initialize(Origin.CPD);
          initialize(Origin.SYSTEM);
      }
   }
}
