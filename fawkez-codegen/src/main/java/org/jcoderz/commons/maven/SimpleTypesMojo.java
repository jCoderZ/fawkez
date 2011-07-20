package org.jcoderz.commons.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal simpletypes
 *
 * @author mrumpf
 *
 */
public class SimpleTypesMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.sourceDirectory}"
     */
    private File destDirectory;

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


    public void execute()
        throws MojoExecutionException
    {
        File f = destDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}