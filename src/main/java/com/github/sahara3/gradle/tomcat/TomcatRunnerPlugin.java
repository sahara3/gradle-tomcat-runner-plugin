package com.github.sahara3.gradle.tomcat;

import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.WarPlugin;

public class TomcatRunnerPlugin implements Plugin<Project> {

    public static final String TOMCAT_CONFIGURATION_NAME = "tomcat";

    public static final String TOMCAT_EXTENSION_NAME = "tomcat";

    public static final String TOMCAT_RUN_TASK_NAME = "tomcatRun";

    @Override
    public void apply(Project project) {
        project.getConfigurations().create(TOMCAT_CONFIGURATION_NAME);
        // project.getConfigurations().create("webapp");
        // project.getConfigurations().getByName("webapp").setTransitive(false);

        project.getExtensions().create(TOMCAT_EXTENSION_NAME, TomcatRunnerExtension.class);

        // create new task.
        Task task = project.getTasks().create(TOMCAT_RUN_TASK_NAME, TomcatRunTask.class);
        task.setGroup("application");
        task.setDescription("FIXME");

        // register after evaluate hook.
        project.afterEvaluate(new TomcatRunTaskConfigureAction());
    }

    static class TomcatRunTaskConfigureAction implements Action<Project> {
        @Override
        public void execute(Project project) {
            TomcatRunnerExtension ext = project.getExtensions().getByType(TomcatRunnerExtension.class);

            // set dependency to tomcat-embed-core.
            Set<Dependency> tomcatModules = TomcatUtil.getDefaultTomcatEmbedDependencies(project, ext);
            Configuration config = project.getConfigurations().getByName(TOMCAT_CONFIGURATION_NAME);
            config.defaultDependencies(dependencySet -> {
                tomcatModules.forEach(dependency -> {
                    dependencySet.add(dependency);
                });
            });

            // set task dependencies.
            Task runTask = project.getTasks().getByName(TOMCAT_RUN_TASK_NAME);
            ext.getWebapps().stream().filter(webapp -> webapp.getWarProject() != null).forEach(webapp -> {
                Task warTask = webapp.getWarProject().getTasks().getByName(WarPlugin.WAR_TASK_NAME);
                runTask.dependsOn(warTask);
            });
        }
    }
}
