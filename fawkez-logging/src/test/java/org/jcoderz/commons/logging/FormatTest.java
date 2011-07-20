/*
 * $Id: FormatTest.java 1299 2009-03-23 20:06:23Z amandel $
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
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jcoderz.commons.LogEvent;
import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.test.TssLogMessage;
import org.jcoderz.commons.util.Constants;


/**
 * This class is used for testing the various Formats, especially if they are
 * symmetric, i.e. whether parsing the string of a formatted object delivers
 * the object again.
 *
 */
public class FormatTest
      extends TestCase
{
   private static final String CLASSNAME = FormatTest.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   private static final int FIX_LENGTH = 8;

   /**
    * Creates a new instance of this.
    */
   public FormatTest ()
   {
      super();
   }

   /**
    * Creates a new instance of this and sets the test case.
    *
    * @param name The name of the test case.
    */
   public FormatTest (String name)
   {
      super(name);
   }

   /**
    * Tests the string escape format. Formats and parses several strings using
    * this format and fails if parsing fails or does not deliver a string
    * equal to the source string.
    */
   public void testStringEscapeFormat ()
   {
      final Format format = new StringEscapeFormat(",.");
      formatAndParse(format, "A simple test");
      formatAndParse(format, "Another not so simple test.");
      formatAndParse(format, "Well, another not so simple test.");
      formatAndParse(format,
            "Well, another not so simple test. Indeed, this should be enough.");
   }

   /**
    * Tests the fix length format. Formats and parses several strings using
    * this format and fails if parsing fails or does not deliver a string
    * equal to the source string.
    */
   public void testFixLengthFormat ()
   {
      Format format = new FixLengthFormat(
            FIX_LENGTH, FixLengthFormat.LEFT_PADDING, '#');
      formatAndParse(format, "123#4#");
      formatAndParse(format, "String78");

      format = new FixLengthFormat(
            FIX_LENGTH, FixLengthFormat.RIGHT_PADDING, '#');
      formatAndParse(format, "123#4");
      formatAndParse(format, "String78");

      format = new FixLengthFormat(
          FIX_LENGTH, FixLengthFormat.LEFT_CUT_RIGHT_PADDING, '#');
      formatAndParse(format, "123#4");
      formatAndParse(format, "String78");
      Assert.assertEquals("23456789", format.format("123456789"));
      Assert.assertEquals("1#######", format.format("1"));
   }

   /**
    * Tests formatting and parsing a complete trace log record with the standard
    * formatters. Fails if parsing of the formatted log record fails.
    */
   public void testLogRecord ()
   {
      final LogRecord record = new LogRecord(Level.INFO, "TestLogRecord");
      record.setSourceClassName("FormatTest");
      record.setSourceMethodName("testLogRecord");
      final LogLineFormat format = LogLineFormatFactory.create(
            LogLineFormat.TRACE_MESSAGE);

      formatAndParseLogRecord(record, null,
            Arrays.asList(new Object[]{
                  String.valueOf(record.getSequenceNumber())}), format);
   }

   /**
    * Tests formatting and parsing a complete trace log record with the standard
    * formatters. The log record contains a multi line text message.
    * Fails if parsing of the formatted log record fails.
    */
   public void testLogRecordWithMultiLineText ()
   {
      final LogRecord record = new LogRecord(Level.INFO, "TestLogRecord Line1"
            + Constants.LINE_SEPARATOR + "same TestLogRecord Line2"
            + Constants.LINE_SEPARATOR + "same TestLogRecord Line  3");
      record.setSourceClassName("FormatTest");
      record.setSourceMethodName("testLogRecordWithMultiLineText");
      final LogLineFormat format = LogLineFormatFactory.create(
            LogLineFormat.TRACE_MESSAGE);

      formatAndParseLogRecord(record, null,
            Arrays.asList(new Object[]{"1", "2"}), format);
   }

   /**
    * Tests formatting and parsing a complete log message with the standard
    * formatters. Fails if parsing of the formatted log record fails.
    */
   public void testLogMessage ()
   {
      final Loggable loggable = new LogEvent(TssLogMessage.FUNNY_RUNTIME);
      loggable.addParameter("Param1", "foo");
      loggable.addParameter("DateNow", new Date());
      final LogRecord record = new LogRecord(Level.INFO, "LogMessage Test");
      record.setSourceClassName("FormatTest");
      record.setSourceMethodName("testLogMessage");
      record.setParameters(new Object[] {loggable});

      final LogLineFormat format = LogLineFormatFactory.create(
            LogLineFormat.LOG_MESSAGE);
      formatAndParseLogRecord(record, loggable,
            Arrays.asList(new Object[]{loggable.getTrackingNumber()}), format);
   }

   /**
    * Tests the collection format with string escape and fixe length sub
    * formats. Formats and parses several collection of strings using
    * these formats and fails if parsing fails or does not deliver a string list
    * equal to the source list.
    */
   public void testCollectionFormat ()
   {
      // The StringEscapeFormat must be configured for escaping the list end
      // sequence as well, otherwise it would not stop parsing when finding
      // this character.
      Format subFormat = new StringEscapeFormat(",.]");
      Format format = new CollectionFormat(subFormat);
      final List rc = new ArrayList();

      formatAndParse(format, rc);

      rc.add("A simple test");
      formatAndParse(format, rc);
      rc.add("Another not so simple test.");
      formatAndParse(format, rc);
      rc.add("Well, another not so simple test.");
      formatAndParse(format, rc);
      rc.add(
            "Well, another not so simple test. Indeed, this should be enough.");
      formatAndParse(format, rc);

      rc.clear();
      subFormat = new FixLengthFormat(FIX_LENGTH,
            FixLengthFormat.LEFT_PADDING, '0');
      format = new CollectionFormat(subFormat, "", "", "-");
      rc.add("1");
      formatAndParse(format, rc);
      rc.add("2");
      formatAndParse(format, rc);
      rc.add("3");
      formatAndParse(format, rc);
   }

   private void formatAndParse (
         final Format format,
         final Object toBeFormatted)
   {
      logger.info("Formatting '" + toBeFormatted.toString() + "'");
      final String formattedObject = format.format(toBeFormatted);
      logger.info("Got '" + formattedObject + "'");
      try
      {
         final Object parsedObject = format.parseObject(formattedObject);
         assertNotNull("Got error parsing " + formattedObject, parsedObject);
         assertEquals("Parsed object must be equal to the source object",
               toBeFormatted, parsedObject);

      }
      catch (ParseException pex)
      {
         logger.logp(Level.SEVERE, CLASSNAME, "formatAndParse",
               "Got an exception", pex);
         logger.severe("This happend while parsing " + formattedObject);
         logger.severe("Which was the result of formatting " + toBeFormatted);
         fail("ParseException parsing " + formattedObject);
      }
   }

   private void formatAndParseLogRecord (
         final LogRecord record,
         final Loggable loggable,
         final List args,
         final LogLineFormat format)
   {
      final StringBuffer sb = new StringBuffer();

      format.format(sb, record, loggable, args, null, null);
      logger.info("###### Formatted string is:\n'" + sb.toString() + "'");
      try
      {
         final LogFileEntry entry = LogFileEntry.getLogFileEntry();
         format.parse(sb, entry);
         if (loggable != null)
         {
            // the parsed line will not contain the solution, since this is
            // formatted into a new line. So set it here to let the check pass
            // The same applies to the symbol
            // and to the parameters
            entry.setSolution(loggable.getLogMessageInfo().getSolution());
            entry.setSymbol(loggable.getLogMessageInfo().getSymbol());
            for (final Iterator iter = loggable.getParameterNames().iterator();
                  iter.hasNext(); )
            {
               final String pName = (String) iter.next();
               if (! pName.startsWith(LogItem.INTERNAL_PARAMETER_PREFIX))
               {
                  entry.addToParameters(pName, loggable.getParameter(pName));
               }
            }
         }
         LoggerUtil.checkLogItem(entry, record);
         entry.release();
         logger.info("###### DONE with parsing");
      }
      catch (ParseException ex)
      {
         ex.printStackTrace();
         fail("ParseException parsing\n" + sb);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         fail("Exception parsing\n" + sb);
      }
   }
}
