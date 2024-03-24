package com.github.sahara3.gradle.tomcat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;

class TomcatUtil {

    public static Set<Dependency> getDefaultTomcatEmbedDependencies(TomcatRunnerExtension extension) {
        String version = extension.getVersion();

        // create a set of module dependencies for tomcat-embed.
        Set<Dependency> modules = Stream.of(
                        "tomcat-embed-core", "tomcat-embed-el", "tomcat-embed-jasper", "tomcat-embed-websocket")
                .map(name -> createTomcatModuleDependency("org.apache.tomcat.embed", name, version))
                .collect(Collectors.toSet());

        modules.add(createTomcatModuleDependency("org.apache.tomcat", "tomcat-annotations-api", version));

        // add JSTL implementation libraries.
        Set<Dependency> jstlModules = Stream.of(
                        "taglibs-standard-impl", "taglibs-standard-spec", "taglibs-standard-jstlel")
                .map(name -> new DefaultExternalModuleDependency("org.apache.taglibs", name, "1.2.5")
                        .setTransitive(false))
                .collect(Collectors.toSet());

        modules.addAll(jstlModules);

        return modules;
    }

    private static ModuleDependency createTomcatModuleDependency(String group, String name, String version) {
        DefaultExternalModuleDependency dependency = new DefaultExternalModuleDependency(group, name, version);
        dependency.version(constraint -> constraint.require(version));
        dependency.setTransitive(true);
        return dependency;
    }

    public static String getJarsToSkipPropertyName(@SuppressWarnings("unused") String version) {
        return "tomcat.util.scan.StandardJarScanFilter.jarsToSkip";
    }
}
