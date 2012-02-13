/*
 * $Id: JDepend.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.phoenix.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoader;
import org.apache.bcel.util.ClassLoaderRepository;

/**
 * @author Michael Griffel
 */
public final class JDepend
{
   private static final ClassLoaderRepository REPOSITORY
         = new ClassLoaderRepository(new ClassLoader());

   private static final char[] PROPELLER_CHARS 
         = new char[]{'\\', '|', '/', '-'};
   
   private final List mClassDependencies = new ArrayList();
   private final List mAllClasses = new ArrayList();
   private final List mPackageDependency = new ArrayList();

   private String mOutputFilename = "out.dot";
   private File mClassDir = new File("build/classes");
   private String mIncludePattern = ".*";
   private String mExcludePattern = ""; 

   private int mPropellerIndex = 0;
   
   private JDepend ()
   {
   }

   public static void main (String[] args) 
         throws ClassNotFoundException, FileNotFoundException
   {
      final JDepend main = new JDepend();
      main.parseArgs(args);  
      main.findClasses();
      main.writeDotFile();
   }

   private void parseArgs (String[] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         if (args[i].equals("-out"))
         {
            mOutputFilename = args[i + 1];
         }
         else if (args[i].equals("-classes"))
         {
            mClassDir = new File(args[i + 1]);
            if (! mClassDir.isDirectory())
            {
               throw new IllegalArgumentException("The argument '" + mClassDir 
                     + "' for parameter classdir is not a valid directory");
            }
         }
         else if (args[i].equals("-include"))
         {
            mIncludePattern = args[i + 1];
         }
         else if (args[i].equals("-exclude"))
         {
            mExcludePattern = args[i + 1];
         }
         else if (args[i].startsWith("-h") 
                  || args[i].startsWith("--h") 
                  || args[i].indexOf('?') != -1)
         {
            help();
         }
         else
         {
            throw new IllegalArgumentException(
                    "Unknown parameter '" + args[i] + "'");
         } 
         ++i;
      }
   }

   private static void help ()
   {
      System.err.println("     _ ____                            _"); 
      System.err.println("    | |  _ \\  ___ _ __   ___ _ __   __| |");
      System.err.println(" _  | | | | |/ _ \\ \'_ \\ / _ \\ \'_ \\ / _` |"); 
      System.err.println("| |_| | |_| |  __/ |_) |  __/ | | | (_| |"); 
      System.err.println(" \\___/|____/ \\___| .__/ \\___|_| |_|\\__,_|"); 
      System.err.println("                 |_|");
      System.err.println("   )=- The Java Dependency Checker -=(");
      System.err.println();
      System.err.println("Usage:");
      System.err.println(
              " -classes DIR      ... path to class directory [build/classes]");
      System.err.println(
              " -out FILE         ... path to the output file [out.dot]");
      System.err.println(
              " -include PATTERN  ... regex for include packages [.*]");
      System.err.println(
              " -exclude PATTERN  ... regex for exclude packages []");
      System.err.println("");
      System.exit(-1);
   }

   public void writeDotFile () 
         throws FileNotFoundException
   {
      final PrintWriter out 
          = new PrintWriter(new FileOutputStream(mOutputFilename));
      out.println("digraph G  {\n" 
            + "  center=\"\"\n" 
            + "  node[width=.25,hight=.375,fontsize=10,shape=box]");
      for (final Iterator iterator = mPackageDependency.iterator(); 
          iterator.hasNext();)
      {
         final PackageDependency d = (PackageDependency) iterator.next();
         out.println("   " + d.toDotString() 
             + " [fontname=\"verdana\", fontcolor=\"black\", fontsize=10.0];");
      }
      out.println("}");
      out.close();
   }

   private void visit (String clazz) 
         throws ClassNotFoundException
   {
      final JavaClass javaClass = REPOSITORY.loadClass(clazz);
      final ConstantPool constantPool = javaClass.getConstantPool();
      mAllClasses.add(clazz);
      propeller();
      final Constant[] constant = constantPool.getConstantPool();
      for (int i = 0; i < constant.length; i++)
      {
         final Constant c = constant[i];
         if (c instanceof ConstantClass)
         {
             final ConstantClass constantClass = (ConstantClass) c;
           
            String newClazz 
               = (String) constantClass.getConstantValue(constantPool);
            newClazz = newClazz.replace('/', '.');
            
            if (mAllClasses.contains(newClazz))
            {
               continue;
            }
            
   
            if (!newClazz.matches(mIncludePattern)
                || newClazz.startsWith("[") 
                || newClazz.matches(mExcludePattern))
            {
               //System.out.println("Skipping '" + newClazz + "'");
               continue;
            }
            
            final JDepend.ClassDependency x 
                  = new JDepend.ClassDependency(clazz, newClazz);
            final JDepend.PackageDependency p 
                  = new JDepend.PackageDependency(clazz, newClazz);
            if (! mPackageDependency.contains(p))
            {
               mPackageDependency.add(p);
            }
            
            if (! mClassDependencies.contains(x))
            {
               mClassDependencies.add(x);
               //System.out.println("visit: " + newClazz);
               visit(newClazz);
            }
         }
      }

   }

   /**
    * 
    */
   private void propeller ()
   {
      System.out.print("\b");
      System.out.print(
              PROPELLER_CHARS[mPropellerIndex++ % PROPELLER_CHARS.length]);
   }

   public void findClasses ()
   {
      System.out.print("Scanning classpath ...  ");
      findClasses(mClassDir, null);
      System.out.println();
      System.out.println("Scanned " + mAllClasses.size() + " classes.");
   }

   private void findClasses (File dir, String pkg) 
   {
      final File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         if (files[i].isDirectory())
         {
            final String newpkg;
            if (pkg == null)
            {
               newpkg = files[i].getName();
            }
            else
            {
               newpkg = pkg + "." + files[i].getName();
            }
            findClasses(files[i], newpkg);
         }
         else if (files[i].getName().endsWith(".class") 
                  && files[i].getName().indexOf('$') == -1)
         {
            final String filename = files[i].getName();
            final String clazz = pkg + "." 
                  + filename.substring(0, 
                          filename.length() - ".class".length());
            try
            {
               visit(clazz);
            }
            catch (ClassNotFoundException ignore)
            {
               // classes that are not on the classpath are ignored.
            }
         }
      }
   }   
   
   
   public static final class ClassDependency
   {
      private final String mClazz;
      private final String mDependsClazz;

      ClassDependency (String c, String dc)
      {
         mClazz = c;
         mDependsClazz = dc;
      }
      
      public String toString ()
      {
         return mClazz + " -> " + mDependsClazz;
      }

      public String toDotString ()
      {
         return "\"" + mClazz + "\"" +  " -> " + "\"" + mDependsClazz + "\"";
      }

      /** {@inheritDoc} */
      public boolean equals (Object obj)
      {
         if (! (obj instanceof JDepend.ClassDependency))
         {
            return false;
         }
         final JDepend.ClassDependency o = (JDepend.ClassDependency) obj;
         
         return o.mClazz.equals(mClazz) 
                 && o.mDependsClazz.equals(mDependsClazz);
      }
      
      /** {@inheritDoc} */
      public int hashCode ()
      {
         return mClazz.hashCode() + mDependsClazz.hashCode();
      }
   }

   public static final class PackageDependency
   {
      private final String mPackage;
      private final String mDependsPackage;

      PackageDependency (String c, String dc)
      {
         mPackage = c.substring(0, c.lastIndexOf('.'));
         mDependsPackage = dc.substring(0, dc.lastIndexOf('.'));
      }
      
      public String toString ()
      {
         return mPackage + " -> " + mDependsPackage;
      }

      public String toDotString ()
      {
         return "\"" + mPackage + "\"" +  " -> " + "\"" 
                 + mDependsPackage + "\"";
      }

      /** {@inheritDoc} */
      public boolean equals (Object obj)
      {
         if (! (obj instanceof JDepend.PackageDependency))
         {
            return false;
         }
         final JDepend.PackageDependency o = (JDepend.PackageDependency) obj;
         
         return o.mPackage.equals(mPackage) 
             && o.mDependsPackage.equals(mDependsPackage);
      }
      
      /** {@inheritDoc} */
      public int hashCode ()
      {
         return mPackage.hashCode() + mDependsPackage.hashCode();
      }
   }
}
