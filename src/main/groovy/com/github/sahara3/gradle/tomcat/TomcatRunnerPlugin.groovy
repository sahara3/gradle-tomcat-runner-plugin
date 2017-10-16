package com.github.sahara3.gradle.tomcat

import java.io.File

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.JavaExec

class TomcatRunnerPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.configurations.create('tomcat')
        project.configurations.create('webapp')
        project.configurations.webapp.transitive = false

        def ext = project.extensions.create('tomcat', TomcatRunnerExtensions, project)

        project.tasks.create('clean', Delete) {
            group = 'build'
            delete project.buildDir
        }

        project.afterEvaluate {
            def base = ext.baseDir.get()
            def port = ext.port.get()

            project.tasks.create('tomcatRun', JavaExec) {
                group = 'application'

                main = TomcatRunner.canonicalName
                classpath project.configurations.tomcat
                classpath project.file("${project.rootDir}/buildSrc/build/classes/groovy/main")

                systemProperty 'tomcat.base', base
                systemProperty 'tomcat.port', port
                ext.systemProperties.each { name, value ->
                    systemProperty name, value
                }

                args = project.configurations.webapp.dependencies.collect {
                    it.dependencyProject.war.archivePath
                }

                doFirst {
                    logger.quiet "Tomcat run: port=${port}, base=${base}"
                    project.mkdir(base + File.separator + 'webapps')
                }

                dependsOn 'clean'
                project.configurations.webapp.dependencies.each {
                    dependsOn it.dependencyProject.war
                }
            }
        }
    }
}
