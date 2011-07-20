package org.jcoderz.commons.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jcoderz.commons.util.XsltBase;

/**
 * The simple type task generates Java classes from an XML file.
 * 
 * @goal simpletypes
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * 
 * @author mrumpf
 * 
 */
public class SimpleTypesMojo extends AbstractMojo {
	/**
	 * <i>Maven Internal</i>: Project to interact with.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The XSL stylesheet file.
	 * 
	 * @parameter default-value= "generate-simple-types.xsl"
	 */
	private String xslFile = null;

	/**
	 * An include pattern for the simple type definition files.
	 * 
	 * @parameter default-value=".type.xml"
	 */
	private String includePattern;

	/**
	 * The source folder with the simple type XML definitions.
	 * 
	 * @parameter
	 */
	private File sourceDirectory;

	/**
	 * The destination folder where the generated code will be written to.
	 * 
	 * @parameter
	 */
	private File destinationDirectory;

	// TODO:
	// http://code.hammerpig.com/search-for-files-in-directory-using-wildcards-in-java.html
	public void execute() throws MojoExecutionException {
		if (!destinationDirectory.exists())
		{
			destinationDirectory.mkdirs();
		}
		List<File> files = findFiles(sourceDirectory, includePattern);
		for (File file : files) {
			String log = file.getName() + ".log";
			XsltBase.transform(file, xslFile, destinationDirectory, new File(project
					.getBuild().getDirectory(), log), false);
		}
	}

	private List<File> findFiles(File dir, String pattern) {
		List<File> files = new ArrayList<File>();
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((pattern))) {
				files.add(file);
			}
		}
		return files;
	}
}
