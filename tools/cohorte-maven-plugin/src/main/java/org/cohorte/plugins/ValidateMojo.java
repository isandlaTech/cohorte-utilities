package org.cohorte.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 *
 * @goal validate
 * @requiresProject true
 */
public class ValidateMojo
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
     * Cohorte's Bundle principal programming language.
     * @parameter expression="${cohorte.bundle.language}" default-value="python"
     * @required
     */
    private String language;

    /**
     * Package options
     * @parameter alias="package"
     */
    private Map packageInstructions = new LinkedHashMap();

    /**
     * Test options
     * @parameter alias="test"
     */
    private Map testInstructions = new LinkedHashMap();


    private Object pkgTopLevel;
    private Object skipTest;

    private void prepareParameters() {
        pkgTopLevel = packageInstructions.get("top_level");
        if (pkgTopLevel == null) {
            pkgTopLevel = bundleName;
        }

        skipTest = testInstructions.get("skip_test");
        if (skipTest == null) {
            skipTest = "true";    // default value: skip tests
        }


    }

    public void execute()
        throws MojoExecutionException
    {
        prepareParameters();
        if (language.equalsIgnoreCase("python")) {
            getLog().info("Using python programming language: updating pom source folder...");
            project.getBuild().setSourceDirectory(baseDir + "/src/main/python");
            project.getBuild().setOutputDirectory(baseDir + "/target/" + pkgTopLevel);
            if (skipTest.toString().equalsIgnoreCase("false")) {
                project.getProperties().setProperty("maven.test.skip", "false");
                project.getBuild().setTestSourceDirectory(baseDir + "/src/test/python");
                project.getBuild().setTestOutputDirectory(baseDir + "/target/" + pkgTopLevel + "_tests");
            } else {

            }
        }
    }
}
