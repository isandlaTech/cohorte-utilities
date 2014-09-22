package org.cohorte.plugins;

/*
 * Copyright 2014 The isandlaTech (http://isandlatech.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import static java.nio.file.StandardCopyOption.*;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Goal which construct the python package.
 * @author Bassem Debbabi
 *
 * @goal package
 * @requiresProject true
 */
public class PackageMojo
    extends AbstractMojo
{
    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Used for attaching new artifacts.
     * @component
     * @required
     */
    private MavenProjectHelper helper;

    /**
     * Cohorte's Bundle principal programming language.
     * @parameter expression="${cohorte.bundle.language}" default-value="python"
     * @required
     */
    private String language;

    /**
    * Base Directory
    * @parameter
    *   expression="${basedir}"
    **/
    private String baseDir;

    /** 
    * Bundle Name
    * @parameter
    *   expression="${project.artifactId}"
    **/
    private String bundleName;

    /** 
    * Bundle Version
    * @parameter
    *   expression="${project.version}"
    **/
    private String bundleVersion;

    /**
     * Resources options
     *
     * @parameter alias="resources"
     */
    private Map resourcesInstructions = new LinkedHashMap();

	/**
    * Package options
	*
    * @parameter alias="package"
    */
	private Map packageInstructions = new LinkedHashMap();

    /**
     * The Zip archiver.
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     */
    private ZipArchiver zipArchiver;

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject m_project;

    /**
     * The Maven project.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession m_session;

    /**
     * The Maven BuildPluginManager component.
     *
     * @component
     * @required
     */
    private BuildPluginManager m_pluginManager;

	private Object pkgTopLevel;
    private Object pkgName;
    private Object pkgVersion;
    private Object pkgSkipPackageSetup;
    private Object pkgSkipPackageSource;

    private Object rsLibDirectory;

	/**
	* @parameter 
	*   expression="${cohorte.verbose}"
	*   default-value="true"
	*/
	private Object verbose;

	private void prepareParameters() {

        rsLibDirectory = resourcesInstructions.get("lib_directory");
        if (rsLibDirectory == null) {
            rsLibDirectory = "/src/lib/python";
        }

        /* verbose mode */
        Object tmp = packageInstructions.get("verbose");
        if (tmp != null) {
        	verbose = tmp;
        }
        pkgTopLevel = packageInstructions.get("top_level");
        if (pkgTopLevel == null) {
        	pkgTopLevel = bundleName; 
        }
        pkgName = packageInstructions.get("name");
        if (pkgName == null) {
            pkgName = bundleName;
        }
        pkgVersion = packageInstructions.get("version");
        if (pkgVersion == null) {
            pkgVersion = bundleVersion;
        }
        pkgSkipPackageSetup = packageInstructions.get("skip_package_setup");
        if (pkgSkipPackageSetup == null) {
            pkgSkipPackageSetup = "true";
        }
        pkgSkipPackageSource = packageInstructions.get("skip_package_source");
        if (pkgSkipPackageSource == null) {
            pkgSkipPackageSource = "false";
        }
    }

    public void execute()
        throws MojoExecutionException
    {
        if (this.language.equalsIgnoreCase("python")) {
            
            prepareParameters();

            // MDEP-187
            /*
            try {
                executeMojo(
                        plugin(
                                groupId("org.cohorte.plugins"),
                                artifactId("cohorte-maven-plugin"),
                                version("1.0.0")
                        ),
                        goal("resources"),
                        configuration(
                                element(name("outputDirectory"), baseDir+rsLibDirectory.toString()),
                                element(name("overWriteIfNewer"),"true"),
                                element(name("overWriteReleases"),"false"),
                                element(name("overWriteSnapshots"),"false"),
                                element(name("excludeTransitive"), "false")
                        ), executionEnvironment(
                                m_project,
                                m_session,
                                m_pluginManager
                        )
                );
            } catch (MojoExecutionException e) {
                e.printStackTrace();
            }*/

    		try {
                /* create package using setuptools */
                if (pkgSkipPackageSetup.toString().equalsIgnoreCase("false")) {
                    runSetupToolsPackaging();
                }
    		} catch ( IOException e1) { 
    			getLog().error("Can not run packaging using setuptools! IOException");
    		} catch ( InterruptedException e2) {
    			getLog().error("Can not run packaging using setuptools! InterruptedException");
    		}
            File sourceFile = null;
            try {
                /* create source zip (to upload to maven repository, using plexus */
                if (pkgSkipPackageSource.toString().equalsIgnoreCase("false")) {
                    sourceFile = createSourceArchive();
                }
            } catch( IOException e1 ) {
                getLog().error("Can not create source archive file!");
            }

            /* attach only the source zip to be uploaded to the repository */
            if (pkgSkipPackageSource.toString().equalsIgnoreCase("false")) {
                attachArtifact(sourceFile, "zip");
            } else {
                attachArtifact(new File(baseDir+"/pom.xml"), "pom");
            }
        }
    }

    private File createSourceArchive() throws IOException {
        getLog().info("Start creating maven source file...");
        File f1 = new File(baseDir+"/src/main/python");
        File f2 = new File(baseDir+"/target/dist/"+pkgName+"-"+pkgVersion+"-source.zip");
        zipArchiver.addDirectory(f1);
        zipArchiver.setDestFile(f2);
        zipArchiver.createArchive();
        return f2;
    }

    private void runSetupToolsPackaging() throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("python", "setup.py", "sdist", "--formats=zip");
    	pb.directory(new File(baseDir+"/target/"+pkgTopLevel));
    	if (verbose.toString().equalsIgnoreCase("true")) {
	    	pb.redirectErrorStream(true);
	    	pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    	}    	
    	getLog().info("Start packaging...");
    	final Process process = pb.start();
    	process.waitFor();
		if (process.exitValue() == 0) {
            File f1 = new File(baseDir+"/target/"+pkgTopLevel+"/dist");
            File f2 = new File(baseDir+"/target/dist");
            Files.move(f1.toPath(), f2.toPath(), REPLACE_EXISTING);
			getLog().info("Packaging terminated! (result on /target/dist)");
		} else {
			getLog().info("Packaging terminated with errors!");
		}
    }

    private void attachArtifact(File file, String type) {
        helper.attachArtifact(project, type, null, file);
    }

    public void setPackage(Map instructions) { this.packageInstructions = instructions; }

}
