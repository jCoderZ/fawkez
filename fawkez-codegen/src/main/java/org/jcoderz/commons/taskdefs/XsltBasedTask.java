/*
 * $Id: XsltBasedTask.java 1633 2010-05-26 18:16:57Z amandel $
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.transform.Transformer;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jcoderz.commons.util.XsltBase;


/**
 * This class implements common functionality for XSLT based Ant tasks.
 *
 * @author Michael Griffel
 */
public abstract class XsltBasedTask
    extends Task
{
    /** The fawkeZ VERSION file. */
    private static final String FAWKEZ_VERSION_FILE =
        "/org/jcoderz/commons/VERSION";

    /** The destination directory. */
    private File mDestDir = null;

    /** The XSL stylesheet file. */
    private String mXslFile = null;

    /** The Input XML document (log message info file) to be used. */
    private File mInFile = null;

    /** The Output file. */
    private File mOutFile = null;

    /** force output of target files even if they already exist. */
    private boolean mForce = false;

    /** terminate ant build on error. */
    private boolean mFailOnError = false;

    /** Log level. */
    private int mLogLevel = Project.MSG_INFO;

    private boolean mResolveExternalEntities = true;

    /**
     * AntClassLoader for the nested &lt;classpath&gt; - if set.
     * <p>
     * We keep this here in order to reset the context classloader in
     * execute. We can't use liaison.getClass().getClassLoader() since
     * the actual liaison class may have been loaded by a loader higher
     * up (system classloader, for example).
     * </p>
     *
     * @since Ant 1.6.2
     */
    private AntClassLoader mClassLoader = null;

    /**
     * Set the destination directory into which the XSL result files
     * should be copied to. This parameter is required.
     *
     * @param dir the name of the destination directory.
     */
    public void setDestdir (File dir)
    {
        mDestDir = dir;
    }

    /**
     * Sets the XSL file that is used to generate the log message info
     * classes.
     *
     * @param s the XSL file to use.
     */
    public void setXsl (String s)
    {
        mXslFile = s;
    }

    /**
     * Sets the XML input file that contains the log message info
     * document.
     *
     * @param f the XML input file (log message info).
     */
    public void setIn (File f)
    {
        mInFile = f;
    }

    /**
     * Sets the output file.
     *
     * @param f The output file.
     */
    public void setOut (File f)
    {
        mOutFile = f;
    }

    /**
     * Sets the force output of target files flag to the given value.
     *
     * @param b Whether we should force the generation of output files.
     */
    public void setForce (boolean b)
    {
        mForce = b;
    }

    /**
     * Set whether we should fail on an error.
     *
     * @param b Whether we should fail on an error.
     */
    public void setFailonerror (boolean b)
    {
        mFailOnError = b;
    }

    /**
     * Sets the log level.
     *
     * @param level the new log level
     */
    public void setLogLevel (int level)
    {
        mLogLevel = level;
    }

    /**
     * Execute this task.
     *
     * @throws BuildException An building exception occurred.
     */
    public void execute ()
        throws BuildException
    {
        try
        {
            checkAttributes();
            if (mForce || mInFile.lastModified() > mOutFile.lastModified())
            {
                if (mDestDir != null)
                {
                    log("Generating files to directory " + mDestDir,
                        Project.MSG_VERBOSE);
                }
                log("Processing " + mInFile + " to " + mOutFile
                    + " using stylesheet " + mXslFile, mLogLevel);
                transform();
                postExecute();
            }
        }
        catch (BuildException e)
        {
            if (mFailOnError)
            {
                throw e;
            }
            log(e.getMessage(), Project.MSG_ERR);
        }
    }

    /**
     * Returns the VERSION file properties.
     *
     * @return the VERSION file properties.
     * @throws IOException if the VERSION file cannot be found or read.
     */
    private Properties getFawkezVersionProperties ()
        throws IOException
    {
        final Properties props = new Properties();
        final InputStream in 
            = XsltBasedTask.class.getResourceAsStream(FAWKEZ_VERSION_FILE);
        try
        {
            props.load(in);
        }
        finally
        {
            if (in != null)
            {
               try
               {
                  in.close();
               }
               catch (IOException x)
               {
            	   log("Error while closing " + InputStream.class.getName() + ": "
            	            + in.getClass().getName() + ".close(): " + x, Project.MSG_ERR);
               }
            }
        }
        return props;
    }

    /**
     * @return the fawkez version used for build.
     */
    public String getFawkezVersionAsString ()
    {
        final StringBuffer version = new StringBuffer();
        try
        {
            final Properties fawkezProps = getFawkezVersionProperties();
            version.append("fawkeZ ");
            version.append(fawkezProps.getProperty("version"));
            version.append(", [");
            version.append(fawkezProps.getProperty("cvs_name"));
            version.append(']');
        }
        catch (Exception x)
        {
            // sorry, we cannot read fawkeZ VERSION file
            version.append("unknown");
        }
        return version.toString();
    }

    /**
     * If set to <tt>false</tt>, external entities will not be
     * resolved.
     *
     * @param b new value.
     */
    public void resolveExternalEntities (boolean b)
    {
        mResolveExternalEntities = b;
    }

    static void checkXercesVersion (Task task)
    {
        final String xercesVersion = org.apache.xerces.impl.Version
            .getVersion();
        if (xercesVersion != null && xercesVersion.indexOf("2.6.2") >= 0)
        {
            task.log("Found " + xercesVersion + " on classpath.",
                Project.MSG_WARN);
            task.log("This Version only supports the outdated 2003 "
                + "namespace for XInclude ", Project.MSG_WARN);
            task.log("please put a newer version of xerces on your classpath"
                + "or use", Project.MSG_WARN);
            task.log("at least ANT 1.7.0.", Project.MSG_WARN);
        }
    }

    /**
     * Returns the build-in default stylesheet file name that should be
     * used by XSL transformer.
     * <p>
     * The stylesheet must be stored in the
     * <tt>/org/jcoderz/commons/taskdefs</tt> directory.
     *
     * @return the default stylesheet file name.
     */
    abstract String getDefaultStyleSheet ();

    /**
     * This method can be overwritten by subclasses to set additional
     * transformer parameters.
     *
     * @param transformer the XSL transformer.
     */
    void setAdditionalTransformerParameters (Transformer transformer)
    {
        // NOP
    }

    File getInFile ()
    {
        return mInFile;
    }

    File getOutFile ()
    {
        return mOutFile;
    }

    File getDestDir ()
    {
        return mDestDir;
    }

    boolean getFailOnError ()
    {
        return mFailOnError;
    }

    /**
     * Checks the attributes provided by this class.
     *
     * @throws BuildException
     */
    void checkAttributes ()
        throws BuildException
    {
        checkAttributeInFile();
        checkAttributeOutFile();
        checkAttributeDestDir();
        checkAttributeXslFile();
        checkXercesVersion(this);
    }

    void checkAttributeXslFile ()
    {
        if (mXslFile == null || !new File(mXslFile).exists())
        {
            mXslFile = getDefaultStyleSheet();
        }
    }

    void checkAttributeDestDir ()
    {
        if (mDestDir == null)
        {
            throw new BuildException("Missing mandatory attribute 'outdir'.",
                getLocation());
        }
        AntTaskUtil.ensureDirectory(mDestDir);
    }

    void checkAttributeOutFile ()
    {
        if (mOutFile == null)
        {
            throw new BuildException("Missing mandatory attribute 'out'.",
                getLocation());
        }
        AntTaskUtil.ensureDirectoryForFile(mOutFile);
    }

    void checkAttributeInFile ()
    {
        if (mInFile == null)
        {
            throw new BuildException("Missing mandatory attribute 'in'.",
                getLocation());
        }
        if (!mInFile.exists())
        {
            throw new BuildException("Input file '" + mInFile + "' not found.",
                getLocation());
        }
    }

    /**
     * This method is the last callback in the execute method. Can be
     * overwritten by subclasses.
     */
    void postExecute ()
    {
        // NOP
    }

    /**
     * Execute the XSL transformation.
     *
     * @throws BuildException if an error during transformation occurs.
     */
    void transform ()
        throws BuildException
    {
        try
        {
        	XsltBase.transform(mInFile, mXslFile, mDestDir, mOutFile, mResolveExternalEntities);
        }
        finally
        {
            if (mClassLoader != null)
            {
                mClassLoader.resetThreadContextLoader();
                mClassLoader.cleanup();
                mClassLoader = null;
            }
        }
    }
}
