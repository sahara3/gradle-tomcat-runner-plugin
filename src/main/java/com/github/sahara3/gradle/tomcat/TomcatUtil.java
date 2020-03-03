package com.github.sahara3.gradle.tomcat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;

class TomcatUtil {

    public static Set<Dependency> getDefaultTomcatEmbedDependencies(Project project, TomcatRunnerExtension extension) {
        // determine version.
        String version = determineTomcatVersionString(extension.getVersion());

        // create a set of module dependencies for tomcat-embed.
        List<String> embedModules = new ArrayList<>(
                Arrays.asList("tomcat-embed-core", "tomcat-embed-el", "tomcat-embed-jasper", "tomcat-embed-websocket"));
        if (version.startsWith("7.0")) {
            embedModules.add("tomcat-embed-logging-juli");
        }
        Set<Dependency> set = embedModules.stream()
                .map(artifact -> new DefaultExternalModuleDependency("org.apache.tomcat.embed", artifact, version))
                .collect(Collectors.toSet());

        // add JSTL implementation libraries.
        List<String> jstlModules = new ArrayList<>(
                Arrays.asList("taglibs-standard-impl", "taglibs-standard-spec", "taglibs-standard-jstlel"));
        jstlModules.stream()
                .map(artifact -> new DefaultExternalModuleDependency("org.apache.taglibs", artifact, "1.2.5"))
                .forEach(dependency -> {
                    dependency.setTransitive(false);
                    set.add(dependency);
                });

        // add logging libraries.
        set.add(new DefaultExternalModuleDependency("ch.qos.logback", "logback-classic", "1.2.3"));
        set.add(new DefaultExternalModuleDependency("org.slf4j", "slf4j-api", "1.7.30"));
        set.add(new DefaultExternalModuleDependency("org.slf4j", "jul-to-slf4j", "1.7.30"));

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

    public static String getJarsToSkipPropertyName(double version) {
        if (Double.compare(version, 7.0) == 0) {
            return "tomcat.util.scan.DefaultJarScanner.jarsToSkip";
        }
        return "tomcat.util.scan.StandardJarScanFilter.jarsToSkip";
    }
}
