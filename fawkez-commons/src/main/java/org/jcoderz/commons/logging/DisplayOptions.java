/*
 * $Id: DisplayOptions.java 1299 2009-03-23 20:06:23Z amandel $
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

/**
 * This class comprises display options, i.e. which fields are to
 * display with how many details.
 *
 */
public final class DisplayOptions
      implements Cloneable
{
   private boolean mDisplayParameters = true;
   private boolean mDisplaySolution = true;
   private boolean mDisplayMethod = false;
   private boolean mDisplayThreadId = false;
   private boolean mDisplayNodeId = true;
   private boolean mDisplayInstanceId = true;
   private boolean mDisplayBusinessImpact = false;
   private boolean mDisplayCategory = false;
   private boolean mDisplayLevel = false;
   private boolean mDisplayTimestamp = false;
   private boolean mDisplayRecordNumber = false;
   private boolean mDisplayClass = false;
   private boolean mDisplaySymbol = true;
   private boolean mDisplaySymbolId = true;
   private boolean mDisplayTrackingNumber = true;
   private boolean mDisplayStackTrace = true;
   private boolean mDisplayMessageStackTrace = false;
   private boolean mDisplayTraceLines = false;
   private boolean mThreadName = false;

   /**
    * Sets the flag whether the thread id is displayed.
    *
    * @param display If true, thread id is displayed; if false do not display
    * the thread id.
    */
   public void displayThreadId (final boolean display)
   {
      mDisplayThreadId = display;
   }

   /**
    * Gets the flag whether the thread id is displayed.
    *
    * @return true if the thread id is to display; false, else.
    */
   public boolean displayThreadId ()
   {
      return mDisplayThreadId;
   }

   /**
    * Sets the flag whether the node id is displayed.
    *
    * @param display If true, node id is displayed; if false do not display
    * the node id.
    */
   public void displayNodeId (final boolean display)
   {
      mDisplayNodeId = display;
   }

   /**
    * Gets the flag whether the node id is displayed.
    *
    * @return true if the node id is to display; false, else.
    */
   public boolean displayNodeId ()
   {
      return mDisplayNodeId;
   }

   /**
    * Sets the flag whether the instance id is displayed.
    *
    * @param display If true, instance id is displayed; if false do not display
    * the instance id.
    */
   public void displayInstanceId (final boolean display)
   {
      mDisplayInstanceId = display;
   }

   /**
    * Gets the flag whether the instance id is displayed.
    *
    * @return true if the instance id is to display; false, else.
    */
   public boolean displayInstanceId ()
   {
      return mDisplayInstanceId;
   }

   /**
    * Sets the flag whether the business impact is displayed.
    *
    * @param display If true, business impact is displayed; if false do not
    * display the business impact.
    */
   public void displayBusinessImpact (final boolean display)
   {
      mDisplayBusinessImpact = display;
   }

   /**
    * Gets the flag whether the business impact is displayed.
    *
    * @return true if the business impact is to display; false, else.
    */
   public boolean displayBusinessImpact ()
   {
      return mDisplayBusinessImpact;
   }

   /**
    * Sets the flag whether the category is displayed.
    *
    * @param display If true, category is displayed; if false do not
    * display the category.
    */
   public void displayCategory (final boolean display)
   {
      mDisplayCategory = display;
   }

   /**
    * Gets the flag whether the category is displayed.
    *
    * @return true if the category is to display; false, else.
    */
   public boolean displayCategory ()
   {
      return mDisplayCategory;
   }

   /**
    * Sets the flag whether the thread name is displayed.
    *
    * @param display If true, the thread name is displayed; if false do not
    * display the thread name.
    */
   public void displayThreadName (final boolean display)
   {
      mThreadName = display;
   }

   /**
    * Sets the flag whether the thread name should be displayed.
    * @return true, the thread name is displayed; if false do not
    * display the thread name.
    */
   public boolean displayThreadName ()
   {
      return mThreadName;
   }

   /**
    * Sets the flag whether the logger level / severity is displayed.
    *
    * @param display If true, logger level is displayed; if false do not
    * display the logger level.
    */
   public void displayLoggerLevel (final boolean display)
   {
      mDisplayLevel = display;
   }

   /**
    * Gets the flag whether the logger level is displayed.
    *
    * @return true if the logger level is to display; false, else.
    */
   public boolean displayLoggerLevel ()
   {
      return mDisplayLevel;
   }

   /**
    * Sets the flag whether the timestamp is displayed.
    *
    * @param display If true, timestamp is displayed; if false do not
    * display the timestamp.
    */
   public void displayTimestamp (final boolean display)
   {
      mDisplayTimestamp = display;
   }

   /**
    * Gets the flag whether the timestamp is displayed.
    *
    * @return true if the timestamp is to display; false, else.
    */
   public boolean displayTimestamp ()
   {
      return mDisplayTimestamp;
   }

   /**
    * Sets the flag whether the record number of a log file record is displayed.
    *
    * @param display If true, record number is displayed; if false do not
    * display the record number.
    */
   public void displayRecordNumber (final boolean display)
   {
      mDisplayRecordNumber = display;
   }

   /**
    * Gets the flag whether the record number is displayed.
    *
    * @return true if the record number is to display; false, else.
    */
   public boolean displayRecordNumber ()
   {
      return mDisplayRecordNumber;
   }

   /**
    * Sets the flag whether the source class, where the message was logged, is
    * displayed.
    *
    * @param display If true, source class is displayed; if false do not
    * display the source class.
    */
   public void displaySourceClass (final boolean display)
   {
      mDisplayClass = display;
   }

   /**
    * Gets the flag whether the source class name is displayed.
    *
    * @return true if the source class name is to display; false, else.
    */
   public boolean displaySourceClass ()
   {
      return mDisplayClass;
   }

   /**
    * Sets the flag whether the source method, where the message was logged, is
    * displayed.
    *
    * @param display If true, source method is displayed; if false do not
    * display the source method.
    */
   public void displaySourceMethod (final boolean display)
   {
      mDisplayMethod = display;
   }

   /**
    * Gets the flag whether the source method name is displayed.
    *
    * @return true if the source method name is to display; false, else.
    */
   public boolean displaySourceMethod ()
   {
      return mDisplayMethod;
   }

   /**
    * Sets the flag whether the possible solution for a message is displayed.
    *
    * @param display If true, solution is displayed; if false do not
    * display the solution.
    */
   public void displaySolution (final boolean display)
   {
      mDisplaySolution = display;
   }

   /**
    * Gets the flag whether the possible solution is displayed.
    *
    * @return true if the solution is to display; false, else.
    */
   public boolean displaySolution ()
   {
      return mDisplaySolution;
   }

   /**
    * Sets the flag whether the parameters for a message are displayed.
    *
    * @param display If true, parameters are displayed; if false do not
    * display the parameters.
    */
   public void displayParameters (final boolean display)
   {
      mDisplayParameters = display;
   }

   /**
    * Gets the flag whether the parameters are displayed.
    *
    * @return true if the parameters are to display; false, else.
    */
   public boolean displayParameters ()
   {
      return mDisplayParameters;
   }

   /**
    * Sets the flag whether the symbol is displayed.
    *
    * @param display If true, the symbol is displayed; if false do not
    * display the symbol.
    */
   public void displaySymbol (final boolean display)
   {
      mDisplaySymbol = display;
   }

   /**
    * Gets the flag whether the symbol is displayed.
    *
    * @return true if the symbol is to display; false, else.
    */
   public boolean displaySymbol ()
   {
      return mDisplaySymbol;
   }

   /**
    * Sets the flag whether the symbol id is displayed.
    *
    * @param display If true, the symbol id is displayed; if false do not
    * display the symbol id.
    */
   public void displaySymbolId (final boolean display)
   {
      mDisplaySymbolId = display;
   }

   /**
    * Gets the flag whether the symbol id is displayed.
    *
    * @return true if the symbol id is to display; false, else.
    */
   public boolean displaySymbolId ()
   {
      return mDisplaySymbolId;
   }

   /**
    * Sets the flag whether the sequence of tracking numbers is displayed.
    *
    * @param display If true, the sequence of tracking numbers is displayed;
    * if false do not display the sequence of tracking numbers.
    */
   public void displayTrackingNumber (final boolean display)
   {
      mDisplayTrackingNumber = display;
   }

   /**
    * Gets the flag whether the sequence of tracking numbers is displayed.
    *
    * @return true if the sequence of tracking numbers is to display;
    * false, else.
    */
   public boolean displayTrackingNumber ()
   {
      return mDisplayTrackingNumber;
   }

   /**
    * Sets the flag whether the stack trace of an exception is displayed.
    *
    * @param display If true, the stacktrace of an exception is displayed;
    * if false do not display an exception's stacktrace.
    */
   public void displayStackTrace (final boolean display)
   {
      mDisplayStackTrace = display;
   }

   /**
    * Gets the flag whether an exception's stacktrace is displayed.
    *
    * @return true if the stacktrace of an exception is to display; false, else.
    */
   public boolean displayStackTrace ()
   {
      return mDisplayStackTrace;
   }

   /**
    * Sets the flag whether the stack trace of a log message is displayed.
    *
    * @param display If true, the stacktrace of a log message is displayed;
    * if false do not display a log message's stacktrace.
    */
   public void displayMessageStackTrace (final boolean display)
   {
      mDisplayMessageStackTrace = display;
   }

   /**
    * Gets the flag whether a log message's stacktrace is displayed.
    *
    * @return true if the stacktrace of an log message is to display;
    * false, else.
    */
   public boolean displayMessageStackTrace ()
   {
      return mDisplayMessageStackTrace;
   }

   /**
    * Sets the flag whether standard trace logs are displayed, i.e. undeclared
    * log messages, which are generated by standard logger api calls.
    *
    * @param display If true, standard trace logs are displayed;
    * if false do not display standard trace logs.
    */
   public void displayTraceLines (final boolean display)
   {
      mDisplayTraceLines = display;
   }

   /**
    * Gets the flag whether standard trace logs are displayed, i.e. undeclared
    * log messages, which are generated by standard logger api calls.
    *
    * @return true if trace logs are to display; false, else.
    */
   public boolean displayTraceLines ()
   {
      return mDisplayTraceLines;
   }

   /** {@inheritDoc} */
   public Object clone ()
       throws CloneNotSupportedException
   {
       return super.clone();
   }
}
