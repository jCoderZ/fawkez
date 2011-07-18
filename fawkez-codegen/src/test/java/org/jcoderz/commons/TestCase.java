/*
 * $Id: TestCase.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import junit.framework.TestSuite;

import org.jcoderz.commons.util.LoggingUtils;

/**
 * Base class for a JUnit test that provides additional utility methods.
 *
 * @author Michael Griffel
 */
public abstract class TestCase
      extends junit.framework.TestCase
{
   private static final String TEST_METHODS = "methods";
   private static final String ANT_PROPERTY = "${methods}";
   private static final String DELIMITER = ",";

   /**
    * System property name for the projects base directory.
    */
   private static final String BASEDIR = "basedir";

   /**
    * Default base directory if the system property for the base directory
    * is not set.
    */
   private static final String DEFAULT_BASEDIR = ".";

   // sets all loggers to Level.ALL
   static
   {
      LoggingUtils.setGlobalHandlerLogLevel(Level.ALL);
   }

   /**
    * Default constructor.
    */
   public TestCase ()
   {
      super();
   }

   /**
    * Constructs a TestCase with the given <code>name</code>.
    * @param name The test case name.
    */
   public TestCase (String name)
   {
      super(name);
   }

   /**
    * Returns the projects base directory.
    * @return the projects base directory.
    */
   public static File getBaseDir ()
   {
      return new File(System.getProperty(BASEDIR, DEFAULT_BASEDIR));
   }

   /**
    * Returns the hostname of localhost.
    *
    * @return String the hostname of localhost.
    */
   public static String getHostName ()
   {
      String result = null;
      try
      {
         final InetAddress addr = InetAddress.getLocalHost();
         result = addr.getCanonicalHostName();
      }
      catch (UnknownHostException e)
      {
         // ignore
      }
      return result;
   }

   /**
    * Check to see if the test cases property is set. Ignores Ant's
    * default setting for the property (or null to be on the safe side).
    * @return boolean true if the test case property is set, false else
    **/
   public static boolean hasTestCases ()
   {
       return
           System.getProperty(TEST_METHODS) == null
           || System.getProperty(TEST_METHODS).equals(ANT_PROPERTY)
           ? false : true;
   }

    /**
     * Create a TestSuite using the TestCase subclass and the list
     * of test cases to run specified using the TEST_CASES JVM property.
     *
     * @param testClass the TestCase subclass to instantiate as tests in
     * the suite.
     *
     * @return a TestSuite with new instances of testClass for each
     * test case specified in the JVM property.
     *
     * @throws IllegalArgumentException if testClass is not a subclass or
     * implementation of junit.framework.TestCase.
     *
     * @throws RuntimeException if testClass is written incorrectly and does
     * not have the approriate constructor.
     **/
   public static TestSuite getSuite (Class testClass)
         throws RuntimeException, IllegalArgumentException
   {
      if (!TestCase.class.isAssignableFrom(testClass))
      {
         throw new IllegalArgumentException
            ("Must pass in a subclass of TestCase");
      }
      final TestSuite suite = new TestSuite();
      try
      {
         final Constructor constructor
               = testClass.getConstructor(new Class[] {});
         final List testCaseNames = getTestCaseNames();
         for (final Iterator testCases = testCaseNames.iterator();
                  testCases.hasNext();)
         {
            final String testCaseName = (String) testCases.next();
            final TestCase test = (TestCase) constructor.newInstance(
                  new Object[] {});
            test.setName(testCaseName);
            suite.addTest(test);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException
               (testClass.getName() + " doesn't have the proper constructor");
      }
      return suite;
   }

   /**
    * Create a List of String names of test cases specified in the
    * JVM property in comma-separated format.
    *
    * @return a List of String test case names
    *
    * @throws NullPointerException if the TEST_CASES property
    * isn't set
    **/
   private static List getTestCaseNames ()
         throws NullPointerException
   {
      if (System.getProperty(TEST_METHODS) == null)
      {
         throw new NullPointerException(
               "Property <" + TEST_METHODS + "> is not set");
      }
      final List testCaseNames = new ArrayList();
      final String testCases = System.getProperty(TEST_METHODS);
      final StringTokenizer tokenizer
            = new StringTokenizer(testCases, DELIMITER);
      while (tokenizer.hasMoreTokens())
      {
         testCaseNames.add(tokenizer.nextToken());
      }
      return testCaseNames;
   }
}
