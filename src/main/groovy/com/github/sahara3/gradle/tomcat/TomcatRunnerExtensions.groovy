package com.github.sahara3.gradle.tomcat

import java.io.File
import java.util.Map

import org.gradle.api.Project
import org.gradle.api.provider.PropertyState

class TomcatRunnerExtensions {
    final PropertyState<String> baseDir

    final PropertyState<Integer> port

    TomcatRunnerExtensions(Project project) {
        baseDir = project.property(String)
        baseDir.set(project.buildDir.absolutePath + File.separator + 'tomcat')

        port = project.property(Integer)
        port.set(8080)
    }

    Map<String, Object> sysprops = [:]

    void systemProperty(String name, Object value) {
        sysprops.put(name, value)
    }

    Map<String, Object> getSystemProperties() {
        sysprops
    }
}
