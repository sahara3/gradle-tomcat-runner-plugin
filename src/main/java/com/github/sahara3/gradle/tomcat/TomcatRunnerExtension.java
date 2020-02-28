package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class TomcatRunnerExtension {

    @Getter(AccessLevel.PROTECTED)
    @Setter
    private double version = 9.0;

    @Getter(AccessLevel.PROTECTED)
    private File baseDirectory;

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = new File(baseDirectory);
    }

    @Deprecated
    public void setBaseDir(String baseDir) {
        this.setBaseDirectory(baseDir);
    }

    @Getter(AccessLevel.PROTECTED)
    @Setter
    private int port = 8080;

    @Getter(AccessLevel.PROTECTED)
    private final List<WebAppConfiguration> webapps = new ArrayList<>();

    public void webapp(File warFile, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warFile);
        action.execute(conf);
        this.webapps.add(conf);
    }

    public void webapp(String warFilePath, Action<WebAppConfiguration> action) {
        this.webapp(new File(warFilePath), action);
    }

    public void webapp(Project warProject, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warProject);
        action.execute(conf);
        this.webapps.add(conf);
    }

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Object> systemProperties = new HashMap<>();

    public void systemProperty(String name, Object value) {
        this.systemProperties.put(name, value);
    }

}
