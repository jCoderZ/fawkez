/*
 * $Id: LoggingProxy.java 1559 2009-10-08 19:39:07Z amandel $
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This class can be used to proxy any object, providing entering and
 * exiting logging for all <i>interfaces</i> of the object.
 * </p>
 * <p>
 * <b>Note:</b> Java Dynamic Proxies only work on <i>interfaces</i>.
 * The object returned by the {@link #getProxy(Object)} can be cast to
 * any interface implemented by the argument or one of its ancestors. It
 * can't, however, be cast to an implementation class.
 * </p>
 *
 * @author Albrecht Messner
 * @author Andreas Mandel
 */
public final class LoggingProxy
      implements InvocationHandler
{
   private final Object mRealObject;
   private final String mRealObjectClassName;
   private final Logger mObjectLogger;

   /**
    * Create a proxy that directs all calls to the real object and logs all
    * method calls with entering/exiting/throwing, using the given logger.
    *
    * @param realObject the object for which a proxy is created
    * @param logger the logger to which calls are logged
    */
   private LoggingProxy (Object realObject, Logger logger)
   {
      mRealObject = realObject;
      mRealObjectClassName = mRealObject.getClass().getName();
      mObjectLogger = logger;
   }

   /**
    * Static factory that wraps an object into a proxy depending on the
    * log level for that object.
    *
    * @param obj an object for which a proxy should be created
    * @return a logging proxy for the obj, if the log level for that
    *       object is FINER or finest, the object itself otherwise
    */
   public static Object getProxy (Object obj)
   {
      final String classname = obj.getClass().getName();
      final Logger logger = Logger.getLogger(classname);

      final Object proxy;
      if (logger.isLoggable(Level.FINER))
      {
         // collect all interfaces implemented by this objects class and
         // its super classes
         //  Note: Ne do not add super-interfaces here....
         final Set interfaces = new HashSet();
         Class currentClass = obj.getClass();
         while (currentClass != null)
         {
            interfaces.addAll(Arrays.asList(currentClass.getInterfaces()));
            currentClass = currentClass.getSuperclass();
         }

         proxy = Proxy.newProxyInstance(
               obj.getClass().getClassLoader(),
               (Class[]) interfaces.toArray(new Class[interfaces.size()]),
               new LoggingProxy(obj, logger));
      }
      else
      {
         proxy = obj;
      }
      return proxy;
   }

   /**
    * Log the entering, exiting and throwing events of the proxied object.
    *
    * @see java.lang.reflect.InvocationHandler#invoke(
    *       java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
    */
   public Object invoke (Object proxy, Method method, Object[] args)
         throws Throwable
   {
      final boolean isLoggable = mObjectLogger.isLoggable(Level.FINER);

      if (isLoggable)
      {
         if (args == null)
         {
            mObjectLogger.entering(mRealObjectClassName, method.getName());
         }
         else
         {
            final Object[] args2 = new Object[args.length];
            for (int i = 0; i < args.length; i++)
            {
                if (args[i] != null && args[i].getClass().isArray())
                {
                    args2[i] = ArraysUtil.toString(args[i]);
                }
                else
                {
                    args2[i] = args[i];
                }
            }
            mObjectLogger.entering(
                  mRealObjectClassName, method.getName(), args2);
         }
      }

      final Object result = invokeMethod(method, args, isLoggable);

      if (isLoggable)
      {
         if (result != null || method.getReturnType() != Void.TYPE)
         {
           mObjectLogger.exiting(
                 mRealObjectClassName, method.getName(), 
                 ArraysUtil.toString(result));
         }
         else
         {
           mObjectLogger.exiting(mRealObjectClassName, method.getName());
         }
      }

      return result;
   }

   private Object invokeMethod (Method method, Object[] args,
         boolean isLoggable)
         throws Throwable
   {
      final Object result;
      try
      {
         result = method.invoke(mRealObject, args);
      }
      catch (InvocationTargetException x)
      {
         if (isLoggable)
         {
            mObjectLogger.throwing(
                  mRealObjectClassName, method.getName(), x.getCause());
         }
         throw x.getCause();
      }
      catch (Exception x)
      {
         if (isLoggable)
         {
            mObjectLogger.throwing(mRealObjectClassName, method.getName(), x);
         }
         throw x;
      }
      return result;
   }
}
