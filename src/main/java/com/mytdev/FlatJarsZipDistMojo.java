package com.mytdev;

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
import static java.util.Arrays.asList;
import java.util.ResourceBundle;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Goal which execute assembly:assembly.
 *
 * @requiresDependencyResolution runtime
 */
@Mojo(name = "flatjarszip", defaultPhase = LifecyclePhase.PACKAGE)
public class FlatJarsZipDistMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException {
        Plugin assembly = plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-assembly-plugin"),
                version("2.6"),
                asList(assemblyRefDependency())
        );
        System.out.println(assembly.getDependencies() + " dependencies");
        for (Dependency dependency : assembly.getDependencies()) {
            System.out.println(dependency);
        }
        executeMojo(
                assembly,
                goal("assembly"),
                configuration(
                        element(name("descriptorRefs"),
                                element(name("descriptorRef"), "flat-jars-zip-assembly")
                        ),
                        element(name("appendAssemblyId"), "false")
                ),
                executionEnvironment(mavenProject, mavenSession, pluginManager)
        );
    }

    private Dependency assemblyRefDependency() {
        ResourceBundle res = ResourceBundle.getBundle(getClass().getName());
        System.out.printf("assembly-ref dependency: %s %% %s %% %s%n",
                res.getString("assembly-ref.groupId"),
                res.getString("assembly-ref.artifactId"),
                res.getString("assembly-ref.version"));
        return dependency(
                res.getString("assembly-ref.groupId"),
                res.getString("assembly-ref.artifactId"),
                res.getString("assembly-ref.version")
        );
    }
}
