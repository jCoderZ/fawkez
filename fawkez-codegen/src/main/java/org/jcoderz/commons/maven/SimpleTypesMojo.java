package org.jcoderz.commons.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
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
	 * The destination directory.
	 * 
	 * @parameter default-value="${project.build.directory}/generated-fawkez"
	 */
	private File destDirectory;

	/**
	 * The destination directory.
	 * 
	 * @parameter default-value="src/main/fawkez"
	 */
	private File sourceDirectory;

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
	 * A set of file patterns to exclude from the zip.
	 * 
	 * @parameter default-value=""
	 */
	private String excludePattern;

	/**
	 * Force output of target files even if they already exist.
	 */
	private boolean force = false;

	// TODO:
	// http://code.hammerpig.com/search-for-files-in-directory-using-wildcards-in-java.html
	public void execute() throws MojoExecutionException {
		List<File> files = findFiles(sourceDirectory, includePattern);
		for (File file : files) {
			XsltBase.transform(file, xslFile, destDirectory, new File(project
					.getBuild().getDirectory(), "simple-types.log"), false);
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
