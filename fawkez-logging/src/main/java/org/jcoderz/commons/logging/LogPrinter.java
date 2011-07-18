/*
 * $Id: LogPrinter.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.logging;

import java.io.PrintWriter;

/**
 * An instance of this formats and prints the output of the log viewer.
 * Implementations of this differ in the format they use for printing the data.
 *
 */
public abstract class LogPrinter
{
   private DisplayOptions mDisplayOptions = new DisplayOptions();

   /**
    * Sets the options to use for formatting.
    *
    * @param options The display options to set.
    *
    * @see DisplayOptions
    */
   public void setDisplayOptions (DisplayOptions options)
   {
      try
      {
          mDisplayOptions = (DisplayOptions) options.clone();
      }
      catch (CloneNotSupportedException e)
      {
          throw new RuntimeException("Unexpected exception caught.", e);
      }
   }

   /**
    * Gets the display options.
    *
    * @return The configured display options.
    */
   public final DisplayOptions getDisplayOptions ()
   {
      return mDisplayOptions;
   }

   /**
    * Formats the log data and prints the formatted data using the supplied
    * print writer. A line feed is printed after the log data has been printed.
    *
    * @param printer The PrintWriter to use for printing the data.
    * @param entry The log data to format and print using <code>printer</code>.
    */
   public abstract void print (PrintWriter printer, LogItem entry);

   /**
    * Gets information whether the stack trace of the supplied log file entry
    * should be displayed.
    *
    * @param entry The entry to check.
    *
    * @return true if the stack trace of <code>entry</code> should be displayed;
    * false, else.
    *
    */
   protected boolean displayStackTrace (final LogItem entry)
   {
      return ((entry.isExceptionItem() && mDisplayOptions.displayStackTrace())
            || mDisplayOptions.displayMessageStackTrace());
   }


   /**
    * Get the LogItem needed to complete the stack trace when a
    * '...nnn more' line has been encountered. This depends on the display
    * settings, since we do not want to display the complete stacktrace again
    * when it has been displayed already.
    *
    * @param entry The entry with the '...nnn more' stack trace line.
    * @param info The '...nnn more' stack trace line.
    *
    * @return <code>entry</code> if the complete stack trace has already been
    * displayed; the entry with the complete stacktrace, else.
    */
   protected LogItem getEntryForMoreStackTrace (
         final LogItem entry,
         final StackTraceInfo info)
   {
      LogItem currentEntry = entry.getParentItem();
      LogItem rc = null;
      while ((currentEntry != null) && (rc == null))
      {
         if (displayStackTrace(currentEntry))
         {
            rc = entry;
         }
         else if (currentEntry.getStackTraceLines().size()
               >= info.getMoreLines())
         {
            rc = currentEntry;
         }
         else
         {
            currentEntry = currentEntry.getParentItem();
         }
      }
      return rc;
   }
}
