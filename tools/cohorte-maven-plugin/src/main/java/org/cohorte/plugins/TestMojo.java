package org.cohorte.plugins;

/*
 * Copyright 2014 isandlaTech. (http://isandlatech.com)
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.*;
/**
 * <p>Goal which runs unit test on python (and java) code.</p>
 * <p>This Mojo requires the following configuration:<br/>
 * <ul><li>test
 *         <ul><li><b>skipTest</b>: true or false. if true, the test is ignored.</li>
 *             <li><b>framework</b>: only setuptools is supported at this version.</li>
 *             <li><b>folder</b>: the test folder. By default is set to 'tests'. You
 *                                should only put the folder name, not the fullpath 
 *                                (src/test/python/tests)</li>
 *         </ul></li></ul>
 *
 *
 * @author Bassem Debbabi
 *
 * @goal test
 * @requiresProject true
 */
public class TestMojo
    extends AbstractMojo
{
    
    /**
     * Cohorte's Bundle principal programming language.
     * @parameter expression="${cohorte.bundle.language}" default-value="python"
     * @required
     */
    private String language;

    /**
    * Test options
    * - skipTest
    * - framework
    * - folder
    * @parameter alias="test"
    */
	private Map testInstructions = new LinkedHashMap();

	/**
    * Package options
	*
    * @parameter alias="package"
    */
	private Map packageInstructions = new LinkedHashMap();

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

 	private Object pkgTopLevel;
    private Object skipTest;
    private Object framework;
    private Object folder;

    /**
	* @parameter 
	*   expression="${cohorte.verbose}"
	*   default-value="true"
	*/
	private Object verbose;

    public void execute()
        throws MojoExecutionException
    {
		if ( baseDir == null )
		{
			throw new IllegalStateException( "basedir is null. Should not be possible." );
		}
    	/* prepare test mojo parameters */
    	prepareParameters();
    	/* if skipTest is positioned => do nothing */
    	if (skipTest.toString().equalsIgnoreCase("true")) {
    		getLog().info("Ignore tests");
    		return;
    	}
    	/* which test framework to use? setuptools by default */
    	if (framework.toString().equalsIgnoreCase("setuptools")) {
    		try {
    			runSetupToolsTest();
    		} catch ( IOException e1) { 
    			getLog().error("Can not run tests using setuptools! IOException");
    		} catch ( InterruptedException e2) {
    			getLog().error("Can not run tests using setuptools! InterruptedException");
    		}
    	}
    }

    private void prepareParameters() {
    	/* skip tests? */
        skipTest = testInstructions.get("skip_test");
        if (skipTest == null) {
            skipTest = "true";    // default value: skip tests
        }
        /* test framework */
        framework = testInstructions.get("framework");
        if (framework == null) {
        	framework = "setuptools";    // default value: setuptools framework       
        }
        /* test framework */
        folder = testInstructions.get("folder");
        if (folder == null) {
        	folder = "tests";    // default value: tests folder is by default called tests.       
        }
        /* verbose mode */
        Object tmp = testInstructions.get("verbose");
        if (tmp != null) {
        	verbose = tmp;
        }
        pkgTopLevel = packageInstructions.get("top_level");
        if (pkgTopLevel == null) {
        	pkgTopLevel = bundleName; 
        }
    }

    public void runSetupToolsTest() throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("python", "setup.py", "test");
    	pb.directory(new File(baseDir+"/target/"+pkgTopLevel+"_tests"));
    	if (verbose.toString().equalsIgnoreCase("true")) {
	    	pb.redirectErrorStream(true);
	    	pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    	} else {
			File log = new File(baseDir+"/target/"+pkgTopLevel+"_tests/test_log.txt");
			pb.redirectErrorStream(true);
			pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
    	}   	
    	getLog().info("Starting tests...");	
    	final Process process = pb.start();
    	process.waitFor();
		/*if (verbose.toString().equalsIgnoreCase("true")) {
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				getLog().info(line);
			}
		}*/
		if (process.exitValue() == 0) {
			getLog().info("Test terminated!");
		} else {
			getLog().info("Test terminated with errors!");
		}
    }

    public void setTest(Map instructions) { this.testInstructions = instructions; }
    public void setPackage(Map instructions) { this.packageInstructions = instructions; }
}
