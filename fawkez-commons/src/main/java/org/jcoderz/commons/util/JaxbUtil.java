/*
 * $Id: JaxbUtil.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.jcoderz.commons.ArgumentMalformedException;
import org.xml.sax.InputSource;


/**
 * Utility class to simplify JAXB marshalling/unmarshalling.
 * @author Albrecht Messner
 */
public final class JaxbUtil
{
   private static final Map<String, JAXBContext> JAXB_CONTEXT_MAP = new HashMap<String, JAXBContext>();


   private JaxbUtil ()
   {
      // avoid instantiation of utility class
   }

   /**
    * Returns a JAXB context for the given context path. Contexts are
    * cached in a hash map.
    * @param contextPath the JAXB context path
    * @return the jaxb context for the given context path
    * @throws JAXBException if the context could not be retrieved
    */
   public static synchronized JAXBContext getJaxbContext (String contextPath)
         throws JAXBException
   {
      JAXBContext ctx = (JAXBContext) JAXB_CONTEXT_MAP.get(contextPath);
      if (ctx == null)
      {
         ctx = JAXBContext.newInstance(contextPath);
         JAXB_CONTEXT_MAP.put(contextPath, ctx);
      }
      return ctx;
   }

   /**
    * Unmarshals the given InputSource and returns the unmarshalled object
    * along with the validation event collector.
    * @param data the data to unmarshal
    * @param ctxPath the context path from which the JAXBContext is created
    *       to create an unmarshaller
    * @return the unmarshalled object along with its validation events
    * @throws JAXBException if unmarshalling or validation fails.
    */
   public static UnmarshalResult unmarshal (InputSource data, String ctxPath)
         throws JAXBException
   {
      final JAXBContext ctx = getJaxbContext(ctxPath);
      return unmarshal(data, ctx);
   }

   /**
    * Unmarshals the given InputSource and returns the unmarshalled object
    * along with the validation event collector.
    * @param data the data to unmarshal
    * @param ctx the JAXBContext from which the unmarshaller should be
    *       retrieved
    * @return the unmarshalled object along with its validation events
    * @throws JAXBException if unmarshalling or validation fails.
    */
   @SuppressWarnings("deprecation")
   public static UnmarshalResult unmarshal (InputSource data, JAXBContext ctx)
         throws JAXBException
   {
      final Unmarshaller unmarsh = ctx.createUnmarshaller();
      unmarsh.setValidating(true);
      final ValidationEventCollector evtHandler
            = new ValidationEventCollector();
      unmarsh.setEventHandler(evtHandler);
      final Object parsedData = unmarsh.unmarshal(data);
      return new UnmarshalResult(parsedData, evtHandler);
   }

   /**
    * Serializes (marshals) a given JAXB object and returns the result as
    * byte array, along with the validation events collected during
    * marshalling.
    * @param data the object to marshal
    * @param contextPath the context path to retrieve the corresponding JAXB
    *       context for.
    * @return the marshalled object and marshalling events
    * @throws JAXBException if marshalling or validation fails.
    */
   public static MarshalResult marshal (Object data, String contextPath)
         throws JAXBException
   {
      final JAXBContext ctx = getJaxbContext(contextPath);
      return marshal(data, ctx);
   }

   /**
    * Serializes (marshals) a given JAXB object and returns the result as
    * byte array, along with the validation events collected during
    * marshalling.
    * @param data the object to marshal
    * @param ctx the JAXBContext from which the unmarshaller can be retrieved.
    * @return the marshalled object and marshalling events
    * @throws JAXBException if marshalling or validation fails.
    */
   public static MarshalResult marshal (Object data, JAXBContext ctx)
         throws JAXBException
   {
      final Marshaller marsh = ctx.createMarshaller();
      final ValidationEventCollector evtHandler
            = new ValidationEventCollector();
      marsh.setEventHandler(evtHandler);
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      marsh.marshal(data, outStream);
      final MarshalResult result
            = new MarshalResult(outStream.toByteArray(), evtHandler);
      return result;
   }

   /**
    * Holds the Result of an unmarshal operation.
    * @author Albrecht Messner
    */
   public static class UnmarshalResult
   {
      private final Object mParsedData;
      private final ValidationEventCollector mValidationEvents;

      UnmarshalResult (Object parsedData, ValidationEventCollector evtHandler)
      {
         mParsedData = parsedData;
         mValidationEvents = evtHandler;
      }

      /**
       * Returns the parsed (unmarshalled) object.
       * @return the parsed (unmarshalled) object.
       */
      public Object getParsedData ()
      {
         return mParsedData;
      }

      /**
       * Returns the validation events of the unmarshal operation.
       * @return the validation events of the unmarshal operation.
       */
      public ValidationEventCollector getValidationEvents ()
      {
         return mValidationEvents;
      }
   }

   /**
    * Holds the Result of an Marshal operation.
    * @author Albrecht Messner
    */
   public static class MarshalResult
   {
      private final byte[] mMarshalledData;
      private final ValidationEventCollector mValidationEvents;

      MarshalResult (byte[] marshalledData, ValidationEventCollector evtHandler)
      {
         mMarshalledData = marshalledData;
         mValidationEvents = evtHandler;
      }

      /**
       * Returns the marshalled object.
       * @return the marshalled object.
       */
      public byte[] getMarshalledData ()
      {
         return mMarshalledData;
      }

      /**
       * Returns the validation events of the marshal operation.
       * @return the validation events of the marshal operation.
       */
      public ValidationEventCollector getValidationEvents ()
      {
         return mValidationEvents;
      }
   }

   /**
    * Validation handler for JAXB.
    *
    * @author Michael Griffel
    */
   public static class ValidationEventCollector
         implements ValidationEventHandler
   {
      private static final String CLASSNAME
            = ValidationEventCollector.class.getName();

      private static final Logger logger = Logger.getLogger(CLASSNAME);

      private final List<ValidationEvent> mEvents = new ArrayList<ValidationEvent>();

      /**
       * Returns all the collected errors and warnings or an empty list
       * if there weren't any. The result is an unmodifiable list.
       *
       * @return all the collected errors and warnings or an empty list
       *      if there weren't any. The result is an unmodifiable list.
       */
      public List<ValidationEvent> getEvents ()
      {
         return Collections.unmodifiableList(mEvents);
      }

      /**
       * Clear all collected errors and warnings.
       */
      public void reset ()
      {
         mEvents.clear();
      }

      /**
       * Returns true if this event collector contains at least one
       * ValidationEvent.
       *
       * @return true if this event collector contains at least one
       *         ValidationEvent, false otherwise
       */
      public boolean hasEvents ()
      {
         return mEvents.size() != 0;
      }

      /** {@inheritDoc} */
      public boolean handleEvent (ValidationEvent event)
      {
         final String methodName = "handleEvent";
         if (logger.isLoggable(Level.FINER))
         {
            logger.entering(CLASSNAME, methodName, event);
            logger.finer("Event details: "
                  + eventToString(new StringBuffer(), event));
         }

         mEvents.add(event);

         final boolean doContinue
               = event.getSeverity() != ValidationEvent.FATAL_ERROR;

         if (logger.isLoggable(Level.FINER))
         {
            logger.exiting(CLASSNAME, methodName, String.valueOf(doContinue));
         }
         return doContinue;
      }

      /**
       * Returns a summary of the validation events as String.
       * @return a summary of the validation events as String.
       */
      public String toString ()
      {
         final StringBuffer sb = new StringBuffer();
         for (int i = 0; i < mEvents.size(); ++i)
         {
            sb.append('[');
            sb.append(i + 1);
            sb.append('/');
            sb.append(mEvents.size());
            sb.append("] ");
            final ValidationEvent e = (ValidationEvent) mEvents.get(i);
            eventToString(sb, e);
         }
         return sb.toString().trim();
      }

      private StringBuffer eventToString (
            final StringBuffer sb, final ValidationEvent e)
      {
         appendLocator(sb, e.getLocator());

         if (e.getLinkedException() != null)
         {
            appendLinkedException(sb, e);
         }
         else
         {
            sb.append(e.getMessage());
         }
         appendSpace(sb);

         return sb;
      }

      private void appendLinkedException (final StringBuffer sb,
            final ValidationEvent e)
      {
         final String causeMessage = e.getLinkedException().getMessage();
         if (!e.getMessage().equals(causeMessage))
         {
            if (e.getLinkedException() instanceof ArgumentMalformedException)
            {
               final ArgumentMalformedException ame
                     = (ArgumentMalformedException) e.getLinkedException();
               sb.append("The Argument ");
               sb.append(getParameter(
                     ame, "ARGUMENT_NAME"));
               sb.append(" with the value '");
               sb.append(getParameter(ame,
            		   "ARGUMENT_VALUE"));
               sb.append("' is malformed. ");
               sb.append(getParameter(
                     ame, "HINT"));
            }
            else
            {
               sb.append(e.getMessage());
               if (causeMessage != null)
               {
                  sb.append(" Cause: ");
                  sb.append(causeMessage);
               }
            }
         }
         else
         {
            sb.append(e.getMessage());
         }
      }

      private void appendLocator (final StringBuffer sb,
            ValidationEventLocator locator)
      {
         if (locator != null)
         {
            if (locator.getObject() != null)
            {
              sb.append("Object: ");
              sb.append(locator.getObject());
              appendSpace(sb);
            }
            if (locator.getNode() != null)
            {
              sb.append("Node: ");
              sb.append(locator.getObject());
              appendSpace(sb);
            }
            if (locator.getOffset() >= 0)
            {
               sb.append("Offset: ");
               sb.append(locator.getOffset());
               appendSpace(sb);
            }
         }
      }

      /** Simply appends a spece to the given StringBuffer. */
      private final StringBuffer appendSpace (final StringBuffer sb)
      {
         return sb.append(' ');
      }

      private String getParameter (ArgumentMalformedException ex, String name)
      {
         final List<String> parameters = ex.getParameter(name);
         final String result;
         if (parameters != null)
         {
            result = parameters.get(0);
         }
         else
         {
            result = "";
         }
         return result;
      }
   }

}
