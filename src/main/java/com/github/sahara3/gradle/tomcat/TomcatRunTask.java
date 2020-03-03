package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import lombok.Getter;
import lombok.Setter;

public class TomcatRunTask extends JavaExec {

    @Getter
    @Setter
    @InputDirectory
    private File baseDirectory;

    @Getter
    @Setter
    @InputDirectory
    private File appBaseDirectory;

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

        // setup system properties.
        ext.getSystemProperties().forEach((name, value) -> {
            this.systemProperty(name, value);
        });

        // cleanup unknown directories in webapps.
        Set<String> appNames = ext.getWebapps().stream().map(WebAppConfiguration::getDocBaseName)
                .collect(Collectors.toSet());
        try {
            Files.list(this.appBaseDirectory.toPath())
                    .filter(entry -> !appNames.contains(entry.getFileName().toString())).forEach(entry -> {
                        project.delete(entry.toFile());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // run.
        int port = ext.getPort();
        this.args(port, this.baseDirectory.getAbsolutePath(), this.appBaseDirectory.getAbsolutePath());
        this.getLogger().info("Tomcat run: port={}, base={}", port, this.baseDirectory);
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