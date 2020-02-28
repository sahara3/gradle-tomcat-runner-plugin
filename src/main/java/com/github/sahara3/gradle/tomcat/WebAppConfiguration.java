package com.github.sahara3.gradle.tomcat;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.War;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class WebAppConfiguration {

    private final File warFile;

    @Getter(AccessLevel.PROTECTED)
    private final Project warProject;

    protected File getWarFile() {
        if (this.warFile != null) {
            return this.warFile;
        }

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

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private String contextPath = null;

}