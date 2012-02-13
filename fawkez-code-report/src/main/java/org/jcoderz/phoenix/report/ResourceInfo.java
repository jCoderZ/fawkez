/*
 * $Id: ResourceInfo.java 1497 2009-06-07 17:30:27Z amandel $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.HashCode;
import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.commons.util.ObjectUtil;

/**
 * This class holds resource information about a Java class.
 *
 * @author Michael Griffel
 */
public final class ResourceInfo
{
   /** holds a map from resource name to ResourceInfo */
    private static final Map<String, ResourceInfo> RESOURCES
        = Collections.synchronizedMap(new HashMap<String, ResourceInfo>());
    /** holds a map from package / classname to ResourceInfo */
    private static final Map<String, ResourceInfo> RESOURCES_BY_CLASS
        = Collections.synchronizedMap(new HashMap<String, ResourceInfo>());

    private static final String CLASSNAME = ResourceInfo.class.getName();
    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private final String mResourceName;
    private final String mPackage;
    private final String mSourcDir;
    private final String mClassname;

    /** Lazy initialized number of source lines value. */
    private int mLinesOfCode = -1;
    /** Lazy initialized hash code value. */
    private int mHashCode = -1;

    private ResourceInfo (String name, String pkg, String sourceDir)
    {
        if (logger.isLoggable(Level.FINER))
        {
            logger.entering(CLASSNAME, "<init>",
                new Object[]{name, pkg, sourceDir});
        }
        Assert.notNull(name, "name");
        Assert.notNull(sourceDir, "sourceDir");
        mResourceName = checkName(name).intern();
        mPackage = ObjectUtil.toStringOrEmpty(pkg);
        mSourcDir = checkName(sourceDir).intern();
        mClassname = determineClassName(name).intern();
        if (logger.isLoggable(Level.FINER))
        {
            logger.exiting(CLASSNAME, "<init>", this);
        }
    }

    /**
     * Registers the a new resource with the given parameters.
     * @param name the name of the resource.
     * @param pkg the Java package of the resource.
     * @param sourceDir the source directory of the resource.
     * @return the registered resource info.
     */
    public static ResourceInfo register (String name, String pkg,
            String sourceDir)
    {
        final String resourceName = checkName(name);
        final ResourceInfo result;
        if (!RESOURCES.containsKey(resourceName))
        {
            result = new ResourceInfo(resourceName, pkg, sourceDir);
            add(resourceName, result);
        }
        else
        {
            result = RESOURCES.get(resourceName);
            final ResourceInfo newInfo
                = new ResourceInfo(resourceName, pkg, sourceDir);
            // sanity check
            Assert.assertEquals("Ups, the ResourceInfo w/ the name "
                + resourceName
                + " is already registered with different parameters!",
                result, newInfo);
        }
        return result;
    }

    /**
     * Locates the resource with the given name.
     *
     * @param name resource name.
     * @return the resource for the given name or <tt>null</tt> if not found.
     */
    public static ResourceInfo lookup (String name)
    {
        String lookupName = name;
        ResourceInfo result = RESOURCES.get(lookupName);
        if (result == null)
        {
            lookupName = checkName(name);
            result = RESOURCES.get(lookupName);
        }
        if (result == null)
        {
            logger.finer("### ResourceInfo not found for '"
                + lookupName + "'");
        }
        return result;
    }

    /**
     * Searches the resource with the given class name and package.
     *
     * @param packageName resource package name.
     * @param className resource class name.
     * @return the resource for the given name or <tt>null</tt> if not found.
     */
    public static ResourceInfo lookup (String packageName, String className)
    {
        final String key = combineName(packageName, className);
        final ResourceInfo result = RESOURCES_BY_CLASS.get(key);
        if (result == null)
        {
            logger.finer("### ResourceInfo not found for '"
                + key + "'");
        }
        return result;
    }

    static String dump ()
    {
        return RESOURCES.toString();
    }

    /**
     * Returns the number of lines for the given file <tt>filename</tt>.
     * @param fileName the name of the file.
     * @return the number of lines.
     * @throws IOException in case of an I/O problem.
     * @throws FileNotFoundException in case the named file does
     *      not exists or is a directory.
     */
    public static int countLinesOfCode (String fileName)
            throws IOException, FileNotFoundException
    {
        int counter = 0;
        final BufferedReader reader
                = new BufferedReader(new FileReader(fileName));
        try
        {
            while (reader.readLine() != null)
            {
                ++counter;
            }
        }
        finally
        {
            IoUtil.close(reader);
        }
        return counter;
    }

    /** {@inheritDoc} */
    public boolean equals (Object obj)
    {
        boolean result = false;
        if (this == obj)
        {
            result = true;
        }
        else if (obj instanceof ResourceInfo)
        {
            final ResourceInfo o = (ResourceInfo) obj;
            result = ObjectUtil.equals(mResourceName, o.getResourceName())
                && ObjectUtil.equals(mPackage, o.getPackage())
                && ObjectUtil.equals(mSourcDir, o.getSourcDir());
        }
        else
        {
            result = false;
        }
        return result;
    }

    /** {@inheritDoc} */
    public int hashCode ()
    {
        if (mHashCode == -1)
        {
            final HashCode hashCode = new HashCode();
            hashCode.hash(mResourceName);
            hashCode.hash(mPackage);
            hashCode.hash(mSourcDir);
            mHashCode  = hashCode.hashCode();
        }
        return mHashCode;
    }

    /**
     * Returns the linesOfCode.
     *
     * @return the linesOfCode.
     */
    public int getLinesOfCode ()
    {
        if (mLinesOfCode == -1)
        {
            try
            {
                mLinesOfCode = countLinesOfCode(mResourceName);
            }
            catch (IOException e)
            {
                mLinesOfCode = 0;
                logger.log(Level.FINER,
                        "Cannot read the resource with the name "
                                + mResourceName, e);
            }
        }
        return mLinesOfCode;
    }

    /**
     * Returns the package.
     *
     * @return the package.
     */
    public String getPackage ()
    {
        return mPackage;
    }

    /**
     * Returns the resourceName.
     *
     * @return the resourceName.
     */
    public String getResourceName ()
    {
        return mResourceName;
    }

    /**
     * Returns the sourcDir.
     *
     * @return the sourcDir.
     */
    public String getSourcDir ()
    {
        return mSourcDir;
    }

    /** {@inheritDoc} */
    public String toString ()
    {
        return "[ResourceInfo: name=" + mResourceName + ", pkg=" + mPackage
                + ", sourceDir=" + mSourcDir + ", mClassname=" + mClassname
                + "]";
    }

    /**
     * Returns the class name.
     * @return the class name.
     */
    public String getClassname ()
    {
        return mClassname;
    }


    private String determineClassName (String name)
    {
        String result = "";

        final String magic = ".java";
        if (name.endsWith(magic))
        {
            final int lastSlashPos = name.lastIndexOf(File.separator);
            if (lastSlashPos != -1)
            {
                result = name.substring(lastSlashPos + File.separator.length());
                result = result.substring(0, result.indexOf(magic));
            }
        }
        return result;
    }

    private static void add (String name, ResourceInfo info)
    {
        synchronized (RESOURCES)
        {
            RESOURCES.put(name, info);
            RESOURCES_BY_CLASS.put(
                combineName(info.getPackage(), info.getClassname()), info);
        }
    }

    private static String combineName (String packageName, String className)
    {
        return ObjectUtil.toStringOrEmpty(packageName) + "--" 
            + ObjectUtil.toStringOrEmpty(className);
    }

    private static String checkName (String lookupName)
    {
        String name = ObjectUtil.toStringOrEmpty(lookupName);
        if (!RESOURCES.containsKey(name))
        {
            try
            {
                name = new File(name).getCanonicalPath();
            }
            catch (IOException ex)
            {
                throw new RuntimeException(
                    "Uuppss, this was not expected in 'getCanonicalPath' "
                        + " for '" + name + "'.",
                    ex);
            }
        }
        return name;
    }

}
