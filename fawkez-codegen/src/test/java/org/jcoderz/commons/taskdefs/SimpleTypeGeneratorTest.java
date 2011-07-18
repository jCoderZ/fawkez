/*
 * $Id: SimpleTypeGeneratorTest.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.io.File;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.jcoderz.commons.TestCase;


/**
 * JUnit test for the Ant task
 * {@link org.jcoderz.commons.taskdefs.SimpleTypeGenerator}.
 *
 * @author Michael Griffel
 */
public class SimpleTypeGeneratorTest
      extends TestCase
{
   private SimpleTypeGenerator mGenerator;
   private File mDestDir;

   /** {@inheritDoc} */
   protected void setUp ()
         throws Exception
   {
      super.setUp();
      mDestDir =  LogMessageGeneratorTest.mkdir("build/test");
      final File in = new File(getBaseDir(), "test/xml/simple-types.xml");
      final File out = new File(mDestDir, "simple-types.out");
      final SimpleTypeGenerator g = new SimpleTypeGenerator();
      g.setDestdir(mDestDir);
      g.setFailonerror(false);
      g.setForce(true);
      g.setIn(in);
      g.setOut(out);
      g.setTaskName("test-message-generator");
      g.setFailonerror(true);
      g.setLocation(new Location("location"));

      final Project project = new Project();
      project.setBaseDir(getBaseDir());
      project.setName("JUnit test");
      g.setProject(project);
      mGenerator = g;
   }

   /** Tests the Ant task. */
   public void testExecute ()
   {
      mGenerator.execute();

      final File testEnumerationFile = new File(mDestDir,
            "org/jcoderz/commons/Color.java");
      assertTrue("Generated Enumeration Java File "
            + testEnumerationFile + " exists",
            testEnumerationFile.exists());

      final File testRestrictedStringFile = new File(mDestDir,
            "org/jcoderz/commons/FooString.java");
      assertTrue("Generated restricted string Java File "
            + testRestrictedStringFile + " exists",
            testRestrictedStringFile.exists());
      final File testRegexStringFile = new File(mDestDir,
         "org/jcoderz/commons/RegexString.java");
      assertTrue("Generated regex string Java File "
            + testRegexStringFile + " exists",
            testRegexStringFile.exists());
      final File testRestrictedLongFile = new File(mDestDir,
      "org/jcoderz/commons/FooLong.java");
      assertTrue("Generated restricted Long Java File "
            + testRestrictedLongFile + " exists",
            testRestrictedLongFile.exists());
   }

}
