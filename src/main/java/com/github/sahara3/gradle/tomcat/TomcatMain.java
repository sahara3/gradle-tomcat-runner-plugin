package com.github.sahara3.gradle.tomcat;

import java.io.File;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;

public class TomcatMain {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("tomcat.port"));
        String base = System.getProperty("tomcat.base");

        // https://stackoverflow.com/questions/17809914/deploy-war-in-embedded-tomcat-7
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(base);
        tomcat.getServer().addLifecycleListener(new VersionLoggerListener());

        // add webapps.
        // tomcat.getHost().addLifecycleListener(new HostConfig());
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("arguments must be a list of (war, contextPath).");
        }
        for (int n = 0; n < args.length / 2; n++) {
            String warPath = args[n];
            String contextPath = args[n + 1];
            tomcat.addWebapp(contextPath, warPath);
        }

        tomcat.start();
        tomcat.getServer().await();
    }
}
