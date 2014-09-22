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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Helpers
{
    /**
    * Create a new folder if not exist.
    */
    public static void createFolder(String path) {
        File f = new File(path);
        if (!f.exists())
            f.mkdir();
    }

    /**
    * Copy a folder content recursively to another folder.
    */
    public static void copyFolder(File src, File dest)
        throws IOException{
 
        if(src.isDirectory()){
 
            //if directory not exists, create it
            if(!dest.exists()){
               dest.mkdir();
               //System.out.println("Directory " + dest + " created");
            }
 
            //list all the directory contents
            String files[] = src.list();
 
            for (String file : files) {
               //construct the src and dest file structure
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               //recursive copy
               copyFolder(srcFile,destFile);
            }
 
        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest); 
 
                byte[] buffer = new byte[1024];
 
                int length;
                //copy the file content in bytes 
                while ((length = in.read(buffer)) > 0){
                   out.write(buffer, 0, length);
                }
 
                in.close();
                out.close();
                //System.out.println("File copied from " + src + " to " + dest);
        }
    }
}
