/*
 * $Id: LogMessageInfo.java 1011 2008-06-16 17:57:36Z amandel $
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
import java.util.Map;
import java.util.logging.Level;


/**
 * Interface for the log message info data structure.
 * Implementations for this type are expected to be generated from
 * MessageInfo xml files. Data in here is bound to the Message and
 * not to specific log events.
 *
 * @author Andreas Mandel
 */
public interface LogMessageInfo
      extends Serializable
{
   /**
    * Returns the symbolic name for this log message info.
    * @return the symbolic name for this log message info.
    */
   String getSymbol ();

   /**
    * Returns the integer code representation of this log message info. This
    * should be logged in hexadecimal form for increasing human readability.
    *
    * @return integer code of this log message info.
    */
   int toInt ();

   /**
    * Returns the log level that should be used (by default) for this message.
    * @return the log level that should be used (by default) for this message.
    */
   Level getLogLevel ();

   /**
    * Returns the message pattern used to create detailed messages for
    * this message type.
    * The string can contain parameters as described for
    * {@link java.text.MessageFormat}. The order of the parameters is
    * given by the parameter list {@link #getParameterList()}.
    * @return the detailed message for this message type with no
    *       parameters substituted.
    */
   String getMessagePattern ();

   /**
    * Fills the detail message into the given string buffer with it's
    * parameters substituted.
    * @param parameters The map of parameters to be substituted.
    * @param buffer the StringBuffer to be filled.
    * @return the buffer argument.
    */
   StringBuffer formatMessage (Map<String, List<Serializable>> parameters, StringBuffer buffer);

   /**
    * Returns a string describing the <b>possible</b> solution for this
    * message. This might also contain a descriptive text about the
    * reason.
    * @return a string describing the <b>possible</b> solution for this
    * message.
    */
   String getSolution ();

   /**
    * Returns the <b>possible</b> business impact of the event that
    * triggered this message to be dumped.
    * @return the <b>possible</b> business impact of the event that
    * triggered this message to be dumped.
    */
   BusinessImpact getBusinessImpact ();

   /**
    * Returns the category of the event that triggered this message to be
    * dumped.
    *
    * @return the category of the event that triggered this message to be
    * dumped.
    */
   Category getCategory ();

   /**
    * Returns a List of Strings containing the name of the parameters
    * that are used by this message. The order of the parameter must
    * accord to the parameters used in the message pattern.
    * @return list of possible parameter names (Strings).
    */
   List<String> getParameterList ();

   /**
    * Returns the application name as string.
    * @return the application name as string.
    */
   String getAppName ();

   /**
    * Returns the application name abbreviation as string.
    * @return the application name abbreviation as string.
    */
   String getAppNameAbbreviation ();

   /**
    * Returns the group name as string.
    * @return the group name as string.
    */
   String getGroupName ();

   /**
    * Returns the group name abbreviation as string.
    * @return the group name abbreviation as string.
    */
   String getGroupNameAbbreviation ();

}
