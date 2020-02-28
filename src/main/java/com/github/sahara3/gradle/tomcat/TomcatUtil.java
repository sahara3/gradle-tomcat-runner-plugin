package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
        String versionString = null;

        double version = extension.getVersion();
        if (Double.compare(version, 7.0) == 0) {
            versionString = "7.0.+";
        } else if (Double.compare(version, 8.0) == 0) {
            versionString = "8.0.+";
        } else if (Double.compare(version, 8.5) == 0) {
            versionString = "8.5.+";
        } else if (Double.compare(version, 9.0) == 0) {
            versionString = "9.0.+";
        } else if (Double.compare(version, 10.0) == 0) {
            versionString = "10.0.+";
        } else {
            versionString = Double.toString(version) + ".+";
        }

        // create a set of module dependencies.
        String group = "org.apache.tomcat.embed";

        Set<Dependency> set = new HashSet<>();
        set.add(new DefaultExternalModuleDependency(group, "tomcat-embed-core", versionString));

        return set;
    }

}