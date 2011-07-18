/*
 * $Id: JmsHandler.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jcoderz.commons.LoggableImpl;


/**
 * This log handler publishes log messages onto a jms queue. Information for the
 * configuration has to be provided in the logging properties file.
 * The following configuration can to be provided:<br>
 *
 * <code>org.jcoderz.commons.logging.JmsHandler.factory:</code><br>
 *    Name of the jms connection factory, is mandatory.<br><br>
 *
 * <code>org.jcoderz.commons.logging.JmsHandler.queue:</code><br>
 *    Name of the jms queue, is mandatory.<br><br>
 *
 * <code>org.jcoderz.commons.logging.JmsHandler.filter:</code><br>
 *    Name of class implementing java.util.logging.Filter and which is to
 *    set as filter for this. If this is not specified, the default filter is
 *    used, which filters according to the symbol ids.<br><br>
 *
 * <code>org.jcoderz.commons.logging.JmsHandler.messageids:</code><br>
 *    A comma or space separated list of message symbol ids for those
 *    messages, which are loggable for the default filter. The ids have either
 *    to be specified as integer values with base 10, or by prefixing with 0x as
 *    hex values.
 *
 */
public class JmsHandler
      extends Handler
{
   private static final String CLASSNAME = JmsHandler.class.getName();
   private static final String CLIENT_ID
         = "JmsHandler@" + LoggableImpl.INSTANCE_ID;

   private static final String JMS_FACTORY_NAME_PROPERTY
         = CLASSNAME + "." + "factory";
   private static final String JMS_QUEUE_NAME_PROPERTY
         = CLASSNAME + "." + "queue";
   private static final String JMS_FILTER_PROPERTY
         = CLASSNAME + "." + "filter";
   private static final String JMS_FORMATTER_PROPERTY
         = CLASSNAME + "." + "formatter";

   /**
    * The created sessions to not behave transactional, i.e. do not put a tx
    * boundary around sending several messages.
    */
   private static final boolean SESSION_TRANSACTION_MODE = false;

   /**
    * The acknowledge mode, is being ignored for senders.
    */
   private static final int SESSION_ACKNOWLEDGE_MODE = Session.AUTO_ACKNOWLEDGE;

   /**
    * Stores the jms queue session for the current thread. With respect to the
    * JMS specification a session and the resources it provides must be used by
    * only one thread.
    */
   private final ThreadLocal mJmsSessions = new ThreadLocal();

   /**
    * Stores the jms queue sender for the current thread.
    */
   private final ThreadLocal mJmsSenders = new ThreadLocal();

   /**
    * When closing this handler, all sessions have to be closed (closing a
    * session is the only session method, which is allowed to be called from
    * another than the session controlling thread). This gives access to all
    * sessions and will store WeakReferences to allow a session being garbage
    * collected if the corresponding thread dies.
    */
   private final List mAllSessions = new ArrayList();

   private final LogManager mManager = LogManager.getLogManager();

   private QueueConnection mJmsConnection = null;
   private Queue mJmsQueue = null;

   private Context mContext;

   private String mFactoryName;
   private String mQueueName;

   /**
    * This is the formatter to be used for formatting log records before they
    * are put onto the jms queue.
    * It formats the log record into a xml message using the
    * {@link XmlPrinter}. The stack trace of messages and exceptions is
    * neglected.
    *
    */
   private static final class DefaultFormatter
         extends Formatter
   {
      /** A PrintWriter is used by the XmlPrinter. */
      private final ThreadLocal mPrintWriters = new ThreadLocal();
      /** A CharWriter is used by the PrintWriter. */
      private final ThreadLocal mCharWriters = new ThreadLocal();

      private final XmlPrinter mXmlPrinter;
      private final DisplayOptions mDisplayOptions;

      private DefaultFormatter ()
            throws InstantiationException
      {
         mXmlPrinter = new XmlPrinter();
         mDisplayOptions = new DisplayOptions();
         mDisplayOptions.displayMessageStackTrace(false);
         mDisplayOptions.displayStackTrace(false);
         mXmlPrinter.setDisplayOptions(mDisplayOptions);
      }

      /** {@inheritDoc} */
      public String format (LogRecord record)
      {
         final CharArrayWriter writer = getCharWriter();
         writer.reset();
         final PrintWriter printer = getPrintWriter();
         mXmlPrinter.print(printer, new LogElement(record));
         return writer.toString();
      }

      private PrintWriter getPrintWriter ()
      {
         PrintWriter rc = (PrintWriter) mPrintWriters.get();
         if (rc == null)
         {
            final CharArrayWriter cw = getCharWriter();
            rc = new PrintWriter(cw);
            mPrintWriters.set(rc);
         }
         return rc;
      }

      private CharArrayWriter getCharWriter ()
      {
         CharArrayWriter rc = (CharArrayWriter) mCharWriters.get();
         if (rc == null)
         {
            rc = new CharArrayWriter();
            mCharWriters.set(rc);
         }
         return rc;
      }
   }


   /**
    * Creates a new instance of this and initialises resources. It retrieves
    * configuration parameters from the LogManagers and connects to the jms
    * provider.
    *
    * @throws SecurityException If no permission to do the tasks.
    * @throws NamingException If the jms connection factory lookup fails.
    * @throws InstantiationException If not all required configuration
    * parameters are specified.
    * @throws IllegalAccessException If illegal access to a class.
    * @throws ClassNotFoundException If a specified class name could not be
    * found.
    * @throws JMSException If an error connecting to the JmsProvider occurs.
    */
   public JmsHandler ()
         throws SecurityException,
         NamingException,
         InstantiationException,
         IllegalAccessException,
         ClassNotFoundException,
         JMSException
   {
      super();
      mManager.checkAccess();
      configure();
      connect();
   }

   /** {@inheritDoc} */
   public void close ()
         throws SecurityException
   {
      mManager.checkAccess();
      setFormatter(null);
      setFilter(null);
      synchronized (mAllSessions)
      {
         while (! mAllSessions.isEmpty())
         {
            final WeakReference ref = (WeakReference) mAllSessions.remove(0);
            final QueueSession session = (QueueSession) ref.get();
            if (session != null)
            {
               try
               {
                  session.close();
               }
               catch (Exception ex)
               {
                  reportError("Error closing jms session: " + session,
                        ex, ErrorManager.CLOSE_FAILURE);
               }
            }
         }
      }
      try
      {
         mJmsConnection.close();
      }
      catch (JMSException jex)
      {
         reportError("Error closing jms connection: " + mJmsConnection,
               jex, ErrorManager.CLOSE_FAILURE);
      }
   }

   /** {@inheritDoc} */
   public void flush ()
   {
      // nop
   }

   /** {@inheritDoc} */
   public void publish (final LogRecord record)
   {
      if (getFilter().isLoggable(record))
      {
         sendRecord(record);
      }
   }

   private void configure ()
         throws NamingException,
         SecurityException,
         InstantiationException,
         IllegalAccessException,
         ClassNotFoundException
   {
      configureJndiContext();
      configureResources();
      configureFilter();
      configureFormatter();
   }

   private void configureJndiContext ()
         throws NamingException
   {
      final Context context = new InitialContext();
      mContext = context;
   }

   private void configureFilter ()
         throws SecurityException,
         InstantiationException,
         IllegalAccessException,
         ClassNotFoundException
   {
      final String filterClass = mManager.getProperty(JMS_FILTER_PROPERTY);
      if (filterClass == null || filterClass.length() == 0)
      {
         setFilter(new MessageIdFilter());
      }
      else
      {
         try
         {
            AccessController.doPrivileged(
                  new PrivilegedExceptionAction()
                  {
                     public Object run ()
                           throws SecurityException,
                           InstantiationException,
                           IllegalAccessException,
                           ClassNotFoundException
                     {
                        setFilter((Filter) Class.forName(filterClass)
                              .newInstance());
                        return null;
                     }
                  }
            );
        }
        catch (PrivilegedActionException e)
        {
           final InstantiationException iex = new InstantiationException(
                 "Could not install the Filter: " + filterClass);
           iex.initCause(e);
           throw iex;
        }
      }
   }

   private void configureFormatter ()
         throws SecurityException,
         InstantiationException,
         IllegalAccessException,
         ClassNotFoundException
   {
      final String formatter = mManager.getProperty(JMS_FORMATTER_PROPERTY);
      if (formatter == null || formatter.length() == 0)
      {
         setFormatter(new DefaultFormatter());
      }
      else
      {
         try
         {
            AccessController.doPrivileged(
                  new PrivilegedExceptionAction()
                  {
                     public Object run ()
                           throws SecurityException,
                           InstantiationException,
                           IllegalAccessException,
                           ClassNotFoundException
                     {
                        setFormatter((Formatter) Class.forName(formatter)
                              .newInstance());
                        return null;
                     }
                  }
            );
         }
         catch (PrivilegedActionException e)
         {
            final InstantiationException iex
                  = new InstantiationException(
                        "Could not install the Formatter: " + formatter);
            iex.initCause(e);
            throw iex;
         }
      }
   }

   private void configureResources ()
         throws InstantiationException
   {
      final String factory = mManager.getProperty(JMS_FACTORY_NAME_PROPERTY);
      if ((factory == null) || (factory.length() == 0))
      {
         throw new InstantiationException("No jms connection factory configured"
               + " in properties file with property: "
               + JMS_FACTORY_NAME_PROPERTY);
      }
      final String queue = mManager.getProperty(JMS_QUEUE_NAME_PROPERTY);
      if ((queue == null) || (queue.length() == 0))
      {
         throw new InstantiationException("No jms queue configured in "
               + "properties file with property: " + JMS_QUEUE_NAME_PROPERTY);
      }
      mFactoryName = factory;
      mQueueName = queue;
   }

   /**
    * Creates a connection to the jms provider and performs a lookup for the
    * queue, which will receive the log messages.
    *
    * @throws NamingException
    * @throws JMSException
    */
   private void connect ()
         throws NamingException, JMSException
   {
      final QueueConnectionFactory factory
            = (QueueConnectionFactory) mContext.lookup(mFactoryName);
      final Queue queue = (Queue) mContext.lookup(mQueueName);

      mJmsConnection = factory.createQueueConnection();
      mJmsConnection.setClientID(CLIENT_ID);
      mJmsQueue = queue;
   }

   /**
    * Gets the queue sender for the current thread. If there is no sender yet,
    * this thread's session is used for creating a new sender.
    *
    * @return queue sender for the current thread.
    *
    * @throws JMSException if an error occurs.
    */
   private QueueSender getSender ()
         throws JMSException
   {
      QueueSender rc = (QueueSender) mJmsSenders.get();
      if (rc == null)
      {
         try
         {
            rc = (QueueSender) AccessController.doPrivileged(
                  new PrivilegedExceptionAction()
                  {
                     public Object run ()
                           throws JMSException
                     {
                        return installSender();
                     }
                  });
         }
         catch (PrivilegedActionException e)
         {
            final JMSException jex
                  = new JMSException("Could not install a QueueSender:" + e);
            jex.initCause(e);
            throw jex;
         }
      }
      return rc;
   }

   /**
    * Gets the queue session for the current thread. If there is no session yet,
    * a new session is created and initialized.
    *
    * @return queue session for the current thread.
    *
    * @throws JMSException if an error occurs.
    */
   private QueueSession getSession ()
         throws JMSException
   {
      QueueSession rc = (QueueSession) mJmsSessions.get();
      if (rc == null)
      {
         final QueueSession session = mJmsConnection.createQueueSession(
               SESSION_TRANSACTION_MODE, SESSION_ACKNOWLEDGE_MODE);
         mJmsSessions.set(session);
         synchronized (mAllSessions)
         {
            mAllSessions.add(new WeakReference(session));
         }
         rc = session;
      }
      return rc;
   }

   private QueueSender installSender ()
         throws JMSException
   {
      final QueueSession session = getSession();
      QueueSender sender = null;
      try
      {
         sender = session.createSender(mJmsQueue);
         mJmsSenders.set(sender);
      }
      finally
      {
         if (sender == null)
         {
            // An error ocurred on this session. Close it, it might occur again.
            mJmsSessions.set(null);
            session.close();
         }
      }
      return sender;
   }

   private void sendRecord (final LogRecord record)
   {
      String text = null;
      try
      {
         text = getFormatter().format(record);
      }
      catch (Exception ex)
      {
         reportError("Error formatting the log record",
               ex, ErrorManager.FORMAT_FAILURE);
      }
      if (text != null)
      {
         try
         {
            final TextMessage msg = getSession().createTextMessage();
            msg.setText(text);
            getSender().send(msg);
         }
         catch (JMSException ex)
         {
            reportError("Error publishing a log record", ex,
                  ErrorManager.WRITE_FAILURE);
         }
      }
   }
}
