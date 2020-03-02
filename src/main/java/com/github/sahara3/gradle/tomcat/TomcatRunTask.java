package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Paths;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

public class TomcatRunTask extends JavaExec {

    public TomcatRunTask() {
        // set default values.
        this.setMain(TomcatLauncher.class.getCanonicalName());
        this.setStandardInput(System.in);
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
        this.classpath(getLauncherClasspath());

        // prepare webapps directory.
        File base = TomcatUtil.determineBaseDirectory(project, ext);
        project.mkdir(new File(base, "webapps"));

        // setup system properties.
        ext.getSystemProperties().forEach((name, value) -> {
            this.systemProperty(name, value);
        });

        // setup arguments.
        int port = ext.getPort();
        this.args(port, base.getAbsolutePath());

        ext.getWebapps().forEach(webapp -> {
            File warFile = webapp.getWarFile();
            String contextPath = webapp.getContextPath();
            if (contextPath == null) {
                String name = warFile.getName().replaceAll("\\.war$", "");
                contextPath = name.equals("ROOT") ? "" : "/" + name;
            }

            this.args(warFile.getAbsolutePath(), contextPath);
        });

        // run.
        this.getLogger().quiet("Tomcat run: base={}, port={}", base, port);
        this.getLogger().info("Tomcat classpath: {}", this.getClasspath().getFiles());
        super.exec();
    }

    static File getLauncherClasspath() {
        // get jar or directory URL.
        Class<?> clazz = TomcatRunTask.class;
        URL url = clazz.getResource(clazz.getSimpleName() + ".class");
        long depth = clazz.getName().chars().filter(c -> c == '.').count();

        // when this class file is in jar:
        if (url.getProtocol().equals("jar")) {
            try {
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                return Paths.get(conn.getJarFileURL().toURI()).toFile();
            } catch (Exception e) {
                return null;
            }
        }

        // when this class file is in directory:
        if (url.getProtocol().equals("file")) {
            File file = new File(url.getFile()).getParentFile();
            for (int i = 0; i < depth; i++) {
                file = file.getParentFile();
            }
            return file;
        }

        // unreachable.
        throw new RuntimeException("Could not determine Tomcat launcher classpath.");
    }
}