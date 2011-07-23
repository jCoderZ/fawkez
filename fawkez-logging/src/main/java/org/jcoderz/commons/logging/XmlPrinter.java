/*
 * $Id: XmlPrinter.java 1304 2009-03-24 09:15:55Z amandel $
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
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.jcoderz.commons.util.Assert;



/**
 * This printer formats the log messages into an Xml format and prints the log
 * messages as a sequence of xml elements.
 *
 */
public final class XmlPrinter
      extends LogPrinter
{
   private final LogRecordTypesObjectPool mXmlObjectsPool;
   private final ObjectFactory mObjectFactory = new ObjectFactory();
   private final JAXBContext mJaxbContext;
   private final Marshaller mMarshaller;

   private static final class LogRecordTypesObjectPool
         extends StackKeyedObjectPool
   {
      private LogRecordType borrowLogRecord ()
            throws LoggingException
      {
         try
         {
            return (LogRecordType) borrowObject(LogRecordType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing LogRecordType", ex);
         }
      }

      private void returnLogRecord (final LogRecordType record)
            throws LoggingException
      {
         try
         {
            returnObject(LogRecordType.class, record);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning LogRecordType", ex);
         }
      }

      private FrameType borrowFrame ()
            throws LoggingException
      {
         try
         {
            return (FrameType) borrowObject(FrameType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing FrameType", ex);
         }
      }

      private void returnFrame (final FrameType frame)
            throws LoggingException
      {
         try
         {
            returnObject(FrameType.class, frame);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning FrameType", ex);
         }
      }

      private ParameterType borrowParameter ()
            throws LoggingException
      {
         try
         {
            return (ParameterType) borrowObject(ParameterType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing ParameterType", ex);
         }
      }

      private void returnParameter (final ParameterType parameter)
            throws LoggingException
      {
         try
         {
            returnObject(ParameterType.class, parameter);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning ParameterType", ex);
         }
      }

      private CauseType borrowCause ()
            throws LoggingException
      {
         try
         {
            return (CauseType) borrowObject(CauseType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing CauseType", ex);
         }
      }

      private void returnCause (final CauseType cause)
            throws LoggingException
      {
         try
         {
            returnObject(CauseType.class, cause);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning CauseType", ex);
         }
      }

      private ExceptionType borrowException ()
            throws LoggingException
      {
         try
         {
            return (ExceptionType) borrowObject(ExceptionType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing ExceptionType", ex);
         }
      }

      private void returnException (final ExceptionType ex)
            throws LoggingException
      {
         try
         {
            returnObject(ExceptionType.class, ex);
         }
         catch (Exception ex1)
         {
            throw new LoggingException("Error returning ExceptionType", ex1);
         }
      }

      private StacktraceType borrowStacktrace ()
            throws LoggingException
      {
         try
         {
            return (StacktraceType) borrowObject(StacktraceType.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing StacktraceType", ex);
         }
      }

      private void returnStacktrace (final StacktraceType stacktrace)
            throws LoggingException
      {
         try
         {
            returnObject(StacktraceType.class, stacktrace);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning StacktraceType", ex);
         }
      }

      private XMLGregorianCalendar borrowCalendar ()
            throws LoggingException
      {
         try
         {
            return (XMLGregorianCalendar) borrowObject(XMLGregorianCalendar.class);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error borrowing Calendar", ex);
         }
      }

      private void returnCalendar (final XMLGregorianCalendar cal)
            throws LoggingException
      {
         try
         {
            returnObject(Calendar.class, cal);
         }
         catch (Exception ex)
         {
            throw new LoggingException("Error returning Calendar", ex);
         }
      }
   }

   private static final class XmlObjectFactory
         extends BaseKeyedPoolableObjectFactory
   {
      private final LogRecordTypesObjectPool mPool;
      private final ObjectFactory mJaxbFactory;

      private XmlObjectFactory (
            final ObjectFactory factory,
            final LogRecordTypesObjectPool pool)
      {
         mPool = pool;
         mJaxbFactory = factory;
      }

      /**
       * Makes an object for the key given by <code>key</code>.
       * This accepts as keys classes of the jaxb objects used by this.
       *
       * @param key The class of the requested object.
       *
       * @return Instance of specified class.
       *
       * @see org.apache.commons.pool.BaseKeyedPoolableObjectFactory#makeObject(java.lang.Object)
       */
      public Object makeObject (Object key)
            throws JAXBException
      {
         final Object rc;

         if (key == LogRecordType.class)
         {
            rc = mJaxbFactory.createLogRecordType();
         }
         else if (key == FrameType.class)
         {
            rc = mJaxbFactory.createFrameType();
         }
         else if (key == StacktraceType.class)
         {
            rc = mJaxbFactory.createStacktraceType();
         }
         else if (key == ParameterType.class)
         {
            rc = mJaxbFactory.createParameterType();
         }
         else if (key == ExceptionType.class)
         {
            rc = mJaxbFactory.createExceptionType();
         }
         else if (key == CauseType.class)
         {
            rc = mJaxbFactory.createCauseType();
         }
         else if (key == Calendar.class)
         {
            rc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         }
         else
         {
            throw new IllegalArgumentException("Cannot make an object for "
                  + key);
         }
         return rc;
      }

      /**
       * Sets all required sub type fields of the supplied object.
       *
       * @param key The key of the object to activate.
       * @param xmlObj The object to activate.
       *
       * @see org.apache.commons.pool.KeyedPoolableObjectFactory#activateObject(java.lang.Object, java.lang.Object)
       */
      public void activateObject (Object key, Object xmlObj)
            throws LoggingException
      {
         if (key == LogRecordType.class)
         {
            final LogRecordType logRecord = (LogRecordType) xmlObj;

            // set all required sub typed elements
            // every log record has a source
            logRecord.setSource(mPool.borrowFrame());
            // ... and a timestamp
            logRecord.setTimestamp(mPool.borrowCalendar());
         }
         else if (key == FrameType.class)
         {
            // nop
         }
         else if (key == StacktraceType.class)
         {
            // nop
         }
         else if (key == ParameterType.class)
         {
            // nop
         }
         else if (key == ExceptionType.class)
         {
            // nop
         }
         else if (key == CauseType.class)
         {
            // nop
         }
         else if (key == Calendar.class)
         {
            // nop
         }
         else
         {
            throw new IllegalArgumentException("Cannot make an object for "
                  + key);
         }
      }

      /**
       * Sets all elements of the object to passivate to null and puts back
       * into the pool all sub typed objects.
       *
       * @param key The key of the object, which is the type of the object.
       * @param jaxbObj The jaxb object to passivate
       *
       * @see org.apache.commons.pool.KeyedPoolableObjectFactory#passivateObject(java.lang.Object, java.lang.Object)
       */
      public void passivateObject (Object key, Object jaxbObj)
            throws LoggingException
      {
         if (key == LogRecordType.class)
         {
            passivateLogRecord((LogRecordType) jaxbObj);
         }
         else if (key == FrameType.class)
         {
            passivateFrame((FrameType) jaxbObj);
         }
         else if (key == StacktraceType.class)
         {
            passivateStackTrace((StacktraceType) jaxbObj);
         }
         else if (key == ParameterType.class)
         {
            passivateParameter((ParameterType) jaxbObj);
         }
         else if (key == ExceptionType.class)
         {
            passivateException((ExceptionType) jaxbObj);
         }
         else if (key == CauseType.class)
         {
            passivateCause((CauseType) jaxbObj);
         }
         else if (key == Calendar.class)
         {
            // nop
         }
         else
         {
            throw new IllegalArgumentException("Cannot make an object for "
                  + key);
         }
      }

      /**
       * Passivates an object of type <code>CauseType</code>.
       *
       * @param cause The CauseType object to passivate
       *
       * @throws Exception thrown by called methods.
       */
      private void passivateCause (final CauseType cause)
            throws LoggingException
      {
         final ExceptionType ex = cause.getException();
         cause.setException(null);

         final LogRecordType record = cause.getNestedRecord();
         cause.setNestedRecord(null);

         if (ex != null)
         {
            mPool.returnException(ex);
         }
         if (record != null)
         {
            mPool.returnLogRecord(record);
         }
      }

      /**
       * Passivates an object of type <code>ExceptionType</code>.
       *
       * @param ex The ExceptionType object to passivate
       *
       * @throws Exception thrown by called methods.
       */
      private void passivateException (final ExceptionType ex)
            throws LoggingException
      {
         ex.setMessage(null);

         final CauseType cause = ex.getCause();
         ex.setCause(null);

         final StacktraceType stacktrace = ex.getStacktrace();
         ex.setStacktrace(null);

         if (cause != null)
         {
            mPool.returnCause(cause);
         }
         if (stacktrace != null)
         {
            mPool.returnStacktrace(stacktrace);
         }
      }

      /**
       * Passivates an object of type <code>ParameterType</code>.
       *
       * @param parameter The ParameterType object to passivate
       */
      private void passivateParameter (final ParameterType parameter)
      {
         parameter.setName(null);
         parameter.getValue().clear();
      }

      /**
       * Passivates an object of type <code>StacktraceType</code>.
       *
       * @param stacktrace The stacktrace object to passivate
       *
       * @throws Exception thrown by called methods.
       */
      private void passivateStackTrace (final StacktraceType stacktrace)
            throws LoggingException
      {
         for (final Iterator iter
               = stacktrace.getStacktraceElement().iterator(); iter.hasNext(); )
         {
            mPool.returnFrame((FrameType) iter.next());
         }
         stacktrace.getStacktraceElement().clear();
      }

      /**
       * Passivates an object of type <code>LogRecordType</code>.
       *
       * @param logRecord The LogRecordType object to passivate
       *
       * @throws Exception thrown by called methods.
       */
      private void passivateLogRecord (final LogRecordType logRecord)
            throws LoggingException
      {
         logRecord.setBusinessImpact(null);
         logRecord.setCategory(null);
         logRecord.setInstanceId(null);
         logRecord.setLevel(null);
         logRecord.setMessage(null);
         logRecord.setNodeId(null);
         logRecord.setSolution(null);
         logRecord.setSymbol(null);
         logRecord.setTrackingNumber(null);

         final XMLGregorianCalendar cal = logRecord.getTimestamp();
         logRecord.setTimestamp(null);

         final StacktraceType stack = logRecord.getStacktrace();
         logRecord.setStacktrace(null);

         final FrameType source = logRecord.getSource();
         logRecord.setSource(null);

         final CauseType cause = logRecord.getCause();
         logRecord.setCause(null);

         if (cal != null)
         {
            mPool.returnCalendar(cal);
         }
         if (stack != null)
         {
            mPool.returnStacktrace(stack);
         }
         if (source != null)
         {
            mPool.returnFrame(source);
         }
         if (cause != null)
         {
            mPool.returnCause(cause);
         }
         for (final Iterator iter = logRecord.getParameter().iterator();
               iter.hasNext(); )
         {
            mPool.returnParameter((ParameterType) iter.next());
         }

         logRecord.getParameter().clear();
      }

      /**
       * Passivates an object of type <code>FrameType</code>.
       *
       * @param frame The FrameType object to passivate
       *
       * @throws Exception thrown by called methods.
       */
      private void passivateFrame (final FrameType frame)
      {
         frame.setSourceMethod(null);
         frame.setSourceClass(null);
         frame.setSourceLine(null);
      }
   }

   /**
    * Creates a new instance of this. Allocates object pools and jaxb resources.
    *
    * @throws InstantiationException if an error occurs allocating the
    * resources.
    */
   public XmlPrinter ()
         throws InstantiationException
   {
      super();
      try
      {
         mJaxbContext = JAXBContext
               .newInstance("org.jcoderz.commons.logging");
         mMarshaller = mJaxbContext.createMarshaller();
         mMarshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
      }
      catch (JAXBException ex)
      {
         final InstantiationException iex
               = new InstantiationException("Cannot initialize jaxb resources");
         iex.initCause(ex);
         throw iex;
      }
      mXmlObjectsPool = new LogRecordTypesObjectPool();
      mXmlObjectsPool.setFactory(
            new XmlObjectFactory(mObjectFactory, mXmlObjectsPool));
   }

   /**
    * Prints the log data using the supplied print writer in xml format.
    *
    * @param printer The PrintWriter to use for printing the data.
    * @param logRecord The log data to format into xml and print using
    *       <code>printer</code>.
    *
    * @see LogPrinter#print(PrintWriter, LogItem)
    */
   public void print (
         final PrintWriter printer,
         final LogItem logRecord)
   {
      try
      {
         Assert.notNull(printer, "Printer");
         Assert.notNull(logRecord, "entry");
         Assert.notNull(logRecord.getType(), "logRecord.getType()");

         LogItem logEntry = logRecord;
         CauseType parentsCause = null;
         LogRecordType rootRecord = null;

         while (logEntry != null)
         {
            if (! logEntry.isExceptionItem())
            {
               LogRecordType currentRecord = null;

               if (rootRecord == null)
               {
                  currentRecord = mXmlObjectsPool.borrowLogRecord();
                  rootRecord = currentRecord;
               }
               fillLogRecord(currentRecord, logEntry);

               if (parentsCause != null)
               {
                  parentsCause.setNestedRecord(currentRecord);
               }
               logEntry = logEntry.getNestedItem();
               parentsCause = setCause(logEntry, currentRecord);
            }
            else
            {
               final ExceptionType currentException
                     = mXmlObjectsPool.borrowException();
               fillException(currentException, logEntry);
               if (parentsCause != null)
               {
                  parentsCause.setException(currentException);
               }
               logEntry = logEntry.getNestedItem();
               parentsCause = setCause(logEntry, currentException);
            }
         }
         if (rootRecord != null)
         {
            mMarshaller.marshal(rootRecord, printer);
            mXmlObjectsPool.returnLogRecord(rootRecord);
         }
      }
      catch (Exception ex)
      {
         System.err.println("Error formatting log file entry into xml: " + ex);
         ex.printStackTrace();
      }
   }

   private void fillLogRecord (
         final LogRecordType logRecord,
         final LogItem entry)
   {
      logRecord.setNodeId(entry.getNodeId());
      logRecord.setInstanceId(entry.getInstanceId());
      logRecord.setLevel(entry.getLoggerLevel().toString());
      logRecord.setSymbol(entry.getSymbol());
      logRecord.setSymbolId(entry.getSymbolId());

      // the timestamp object is set when the log record is borrowed from the
      // pool
      Calendar c = Calendar.getInstance();
      c.setTime(entry.getTimestamp().toUtilDate());
      logRecord.getTimestamp().setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

      logRecord.setTrackingNumber(entry.getTrackingNumber());

      logRecord.getSource().setSourceClass(
            entry.getSourceClass());
      logRecord.getSource().setSourceMethod(
            entry.getSourceMethod());
      logRecord.setThread(entry.getThreadId());
      logRecord.setThreadName(entry.getThreadName());
      logRecord.setMessage(entry.getMessage());
      logRecord.setSolution(entry.getSolution());
      logRecord.setBusinessImpact(BusinessImpactEnumType.valueOf(entry.getBusinessImpact().toString()));
      logRecord.setCategory(CategoryEnumType.valueOf(entry.getCategory().toString()));

      logRecord.setMessage(entry.getMessage());

      final Set params = entry.getParameterNames();
      for (final Iterator iter = params.iterator(); iter.hasNext(); )
      {
         final String parameterName = (String) iter.next();
         final ParameterType param
               = mXmlObjectsPool.borrowParameter();

         param.setName(parameterName);
         final List values
               = entry.getParameterValues(parameterName);
         if (values != null)
         {
            for (final Iterator valueIter = values.iterator();
                  valueIter.hasNext(); )
            {
               param.getValue().add(valueIter.next().toString());
            }
         }
         logRecord.getParameter().add(param);
      }
      fillStacktrace(logRecord, entry);
   }

   /**
    * Fills the stack trace according to the display options.
    *
    * @param logRecord The jaxb log record object to be filled,
    * @param entry The log entry from which to get the information.
    */
   private void fillStacktrace (LogRecordType logRecord, LogItem entry)
   {
      if (displayStackTrace(entry))
      {
         logRecord.setStacktrace(getStackTrace(entry));
      }
   }

   private void fillException (
         final ExceptionType exception,
         final LogItem entry)
   {
      exception.setMessage(entry.getMessage());
      if (displayStackTrace(entry))
      {
         exception.setStacktrace(getStackTrace(entry));
      }
   }

   private StacktraceType getStackTrace (final LogItem entry)
   {
      final StacktraceType rc;
      if (entry.getStackTraceLines().isEmpty())
      {
         rc = null;
      }
      else
      {
         rc = mXmlObjectsPool.borrowStacktrace();
         fillStackTrace(rc, entry);
      }
      return rc;
   }

   private void fillStackTrace (
         final StacktraceType stack,
         final LogItem entry)
   {
      for (final Iterator<StackTraceInfo> iter = entry.getStackTraceLines().iterator();
            iter.hasNext(); )
      {
         final StackTraceInfo info = (StackTraceInfo) iter.next();

         // not interested in lines, which contain the exception message,
         // this information is stored in other elements already.
         if (info.isLocationLine())
         {
            final FrameType frame = mXmlObjectsPool.borrowFrame();
            frame.setSourceClass(info.getClassName());
            frame.setSourceMethod(info.getMethodName());
            if (info.getLine() != 0)
            {
               frame.setSourceLine(BigInteger.valueOf(info.getLine()));
            }
            stack.getStacktraceElement().add(frame);
         }
         else if (info.isMoreLine())
         {
            Assert.assertTrue(info + " must be last line of a StackTrace,",
                  ! iter.hasNext());
            fillMoreStackTrace(entry, stack, info);
         }
      }
   }

   /**
    * This fills the stack trace if a '...nnn more' line has been encountered.
    * According to the display settings it might be necessary to take the stack
    * trace lines of parent entries.
    *
    * @param entry The log file entry with the current stack trace line.
    * @param stack The jaxb stack trace object, into which to fill the stack
    * trace.
    * @param info The current stack trace info from <code>entry</code>.
    */
   private void fillMoreStackTrace (
         final LogItem entry,
         final StacktraceType stack,
         final StackTraceInfo info)
   {
      final LogItem stackTraceEntry
            = getEntryForMoreStackTrace(entry, info);
      if (stackTraceEntry == null)
      {
         throw new IllegalStateException("Did not find correct stack trace to "
               + "display for " + entry);
      }
      else if (entry == stackTraceEntry)
      {
         // in this case the stack trace has been displayed already and we can
         // display the more line again.
         final FrameType frame = mXmlObjectsPool.borrowFrame();
         frame.setSourceClass(info.toString());
         frame.setSourceMethod("");
         stack.getStacktraceElement().add(frame);
      }
      else
      {
         fillStackTrace(stack, stackTraceEntry);
      }
   }

   private CauseType setCause (
         final LogItem entry,
         final LogRecordType record)
   {
      final CauseType rc;
      if (entry != null)
      {
         rc = mXmlObjectsPool.borrowCause();
         record.setCause(rc);
      }
      else
      {
         rc = null;
      }
      return rc;
   }

   private CauseType setCause (
         final LogItem entry,
         final ExceptionType exception)
   {
      final CauseType rc;
      if (entry != null)
      {
         rc = mXmlObjectsPool.borrowCause();
         exception.setCause(rc);
      }
      else
      {
         rc = null;
      }
      return rc;
   }
}
