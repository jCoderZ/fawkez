/*
 * $Id: NestedLineFormat.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;

/**
 * Formats and parses log lines for nested messages.
 *
 */
public final class NestedLineFormat
      extends ContinuationLineFormat
{
   /** The number of fields added to the basic continuation line format.
     * The cause field is added.
     */
   private static final int NUMBER_OF_ADDITIONAL_PARAMETERS = 1;

   private static final int CAUSE_INDEX = NUMBER_OF_PARAMETERS;

   /** The fields added to the basic format. No fields are added. */
   private static final String ADDITIONAL_LOGLINE_FORMAT_PATTERN
        = " Caused by: {" + CAUSE_INDEX + "}";

  /**
    * Creates a new instance of this and initializes the message format.
    */
   public NestedLineFormat ()
   {
      super(LogLineFormat.NESTED_MESSAGE, ADDITIONAL_LOGLINE_FORMAT_PATTERN,
            NUMBER_OF_ADDITIONAL_PARAMETERS);
      setFormats(getFormatList(null, true));
   }

   /**
    * Gets the formats as array for formatting a nested line.
    *
    * @param options The display options specifying which fields to display.
    * Will be ignored and ,ight be null if <code>ignoreOptions == true</code>.
    * @param ignoreOptions flag whether to ignore the supplied options and
    * return the formats for all fields.
    *
    * @return array filled with formats for each selected field. Might be empty
    * array, never null.
    */
   static Format[] getFormatList (
         final DisplayOptions options,
         final boolean ignoreOptions)
   {
      final List<Format> formatList = new ArrayList<Format>();
      formatList.addAll(getBasicFormatList(options, ignoreOptions));
      formatList.add(new WhitespaceFormat(new AsItIsFormat("\r\n")));
      return (Format[]) formatList.toArray(EMPTY_FORMATTERS);
   }

   /**
    * Formats either a LogRecord or a Loggable as nested message.
    * Append a line feed after the data has been formatted into the
    * StringBuffer.
    *
    * @param sb The StringBuffer where to append the formatted data.
    * @param record The LogRecord to format, if <code>loggable</code> is not
    * null, this is unused and might be null.
    * @param loggable The Loggable to format, might be null, but then
    * <code>record</code> must not be null.
    * @param trackingIdSequence The sequence of contributing tracking ids.
    * @param thrown Unused, might be null.
    * @param parameter Gives the cause of the message on top of this and is
    * the message text to be formatted by this.
    */
   public void format (
         final StringBuffer sb,
         final LogRecord record,
         final Loggable loggable,
         final List<String> trackingIdSequence,
         final Throwable thrown,
         final Object parameter)
   {
      setCause(parameter.toString());
      basicFormat(sb, record, loggable, trackingIdSequence);
   }

   /** {@inheritDoc} */
   public void parse (StringBuffer sb, LogFileEntry entry)
         throws ParseException
   {
      try
      {
         basicParse(sb, entry);
         entry.setMessage(getCause());
      }
      catch (ParseException pex)
      {
         // just rethrow
         throw pex;
      }
      catch (Exception ex)
      {
         final ParseException pex = new ParseException(
               "Got an error parsing " + sb, 0);
         pex.initCause(ex);
         throw pex;
      }
   }

   private void setCause (final String cause)
   {
      setParameter(CAUSE_INDEX, cause);
   }

   private String getCause ()
   {
      return (String) getParameter(CAUSE_INDEX);
   }
}
