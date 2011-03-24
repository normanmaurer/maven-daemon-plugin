package me.normanmaurer.maven.daemon;
/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * {@link AbstractMojo} implementation which copy all dependencies to folder
 * 
 *
 * @author Norman Maurer
 * @goal copy-libs
 * @requiresDependencyResolution runtime
 * @phase package
 *
**/
public class CopyLibraryMojo extends AbstractMojo {

    // -----------------------------------------------------------------------

    /**
     * The directory that will be used to assemble the artifacts in and place the bin scripts.
     * 
     * @required
     * @parameter expression="${project.build.directory}/daemon"
     */
    private File assembleDirectory;

    
    private final static String LIB_PATH = "lib";

    
    /**
     * @readonly
     * @parameter expression="${project.artifacts}"
     */
    private Set<Artifact> artifacts;
    
    

    /*
     * (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
    
        Iterator<Artifact> artIt = artifacts.iterator();

        File libFolder = new File(assembleDirectory, LIB_PATH);
        
        // create the folder structure
        libFolder.mkdirs();
        
        // loop over the artifacts and copy them over to the lib folder
        while(artIt.hasNext()) {
            
            Artifact art = artIt.next();
            File artFile = art.getFile();
            
            if (artFile != null) {
                getLog().info("Copy dependency " + createArtifactString(art) + " to folder " + libFolder);
                try {
                    FileUtils.copyFileToDirectory(artFile, libFolder);
                } catch (IOException e) {
                    throw new MojoExecutionException( "Failed to copy artifact file " + artFile, e );
                }
            } else {
                getLog().warn("Could not get file for artifact " + createArtifactString(art));
            }
        }
    }
    
    /**
     * Return a String representation of the given {@link Artifact}
     * 
     * @param art
     * @return artString
     */
    public static String createArtifactString(Artifact art) {
        return art.getGroupId() + "::" +art.getArtifactId() + "::" + art.getVersion();
    }

}
