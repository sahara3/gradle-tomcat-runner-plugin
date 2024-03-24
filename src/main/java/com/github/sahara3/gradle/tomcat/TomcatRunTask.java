package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

@Setter
@Getter
public class TomcatRunTask extends JavaExec {

    @InputDirectory
    private File baseDirectory;

    @InputDirectory
    private File appBaseDirectory;

    public TomcatRunTask() {
        // set default values.
        this.getMainClass().set(TomcatLauncher.class.getCanonicalName());
        this.setStandardInput(System.in);
    }

    @TaskAction
    public void exec() {
        Project project = this.getProject();
        TomcatRunnerExtension ext = project.getExtensions().getByType(TomcatRunnerExtension.class);

        // setup classpath.
        Configuration tomcat = project.getConfigurations().getByName(TomcatRunnerPlugin.TOMCAT_CONFIGURATION_NAME);
        for (ResolvedArtifact artifact : tomcat.getResolvedConfiguration().getResolvedArtifacts()) {
            this.classpath(artifact.getFile());
        }
        this.classpath(getLauncherClasspath());

        // setup system properties.
        ext.getSystemProperties().forEach(this::systemProperty);

        String jarsToSkip = TomcatUtil.getJarsToSkipPropertyName(ext.getVersion());
        if (!this.getSystemProperties().containsKey(jarsToSkip)) {
            // add jarsToSkip property if not defined.
            this.systemProperty(jarsToSkip, String.join(",", ext.getJarsToSkip()));
        }

        // cleanup unknown directories in webapps.
        Set<String> appNames = ext.getWebapps().stream().map(WebAppConfiguration::getDocBaseName)
                .collect(Collectors.toSet());
        try (Stream<Path> paths = Files.list(this.appBaseDirectory.toPath())) {
            paths.filter(entry -> !appNames.contains(entry.getFileName().toString()))
                    .forEach(entry -> project.delete(entry.toFile()));
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
        if (url == null) {
            throw new RuntimeException("TomcatRunTask class file not found");
        }

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
