/*
 * $Id: FindingsSummary.java 1454 2009-05-10 11:06:43Z amandel $
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcoderz.commons.util.XmlUtil;
import org.jcoderz.phoenix.report.jaxb.Item;

/**
 * This class holds all findings, by type and file for the project.
 *
 * This class in NOT thread save in any way.
 *
 * @author Andreas Mandel
 */
final class FindingsSummary
{
   /** Singleton type findings collector. */
   private static FindingsSummary sFindingsSummary = new FindingsSummary();

   private final Map<String, FindingSummary> mFindings
       = new HashMap<String, FindingSummary>();
   private int mOverallCounter = 0;

   private FindingsSummary ()
   {
      // singleton class only instantiated by the factory
   }

   /**
    * Utility method to get the Singleton.
    * @return the one and only findings summary object.
    */
   public static FindingsSummary getFindingsSummary ()
   {
      return sFindingsSummary;
   }

   /**
    * Generates a key unique for kind of the given finding.
    * @param finding item where to generate the key for.
    * @return a key unique for kind of the given finding.
    */
    public static String getKeyForFinding (Item finding)
    {
        return finding.getFindingType() + "_"
                  + finding.getSeverity().toString();
    }

    /**
     * Generates a key unique for kind of the given finding type and
     *          severity.
     * @param findingType the type to generate the key for.
     * @param severity the severity to generate the key for.
     * @return a key unique for kind of the given finding type and
     *          severity.
     */
     public static String getKeyForFinding (FindingType findingType,
             Severity severity)
     {
         return findingType.getSymbol() + "_" + severity.toString();
     }

   /**
    * Adds the finding to the findings data structure.
    * All references and counters are updated.
    * @param finding the concrete item that was detected
    * @param file the FileSummary object of the detected finding.
    */
   public static void addFinding (Item finding, FileSummary file)
   {
      getFindingsSummary().getFindingSummary(finding)
         .addFinding(finding, file);
   }

   /**
    * Provides access to all findings of the given type.
    * @param findingType the type of the finding.
    * @param severity the severity of the finding.
    * @return a FindingSummary of all findings of
    *          the given FindingType, might be null if
    *          no such finding exists.
    */
   public FindingSummary getFindingSummary (FindingType findingType,
         Severity severity)
   {
       return mFindings.get(getKeyForFinding(findingType, severity));
   }

   /**
    * Returns the FindingSummary appropriate to hold findings of the
    * type of the given Item.
    * If no such summary exists yet, a new one is generated and
    * returned.
    * @param item the item where to return a summary for.
    * @return the FindingSummary appropriate to hold findings of the
    *     type of the given Item.
    */
   public FindingSummary getFindingSummary (Item item)
   {
      final String key = getKeyForFinding(item);
      // cast to make sure we get an exception once item.getFindingType
      // returns a real FindingType
      FindingSummary result = mFindings.get(key);
      if (result == null)
      {
         result = new FindingSummary(item);
      }
      return result;
   }

   /**
    * Returns the map mapping from the type/severity string to stored
    * FindingSummary objects.
    * The returned map is immutable. Stored objects MUST not be
    * modified.
    * @return the map mapping from the type/severity string to stored
    * FindingSummary objects.
    */
   Map<String, FindingSummary> getFindings ()
   {
      return Collections.unmodifiableMap(mFindings);
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      return "[FindingsSummary: " + mFindings + "(" + mOverallCounter + ")]";
   }

    /**
     * Generates a page that lists all findings, that links to the
     * detailed finding pages. The content is ordered by severity and
     * number of occurrences.
     * @param out the writer where to write the html data to.
     * @throws IOException if the data can not be written.
     */
    static void createOverallContent (Writer out) throws IOException
    {
        final Collection<FindingSummary> colAllFindings
                = getFindingsSummary().getFindings().values();
        final FindingSummary[] allFindings
                = colAllFindings.toArray(
                    new FindingSummary[colAllFindings.size()]);

         Arrays.sort(allFindings);

         Severity currentSeverity = null;

         out.write("<table border='0' cellpadding='0' cellspacing='0' "
                 + "width='95%' summary='Summary of all findings.'>");
         int row = 0;
         for (final FindingSummary summary : allFindings)
         {
            if (summary.getSeverity() != currentSeverity)
            {
               out.write("<tr><td colspan='3' class='severityheader'>");
               currentSeverity = summary.getSeverity();
               out.write("<a name='" + currentSeverity.toString() + "'/>");
               out.write("Severity: ");
               out.write(currentSeverity.toString());
               out.write("\n</td></tr>");
               row = 0;
            }
            row++;
            out.write("<tr class='" + currentSeverity
                  + Java2Html.toOddEvenString(row) + "'>");
            out.write("<td class='finding-counter'>");
            out.write(String.valueOf(summary.getCounter()));
            out.write("</td>");
            out.write("<td class='finding-origin'>");
            out.write(summary.getOrigin().toString());
            out.write("</td>");
            out.write("<td class='finding-data' width='100%'>");

            out.write("<a href='");
            out.write(summary.createFindingDetailFilename());
            out.write("' title='");
            out.write(summary.getFindingType().getSymbol());
            out.write("'>");
   //         if (summary.isFindingsHaveSameMessage()
   //               && summary.getFindingMessage() != null)
   //         {
   //            out.write(summary.getFindingMessage());
   //         }
   //         else
            {
               out.write(summary.getFindingType().getShortText());
            }
            out.write("</a></td></tr>\n");
         }
         out.write("</table>");
      }



   /**
    * Holds all findings of a specific type.
    * @author Andreas Mandel
    */
   final class FindingSummary implements Comparable<FindingSummary>
   {
      private final Map<String, FindingOccurrence> mOccurrences
          = new HashMap<String, FindingOccurrence>();
      private final Severity mSeverity;
      private final Origin mOrigin;
      private int mCounter;
      private boolean mFindingsHaveSameMessage = true;
      private final String mFindingMessage;
      private final FindingType mFindingType;

      /**
       * Creates a new FindingSummary to collect findings similiar
       * to the given finding.
       * @param finding the reference Item for the types of findings
       *     collected in this summary.
       */
      public FindingSummary (Item finding)
      {
         final String key = getKeyForFinding(finding);
         mFindingType = FindingType.fromString(finding.getFindingType());
         mSeverity = finding.getSeverity();
         mOrigin = finding.getOrigin();
         mFindingMessage = finding.getMessage();
         mFindings.put(key, this);
      }

      /**
       * @return Returns the counter.
       */
      public int getCounter ()
      {
         return mCounter;
      }

      /**
       * @return Returns the origin of the findings.
       */
      public Origin getOrigin ()
      {
         return mOrigin;
      }

      /**
       * @return Returns the severity.
       */
      public Severity getSeverity ()
      {
         return mSeverity;
      }

      /**
       * @return Returns the findingMessage.
       */
      public String getFindingMessage ()
      {
         return mFindingMessage;
      }
      /**
       * @return Returns the findingsHaveSameMessage.
       */
      public boolean isFindingsHaveSameMessage ()
      {
         return mFindingsHaveSameMessage;
      }

      /**
       * @return Returns the finding type.
       */
      public FindingType getFindingType ()
      {
         return mFindingType;
      }

      /**
       * @return Returns the occurrences.
       */
      public Map<String, FindingOccurrence> getOccurrences ()
      {
         return Collections.unmodifiableMap(mOccurrences);
      }

      public FindingOccurrence getOccurrence (FileSummary fileSummary)
      {
         FindingOccurrence result =
            getOccurrence(fileSummary.getFullClassName());

         if (result == null)
         {
            result = new FindingOccurrence(fileSummary);
         }

         return result;
      }

      public void addFinding (Item finding, FileSummary summary)
      {
         if (mFindingsHaveSameMessage)
         {
            if (mFindingMessage == null)
            {
               mFindingsHaveSameMessage
                  = (finding.getMessage() == null);
            }
            else
            {
               mFindingsHaveSameMessage
                  = mFindingMessage.equals(finding.getMessage());
            }
         }
         getOccurrence(summary).addFinding(finding);
      }

      /** {@inheritDoc} */
      public String toString ()
      {
         return "[" + mFindingType + "(" + mSeverity
               + (mFindingsHaveSameMessage ? " " + mFindingMessage : "")
               + "): " + mOccurrences + "(" + mCounter + ")]";
      }


      /**
       * {@inheritDoc}
       * Be aware that the order (result of {@link #compareTo} can change
       * if new findings are added.
       * The order is from severe with most findings to info with
       * fewer findings.
       */
      public int compareTo (FindingSummary other)
      {
         int result = -mSeverity.compareTo(other.mSeverity);
         if (result == 0)
         {
            result = other.mCounter - mCounter;
         }
         return result;
      }

      private void addOccurrence (FindingOccurrence occurrence)
      {
         mOccurrences.put(occurrence.getFullClassName(), occurrence);
      }

      private FindingOccurrence getOccurrence (String filename)
      {
         return mOccurrences.get(filename);
      }

      public String createFindingDetailFilename ()
      {
         return "finding-" + getSeverity() + "-"
            + getFindingType().getSymbol() + ".html";
      }

      public void createFindingTypeContent (Writer out)
         throws IOException
      {
          // TODO: Handle global findings more nice
         final FindingOccurrence[] allFindings
                 = mOccurrences.values().toArray(new FindingOccurrence[0]);

         Arrays.sort(allFindings);

         out.write("<h1><a href='index.html'>View by Classes</a></h1>");
         out.write("<h1><a href='findings.html'>Findings - Overview</a></h1>");

         out.write("<h1 title='");
         out.write(getFindingType().getSymbol());
         out.write("'>");

         out.write(getSeverity().toString());
         out.write(" ");
         out.write(getFindingType().getShortText());
         out.write(" (");
         out.write(getOrigin().toString());
         out.write(")");
         out.write("</h1>\n");

         if (isFindingsHaveSameMessage()
               && getFindingMessage() != null)
         {
            out.write("<h2>");
            out.write(XmlUtil.escape(getFindingMessage()));
            out.write("</h2>\n");
         }

         if (getWikiPrefix() != null)
         {
            out.write("<a href='" + getWikiPrefix()
                  + getFindingType().getSymbol()
                  + "'>Further info on the wiki.</a>\n");
         }

         out.write("<blockquote>\n");
         out.write(getFindingType().getDescription());
         out.write("</blockquote>\n");


         out.write("<table border='0' cellpadding='0' cellspacing='0' "
                 + "width='95%' summary='Places of this finding.'>");

         for (final FindingSummary.FindingOccurrence
             occurrence : allFindings)
         {
            out.write("<tr><td class='findingtype-counter'>");
            out.write(Integer.toString(occurrence.getFindings().size()));
            out.write("</td><td class='findingtype-class' width='100%'>");
//            out.write("<a href='");
//            out.write(occurrence.getHtmlLink());
//            out.write("'>");
            out.write(occurrence.getFullClassName());
            out.write("</td></tr>");

            out.write("<tr><td class='findingtype-data' colspan='2'>");

            final Iterator<Item> i = occurrence.getFindings().iterator();
            while (i.hasNext())
            {
               final Item item = i.next();
               final String htmlLink = occurrence.getHtmlLink();
               if (htmlLink != null)
               {
                   out.write("<a href='");
                   out.write(occurrence.getHtmlLink());
                   out.write("#LINE");
                   out.write(Integer.toString(item.getLine()));
                   out.write("'>");
               }
               if (!isFindingsHaveSameMessage() && item.getMessage() != null)
               {
                  out.write(XmlUtil.escape(item.getMessage()));
               }
               out.write("&#160;[");
               out.write(Integer.toString(item.getLine()));
               if (item.getColumn() != 0)
               {
                  out.write(":");
                  out.write(Integer.toString(item.getColumn()));
               }
               out.write("]");
               if (htmlLink != null)
               {
                   out.write("</a>");
               }
               if (i.hasNext())
               {
                  out.write(", ");
               }
               if (!isFindingsHaveSameMessage() && item.getMessage() != null)
               {
                  out.write("<br />");
               }
            }
            out.write("</td></tr>\n");
         }
         out.write("</table>\n");
      }

      /**
       * Checks for the wiki prefix to be used.
       * @return the wiki prefix to be used.
       */
      private String getWikiPrefix ()
      {
         return System.getProperty(Java2Html.WIKI_BASE_PROPERTY);
      }


      /**
       * A occurrence of a finding.
       * This class encapsulates all findings of a single type in one file.
       * Be aware that the order (result of {@link #compareTo} can change
       * if new findings are added.
       *
       * @author Andreas Mandel
       */
      final class FindingOccurrence implements Comparable<FindingOccurrence>
      {
         private final FileSummary mFileSummary;
         private final List<Item> mFindingsInFile = new ArrayList<Item>();

         private FindingOccurrence (FileSummary summary)
         {
            mFileSummary = summary;
            addOccurrence(this);
         }

         /**
          * @return the name of the package of this class/file
          */
         public String getPackagename ()
         {
            return mFileSummary.getPackage();
         }

         public void addFinding (Item finding)
         {
            mFindingsInFile.add(finding);
            mCounter++;
            mOverallCounter++;
         }

         public List<Item> getFindings ()
         {
            return Collections.unmodifiableList(mFindingsInFile);
         }

         /**
          * @return ClassName including package.
          */
         public String getFullClassName ()
         {
            return mFileSummary.getFullClassName();
         }

         public String getClassName ()
         {
            return mFileSummary.getClassName();
         }

         public String getHtmlLink ()
         {
            return mFileSummary.getHtmlLink();
         }

         public int countFindingsInFile ()
         {
            return mFindingsInFile.size();
         }

         /** {@inheritDoc} */
         public String toString ()
         {
            return "[" + getClassName() + ": " + findingsToString()
                  + "(" + mFindingsInFile.size() + ")]";
         }

         public String findingsToString ()
         {
            final StringBuilder sb = new StringBuilder();
            sb.append('{');
            final Iterator<Item> i = mFindingsInFile.iterator();
            while (i.hasNext())
            {
               final Item finding = i.next();
               sb.append('@');
               sb.append(finding.getLine());
               sb.append(':');
               sb.append(finding.getColumn());
               if (i.hasNext())
               {
                  sb.append(", ");
               }
            }
            sb.append('}');
            return sb.toString();
         }

         /**
          * {@inheritDoc}
          * Be aware that the order (result of {@link #compareTo} can change
          * if new findings are added.
          * The order is from most findings to fewer findings.
          */
         public int compareTo (FindingOccurrence o)
         {
            return o.mFindingsInFile.size() - this.mFindingsInFile.size();
         }
      }
   }
}
