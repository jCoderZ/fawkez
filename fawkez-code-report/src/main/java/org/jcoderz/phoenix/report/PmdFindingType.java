/*
 * $Id: PmdFindingType.java 128 2006-12-08 20:13:10Z amandel $
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

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jcoderz.phoenix.pmd.ruleset.jaxb.Rule;
import org.jcoderz.phoenix.pmd.ruleset.jaxb.Ruleset;

/**
 * Adds the PMD ruleset description as finding type map.
 *
 * @author Michael Griffel
 */
public final class PmdFindingType
      extends FindingType
{
   private static final String CLASSNAME = PmdFindingType.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private static final String PMD_RULESET_JAXB_CONTEXT
      = "org.jcoderz.phoenix.pmd.ruleset.jaxb";

   private static final String PMD_RULESET_PROPERTIES_FILE
      = "rulesets/rulesets.properties";

   private final int mPriority;

   /**
    * Constructor.
    * @param symbol
    * @param shortText
    * @param description
    */
   private PmdFindingType (
         String symbol, String shortText, String description, int priority)
   {
      super(symbol, shortText, description);
      mPriority = priority;
   }

   /**
    *
    */
   public static void initialize ()
   {
      try
      {
         final Class clazz = PmdFindingType.class;
         final Properties properties = new Properties();
         properties.load(clazz.getClassLoader().getResourceAsStream(
                     PMD_RULESET_PROPERTIES_FILE));

         final String rulesets = (String) properties.get("rulesets.filenames");
         final StringTokenizer st = new StringTokenizer(rulesets, ",");

         final JAXBContext jaxbContext
            = JAXBContext.newInstance(PMD_RULESET_JAXB_CONTEXT,
                PmdFindingType.class.getClassLoader());
         final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         unmarshaller.setEventHandler(new PmdReportReader());

         while (st.hasMoreTokens())
         {
            final String rulesetResource = st.nextToken();
            logger.finest("Try to unmarshalling " + rulesetResource);
            final InputStream in = clazz.getClassLoader().getResourceAsStream(
                  rulesetResource);
            addRulesetFindings(unmarshaller, in);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Cannot initialize PmdFindingTypes", e);
      }
   }

   private static void addRulesetFindings (Unmarshaller unmarshaller,
                                           InputStream in)
         throws JAXBException
   {
      final Ruleset ruleset = (Ruleset) unmarshaller.unmarshal(in);
      for (final Iterator iterator = ruleset.getRule().iterator();
            iterator.hasNext();)
      {
         final Rule rule = (Rule) iterator.next();
         final String type = rule.getName();
         final String shortDescription = rule.getName();
         String details = null;
         String example = null;
         int priority = 0;
/* FIXME: !!!!
         for (final Iterator ruleTypeIterator
                 = rule.getDescriptionOrExampleOrPriority().iterator();
              ruleTypeIterator.hasNext();)
         {
            final Object element = ruleTypeIterator.next();
            if (element instanceof Rule.Example)
            {
               final Rule.Example e = (Rule.Example) element;
               example = "<pre>" + e.getValue() + "</pre>";
            }
            else if (element instanceof Rule.Description)
            {
               final Rule.Description e = (Rule.Description) element;
               details = e.getValue().trim();
            }
            else if (element instanceof Rule.Priority)
            {
               final Rule.Priority e = (Rule.Priority) element;
               priority = e.getValue();
            }
         }
*/
         String externalLink = "";
         if (rule.isSetExternalInfoUrl())
         {
            externalLink = "<p>Additional info can be found at this <a href='"
               + rule.getExternalInfoUrl() + "'>" + rule.getExternalInfoUrl()
               + " site</a>.</p>";
         }

         
         new PmdFindingType(type, shortDescription,
                  details + "<br/>" + example + externalLink, priority);
      }
   }

   /**
    * Returns the priority.
    * @return the priority.
    */
   public int getPriority ()
   {
      return mPriority;
   }

}
