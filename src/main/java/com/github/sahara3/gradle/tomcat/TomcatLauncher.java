package com.github.sahara3.gradle.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;

public class TomcatLauncher {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String base = args[1];

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(base);
        tomcat.getServer().addLifecycleListener(new VersionLoggerListener());

        // add webapps.
        // tomcat.getHost().addLifecycleListener(new HostConfig());
        if ((args.length - 2) % 2 != 0) {
            throw new IllegalArgumentException("arguments must be a list of (war, contextPath).");
        }
        for (int n = 1; n < args.length / 2; n++) {
            String warPath = args[n];
            String contextPath = args[n + 1];
            tomcat.addWebapp(contextPath, warPath);
        }

        tomcat.start();
        tomcat.getServer().await();
    }
}
