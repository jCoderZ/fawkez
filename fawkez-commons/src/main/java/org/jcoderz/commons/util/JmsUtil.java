/*
 * $Id: JmsUtil.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Collects some JMS utility functions.
 *
 */
public final class JmsUtil
{
   /** The full qualified name of this class. */
   private static final String CLASSNAME = JmsUtil.class.getName();

   /** The logger to use. */
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   /**
    * Constructor.
    */
   private JmsUtil ()
   {
      // Utility class -- only static methods
   }

   /**
    * Closes the jms session (safe).
    *
    * This method tries to close the given session and if an
    * {@link JMSException} occurs a message with the level {@link Level#FINE} is
    * logged. It's safe to pass a <code>null</code> reference for the argument.
    *
    * @param session The jms session that should be closed.
    */
   public static void close (Session session)
   {
      if (session != null)
      {
         try
         {
            session.close();
         }
         catch (JMSException e)
         {
            logCloseFailedWarningMessage(
                  Session.class, session.getClass(), e);
         }
      }
   }

   /**
    * Closes the jms connection (safe).
    *
    * This method tries to close the given connection and if an
    * {@link JMSException} occurs a message with the level {@link Level#FINE} is
    * logged. It's safe to pass a <code>null</code> reference for the argument.
    *
    * @param connection The connection to be closed.
    */
   public static void close (Connection connection)
   {
      if (connection != null)
      {
         try
         {
            connection.close();
         }
         catch (JMSException e)
         {
            logCloseFailedWarningMessage(
                  Connection.class, connection.getClass(), e);
         }
      }
   }

   /**
    * Closes the jms message producer (safe).
    *
    * This method tries to close the given message producer and if an
    * {@link JMSException} occurs a message with the level {@link Level#FINE} is
    * logged. It's safe to pass a <code>null</code> reference for the argument.
    *
    * @param producer The message producer to be closed.
    */
   public static void close (MessageProducer producer)
   {
      if (producer != null)
      {
         try
         {
            producer.close();
         }
         catch (JMSException e)
         {
            logCloseFailedWarningMessage(
                  MessageProducer.class, producer.getClass(), e);
         }
      }
   }

   private static void logCloseFailedWarningMessage (Class<?> resource,
         Class<?> clazz, JMSException x)
   {
      logger.log(Level.FINE, "Error while closing " + resource.getName() + ": "
            + clazz.getName() + ".close()", x);
   }
}
