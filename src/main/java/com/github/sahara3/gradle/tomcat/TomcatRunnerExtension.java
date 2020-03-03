package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;

import lombok.Getter;
import lombok.Setter;

public class TomcatRunnerExtension {

    @Getter
    @Setter
    private double version = 9.0;

    @Getter
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

    @Getter
    @Setter
    private int port = 8080;

    @Getter
    private final List<WebAppConfiguration> webapps = new ArrayList<>();

    public void webapp(File warFile, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warFile);
        if (action != null) {
            action.execute(conf);
        }
        this.webapps.add(conf);
    }

    public void webapp(File warFile) {
        this.webapp(warFile, null);
    }

    public void webapp(String warFilePath, Action<WebAppConfiguration> action) {
        this.webapp(new File(warFilePath), action);
    }

    public void webapp(String warFilePath) {
        this.webapp(new File(warFilePath), null);
    }

    public void webapp(Project warProject, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warProject);
        if (action != null) {
            action.execute(conf);
        }
        this.webapps.add(conf);
    }

    public void webapp(Project warProject) {
        this.webapp(warProject, null);
    }

    @Getter
    private final Map<String, Object> systemProperties = new HashMap<>();

    public void systemProperty(String name, Object value) {
        this.systemProperties.put(name, value);
    }

}
