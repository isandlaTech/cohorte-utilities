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
import org.apache.maven.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal deploy
 * @requiresProject true
 */
public class DeployMojo
    extends AbstractMojo
{
    
    /**
     * Cohorte's Bundle principal programming language.
     * @parameter expression="${cohorte.bundle.language}" default-value="python"
     * @required
     */
    private Object language;

    /**
     * Any Object to print out.
     * @parameter expression="${cohorte.echo.message}" default-value="Hello World..."
     */
    private Object message;

    /**
    * Any Object to print out.
    * @parameter
    *   expression="${cohorte.test.skip}"
    *   default-value="true"
    */
    private Boolean skipTest;

    public void execute()
        throws MojoExecutionException
    {
        getLog().info(language.toString());
        getLog().info( message.toString() );

    }
}
