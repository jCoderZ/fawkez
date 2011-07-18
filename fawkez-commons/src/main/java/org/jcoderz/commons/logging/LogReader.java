/*
 * $Id: LogReader.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This implements a reader reading from log files. It can be used for reading
 * the next entry from the log file, which matches the filter criteria or
 * skipping a number of log file entries.
 *
 */
public class LogReader
{
   private final List mBufferedLines = new ArrayList();
   private final BufferedReader mReader;
   private final File mFile;
   private final List mFilters = new ArrayList();

   /**
    * Creates a new LogReader for reading from the supplied file.
    *
    * @param fileName The name of the file to read.
    *
    * @throws InstantiationException in case there is an error opening the file
    * for reading.
    */
   LogReader (final String fileName)
         throws InstantiationException
   {
      try
      {
         mFile = new File(fileName);
         mReader = new BufferedReader(new FileReader(mFile));
      }
      catch (Exception ex)
      {
         final InstantiationException iex = new InstantiationException(
               "Cannot install LogReader for file '" + fileName + "'.");
         iex.initCause(ex);
         throw iex;
      }

   }

   /**
    * Installs a new filter for filtering log file entries.
    *
    * @param filter The filter to add to the already stored filters.
    */
   void addFilter (final Filter filter)
   {
      mFilters.add(filter);
   }

   /**
    * Gets the next LogFileEntry from the log file, which passes all installed
    * filters. If end of file is reached before an entry has been found matching
    * all criteria, this returns null.
    * Each LogFileEntry instance being returned by this should be released if it
    * is not needed anymore.
    *
    * @return the next LogFileEnbtry passing all filters or null if no such
    * available.
    * @throws LoggingException if an error occurs.
    */
   LogFileEntry readLogFileEntry ()
         throws LoggingException
   {
      LogFileEntry rc = null;
      final LogFileEntry currentEntry = LogFileEntry.getLogFileEntry();
      int numBufferedLines = mBufferedLines.size();
      int bufferedLine = 0;
      boolean readBuffered;
      boolean consumedLine = false;

      while (((bufferedLine < numBufferedLines) || available()) && (rc == null))
      {
         final StringBuffer currentLine;
         if (bufferedLine < numBufferedLines)
         {
            currentLine = (StringBuffer) mBufferedLines.get(bufferedLine++);
            readBuffered = true;
         }
         else
         {
            currentLine = readLine();
            readBuffered = false;
         }
         if (currentLine != null)
         {
            try
            {
               if (currentEntry.addLogLine(currentLine))
               {
                  if (! readBuffered)
                  {
                     mBufferedLines.add(currentLine);
                  }
                  consumedLine = true;
               }
               else
               {
                  mBufferedLines.clear();
                  mBufferedLines.add(currentLine);
                  numBufferedLines = 1;
                  bufferedLine = 0;
                  consumedLine = false;
                  if (passesFilters(currentEntry))
                  {
                     rc = currentEntry;
                  }
                  else
                  {
                     currentEntry.reset();
                  }
               }
            }
            catch (Exception ex)
            {
               /* in case of exception the entry currently in process is
                  returned and the current log line is discarded */
               System.err.println("Got an exception when processing line: "
                     + currentLine);
               System.err.println(ex);
               ex.printStackTrace();
               mBufferedLines.clear();
               numBufferedLines = 0;
               bufferedLine = 0;
               if (consumedLine && passesFilters(currentEntry))
               {
                  rc = currentEntry;
               }
            }
         }
      }
      if ((rc == null) && consumedLine && passesFilters(currentEntry))
      {
         rc = currentEntry;
         mBufferedLines.clear();
      }
      if (rc != currentEntry)
      {
         currentEntry.release();
      }
      return rc;
   }

   /**
    * Checks whether more data is to read from the log file.
    *
    * @return true if the log file contains data not already read by this;
    * false, else.
    *
    * TODO: Add some more criteria, e.g. modification time to detect log file
    * switches etc.
    */
   boolean available ()
   {
      boolean rc = false;

      try
      {
         rc = mReader.ready();
      }
      catch (IOException iex)
      {
         System.err.println(
               "Error while checking whether more data is available");
         iex.printStackTrace();
      }
      return rc;
   }

   /**
    * Checks whether the LogFileEntry passes all filters. An implicit filter is
    * that the type of the entry has been successfully parsed.
    *
    * @param entry The LogFileEntry to check.
    *
    * @return true if <code>entry</code>passes all filters.
    */
   private boolean passesFilters (final LogFileEntry entry)
   {
      boolean rc = entry.getType() != null;
      for (final Iterator filterIterator = mFilters.iterator();
            filterIterator.hasNext() && rc; )
      {
         final Filter filter = (Filter) filterIterator.next();
         rc = filter.isPassable(entry);
      }
      return rc;
   }

   /**
    * Reads the current line from the log file.
    *
    * @return line wrapped in a StringBuffer
    */
   private StringBuffer readLine ()
   {
      StringBuffer rc = null;
      try
      {
         final String line = mReader.readLine();
         if (line != null)
         {
            rc = new StringBuffer(line);
         }
      }
      catch (Exception ex)
      {
         System.err.println("Caught an exception reading the current log line");
         ex.printStackTrace();
      }
      return rc;
   }

   void close ()
   {
      if (mReader != null)
      {
         try
         {
            mReader.close();
         }
         catch (IOException iex)
         {
            System.err.println("Error closing this:" + iex);
            iex.printStackTrace();
         }
      }
   }
}
