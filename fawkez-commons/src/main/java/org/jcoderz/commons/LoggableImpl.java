/*
 * $Id: LoggableImpl.java 1577 2009-12-07 15:44:44Z amandel $
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcoderz.commons.util.ArraysUtil;
import org.jcoderz.commons.util.StringUtil;
import org.jcoderz.commons.util.ThrowableUtil;


/**
 * Implements code common to all Exceptions.
 * <p>
 * The two base exceptions {@link org.jcoderz.commons.BaseException}
 * and {@link org.jcoderz.commons.BaseRuntimeException} and the
 * {@link org.jcoderz.commons.LogEvent} use a object of this class
 * as a member and delegate all common calls to this member.
 * </p>
 * <p>
 * This class also implements the special
 * {@link org.jcoderz.commons.Loggable} that allows to add named
 * parameters and to get a more detailed logging, with an assigned
 * error message.
 * </p>
 * <p>
 * The error response id is used to mark log entries with an unique id. This id
 * is also returned to the client (caller). If the client reports the error
 * response id it can be used to find a specific log entries more quickly. If
 * the nested exception has already an error response id, it is re-used for this
 * exception and will not get a new one.
 * </p>
 * Functionality provided by this class is:
 * <ul>
 * <li>Create a unique <code>ERROR_RESPONSE_ID</code> parameter for each
 * instance.</li>
 * <li>Create a message that contains all parameters for a full informational
 * toString() output.</li>
 * <li>Handles nested exceptions so that all information is available and
 * avoids duplicate information for the JDK1.4 environment which supports nested
 * exceptions itself.</li>
 * <li>Holds the constant names for commonly used exception parameters.</li>
 * </ul>
 *
 * @author Andreas Mandel
 */
public class LoggableImpl
      implements Serializable, Loggable
{
   /** Name of this class. */
   public static final String CLASSNAME = LoggableImpl.class.getName();

   /** Logger used for this class. */
   public static final Logger logger = Logger.getLogger(CLASSNAME);

   /** Key used for the log message info parameter object. */
   public static final String MESSAGE_INFO_PARAMETER_NAME = "_MESSAGE_INFO";

   /** Key used for the error response id added to the loggable. */
   public static final String TRACKING_NUMBER_PARAMETER_NAME
         = "_TRACKING_NUMBER";

   /** Key used for the root cause added to the loggable. */
   public static final String CAUSE_PARAMETER_NAME = "_CAUSE";

   /** Key used for the thread id parameter object. */
   public static final String THREAD_ID_PARAMETER_NAME = "_THREAD_ID";

   /** Key used for the thread name parameter object. */
   public static final String THREAD_NAME_PARAMETER_NAME = "_THREAD_NAME";

   /** Key used for the instance id parameter object. */
   public static final String INSTANCE_ID_PARAMETER_NAME = "_INSTANCE_ID";

   /** Key used for the node id parameter object. */
   public static final String NODE_ID_PARAMETER_NAME = "_NODE_ID";

   /** Key used for the event time parameter of the loggable. */
   public static final String EVENT_TIME_PARAMETER_NAME = "_TIME";

   /** Name of the application of the loggable. */
   public static final String APPLICATION_NAME_PARAMETER_NAME = "_APPLICATION";

   /** Name of the group of the loggable. */
   public static final String GROUP_NAME_PARAMETER_NAME = "_GROUP";

   /** Context parameter values prefix. */
   public static final String CONTEXT_PARAMETER_PREFIX = "CTX~";
   
   /** This nodes id. */
   public static final String NODE_ID = getStaticNodeId();

   /** Id for this instance. */
   public static final String INSTANCE_ID;

   /** Virtual thread Id generated for this thread. */
   public static final ThreadIdHolder THREAD_ID_GENERATOR
       = new ThreadIdHolder();

   static final long serialVersionUID = 1;

   private static final int MAX_REASONABLE_PARAMETER_LENGTH = 10000;
   /**
    * Maximum number of steps to get the cause of an exception,
    * until we stop climbing up the cause chain.
    */
   private static final int MAX_EXCEPTION_CHAIN_UP = 20;

   /**
    * In the first step use bea specific instance name, which is set as system
    * property with the following name.
    * TODO: Make this bea-independent, requires entry in logging.properties,
    * system property, or something alike.
    */
   private static final String INSTANCE_NAME_PROPERTY = "weblogic.Name";

   /** Random generator to create pseudo unique Ids for each loggable. */
   private static final Random RANDOM_ID_GENERATOR = new Random();


   private static final String DUMMY_INSTANCE_ID
         = "P" + Integer.toHexString(RANDOM_ID_GENERATOR.nextInt());
   private static final String DUMMY_NODE_ID = "127.0.0.1";

   /** Name of getter methods start with this prefix. */
   private static final String GETTER_METHOD_PREFIX = "get";
   /** Name of getter methods start with this prefix. */
   private static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
   /** Length of the getter prefix. */
   private static final int GETTER_METHOD_PREFIX_LENGTH
       = GETTER_METHOD_PREFIX.length();
   /** Length of the boolean getter prefix. */
   @SuppressWarnings("unused")
   private static final int BOOLEAN_GETTER_METHOD_PREFIX_LENGTH
       = BOOLEAN_GETTER_METHOD_PREFIX.length();
   
   /**
    * Stores the Throwable.getCause() method if this method is available.
    * This should be the case for all JDKs > 1.4.
    */
   @SuppressWarnings("unused")
   private static final Method GET_CAUSE;

   /**
    * Stores the Throwable.initCause(Throwable) method if this method
    * is available.
    * This should be the case for all JDKs > 1.4.
    */
   private static final Method INIT_CAUSE;
   /** A empty object array. */
   private static final Object[] EMPTY_ARRAY = new Object[0];

   private static final int MAX_NESTING_DEPTH = 15;

   /**
    * list of parameter for this exception The list is not thread save!
    */
   private final Map<String, List<Serializable>> mParameters = new HashMap<String, List<Serializable>>();

   /**
    * Remember the ERROR_RESPONSE_ID. Intention is to log this id with the
    * exception and pass the Id to the recipient. It should be really easy to
    * find the exception in the log.
    */
   private final String mTrackingNumber;

   /** The error ID for this loggable */
   private final LogMessageInfo mLogMessageInfo;

   /** The point in time when this event occurred. */
   private final long mEventTime;

   /** The node id. */
   private final String mNodeId;

   /** The id for this instance id. */
   private final String mInstanceId;

   /** The thread id. */
   private final long mThreadId;

   /** The thread name. */
   private final String mThreadName;

   /**
    * The Throwable that caused this loggable.
    * Should be equal to mOuter.getCause()
    */
   private Throwable mCause;

   /** The outer exception, where this loggable belongs to. */
   private Loggable mOuter;

   private String mClassName = null;
   private String mMethodName = null;

   static
   {
      INSTANCE_ID = getStaticInstanceId();

      Method theGetCauseMethod = null;
      Method theInitCauseMethod = null;
      try
      {
         theGetCauseMethod
               = Throwable.class.getDeclaredMethod("getCause", new Class[0]);
         theInitCauseMethod
               = Throwable.class.getDeclaredMethod("initCause",
                  new Class[] {Throwable.class});
      }
      catch (Exception ex)
      {
         // Warning, cause this should not fail with JDK > 1.4
         logger.log(Level.WARNING, "Could not initialize, will run without.",
               ex);
      }
      GET_CAUSE = theGetCauseMethod;
      INIT_CAUSE = theInitCauseMethod;
   }

   /**
    * Create this loggable provide the 'Loggable' functionality for the
    * given outer loggable.
    * @param outer the the outer loggable.
    * @param errorId the static LogMessageInfo for this Loggable.
    */
   public LoggableImpl (Loggable outer, LogMessageInfo errorId)
   {
      this(outer, errorId, THREAD_ID_GENERATOR.getThreadId(),
          Thread.currentThread().getName(), INSTANCE_ID, NODE_ID);
   }

   /**
    * Create this loggable provide the 'Loggable' functionality for the
    * given outer loggable with an initial cause.
    * @param outer the the outer loggable.
    * @param errorId the static LogMessageInfo for this Loggable.
    * @param cause the cause of the outer.
    */
   public LoggableImpl (Loggable outer, LogMessageInfo errorId,
       Throwable cause)
   {
      this(outer, errorId, THREAD_ID_GENERATOR.getThreadId(),
          Thread.currentThread().getName(), INSTANCE_ID,
            NODE_ID, cause);
   }

   /**
    * Create this loggable provide the 'Loggable' functionality for the
    * given outer loggable with the given dynamic parameters.
    * @param outer the the outer loggable.
    * @param errorId the static LogMessageInfo for this Loggable.
    * @param threadId the threadId to be set.
    * @param threadName the threadName to be set.
    * @param instanceId the instanceId to be set.
    * @param nodeId the nodeId to be set.
    */
   public LoggableImpl (Loggable outer, LogMessageInfo errorId,
       long threadId, String threadName, String instanceId, String nodeId)
   {
      mEventTime = System.currentTimeMillis();
      mTrackingNumber = Integer.toHexString(RANDOM_ID_GENERATOR.nextInt());
      mLogMessageInfo = errorId;
      mThreadId = threadId;
      mThreadName = threadName;
      mInstanceId = instanceId;
      mNodeId = nodeId;
      mOuter = outer;
      initInternalParameters();
      initThreadContextParameters();
   }

   /**
    * Create this loggable provide the 'Loggable' functionality for the
    * given outer loggable with the given dynamic parameters and an
    * initial cause..
    * @param outer the the outer loggable.
    * @param errorId the static LogMessageInfo for this Loggable.
    * @param threadId the threadId to be set.
    * @param threadName the threadName to be set.
    * @param instanceId the instanceId to be set.
    * @param nodeId the nodeId to be set.
    * @param cause the cause of the outer.
    */
   public LoggableImpl (Loggable outer, LogMessageInfo errorId,
       long threadId, String threadName, String instanceId, String nodeId,
       Throwable cause)
   {
      mEventTime = System.currentTimeMillis();
      fixChaining(cause);
      Throwable thr = cause;
      int depth = 0;
      while (thr != null
          && !(thr instanceof Loggable)
          && depth < MAX_EXCEPTION_CHAIN_UP)
      {
          thr = thr.getCause();
          depth++;
      }
      if (thr instanceof Loggable)
      {
         mTrackingNumber = ((Loggable) thr).getTrackingNumber();
      }
      else
      {
         mTrackingNumber = Integer.toHexString(RANDOM_ID_GENERATOR.nextInt());
      }
      mLogMessageInfo = errorId;
      mThreadId = threadId;
      mThreadName = threadName;
      mInstanceId = instanceId;
      mNodeId = nodeId;
      mOuter = outer;
      initCause(cause);
      initInternalParameters();
      initThreadContextParameters();
   }

   /**
    * Sets the cause of this throwable.
    *
    * This method should be called after the call to the
    * {@link Throwable#initCause(Throwable)} for the case
    * the super call fails.
    *
    * @param cause the cause of this Exception.
    */
   public final void initCause (Throwable cause)
   {
      mCause = cause;
      addParameter(CAUSE_PARAMETER_NAME, cause);
      fixChaining(cause);
      collectNestedData(this);
   }

   /**
    * Pull up nested information.
    * This method goes down the exception chain of the given
    * loggable and if it find getters for properties,
    * like <code>getSql()</code> adds the values of these
    * properties as parameters to the loggable. The search is
    * stopped when either a {@link Loggable} is found in the list,
    * the end of the chain is reached or after MAX_NESTING_DEPTH
    * steps down the chain.
    * @param loggable the Loggable to be feed with parameters.
    */
   public static void collectNestedData (Loggable loggable)
   {
      try
      {
         Throwable current = loggable.getCause();
         int nesting = 0;
         while (current != null)
         {
            if (nesting++ > MAX_NESTING_DEPTH)
            {
               logger.log(Level.FINE,
                     "Stopped collecting nested information max depth "
                     + "reached for given exception.", loggable);
               break;
            }
            if (!(current instanceof Loggable))
            {
                collectParameters(loggable, current, nesting);
            }
            current = current.getCause();
         }
      }
      catch (Exception unexpected)
      {
         // do not risk any side effect here
         logger.log(
               Level.SEVERE, "Unexpected exception, ignored.", unexpected);
      }
   }

   private static void collectParameters (
	       Loggable loggable, Throwable thr, int nesting)
	       throws IllegalAccessException, InvocationTargetException
	   {
	       final Method[] methods = thr.getClass().getMethods();
	       for (int i = 0; i < methods.length; i++)
	       {
	          final int modifier = methods[i].getModifiers();
	          if (methods[i].getDeclaringClass() != Throwable.class
	              && methods[i].getDeclaringClass() != Object.class
	              && methods[i].getParameterTypes().length == 0
	              && methods[i].getExceptionTypes().length == 0
	              && Modifier.isPublic(modifier)
	              && !Modifier.isStatic(modifier)
	              && methods[i].getName().startsWith(GETTER_METHOD_PREFIX)
	              && !methods[i].getReturnType().equals(Void.TYPE))
	          {
	              final Object result = methods[i].invoke(thr, (Object[]) null);
	              if (result != null && result != thr.getCause())
	              {
	                  loggable.addParameter(
	                      "CAUSE_" + nesting + "_" + thr.getClass().getName()
	                          + "#" + methods[i].getName().substring(
	                              GETTER_METHOD_PREFIX_LENGTH),
	                      asString(result));
	              }
	          }
	       }
	   }

   /**
    * Tries to fix the exception chaining for the given Throwable.
    * Some exception classes still use a none standard way
    * to nest exceptions. This method tries best to detect this
    * classes and pass the nested exceptions into the standard
    * nesting mechanism available since JDK1.4 with the throwable
    * class. It is save to call this method several times for a
    * give exception.
    * @param ex the exception to be checked.
    */
   public static void fixChaining (Throwable ex)
   {
      try
      {
         Throwable current = ex;
         int nesting = 0;
         while (INIT_CAUSE != null && current != null
               && !(current instanceof Loggable))
         {
            if (nesting++ > MAX_NESTING_DEPTH)
            {
               logger.log(Level.FINE,
                     "Stopped fixing exception nesting cause max depth "
                     + "reached for given exception.", ex);
               break;
            }
            if (current.getCause() == null)
            {
               final Method theGetCauseMethod
                     = findGetCauseMethod(current.getClass().getMethods());
               if (theGetCauseMethod != null)
               {
                  initCause(current, theGetCauseMethod);
               }
            }
            current = current.getCause();
         }
      }
      catch (Exception unexpected)
      {
         // do not risk any side effect here
         logger.log(
               Level.SEVERE, "Unexpected exception, ignored.", unexpected);
      }
   }

   static Method findGetCauseMethod (final Method[] methods)
   {
      Method theGetCauseMethod = null;
      for (int i = 0; i < methods.length; i++)
      {
         if (methods[i].getDeclaringClass() == Throwable.class)
         {
            continue;
         }
         final int modifier = methods[i].getModifiers();
         if (methods[i].getParameterTypes().length == 0
             && Modifier.isPublic(modifier)
             && !Modifier.isStatic(modifier)
             && Throwable.class.isAssignableFrom(methods[i].getReturnType()))
         {
             // if the method is called getCause, assume it does 
             // what it is named FIXES #76
             if (methods[i].getName().equals("getCause"))
             {
                 theGetCauseMethod = methods[i];
                 break;
             }
            if (theGetCauseMethod != null)
            {
               // 2nd hit, safety first
               logger.fine("Found 2 matching methods "  + theGetCauseMethod
                     + " or " + methods[i] + ".");
               theGetCauseMethod = null;
               break;
            }
            theGetCauseMethod = methods[i];
         }
      }
      return theGetCauseMethod;
   }

   private static void initCause (Throwable current, Method theGetCauseMethod)
   {
      try
      {
         final Throwable cause
               = (Throwable) theGetCauseMethod.invoke(
                  current, EMPTY_ARRAY);
         INIT_CAUSE.invoke(current, new Object[] {cause});
      }
      catch (Exception e)
      {
         logger.log(Level.FINEST, "Failed to init cause for " + current
               + " using " + theGetCauseMethod + " got a exception.", e);
      }
   }

    private static String asString (Object obj)
    {
        String result;
        if (obj instanceof Object[])
        {
            result = ArraysUtil.toString((Object[]) obj);
        }
        else
        {
            result = String.valueOf(obj);
        }
        return StringUtil.trimLength(
            result,
            MAX_REASONABLE_PARAMETER_LENGTH);
    }

    /**
    * Adds a new named parameter. The parameter is added at the end of the list
    * of parameters. The same <code>name</code> might occur several times.
    *
    * @param name the name of the parameter.
    * @param value The value of the parameter
    */
   public final void addParameter (String name, Serializable value)
   {
      List<Serializable> values = mParameters.get(name);
      if (values == null)
      {
         values = new ArrayList<Serializable>();
         mParameters.put(name, values);
      }
      values.add(value);
   }

   /** {@inheritDoc} */
   public List<Serializable> getParameter (String name)
   {
      final List<Serializable> values = (List<Serializable>) mParameters.get(name);

      final List<Serializable> result;
      if (values != null)
      {
         result = Collections.unmodifiableList(values);
      }
      else
      {
         result = Collections.emptyList();
      }
      return result;
   }

   /** {@inheritDoc} */
   public Set<String> getParameterNames ()
   {
      return Collections.unmodifiableSet(mParameters.keySet());
   }

   /** {@inheritDoc} */
   public final LogMessageInfo getLogMessageInfo ()
   {
      return mLogMessageInfo;
   }

   /** {@inheritDoc} */
   public final String getTrackingNumber ()
   {
      return mTrackingNumber;
   }

   /** {@inheritDoc} */
   public final long getEventTime ()
   {
      return mEventTime;
   }

   /** {@inheritDoc} */
   public final String getNodeId ()
   {
      return mNodeId;
   }

   /** {@inheritDoc} */
   public final String getInstanceId ()
   {
      return mInstanceId;
   }

   /** {@inheritDoc} */
   public final long getThreadId ()
   {
      return mThreadId;
   }

   /** {@inheritDoc} */
   public final String getThreadName ()
   {
      return mThreadName;
   }

   /** {@inheritDoc} */
   public Throwable getCause ()
   {
      return mCause;
   }

   /** {@inheritDoc} */
   public void log ()
   {
      getSource();
      logger.logp(getLogMessageInfo().getLogLevel(), mClassName, mMethodName,
            getMessage(), mOuter);
   }

   /** {@inheritDoc} */
   public String getMessage ()
   {
      return getLogMessageInfo().formatMessage(
          mParameters, new StringBuffer()).toString();
   }

   /** {@inheritDoc} */
   public String getSourceClass ()
   {
       getSource();
       return mClassName;
   }

   /** {@inheritDoc} */
   public String getSourceMethod ()
   {
       getSource();
       return mMethodName;
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      final StringBuffer sb = new StringBuffer();
      if (mOuter != null)
      {
          sb.append(mOuter.getClass().getName());
      }
      else
      {
          sb.append(getClass().getName());
      }
      sb.append(": ");
      getLogMessageInfo().formatMessage(mParameters, sb);
      return sb.toString();
   }

   /** {@inheritDoc} */
   public String toDetailedString ()
   {
       final StringBuffer sb = new StringBuffer();
       LoggableImpl.appendParameters(sb, this);
       Throwable cause = null;
       if (mOuter != null)
       {
           cause = mOuter.getCause();
       }
       if (cause == null)
       {
           cause = getCause();
       }
       // add parameters of nested chain
       int depth = 0;
       while (cause != null && depth < MAX_EXCEPTION_CHAIN_UP)
       {
           if (cause instanceof Loggable)
           {
               sb.append("\nCaused by: ");
               LoggableImpl.appendParameters(sb, (Loggable) cause);
               break;
           }
           cause = cause.getCause();
           depth++;
       }
       cause = null;
       if (mOuter != null)
       {
           cause = mOuter.getCause();
       }
       if (cause == null)
       {
           cause = getCause();
       }
       if (cause != null)
       {
           sb.append('\n');
           sb.append(ThrowableUtil.toString(cause));
       }
       return sb.toString();
   }

   private void initInternalParameters ()
   {
      addParameter(MESSAGE_INFO_PARAMETER_NAME, mLogMessageInfo);
      addParameter(TRACKING_NUMBER_PARAMETER_NAME, mTrackingNumber);
      addParameter(EVENT_TIME_PARAMETER_NAME, new Long(mEventTime));
      addParameter(THREAD_ID_PARAMETER_NAME, new Long(mThreadId));
      addParameter(THREAD_NAME_PARAMETER_NAME, mThreadName);
      addParameter(INSTANCE_ID_PARAMETER_NAME, mInstanceId);
      addParameter(NODE_ID_PARAMETER_NAME, mNodeId);
      addParameter(APPLICATION_NAME_PARAMETER_NAME,
            mLogMessageInfo.getAppName());
      addParameter(GROUP_NAME_PARAMETER_NAME, mLogMessageInfo.getGroupName());
   }

   private final void initThreadContextParameters ()
   {
       final Iterator<Map.Entry<String, String>> i = LogThreadContext.get().entrySet().iterator();
       while (i.hasNext())
       {
           final Map.Entry<String, String> entry = (Map.Entry<String, String>) i.next();
           addParameter(
               CONTEXT_PARAMETER_PREFIX + entry.getKey(), 
               String.valueOf(entry.getValue()));
       }
   }
   
   private final void getSource ()
   {
      // not analyzed yet.
      if (mMethodName == null || mClassName == null)
      {
          final StackTraceElement[] stack = new Throwable().getStackTrace();
          // First, search back to a method in the Logger class.
          int ix = 0;
          boolean found = false;
          while (ix < stack.length)
          {
             final StackTraceElement frame = stack[ix];
             final String cname = frame.getClassName();
             if (cname.equals(CLASSNAME))
             {
                found = true;
             }
             else if (found)
             {
                break;
             }
             ix++;
          }
          // Now search for the first frame before the "LoggableImpl" class or
          // LogMessageInfo class.
          while (ix < stack.length)
          {
             final StackTraceElement frame = stack[ix];
             try
             {
                final String cname = frame.getClassName();
                final Class<?> clazz = Class.forName(cname);
                if (! (Loggable.class.isAssignableFrom(clazz)
                      || LogMessageInfo.class.isAssignableFrom(clazz)))
                {
                   // We've found the relevant frame.
                   setMethodAndClass(frame);
                   break;
                }
             }
             catch (ClassNotFoundException e)
             {
                setMethodAndClass(frame);
                break;
             }
             ix++;
          }
      }
   }

   private void setMethodAndClass (final StackTraceElement frame)
   {
      mClassName = frame.getClassName();
      mMethodName = frame.getMethodName();
      final String fileName = frame.getFileName();
      if (fileName != null)
      {
         final int lineNumber = frame.getLineNumber();
         if (lineNumber >= 0)
         {
            mMethodName = frame.getMethodName()
                  + "(" + fileName + ":" + lineNumber + ")";
         }
         else
         {
            mMethodName = frame.getMethodName() + "(" + fileName + ")";
         }
      }
      else if (frame.getMethodName().indexOf('(') < 0)
      {
         mMethodName = frame.getMethodName() + "()";
      }
      else
      {
          mMethodName = frame.getMethodName();
      }
   }

   private static void appendParameters (StringBuffer sb, Loggable loggable)
   {
       sb.append(loggable.toString());

       final Object[] params = loggable.getParameterNames().toArray();
       Arrays.sort(params);
       final Iterator<Object> parameterNames
           = Arrays.asList(params).iterator();
       while (parameterNames.hasNext())
       {
           final String parameterName = (String) parameterNames.next();
           sb.append("\n\t");
           sb.append(parameterName);
           sb.append(": \t");
           sb.append(loggable.getParameter(parameterName));
       }
   }

   private static String getStaticNodeId ()
   {
      String nodeId = DUMMY_NODE_ID;
      try
      {
         nodeId = InetAddress.getLocalHost().getHostAddress();
      }
      catch (UnknownHostException e)
      {
         System.err.println("Error retrieving inet address of local host, "
               + "setting " + DUMMY_NODE_ID + " as node id");
      }
      return nodeId;
   }

   private static String getStaticInstanceId ()
   {
      return System.getProperty(INSTANCE_NAME_PROPERTY, DUMMY_INSTANCE_ID);
   }


   private static class ThreadIdHolder
         extends ThreadLocal<Long>
   {
      private static final long INITIAL_THREAD_ID = 10L;
      private static long sNextThreadId = INITIAL_THREAD_ID;

      protected Long initialValue ()
      {
         return new Long(sNextThreadId++);
      }

      long getThreadId ()
      {
         return ((Long) get()).longValue();
      }
   }
}
