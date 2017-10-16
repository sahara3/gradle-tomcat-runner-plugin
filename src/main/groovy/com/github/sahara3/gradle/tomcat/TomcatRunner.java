// -*- mode: groovy -*-
package com.github.sahara3.gradle.tomcat;

import java.io.File;

import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;

public class TomcatRunner {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("tomcat.port"));
        String base = System.getProperty("tomcat.base");

        // https://stackoverflow.com/questions/17809914/deploy-war-in-embedded-tomcat-7
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(base);
        tomcat.getServer().addLifecycleListener(new VersionLoggerListener());

        // automatic war detection.
        //tomcat.getHost().addLifecycleListener(new HostConfig());
        for (String war : args) {
            File warfile = new File(war);
            String name = warfile.getName().replaceAll("\\.war$", "");
            tomcat.addWebapp("/" + name, warfile.getAbsolutePath());
        }

        tomcat.start();
        tomcat.getServer().await();
    }
}
