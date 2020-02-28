package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;

class TomcatUtil {
    public static File determineBaseDirectory(Project project, TomcatRunnerExtension extension) {
        File base = extension.getBaseDirectory();
        if (base != null) {
            return base;
        }
        return new File(project.getBuildDir(), "tomcat");
    }

    public static Set<Dependency> getDefaultTomcatEmbedDependencies(Project project, TomcatRunnerExtension extension) {
        // determine version.
        String version = determineTomcatVersionString(extension.getVersion());

        // create a set of module dependencies.
        Set<Dependency> set = Arrays
                .asList("tomcat-embed-core", "tomcat-embed-el", "tomcat-embed-jasper", "tomcat-embed-websocket")
                .stream()
                .map(artifact -> new DefaultExternalModuleDependency("org.apache.tomcat.embed", artifact, version))
                .collect(Collectors.toSet());
        return set;
    }

    private static String determineTomcatVersionString(double version) {
        if (Double.compare(version, 7.0) == 0) {
            return "7.0.+";
        }
        if (Double.compare(version, 8.0) == 0) {
            return "8.0.+";
        }
        if (Double.compare(version, 8.5) == 0) {
            return "8.5.+";
        }
        if (Double.compare(version, 9.0) == 0) {
            return "9.0.+";
        }
        if (Double.compare(version, 10.0) == 0) {
            return "10.0.+";
        }

        // it may be incorrect.
        return Double.toString(version) + ".+";
    }
}
