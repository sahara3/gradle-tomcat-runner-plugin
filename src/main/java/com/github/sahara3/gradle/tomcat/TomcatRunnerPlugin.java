package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;

public class TomcatRunnerPlugin implements Plugin<Project> {

    public static final String TOMCAT_CONFIGURATION_NAME = "tomcat";

    public static final String TOMCAT_EXTENSION_NAME = "tomcat";

    public static final String TOMCAT_RUN_TASK_NAME = "tomcatRun";
    public static final String UNZIP_WEBAPPS_TASK_NAME = "unzipWebapps";
    public static final String TOMCAT_CLEAN_TASK_NAME = "tomcatClean";

    @Override
    public void apply(Project project) {
        Configuration config = project.getConfigurations().create(TOMCAT_CONFIGURATION_NAME);

        project.getExtensions().create(TOMCAT_EXTENSION_NAME, TomcatRunnerExtension.class);

        this.configureTasks(project);
    }

    private void configureTasks(Project project) {
        // create run task.
        TomcatRunTask runTask = project.getTasks().create(TOMCAT_RUN_TASK_NAME, TomcatRunTask.class);
        runTask.setGroup("application");
        runTask.setDescription("Runs web applications on Tomcat.");

        // create unzip webapps task. (parent of each unzip task)
        Task unzipWebappsTask = project.getTasks().create(UNZIP_WEBAPPS_TASK_NAME);
        unzipWebappsTask.setGroup("application");
        unzipWebappsTask.setDescription("Unzips all web application archives into webapps.");

        // create clean task.
        Delete cleanTask = project.getTasks().create(TOMCAT_CLEAN_TASK_NAME, Delete.class);
        cleanTask.setGroup("application");
        cleanTask.setDescription("Cleans the Tomcat base directory.");

        // register after evaluate hook.
        project.afterEvaluate(new TomcatTasksConfigureAction(runTask, unzipWebappsTask, cleanTask));
    }

    @RequiredArgsConstructor
    static class TomcatTasksConfigureAction implements Action<Project> {

        private final TomcatRunTask runTask;

        private final Task unzipWebappsTask;

        private final Delete cleanTask;

        @Override
        public void execute(Project project) {
            TomcatRunnerExtension ext = project.getExtensions().getByType(TomcatRunnerExtension.class);

            // set dependency to tomcat-embed-core.
            Set<Dependency> tomcatModules = TomcatUtil.getDefaultTomcatEmbedDependencies(ext);
            Configuration config = project.getConfigurations().getByName(TOMCAT_CONFIGURATION_NAME);
            config.getDependencies().addAll(tomcatModules);

            // set base directory.
            File baseDirectory = ext.getBaseDirectory();
            if (baseDirectory == null) {
                baseDirectory = new File(project.getBuildDir(), "tomcat");
            }
            this.runTask.setBaseDirectory(baseDirectory);
            this.runTask.setAppBaseDirectory(new File(baseDirectory, "webapps"));
            project.getLogger().info("baseDir: {}", this.runTask.getBaseDirectory());
            project.getLogger().info("appBase: {}", this.runTask.getAppBaseDirectory());

            this.cleanTask.delete(baseDirectory);

            // create and configure unzip war file task.
            for (WebAppConfiguration webapp : ext.getWebapps()) {
                this.createWebappUnzipTask(project, webapp);
            }
        }

        private void createWebappUnzipTask(Project project, WebAppConfiguration webapp) {
            String name = webapp.getDocBaseName();

            Copy task = project.getTasks().create("unzipWebapp" + StringUtils.capitalize(name), Copy.class);
            task.setGroup("application");
            task.setDescription("Unzips web application archive [" + name + "] into webapps.");
            task.from(project.zipTree(webapp.getWarFile()));
            task.into(new File(this.runTask.getAppBaseDirectory(), name));
            project.getLogger().info("unzip from: {}", task.getSource());
            project.getLogger().info("unzip into: {}", task.getDestinationDir());

            // set task dependencies.
            this.runTask.dependsOn(task);
            task.dependsOn(this.unzipWebappsTask);

            Project warProject = webapp.getWarProject();
            if (warProject != null) {
                task.dependsOn(warProject.getTasks().getByName(WarPlugin.WAR_TASK_NAME));
            }
        }
    }
}
