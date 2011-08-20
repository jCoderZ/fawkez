/*
 * $Id: Loggable.java 1492 2009-06-06 13:36:19Z amandel $
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
 * Interface for all loggable objects.
 *
 * @author Andreas Mandel
 */
public interface Loggable
      extends Serializable
{
   /**
    * Add a new parameter to the <code>Loggable</code>.
    * All parameters are always added to the <code>Loggable</code>.
    * There is no way of changing or removing a parameter. Once the
    * <code>Loggable</code> was asked for one of it's values there
    * <b>should</b> not be a new parameter. Nevertheless
    * implementations of this interface MUST not throw an exception
    * if this happens.
    * Multiple parameters with the same name are stored within
    * the <code>Loggable</code>.
    * Values must be <code>Serializable</code> and have a useful
    * to String representation. If the value is not serializable
    * it might be sufficient to store the String representation
    * of the value. The <code>Loggable</code> is not responsible
    * to make a deep copy of the value. The calling code should
    * take care for this. To solve this a possible solution
    * would again be to use the String representation of the
    * value.
    * @param name the name of the parameter to be added.
    * @param value the value for the parameter.
    */
   void addParameter (String name, Serializable value);

   /**
    * Returns a list of all parameters with the given name.
    * Even if the objects in the list are mutable clients are
    * expected not to modify them and to make deep copies as
    * needed.
    * The <code>name</code> must not start with an <code>_</code>
    * character. These are reserved for internal use.
    * @param name The name of the parameter to be retrieved.
    * @return a list of serializable objects representing the values
    *       of the parameters with the given name.
    */
   List<Serializable> getParameter (String name);

   /**
    * Returns a set of available parameters.
    * @return a set of available parameters.
    */
   Set<String> getParameterNames ();

   /**
    * Returns the {@link LogMessageInfo} for this <code>Loggable</code>.
    * @return the {@link LogMessageInfo} for this <code>Loggable</code>.
    */
   LogMessageInfo getLogMessageInfo ();

   /**
    * Returns the (unique) tracking number of this
    * <code>Loggable</code> instance.
    * @return the (unique) tracking number of this
    * <code>Loggable</code> instance.
    */
   String getTrackingNumber ();

   /**
    * Returns the (Detail) message of this loggable.
    * The message is created using the pattern of the associated
    * {@link LogMessageInfo} and parameters.
    * The field might get truncated on the presentation layer.
    * @return the (Detail) message of this loggable.
    */
   String getMessage ();

   /**
    * Returns the point in time when the event occurred.
    * More general this is the point in time when the
    * <code>Loggable</code> object was created.
    * @return the point in time when the event occurred, measured in
    *       milliseconds, between the current time and midnight,
    *       January 1, 1970 UTC.
    */
   long getEventTime ();

   /**
    * Returns the ip-address or the host name where the Loggable was
    * created.
    * The field might get truncated on the presentation layer.
    * @return the ip-address or the host name where the process is
    * running on.
    */
   String getNodeId ();

   /**
    * Returns the instance identifier of the process that generated
    * this Loggable.
    * The instance id should be a descriptive name for the process
    * running the application. For example, on Bea WLS,
    * we could use the Server name. If no such information is
    * available, the OS Process ID should be used.
    * The field might get truncated on the presentation layer.
    * @return the instance identifier of the process that generated
    * this Loggable.
    */
   String getInstanceId ();

   /**
    * Returns the thread id of the thread that created this
    * <code>Loggable</code>.
    * @return the thread id of the thread that created this
    * <code>Loggable</Code>.
    */
   long getThreadId ();

   /**
    * Returns the thread name as it was valid at the time of
    * creation of this <code>Loggable</code>.
    * @return the thread name as it was valid at the time of
    * creation of this <code>Loggable</code>.
    * @see Thread#getName()
    */
   String getThreadName ();

   /**
    * Returns the cause of this throwable or <code>null</code> if the
    * cause is nonexistent or unknown.  (The cause is the throwable
    * that caused this throwable to get thrown.)
    * The returned object might be a <code>Loggable</code>.
    * @return  the cause of this throwable or <code>null</code> if the
    *          cause is nonexistent or unknown.
    */
   Throwable getCause ();

   /**
    * The toString method of <code>Loggable</code> dumps the
    * String representation of the class name of the loggable
    * and the contained message.
    * @return one line information about this <code>Loggable</code>.
    */
   String toString ();

   /**
    * The toString method of <code>Loggable</code> must dump out all
    * information stored within this loggable in a readable way. This
    * includes the information of possible nested data.
    * @return a exhaustive dump of the data stored within this
    *       <code>Loggable</code>.
    */
   String toDetailedString ();

   /**
    * Logs this <code>Loggable</code> into the appropriate log
    * sink.
    */
   void log ();

   /**
    * Tries to find the source method where this log event was fired.
    * @return the name of the method including the line number if available
    * @see StackTraceElement#getMethodName()
    * @see StackTraceElement#getLineNumber()
    */
   String getSourceMethod ();

   /**
    * Tries to find the source class name where this log event was fired.
    * @return the name of the class
    * @see StackTraceElement#getClassName()
    */
   String getSourceClass ();

}
