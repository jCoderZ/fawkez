/*
 * $Id: ThrowableUtil.java 1625 2010-04-07 06:33:35Z amandel $
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helper class around throwables.
 * @author Andreas Mandel
 */
public final class ThrowableUtil
{
   /** Name of getter methods start with this prefix. */
   private static final String GETTER_METHOD_PREFIX = "get";
   /** Name of getter methods start with this prefix. */
   private static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
   /** Length of the getter prefix. */
   private static final int GETTER_METHOD_PREFIX_LENGTH
       = GETTER_METHOD_PREFIX.length();
   /** Length of the boolean getter prefix. */
   private static final int BOOLEAN_GETTER_METHOD_PREFIX_LENGTH
       = BOOLEAN_GETTER_METHOD_PREFIX.length();
   
   private static final String CLASSNAME = ThrowableUtil.class.getName();
   private static final Logger logger = Logger.getLogger(CLASSNAME);

   static
   {
      try
      {
         Throwable.class.getDeclaredMethod("getCause", new Class[0]);
         Throwable.class.getDeclaredMethod("initCause",
                  new Class[] {Throwable.class});
      }
      catch (Exception ex)
      {
         // Warning, cause this should not fail with JDK > 1.4
         logger.log(Level.WARNING, "Could not initialize, will run without.",
               ex);
      }
   }

   private ThrowableUtil ()
   {
      // NO Instances
   }

   /**
    * Tries to read additional property like information from this throwable 
    * and fills it in a map suitable for detailed information output. 
    * @param thr the throwable to analyze.
    * @return a Map pointing from String property names to the value. 
    */
   public static Map<String, Object> getProperties(Throwable thr)
   {
       final Map<String, Object> result = new HashMap<String, Object>();
       final Method[] methods = thr.getClass().getMethods();
       for (int i = 0; i < methods.length; i++)
       {
          final int modifier = methods[i].getModifiers();
          if (methods[i].getDeclaringClass() != Throwable.class
              && methods[i].getDeclaringClass() != Object.class
              && methods[i].getParameterTypes().length == 0
              && Modifier.isPublic(modifier)
              && !Modifier.isStatic(modifier)
              && !methods[i].getReturnType().equals(Void.TYPE))
          {
              try
              {
                  if (methods[i].getName().startsWith(GETTER_METHOD_PREFIX))
                  {
                      final Object value
                          = methods[i].invoke(thr, (Object[]) null);
                      final String key
                          = methods[i].getName().substring(
                              GETTER_METHOD_PREFIX_LENGTH);
                      result.put(key, value);
                  }
                  else if (methods[i].getName().startsWith(
                          BOOLEAN_GETTER_METHOD_PREFIX)
                      && (methods[i].getReturnType().equals(Boolean.class)
                          || methods[i].getReturnType().equals(
                              java.lang.Boolean.TYPE)))
                  {
                      final Object value
                          = methods[i].invoke(thr, (Object[]) null);
                      final String key
                          = methods[i].getName().substring(
                              BOOLEAN_GETTER_METHOD_PREFIX_LENGTH);
                      result.put(key, value);
                  }
              }
              catch (InvocationTargetException e)
              {
                  // Ignore this property, continue with next
              }
              catch (IllegalArgumentException e)
              {
                  // Ignore this property, continue with next
              }
              catch (IllegalAccessException e)
              {
                  // Ignore this property, continue with next
              }
          }
       }
       return result;
   }
   
   /**
    * Dumps the stack trace of the given throwable to its String representation.
    * @param thr the throwable to dump the stack trace from.
    * @return a String representation of the given throwable
    * @see Throwable#printStackTrace()
    */
   public static String toString (Throwable thr)
   {
       final StringWriter sw = new StringWriter();
       PrintWriter pw = null;
       try
       {
           pw = new PrintWriter(sw);
           thr.printStackTrace(pw);
       }
       finally
       {
           IoUtil.close(pw);
           IoUtil.close(sw);
       }
       return sw.toString();
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
}
