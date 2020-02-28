package com.github.sahara3.gradle.tomcat;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

public class TomcatRunTask extends JavaExec {

    public TomcatRunTask() {
        // set default values.
        this.setMain(TomcatMain.class.getCanonicalName());
    }

    @TaskAction
    public void exec() {
        Project project = this.getProject();
        TomcatRunnerExtension ext = project.getExtensions().getByType(TomcatRunnerExtension.class);

        // setup classpathes.
        Configuration tomcat = project.getConfigurations().getByName(TomcatRunnerPlugin.TOMCAT_CONFIGURATION_NAME);
        tomcat.getResolvedConfiguration().getResolvedArtifacts().forEach(artifact -> {
            this.classpath(artifact.getFile());
        });

        // setup system properties.
        File base = TomcatUtil.determineBaseDirectory(project, ext);
        int port = ext.getPort();

        this.systemProperty("tomcat.base", base.getAbsolutePath());
        this.systemProperty("tomcat.port", port);
        ext.getSystemProperties().forEach((name, value) -> {
            this.systemProperty(name, value);
        });

        // setup arguments.
        ext.getWebapps().forEach(webapp -> {
            File warFile = webapp.getWarFile();
            String contextPath = webapp.getContextPath();
            if (contextPath == null) {
                String name = warFile.getName().replaceAll("\\.war$", "");
                contextPath = name.equals("ROOT") ? "" : "/" + name;
            }

            this.args(warFile.getAbsolutePath(), contextPath);
        });

        // prepare webapps directory.
        project.mkdir(new File(base, "webapps"));

        // run.
        this.getLogger().quiet("Tomcat run: base={}, port={}", base, port);
        super.exec();
    }
}