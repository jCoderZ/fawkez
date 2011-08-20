/*
 * $Id: LogItem.java 1299 2009-03-23 20:06:23Z amandel $
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jcoderz.commons.BusinessImpact;
import org.jcoderz.commons.Category;
import org.jcoderz.commons.types.Date;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.Constants;



/**
 * This class is the base class of nested log information as it is used for
 * displaying or transferring log messages.
 *
 */
public abstract class LogItem
{
   /** This is a prefix of an internal parameter of a log message, i.e. a
    *  parameter, which is not defined as parameter in the xml file. */
   public static final String INTERNAL_PARAMETER_PREFIX = "_";

   private LogItem mNestedEntry = null;
   private LogItem mParentEntry = null;

   private Date mTimestamp = null;
   private Level mLogLevel = null;
   private long mThreadId;
   private BusinessImpact mBusinessImpact = null;
   private Category mCategory = null;
   private String mNodeId = null;
   private String mInstanceId = null;
   private String mTrackingNumber = null;
   private String mSymbol = null;
   private String mSymbolId = null;
   private String mMessage = null;
   private String mSourceClass = null;
   private String mSourceMethod = null;
   private String mSolution = null;
   private String mThreadName = null;
   private StringBuffer mMessageBuffer = null;

   private final Map<String, Object> mParameters = new HashMap<String, Object>();

   private final List<StackTraceInfo> mStackTraceLines = new ArrayList<StackTraceInfo>();

   private String mType = null;

   /**
    * Sets the business impact of this.
    *
    * @param businessImpact The business impact to set.
    */
   public void setBusinessImpact (BusinessImpact businessImpact)
   {
      mBusinessImpact = businessImpact;
   }

   /**
    * Sets the category of this.
    *
    * @param category The category to set.
    */
   public void setCategory (Category category)
   {
      mCategory = category;
   }

   /**
    * Sets the instance id.
    *
    * @param instanceId The instance id to set.
    */
   public void setInstanceId (String instanceId)
   {
      mInstanceId = instanceId;
   }

   /**
    * Sets the log level of this.
    *
    * @param logLevel The log level, which had been used for logging the
    * message.
    */
   public void setLoggerLevel (Level logLevel)
   {
      mLogLevel = logLevel;
   }

   /**
    * Sets the message text.
    *
    * @param message The text of the logged message.
    */
   public void setMessage (String message)
   {
      mMessageBuffer = new StringBuffer(String.valueOf(message));
      mMessage = null;
   }

   /**
    * Appends a new line with supplied message text to the already stored
    * message. If no message has been stored yet, the supplied string is set
    * as message.
    *
    * @param messageLine The new line of message.
    */
   public void appendMessageLine (String messageLine)
   {
      mMessage = null;
      if (mMessageBuffer == null)
      {
         mMessageBuffer = new StringBuffer(String.valueOf(messageLine));
      }
      else
      {
         mMessageBuffer.append(Constants.LINE_SEPARATOR).append(
               String.valueOf(messageLine));
      }
   }

   /**
    * Sets the node id.
    *
    * @param nodeId The node id to set.
    */
   public void setNodeId (String nodeId)
   {
      mNodeId = nodeId;
   }

   /**
    * Sets the possible solution to resolve the error indicated by the logged
    * message.
    *
    * @param solution The solution for the logged message.
    */
   public void setSolution (String solution)
   {
      mSolution = solution;
   }

   /**
    * Sets the class name of the source location where the message was logged.
    *
    * @param sourceClass The name of the class where the message was logged,
    */
   public void setSourceClass (String sourceClass)
   {
      mSourceClass = sourceClass;
   }

   /**
    * Sets the method name of the source location where the message was logged.
    *
    * @param sourceMethod The name of the method where the message was logged,
    */
   public void setSourceMethod (String sourceMethod)
   {
      mSourceMethod = sourceMethod;
   }

   /**
    * Sets the message symbol.
    *
    * @param symbol The symbol for the logged message.
    */
   public void setSymbol (final String symbol)
   {
      mSymbol = symbol;
   }

   /**
    * Sets the message symbol code.
    *
    * @param symbolId The id/code for the symbol of the logged message.
    */
   public void setSymbolId (final String symbolId)
   {
      mSymbolId = symbolId;
   }

   /**
    * Sets the thread id.
    *
    * @param threadId The thread id to set.
    */
   public void setThreadId (long threadId)
   {
      mThreadId = threadId;
   }

   /**
    * Sets the timestamp of the message.
    *
    * @param timestamp The timestamp of when the message was logged.
    */
   public void setTimestamp (final Date timestamp)
   {
      mTimestamp = timestamp;
   }

   /**
    * Sets the tracking number from the parsed log line.
    *
    * @param trackingNumber The tracking number of the logged message.
    */
   public void setTrackingNumber (final String trackingNumber)
   {
      mTrackingNumber = trackingNumber;
   }

   /**
    * Sets the type of this.
    *
    * @param type The type to set.
    */
   public void setType (final String type)
   {
      mType = type;
   }

   /**
    * Sets the thread name for this LogItem.
    *
    * @param type The thread name to set..
    */
   public void setThreadName (final String threadName)
   {
      mThreadName = threadName;
   }

   /**
    * Gets the business impact.
    *
    * @return BusinessImpact of the message.
    */
   public BusinessImpact getBusinessImpact ()
   {
      return mBusinessImpact;
   }

   /**
    * Gets the category.
    *
    * @return Category of the message.
    */
   public Category getCategory ()
   {
      return mCategory;
   }

   /**
    * Gets the id of the instance which logged this.
    *
    * @return Id of instance, which logged this.
    */
   public String getInstanceId ()
   {
      return mInstanceId;
   }

   /**
    * Gets the logger level of this entry.
    *
    * @return log level used to log this.
    */
   public Level getLoggerLevel ()
   {
      return mLogLevel;
   }

   /**
    * Gets the message of this entry.
    *
    * @return Message text.
    */
   public String getMessage ()
   {
      if (mMessage == null)
      {
         mMessage = (mMessageBuffer == null) ? null : mMessageBuffer.toString();
      }
      return mMessage;
   }

   /**
    * Gets the id of the node which logged this.
    *
    * @return Id of the node qwhich logged this.
    */
   public String getNodeId ()
   {
      return mNodeId;
   }

   /**
    * Gets the parameter names of the parameters for this as unmodifiable Set.
    *
    * @return Set with parameter names, migth be empty, never null.
    */

   public Set<String> getParameterNames ()
   {
      return Collections.unmodifiableSet(mParameters.keySet());
   }

   /**
    * Gets the List of parameter values for the specified parameter name as
    * unmodifiable List.
    *
    * @param parameterName The name of the parameter for which to return the
    * values.
    *
    * @return List of parameter values, is null if <code>parameterName</code> is
    * unknown.
    */

   public List<?> getParameterValues (final String parameterName)
   {
      final List<?> parameterValues = (List) mParameters.get(parameterName);
      final List<?> rc = (parameterValues == null)
            ? null : Collections.unmodifiableList(parameterValues);
      return rc;
   }

   /**
    * Gets a possible solution.
    *
    * @return The solution for an error message;
    */
   public String getSolution ()
   {
      return mSolution;
   }

   /**
    * Gets the name of the class where the message was logged.
    *
    * @return Name of the source class.
    */
   public String getSourceClass ()
   {
      return mSourceClass;
   }

   /**
    * Gets the name of the method where this was logged.
    *
    * @return Name of the source method.
    */
   public String getSourceMethod ()
   {
      return mSourceMethod;
   }

   /**
    * Gets the list of stack trace linesstored by this.
    *
    * @return List with stack trace lines, might be empty, never null.
    */
   public List<StackTraceInfo> getStackTraceLines ()
   {
      return mStackTraceLines;
   }

   /**
    * Gets the message symbol.
    *
    * @return The message symbol.
    */
   public String getSymbol ()
   {
      return mSymbol;
   }

   /**
    * Gets the id of the message symbol in hex representation.
    *
    * @return The symbol id in hex representation
    */
   public String getSymbolId ()
   {
      return mSymbolId;
   }

   /**
    * Gets the id of the thread which logged this.
    *
    * @return Id of thread whcih logged this.
    */
   public long getThreadId ()
   {
      return mThreadId;
   }

   /**
    * Gets the timestamp of when this was logged.
    *
    * @return Timestamp when this was logged.
    */
   public Date getTimestamp ()
   {
      return mTimestamp;
   }

   /**
    * Gets the tracking number of this.
    *
    * @return Tracking number of this.
    */
   public String getTrackingNumber ()
   {
      return mTrackingNumber;
   }

   /**
    * Returns the type of this, which is retrieved from the log line type.
    *
    * @return Type of this.
    */
   public String getType ()
   {
      return mType;
   }

   /**
    * Gets the thread name for this LogItem.
    *
    * @return The thread name of this log item.
    */
   public String getThreadName ()
   {
      return mThreadName;
   }

   /**
    * Gets information whether this item stores an exception + stacktrace +
    * cause description only. Even if this is the case, there might be a nested
    * item storing a log message.
    *
    * @return true, if this item only stores an exception; false, else.
    */
   public boolean isExceptionItem ()
   {
      // for all types of log messages the mType is set. So if it is not set,
      // this entry only stores exception values.
      return (mType == null);
   }

   /**
    * Gets the item nested to this.
    *
    * @return nested log item, might be null if no such.
    */
   public LogItem getNestedItem ()
   {
      return mNestedEntry;
   }

   /**
    * Sets the supplied item as nested item for this and this as parent for
    * the supplied item.
    *
    * @param nestedItem The LogItem to set as nested item for this.
    */
   public void setNestedItem (LogItem nestedItem)
   {
      Assert.notNull(nestedItem, "nestedItem");
      mNestedEntry = nestedItem;
      nestedItem.setParentItem(this);
   }

   /**
    * Sets the log item which is parent to this.
    *
    * @param parentEntry The parentEntry to set.
    */
   public void setParentItem (LogItem parentEntry)
   {
      mParentEntry = parentEntry;
   }

   /**
    * Gets the log item which is parent to this.
    *
    * @return The parent item of this, might be null.
    */
   public LogItem getParentItem ()
   {
      return mParentEntry;
   }

   /**
    * Adds a parameter with value to the stored parameters.
    *
    * @param name The name of the parameter.
    * @param value The value of the parameter with this name.
    */
   public void addToParameters (final String name, final Object value)
   {
      mParameters.put(name, value);
   }

   /**
    * Used for resetting this to an initial state so that this object could be
    * reused again.
    *
    * @throws LoggingException if an error occurs.
    */
   public void reset ()
         throws LoggingException
   {
      mNestedEntry = null;
      mParentEntry = null;
      mTimestamp = null;
      mLogLevel = null;
      mBusinessImpact = null;
      mCategory = null;
      mNodeId = null;
      mInstanceId = null;
      mTrackingNumber = null;
      mSymbol = null;
      mSymbolId = null;
      mMessage = null;
      mSourceClass = null;
      mSourceMethod = null;
      mSolution = null;
      mParameters.clear();
      mStackTraceLines.clear();
      mParameters.clear();
      mType = null;
   }

}
