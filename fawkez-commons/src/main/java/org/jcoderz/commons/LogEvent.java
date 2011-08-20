/*
 * $Id: LogEvent.java 1492 2009-06-06 13:36:19Z amandel $
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
package org.jcoderz.commons;


import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * This is the Base class for log events.
 *
 * <p>The base class of this LogEvent is Throwable but instances
 * of this class are not expected to be thrown.</p>
 *
 * <p>Most functionality is implemented and documented by the
 * {@link org.jcoderz.commons.LoggableImpl} which is used a member of
 * objects of this class.</p>
 *
 * @see org.jcoderz.commons
 * @author Andreas Mandel
 */
public class LogEvent
      extends Throwable
      implements Loggable
{
   static final long serialVersionUID = 2L;

   /** The loggable implementation. */
   private final LoggableImpl mLoggable;

   /**
    * Constructor getting an log message info.
    *
    * @param messageInfo the log message info for this exception
    */
   public LogEvent (LogMessageInfo messageInfo)
   {
      super(messageInfo.getSymbol());
      mLoggable = new LoggableImpl(this, messageInfo);
   }

   /**
    * Constructor getting an log message info and a root exception.
    *
    * @param messageInfo the log message info for this exception
    * @param cause the problem that caused this exception to be thrown
    */
   public LogEvent (LogMessageInfo messageInfo, Throwable cause)
   {
      super(messageInfo.getSymbol(), cause);
      mLoggable = new LoggableImpl(this, messageInfo, cause);
   }

   /** {@inheritDoc} */
   public Throwable initCause (Throwable cause)
   {
      super.initCause(cause);
      mLoggable.initCause(cause);
      return this;
   }

   /** {@inheritDoc} */
   public final void addParameter (String name, Serializable value)
   {
      mLoggable.addParameter(name, value);
   }

   /** {@inheritDoc} */
   public String getInstanceId ()
   {
      return mLoggable.getInstanceId();
   }

   /** {@inheritDoc} */
   public final String getMessage ()
   {
      return mLoggable.getMessage();
   }

   /** {@inheritDoc} */
   public final void log ()
   {
      mLoggable.log();
   }

   /** {@inheritDoc} */
   public Throwable getCause ()
   {
      return mLoggable.getCause();
   }

   /** {@inheritDoc} */
   public long getEventTime ()
   {
      return mLoggable.getEventTime();
   }

   /** {@inheritDoc} */
   public LogMessageInfo getLogMessageInfo ()
   {
      return mLoggable.getLogMessageInfo();
   }

   /** {@inheritDoc} */
   public String getNodeId ()
   {
      return mLoggable.getNodeId();
   }

   /** {@inheritDoc} */
   public List<Serializable> getParameter (String name)
   {
      return mLoggable.getParameter(name);
   }

   /** {@inheritDoc} */
   public Set<String> getParameterNames ()
   {
      return mLoggable.getParameterNames();
   }

   /** {@inheritDoc} */
   public long getThreadId ()
   {
      return mLoggable.getThreadId();
   }

   /** {@inheritDoc} */
   public String getTrackingNumber ()
   {
      return mLoggable.getTrackingNumber();
   }

   /** {@inheritDoc} */
   public String getSourceClass ()
   {
      return mLoggable.getSourceClass();
   }

   /** {@inheritDoc} */
   public String getSourceMethod ()
   {
      return mLoggable.getSourceMethod();
   }

   /** {@inheritDoc} */
   public String getThreadName ()
   {
       return mLoggable.getThreadName();
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      return mLoggable.toString();
   }

   /** {@inheritDoc} */
   public String toDetailedString ()
   {
      return mLoggable.toDetailedString();
   }


   LoggableImpl getExceptionImpl ()
   {
      return mLoggable;
   }
}
