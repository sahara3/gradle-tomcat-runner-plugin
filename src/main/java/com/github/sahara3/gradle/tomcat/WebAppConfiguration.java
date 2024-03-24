package com.github.sahara3.gradle.tomcat;

import java.io.File;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.War;

public class WebAppConfiguration {

    private final File warFile;

    @Getter(AccessLevel.PROTECTED)
    private final Project warProject;

    public File getWarFile() {
        if (this.warFile != null) {
            return this.warFile;
        }

        assert warProject != null;
        War warTask = (War) warProject.getTasks().getByName(WarPlugin.WAR_TASK_NAME);
        return warTask.getArchiveFile().get().getAsFile();
    }

    protected WebAppConfiguration(File warFile) {
        this.warFile = warFile;
        this.warProject = null;
    }

    protected WebAppConfiguration(Project warProject) {
        this.warFile = null;
        this.warProject = warProject;
    }

    @Setter
    private String contextPath = null;

    public String getContextPath() {
        String name = this.getDocBaseName();
        return "ROOT".equals(name) ? "" : "/" + name;
    }

    public String getDocBaseName() {
        String name = this.contextPath;
        if (name == null) {
            name = this.getWarFile().getName().replaceAll("\\.war$", "");
        }
        name = name.trim();

        if (name.startsWith("/")) {
            name = name.replaceFirst("^/+", "");
        }
        return name.isEmpty() ? "ROOT" : name;
    }
}
