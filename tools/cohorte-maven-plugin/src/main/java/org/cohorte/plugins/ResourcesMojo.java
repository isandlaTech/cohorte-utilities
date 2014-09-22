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
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Goal which prepare the test and packaging resources.
 * @author Bassem Debbabi
 *
 * @goal resources
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public class ResourcesMojo
    extends AbstractMojo
{
    
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
    * Test options
	*
    * @parameter alias="test"
    */
	private Map testInstructions = new LinkedHashMap();

	/**
    * Package options
	*
    * @parameter alias="package"
    */
	private Map packageInstructions = new LinkedHashMap();

    private Object rsLibDirectory;
   
    private Object pkgName;
    private Object pkgTopLevel;
    private Object pkgVersion;
    private Object pkgLicense;
    private Object pkgDescription;
    private Object pkgLongDescription;
    private Object pkgAuthor;
    private Object pkgAuthorEmail;
    private Object pkgUrl;
    private Object pkgDownloadUrl;
    private Object pkgPackages;
    private Object pkgClassifiers;
    private Object pkgTestSuite;

	private Object skipTest;

    /**
	* @parameter 
	*   expression="${cohorte.verbose}"
	*   default-value="true"
	*/
	private Object verbose;

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

    public void execute()
            throws MojoExecutionException
    {
        prepareParameters();

        if (this.language.equalsIgnoreCase("python")) {

            prepareLibFolder();

            prepareTargetFolder();

            try {
                File setup1 = generateSetup(baseDir+"/target/"+pkgTopLevel+"/setup.py");
                if (skipTest.toString().equalsIgnoreCase("false")) {
                    File setup2 = generateSetup(baseDir+"/target/"+pkgTopLevel+"_tests/setup.py");
                }
            } catch ( FileNotFoundException e1 ) {
                getLog().warn(e1.getMessage());
            } catch ( UnsupportedEncodingException e2) {
                getLog().warn(e2.getMessage());
            }
        }
    }

    private void prepareLibFolder() {
        try {
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version("2.8")
                    ),
                    goal("unpack-dependencies"),
                    configuration(
                            element(name("outputDirectory"), baseDir+rsLibDirectory.toString()),
                            element(name("includes"), "**/*"),
                            element(name("overWriteIfNewer"),"true"),
                            element(name("overWriteReleases"),"true"),
                            element(name("overWriteSnapshots"),"true"),
                            element(name("excludeTransitive"), "false")
                    ), executionEnvironment(
                            m_project,
                            m_session,
                            m_pluginManager
                    )
            );
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }
    }

    private void prepareTargetFolder() {
        /* copy python source folder to target/bundle_name folder */
        copy(this.baseDir+"/src/main/python", this.baseDir+"/target/"+pkgTopLevel,
                "Python source folder", "target folder");
            /* copy python resources folder to target/bundle_name folder */
        copy(this.baseDir+"/src/main/resources", this.baseDir+"/target/"+pkgTopLevel,
                "Python resources folder", "target folder");
        if (skipTest.toString().equalsIgnoreCase("false")) {
	            /* copy python source folder to target/bundle_name_test folder */
            copy(this.baseDir+"/src/main/python", this.baseDir+"/target/"+pkgTopLevel+"_tests",
                    "Python source folder", "target test folder");
	            /* copy python resources folder to target/bundle_name_test folder */
            copy(this.baseDir+"/src/main/resources", this.baseDir+"/target/"+pkgTopLevel+"_tests",
                    "Python resources folder", "target test folder");
	            /* copy python test folder to target/bundle_name_test folder */
            copy(this.baseDir+"/src/test/python", this.baseDir+"/target/"+pkgTopLevel+"_tests",
                    "Python test folder", "target test folder");
	            /* copy python test resources folder to target/bundle_name_test folder */
            copy(this.baseDir+"/src/test/resources", this.baseDir+"/target/"+pkgTopLevel+"_tests",
                    "Python test resources folder", "target test folder");
	            /* copy python lib folder to target/bundle_name_test folder */
            copy(this.baseDir+"/src/lib/python", this.baseDir+"/target/"+pkgTopLevel+"_tests",
                    "Python lib folder", "target test folder");
        }
    }

    private void prepareParameters() {

        rsLibDirectory = resourcesInstructions.get("lib_directory");
        if (rsLibDirectory == null) {
            rsLibDirectory = "/src/lib/python";
        }

        pkgName = packageInstructions.get("name");
        if (pkgName == null) {
        	pkgName = "'"+bundleName+"'"; 
        } else { pkgName = "'"+pkgName+"'"; }
        pkgVersion = packageInstructions.get("version");
        if (pkgVersion == null) {
        	pkgVersion = "'"+bundleVersion+"'"; 
        } else { pkgVersion = "'"+pkgVersion+"'"; }
        pkgTopLevel = packageInstructions.get("top_level");
        if (pkgTopLevel == null) {
        	pkgTopLevel = bundleName; 
        }
        pkgLicense = packageInstructions.get("license");
        if (pkgLicense == null) {
        	pkgLicense = "None"; 
        } else { pkgLicense = "'"+pkgLicense+"'"; }
        pkgDescription = packageInstructions.get("description");
        if (pkgDescription == null) {
        	pkgDescription = "None"; 
        } else { pkgDescription = "'"+pkgDescription+"'"; }
        pkgLongDescription = packageInstructions.get("long_description");
        if (pkgLongDescription == null) {
        	pkgLongDescription = "None"; 
        } else { pkgLongDescription = "'"+pkgLongDescription+"'"; }
        pkgAuthor = packageInstructions.get("author");
        if (pkgAuthor == null) {
        	pkgAuthor = "None"; 
        } else { pkgAuthor = "'"+pkgAuthor+"'"; }
        pkgAuthorEmail = packageInstructions.get("author_email");
        if (pkgAuthorEmail == null) {
        	pkgAuthorEmail = "None"; 
        } else { pkgAuthorEmail = "'"+pkgAuthorEmail+"'"; }
        pkgUrl = packageInstructions.get("url");
        if (pkgUrl == null) {
        	pkgUrl = "None"; 
        } else { pkgUrl = "'"+pkgUrl+"'"; }
        pkgDownloadUrl = packageInstructions.get("download_url");
        if (pkgDownloadUrl == null) {
        	pkgDownloadUrl = "None"; 
        } else { pkgDownloadUrl = "'"+pkgDownloadUrl+"'"; }
        pkgPackages = packageInstructions.get("packages");
        if (pkgPackages == null) {
        	pkgPackages = "list(find_packages("+pkgTopLevel+".__path__, "+pkgTopLevel+".__name__))"; 
        } else { pkgPackages = "["+pkgPackages+"]";}
        pkgClassifiers = packageInstructions.get("classifiers");
        if (pkgClassifiers == null) {
        	pkgClassifiers = "[]"; 
        } else { pkgClassifiers = "["+pkgClassifiers+"]";}
        
        /* skip tests? */
        skipTest = testInstructions.get("skip_test");
        if (skipTest == null) {
            skipTest = "true";    // default value: skip tests
        }
        /* test folder */
        pkgTestSuite = testInstructions.get("folder");
        if (pkgTestSuite == null) {
        	pkgTestSuite = "'tests'";    // default value: tests folder is by default called tests.
        } else { pkgTestSuite = "'"+pkgTestSuite+"'"; }

        /*
        try {
            m_pluginManager = (BuildPluginManager) m_session.lookup("org.apache.maven.plugin.BuildPluginManager");
        } catch (ComponentLookupException e) {
            e.printStackTrace();
        }*/
    }

    private void copy(String src, String dest, String what, String where)
    { 
        //getLog().info("BaseDir=" + this.baseDir + " -- pkgTopLevel="+pkgTopLevel);
        File srcFolder = new File(src);
        File destFolder = new File(dest);
        /* make sure python source folder exists */
        if (!srcFolder.exists()) {
            getLog().warn(what + " '"+src+"'' does not exist!");
            return;
        }
        Helpers.createFolder(this.baseDir+"/target");
        try {
            Helpers.copyFolder(srcFolder, destFolder);
        } catch(IOException e) {
            getLog().error("Cannot copy "+what+" folder to the " + where);
            //e.printStackTrace();
            return;
        }
        if (verbose.toString().equalsIgnoreCase("true")) {
        	getLog().info(what+" is copied to the " + where);
    	}
    }

    private File generateSetup(String path) throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writer = new PrintWriter(path, "UTF-8");
    	StringBuilder content = new StringBuilder();
    	content.append(
		 	         "#!/usr/bin/env python"
			+ "\n" + "# -- Content-Encoding: UTF-8 --" 
			+ "\n" + "# Generated by cohorte-maven-plugin. " 
			+ "\n" + "# Do not edit this file manually. Update the information given in the pom.xml file" 
			+ "\n" + "" 
			+ "\n" + "__version__ = "+pkgVersion 
			+ "\n" + "" 
			+ "\n" + "__docformat__ = 'restructuredtext en'" 
			+ "\n" + "" 
			+ "\n" + "" 
			+ "\n" + "try:" 
			+ "\n" + "    from setuptools import setup" 
			+ "\n" + "except ImportError:" 
			+ "\n" + "    from distutils.core import setup" 
			+ "\n" + "from pkgutil import walk_packages" 
			+ "\n" + ""
			);
		if (packageInstructions.get("packages") == null) {
			content.append(	
			  "\n" + "import " + pkgTopLevel			
			+ "\n" + "" 			
			+ "\n" + "def find_packages(path, prefix=\"\"):" 
			+ "\n" + "    yield prefix" 			
			+ "\n" + "    prefix = prefix + \".\"" 
			+ "\n" + "    for _, name, ispkg in walk_packages(path, prefix):" 			
			+ "\n" + "        if ispkg:" 
			+ "\n" + "            yield name" 	
			);
		}	
		content.append(	
			  "\n" + "" 
			+ "\n" + "setup(" 
			+ "\n" + "    name="+pkgName+"," 
			+ "\n" + "    version=__version__," 
			+ "\n" + "    license="+pkgLicense+"," 
			+ "\n" + "    description="+pkgDescription+"," 
			+ "\n" + "    long_description="+pkgLongDescription+"," 
			+ "\n" + "    author="+pkgAuthor+","
			+ "\n" + "    author_email="+pkgAuthorEmail+"," 
			+ "\n" + "    url="+pkgUrl+"," 
			+ "\n" + "    download_url="+pkgDownloadUrl+"," 
			+ "\n" + "    packages="+pkgPackages+","
			);
		if (skipTest.toString().equalsIgnoreCase("false")) {
			content.append(
			  "\n" + "    test_suite="+pkgTestSuite+"," 
			);
		}
		content.append(
			  "\n" + "    classifiers="+pkgClassifiers
			+ "\n" + ")"
    		);
		writer.println(content.toString());
		writer.close();
		return new File(path);
    }

    public void setTest(Map instructions) { this.testInstructions = instructions; }
    public void setPackage(Map instructions) { this.packageInstructions = instructions; }

}
