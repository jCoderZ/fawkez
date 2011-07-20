/*
 * $Id: LogMessageGenerator.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.taskdefs;

import javax.xml.transform.Transformer;

import org.apache.tools.ant.BuildException;


/**
 * Ant task that generates classes out of the log message info XML document.
 * <p>
 * Documentation of this Ant task can be found in
 * {@link org.jcoderz.commons.taskdefs}.
 *
 * @author Michael Griffel
 */
public final class LogMessageGenerator
      extends XsltBasedTask
{
   /** The default stylesheet name. */
   private static final String DEFAULT_STYLESHEET
         = "generate-log-message-info.xsl";

   /** The application name. */
   private String mApplication = null;

   /**
    * The custom logger class to generate log messages for.
    * A possible valid value is 'java.util.logging.Logger'.
    */
   private String mCustomLoggerClass = null;

   /**
    * Shall the log messages get a .log() method that uses the
    * logger of the LoggableImpl to log the messages to?
    */
   private Boolean mAllowUseOfBaseLogger = null;

   /**
    * Sets the custom logger class to generate log messages for.
    * A possible valid value is 'java.util.logging.Logger'.
    * @param className the name of the custom logger
    */
   public void setCustomLoggerClass (String className)
   {
       mCustomLoggerClass = className;
   }

   /**
    * Sets whether the log messages get a .log() method that uses the
    * logger of the LoggableImpl to log the messages to?
    * @param allow the flag value to be set.
    */
   public void setAllowUseOfBaseLogger (boolean allow)
   {
       mAllowUseOfBaseLogger = Boolean.valueOf(allow);
   }

   /**
    * Sets the application (short) name. This parameter is required.
    * @param s The application (short) name.
    */
   public void setApplication (String s)
   {
      mApplication = s;
   }

   /** {@inheritDoc} */
   String getDefaultStyleSheet ()
   {
      return DEFAULT_STYLESHEET;
   }

   /** {@inheritDoc} */
   void setAdditionalTransformerParameters (Transformer transformer)
   {
      transformer.setParameter("application-short-name", mApplication);
      transformer.setParameter("application-name", mApplication);
      if (mAllowUseOfBaseLogger != null)
      {
          transformer.setParameter("allow-use-of-base-logger",
              mAllowUseOfBaseLogger);
      }
      if (mCustomLoggerClass != null)
      {
          transformer.setParameter("custom-logger-class",
              mCustomLoggerClass);
      }
   }

   /** {@inheritDoc} */
   void checkAttributes ()
         throws BuildException
   {
      super.checkAttributes();

      if (mApplication == null)
      {
         throw new BuildException(
               "Missing mandatory attribute 'application'.", getLocation());
      }

   }
}
