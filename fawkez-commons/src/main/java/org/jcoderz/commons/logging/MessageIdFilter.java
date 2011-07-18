/*
 * $Id: MessageIdFilter.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.jcoderz.commons.Loggable;


/**
 * This class implements the default filter for filering the messages, which
 * are to put onto the jms queue.
 *
 * @see JmsHandler
 *
 */
public final class MessageIdFilter
      extends LogFilterBase
{
   /** The full qualified name of this class. */
   private static final String CLASSNAME = MessageIdFilter.class.getName();

   private static final String HEX_PREFIX = "0x";
   private static final int HEX_NUMBER_BASE = 16;

   private static final String JMS_MESSAGE_IDS_PROPERTY
         = CLASSNAME + "." + "messageids";

   private final Set mMessageIds = new HashSet();

   /**
    * Creates and configures a new isntance of this.
    */
   public MessageIdFilter ()
   {
      configure();
   }

   /** {@inheritDoc} */
   public boolean isLoggable (LogRecord record)
   {
      final boolean rc;
      final Loggable loggable = getLoggable(record);
      if (loggable != null
            && mMessageIds.contains(
               new Integer(loggable.getLogMessageInfo().toInt())))
      {
         rc = true;
      }
      else
      {
         rc = false;
      }

      return rc;
   }

   // TODO: shouldn't this method catch a NumberFormatException?
   private void configure ()
   {
      final String messageIds = LogManager.getLogManager()
            .getProperty(JMS_MESSAGE_IDS_PROPERTY);
      if ((messageIds != null) && (messageIds.length() > 0))
      {
         final StringTokenizer tokenizer
               = new StringTokenizer(messageIds, " ,/t");

         while (tokenizer.hasMoreTokens())
         {
            final String id = tokenizer.nextToken();
            if (id.startsWith(HEX_PREFIX))
            {
               mMessageIds.add(Integer.valueOf(
                     id.substring(HEX_PREFIX.length()), HEX_NUMBER_BASE));
            }
            else
            {
               mMessageIds.add(Integer.valueOf(id));
            }
         }
      }
   }
}
